package com.rasteplads.festfriend.pages

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import org.osmdroid.views.MapView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.doOnLayout
import com.rasteplads.festfriend.R
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.OverlayItem
import com.rasteplads.festfriend.Friends
import com.rasteplads.festfriend.Position
import org.osmdroid.util.BoundingBox

@Composable
fun MapPage(
    groupID: String,
    password: String,
    username: String,
    friends: Friends,
){
    Box(modifier = Modifier.fillMaxSize()) {
        // Actual map
        MapViewComp(friends)
    }
    // Code at the top
    Column (horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(16.dp)) // Add space at the top

        Box(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(16.dp)) // Apply rounded corners with 16dp radius
                .background(Color.Red) // Set background color for better visibility
                .padding(16.dp) // Add some padding
        ) {

            Text(
                text = groupID,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }

}

@Composable
fun rememberMapViewWithLifecycle(friends: Friends): MapView {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            id = R.id.map
        }
    }

    //Overlay
    if (friends.size >= 1) {
        mapView.doOnLayout {
            zoomToFriends(friends, mapView)
        }

    }

    LaunchedEffect(friends) {
        // Your function to be re-run
        Log.d("Testing", "Launcheffect")
        createMarkers(friends, mapView)
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
    Configuration.getInstance().setUserAgentValue("github-laurits-mumberg-myapp");
    mapView.setTileSource(TileSourceFactory.MAPNIK)

    return mapView
}

fun zoomToFriends(friends: Friends, map: MapView){
    var latMin = friends.values.first().latitude
    var latMax = friends.values.first().latitude
    var lngMin = friends.values.first().longitude
    var lngMax = friends.values.first().longitude


    for ((_, pos) in friends){
        latMin = minOf(latMin, pos.latitude)
        latMax = maxOf(latMax, pos.latitude)
        lngMin = minOf(lngMin, pos.longitude)
        lngMax = maxOf(lngMax, pos.longitude)
    }

    Log.d("Testing", BoundingBox(lngMax.toDouble(),latMin.toDouble(),lngMin.toDouble(),latMax.toDouble()).toString())

    map.zoomToBoundingBox(BoundingBox(latMax.toDouble(),lngMin.toDouble(),latMin.toDouble(),lngMax.toDouble()),true,1,
        5.0,500)
    Log.d("Testing", "Hello: " + map.boundingBox.toString())

}

fun createMarkers (friends: Friends, map: MapView){
    for ((name, pos) in friends){
        createMarker(name, pos.latitude, pos.longitude, map)
    }
}

fun createMarker(name: String, lat: Float, long: Float, map: MapView){

    val marker = Marker(map)
    marker.setPosition(GeoPoint(lat.toDouble(),long.toDouble()))
    marker.setInfoWindow(null)
    marker.textLabelFontSize = 50
    marker.setTextIcon(" $name ")

    map.overlays.add(marker);
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
    friends: Friends,
    modifier: Modifier = Modifier,
    onLoad: ((map: MapView) -> Unit)? = null,
) {
    val mapViewState = rememberMapViewWithLifecycle(friends)

    AndroidView(
        { mapViewState },
        modifier
    ) { mapView -> onLoad?.invoke(mapView) }
}