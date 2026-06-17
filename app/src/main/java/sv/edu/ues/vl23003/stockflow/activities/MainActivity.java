package sv.edu.ues.vl23003.stockflow.activities; // paquete de actividades principal

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import sv.edu.ues.vl23003.stockflow.databinding.ActivityMainBinding;
import sv.edu.ues.vl23003.stockflow.utils.PrefManager;

public class MainActivity extends AppCompatActivity { // clase que representa la pantalla inicial de acceso

    private ActivityMainBinding binding; // enlace con las vistas definidas en el layout principal
    private PrefManager prefManager; // gestor para leer y guardar datos locales del usuario

    @Override
    protected void onCreate(Bundle savedInstanceState) { // metodo que se ejecuta al crear la pantalla
        super.onCreate(savedInstanceState); // llamada obligatoria a la actividad padre
        binding = ActivityMainBinding.inflate(getLayoutInflater()); // se infla el layout con view binding
        setContentView(binding.getRoot()); // se asigna la vista raiz como contenido de la pantalla

        prefManager = new PrefManager(this); // se crea el gestor de preferencias usando esta pantalla

        if (prefManager.isLoggedIn() && prefManager.hasRegisteredUser()) { // si ya existe sesion iniciada y usuario registrado se va al inicio
            startActivity(new Intent(MainActivity.this, HomeActivity.class)); // se abre la pantalla principal del sistema
            finish(); // se cierra esta pantalla para evitar volver atras
            return; // se detiene el resto del flujo porque ya no hace falta continuar
        }

        if (!prefManager.hasRegisteredUser()) { // usuario por defecto solo si no hay usuario registrado
            prefManager.saveUser("admin", "12345"); // se guarda una cuenta base para poder ingresar por primera vez
        }

        binding.btnLogin.setOnClickListener(v -> { // evento para iniciar sesion cuando se presiona el boton
            String user = binding.etUser.getText().toString(); // se obtiene el texto escrito en el campo de usuario
            String pass = binding.etPass.getText().toString(); // se obtiene el texto escrito en el campo de contrasena

            if (prefManager.validateCredentials(user, pass)) { // se valida si las credenciales coinciden con las guardadas
                prefManager.setLoggedIn(true); // se marca la sesion como activa
                Intent intent = new Intent(MainActivity.this, HomeActivity.class); // se prepara la apertura de la pantalla principal
                startActivity(intent); // se inicia la pantalla principal
                finish(); // se cierra la pantalla actual para no regresar al login
            } else {
                Toast.makeText(MainActivity.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show(); // se informa que los datos no son correctos
            }
        });

        binding.tvRegister.setOnClickListener(v -> { // evento para abrir la pantalla de registro
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class); // se crea la intencion para ir al registro
            startActivity(intent); // se abre la pantalla de registro
        });
        SharedPreferences prefs =
                getSharedPreferences(
                        "config",
                        MODE_PRIVATE
                );

        boolean modoOscuro =
                prefs.getBoolean(
                        "modo_oscuro",
                        false
                );

        if(modoOscuro){

            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES
            );

        }else{

            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO
            );
        }
    }
}