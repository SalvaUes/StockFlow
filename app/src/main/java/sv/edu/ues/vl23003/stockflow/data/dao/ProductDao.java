package sv.edu.ues.vl23003.stockflow.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import java.util.List;

import sv.edu.ues.vl23003.stockflow.data.entities.Product;

@Dao
public interface ProductDao {

    @Insert
    void insertProduct(Product product);

    @Query("SELECT * FROM products")
    List<Product> getAllProducts();

    @Query("SELECT * FROM products WHERE category = :categoryName")
    List<Product> getProductsByCategory(String categoryName);

    @Update
    void updateProduct(Product product);

    @Delete
    void deleteProduct(Product product);

    @Query("SELECT COUNT(*) FROM products")
    int getTotalProductsCount();

    @Query("SELECT * FROM products WHERE stock <= minStock")
    List<Product> getLowStockProducts();
}
