package com.example.app.activities

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.app.R
import com.example.app.utils.NotificationHelper
import com.example.app.utils.SharedPrefsHelper
import android.content.Intent

class BudgetSetupActivity : AppCompatActivity() {

    private lateinit var etBudget: EditText
    private lateinit var btnSaveBudget: Button
    private lateinit var tvBudgetStatus: TextView
    private lateinit var btnBack: Button
    private lateinit var spinnerCurrency: Spinner

    private val currencySymbols = arrayOf("₨", "₹", "$")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget_setup)

        etBudget = findViewById(R.id.etBudget)
        btnSaveBudget = findViewById(R.id.btnSaveBudget)
        tvBudgetStatus = findViewById(R.id.tvBudgetStatus)
        btnBack = findViewById(R.id.btnBack)
        spinnerCurrency = findViewById(R.id.spinnerCurrency)

        // Setup spinner with currency symbols
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencySymbols)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCurrency.adapter = adapter

        // Load saved currency and set spinner selection
        val savedCurrency = SharedPrefsHelper.getCurrency(this)
        val index = currencySymbols.indexOf(savedCurrency)
        if (index >= 0) spinnerCurrency.setSelection(index)

        spinnerCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View, position: Int, id: Long) {
                val selectedCurrency = currencySymbols[position]
                SharedPrefsHelper.saveCurrency(this@BudgetSetupActivity, selectedCurrency)
                showBudgetStatus(SharedPrefsHelper.getBudget(this@BudgetSetupActivity))
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val currentBudget = SharedPrefsHelper.getBudget(this)
        if (currentBudget > 0) {
            etBudget.setText(currentBudget.toString())
        }

        showBudgetStatus(currentBudget)

        btnSaveBudget.setOnClickListener {
            val input = etBudget.text.toString()
            if (input.isNotEmpty()) {
                try {
                    val newBudget = input.toDouble()
                    SharedPrefsHelper.saveBudget(this, newBudget)
                    Toast.makeText(this, getString(R.string.budget_saved), Toast.LENGTH_SHORT).show()
                    showBudgetStatus(newBudget)

                    val totalSpent = SharedPrefsHelper.getExpenses(this)
                    when {
                        totalSpent > newBudget -> {
                            NotificationHelper.sendBudgetAlert(this, "You have exceeded your budget!")
                        }
                        totalSpent >= newBudget * 0.9 -> {
                            NotificationHelper.sendBudgetAlert(this, "Warning: You are about to reach your budget limit!")
                        }
                    }

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()

                } catch (e: NumberFormatException) {
                    Toast.makeText(this, getString(R.string.invalid_budget), Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, getString(R.string.enter_budget), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showBudgetStatus(budget: Double) {
        if (budget <= 0) return

        val totalSpent = SharedPrefsHelper.getExpenses(this)
        val difference = totalSpent - budget
        val currency = SharedPrefsHelper.getCurrency(this)

        val statusText: String
        val statusColor: Int

        when {
            totalSpent > budget -> {
                statusText = "You have spent $currency${String.format("%.2f", totalSpent)} / $currency$budget\n" +
                        "⚠️ Budget Exceeded by $currency${String.format("%.2f", difference)}"
                statusColor = getColor(R.color.warningColor)
            }
            totalSpent >= budget * 0.9 -> {
                statusText = "You have spent $currency${String.format("%.2f", totalSpent)} / $currency$budget\n" +
                        "⚠️ Warning: You are about to reach your budget limit!"
                statusColor = getColor(R.color.warningColor)
            }
            else -> {
                statusText = "You have spent $currency${String.format("%.2f", totalSpent)} / $currency$budget"
                statusColor = getColor(R.color.sucessColor)
            }
        }

        tvBudgetStatus.text = statusText
        tvBudgetStatus.setTextColor(statusColor)
    }
}
