package sv.edu.ues.vl23003.stockflow.activities; // paquete de actividades principal de la pantalla interna

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import sv.edu.ues.vl23003.stockflow.R;
import sv.edu.ues.vl23003.stockflow.databinding.ActivityHomeBinding;
import sv.edu.ues.vl23003.stockflow.fragments.AgregarProductoFragment;
import sv.edu.ues.vl23003.stockflow.fragments.InicioFragment;
import sv.edu.ues.vl23003.stockflow.fragments.ListaProductosFragment;
import sv.edu.ues.vl23003.stockflow.fragments.PerfilFragment;

public class HomeActivity extends AppCompatActivity { // clase que maneja la pantalla principal despues del acceso

    private ActivityHomeBinding binding; // enlace con los elementos visuales de esta actividad

    @Override
    protected void onCreate(Bundle savedInstanceState) { // metodo que se ejecuta al crear la actividad
        super.onCreate(savedInstanceState); // llamada obligatoria a la clase padre
        binding = ActivityHomeBinding.inflate(getLayoutInflater()); // se infla el layout usando view binding
        setContentView(binding.getRoot()); // se asigna la vista raiz como contenido

        if (savedInstanceState == null) { // si la actividad se crea por primera vez se muestra el inicio
            loadFragment(new InicioFragment()); // se carga el fragmento de inicio como vista inicial
        }

        binding.bottomNavigation.setOnItemSelectedListener(item -> { // se configura la navegacion inferior para cambiar de fragmento
            Fragment selectedFragment = null; // variable que guardara el fragmento elegido
            int id = item.getItemId(); // se obtiene el identificador del elemento pulsado

            if (id == R.id.nav_home) {
                selectedFragment = new InicioFragment();
            }
            else if (id == R.id.nav_products) {
                selectedFragment = new ListaProductosFragment();
            }
            else if (id == R.id.nav_add) {
                selectedFragment = new AgregarProductoFragment();
            }
            else if (id == R.id.nav_profile) {
                selectedFragment = new PerfilFragment();
            }

            if (selectedFragment != null) { // si existe un fragmento valido se reemplaza el actual
                loadFragment(selectedFragment); // se carga el fragmento seleccionado en el contenedor
                return true; // se indica que el evento fue atendido
            }
            return false; // se indica que el evento no pudo resolverse
        });
    }

    private void loadFragment(Fragment fragment) { // metodo auxiliar para cambiar el fragmento visible
        getSupportFragmentManager() // se inicia una transaccion del administrador de fragmentos
                .beginTransaction() // se prepara el cambio de fragmento
                .replace(R.id.fragment_container, fragment) // se reemplaza el contenido del contenedor por el fragmento recibido
                .commit(); // se confirma el cambio en pantalla
    }
}