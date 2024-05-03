package com.rasteplads.festfriend


data class InputError(val isError: Boolean = false, val msg: String = "")
data class AppState (
    val groupID: String = "",
    val password: String = "",

    val userID: String = "",
    val username: String = "",
    val position: Position = Position(),

    val friends: Friends = Friends(),
    val friendTick: Int = 0,

    val usernameError: InputError = InputError(),
    val passwordError: InputError = InputError(),
    val groupIDError: InputError = InputError(),
    val genericError: InputError = InputError()
){
    fun getFrom(
        groupID: String = this.groupID,
        password: String = this.password,
        userID: String = this.userID,
        username: String = this.username,
        position: Position = this.position,
        friends: Friends = this.friends,
        friendTick: Int = this.friendTick,
        usernameError: InputError = this.usernameError,
        passwordError: InputError = this.passwordError,
        groupIDError: InputError = this.groupIDError,
        genericError: InputError = this.genericError

        ): AppState{
        return AppState(groupID, password, userID, username, position, friends, friendTick, usernameError, passwordError, groupIDError, genericError)
    }
}