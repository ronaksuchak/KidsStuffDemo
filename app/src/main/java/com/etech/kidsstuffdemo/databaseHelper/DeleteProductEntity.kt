package com.etech.kidsstuffdemo.databaseHelper

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DeleteProductEntity (
    @PrimaryKey(autoGenerate = true)
    var _id:Int,
    var id:String

)