package com.rasteplads.festfriend

import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import com.rasteplads.festfriend.pages.MapPage
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

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

    @Test
    fun MakeMarkersWhenFriends(){
        var friends = HashMap<String, Position>()
        friends["Alice"] = Position(10.0f, 5.0f)
        friends["Bob"] = Position(12.5f, 7.8f)
        friends["Charlie"] = Position(9.2f, 4.1f)

        val appState = AppState(
            friends = friends
        )

        var counter = 0

        rule.setContent {
            MapPage(appState = appState, onMarkerMade = {counter++})
        }

        assertEquals(counter, 3)
    }

    @Test
    fun MakeNoMarkersWhenNoFriends(){
        var friends = HashMap<String, Position>()

        val appState = AppState(
            friends = friends
        )

        var counter = 0

        rule.setContent {
            MapPage(appState = appState, onMarkerMade = {counter++})
        }

        assertEquals(counter, 0)
    }
}