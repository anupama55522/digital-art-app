package com.example.learn

import java.io.Serializable

data class Order(
    val pid: Int = 0,
    val cid: Int = 0,
    val artId: Int = 0,
    val artTitle: String = "",
    val address: String = "",
    val status: String = "processing",
    val customize: String? = null,
    val customStatus: String = "pending",
    val date: String = ""
) : Serializable