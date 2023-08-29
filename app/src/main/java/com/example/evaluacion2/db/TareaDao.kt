package  com.example.evaluacion2

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TareaDao {

    @Query("SELECT * FROM tarea ORDER BY realizada")
    fun findAll():List<Tarea>

    @Query("SELECT COUNT(*)FROM tarea")
    fun contar(): Int

    @Insert
    fun insertar(tarea: Tarea):Long

    @Update
    fun actualizar(tarea:Tarea)

    @Delete
    fun eliminar(tarea:Tarea)



}