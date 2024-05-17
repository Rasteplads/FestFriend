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
        val friends = HashMap<UByte, UserData>()
        friends[0u] = UserData(id = 0u, pos = Position(10.0f, 5.0f), username = "Alice")
        friends[1u] = UserData(id = 1u, pos = Position(12.5f, 7.8f), username = "Bob")
        friends[2u] = UserData(id = 2u, pos = Position(9.2f, 4.1f), username = "Charlie")

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
        val friends2 = HashMap<UByte, UserData>()

        val appState = AppState(
            friends = friends2
        )

        var counter2 = 0

        rule.setContent {
            MapPage(appState = appState, onMarkerMade = {counter2++})
        }

        assertEquals(counter2, 0)
    }
}