package sv.edu.ues.vl23003.stockflow.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import sv.edu.ues.vl23003.stockflow.R;
import sv.edu.ues.vl23003.stockflow.activities.DetalleProductoActivity;
import sv.edu.ues.vl23003.stockflow.database.Producto;
import sv.edu.ues.vl23003.stockflow.databinding.ItemProductoBinding;

public class ProductoAdapter
        extends RecyclerView.Adapter<ProductoAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Producto> lista;

    public ProductoAdapter(Context context,
                           ArrayList<Producto> lista) {

        this.context = context;
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        ItemProductoBinding binding =
                ItemProductoBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                );

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        Producto producto = lista.get(position);

        holder.binding.tvNombre.setText(producto.getNombre());

        holder.binding.tvSku.setText(
                producto.getSku()
                        + " - "
                        + producto.getCategoria()
        );

        holder.binding.tvPrecio.setText(
                "$" + producto.getPrecio()
        );

        holder.binding.tvEstado.setText(
                producto.getEstado()
        );

        holder.binding.tvStock.setText(
                String.valueOf(
                        producto.getStock()
                )
        );

        int colorEstado;
        switch (producto.getEstado()) {
            case "Disponible":
                colorEstado = ContextCompat.getColor(context, R.color.status_disponible);
                break;
            case "Bajo Stock":
                colorEstado = ContextCompat.getColor(context, R.color.status_bajo_stock);
                break;
            case "Agotado":
                colorEstado = ContextCompat.getColor(context, R.color.status_agotado);
                break;
            default:
                colorEstado = ContextCompat.getColor(context, R.color.elegant_on_surface);
                break;
        }
        holder.binding.tvEstado.setTextColor(colorEstado);
        holder.binding.tvStock.setTextColor(colorEstado);
        try {

            if(producto.getImagen() != null &&
                    !producto.getImagen().isEmpty()) {

                holder.binding.imgProducto.setImageURI(
                        Uri.parse(
                                producto.getImagen()
                        )
                );

            } else {

                holder.binding.imgProducto.setImageResource(
                        R.drawable.ic_inventory_24
                );
            }

        } catch (Exception e) {

            holder.binding.imgProducto.setImageResource(
                    R.drawable.ic_inventory_24
            );
        }

        holder.itemView.setOnClickListener(v -> {

            Intent intent =
                    new Intent(context,
                            DetalleProductoActivity.class);

            intent.putExtra("id", producto.getId());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        ItemProductoBinding binding;

        public ViewHolder(
                ItemProductoBinding binding) {

            super(binding.getRoot());

            this.binding = binding;
        }
    }

}