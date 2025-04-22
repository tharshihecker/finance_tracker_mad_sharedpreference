package com.example.app.utils

import android.content.Context
import com.example.app.models.Transaction
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object SharedPrefsHelper {
    private const val PREF_NAME = "finance_prefs"
    private const val KEY_TRANSACTIONS = "transactions"
    private const val KEY_BUDGET = "budget"
    private const val KEY_CURRENCY = "currency"     // <— use this everywhere

    // Save a single transaction
    fun saveTransaction(context: Context, transaction: Transaction) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val list = getTransactions(context).toMutableList().apply { add(transaction) }
        prefs.edit().putString(KEY_TRANSACTIONS, Gson().toJson(list)).apply()
    }

    // Get all transactions
    fun getTransactions(context: Context): List<Transaction> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_TRANSACTIONS, "[]")
        val type = object : TypeToken<List<Transaction>>() {}.type
        return Gson().fromJson(json, type)
    }

    // Overwrite the entire transaction list
    fun saveAllTransactions(context: Context, transactions: List<Transaction>) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_TRANSACTIONS, Gson().toJson(transactions)).apply()
    }

    // Clear transactions
    fun clearAllTransactions(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_TRANSACTIONS).apply()
    }

    // Update one transaction by ID
    fun updateTransaction(context: Context, updated: Transaction) {
        val list = getTransactions(context).toMutableList()
        val idx = list.indexOfFirst { it.id == updated.id }
        if (idx != -1) list[idx] = updated
        saveAllTransactions(context, list)
    }

    // Delete one transaction by ID
    fun deleteTransaction(context: Context, toDelete: Transaction) {
        val list = getTransactions(context).toMutableList()
        list.removeIf { it.id == toDelete.id }
        saveAllTransactions(context, list)
    }

    // Budget
    fun saveBudget(context: Context, budget: Double) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putFloat(KEY_BUDGET, budget.toFloat()).apply()
    }
    fun getBudget(context: Context): Double {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getFloat(KEY_BUDGET, 0f).toDouble()
    }

    // Income / Expenses helpers
    fun getIncome(context: Context): Double =
        getTransactions(context).filter { it.category.equals("Income", true) }
            .sumOf { it.amount }

    fun getExpenses(context: Context): Double =
        getTransactions(context).filter { !it.category.equals("Income", true) }
            .sumOf { it.amount }

    // Currency
    fun saveCurrency(context: Context, currency: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_CURRENCY, currency).apply()
    }
    fun getCurrency(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_CURRENCY, "₨") ?: "₨"  // default ₨
    }
}
