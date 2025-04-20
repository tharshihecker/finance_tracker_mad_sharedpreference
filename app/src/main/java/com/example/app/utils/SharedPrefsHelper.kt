package com.example.app.utils

import android.content.Context
import com.example.app.models.Transaction
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object SharedPrefsHelper {
    private const val PREF_NAME = "finance_prefs"
    private const val KEY_TRANSACTIONS = "transactions"
    private const val KEY_BUDGET = "budget"

    // Save a single transaction
    fun saveTransaction(context: Context, transaction: Transaction) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val list = getTransactions(context).toMutableList()
        list.add(transaction)

        val json = Gson().toJson(list)
        prefs.edit().putString(KEY_TRANSACTIONS, json).apply()
    }

    // Get the list of transactions
    fun getTransactions(context: Context): List<Transaction> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_TRANSACTIONS, "[]")
        val type = object : TypeToken<List<Transaction>>() {}.type
        return Gson().fromJson(json, type)
    }

    // Save a budget value
    fun saveBudget(context: Context, budget: Double) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putFloat(KEY_BUDGET, budget.toFloat()).apply()
    }

    // Get the budget value
    fun getBudget(context: Context): Double {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getFloat(KEY_BUDGET, 0f).toDouble()
    }

    // Update an existing transaction by its ID
    fun updateTransaction(context: Context, updatedTransaction: Transaction) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val transactions = getTransactions(context).toMutableList()

        for (i in transactions.indices) {
            if (transactions[i].id == updatedTransaction.id) {
                transactions[i] = updatedTransaction
                break
            }
        }

        val json = Gson().toJson(transactions)
        prefs.edit().putString(KEY_TRANSACTIONS, json).apply()
    }

    // Delete a transaction by its ID
    fun deleteTransaction(context: Context, transactionToDelete: Transaction) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val transactions = getTransactions(context).toMutableList()
        transactions.removeIf { it.id == transactionToDelete.id }

        val json = Gson().toJson(transactions)
        prefs.edit().putString(KEY_TRANSACTIONS, json).apply()
    }

    // Save all transactions at once
    fun saveAllTransactions(context: Context, transactions: List<Transaction>) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = Gson().toJson(transactions)
        prefs.edit().putString(KEY_TRANSACTIONS, json).apply()
    }

    // Clear all transactions
    fun clearAllTransactions(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_TRANSACTIONS).apply()
    }

    // ✅ Get total income
    fun getIncome(context: Context): Double {
        return getTransactions(context)
            .filter { it.category == "Income" }
            .sumOf { it.amount }
    }

    // ✅ Get total expenses only (new method)
    fun getExpenses(context: Context): Double {
        return getTransactions(context)
            .filter { it.category != "Income" }
            .sumOf { it.amount }
    }
}
