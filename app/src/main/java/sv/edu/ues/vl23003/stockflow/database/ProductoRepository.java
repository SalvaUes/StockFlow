package sv.edu.ues.vl23003.stockflow.database;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class ProductoRepository {

    private final ProductoDAO dao;

    public ProductoRepository(Context context) {
        dao = AppDatabase.getInstance(context).productoDAO();
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
}
