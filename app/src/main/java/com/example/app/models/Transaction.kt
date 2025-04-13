package com.example.app.models

data class Transaction(
    val id: Long = System.currentTimeMillis(), // Unique ID for each transaction
    var title: String,
    var amount: Double,
    var category: String,
    var date: String
)
