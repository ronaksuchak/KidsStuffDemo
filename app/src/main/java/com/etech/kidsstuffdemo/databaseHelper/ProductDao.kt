package com.etech.kidsstuffdemo.databaseHelper

import androidx.room.*
import org.jetbrains.annotations.NotNull


@Dao
 interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addProduct(product: MutableList<ProductEntity>)

    @Query("DELETE FROM ProductEntity where ProductId = :id")
    fun removeProduct(id:String)

    @Query("DELETE FROM ProductEntity")
    fun deleteAllProduct()

   @Query("SELECT * FROM ProductEntity")
   fun getAllProducts():List<ProductEntity>

//    @Query("DELETE FROM ProductEntity WHERE ProductId=id")
//    fun deleteById(@NotNull id:String)

}