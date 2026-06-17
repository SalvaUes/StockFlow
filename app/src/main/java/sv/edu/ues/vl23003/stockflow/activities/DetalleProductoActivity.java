package sv.edu.ues.vl23003.stockflow.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import sv.edu.ues.vl23003.stockflow.R;
import sv.edu.ues.vl23003.stockflow.database.AppDatabase;
import sv.edu.ues.vl23003.stockflow.database.Producto;
import sv.edu.ues.vl23003.stockflow.database.ProductoDAO;
import sv.edu.ues.vl23003.stockflow.databinding.ActivityDetalleProductoBinding;

public class DetalleProductoActivity
        extends AppCompatActivity {

    private ActivityDetalleProductoBinding binding;

    private Producto producto;

    private ProductoDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding =
                ActivityDetalleProductoBinding.inflate(
                        getLayoutInflater()
                );

        setContentView(binding.getRoot());

        dao = AppDatabase.getInstance(this).productoDAO();

        int id =
                getIntent()
                        .getIntExtra("id",0);

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

        binding.btnStockMas.setOnClickListener(
                v -> mostrarDialogoAjuste(1));

        binding.btnStockMenos.setOnClickListener(
                v -> mostrarDialogoAjuste(-1));
    }

    @Override
    protected void onResume() {
        super.onResume();

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

    private void mostrarDialogoAjuste(int direccion) {

        String titulo = direccion > 0 ? "Entrada de stock" : "Salida de stock";
        String mensaje = direccion > 0
                ? "Cantidad a agregar al stock actual (" + producto.getStock() + "):"
                : "Cantidad a retirar del stock actual (" + producto.getStock() + "):";

        EditText input = new EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        input.setHint("0");

        new AlertDialog.Builder(this)
                .setTitle(titulo)
                .setMessage(mensaje)
                .setView(input)
                .setPositiveButton("Aceptar", (d, w) -> {
                    String texto = input.getText().toString().trim();
                    if (texto.isEmpty()) {
                        Toast.makeText(this, "Ingrese una cantidad", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int cantidad = Integer.parseInt(texto);
                    if (cantidad <= 0) {
                        Toast.makeText(this, "La cantidad debe ser mayor a 0", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ajustarStock(direccion * cantidad);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void ajustarStock(int cambio) {

        int nuevoStock = producto.getStock() + cambio;

        if (nuevoStock < 0) {
            Toast.makeText(this, "Stock no puede ser negativo", Toast.LENGTH_SHORT).show();
            return;
        }

        producto.setStock(nuevoStock);
        dao.actualizar(producto);

        String msg = cambio > 0
                ? "Entrada: +" + cambio + " unidades"
                : "Salida: " + cambio + " unidades";
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

        cargar();
    }
}
