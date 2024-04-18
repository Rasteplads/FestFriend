package com.rasteplads.festfriend

import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import com.rasteplads.festfriend.pages.LandingPage
import org.junit.Rule
import org.junit.Assert.*
import org.junit.Test

class LandingPageTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun onButtonsClickEvokedTest(){
        var onCreateEvoked = false
        var onJoinEvoked = false
        rule.setContent {
            LandingPage(
                onCreateButtonClick = { onCreateEvoked = true },
                onJoinButtonClick = {onJoinEvoked = true}
            )
        }

        rule.onNode(hasText("Create Group")).performClick()
        rule.onNode(hasText("Join Group")).performClick()

        assertEquals(true, onCreateEvoked)
        assertEquals(true, onJoinEvoked)

    }

}