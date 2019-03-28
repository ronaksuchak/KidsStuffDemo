package com.etech.kidsstuffdemo.models

import android.graphics.Bitmap

data class AddProductModel (
    val sellerId: String,
    val accessToken: String,
    val productName: String,
    val description: String,
    val categoryId: String,
    val price: String,
    val forGender: String,
    val ageGroupId: String,
    val file:Bitmap,
    val latitude: String,
    val longitude: String,
    val address: String,
    val city: String,
    val state: String,
    val country: String
)