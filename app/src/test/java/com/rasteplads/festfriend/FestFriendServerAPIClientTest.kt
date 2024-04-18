package com.rasteplads.festfriend

import com.rasteplads.festfriend.api.CreateGroupResponse
import com.rasteplads.festfriend.api.FestFriendAPIClient
import com.rasteplads.festfriend.api.FestFriendServerAPI
import com.rasteplads.festfriend.api.GetMembersResponse
import com.rasteplads.festfriend.api.JoinGroupResponse
import io.mockk.MockKMatcherScope
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class FestFriendAPIClientTest {
    private val api: FestFriendAPIClient = FestFriendAPIClient
    private val service: FestFriendServerAPI = mockk<FestFriendServerAPI>()

    @Before
    fun setUp(){
        api.setServiceProvider { _ -> service }
    }

    @Test
    fun joinGroupSuccessTest() {
        val successfulResponse = Response.success(JoinGroupResponse("OK"))
        coEvery { service.joinGroup(any()) } returns successfulResponse

        var callbackInvoked = false
        var errorCallbackInvoked = false
        val callback: () -> Unit = { callbackInvoked = true }
        val error: (Int, String) -> Unit = { _, _ -> errorCallbackInvoked = true }

        runBlocking {
            api.joinGroup(this, 1u, "john", "weak", error, callback)
        }

        assertEquals(true, callbackInvoked)
        assertEquals(false, errorCallbackInvoked)
    }

    @Test
    fun joinGroupErrorTest(){
        val errorResponse = Response.error<JoinGroupResponse>(404, ResponseBody.create(null, "Not found"))
        coEvery { service.joinGroup(any()) } returns errorResponse

        var callbackInvoked = false
        var errorCallbackInvoked = false
        val callback: () -> Unit = { callbackInvoked = true }
        val error: (Int, String) -> Unit = { _, _ -> errorCallbackInvoked = true }

        runBlocking {
            api.joinGroup(this, 1u, "john", "weak", error, callback)
        }

        assertEquals(false, callbackInvoked)
        assertEquals(true, errorCallbackInvoked)
    }

    @Test
    fun createGroupSuccessTest(){
        val successfulResponse = Response.success(CreateGroupResponse(1u))
        coEvery { service.createGroup(any()) } returns successfulResponse

        var callbackInvoked = false
        var errorCallbackInvoked = false
        val callback: (UShort) -> Unit = { _ -> callbackInvoked = true }
        val error: (Int, String) -> Unit = { _, _ -> errorCallbackInvoked = true }

        runBlocking {
            api.createGroup(this, "weak", error, callback)
        }

        assertEquals(true, callbackInvoked)
        assertEquals(false, errorCallbackInvoked)
    }

    @Test
    fun createGroupErrorTest(){
        val errorResponse = Response.error<CreateGroupResponse>(404, ResponseBody.create(null, "Not found"))
        coEvery { service.createGroup(any()) } returns errorResponse

        var callbackInvoked = false
        var errorCallbackInvoked = false
        val callback: (UShort) -> Unit = { _ -> callbackInvoked = true }
        val error: (Int, String) -> Unit = { _, _ -> errorCallbackInvoked = true }

        runBlocking {
            api.createGroup(this, "weak", error, callback)
        }

        assertEquals(false, callbackInvoked)
        assertEquals(true, errorCallbackInvoked)
    }

    @Test
    fun getMembersSuccessTest(){
        val successfulResponse = Response.success(GetMembersResponse(arrayOf("john", "alice", "bob")))
        coEvery { service.getMembers(any()) } returns successfulResponse

        var callbackInvoked = false
        var errorCallbackInvoked = false
        val callback: (Array<String>) -> Unit = { _ -> callbackInvoked = true }
        val error: (Int, String) -> Unit = { _, _ -> errorCallbackInvoked = true }

        runBlocking {
            api.getMembers(this, 3u, "weak", error, callback)
        }

        assertEquals(true, callbackInvoked)
        assertEquals(false, errorCallbackInvoked)
    }

    @Test
    fun getMembersErrorTest(){
        val errorResponse = Response.error<GetMembersResponse>(404, ResponseBody.create(null, "Not found"))
        coEvery { service.getMembers(any()) } returns errorResponse

        var callbackInvoked = false
        var errorCallbackInvoked = false
        val callback: (Array<String>) -> Unit = { _ -> callbackInvoked = true }
        val error: (Int, String) -> Unit = { _, _ -> errorCallbackInvoked = true }

        runBlocking {
            api.getMembers(this, 3u, "weak", error, callback)
        }

        assertEquals(false, callbackInvoked)
        assertEquals(true, errorCallbackInvoked)
    }
}