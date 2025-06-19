package com.example.lineargraphgenerator.interfaces

import com.echo.holographlibrary.LinePoint

interface DrawInterface {
    fun addPointToLine(linePoints: ArrayList<LinePoint>, xValue: Float, yValue: String): Boolean
    fun generarColorHexAleatorio() : String
}