package sv.edu.ues.vl23003.stockflow.activities; // paquete de actividades para el registro de usuarios

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import sv.edu.ues.vl23003.stockflow.R;
import sv.edu.ues.vl23003.stockflow.utils.PrefManager;

public class RegisterActivity extends AppCompatActivity { // clase que maneja el formulario de registro

    EditText edtUsuario, edtEmail, edtPassword, edtConfirmar; // campos de texto para capturar datos del usuario
    Button btnGuardar, btnRegresar; // botones para guardar datos o regresar
    PrefManager prefManager; // gestor para guardar usuario email y contrasena en preferencias

    @Override
    protected void onCreate(Bundle savedInstanceState) { // metodo que se ejecuta al crear la pantalla
        super.onCreate(savedInstanceState); // llamada obligatoria a la clase padre
        setContentView(R.layout.activity_register); // se asigna el layout de registro como contenido

        edtUsuario = findViewById(R.id.edtUsuario); // se vincula el campo del usuario con su vista
        edtEmail = findViewById(R.id.edtEmail); // se vincula el campo del correo con su vista
        edtPassword = findViewById(R.id.edtPassword); // se vincula el campo de contrasena con su vista
        edtConfirmar = findViewById(R.id.edtConfirmar); // se vincula el campo de confirmacion con su vista
        btnGuardar = findViewById(R.id.btnGuardar); // se vincula el boton para guardar el formulario
        btnRegresar = findViewById(R.id.btnRegresar); // se vincula el boton para volver a la pantalla anterior

        prefManager = new PrefManager(this); // se crea el gestor de preferencias con esta pantalla

        btnGuardar.setOnClickListener(v -> guardar()); // al presionar guardar se ejecuta la validacion del formulario
        btnRegresar.setOnClickListener(v -> finish()); // al presionar regresar se cierra esta actividad
    }

    private void guardar() { // metodo que valida y almacena los datos del usuario
        String user = edtUsuario.getText().toString().trim(); // se obtiene el usuario escrito y se eliminan espacios innecesarios
        String email = edtEmail.getText().toString().trim(); // se obtiene el correo escrito y se eliminan espacios innecesarios
        String pass = edtPassword.getText().toString().trim(); // se obtiene la contrasena escrita y se eliminan espacios innecesarios
        String confirm = edtConfirmar.getText().toString().trim(); // se obtiene la confirmacion de contrasena y se limpian espacios

        if (user.length() < 3) { // se valida que el usuario tenga una longitud minima
            toast("Usuario mínimo 3 caracteres"); // se informa que el usuario es demasiado corto
            return; // se detiene el proceso hasta corregir el dato
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { // se valida que el correo tenga un formato valido
            toast("Email inválido"); // se informa que el correo no cumple el formato esperado
            return; // se detiene el proceso hasta corregir el dato
        }

        if (pass.length() < 5 || !pass.matches("[a-zA-Z0-9]+")) { // se valida que la contrasena tenga largo y contenido permitido
            toast("Password mínimo 5 y alfanumérico"); // se informa que la contrasena no cumple las reglas
            return; // se detiene el proceso hasta corregir el dato
        }

        if (!pass.equals(confirm)) { // se valida que la contrasena coincida con la confirmacion
            toast("Passwords no coinciden"); // se informa que ambas claves no son iguales
            return; // se detiene el proceso hasta corregir el dato
        }

        prefManager.saveUser(user, pass); // se guardan los datos del usuario en preferencias locales
        prefManager.saveEmail(email); // se guarda tambien el correo electronico del usuario
        toast("Usuario registrado exitosamente"); // se informa que el registro fue completado con exito
        limpiar(); // se limpian los campos para dejar el formulario vacio
    }

    private void limpiar() { // metodo que limpia todos los campos del formulario
        edtUsuario.setText(""); // se borra el texto del campo usuario
        edtEmail.setText(""); // se borra el texto del campo correo
        edtPassword.setText(""); // se borra el texto del campo contraseña
        edtConfirmar.setText(""); // se borra el texto del campo confirmacion
    }

    private void toast(String msg) { // metodo auxiliar para mostrar mensajes en pantalla
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show(); // se crea y se muestra el mensaje corto al usuario
    }
}