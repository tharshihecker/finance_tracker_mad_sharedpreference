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
import java.io.IOException

class DataBackupActivity : AppCompatActivity() {

    private val fileName = "transactions_backup.json"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_backup)

        findViewById<Button>(R.id.btnBack).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        findViewById<Button>(R.id.btnExport).setOnClickListener { exportData() }
        findViewById<Button>(R.id.btnImport).setOnClickListener { importData() }
    }

    private fun exportData() {
        val transactions = SharedPrefsHelper.getTransactions(this)
        val currencySymbol = SharedPrefsHelper.getCurrency(this)

        // Combine into a single map
        val backupData = mapOf(
            "transactions"   to transactions,
            "currencySymbol" to currencySymbol
        )

        val json = Gson().toJson(backupData)
        try {
            openFileOutput(fileName, MODE_PRIVATE).use { it.write(json.toByteArray()) }
            Toast.makeText(this, "Exported successfully!", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Export failed!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun importData() {
        try {
            val json = openFileInput(fileName).bufferedReader().use { it.readText() }
            val type = object : TypeToken<Map<String, Any>>() {}.type
            val backupData: Map<String, Any> = Gson().fromJson(json, type)

            // Transactions list
            val transactionsJson = Gson().toJson(backupData["transactions"])
            val listType = object : TypeToken<List<Transaction>>() {}.type
            val transactionList: List<Transaction> = Gson().fromJson(transactionsJson, listType)

            // Currency symbol
            val currencySymbol = backupData["currencySymbol"] as? String ?: "₨"

            // Save both via helper
            SharedPrefsHelper.saveAllTransactions(this, transactionList)
            SharedPrefsHelper.saveCurrency(this, currencySymbol)

            Toast.makeText(this, "Imported successfully!", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Import failed!", Toast.LENGTH_SHORT).show()
        }
    }
}
