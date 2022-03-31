package com.example.checkpoint

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.checkpoint.extension.currentFraction
import com.example.checkpoint.extension.noRippleClickable
import com.example.checkpoint.ui.theme.CheckpointTheme
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch

class MainActivity :AppCompatActivity(), OnMapReadyCallback{

    private lateinit var mMap: GoogleMap

    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_maps)
        setContent {
            CheckpointTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    CheckpointBottomSheet()
                }
            }
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions()
            .position(sydney)
            .title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CheckpointBottomSheet(){
    val scope = rememberCoroutineScope()

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(BottomSheetValue.Collapsed)
    )

    val sheetToggle: () -> Unit = {
        scope.launch {
            if (scaffoldState.bottomSheetState.isCollapsed) {
                scaffoldState.bottomSheetState.expand()
            } else {
                scaffoldState.bottomSheetState.collapse()
            }
        }
    }

    val title = "Checkpoint"

    val radius = (30 * scaffoldState.currentFraction).dp

    BottomSheetScaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        sheetShape = RoundedCornerShape(topStart = radius, topEnd = radius),
        topBar = { TopBar() },
        sheetContent = {
            SheetExpanded{
                BottomSheetContentLarge()
            }
            SheetCollapsed(
                isCollapsed = scaffoldState.bottomSheetState.isCollapsed,
                currentFraction = scaffoldState.currentFraction,
                onSheetClick = sheetToggle
            ) {
                BottomSheetContentSmall()
            }
        },
        sheetPeekHeight = 72.dp
    ) {
        // TODO: Add a debug screen to test the bottom sheet
    }
}

@Composable
fun BottomSheetContentSmall() {
    Text(
        text = "Bottom Sheet Content Small",
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun BottomSheetContentLarge() {
    Text(text = "Bottom Sheet Content Large",
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun SheetCollapsed(
    isCollapsed: Boolean,
    currentFraction: Float,
    onSheetClick: () -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(MaterialTheme.colors.primary)
            .graphicsLayer(alpha = 1f - currentFraction)
            .noRippleClickable(
                onClick = onSheetClick,
                enabled = isCollapsed
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}

@Composable
fun SheetExpanded(content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.primary)
    ) {
        content()
    }
}

@Composable
fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(MaterialTheme.colors.primary)
            .graphicsLayer(alpha = 0.5f)
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Checkpoint",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp)
        )
    }
}