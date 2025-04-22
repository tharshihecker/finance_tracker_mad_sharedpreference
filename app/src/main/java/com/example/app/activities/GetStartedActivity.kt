package com.example.app.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.app.R
import com.example.app.models.Transaction
import com.example.app.utils.SharedPrefsHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject

class GetStartedActivity : AppCompatActivity() {

    private var tapCounter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_started)

        val btnContinue = findViewById<Button>(R.id.btnContinue)
        btnContinue.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Tap appName 5 times to import sample data
        val appName = findViewById<TextView>(R.id.appName)
        appName.setOnClickListener {
            tapCounter++
            if (tapCounter >= 5) {
                importSampleData()
                tapCounter = 0
            }
        }
    }

    private fun importSampleData() {
        try {
            // 1) Read the raw JSON
            val inputStream = resources.openRawResource(R.raw.sample_backup)
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val obj = JSONObject(jsonString)

            // 2) Parse out the two pieces
            val transactionsJson = obj.getJSONArray("transactions").toString()
            val currencySymbol  = obj.getString("currencySymbol")

            // 3) Overwrite via your helper
            //    - finance_prefs is what SharedPrefsHelper uses
            //    - KEY_TRANSACTIONS and KEY_CURRENCY are hard‑coded there
            val txListType = object : TypeToken<List<Transaction>>() {}.type
            val txList: List<Transaction> = Gson().fromJson(transactionsJson, txListType)

            SharedPrefsHelper.saveAllTransactions(this, txList)
            SharedPrefsHelper.saveCurrency(this, currencySymbol)

            Toast.makeText(this, "Sample data imported!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Import failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }


}
