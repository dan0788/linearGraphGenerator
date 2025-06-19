package com.example.lineargraphgenerator.classes

import com.echo.holographlibrary.LinePoint
import com.example.lineargraphgenerator.interfaces.DrawInterface
import android.graphics.Color
import java.util.Random
import android.util.Log

class DrawClass : DrawInterface {
    override fun addPointToLine(linePoints: ArrayList<LinePoint>, xValue: Float, yValue: String): Boolean {
        try {
            val yFloatValue = yValue.toFloat()
            val point = LinePoint()
            point.x = xValue
            point.y = yFloatValue
            linePoints.add(point)

            return true
        } catch (e: NumberFormatException) {
            Log.e(
                "Draw",
                "Error al convertir la cantidad '$yValue' a Float para punto de línea: ${e.message}",
                e
            )
            return false
        }
    }

    override fun generarColorHexAleatorio(): String {
        val random = Random()

        val red = random.nextInt(256)
        val green = random.nextInt(256)
        val blue = random.nextInt(256)

        return String.format("#%02X%02X%02X", red, green, blue)
    }
}