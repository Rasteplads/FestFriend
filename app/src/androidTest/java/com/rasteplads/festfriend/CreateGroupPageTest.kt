package com.rasteplads.festfriend

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.rasteplads.festfriend.pages.CreateGroupPage
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

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
                onBackButtonClick = {},
                locationPermissionChecker = {c, b, g -> }
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
                onBackButtonClick = {},
                locationPermissionChecker = {c, b, g -> }
            )
        }

        val msgs = arrayOf("uError", "pError", "gcError")

        msgs.forEach {
            rule.onNode(hasText(it)).assertDoesNotExist()
        }
    }

    @Test
    fun checkPermissionTrueOnCreateButtonClick(){
        val appState = AppState(
            usernameError = InputError(false, "uError"),
            passwordError = InputError(false, "pError"),
            genericError = InputError(false, "gcError"),
        )

        var locationCheckerRan = false
        rule.setContent {
            CreateGroupPage(
                appState = appState,
                onUsernameChange = {},
                onPasswordChange = {},
                onCreateButtonClick = {},
                onBackButtonClick = {},
                locationPermissionChecker = @Composable { c, b, g ->
                    if (b)
                        locationCheckerRan = true
                }
            )
        }
        rule.onNodeWithText("Create Group").performClick()
        rule.waitForIdle()
        assertEquals(true, locationCheckerRan)
    }

}