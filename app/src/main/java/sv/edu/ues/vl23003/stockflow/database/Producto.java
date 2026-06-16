package sv.edu.ues.vl23003.stockflow.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "productos")
public class Producto {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String nombre;
    private String sku;
    private String categoria;
    private double precio;
    private int stock;
    private int stockMinimo;
    private String imagen;

    public Producto() {
    }

    public Producto(int id,
                    String nombre,
                    String sku,
                    String categoria,
                    double precio,
                    int stock,
                    int stockMinimo,
                    String imagen) {

        this.id = id;
        this.nombre = nombre;
        this.sku = sku;
        this.categoria = categoria;
        this.precio = precio;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
        this.imagen = imagen;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getEstado() {

        if (stock == 0) {
            return "Agotado";
        }

        if (stock <= stockMinimo) {
            return "Bajo Stock";
        }

        return "Disponible";
    }
}