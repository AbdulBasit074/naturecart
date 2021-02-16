package com.example.naturescart.services

interface Results {

    fun onSuccess(requestCode:Int,data:String)
    fun onFailure(requestCode:Int,data:String)


}