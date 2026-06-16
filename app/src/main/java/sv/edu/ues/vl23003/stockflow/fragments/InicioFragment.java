package sv.edu.ues.vl23003.stockflow.fragments; // paquete donde vive el fragmento de inicio

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import sv.edu.ues.vl23003.stockflow.R;
import sv.edu.ues.vl23003.stockflow.utils.PrefManager;

public class InicioFragment extends Fragment { // clase que muestra la pantalla de inicio dentro de la navegacion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { // metodo que construye la interfaz visible del fragmento
        View view = inflater.inflate(R.layout.fragment_inicio, container, false); // se infla el layout del inicio para crear la vista principal

        PrefManager prefManager = new PrefManager(requireContext()); // se crea el gestor de preferencias para leer datos guardados
        TextView tvBienvenida = view.findViewById(R.id.tvBienvenida); // se obtiene el texto de bienvenida del layout
        TextView tvCountProductos = view.findViewById(R.id.tvCountProductos); // se obtiene el texto que mostrara la cantidad de productos

        String usuario = prefManager.getUsuario(); // se lee el usuario guardado para personalizar la bienvenida
        if (!usuario.isEmpty()) { // se valida si existe un usuario registrado
            tvBienvenida.setText(getString(R.string.bienvenida_usuario, usuario)); // se muestra bienvenida personalizada con el nombre del usuario
        } else {
            tvBienvenida.setText(getString(R.string.bienvenida_default)); // se muestra bienvenida por defecto cuando no hay usuario
        }

        int count = prefManager.getProductCount(); // se obtiene el numero actual de productos guardados
        tvCountProductos.setText(String.valueOf(count)); // se muestra el contador inicial de productos en pantalla

        return view; // se devuelve la vista ya configurada al sistema
    }

    @Override
    public void onResume() { // metodo que refresca la informacion cuando el fragmento vuelve a mostrarse
        super.onResume(); // llamada obligatoria al metodo padre para conservar el ciclo de vida
        PrefManager prefManager = new PrefManager(requireContext()); // se crea nuevamente el gestor para leer el valor mas reciente
        View v = getView(); // se obtiene la vista actual del fragmento si existe
        if (v != null) { // se valida que la vista siga disponible antes de usarla
            TextView tvCount = v.findViewById(R.id.tvCountProductos); // se busca el texto donde se muestra el contador de productos
            tvCount.setText(String.valueOf(prefManager.getProductCount())); // se actualiza el contador con el valor actual guardado
        }
    }
}