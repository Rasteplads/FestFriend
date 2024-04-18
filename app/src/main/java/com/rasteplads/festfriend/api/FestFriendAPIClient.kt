package com.rasteplads.festfriend.api

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.rasteplads.festfriend.utils.Constants.Companion.BASE_URL
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.MainScope
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

object FestFriendAPIClient {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val service: FestFriendServerAPI by lazy {
        retrofit.create(FestFriendServerAPI::class.java)
    }

    fun joinGroup(groupID: UShort, username: String, password: String, error: (Int, String) -> Unit, callback: () -> Unit) {
        MainScope().launch {
            try {
                val res = service.joinGroup(JoinGroupRequest(groupID, username, password))
                if (res.isSuccessful) {
                    callback()
                    return@launch
                }
                error(res.code(), getMsg(res))
            } catch (e: Exception) {
                error(-1, e.message ?: "Unknown error")
            }
        }
    }
    fun createGroup(password: String, error: (Int, String) -> Unit, callback: (UShort) -> Unit)  {
        MainScope().launch {
            try {
                val res = service.createGroup(CreateGroupRequest(password))
                val groupID = res.body()?.groupID
                if (res.isSuccessful && groupID != null) {
                    callback(groupID)
                    return@launch
                }
                error(res.code(), getMsg(res))
            }catch (e: Exception){
                error(-1, e.message ?: "Unknown error")
            }
        }
    }
    fun getMembers(groupID: UShort, password: String, error: (Int, String) -> Unit, callback: (Array<String>) -> Unit)  {
        MainScope().launch {
            try {
                val res = service.getMembers(GetMembersRequest(groupID, password))
                val members = res.body()?.members
                if (res.isSuccessful && members != null) {
                    callback(members)
                    return@launch
                }
                error(res.code(), getMsg(res))
            } catch (e: Exception) {
                error(-1, e.message ?: "Unknown error")
            }
        }
    }

    private fun <T> getMsg(res: Response<T>): String{
       return try {
           JSONObject(res.errorBody()?.string() ?: "")
               .getString("detail")
       } catch (e: JSONException) {
           res.message()
       }
    }
}