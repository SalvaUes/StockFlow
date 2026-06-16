package sv.edu.ues.vl23003.stockflow.utils; // paquete donde vive el gestor de preferencias

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager { // clase que gestiona el almacenamiento de datos locales en preferencias

    private static final String PREF_NAME = "LoginPrefs"; // nombre del archivo de preferencias
    private static final String KEY_USER = "user"; // clave para guardar el nombre de usuario
    private static final String KEY_PASS = "pass"; // clave para guardar la contrasena
    private static final String KEY_EMAIL = "email"; // clave para guardar el correo electronico
    private static final String KEY_LOGGED_IN = "logged_in"; // clave para guardar el estado de sesion
    private static final String KEY_PRODUCT_LIST = "product_list"; // clave para guardar la lista de productos

    private final SharedPreferences pref; // objeto que maneja las preferencias locales

    public PrefManager(Context context) { // constructor que inicializa las preferencias
        this.pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE); // se obtiene acceso al archivo de preferencias
    }

    public void saveUser(String user, String pass) { // metodo para guardar usuario y contrasena
        SharedPreferences.Editor editor = pref.edit(); // se crea un editor para modificar las preferencias
        editor.putString(KEY_USER, user); // se guarda el usuario
        editor.putString(KEY_PASS, pass); // se guarda la contrasena
        editor.apply(); // se aplican los cambios
    }

    public void saveEmail(String email) { // metodo para guardar el correo electronico
        SharedPreferences.Editor editor = pref.edit(); // se crea un editor para modificar las preferencias
        editor.putString(KEY_EMAIL, email); // se guarda el correo
        editor.apply(); // se aplican los cambios
    }

    public void setLoggedIn(boolean loggedIn) { // metodo para marcar si hay sesion iniciada
        SharedPreferences.Editor editor = pref.edit(); // se crea un editor para modificar las preferencias
        editor.putBoolean(KEY_LOGGED_IN, loggedIn); // se guarda el estado de la sesion
        editor.apply(); // se aplican los cambios
    }

    public String getUsuario() { // metodo para obtener el usuario guardado
        return pref.getString(KEY_USER, ""); // se devuelve el usuario o vacio si no existe
    }

    public String getEmail() { // metodo para obtener el correo guardado
        return pref.getString(KEY_EMAIL, ""); // se devuelve el correo o vacio si no existe
    }

    public boolean hasRegisteredUser() { // metodo para validar si existe un usuario registrado
        return pref.contains(KEY_USER); // se devuelve verdadero si existe la clave del usuario
    }

    public boolean isLoggedIn() { // metodo para saber si hay sesion activa
        return pref.getBoolean(KEY_LOGGED_IN, false); // se devuelve el estado de la sesion o falso por defecto
    }

    public void logout() { // metodo para cerrar la sesion
        SharedPreferences.Editor editor = pref.edit(); // se crea un editor para modificar las preferencias
        editor.putBoolean(KEY_LOGGED_IN, false); // se marca como no autenticado
        editor.apply(); // se aplican los cambios
    }

    public boolean validateCredentials(String user, String pass) { // metodo para validar credenciales de acceso
        String savedUser = pref.getString(KEY_USER, ""); // se obtiene el usuario guardado
        String savedPass = pref.getString(KEY_PASS, ""); // se obtiene la contrasena guardada
        return !user.isEmpty() && user.equals(savedUser) && pass.equals(savedPass); // se valida que coincidan
    }

    public java.util.List<String> getProductList() { // metodo para obtener la lista de productos
        String json = pref.getString(KEY_PRODUCT_LIST, "[]"); // se obtiene la lista como JSON o un array vacio
        java.util.List<String> list = new java.util.ArrayList<>(); // se crea una lista vacia
        try { // se intenta parsear el JSON
            org.json.JSONArray arr = new org.json.JSONArray(json); // se convierte el string a array JSON
            for (int i = 0; i < arr.length(); i++) { // se recorre cada elemento
                list.add(arr.optString(i)); // se agrega el elemento a la lista
            }
        } catch (org.json.JSONException e) { // si hay error al parsear
            // se ignora el error y se devuelve lista vacia
        }
        return list; // se devuelve la lista con los productos
    }

    public void saveProductList(java.util.List<String> list) { // metodo para guardar la lista de productos
        org.json.JSONArray arr = new org.json.JSONArray(); // se crea un array JSON vacio
        for (String s : list) arr.put(s); // se agrega cada producto al array
        SharedPreferences.Editor editor = pref.edit(); // se crea un editor para modificar
        editor.putString(KEY_PRODUCT_LIST, arr.toString()); // se guarda el array como string
        editor.apply(); // se aplican los cambios
    }

    public void addProduct(String product) { // metodo para agregar un producto a la lista
        java.util.List<String> list = getProductList(); // se obtiene la lista actual de productos
        list.add(product); // se agrega el nuevo producto
        saveProductList(list); // se guarda la lista actualizada
    }

    public void removeProduct(String product) { // metodo para eliminar un producto de la lista
        java.util.List<String> list = getProductList(); // se obtiene la lista actual de productos
        java.util.Iterator<String> it = list.iterator(); // se crea un iterador para recorrer la lista
        while (it.hasNext()) { // mientras haya elementos
            String p = it.next(); // se obtiene el siguiente elemento
            if (p.equals(product)) { // si coincide con el producto a eliminar
                it.remove(); // se elimina del iterador
                break; // se detiene el ciclo
            }
        }
        saveProductList(list); // se guarda la lista actualizada
    }

    public int getProductCount() { // metodo para obtener la cantidad de productos
        return getProductList().size(); // se devuelve el tamaño de la lista
    }
}