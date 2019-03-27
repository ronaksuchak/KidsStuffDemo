package com.etech.kidsstuffdemo.databaseHelper

import androidx.room.*

@Dao
interface AddProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGender(addProductEntity: AddProductEntity)

    @Update
    fun updateGender(addProductEntity: AddProductEntity)

    @Delete
    fun deleteGender(addProductEntity: AddProductEntity)

    @Query("SELECT * FROM AddProductEntity")
    fun getAllPendingUploads():List<AddProductEntity>

}