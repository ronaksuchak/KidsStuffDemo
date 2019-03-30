package com.etech.kidsstuffdemo.databaseHelper

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AddProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    val sellerId: String,
    val accessToken: String,
    val productName: String,
    val description: String,
    val categoryId: String,
    val price: String,
    val forGender: String,
    val ageGroupId: String,

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val bitmap: ByteArray,

    val latitude: String,
    val longitude: String,
    val address: String,
    val city: String,
    val state: String,
    val country: String

)

/*
sellerId:{{userId}}
accessToken:{{access_token}}
productName:tshirt
description:fshfufj sffjs sfjiudhrf drgerg dfrgerfgv
categoryId:5c2de90aa730028d00e557d5
//subCategoryId:5c08cd22a338149714873f90
price:5500.00
forGender:female
ageGroupId:5c2dea5ca730028d00e55a48
latitude:23.02579
longitude:73.58727
address:here
city:Abad
state:Gujarat
country:India
 */