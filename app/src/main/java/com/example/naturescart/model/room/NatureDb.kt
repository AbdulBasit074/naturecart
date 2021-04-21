package com.example.naturescart.model.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.naturescart.model.CartDetail
import com.example.naturescart.model.Category
import com.example.naturescart.model.Product
import com.example.naturescart.model.User

@Database(entities = [User::class, Product::class, Category::class], version = 2, exportSchema = false)
abstract class NatureDb : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun favouriteDao(): FavouriteDao

    companion object {
        private var databaseName: String = "NatureDb"
        private var instance: NatureDb? = null

        fun getInstance(context: Context): NatureDb {
            if (instance == null) {
                instance = Room.databaseBuilder(context, NatureDb::class.java, databaseName)
                    .allowMainThreadQueries().fallbackToDestructiveMigration().build()
            }
            return instance!!
        }
    }

}