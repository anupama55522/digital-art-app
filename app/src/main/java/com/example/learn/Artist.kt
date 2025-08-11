package com.example.learn

data class Artist(
    val id: Int,
    val name: String,
    val email: String,
    val certPath: String,
    val verify: String,
    var isAccepted:Boolean = false
)