package com.example.naturescart.model.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.naturescart.model.Category
import com.example.naturescart.model.Product

@Dao
interface FavouriteDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCategory(category: Category)


    @Insert
    fun insertProduct(product: Product)

    @Query("SELECT * FROM Category")
    fun getAllCategory(): List<Category>

    @Query("SELECT * FROM Product")
    fun getAllProduct(): List<Product>

    @Query("SELECT * FROM Category WHERE id=:categoryId ")
    fun getCategory(categoryId: Long): Category?


    @Query("SELECT * FROM product WHERE categoryID =:categoryId")
    fun getProductByCategory(categoryId: Long): List<Product>


    @Query("SELECT * FROM product WHERE id =:productId LIMIT 1")
    fun getProduct(productId: Long): Product?

    @Query("DELETE FROM Category WHERE id=:categoryId")
    fun removeCategory(categoryId: Long)


    @Query("DELETE FROM Product WHERE id=:productId")
    fun removeProduct(productId: Long)



}