package sv.edu.ues.vl23003.stockflow.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import sv.edu.ues.vl23003.stockflow.R;
import sv.edu.ues.vl23003.stockflow.database.ProductoDAO;
import sv.edu.ues.vl23003.stockflow.utils.PrefManager;

public class InicioFragment extends Fragment {

    private ProductoDAO productoDAO;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);

        productoDAO = new ProductoDAO(requireContext());
        PrefManager prefManager = new PrefManager(requireContext());
        TextView tvBienvenida = view.findViewById(R.id.tvBienvenida);
        TextView tvCountProductos = view.findViewById(R.id.tvCountProductos);

        String usuario = prefManager.getUsuario();
        if (!usuario.isEmpty()) {
            tvBienvenida.setText(getString(R.string.bienvenida_usuario, usuario));
        } else {
            tvBienvenida.setText(getString(R.string.bienvenida_default));
        }

        actualizarContador(tvCountProductos);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        View v = getView();
        if (v != null) {
            TextView tvCount = v.findViewById(R.id.tvCountProductos);
            actualizarContador(tvCount);
        }
    }

    private void actualizarContador(TextView tvCount) {
        int count = productoDAO.obtenerTodos().size();
        tvCount.setText(String.valueOf(count));
    }
}