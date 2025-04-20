package com.example.app.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.app.R
import com.example.app.utils.SharedPrefsHelper

class MainActivity : AppCompatActivity() {

    private lateinit var tvTotalIncome: TextView
    private lateinit var tvTotalExpenses: TextView
    private lateinit var tvBudget: TextView
    private lateinit var tvBudgetStatus: TextView
    private lateinit var tvNetBalance: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // --- Initialize Views ---
        tvTotalIncome = findViewById(R.id.tvTotalIncome)
        tvTotalExpenses = findViewById(R.id.tvTotalExpenses)
        tvBudget = findViewById(R.id.tvBudget)
        tvBudgetStatus = findViewById(R.id.tvBudgetStatus)
        tvNetBalance = findViewById(R.id.tvNetBalance)

        // --- Navigation Buttons ---
        findViewById<Button>(R.id.btnSetupIncome).setOnClickListener {
            startActivity(Intent(this, AddIncomeActivity::class.java))
        }
        findViewById<Button>(R.id.btnAddTransaction).setOnClickListener {
            startActivity(Intent(this, AddTransactionActivity::class.java))
        }
        findViewById<Button>(R.id.btnCategoryAnalysis).setOnClickListener {
            startActivity(Intent(this, CategoryAnalysisActivity::class.java))
        }
        findViewById<Button>(R.id.btnSetupBudget).setOnClickListener {
            startActivity(Intent(this, BudgetSetupActivity::class.java))
        }
        findViewById<Button>(R.id.btnBackupRestore).setOnClickListener {
            startActivity(Intent(this, DataBackupActivity::class.java))
        }
        findViewById<Button>(R.id.btnHistroy).setOnClickListener {
            startActivity(Intent(this, TransactionHistory::class.java))
        }
    }

    override fun onResume() {
        super.onResume()

        // --- Fetch Updated Data ---
        val income = SharedPrefsHelper.getIncome(this)
        val expense = SharedPrefsHelper.getExpenses(this)
        val budget = SharedPrefsHelper.getBudget(this)
        val net = income - expense
        val diff = budget - expense
        val status = if (diff >= 0) "Saved Rs.$diff" else "Exceeded by Rs.${-diff}"

        // --- Update Views ---
        tvTotalIncome.text = "Total Income: Rs.$income"
        tvTotalExpenses.text = "Total Expenses: Rs.$expense"
        tvBudget.text = "Budget: Rs.$budget"
        tvBudgetStatus.text = status
        tvNetBalance.text = "Net Balance: Rs.$net"
    }
}
