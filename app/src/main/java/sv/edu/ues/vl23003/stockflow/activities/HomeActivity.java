package sv.edu.ues.vl23003.stockflow.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import sv.edu.ues.vl23003.stockflow.R;
import sv.edu.ues.vl23003.stockflow.databinding.ActivityHomeBinding;
import sv.edu.ues.vl23003.stockflow.fragments.AgregarProductoFragment;
import sv.edu.ues.vl23003.stockflow.fragments.InicioFragment;
import sv.edu.ues.vl23003.stockflow.fragments.ListaProductosFragment;
import sv.edu.ues.vl23003.stockflow.fragments.PerfilFragment;
import sv.edu.ues.vl23003.stockflow.utils.PrefManager;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        if (savedInstanceState == null) {
            loadFragment(new InicioFragment());
        }

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                selectedFragment = new InicioFragment();
                binding.toolbar.setTitle("StockFlow");
            } else if (id == R.id.nav_products) {
                selectedFragment = new ListaProductosFragment();
                binding.toolbar.setTitle("Productos");
            } else if (id == R.id.nav_add) {
                selectedFragment = new AgregarProductoFragment();
                binding.toolbar.setTitle("Agregar Producto");
            } else if (id == R.id.nav_profile) {
                selectedFragment = new PerfilFragment();
                binding.toolbar.setTitle("Perfil");
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });

        binding.toolbar.setOnMenuItemClickListener(item -> {
            return onOptionsItemSelected(item);
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_profile) {
            binding.bottomNavigation.setSelectedItemId(R.id.nav_profile);
            return true;
        } else if (id == R.id.action_settings) {
            Toast.makeText(this, "Ajustes", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_logout) {
            PrefManager prefManager = new PrefManager(this);
            prefManager.logout();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        android.R.anim.fade_in,
                        android.R.anim.fade_out,
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                )
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}