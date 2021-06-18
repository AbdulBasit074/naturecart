package com.example.naturescart.services

import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


open class BaseService(private val requestCode: Int, private val callBack: Results, private val returnRaw: Boolean = false) :
    Callback<ResponseBody> {
    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        try {
            if (t.message?.contains("resolve host") == true)
                callBack.onFailure(
                    requestCode, "Something went wrong"
//                    TranslationsHelper.getInstanceWithoutContext()
//                        ?.getTranslation("internet_not_found") ?: "Something went wrong"
                )
            else
                callBack.onFailure(requestCode, "Something went wrong")
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
        try {
            if (response.isSuccessful && response.body() != null) {
                val responseBody = response.body()!!.string()
                val responseObject = JSONObject(responseBody)
                if ((responseObject["status"] as Int) == 200) {
                    if (returnRaw) {
                        callBack.onSuccess(requestCode, responseBody)
                    } else {
                        when {
                            responseObject.has("data") -> callBack.onSuccess(
                                requestCode,
                                responseObject["data"].toString()
                            )
                            responseObject.has("message") -> callBack.onSuccess(
                                requestCode,
                                responseObject["message"].toString()
                            )
                            else -> callBack.onSuccess(requestCode, "Success!")
                        }
                    }
                } else {
                    if (responseObject.has("errors")) {
                        val errors = responseObject["errors"].toString()
                        var error = ""
                        if (!errors.startsWith("{")) {
                            error = errors
                        } else {
                            val errorsObject = JSONObject(errors)
                            for (key in errorsObject.keys()) {
                                error = errorsObject.get(key.toString()).toString()
                                break
                            }
                        }
                        callBack.onFailure(requestCode, error)
                    } else if (responseObject.has("message"))
                        callBack.onFailure(requestCode, responseObject["message"].toString())
                    else
                        callBack.onFailure(requestCode, "something went wrong")
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val responseObject =
                    JSONObject(errorBody ?: "{\"errors\":\"something went wrong\"}")
                if (responseObject.has("errors")) {
                    val errors = responseObject["errors"].toString()
                    var error = ""
                    if (!errors.startsWith("{")) {
                        error = errors
                    } else {
                        val errorsObject = JSONObject(errors)
                        for (key in errorsObject.keys()) {
                            error = errorsObject.get(key.toString()).toString()
                            break
                        }
                    }
                    callBack.onFailure(requestCode, error)
                } else if (responseObject.has("message"))
                    callBack.onFailure(requestCode, responseObject["message"].toString())
                else
                    callBack.onFailure(requestCode, "something went wrong")
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            callBack.onFailure(requestCode, "something went wrong")
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}