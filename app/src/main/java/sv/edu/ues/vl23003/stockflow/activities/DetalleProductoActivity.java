package sv.edu.ues.vl23003.stockflow.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import sv.edu.ues.vl23003.stockflow.R;
import sv.edu.ues.vl23003.stockflow.database.Producto;
import sv.edu.ues.vl23003.stockflow.database.ProductoDAO;
import sv.edu.ues.vl23003.stockflow.databinding.ActivityDetalleProductoBinding;

public class DetalleProductoActivity
        extends AppCompatActivity {

    private ActivityDetalleProductoBinding binding;

    private Producto producto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding =
                ActivityDetalleProductoBinding.inflate(
                        getLayoutInflater()
                );

        setContentView(binding.getRoot());

        int id =
                getIntent()
                        .getIntExtra("id",0);

        ProductoDAO dao =
                new ProductoDAO(this);

        producto =
                dao.obtenerPorId(id);

        cargar();

        binding.btnEliminar
                .setOnClickListener(v -> eliminar());

        binding.btnVolver.setOnClickListener(v -> finish());

        binding.btnEditar
                .setOnClickListener(v -> {

                    Intent intent =
                            new Intent(
                                    this,
                                    EditarProductoActivity.class
                            );

                    intent.putExtra(
                            "id",
                            producto.getId()
                    );

                    startActivity(intent);
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        ProductoDAO dao =
                new ProductoDAO(this);

        producto =
                dao.obtenerPorId(
                        producto.getId()
                );

        if(producto != null){
            cargar();
        }
    }

    private void cargar() {

        binding.tvNombre.setText(
                producto.getNombre()
        );

        binding.tvSku.setText(
                producto.getSku()
        );

        binding.tvCategoria.setText(
                producto.getCategoria()
        );

        binding.tvPrecio.setText(
                "$" + producto.getPrecio()
        );

        binding.tvStock.setText(
                producto.getStock() + ""
        );

        binding.tvEstado.setText(
                producto.getEstado()
        );

        try {

            if (producto.getImagen() != null &&
                    !producto.getImagen().isEmpty()) {

                binding.imgProducto.setImageURI(
                        Uri.parse(
                                producto.getImagen()
                        )
                );
            } else {

                binding.imgProducto.setImageResource(
                        R.drawable.ic_inventory_24
                );
            }

        } catch (Exception e) {

            binding.imgProducto.setImageResource(
                    R.drawable.ic_inventory_24
            );
        }
    }

    private void eliminar(){

        new AlertDialog.Builder(this)
                .setTitle("Eliminar")
                .setMessage(
                        "¿Desea eliminar este producto?"
                )
                .setPositiveButton(
                        "Eliminar",
                        (d,w)->{

                            ProductoDAO dao =
                                    new ProductoDAO(this);

                            dao.eliminar(
                                    producto.getId()
                            );

                            finish();
                        }
                )
                .setNegativeButton(
                        "Cancelar",
                        null
                )
                .show();
    }
}