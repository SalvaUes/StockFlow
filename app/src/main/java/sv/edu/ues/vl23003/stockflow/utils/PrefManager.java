package sv.edu.ues.vl23003.stockflow.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {

    private static final String PREF_NAME = "LoginPrefs";
    private static final String KEY_USER = "user";
    private static final String KEY_PASS = "pass";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_LOGGED_IN = "logged_in";
    private static final String KEY_PRODUCT_LIST = "product_list";

    private final SharedPreferences pref;

    public PrefManager(Context context) {
        this.pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveUser(String user, String pass) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_USER, user);
        editor.putString(KEY_PASS, pass);
        editor.apply();
    }

    public void saveEmail(String email) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    public void setLoggedIn(boolean loggedIn) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(KEY_LOGGED_IN, loggedIn);
        editor.apply();
    }

    public String getUsuario() {
        return pref.getString(KEY_USER, "");
    }

    public String getEmail() {
        return pref.getString(KEY_EMAIL, "");
    }

    public boolean hasRegisteredUser() {
        return pref.contains(KEY_USER);
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_LOGGED_IN, false);
    }

    public void logout() {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(KEY_LOGGED_IN, false);
        editor.apply();
    }

    public boolean validateCredentials(String user, String pass) {
        String savedUser = pref.getString(KEY_USER, "");
        String savedPass = pref.getString(KEY_PASS, "");
        return !user.isEmpty() && user.equals(savedUser) && pass.equals(savedPass);
    }

    public java.util.List<String> getProductList() {
        String json = pref.getString(KEY_PRODUCT_LIST, "[]");
        java.util.List<String> list = new java.util.ArrayList<>();
        try { // se intenta parsear el JSON
            org.json.JSONArray arr = new org.json.JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                list.add(arr.optString(i));
            }
        } catch (org.json.JSONException e) {

        }
        return list;
    }

    public void saveProductList(java.util.List<String> list) {
        org.json.JSONArray arr = new org.json.JSONArray();
        for (String s : list) arr.put(s);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_PRODUCT_LIST, arr.toString());
        editor.apply();
    }

    public void addProduct(String product) {
        java.util.List<String> list = getProductList();
        list.add(product);
        saveProductList(list);
    }

    public void removeProduct(String product) {
        java.util.List<String> list = getProductList();
        java.util.Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            String p = it.next();
            if (p.equals(product)) {
                it.remove();
                break;
            }
        }
        saveProductList(list);
    }

    public int getProductCount() {
        return getProductList().size();
    }
}