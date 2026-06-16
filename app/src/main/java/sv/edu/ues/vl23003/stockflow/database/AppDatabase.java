package sv.edu.ues.vl23003.stockflow.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AppDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "inventario.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_PRODUCTOS = "productos";

    public AppDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE productos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT," +
                "sku TEXT," +
                "categoria TEXT," +
                "precio REAL," +
                "stock INTEGER," +
                "stockMinimo INTEGER," +
                "imagen TEXT)";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion,
                          int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS productos");

        onCreate(db);
    }
}