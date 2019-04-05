package com.etech.kidsstuffdemo.databaseHelper

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import org.jetbrains.annotations.NotNull

@Dao
interface DeleteProductDao {
    @Insert
    fun addproduct(deleteProductEntity: DeleteProductEntity)

    @Delete
    fun removeProduct(deleteProductEntity: DeleteProductEntity)

    @Query("DELETE FROM DeleteProductEntity")
    fun deleteAll()

    @Query("SELECT * FROM DeleteProductEntity")
    fun getAll():List<DeleteProductEntity>

    @Query("DELETE FROM DeleteProductEntity where id = :id")
    fun removeProduct(id:String)
}