package com.example.app.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.app.R
import com.example.app.models.Transaction
import com.example.app.utils.SharedPrefsHelper

class CategoryAnalysisActivity : AppCompatActivity() {

    private lateinit var summaryLayout: LinearLayout
    private lateinit var emptyText: TextView
    private lateinit var btnBack: Button
    private lateinit var scrollContainer: ScrollView
    private lateinit var transactions: List<Transaction>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_analysis)

        summaryLayout   = findViewById(R.id.categoryAnalysisLayout)
        emptyText       = findViewById(R.id.tvNoDataCategory)
        btnBack         = findViewById(R.id.btnBack)
        scrollContainer = findViewById(R.id.scrollContainer)

        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        transactions = SharedPrefsHelper.getTransactions(this)
        displayAnalysis()
    }

    private fun displayAnalysis() {
        // Clear any old views (including the emptyText)
        summaryLayout.removeAllViews()

        if (transactions.isEmpty()) {
            // Show empty state
            emptyText.apply {
                // Convert 32dp to px
                val topPx = (32 * resources.displayMetrics.density).toInt()
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { topMargin = topPx }
                textSize = 20f
                setTypeface(typeface, Typeface.BOLD)
                gravity = Gravity.CENTER
                visibility = View.VISIBLE
            }
            summaryLayout.addView(emptyText)
            return
        }

        // Group transactions by category
        val grouped = transactions.groupBy { it.category }
        for ((category, txns) in grouped) {
            // Category header
            val categoryTitle = TextView(this).apply {
                text = "📂 $category"
                textSize = 25f
                setTypeface(typeface, Typeface.BOLD)
                setTextColor(resources.getColor(R.color.primaryText, null))
                // margin bottom
                setPadding(0, 16, 0, 8)
            }
            summaryLayout.addView(categoryTitle)

            // List each txn
            var total = 0.0
            txns.forEach { txn ->
                val detail = TextView(this).apply {
                    text = "• Rs.${"%.2f".format(txn.amount)} on ${txn.date}"
                    textSize = 20f
                    setTypeface(typeface, Typeface.BOLD)
                    setTextColor(resources.getColor(R.color.primaryText, null))
                    setPadding(32, 4, 0, 4)
                }
                summaryLayout.addView(detail)
                total += txn.amount
            }

            // Total line
            val totalView = TextView(this).apply {
                text = "➤ Total: Rs. ${"%.2f".format(total)}"
                textSize = 20f
                setTypeface(typeface, Typeface.BOLD)
                setTextColor(resources.getColor(R.color.primaryText, null))
                setPadding(32, 8, 0, 24)
            }
            summaryLayout.addView(totalView)
        }
    }
}
