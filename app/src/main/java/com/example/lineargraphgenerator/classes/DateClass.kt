package com.example.lineargraphgenerator.classes

import com.example.lineargraphgenerator.interfaces.DateInterface
import java.util.Calendar

class DateClass : DateInterface{
    override fun getCurrentYear(): Int {
        return Calendar.getInstance().get(Calendar.YEAR)
    }
}