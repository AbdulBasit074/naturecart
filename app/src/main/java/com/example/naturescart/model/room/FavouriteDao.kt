package com.example.naturescart.model.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.naturescart.model.Product

@Dao
interface FavouriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProduct(product: Product)

    @Query("SELECT * FROM Product")
    fun getAllProduct(): List<Product>

    @Query("SELECT * FROM product WHERE categoryName =:categoryName")
    fun getProductByCategoryName(categoryName: String): List<Product>

    @Query("SELECT * FROM product WHERE id =:productId LIMIT 1")
    fun getProduct(productId: Long): Product?

    @Query("DELETE FROM Product WHERE id=:productId")
    fun removeProduct(productId: Long)

}