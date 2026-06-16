package sv.edu.ues.vl23003.stockflow.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import sv.edu.ues.vl23003.stockflow.R;
import sv.edu.ues.vl23003.stockflow.database.Producto;
import sv.edu.ues.vl23003.stockflow.database.ProductoDAO;
import sv.edu.ues.vl23003.stockflow.databinding.ActivityEditarProductoBinding;

public class EditarProductoActivity extends AppCompatActivity {

    private ActivityEditarProductoBinding binding;

    private ProductoDAO dao;
    private Producto producto;

    private String imagenSeleccionada = "";

    private ActivityResultLauncher<Intent> launcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding =
                ActivityEditarProductoBinding.inflate(
                        getLayoutInflater()
                );

        setContentView(binding.getRoot());

        dao = new ProductoDAO(this);

        configurarGaleria();

        int id =
                getIntent()
                        .getIntExtra("id", 0);

        producto =
                dao.obtenerPorId(id);

        if(producto != null){
            cargarDatos();
        }

        binding.btnGuardar.setOnClickListener(
                v -> actualizarProducto()
        );
    }

    private String obtenerCategoria() {

        int id =
                binding.chipGroupCategoria
                        .getCheckedChipId();

        if(id == R.id.chipElectronica)
            return "Electrónica";

        if(id == R.id.chipRopa)
            return "Ropa";

        if(id == R.id.chipHogar)
            return "Hogar";

        if(id == R.id.chipAlimentos)
            return "Alimentos";

        if(id == R.id.chipDeportes)
            return "Deportes";

        return "";
    }

    private void cargarDatos(){

        binding.etNombre.setText(
                producto.getNombre()
        );

        binding.etSku.setText(
                producto.getSku()
        );

        binding.etPrecio.setText(
                String.valueOf(producto.getPrecio())
        );

        binding.etStock.setText(
                String.valueOf(producto.getStock())
        );

        binding.etMinimo.setText(
                String.valueOf(producto.getStockMinimo())
        );

        imagenSeleccionada =
                producto.getImagen();

        try{

            if(imagenSeleccionada != null
                    && !imagenSeleccionada.isEmpty()){

                binding.imgProducto.setImageURI(
                        Uri.parse(imagenSeleccionada)
                );
            }

        }catch (Exception e) {

            binding.imgProducto.setImageResource(
                    android.R.drawable.ic_menu_gallery
            );
        }

        String categoria =
                producto.getCategoria();

        switch (categoria){

            case "Electrónica":
                binding.chipElectronica.setChecked(true);
                break;

            case "Ropa":
                binding.chipRopa.setChecked(true);
                break;

            case "Hogar":
                binding.chipHogar.setChecked(true);
                break;

            case "Alimentos":
                binding.chipAlimentos.setChecked(true);
                break;

            case "Deportes":
                binding.chipDeportes.setChecked(true);
                break;
        }
    }


    private void configurarGaleria(){

        launcher =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        result -> {

                            if(result.getResultCode()
                                    == Activity.RESULT_OK){

                                Intent data =
                                        result.getData();

                                if(data != null){

                                    Uri uri = data.getData();

                                    if(uri != null){

                                        getContentResolver()
                                                .takePersistableUriPermission(
                                                        uri,
                                                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                                                );

                                        imagenSeleccionada =
                                                uri.toString();

                                        binding.imgProducto
                                                .setImageURI(uri);
                                    }
                                }
                            }
                        });

        binding.btnImagen.setOnClickListener(v -> {

            Intent intent =
                    new Intent(Intent.ACTION_OPEN_DOCUMENT);

            intent.addCategory(
                    Intent.CATEGORY_OPENABLE
            );

            intent.setType("image/*");

            intent.addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
            );

            launcher.launch(intent);
        });
    }

    private void actualizarProducto(){

        try{

            producto.setNombre(
                    binding.etNombre
                            .getText()
                            .toString()
            );

            producto.setSku(
                    binding.etSku
                            .getText()
                            .toString()
            );

            producto.setCategoria(
                    obtenerCategoria()
            );

            producto.setPrecio(
                    Double.parseDouble(
                            binding.etPrecio
                                    .getText()
                                    .toString()
                    )
            );

            producto.setStock(
                    Integer.parseInt(
                            binding.etStock
                                    .getText()
                                    .toString()
                    )
            );

            producto.setStockMinimo(
                    Integer.parseInt(
                            binding.etMinimo
                                    .getText()
                                    .toString()
                    )
            );

            producto.setImagen(
                    imagenSeleccionada
            );

            dao.actualizar(producto);

            Toast.makeText(
                    this,
                    "Producto actualizado",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        }catch (Exception e){

            Toast.makeText(
                    this,
                    "Complete todos los campos",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}
