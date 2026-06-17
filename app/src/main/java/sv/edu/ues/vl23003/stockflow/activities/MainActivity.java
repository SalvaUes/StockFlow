package sv.edu.ues.vl23003.stockflow.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import sv.edu.ues.vl23003.stockflow.databinding.ActivityMainBinding;
import sv.edu.ues.vl23003.stockflow.utils.PrefManager;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefManager = new PrefManager(this);
        aplicarTema();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (prefManager.isLoggedIn() && prefManager.hasRegisteredUser()) {
            navigateToHome();
            return;
        }

        configurarCampos();
        configurarBotones();
        actualizarTextoTema();
    }

    private void aplicarTema() {
        if (prefManager.isDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void actualizarTextoTema() {
        binding.tvToggleTheme.setText(prefManager.isDarkMode() ? "Modo claro" : "Modo oscuro");
    }

    private void configurarCampos() {
        TextWatcher limpiarError = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                binding.tilUser.setError(null);
                binding.tilPass.setError(null);
            }
        };
        binding.etUser.addTextChangedListener(limpiarError);
        binding.etPass.addTextChangedListener(limpiarError);
    }

    private void configurarBotones() {
        binding.btnLogin.setOnClickListener(v -> {
            String user = binding.etUser.getText().toString().trim();
            String pass = binding.etPass.getText().toString().trim();

            if (user.isEmpty()) {
                binding.tilUser.setError("Ingrese su usuario");
                binding.etUser.requestFocus();
                return;
            }

            if (pass.isEmpty()) {
                binding.tilPass.setError("Ingrese su contraseña");
                binding.etPass.requestFocus();
                return;
            }

            if (!prefManager.hasRegisteredUser()) {
                Toast.makeText(MainActivity.this, "No hay usuarios registrados. Regístrese primero.", Toast.LENGTH_LONG).show();
                return;
            }

            if (prefManager.validateCredentials(user, pass)) {
                prefManager.setLoggedIn(true);
                navigateToHome();
            } else {
                binding.tilPass.setError("Credenciales incorrectas");
                binding.etPass.setText("");
                binding.etPass.requestFocus();
            }
        });

        binding.tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        binding.tvToggleTheme.setOnClickListener(v -> {
            boolean dark = !prefManager.isDarkMode();
            prefManager.setDarkMode(dark);
            recreate();
        });
    }

    private void navigateToHome() {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
