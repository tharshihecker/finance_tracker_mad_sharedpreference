package com.example.app.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.app.R

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
}
