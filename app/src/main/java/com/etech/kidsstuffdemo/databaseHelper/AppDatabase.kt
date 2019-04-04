package com.etech.kidsstuffdemo.databaseHelper

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AddProductEntity::class,ProductEntity::class,DeleteProductEntity::class],version = 1,exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract fun addProductDao():AddProductDao
    abstract fun productDao():ProductDao
    abstract fun deleteProductDao():DeleteProductDao

    companion object {
        var INSTANCE: AppDatabase? = null

        fun getAppDataBase(context: Context): AppDatabase? {
            if (INSTANCE == null){
                synchronized(AppDatabase::class){
                    INSTANCE = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "myDB").build()
                }
            }
            return INSTANCE
        }

        fun destroyDataBase(){
            INSTANCE = null
        }
    }
}

//error: Type of the parameter must be a class annotated with @Entity or a collection/array of it.
//    com.etech.kidsstuffdemo.databaseHelper.DeleteProductDao deleteProductDao);
//                                                            ^