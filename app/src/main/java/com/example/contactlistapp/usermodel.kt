package com.example.contactlistapp

import com.google.firebase.Timestamp
import java.util.Date

data class usermodel(
    val nametext: String? =null,
    val numbertext: String? = null,
    val completed: Boolean = false,
    val timestamp: Timestamp,
    val userid: String? =null,
    var firestoreId: String
){
    // No-argument constructor
    constructor() : this("","", false, Timestamp(Date()), "", "")
}
