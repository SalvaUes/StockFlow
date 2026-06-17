package sv.edu.ues.vl23003.stockflow.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import sv.edu.ues.vl23003.stockflow.Adapter.ProductoAdapter;
import sv.edu.ues.vl23003.stockflow.R;
import sv.edu.ues.vl23003.stockflow.database.Producto;
import sv.edu.ues.vl23003.stockflow.database.ProductoRepository;
import sv.edu.ues.vl23003.stockflow.utils.PrefManager;

public class InicioFragment extends Fragment {

    private ProductoRepository repository;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);

        repository = new ProductoRepository(requireContext());

        PrefManager prefManager = new PrefManager(requireContext());
        TextView tvBienvenida = view.findViewById(R.id.tvBienvenida);

        String usuario = prefManager.getUsuario();
        if (!usuario.isEmpty()) {
            tvBienvenida.setText(getString(R.string.bienvenida_usuario, usuario));
        } else {
            tvBienvenida.setText(getString(R.string.bienvenida_default));
        }

        TextView tvTotalProductos = view.findViewById(R.id.tvTotalProductos);
        TextView tvValorInventario = view.findViewById(R.id.tvValorInventario);
        TextView tvAlertasStock = view.findViewById(R.id.tvAlertasStock);
        RecyclerView rvProductosBajoStock = view.findViewById(R.id.rvProductosBajoStock);

        int totalProductos = repository.getTotalProductos();
        double valorInventario = repository.getValorTotalInventario();
        int cantidadBajoStock = repository.getCantidadProductosBajoStock();
        List<Producto> listaBajoStock = repository.getProductosBajoStock();

        tvTotalProductos.setText(String.valueOf(totalProductos));
        tvValorInventario.setText(String.format("$%.2f", valorInventario));
        tvAlertasStock.setText(String.valueOf(cantidadBajoStock));

        if (!listaBajoStock.isEmpty()) {
            ProductoAdapter adapter = new ProductoAdapter(requireContext(), listaBajoStock);
            rvProductosBajoStock.setLayoutManager(new LinearLayoutManager(requireContext()));
            rvProductosBajoStock.setAdapter(adapter);
        }

        return view;
    }
}
