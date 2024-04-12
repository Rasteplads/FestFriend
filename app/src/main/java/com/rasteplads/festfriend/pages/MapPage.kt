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

@Composable
fun MapPage(
    groupID: String,
    password: String,
    username: String,
    friends: Friends,
    onUpdateFriendsListClick: () -> Unit,
){
    Box(modifier = Modifier.fillMaxSize()) {
        // Background element
        MapView()
        // Text on top of the image
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Group ID: #$groupID",
            modifier = Modifier.padding(10.dp)
        )
        Text(
            text = "Password: $password",
            modifier = Modifier.padding(10.dp)
        )
        Text(
            text = "username: $username",
            modifier = Modifier.padding(10.dp)
        )
        Divider(Modifier.fillMaxWidth(0.8f) )
        for ((name, pos) in friends){
            Text(text = "$name: lat:${pos.latitude}, long: ${pos.longitude}")
        }
        Button(
            onClick = onUpdateFriendsListClick,
            modifier = Modifier.fillMaxWidth(0.8f)) {
            Text(text = "Update Friend List")
        }
    }

    Column (horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(16.dp)) // Add space at the top

        Box(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(16.dp)) // Apply rounded corners with 16dp radius
                .background(Color.Red) // Set background color for better visibility
                .padding(16.dp) // Add some padding
        ) {

            Text(
                text = "YOUR_CODE",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }

}

@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            id = R.id.map
        }
    }

    //Overlay

    createMarker("a", 1.0f, 2.0f, mapView)

    // Makes MapView follow the lifecycle of this composable
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

fun createMarker(name: String, lat: Float, long: Float, map: MapView){
    Log.d("Testing", "Hello")

    val marker = Marker(map)
    marker.setPosition(GeoPoint(0,0))
    marker.setInfoWindow(null)
    marker.textLabelFontSize = 50
    marker.setTextIcon("Hello")

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
fun MapView(
    modifier: Modifier = Modifier,
    onLoad: ((map: MapView) -> Unit)? = null
) {
    val mapViewState = rememberMapViewWithLifecycle()

    AndroidView(
        { mapViewState },
        modifier
    ) { mapView -> onLoad?.invoke(mapView) }
}