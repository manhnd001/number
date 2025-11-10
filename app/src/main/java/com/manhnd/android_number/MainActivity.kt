package com.manhnd.android_number

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.RadioButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.LinkedHashSet

class MainActivity : AppCompatActivity() {

    private enum class NumberType {
        ODD, EVEN, PRIME, PERFECT, SQUARE, FIBONACCI
    }

    private lateinit var inputNumber: EditText
    private lateinit var numberList: ListView
    private lateinit var emptyView: TextView

    private val numberButtons: MutableMap<NumberType, RadioButton> = mutableMapOf()
    private lateinit var adapter: ArrayAdapter<String>

    private var selectedType = NumberType.ODD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupViews()
        setupListeners()
        updateSelection(NumberType.ODD)
        refreshNumbers()
    }

    private fun setupViews() {
        inputNumber = findViewById(R.id.inputNumber)
        numberList = findViewById(R.id.numberList)
        emptyView = findViewById(R.id.emptyView)

        numberButtons[NumberType.ODD] = findViewById(R.id.radioOdd)
        numberButtons[NumberType.EVEN] = findViewById(R.id.radioEven)
        numberButtons[NumberType.PRIME] = findViewById(R.id.radioPrime)
        numberButtons[NumberType.PERFECT] = findViewById(R.id.radioPerfect)
        numberButtons[NumberType.SQUARE] = findViewById(R.id.radioSquare)
        numberButtons[NumberType.FIBONACCI] = findViewById(R.id.radioFibonacci)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
        numberList.adapter = adapter
        numberList.emptyView = emptyView
    }

    private fun setupListeners() {
        numberButtons.forEach { (type, button) ->
            button.setOnClickListener {
                if (selectedType != type) {
                    updateSelection(type)
                    refreshNumbers()
                }
            }
        }

        inputNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                refreshNumbers()
            }

            override fun afterTextChanged(s: Editable?) = Unit
        })
    }

    private fun updateSelection(type: NumberType) {
        selectedType = type
        numberButtons.forEach { (numberType, button) ->
            button.isChecked = numberType == type
        }
    }

    private fun refreshNumbers() {
        val limit = inputNumber.text.toString().toIntOrNull()
        val numbers = if (limit != null) generateNumbers(limit, selectedType) else emptyList()
        adapter.clear()
        adapter.addAll(numbers.map { it.toString() })
        adapter.notifyDataSetChanged()
    }

    private fun generateNumbers(limit: Int, type: NumberType): List<Int> {
        if (limit <= 1) return emptyList()
        return when (type) {
            NumberType.ODD -> (1 until limit).filter { it % 2 != 0 }
            NumberType.EVEN -> (2 until limit).filter { it % 2 == 0 }
            NumberType.PRIME -> (2 until limit).filter { isPrime(it) }
            NumberType.PERFECT -> (2 until limit).filter { isPerfect(it) }
            NumberType.SQUARE -> generateSquares(limit)
            NumberType.FIBONACCI -> generateFibonacci(limit)
        }
    }

    private fun isPrime(value: Int): Boolean {
        if (value < 2) return false
        if (value == 2) return true
        if (value % 2 == 0) return false
        var i = 3
        while (i * i <= value) {
            if (value % i == 0) return false
            i += 2
        }
        return true
    }

    private fun isPerfect(value: Int): Boolean {
        if (value < 2) return false
        var sum = 1
        var divisor = 2
        while (divisor * divisor <= value) {
            if (value % divisor == 0) {
                sum += divisor
                val pair = value / divisor
                if (pair != divisor) {
                    sum += pair
                }
            }
            divisor++
        }
        return sum == value
    }

    private fun generateSquares(limit: Int): List<Int> {
        val squares = mutableListOf<Int>()
        var base = 1
        while (true) {
            val square = base * base
            if (square >= limit) break
            squares.add(square)
            base++
        }
        return squares
    }

    private fun generateFibonacci(limit: Int): List<Int> {
        if (limit <= 1) return emptyList()
        val fibonacci = LinkedHashSet<Int>()
        var a = 0
        var b = 1
        while (a < limit) {
            if (a > 0) {
                fibonacci.add(a)
            }
            val next = a + b
            a = b
            b = next
        }
        return fibonacci.toList()
    }
}
