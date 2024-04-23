package com.rasteplads.festfriend

import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import com.rasteplads.festfriend.pages.MapPage
import org.junit.Rule
import org.junit.Test

class MapPageTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun showCorrectGroupIdTest(){
        val groupId = "1234"
        val appState = AppState(
            groupID = groupId
        )
        rule.setContent {
            MapPage(appState = appState)
        }

        rule.onNode(matcher = hasText("$groupId ")).assertExists()
    }
}