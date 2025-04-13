package com.example.app.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.app.R
import com.example.app.models.Transaction
import com.example.app.utils.SharedPrefsHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.*

class DataBackupActivity : AppCompatActivity() {

    private val fileName = "transactions_backup.json"
    private lateinit var btnBack: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_backup)

        val btnExport = findViewById<Button>(R.id.btnExport)
        val btnImport = findViewById<Button>(R.id.btnImport)
        btnBack = findViewById(R.id.btnBack)

        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        btnExport.setOnClickListener {
            exportData()
        }

        btnImport.setOnClickListener {
            importData()
        }
    }

    private fun exportData() {
        val transactions = SharedPrefsHelper.getTransactions(this)
        val json = Gson().toJson(transactions)

        try {
            openFileOutput(fileName, MODE_PRIVATE).use {
                it.write(json.toByteArray())
            }
            Toast.makeText(this, "Exported successfully!", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Export failed!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun importData() {
        try {
            val input = openFileInput(fileName)
            val json = input.bufferedReader().use { it.readText() }
            val type = object : TypeToken<List<Transaction>>() {}.type
            val transactionList: List<Transaction> = Gson().fromJson(json, type)

            // Save to SharedPreferences
            val prefs = getSharedPreferences("finance_prefs", MODE_PRIVATE)
            prefs.edit().putString("transactions", Gson().toJson(transactionList)).apply()

            Toast.makeText(this, "Imported successfully!", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Import failed!", Toast.LENGTH_SHORT).show()
        }
    }
}
