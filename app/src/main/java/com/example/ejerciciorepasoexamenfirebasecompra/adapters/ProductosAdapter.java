package com.example.ejerciciorepasoexamenfirebasecompra.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ejerciciorepasoexamenfirebasecompra.R;
import com.example.ejerciciorepasoexamenfirebasecompra.modelos.Producto;
import com.google.firebase.database.DatabaseReference;

import java.text.NumberFormat;
import java.util.List;

public class ProductosAdapter extends RecyclerView.Adapter<ProductosAdapter.ProductoVH> {

    private List<Producto> objects;
    private int resource;
    private Context context;
    private DatabaseReference refDatabase;

    public ProductosAdapter(List<Producto> objects, int resource, Context context, DatabaseReference refDatabase) {
        this.objects = objects;
        this.resource = resource;
        this.context = context;
        this.refDatabase = refDatabase;
    }

    @NonNull
    @Override
    public ProductoVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View productoView = LayoutInflater.from(context).inflate(resource,null);
        RecyclerView.LayoutParams layoutParams =new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        productoView.setLayoutParams(layoutParams);
        return new ProductoVH(productoView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoVH holder, int position) {
        Producto producto = objects.get(position);

        holder.lblNombre.setText(producto.getNombre());
        holder.lblCantidad.setText(String.valueOf(producto.getCantidad()));
        holder.lblPrecio.setText(producto.getPrecioMoneda());

        holder.imgBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDelete(producto, holder.getAdapterPosition()).show();
            }
        });

        holder.lblCantidad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable e) {
                int cantidad;

                try{
                    cantidad = Integer.parseInt(e.toString());
                }
                catch (NumberFormatException ex) {
                    cantidad = 0;
                }
                producto.setCantidad(cantidad);
                producto.updateTotal();
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProducto(producto, holder.getAdapterPosition()).show();
            }
        });
    }

    private AlertDialog editProducto(Producto producto, int adapterPosition) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edita Producto de la lista");
        builder.setCancelable(false);

        View productoView = LayoutInflater.from(context).inflate(R.layout.producto_view_alert, null);
        EditText txtNombre = productoView.findViewById(R.id.txtNombreProductoAlert);
        EditText txtCantidad = productoView.findViewById(R.id.txtCantidadProductoAlert);
        EditText txtPrecio = productoView.findViewById(R.id.txtPrecioProductoAlert);
        TextView lblTotal = productoView.findViewById(R.id.lblTotalProductoAlert);
        builder.setView(productoView);

        txtNombre.setText(producto.getNombre());
        txtCantidad.setText(String.valueOf(producto.getCantidad()));
        txtPrecio.setText(String.valueOf(producto.getPrecio()));

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
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
        builder.setPositiveButton("EDITAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!txtNombre.getText().toString().isEmpty() && !txtCantidad.getText().toString().isEmpty() && !txtPrecio.getText().toString().isEmpty() ){
                    producto.setNombre(txtNombre.getText().toString());
                    producto.setCantidad(Integer.parseInt(txtCantidad.getText().toString()));
                    producto.setPrecio(Float.parseFloat(txtPrecio.getText().toString()));
                    producto.updateTotal();
                    //notifyItemChanged(adapterPosition);
                    refDatabase.setValue(objects);
                }
            }
        });

        return builder.create();
    }

    private AlertDialog confirmDelete(Producto producto, int adapterPosition) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirma Eliminación");
        builder.setCancelable(false);
        TextView mensaje = new TextView(context);
        mensaje.setText("¿Estas seguro que quieres eliminar el producto?");
        mensaje.setTextSize(24);
        mensaje.setTextColor(Color.RED);
        mensaje.setPadding(100,100, 100, 100);
        builder.setView(mensaje);
        builder.setNegativeButton("CANCELAR", null);
        builder.setPositiveButton("ELIMINAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                objects.remove(producto);
                //notifyItemRemoved(adapterPosition);
                refDatabase.setValue(objects);
            }
        });

        return builder.create();
    }


    @Override
    public int getItemCount() {
        return objects.size();
    }

    public class ProductoVH extends RecyclerView.ViewHolder {
        ImageView imgBorrar;

        TextView lblNombre;
        TextView lblCantidad;
        TextView lblPrecio;

        public ProductoVH(@NonNull View itemView) {
            super(itemView);
            imgBorrar = itemView.findViewById(R.id.imgBorrarCardView);
            lblNombre = itemView.findViewById(R.id.lblNombreCardView);
            lblCantidad = itemView.findViewById(R.id.lblCantidadCardView);
            lblPrecio = itemView.findViewById(R.id.lblPrecioCardView);
        }
    }
}
