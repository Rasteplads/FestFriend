package com.rasteplads.festfriend

data class AppState (
    val groupID: String = "",
    val password: String = "",

    val username: String = "",
    val position: Position = Position(0f, 0f),

    val friends: Friends = Friends(),

    val error: String = "",
    val isError: Boolean = false
){
    fun getFrom(
        groupID: String = this.groupID,
        password: String = this.password,
        username: String = this.username,
        position: Position = this.position,
        friends: Friends = this.friends,
        error: String = this.error,
        isError: Boolean = this.isError

    ): AppState{
        return AppState(groupID, password, username, position, friends, error, isError)
    }
}