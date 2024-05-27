package com.example.calciman

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.calciman.ui.theme.CalciManTheme

class MainActivity : ComponentActivity() {

    private lateinit var working: TextView
    private lateinit var result: TextView
    private var canAddOperation = false
    private var canAddDecimal = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        working = findViewById(R.id.working)
        result = findViewById(R.id.result)
    }

    fun numberAction(view: View) {
        if (view is Button) {
            if (view.text == ".") {
                if (canAddDecimal) {
                    working.append(view.text)
                    canAddDecimal = false
                }
            } else {
                working.append(view.text)
            }
            canAddOperation = true
        }
    }

    fun operatorAction(view: View) {
        if (view is Button && canAddOperation) {
            working.append(view.text)
            canAddOperation = false
            canAddDecimal = true
        }
    }

    fun allClearAction(view: View) {
        working.text = ""
        result.text = ""
    }

    fun backSpaceAction(view: View) {
        val length = working.length()
        if (length > 0) {
            working.text = working.text.subSequence(0, length - 1)
        }
    }

    fun equalsAction(view: View) {
        result.text = calculateResults()
    }

    private fun calculateResults(): String {
        val digitsOperators = extractDigitsAndOperators()
        if (digitsOperators.isEmpty()) return ""

        val timeDivisionResult = calculateMultiplicationAndDivision(digitsOperators)
        if (timeDivisionResult.isEmpty()) return ""

        val result = calculateAdditionAndSubtraction(timeDivisionResult)
        return result.toString()
    }

    private fun extractDigitsAndOperators(): MutableList<Any> {
        val list = mutableListOf<Any>()
        var currentDigit = ""

        for (character in working.text) {
            if (character.isDigit() || character == '.') {
                currentDigit += character
            } else {
                list.add(currentDigit.toFloatOrNull() ?: return mutableListOf())
                currentDigit = ""
                list.add(character)
            }
        }
        if (currentDigit.isNotEmpty()) {
            list.add(currentDigit.toFloatOrNull() ?: return mutableListOf())
        }

        return list
    }

    private fun calculateMultiplicationAndDivision(passedList: MutableList<Any>): MutableList<Any> {
        var list = passedList
        while (list.contains('X') || list.contains('/')) {
            list = performMultiplicationAndDivision(list)
        }
        return list
    }

    private fun performMultiplicationAndDivision(passedList: MutableList<Any>): MutableList<Any> {
        val newList = mutableListOf<Any>()
        var restartIndex = passedList.size

        for (i in passedList.indices) {
            if (passedList[i] is Char && i != passedList.lastIndex && i < restartIndex) {
                val operator = passedList[i]
                val prevDigit = passedList[i - 1] as Float
                val nextDigit = passedList[i + 1] as Float

                when (operator) {
                    'X' -> {
                        newList.add(prevDigit * nextDigit)
                        restartIndex = i + 1
                    }
                    '/' -> {
                        if (nextDigit != 0f) {
                            newList.add(prevDigit / nextDigit)
                            restartIndex = i + 1
                        } else {
                            return mutableListOf()  // Handle division by zero
                        }
                    }
                    else -> {
                        newList.add(prevDigit)
                        newList.add(operator)
                    }
                }
            }
            if (i > restartIndex) {
                newList.add(passedList[i])
            }
        }
        return newList
    }

    private fun calculateAdditionAndSubtraction(passedList: MutableList<Any>): Float {
        var result = passedList[0] as Float

        for (i in passedList.indices) {
            if (passedList[i] is Char && i != passedList.lastIndex) {
                val operator = passedList[i]
                val nextDigit = passedList[i + 1] as Float
                when (operator) {
                    '+' -> result += nextDigit
                    '-' -> result -= nextDigit
                }
            }
        }
        return result
    }
}
