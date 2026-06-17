package sv.edu.ues.vl23003.stockflow.activities;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import sv.edu.ues.vl23003.stockflow.databinding.ActivityRegisterBinding;
import sv.edu.ues.vl23003.stockflow.utils.PrefManager;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefManager = new PrefManager(this);
        if (prefManager.isDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnGuardar.setOnClickListener(v -> guardar());
        binding.btnRegresar.setOnClickListener(v -> finish());
    }

    private void guardar() {
        String user = binding.edtUsuario.getText().toString().trim();
        String email = binding.edtEmail.getText().toString().trim();
        String pass = binding.edtPassword.getText().toString().trim();
        String confirm = binding.edtConfirmar.getText().toString().trim();

        binding.tilUsuario.setError(null);
        binding.tilEmail.setError(null);
        binding.tilPassword.setError(null);
        binding.tilConfirmar.setError(null);

        if (user.length() < 3) {
            binding.tilUsuario.setError("Mínimo 3 caracteres");
            binding.edtUsuario.requestFocus();
            return;
        }

        if (prefManager.hasRegisteredUser() && user.equals(prefManager.getUsuario())) {
            binding.tilUsuario.setError("Este usuario ya existe");
            binding.edtUsuario.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.setError("Email inválido");
            binding.edtEmail.requestFocus();
            return;
        }

        if (pass.length() < 5) {
            binding.tilPassword.setError("Mínimo 5 caracteres");
            binding.edtPassword.requestFocus();
            return;
        }

        if (!pass.matches(".*[A-Za-z].*") || !pass.matches(".*[0-9].*")) {
            binding.tilPassword.setError("Debe contener letras y números");
            binding.edtPassword.requestFocus();
            return;
        }

        if (!pass.equals(confirm)) {
            binding.tilConfirmar.setError("Las contraseñas no coinciden");
            binding.edtConfirmar.requestFocus();
            return;
        }

        prefManager.saveUser(user, pass);
        prefManager.saveEmail(email);
        Toast.makeText(this, "Registro exitoso. Ahora inicie sesión.", Toast.LENGTH_LONG).show();
        finish();
    }
}
