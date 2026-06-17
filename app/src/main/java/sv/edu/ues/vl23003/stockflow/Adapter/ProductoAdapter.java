package sv.edu.ues.vl23003.stockflow.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import sv.edu.ues.vl23003.stockflow.R;
import sv.edu.ues.vl23003.stockflow.activities.DetalleProductoActivity;
import sv.edu.ues.vl23003.stockflow.database.Producto;
import sv.edu.ues.vl23003.stockflow.databinding.ItemProductoBinding;

public class ProductoAdapter
        extends RecyclerView.Adapter<ProductoAdapter.ViewHolder> {

    private final Context context;
    private final List<Producto> lista;

    public ProductoAdapter(Context context,
                           List<Producto> lista) {

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

        switch (producto.getEstado()) {

            case "Disponible":

                holder.binding.tvEstado.setTextColor(
                        0xFF2E7D32
                );

                holder.binding.tvStock.setTextColor(
                        0xFF2E7D32
                );

                break;

            case "Bajo Stock":

                holder.binding.tvEstado.setTextColor(
                        0xFFF57C00
                );

                holder.binding.tvStock.setTextColor(
                        0xFFF57C00
                );

                break;

            case "Agotado":

                holder.binding.tvEstado.setTextColor(
                        0xFFC62828
                );

                holder.binding.tvStock.setTextColor(
                        0xFFC62828
                );

                break;
        }
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