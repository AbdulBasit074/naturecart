package com.example.naturescart.model.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.naturescart.model.User
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


@Dao
interface UserDao {

    @Insert
    fun login(user: User)


    @Query("SELECT * FROM User LIMIT 1")
    fun getLoggedUser(): User

    @Query("DELETE FROM User")
    fun logOut()

}