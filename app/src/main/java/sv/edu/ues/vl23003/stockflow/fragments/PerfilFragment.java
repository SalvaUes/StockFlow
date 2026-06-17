package sv.edu.ues.vl23003.stockflow.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import sv.edu.ues.vl23003.stockflow.R;
import sv.edu.ues.vl23003.stockflow.activities.MainActivity;
import sv.edu.ues.vl23003.stockflow.utils.PrefManager;

public class PerfilFragment extends Fragment {

    private PrefManager prefManager;
    private TextView tvUsuario, tvEmail;
    private Button btnToggleTheme, btnLogout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        prefManager = new PrefManager(requireContext());

        tvUsuario = view.findViewById(R.id.tvPerfilUsuario);
        tvEmail = view.findViewById(R.id.tvPerfilEmail);
        btnToggleTheme = view.findViewById(R.id.btnToggleTheme);
        btnLogout = view.findViewById(R.id.btnLogout);

        cargarDatosUsuario();
        actualizarBotonTema();

        btnToggleTheme.setOnClickListener(v -> {
            boolean dark = !prefManager.isDarkMode();
            prefManager.setDarkMode(dark);
            if (dark) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            requireActivity().recreate();
        });

        btnLogout.setOnClickListener(v -> mostrarDialogoLogout());

        return view;
    }

    private void actualizarBotonTema() {
        if (prefManager.isDarkMode()) {
            btnToggleTheme.setText("Modo claro");
        } else {
            btnToggleTheme.setText("Modo oscuro");
        }
    }

    private void cargarDatosUsuario() {
        String usuario = prefManager.getUsuario();
        String email = prefManager.getEmail();

        tvUsuario.setText("Usuario: " + (usuario.isEmpty() ? "No disponible" : usuario));
        tvEmail.setText("Email: " + (email.isEmpty() ? "No disponible" : email));
    }

    private void mostrarDialogoLogout() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Cerrar sesion")
                .setMessage("Estás seguro que deseas cerrar sesion")
                .setPositiveButton("Si", (dialog, which) -> cerrarSesion())
                .setNegativeButton("No", null)
                .show();
    }

    private void cerrarSesion() {
        prefManager.logout();
        Toast.makeText(requireContext(), "Sesion cerrada", Toast.LENGTH_SHORT).show();

        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
        }
    }
}