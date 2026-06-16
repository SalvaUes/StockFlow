package sv.edu.ues.vl23003.stockflow.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import sv.edu.ues.vl23003.stockflow.R;
import sv.edu.ues.vl23003.stockflow.database.Producto;
import sv.edu.ues.vl23003.stockflow.database.ProductoRepository;
import sv.edu.ues.vl23003.stockflow.databinding.FragmentAgregarProductoBinding;

public class AgregarProductoFragment extends Fragment {

    private FragmentAgregarProductoBinding binding;

    private String imagenSeleccionada = "";

    private ActivityResultLauncher<Intent> launcher;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentAgregarProductoBinding.inflate(
                inflater,
                container,
                false
        );

        configurarGaleria();

        binding.btnGuardar.setOnClickListener(v -> guardar());

        return binding.getRoot();
    }

    private void configurarGaleria() {

        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    if(result.getResultCode() == Activity.RESULT_OK){

                        Intent data = result.getData();

                        if(data != null){

                            Uri uri = data.getData();

                            requireContext()
                                    .getContentResolver()
                                    .takePersistableUriPermission(
                                            uri,
                                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    );

                            imagenSeleccionada = uri.toString();

                            binding.imgProducto.setImageURI(uri);
                        }
                    }
                });

        binding.btnImagen.setOnClickListener(v -> {

            Intent intent =
                    new Intent(Intent.ACTION_OPEN_DOCUMENT);

            intent.addCategory(Intent.CATEGORY_OPENABLE);

            intent.setType("image/*");

            intent.addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
            );

            launcher.launch(intent);
        });
    }

    private String obtenerCategoria() {

        int id =
                binding.chipGroupCategoria.getCheckedChipId();

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

    private void guardar() {

        try {

            Producto producto = new Producto();

            producto.setNombre(
                    binding.etNombre.getText().toString());

            producto.setSku(
                    binding.etSku.getText().toString());

            producto.setCategoria(
                    obtenerCategoria());

            producto.setPrecio(
                    Double.parseDouble(
                            binding.etPrecio.getText().toString()
                    ));

            producto.setStock(
                    Integer.parseInt(
                            binding.etStock.getText().toString()
                    ));

            producto.setStockMinimo(
                    Integer.parseInt(
                            binding.etMinimo.getText().toString()
                    ));

            producto.setImagen(imagenSeleccionada);

            ProductoRepository repository =
                    new ProductoRepository(requireContext());

            repository.insertar(producto);

            Toast.makeText(
                    requireContext(),
                    "Producto guardado",
                    Toast.LENGTH_SHORT
            ).show();

            limpiar();

        } catch (Exception e){

            Toast.makeText(
                    requireContext(),
                    "Complete todos los campos",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void limpiar() {

        binding.etNombre.setText("");
        binding.etSku.setText("");
        binding.etPrecio.setText("");
        binding.etStock.setText("");
        binding.etMinimo.setText("");

        binding.imgProducto.setImageDrawable(null);

        imagenSeleccionada = "";
    }
}