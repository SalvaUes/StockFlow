package sv.edu.ues.vl23003.stockflow.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ProductoDAO {

    @Insert
    long insertar(Producto producto);

    @Update
    void actualizar(Producto producto);

    @Query("DELETE FROM productos WHERE id = :id")
    void eliminar(int id);

    @Query("SELECT * FROM productos")
    List<Producto> obtenerTodos();

    @Query("SELECT * FROM productos WHERE id = :id")
    Producto obtenerPorId(int id);

    @Query("SELECT * FROM productos WHERE nombre LIKE '%' || :texto || '%' OR sku LIKE '%' || :texto || '%' OR categoria LIKE '%' || :texto || '%' OR precio LIKE '%' || :texto || '%' OR stock LIKE '%' || :texto || '%' OR stockMinimo LIKE '%' || :texto || '%'")
    List<Producto> buscar(String texto);

    @Query("SELECT COUNT(id) FROM productos")
    int getTotalProductos();

    @Query("SELECT SUM(precio * stock) FROM productos")
    double getValorTotalInventario();

    @Query("SELECT COUNT(id) FROM productos WHERE stock <= stockMinimo")
    int getCantidadProductosBajoStock();

    @Query("SELECT * FROM productos WHERE stock <= stockMinimo ORDER BY stock ASC")
    List<Producto> getProductosBajoStock();
}
