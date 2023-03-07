package valle.carlos.misnotas

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import org.w3c.dom.Text
import java.io.BufferedReader
import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    var notas= ArrayList<Nota>()
    lateinit var adaptador: AdaptadorNotas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fab: Button = findViewById(R.id.fab) as Button

        fab.setOnClickListener{
            var intent= Intent(this, AgregarNotaActivity::class.java)
            startActivityForResult(intent, 123)
        }

        notasDePrueba()

        var listview: ListView= findViewById(R.id.listview) as ListView

        adaptador= AdaptadorNotas(this, notas)
        listview.adapter= adaptador
    }

    fun leerNotas(){
        notas.clear()
        var carpeta= File(ubicacion().absolutePath)

        if(carpeta.exists()){
            var archivos= carpeta.listFiles()
            if(archivos != null){
                for(archivo in archivos){
                    leerArchivo(archivo)
                }
            }
        }
    }

    fun leerArchivo(archivo: File){
        val fis= FileInputStream(archivo)
        val di= DataInputStream(fis)
        val br= BufferedReader(InputStreamReader(di))
        var strLine: String? = br.readLine()
        var myData= ""

        while(strLine != null){
            myData= myData + strLine
            strLine= br.readLine()
        }

        br.close()
        di.close()
        fis.close()

        var nombre= archivo.name.substring(0, archivo.name.length-4)

        var nota= Nota(nombre, myData)
        notas.add(nota)
    }

    private fun ubicacion(): File{
        val folder= File(Environment.getExternalStorageDirectory(), "Titulo")
        if(!folder.exists()){
            folder.mkdir()
        }

        return folder
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 123){
            leerNotas()
            adaptador.notifyDataSetChanged()
        }
    }

    fun notasDePrueba(){
        notas.add(Nota("prueba 1", "Contenido de la nota 1"))
        notas.add(Nota("prueba 2", "Contenido de la nota 2"))
        notas.add(Nota("prueba 3", "Contenido de la nota 3"))
    }

    class AdaptadorNotas: BaseAdapter {
        var context: Context?=null
        var notas= ArrayList<Nota>()

        constructor(context: Context, notas: ArrayList<Nota>){
            this.context= context
            this.notas= notas
        }

        override fun getCount(): Int {
             return notas.size
        }

        override fun getItem(p0: Int): Any {
           return notas[p0]
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            var inflador= LayoutInflater.from(context)
            var vista= inflador.inflate(R.layout.nota_layout, null)
            var nota= notas[p0]

            var titulo= vista.findViewById(R.id.tv_titulo_det) as TextView
            var contenido= vista.findViewById(R.id.tv_contenido_det) as TextView
            var borrar= vista.findViewById(R.id.boton_borrar) as ImageView

            titulo.setText(nota.titulo)
            contenido.setText(nota.contenido)

            borrar.setOnClickListener{
                eliminar(nota.titulo)
                notas.remove(nota)
                this.notifyDataSetChanged()
            }

            return vista
        }

        private fun eliminar(titulo: String){
            if(titulo == ""){
                Toast.makeText(context, "Error: Titulo Vacío", Toast.LENGTH_SHORT).show()
            }else{
                try{
                    val archivo= File(ubicacion(), titulo + ".txt")
                    archivo.delete()

                    Toast.makeText(context, "Se eliminó el archivo", Toast.LENGTH_SHORT).show()
                }catch (e: Exception){
                    Toast.makeText(context, "Error al eliminar el archivo", Toast.LENGTH_SHORT).show()
                }
            }
        }

        private fun ubicacion(): String{
            val album= File(Environment.getExternalStorageDirectory(), "notas")
            if(!album.exists()){
                album.mkdir()
            }

            return album.absolutePath
        }

    }

}

