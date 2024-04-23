package com.rasteplads.festfriend

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.rasteplads.festfriend.pages.CreateGroupPage
import com.rasteplads.festfriend.pages.JoinGroupPage
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class JoinGroupPageTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun showErrorsTest(){
        val appState = AppState(
            usernameError = InputError(true, "uError"),
            groupIDError = InputError(true, "gError"),
            passwordError = InputError(true, "pError"),
            genericError = InputError(true, "gcError"),
        )
        rule.setContent {
            JoinGroupPage(
                appState = appState,
                onUsernameChange = {},
                onGroupIDChange = {},
                onPasswordChange = {},
                onJoinButtonClick = {},
                onBackButtonClick = {},
                locationPermissionChecker = {c, b, g -> }
            )
        }

        val msgs = arrayOf("uError", "gError", "pError", "gcError")

        msgs.forEach {
            rule.onNode(hasText(it)).assertExists()
        }
    }

    @Test
    fun noErrorsTest(){
        val appState = AppState(
            usernameError = InputError(false, "uError"),
            groupIDError = InputError(false, "gError"),
            passwordError = InputError(false, "pError"),
            genericError = InputError(false, "gcError"),
        )
        rule.setContent {
            JoinGroupPage(
                appState = appState,
                onUsernameChange = {},
                onGroupIDChange = {},
                onPasswordChange = {},
                onJoinButtonClick = {},
                onBackButtonClick = {},
                locationPermissionChecker = {c, b, g -> }
            )
        }

        val msgs = arrayOf("uError", "gError", "pError", "gcError")

        msgs.forEach {
            rule.onNode(hasText(it)).assertDoesNotExist()
        }
    }

    @Test
    fun checkPermissionTrueOnJoinButtonClick(){
        val appState = AppState(
            usernameError = InputError(false, "uError"),
            passwordError = InputError(false, "pError"),
            genericError = InputError(false, "gcError"),
        )

        var locationCheckerRan = false
        rule.setContent {
            JoinGroupPage(
                appState = appState,
                onUsernameChange = {},
                onGroupIDChange = {},
                onPasswordChange = {},
                onJoinButtonClick = {},
                onBackButtonClick = {},
                locationPermissionChecker = @Composable { c, b, g ->
                    if (b)
                        locationCheckerRan = true
                }
            )
        }
        rule.onNodeWithText("Join Group").performClick()
        rule.waitForIdle()
        Assert.assertEquals(true, locationCheckerRan)
    }
}