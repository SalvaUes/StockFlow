package sv.edu.ues.vl23003.stockflow.fragments; // paquete donde vive el fragmento de perfil

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import sv.edu.ues.vl23003.stockflow.R;
import sv.edu.ues.vl23003.stockflow.activities.MainActivity;
import sv.edu.ues.vl23003.stockflow.utils.PrefManager;

public class PerfilFragment extends Fragment { // clase que muestra el perfil del usuario
    
    private PrefManager prefManager;
    private TextView tvPerfilNombre, tvPerfilEmail, tvUsuario, tvCorreo;
    private Button btnLogout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        prefManager = new PrefManager(requireContext());

        tvPerfilNombre = view.findViewById(R.id.tvPerfilNombre);
        tvPerfilEmail = view.findViewById(R.id.tvPerfilEmail);
        tvUsuario = view.findViewById(R.id.tvPerfilUsuario);
        tvCorreo = view.findViewById(R.id.tvPerfilCorreo);
        btnLogout = view.findViewById(R.id.btnLogout);

        cargarDatosUsuario();

        btnLogout.setOnClickListener(v -> mostrarDialogoLogout());

        return view;
    }

    private void cargarDatosUsuario() {
        String usuario = prefManager.getUsuario();
        String email = prefManager.getEmail();

        tvPerfilNombre.setText(usuario.isEmpty() ? "Usuario" : usuario);
        tvPerfilEmail.setText(email.isEmpty() ? "email@ejemplo.com" : email);
        tvUsuario.setText("Usuario: " + (usuario.isEmpty() ? "No disponible" : usuario));
        tvCorreo.setText("Email: " + (email.isEmpty() ? "No disponible" : email));
    }

    private void mostrarDialogoLogout() { // metodo para mostrar dialogo de confirmacion de logout
        new AlertDialog.Builder(requireContext()) // se crea un constructor de dialogo
                .setTitle("Cerrar sesion") // se establece el titulo del dialogo
                .setMessage("Estás seguro que deseas cerrar sesion") // se establece el mensaje del dialogo
                .setPositiveButton("Si", (dialog, which) -> cerrarSesion()) // boton positivo para confirmar logout
                .setNegativeButton("No", null) // boton negativo para cancelar
                .show(); // se muestra el dialogo
    }

    private void cerrarSesion() {
        prefManager.logout(); // se marca como no autenticado en preferencias
        Toast.makeText(requireContext(), "Sesion cerrada", Toast.LENGTH_SHORT).show();

        if (getActivity() != null) {
            // Se crea una intención para ir a MainActivity
            Intent intent = new Intent(getActivity(), MainActivity.class);

            // FLAGS para limpiar el back stack y evitar volver atrás
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            // Se inicia MainActivity
            startActivity(intent);

            // Se cierra HomeActivity
            getActivity().finish();
        }
    }
}