package com.rasteplads.festfriend.api

import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.rasteplads.festfriend.utils.Constants.Companion.BASE_URL
import kotlinx.coroutines.CoroutineScope
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

object FestFriendAPIClient {

    private var retrofitProvider: () -> Retrofit = {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private var serviceProvider: (Retrofit) -> FestFriendServerAPI = { retrofit ->
        retrofit.create(FestFriendServerAPI::class.java)
    }

    private var _retrofit: Retrofit? = null
    private val retrofit: Retrofit
        get() {
            if (_retrofit == null) {
                _retrofit = retrofitProvider()
            }
            return _retrofit!!
        }



    private var _service: FestFriendServerAPI? = null
    private val service: FestFriendServerAPI
        get() {
            if (_service == null) {
                _service = serviceProvider(retrofit)
            }
            return _service!!
        }

    fun setRetrofitProvider(provider: () -> Retrofit) {
        _retrofit = null
        retrofitProvider = provider
    }
    fun setServiceProvider(provider: (Retrofit) -> FestFriendServerAPI) {
        _service = null
        serviceProvider = provider
    }

    private fun <T> request(scope: CoroutineScope, req: suspend () -> Response<T>, error: (Int, String) -> Unit, success: (T) -> Unit) {
        scope.launch {
            try{
                val res = req()
                val body = res.body()
                if (res.isSuccessful && body != null){
                    success(body)
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

    fun joinGroup(scope: CoroutineScope, groupID: UShort, username: String, password: String, error: (Int, String) -> Unit, callback: () -> Unit) {
        val req = suspend { service.joinGroup(JoinGroupRequest(groupID, username, password))}
        request(scope, req, error) {
            callback()
        }
    }
    fun createGroup(scope: CoroutineScope, password: String, error: (Int, String) -> Unit, callback: (UShort) -> Unit)  {
        val req = suspend { service.createGroup(CreateGroupRequest(password)) }
        request(scope, req, error){
            callback(it.groupID)
        }
    }
    fun getMembers(scope: CoroutineScope, groupID: UShort, password: String, error: (Int, String) -> Unit, callback: (Array<String>) -> Unit)  {
       val req = suspend { service.getMembers(GetMembersRequest(groupID, password)) }
        request(scope, req, error){
            callback(it.members)
        }
    }
}