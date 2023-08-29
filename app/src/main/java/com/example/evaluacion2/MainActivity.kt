package  com.example.evaluacion2



import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete

import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch (Dispatchers.IO){
            val tareaDao = AppDatabase.getInstance(this@MainActivity).tareaDao()
            val canRegistros = tareaDao.contar()
            if(canRegistros<1){
            }
        }

        setContent {
            ListaTareasUI()
        }
    }
}

@Composable
fun ListaTareasUI(){
    val contexto = LocalContext.current
    val (tareas, setTareas) = remember { mutableStateOf(emptyList<Tarea>())}

    LaunchedEffect(tareas ){
        withContext(Dispatchers.IO){
            val dao=AppDatabase.getInstance(contexto).tareaDao()
            setTareas(dao.findAll())
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ){
        items(tareas){tarea ->
            TareaItemUI(tarea){
                setTareas(emptyList<Tarea>())
            }

        }

        // Agregar un elemento para crear una nueva tarea
        item {
            CrearTareaUI(contexto = contexto, onTareaCreated = {
                setTareas(emptyList<Tarea>())
            })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearTareaUI(contexto: Context, onTareaCreated: () -> Unit) {
    val alcanceCorrutina = rememberCoroutineScope()
    var nuevaTareaText by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp, horizontal = 20.dp)
    ) {
        TextField(
            value = nuevaTareaText,
            onValueChange = { nuevaTareaText = it },
            modifier = Modifier.weight(2f),
            label = { Text("Agregar") }
        )

        Spacer(modifier = Modifier.width(30.dp))

        Button(
            onClick = {
                alcanceCorrutina.launch(Dispatchers.IO) {
                    val dao = AppDatabase.getInstance(contexto).tareaDao()
                    val nuevaTarea = Tarea(0, nuevaTareaText, false)
                    dao.insertar(nuevaTarea)
                    nuevaTareaText = ""
                    onTareaCreated()

                }
            }
        ) {
            Text("Agregar Producto")
        }

}}


@Composable
fun TareaItemUI(tarea:Tarea, onSave:() ->Unit={}){
    val contexto = LocalContext.current
    val alcanceCorrutina = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp, horizontal = 20.dp)
    ){
        if(tarea.realizada){
            Icon(
                Icons.Filled.Check,
                contentDescription = "Realizado",
                modifier = Modifier.clickable {
                    alcanceCorrutina.launch(Dispatchers.IO) {
                        val dao= AppDatabase.getInstance(contexto).tareaDao()
                        tarea.realizada=false
                        dao.actualizar(tarea)
                        onSave()
                    }
                }
            )
        }else{
            Icon(
                Icons.Filled.ShoppingCart,
                contentDescription="",
                modifier = Modifier.clickable {
                    alcanceCorrutina.launch(Dispatchers.IO) {
                        val dao= AppDatabase.getInstance(contexto).tareaDao()
                        tarea.realizada=true
                        dao.actualizar(tarea)
                        onSave()
                    }
                }
            )
        }

        Spacer(modifier=Modifier.width(20.dp))
        Text(
            text=tarea.tarea,
            modifier=Modifier.weight(2f)
        )
        Icon(
            Icons.Filled.Delete,
            contentDescription = "Eliminar Producto",
            modifier = Modifier.clickable {
                alcanceCorrutina.launch(Dispatchers.IO) {
                    val dao= AppDatabase.getInstance(contexto).tareaDao()
                    dao.eliminar(tarea)
                    onSave()
                }
            }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun TareaItemUIPreview(){
    val tarea = Tarea (1,"ejemplo", true)
    TareaItemUI(tarea)
}

@Preview(showBackground = true)
@Composable
fun TareaItemUIPreview2() {
    val tarea = Tarea(1, "ejemplo", false)
    TareaItemUI(tarea)
}