package sv.edu.ues.vl23003.stockflow.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class ProductoDAO {

    private final AppDatabase dbHelper;

    public ProductoDAO(Context context) {
        dbHelper = new AppDatabase(context);
    }

    public long insertar(Producto producto){

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("nombre", producto.getNombre());
        values.put("sku", producto.getSku());
        values.put("categoria", producto.getCategoria());
        values.put("precio", producto.getPrecio());
        values.put("stock", producto.getStock());
        values.put("stockMinimo", producto.getStockMinimo());
        values.put("imagen", producto.getImagen());

        return db.insert("productos", null, values);
    }

    public ArrayList<Producto> obtenerTodos(){

        ArrayList<Producto> lista = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor =
                db.rawQuery("SELECT * FROM productos", null);

        while(cursor.moveToNext()){

            Producto p = new Producto();

            p.setId(cursor.getInt(0));
            p.setNombre(cursor.getString(1));
            p.setSku(cursor.getString(2));
            p.setCategoria(cursor.getString(3));
            p.setPrecio(cursor.getDouble(4));
            p.setStock(cursor.getInt(5));
            p.setStockMinimo(cursor.getInt(6));
            p.setImagen(cursor.getString(7));

            lista.add(p);
        }

        cursor.close();

        return lista;
    }

    public void eliminar(int id){

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(
                "productos",
                "id=?",
                new String[]{String.valueOf(id)}
        );
    }

    public Producto obtenerPorId(int id){

        SQLiteDatabase db =
                dbHelper.getReadableDatabase();

        Cursor cursor =
                db.rawQuery(
                        "SELECT * FROM productos WHERE id=?",
                        new String[]{
                                String.valueOf(id)
                        }
                );

        Producto producto = null;

        if(cursor.moveToFirst()){

            producto = new Producto();

            producto.setId(cursor.getInt(0));
            producto.setNombre(cursor.getString(1));
            producto.setSku(cursor.getString(2));
            producto.setCategoria(cursor.getString(3));
            producto.setPrecio(cursor.getDouble(4));
            producto.setStock(cursor.getInt(5));
            producto.setStockMinimo(cursor.getInt(6));
            producto.setImagen(cursor.getString(7));
        }

        cursor.close();

        return producto;
    }

    public ArrayList<Producto> buscar(String texto){

        ArrayList<Producto> lista =
                new ArrayList<>();

        SQLiteDatabase db =
                dbHelper.getReadableDatabase();

        Cursor cursor =
                db.rawQuery(
                        "SELECT * FROM productos " +
                                "WHERE nombre LIKE ? " +
                                "OR sku LIKE ? " +
                                "OR categoria LIKE ? " +
                                "OR precio LIKE ? " +
                                "OR stock LIKE ? " +
                                "OR stockMinimo LIKE ?",
                        new String[]{
                                "%" + texto + "%",
                                "%" + texto + "%",
                                "%" + texto + "%",
                                "%" + texto + "%",
                                "%" + texto + "%",
                                "%" + texto + "%"
                        });

        while(cursor.moveToNext()){

            Producto p = new Producto();

            p.setId(cursor.getInt(0));
            p.setNombre(cursor.getString(1));
            p.setSku(cursor.getString(2));
            p.setCategoria(cursor.getString(3));
            p.setPrecio(cursor.getDouble(4));
            p.setStock(cursor.getInt(5));
            p.setStockMinimo(cursor.getInt(6));
            p.setImagen(cursor.getString(7));

            lista.add(p);
        }

        cursor.close();

        return lista;
    }

    public void actualizar(Producto producto){

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("nombre", producto.getNombre());
        values.put("sku", producto.getSku());
        values.put("categoria", producto.getCategoria());
        values.put("precio", producto.getPrecio());
        values.put("stock", producto.getStock());
        values.put("stockMinimo", producto.getStockMinimo());
        values.put("imagen", producto.getImagen());

        db.update(
                "productos",
                values,
                "id=?",
                new String[]{String.valueOf(producto.getId())}
        );
    }

}