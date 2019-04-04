package com.etech.kidsstuffdemo.databaseHelper

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DeleteProductEntity (
    @PrimaryKey(autoGenerate = true)
    var _id:Int,
    var id:String

)

//error: Type of the parameter must be a class annotated with @Entity or a collection/array of it.
//    com.etech.kidsstuffdemo.databaseHelper.DeleteProductDao deleteProductDao);
//                                                            ^