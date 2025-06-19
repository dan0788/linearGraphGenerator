package com.example.lineargraphgenerator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.echo.holographlibrary.LineGraph
import com.echo.holographlibrary.Line
import com.echo.holographlibrary.LinePoint
import com.example.lineargraphgenerator.classes.DrawClass
import com.example.lineargraphgenerator.classes.DateClass // Aunque no se usa directamente para el gráfico, lo mantengo.
import com.example.lineargraphgenerator.databinding.ActivityMainBinding
import android.util.Log
import android.app.AlertDialog
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import android.graphics.Color

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val linePoints: ArrayList<LinePoint> = ArrayList()
    private lateinit var singleLine: Line // Solo necesitamos una instancia de Line
    private val drawClass = DrawClass() // Instancia de DrawClass
    private val lineGraphData: ArrayList<Line> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicialización de la línea del gráfico
        singleLine = Line()
        singleLine.color = Color.BLUE

        // Establecer un título inicial (opcional, pero buena práctica)
        binding.graphTitle.text = "Gráfico de Cantidades"

        // Inicializar el gráfico con datos vacíos o iniciales
        // Si no hay puntos, el gráfico no mostrará nada, lo cual es normal.
        // Lo importante es que el objeto LineGraph tenga asignado un Line.
        binding.lineGraph.lines = lineGraphData
        binding.lineGraph.postInvalidate() // Aseguramos un redibujado inicial

        binding.btnAdd.setOnClickListener {
            val attributeText = binding.editAttribute.text.toString().trim()
            val quantityText = binding.editCuantity.text.toString().trim()

            if (attributeText.isEmpty()) {
                binding.editAttribute.error = "El atributo no puede estar vacío"
                return@setOnClickListener
            }

            // El valor X para el punto de la línea será el índice actual en la lista de puntos.
            val xValue = linePoints.size.toFloat()

            // Intenta añadir el punto a la línea a través de DrawClass.
            val success = drawClass.addPointToLine(linePoints, xValue, quantityText)

            if (success) {
                Log.d("GraphDebug", "Tamaño de puntos antes de asignar: ${linePoints.size}")
                linePoints.forEachIndexed { index, point ->
                    Log.d("GraphDebug", "Punto $index: X=${point.x}, Y=${point.y}")
                }

                // *** SOLUCIÓN CLAVE ***
                // Para que HoloGraphLibrary detecte los cambios, NO solo debemos llamar a setPoints
                // con una nueva instancia, sino que a veces es necesario re-asignar la lista completa
                // de Lines al LineGraph para forzar un redibujo.

                // 1. Asigna los puntos actualizados a la singleLine. Es fundamental pasar una NUEVA
                //    instancia de ArrayList para que HoloGraphLibrary detecte el cambio.
                singleLine.setPoints(ArrayList(linePoints))

                if (lineGraphData.isEmpty()) {
                    lineGraphData.add(singleLine)
                    binding.lineGraph.lines = lineGraphData
                    // Opcional: si lo ocultaste, ahora lo muestras
                    // binding.lineGraph.visibility = View.VISIBLE
                }

                binding.lineGraph.postInvalidate()

                // 3. Invalida la vista para forzar un redibujado.
                binding.lineGraph.postInvalidate()

                // Limpiar campos de entrada
                binding.editAttribute.text.clear()
                binding.editCuantity.text.clear()
                binding.editAttribute.requestFocus()
                Toast.makeText(this, "Punto agregado", Toast.LENGTH_SHORT).show()
            } else {
                binding.editCuantity.error = "Por favor, introduce un número válido para la cantidad (ej. 10.5)"
                Log.e("MainActivity", "Error de entrada: Cantidad no válida.")
            }
        }

        // --- Listener para el botón "Limpiar" ---
        binding.btnLimpiar.setOnClickListener {
            linePoints.clear() // Vacía la lista de puntos
            singleLine.setPoints(ArrayList()) // Asigna una nueva lista de puntos vacía a la línea
            binding.lineGraph.lines = lineGraphData // Re-asigna para asegurar que el gráfico se actualice
            binding.lineGraph.postInvalidate() // Fuerza el redibujado del gráfico
            Log.d("MainActivity", "Gráfico limpiado.")
            Toast.makeText(this, "Gráfico limpiado", Toast.LENGTH_SHORT).show()
        }

        // --- Listener para el botón "Cambiar Título" ---
        binding.btnCambiarTitulo.setOnClickListener {
            val inputEditText = EditText(this)
            inputEditText.hint = "Nuevo título del gráfico"
            inputEditText.setText(binding.graphTitle.text)

            val container = LinearLayout(this)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(50, 0, 50, 0)
            container.layoutParams = params
            container.addView(inputEditText, params)

            AlertDialog.Builder(this).apply {
                setTitle("Cambiar Título del Gráfico")
                setView(container)

                setPositiveButton("Aceptar") { dialog, _ ->
                    val newTitle = inputEditText.text.toString().trim()
                    if (newTitle.isNotEmpty()) {
                        binding.graphTitle.text = newTitle
                        Toast.makeText(this@MainActivity, "Título actualizado", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@MainActivity, "El título no puede estar vacío", Toast.LENGTH_SHORT).show()
                    }
                    dialog.dismiss()
                }

                setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.cancel()
                }
                show()
            }
        }

        // --- Listener para el botón "Eliminar último registro" ---
        binding.btnEliminarUltimo.setOnClickListener {
            if (linePoints.isNotEmpty()) {
                linePoints.removeAt(linePoints.size - 1)
                singleLine.setPoints(ArrayList(linePoints)) // Pasa una nueva instancia
                binding.lineGraph.lines = lineGraphData // Re-asigna para asegurar que el gráfico se actualice
                binding.lineGraph.postInvalidate()
                Log.d("MainActivity", "Último registro eliminado.")
                Toast.makeText(this, "Último registro eliminado", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No hay registros para eliminar", Toast.LENGTH_SHORT).show()
                Log.d("MainActivity", "Intento de eliminar de gráfico vacío.")
            }
        }
    }
}