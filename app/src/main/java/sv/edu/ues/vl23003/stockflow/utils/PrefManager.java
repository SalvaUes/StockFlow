package sv.edu.ues.vl23003.stockflow.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PrefManager {

    private static final String PREF_NAME = "LoginPrefs";
    private static final String KEY_USER = "user";
    private static final String KEY_PASS = "pass";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_LOGGED_IN = "logged_in";
    private static final String KEY_DARK_MODE = "dark_mode";

    private final SharedPreferences pref;

    public PrefManager(Context context) {
        this.pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return password;
        }
    }

    public void saveUser(String user, String pass) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_USER, user);
        editor.putString(KEY_PASS, hashPassword(pass));
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
        return pref.contains(KEY_USER) && pref.contains(KEY_PASS);
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
        return !user.isEmpty() && user.equals(savedUser) && hashPassword(pass).equals(savedPass);
    }

    public boolean isDarkMode() {
        return pref.getBoolean(KEY_DARK_MODE, false);
    }

    public void setDarkMode(boolean darkMode) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(KEY_DARK_MODE, darkMode);
        editor.apply();
    }

    public void clearAll() {
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
    }
}
