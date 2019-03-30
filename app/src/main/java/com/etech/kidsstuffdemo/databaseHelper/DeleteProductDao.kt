package com.etech.kidsstuffdemo.databaseHelper

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DeleteProductDao {
    @Insert
    fun addproduct(deleteProductDao: DeleteProductDao)
    @Delete
    fun removeProduct(deleteProductDao: DeleteProductDao)
    @Query("DELETE FROM DeleteProductEntity")
    fun deleteAll()
}