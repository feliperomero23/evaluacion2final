package  com.example.evaluacion2

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Tarea(
    @PrimaryKey(autoGenerate = true) val id:Int,
    var tarea:String,
    var realizada:Boolean
)