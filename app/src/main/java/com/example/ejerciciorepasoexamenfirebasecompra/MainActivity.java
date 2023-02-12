package com.example.ejerciciorepasoexamenfirebasecompra;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ejerciciorepasoexamenfirebasecompra.adapters.ProductosAdapter;
import com.example.ejerciciorepasoexamenfirebasecompra.databinding.ActivityMainBinding;
import com.example.ejerciciorepasoexamenfirebasecompra.modelos.Producto;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private ArrayList<Producto> productos;

    private ProductosAdapter adapter;
    private RecyclerView.LayoutManager lm;

    private FirebaseDatabase database;
    private DatabaseReference refUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        database = FirebaseDatabase.getInstance("https://lista-de-la-compra-fireb-98c60-default-rtdb.europe-west1.firebasedatabase.app/");
        refUser = database.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("lista_productos");

        inicializarComponentes();

        //esto se trae los datos cada vez que sufre un cambio el dato en la base de datos
        refUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productos.clear();
                if (snapshot.exists()){
                    GenericTypeIndicator<ArrayList<Producto>> gti = new GenericTypeIndicator<ArrayList<Producto>>() {};
                    ArrayList<Producto> temp = snapshot.getValue(gti);
                    productos.addAll(temp);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createProducto().show();
            }
        });
    }

    private AlertDialog createProducto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("CREAR PRODUCTO");
        builder.setCancelable(false);

        // Necesitamos un contenido
        View productoView = LayoutInflater.from(this).inflate(R.layout.producto_view_alert, null);
        EditText txtNombre = productoView.findViewById(R.id.txtNombreProductoAlert);
        EditText txtCantidad = productoView.findViewById(R.id.txtCantidadProductoAlert);
        EditText txtPrecio = productoView.findViewById(R.id.txtPrecioProductoAlert);
        TextView lblTotal = productoView.findViewById(R.id.lblTotalProductoAlert);
        builder.setView(productoView);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    int cantidad = Integer.parseInt(txtCantidad.getText().toString());
                    float precio = Float.parseFloat(txtPrecio.getText().toString());
                    // Formatear numeros en cadenas
                    NumberFormat numberFormat = NumberFormat.getCurrencyInstance();
                    lblTotal.setText(numberFormat.format(cantidad*precio));
                }
                catch (NumberFormatException ignored) {}
            }
        };


        txtCantidad.addTextChangedListener(textWatcher);
        txtPrecio.addTextChangedListener(textWatcher);

        builder.setNegativeButton("CANCELAR", null);
        builder.setPositiveButton("CREAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!txtNombre.getText().toString().isEmpty()
                        && !txtCantidad.getText().toString().isEmpty()
                        && !txtPrecio.getText().toString().isEmpty() )
                {
                    Producto producto = new Producto(
                            txtNombre.getText().toString(),
                            Integer.parseInt(txtCantidad.getText().toString()),
                            Float.parseFloat(txtPrecio.getText().toString())
                    );
                    productos.add(producto);
                    refUser.setValue(productos);
                }
            }
        });

        return builder.create();
    }

    private void inicializarComponentes() {
        int columnas;
        columnas = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? 1 : 2;
        productos = new ArrayList<>();
        adapter = new ProductosAdapter(productos, R.layout.producto_view_holder, this, refUser);
        lm = new GridLayoutManager(this, columnas);
        binding.contentMain.content.setAdapter(adapter);
        binding.contentMain.content.setLayoutManager(lm);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("PRODUCTOS", productos);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ArrayList<Producto> temp = (ArrayList<Producto>) savedInstanceState.getSerializable("PRODUCTOS");
        productos.addAll(temp);
        adapter.notifyItemRangeInserted(0, productos.size());
    }

    //Esta es la funcion que se encarga de mostar el menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
         getMenuInflater().inflate(R.menu.menu_login, menu);
         return true;
    }

    //esta es la que se encarga de decidir que hace cada elemnto del menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
         super.onOptionsItemSelected(item);
         if (item.getItemId() == R.id.logout){
             FirebaseAuth.getInstance().signOut();
             startActivity(new Intent(this, LoginActivity.class));
             finish();
         }
         return true;
    }
}