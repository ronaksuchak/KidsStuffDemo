package com.etech.kidsstuffdemo.databaseHelper

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ProductEntity (

    @PrimaryKey(autoGenerate = true)
    val id:Int,

    var _id:String,

    var productName: String,

    var price:String,

    var imageUrl:String,

    var totalViews:Int


)

/*
*  "_id": "5c9f0b79307c142106eab456",
                "productName": "fru v",
                "price": "9",
                "image": "http://192.168.1.130/kidsstuffexcapi/uploads/product_media/1553927032845_IMG1553927032677.jpg",
                "totalViews":
*  */