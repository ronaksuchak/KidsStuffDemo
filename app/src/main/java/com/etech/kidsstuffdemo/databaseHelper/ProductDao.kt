package com.etech.kidsstuffdemo.databaseHelper

import androidx.room.*


@Dao
 interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addProduct(product: MutableList<ProductEntity>)

    @Delete
    fun removeProduct(product: ProductEntity)

    @Query("DELETE FROM ProductEntity")
    fun deleteAllProduct()

   @Query("SELECT * FROM ProductEntity")
   fun getAllProducts():List<ProductEntity>

    @Query("DELETE FROM ProductEntity WHERE _id=_id")
    fun deleteById(_id:String)

}