package sv.edu.ues.vl23003.stockflow.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(
        entities = {Producto.class},
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ProductoDAO productoDao();
}