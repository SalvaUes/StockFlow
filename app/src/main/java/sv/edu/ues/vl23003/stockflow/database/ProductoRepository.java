package sv.edu.ues.vl23003.stockflow.database;

import android.content.Context;

import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;

public class ProductoRepository {

    private final ProductoDAO dao;

    public ProductoRepository(Context context) {

        AppDatabase db = Room.databaseBuilder(
                        context.getApplicationContext(),
                        AppDatabase.class,
                        "inventario.db"
                )
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();

        dao = db.productoDao();
    }

    public int getTotalProductos() {
        return dao.getTotalProductos();
    }

    public double getValorTotalInventario() {
        return dao.getValorTotalInventario();
    }

    public int getCantidadProductosBajoStock() {
        return dao.getCantidadProductosBajoStock();
    }

    public List<Producto> getProductosBajoStock() {
        return dao.getProductosBajoStock();
    }

    public long insertar(Producto producto) {
        return dao.insertar(producto);
    }

    public void actualizar(Producto producto) {
        dao.actualizar(producto);
    }

    public void eliminar(Producto producto) {
        dao.eliminar(producto);
    }

    public Producto obtenerPorId(int id) {
        return dao.obtenerPorId(id);
    }

    public ArrayList<Producto> obtenerTodos() {
        return new ArrayList<>(dao.obtenerTodos());
    }

    public ArrayList<Producto> buscar(String texto) {
        return new ArrayList<>(dao.buscar(texto));
    }
}