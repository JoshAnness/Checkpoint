package com.example.checkpoint

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.checkpoint.dao.ApiUtils
import com.example.checkpoint.dao.IWeather
import com.example.checkpoint.dto.WeatherAPI
import com.example.checkpoint.extension.currentFraction
import com.example.checkpoint.extension.noRippleClickable
import com.example.checkpoint.ui.theme.CheckpointTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.tasks.Task
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.ResourceOptionsManager
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.ref.WeakReference


class MainActivity : AppCompatActivity() {

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var isLocationPermissionGranted = false
    private lateinit var mapView: MapView
    private lateinit var IWeatherMain: IWeather
    private lateinit var locationPermissionHelper: LocationPermissionHelper
    private var IWeatherResponse: String by mutableStateOf("")
    private var IWeatherResponseSmall: String by mutableStateOf("")
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    //private val fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var cancellationTokenSource = CancellationTokenSource()
    private var lat: Double = 0.0 //by mutableStateOf("")
    private var lon: Double = 0.0 //by mutableStateOf("")
    private var LOCATION_REFRESH_TIME: Int = 15000
    private var LOCATION_REFRESH_DISTANCE = 40233 //25 miles
    //private var locationManager : LocationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ResourceOptionsManager.getDefault(
            this,
            defaultToken = getString(R.string.mapbox_access_token)
        )

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)

        mapView = MapView(this)
        IWeatherMain = ApiUtils.apiService
        locationPermissionHelper = LocationPermissionHelper(WeakReference(this))
        locationPermissionHelper.checkPermissions {
            onMapReady()
        }
        /*btnGetWeather.setOnClickListener{
            getLocation()
        }*/

        setContent {
            CheckpointTheme {
                Surface(color = MaterialTheme.colors.background) {

                }
                CheckpointHome(mapView, IWeatherResponseSmall, IWeatherResponse)
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onStart() {
        super.onStart()
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLastLocation();
        } else {
            requestPermission();
        }

        var latitude = lat.toBigDecimal().toPlainString()
        var longitude = lon.toBigDecimal().toPlainString()
        lon.toString()
        val apiKey = "69702e05c2554c21cf44563eb81ea624"
        val units = "imperial"

        IWeatherMain.getAllWeather(latitude, longitude, apiKey, units).enqueue(object : Callback<WeatherAPI> {
            override fun onResponse(call: Call<WeatherAPI>, response: Response<WeatherAPI>) {
                if (response.code() == 200) {
                    buildResponse(response.body())
                }
            }
            override fun onFailure(call: Call<WeatherAPI>, t: Throwable) {
            }
        })
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedLocationClient.lastLocation.addOnCompleteListener { res ->
            var res = res.result
            if(res == null) {
                currentLocation()
            } else {
                lat = res.latitude
                lon = res.longitude
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun currentLocation() {
        val currentLocationTask: Task<Location> = fusedLocationClient.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        )
        currentLocationTask.addOnSuccessListener { location ->
            location?.let {
                lat = location.latitude
                lon = location.longitude
            }
        }
    }

    private val onIndicatorBearingChangedListener = OnIndicatorBearingChangedListener {
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().bearing(it).build())
    }

    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().center(it).build())
        mapView.gestures.focalPoint = mapView.getMapboxMap().pixelForCoordinate(it)
    }
    private val onMoveListener = object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) {
            onCameraTrackingDismissed()
        }

        override fun onMove(detector: MoveGestureDetector): Boolean {
            return false
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {}
    }

    @Override
    private fun buildResponse(weatherResponse: WeatherAPI?) {
        val temperature = weatherResponse?.main!!.temp
        val stringBuilder = "Country: " +
                weatherResponse.sys.country +
                "\n" +
                "Temperature: " +
                weatherResponse.main.temp +
                "\n" +
                "Temperature(Min): " +
                weatherResponse.main.tempMin +
                "\n" +
                "Temperature(Max): " +
                weatherResponse.main.tempMax +
                "\n" +
                "Humidity: " +
                weatherResponse.main.humidity +
                "\n" +
                "Pressure: " +
                weatherResponse.main.pressure
        IWeatherResponseSmall = temperature.toString()
        IWeatherResponse = stringBuilder
    }

    private fun addAnnotationToMap() {
        // Create an instance of the Annotation API and get the PointAnnotationManager.
        bitmapFromDrawableRes(
            this@MainActivity,
            R.drawable.red_marker
        )?.let {
            val annotationApi = mapView.annotations
            val pointAnnotationManager = annotationApi.createPointAnnotationManager()
            // Set options for the resulting symbol layer.
            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                // Define a geographic coordinate.
                .withPoint(Point.fromLngLat(18.06, 59.31))
                // Specify the bitmap you assigned to the point annotation
                // The bitmap will be added to map style automatically.
                .withIconImage(it)
            // Add the resulting pointAnnotation to the map.
            pointAnnotationManager.create(pointAnnotationOptions)
        }
    }

    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
        convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))

    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
            // copying drawable object to not manipulate on the same reference
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }

    private fun onMapReady() {
        mapView.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .zoom(14.0)
                .build()
        )
        mapView.getMapboxMap().loadStyleUri(
            Style.MAPBOX_STREETS
        ) {
            initLocationComponent()
            setupGesturesListener()
        }
    }

    private fun setupGesturesListener() {
        mapView.gestures.addOnMoveListener(onMoveListener)
    }

    private fun initLocationComponent() {
        val locationComponentPlugin = mapView.location
        locationComponentPlugin.updateSettings {
            this.enabled = true
            this.locationPuck = LocationPuck2D(
                bearingImage = AppCompatResources.getDrawable(
                    this@MainActivity,
                    R.drawable.mapbox_user_puck_icon,
                ),
                shadowImage = AppCompatResources.getDrawable(
                    this@MainActivity,
                    R.drawable.mapbox_user_icon_shadow,
                ),
                scaleExpression = interpolate {
                    linear()
                    zoom()
                    stop {
                        literal(0.0)
                        literal(0.6)
                    }
                    stop {
                        literal(20.0)
                        literal(1.0)
                    }
                }.toJson()
            )
        }
        locationComponentPlugin.addOnIndicatorPositionChangedListener(
            onIndicatorPositionChangedListener
        )
        locationComponentPlugin.addOnIndicatorBearingChangedListener(
            onIndicatorBearingChangedListener
        )
    }

    private fun onCameraTrackingDismissed() {
        Toast.makeText(this, "onCameraTrackingDismissed", Toast.LENGTH_SHORT).show()
        mapView.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.location
            .removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        mapView.gestures.removeOnMoveListener(onMoveListener)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        requestPermission()
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun requestPermission() {
        isLocationPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val permissionRequest: MutableList<String> = ArrayList()

        if (!isLocationPermissionGranted) {
            permissionRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (permissionRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionRequest.toTypedArray())
        }
    }

    @Composable
    private fun MapboxMapView(
        mapView: MapView,
        IWeatherResponseSmall: String,
        IWeatherResponse: String
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                mapView
            },
            update = {
                IWeatherResponse
                IWeatherResponseSmall
            }
        )
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun CheckpointHome(mapView: MapView, IWeatherResponseSmall: String, IWeatherResponse: String) {
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

        val radius = (30 * scaffoldState.currentFraction).dp

        BottomSheetScaffold(
            modifier = Modifier
                .fillMaxSize(),
            scaffoldState = scaffoldState,
            sheetShape = RoundedCornerShape(topStart = radius, topEnd = radius),
            content = { MapboxMapView(mapView, IWeatherResponseSmall, IWeatherResponse) },
            drawerBackgroundColor = MaterialTheme.colors.surface,
            sheetContent = {
                SheetCollapsed(
                    isCollapsed = scaffoldState.bottomSheetState.isCollapsed,
                    currentFraction = scaffoldState.currentFraction,
                    onSheetClick = sheetToggle
                ) {
                    BottomSheetContentSmall(IWeatherResponseSmall)
                }
                SheetExpanded {
                    BottomSheetContentLarge(IWeatherResponse)
                }
            },
            sheetPeekHeight = 80.dp
        )
    }

    @Composable
    fun BottomSheetContentSmall(IWeatherResponseSmall: String) {
        Text(
            text = IWeatherResponseSmall,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.h6,
            color = MaterialTheme.colors.onSurface
        )
    }

    @Composable
    fun BottomSheetContentLarge(IWeatherResponse: String) {
        Text(
            text = IWeatherResponse,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.h6,
            color = MaterialTheme.colors.onSurface
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
                .fillMaxWidth()
                .background(MaterialTheme.colors.primary)
                .height(400.dp)
        ) {
            content()
        }
    }
}