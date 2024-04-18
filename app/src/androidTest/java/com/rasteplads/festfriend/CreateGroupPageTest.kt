package com.rasteplads.festfriend

import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import com.rasteplads.festfriend.pages.CreateGroupPage
import org.junit.Rule
import org.junit.Test

class CreateGroupPageTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun showErrorsTest(){
        val appState = AppState(
            usernameError = InputError(true, "uError"),
            passwordError = InputError(true, "pError"),
            genericError = InputError(true, "gcError"),
        )
        rule.setContent {
            CreateGroupPage(
                appState = appState,
                onUsernameChange = {},
                onPasswordChange = {},
                onCreateButtonClick = {},
                onBackButtonClick = {}
            )
        }

        val msgs = arrayOf("uError", "pError", "gcError")

        msgs.forEach {
            rule.onNode(hasText(it)).assertExists()
        }
    }

    @Test
    fun noErrorsTest(){
        val appState = AppState(
            usernameError = InputError(false, "uError"),
            passwordError = InputError(false, "pError"),
            genericError = InputError(false, "gcError"),
        )
        rule.setContent {
            CreateGroupPage(
                appState = appState,
                onUsernameChange = {},
                onPasswordChange = {},
                onCreateButtonClick = {},
                onBackButtonClick = {}
            )
        }

        val msgs = arrayOf("uError", "pError", "gcError")

        msgs.forEach {
            rule.onNode(hasText(it)).assertDoesNotExist()
        }
    }

}