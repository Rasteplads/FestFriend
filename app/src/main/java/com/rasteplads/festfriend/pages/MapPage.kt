package com.rasteplads.festfriend.pages

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.ColorUtils
import androidx.core.view.doOnLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.rasteplads.festfriend.AppState
import com.rasteplads.festfriend.Friends
import com.rasteplads.festfriend.Position
import com.rasteplads.festfriend.R
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun MapPage(
    appState: AppState,
    getLocation: @Composable () -> Unit,
){
    getLocation()
    Box(modifier = Modifier.fillMaxSize()) {
        // Actual map
        MapViewComp(appState)
    }
    // Code at the top
    GroupIdDisplay(groupID = appState.groupID)
}

@Composable
fun GroupIdDisplay(groupID: String) {
    Column (horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(16.dp)) // Add space at the top

        val clipboardManager = LocalClipboardManager.current
        val copyToast = Toast.makeText(LocalContext.current, "Copied to clipboard", Toast.LENGTH_SHORT)
        Box(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primary)
                // Copy the code to clipboard when clicking
                .clickable(onClick = {
                    clipboardManager.setText(AnnotatedString(groupID))
                    copyToast.show()
                })
                .padding(16.dp) // Add some padding
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$groupID ",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Icon(imageVector = Icons.Sharp.Send, contentDescription = "Copy", tint = Color.White)
            }
        }
    }
}


@Composable
fun rememberMapViewWithLifecycle(appState: AppState): MapView {
    val friends = appState.friends
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            id = R.id.map
        }
    }


    // Changes map bounding box to friends
    var hasZoomed by remember { mutableStateOf(false) }
    LaunchedEffect(arrayOf(friends, appState.position)) {
        if (hasZoomed || !positionLoaded(appState)) return@LaunchedEffect

        zoomToFriends(appState.position, friends, mapView)
        hasZoomed = true;
    }

    // Updates markers when there are changes to friends
    LaunchedEffect(arrayOf(friends, appState.position)) {
        createMarkers(appState.username, appState.position, friends, mapView)
    }

    // Makes MapView follow the lifecycle of this composable (nej jeg ved ikke hvad det betyder)
    val lifecycleObserver = rememberMapLifecycleObserver(mapView)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
        }
    }
    Configuration.getInstance().setUserAgentValue("github-rasteplads-festFriendApp");
    mapView.setTileSource(TileSourceFactory.MAPNIK)

    return mapView
}

fun positionLoaded(appState: AppState): Boolean{
    return appState.position != Position(0f,0f)
}

fun zoomToFriends(myPosition: Position, friends: Friends, map: MapView){
    // Instantiate to default (your position)
    var latMin = myPosition.latitude
    var latMax = myPosition.latitude
    var lngMin = myPosition.longitude
    var lngMax = myPosition.longitude

    for ((_, pos) in friends){
        latMin = minOf(latMin, pos.latitude)
        latMax = maxOf(latMax, pos.latitude)
        lngMin = minOf(lngMin, pos.longitude)
        lngMax = maxOf(lngMax, pos.longitude)
    }
    // The space between the most outer friends and the bounding box
    val zoomPadding = 0.001

    map.zoomToBoundingBox(BoundingBox(latMax.toDouble() + zoomPadding,lngMax.toDouble() + zoomPadding,latMin.toDouble() - zoomPadding,lngMin.toDouble() - zoomPadding),false,1,
        1000.0,500)
}

fun createMarkers (username: String, myPosition: Position, friends: Friends, map: MapView){
    map.overlays.clear()

    createMarker(username, myPosition.latitude, myPosition.longitude, map, 0)

    var counter = 1
    for ((name, pos) in friends){
        createMarker(name, pos.latitude, pos.longitude, map, counter++)
    }
}

fun createMarker(name: String, lat: Float, long: Float, map: MapView, counter: Int) {

    val marker = Marker(map)
    marker.setPosition(GeoPoint(lat.toDouble(),long.toDouble()))
    marker.setInfoWindow(null)
    marker.textLabelBackgroundColor = getMarkerColor(counter)
    marker.textLabelFontSize = 50
    marker.setTextIcon(" $name ")

    map.overlays.add(marker);
}

fun getMarkerColor(friendNumber: Int): Int {
    // Uses Hue, Saturation and Luminosity. Cycles through hues.
    return ColorUtils.HSLToColor(floatArrayOf((friendNumber * 100f) % 360,1f,0.7f))
}

// Nødvendig boilerplate
@Composable
fun rememberMapLifecycleObserver(mapView: MapView): LifecycleEventObserver =
    remember(mapView) {
        LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> {}
            }
        }
    }

//Nødvendig boilerplate
@Composable
fun MapViewComp(
    appState: AppState,
    modifier: Modifier = Modifier,
    onLoad: ((map: MapView) -> Unit)? = null,
) {
    val mapViewState = rememberMapViewWithLifecycle(appState)

    AndroidView(
        { mapViewState },
        modifier
    ) { mapView -> onLoad?.invoke(mapView) }
}