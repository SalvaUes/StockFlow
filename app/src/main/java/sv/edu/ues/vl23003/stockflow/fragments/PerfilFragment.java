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

import androidx.appcompat.app.AppCompatDelegate;
import android.content.SharedPreferences;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class PerfilFragment extends Fragment { // clase que muestra el perfil del usuario
    
    private PrefManager prefManager; // gestor para leer datos del usuario guardados
    private TextView tvUsuario, tvEmail; // campos de texto para mostrar usuario y email
    private Button btnLogout; // boton para cerrar sesion

    private SwitchMaterial switchModoOscuro;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { // metodo que construye la vista del fragmento
        View view = inflater.inflate(R.layout.fragment_perfil, container, false); // se infla el layout del perfil

        prefManager = new PrefManager(requireContext()); // se crea el gestor de preferencias

        tvUsuario = view.findViewById(R.id.tvPerfilUsuario); // se obtiene el campo para mostrar usuario
        tvEmail = view.findViewById(R.id.tvPerfilEmail); // se obtiene el campo para mostrar email
        btnLogout = view.findViewById(R.id.btnLogout); // se obtiene el boton de logout

        cargarDatosUsuario(); // se carga la informacion del usuario en pantalla

        btnLogout.setOnClickListener(v -> mostrarDialogoLogout()); // al presionar logout se muestra un dialogo de confirmacion

        switchModoOscuro =
                view.findViewById(R.id.switchModoOscuro);

        SharedPreferences prefs =
                requireActivity().getSharedPreferences(
                        "config",
                        getContext().MODE_PRIVATE
                );

        boolean modoOscuro =
                prefs.getBoolean(
                        "modo_oscuro",
                        false
                );

        switchModoOscuro.setChecked(
                modoOscuro
        );

        switchModoOscuro.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    prefs.edit()
                            .putBoolean(
                                    "modo_oscuro",
                                    isChecked
                            )
                            .apply();

                    if(isChecked){

                        AppCompatDelegate.setDefaultNightMode(
                                AppCompatDelegate.MODE_NIGHT_YES
                        );

                    }else{

                        AppCompatDelegate.setDefaultNightMode(
                                AppCompatDelegate.MODE_NIGHT_NO
                        );
                    }
                }
        );

        return view; // se devuelve la vista ya configurada
    }

    private void cargarDatosUsuario() { // metodo para mostrar los datos del usuario
        String usuario = prefManager.getUsuario(); // se obtiene el usuario guardado
        String email = prefManager.getEmail(); // se obtiene el email guardado

        tvUsuario.setText("Usuario: " + (usuario.isEmpty() ? "No disponible" : usuario)); // se muestra el usuario o un mensaje si no existe
        tvEmail.setText("Email: " + (email.isEmpty() ? "No disponible" : email)); // se muestra el email o un mensaje si no existe
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