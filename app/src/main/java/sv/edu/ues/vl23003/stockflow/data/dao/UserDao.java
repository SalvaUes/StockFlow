package sv.edu.ues.vl23003.stockflow.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import sv.edu.ues.vl23003.stockflow.data.entities.User;


@Dao
public interface UserDao {
    @Insert
    void insertUser(User user);

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    User login(String email, String password);

    @Query("SELECT * FROM users WHERE id = :userId")
    User getUserById(int userId);

    @Update
    void updateUser(User user);
}
