package com.example.app.utils

import android.content.Context
import com.example.app.models.Transaction
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object SharedPrefsHelper {
    private const val PREF_NAME = "finance_prefs"
    private const val KEY_TRANSACTIONS = "transactions"
    private const val KEY_BUDGET = "budget"

    fun saveTransaction(context: Context, transaction: Transaction) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val list = getTransactions(context).toMutableList()
        list.add(transaction)

        val json = Gson().toJson(list)
        prefs.edit().putString(KEY_TRANSACTIONS, json).apply()
    }

    fun getTransactions(context: Context): List<Transaction> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_TRANSACTIONS, "[]")
        val type = object : TypeToken<List<Transaction>>() {}.type
        return Gson().fromJson(json, type)
    }

    fun saveBudget(context: Context, budget: Double) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putFloat(KEY_BUDGET, budget.toFloat()).apply()
    }

    fun getBudget(context: Context): Double {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getFloat(KEY_BUDGET, 0f).toDouble()
    }

    // Update existing transaction in SharedPreferences using transaction ID
    fun updateTransaction(context: Context, updatedTransaction: Transaction) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val transactions = getTransactions(context).toMutableList()

        // Find and replace the transaction using ID
        for (i in transactions.indices) {
            if (transactions[i].id == updatedTransaction.id) {
                transactions[i] = updatedTransaction
                break
            }
        }

        // Save the updated list back to SharedPreferences
        val json = Gson().toJson(transactions)
        prefs.edit().putString(KEY_TRANSACTIONS, json).apply()
    }

    // Delete a transaction from SharedPreferences using transaction ID
    fun deleteTransaction(context: Context, transactionToDelete: Transaction) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val transactions = getTransactions(context).toMutableList()

        // Remove the transaction using ID
        transactions.removeIf { it.id == transactionToDelete.id }

        // Save the updated list back to SharedPreferences
        val json = Gson().toJson(transactions)
        prefs.edit().putString(KEY_TRANSACTIONS, json).apply()
    }

    // Save all transactions in SharedPreferences
    fun saveAllTransactions(context: Context, transactions: List<Transaction>) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = Gson().toJson(transactions)
        prefs.edit().putString(KEY_TRANSACTIONS, json).apply()
    }

    // Clear all transactions from SharedPreferences
    fun clearAllTransactions(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_TRANSACTIONS).apply()  // Remove the transactions key from SharedPreferences
    }
}
