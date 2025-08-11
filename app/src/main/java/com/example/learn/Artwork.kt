package com.example.learn

import java.io.Serializable

data class Artwork(
    val id: Int,
    val title: String,
    val imageUri: String,
    val description: String,
    val price: Double,
    val category: String,
    val availability: String,
    val customizable: String,
    val stock: Int,
    val dimension: String
):Serializable
