package sv.edu.ues.vl23003.stockflow.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import sv.edu.ues.vl23003.stockflow.Adapter.ProductoAdapter;
import sv.edu.ues.vl23003.stockflow.database.AppDatabase;
import sv.edu.ues.vl23003.stockflow.database.Producto;
import sv.edu.ues.vl23003.stockflow.database.ProductoDAO;
import sv.edu.ues.vl23003.stockflow.databinding.FragmentListaProductosBinding;

public class ListaProductosFragment extends Fragment {

    private FragmentListaProductosBinding binding;

    private TextView tvResultados;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        binding =
                FragmentListaProductosBinding.inflate(
                        inflater,
                        container,
                        false
                );

        tvResultados =
                binding.tvResultados;

        binding.chipTodos.setOnClickListener(
                v -> filtrarCategoria("Todos"));

        binding.chipElectronica.setOnClickListener(
                v -> filtrarCategoria("Electrónica"));

        binding.chipRopa.setOnClickListener(
                v -> filtrarCategoria("Ropa"));

        binding.chipHogar.setOnClickListener(
                v -> filtrarCategoria("Hogar"));

        binding.chipAlimentos.setOnClickListener(
                v -> filtrarCategoria("Alimentos"));

        binding.chipDeportes.setOnClickListener(
                v -> filtrarCategoria("Deportes"));

        cargarProductos();
        binding.etBuscar.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count) {

                        buscarProductos(
                                s.toString()
                        );
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s) {
                    }
                });
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarProductos();
    }



    private void cargarProductos() {

        ProductoDAO dao =
                AppDatabase.getInstance(requireContext()).productoDAO();

        List<Producto> lista =
                dao.obtenerTodos();

        if(lista.isEmpty()){

            binding.tvSinProductos.setVisibility(
                    View.VISIBLE);

            binding.rvProductos.setVisibility(
                    View.GONE);

        }else{

            binding.tvSinProductos.setVisibility(
                    View.GONE);

            binding.rvProductos.setVisibility(
                    View.VISIBLE);
        }

        ProductoAdapter adapter =
                new ProductoAdapter(
                        requireContext(),
                        lista
                );

        binding.rvProductos.setLayoutManager(
                new LinearLayoutManager(
                        requireContext()
                )
        );
        actualizarVista(lista);

        binding.rvProductos.setAdapter(
                adapter
        );

        if (lista.size() == 1) {

            binding.tvResultados.setText(
                    "1 resultado"
            );

        } else {

            binding.tvResultados.setText(
                    lista.size() + " resultados"
            );
        }
    }
    private void buscarProductos(String texto){

        ProductoDAO dao =
                AppDatabase.getInstance(requireContext()).productoDAO();

        List<Producto> lista;

        if(texto.isEmpty()){

            lista = dao.obtenerTodos();

        }else{

            lista = dao.buscar(texto);
        }
        if(lista.isEmpty()){

            binding.tvSinProductos.setVisibility(
                    View.VISIBLE);

            binding.rvProductos.setVisibility(
                    View.GONE);

        }else{

            binding.tvSinProductos.setVisibility(
                    View.GONE);

            binding.rvProductos.setVisibility(
                    View.VISIBLE);
        }

        ProductoAdapter adapter =
                new ProductoAdapter(
                        requireContext(),
                        lista
                );

        binding.rvProductos.setAdapter(
                adapter
        );

        actualizarVista(lista);

        if(lista.size() == 1){

            binding.tvResultados.setText(
                    "1 resultado"
            );

        }else{

            binding.tvResultados.setText(
                    lista.size() + " resultados"
            );
        }
    }

    private void filtrarCategoria(String categoria){

        ProductoDAO dao =
                AppDatabase.getInstance(requireContext()).productoDAO();

        List<Producto> lista =
                dao.obtenerTodos();

        List<Producto> filtrada =
                new ArrayList<>();

        if(categoria.equals("Todos")){

            filtrada = lista;

        }else{

            for(Producto p : lista){

                if(p.getCategoria()
                        .equals(categoria)){

                    filtrada.add(p);
                }
            }
        }

        ProductoAdapter adapter =
                new ProductoAdapter(
                        requireContext(),
                        filtrada
                );

        binding.rvProductos.setAdapter(
                adapter
        );

        actualizarVista(filtrada);

        binding.tvResultados.setText(
                filtrada.size() + " resultados"
        );
    }

    private void actualizarVista(List<Producto> lista){

        if(lista.isEmpty()){

            binding.tvSinProductos.setText(
                    "No se encontraron productos"
            );

            binding.tvSinProductos.setVisibility(
                    View.VISIBLE
            );

            binding.rvProductos.setVisibility(
                    View.GONE
            );

        }else{

            binding.tvSinProductos.setVisibility(
                    View.GONE
            );

            binding.rvProductos.setVisibility(
                    View.VISIBLE
            );
        }

        binding.tvResultados.setText(
                lista.size() +
                        (lista.size() == 1
                                ? " producto"
                                : " productos")
        );
    }
}
