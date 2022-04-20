package com.example.checkpoint

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
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
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.ref.WeakReference

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import com.example.checkpoint.dto.Delay
import com.example.checkpoint.dto.User
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.koin.androidx.viewmodel.ext.android.viewModel

import androidx.compose.material.OutlinedTextField
import androidx.compose.runtime.livedata.observeAsState
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    private lateinit var permmissionLauncher: ActivityResultLauncher<Array<String>>
    private var isLocationPermissionGranted = false
    private lateinit var mapView: MapView
    private lateinit var IWeatherMain: IWeather
    private lateinit var locationPermissionHelper: LocationPermissionHelper
    private var IWeatherResponse: String by mutableStateOf("")
    private var IWeatherResponseSmall: String by mutableStateOf("")
    var weatherAPI: String = "5faf2a035a52f392a0394d9a48bc16be"
    private val rviewModel: ReportViewModel by viewModel<ReportViewModel>()
    private var inDelayName: String = ""
    private var selectedDelay: Delay? = null
    var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser



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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ResourceOptionsManager.getDefault(
            this,
            defaultToken = getString(R.string.mapbox_access_token)
        )
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

    override fun onStart() {
        super.onStart()
        IWeatherMain.getAllWeather().enqueue(object : Callback<WeatherAPI> {
            override fun onResponse(call: Call<WeatherAPI>, response: Response<WeatherAPI>) {
                if (response.code() == 200) {

                    buildResponse(response.body())
                }
            }

            override fun onFailure(call: Call<WeatherAPI>, t: Throwable) {

            }

        })
    }

    @Override
    private fun buildResponse(weatherResponse: WeatherAPI?) {
        val temperature =
            weatherResponse?.sys!!.country + " temperature is currently " + weatherResponse?.main!!.temp.toString()
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
        IWeatherResponseSmall = temperature
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
            permmissionLauncher.launch(permissionRequest.toTypedArray())
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

    @Composable
    fun TextFieldWithDropdownUsage(
        dataIn: List<Delay>,
        label: String = "",
        take: Int = 3,
        selectedDelay: Delay = Delay()
    ) {

        val dropDownOptions = remember { mutableStateOf(listOf<Delay>()) }
        val textFieldValue =
            remember(selectedDelay.delayID) { mutableStateOf(TextFieldValue(selectedDelay.delayName)) }
        val dropDownExpanded = remember { mutableStateOf(false) }

        fun onDropdownDismissRequest() {
            dropDownExpanded.value = false
        }

        fun onValueChanged(value: TextFieldValue) {
            inDelayName = value.text
            dropDownExpanded.value = true
            textFieldValue.value = value
            dropDownOptions.value = dataIn.filter {
                it.toString().startsWith(value.text) && it.toString() != value.text
            }.take(take)
        }

        TextFieldWithDropdown(
            modifier = Modifier.fillMaxWidth(),
            value = textFieldValue.value,
            setValue = ::onValueChanged,
            onDismissRequest = ::onDropdownDismissRequest,
            dropDownExpanded = dropDownExpanded.value,
            list = dropDownOptions.value,
            label = label
        )
    }

    @Composable
    fun TextFieldWithDropdown(
        modifier: Modifier = Modifier,
        value: TextFieldValue,
        setValue: (TextFieldValue) -> Unit,
        onDismissRequest: () -> Unit,
        dropDownExpanded: Boolean,
        list: List<Delay>,
        label: String = ""
    ) {
        Box(modifier) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        if (!focusState.isFocused)
                            onDismissRequest()
                    },
                value = value,
                onValueChange = setValue,
                label = { Text(label) },
                colors = TextFieldDefaults.outlinedTextFieldColors()
            )
            DropdownMenu(
                expanded = dropDownExpanded,
                properties = PopupProperties(
                    focusable = false,
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                ),
                onDismissRequest = onDismissRequest
            ) {
                list.forEach { text ->
                    DropdownMenuItem(onClick = {
                        setValue(
                            TextFieldValue(
                                text.toString(),
                                TextRange(text.toString().length)
                            )
                        )
                        selectedDelay = text

                    }) {
                        Text(text = text.toString())
                    }
                }
            }
        }
    }

    @Composable
    fun DelaySpinner(delays: List<Delay>) {
        var expanded by remember { mutableStateOf(false) }
        var delayText by remember { mutableStateOf("Delay Collection") }
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Row(Modifier
                .padding(24.dp)
                .clickable {
                    expanded = !expanded
                }
                .padding(8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = delayText,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "")
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    delays.forEach { delay ->
                        DropdownMenuItem(onClick = {
                            expanded = false
                            if (delay.delayName == rviewModel.NEW_DELAY) {
                                // create a new specimen object
                                delayText = ""
                                delay.delayName = ""
                            } else {
                                // we have selected an existing specimen.
                                delayText = delay.toString()
                                selectedDelay = Delay(
                                    delayName = "",
                                    reportID = 0,
                                    latitude = "",
                                    longitude = ""
                                )
                                inDelayName = delay.delayName
                            }

                            rviewModel.selectedDelay = delay

                        }) {
                            Text(text = delay.toString())
                        }

                    }
                }
            }
        }
    }


    @Composable
    private fun signIn() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        val signinIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()

        signInLauncher.launch(signinIntent)
    }

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.signInResult(res)
    }


    private fun signInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == ComponentActivity.RESULT_OK) {
            firebaseUser = FirebaseAuth.getInstance().currentUser
            firebaseUser?.let {
                val user = User(it.uid, it.displayName)
                rviewModel.user = user
                rviewModel.saveUser()
                rviewModel.listenToDelay()
            }
        } else {
            Log.e("MainActivity.kt", "Error logging in " + response?.error?.errorCode)

        }
    }


                @OptIn(ExperimentalMaterialApi::class)
                @Composable
                fun CheckpointHome(
                    mapView: MapView,
                    IWeatherResponseSmall: String,
                    IWeatherResponse: String
                ) {
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
                        content = {
                            MapboxMapView(
                                mapView,
                                IWeatherResponseSmall,
                                IWeatherResponse
                            )
                        },
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

