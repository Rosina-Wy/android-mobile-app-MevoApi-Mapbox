package com.example.mapexercise


import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.mapexercise.networking.NetworkChecker
import com.example.mapexercise.networking.RemoteApi
import com.mapbox.bindgen.Value
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.circleLayer
import com.mapbox.maps.extension.style.utils.toValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val remoteApiClass = RemoteApi()

class MainActivity : ComponentActivity() {

    private val networkChecker by lazy {
        NetworkChecker(getSystemService(ConnectivityManager::class.java))
    }

    private var featCol: FeatureCollection? = null
    private val featColLiveData = MutableLiveData<FeatureCollection?>()
    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create a map programmatically and set the initial camera
        mapView = MapView(this)

        // Observe changes to featColLiveData
        featColLiveData.observe(this) { featCol ->
            featCol?.let {
                // FeatureCollection is populated, proceed with adding GeoJsonSource
                Log.d("feat Col main?", featCol.toString())
                val style = mapView.mapboxMap.style
                if (style != null) {
                    addGeoJsonSource(style, featCol)
                }
            }
        }

        lifecycleScope.launch {
            // Fetch car locations data
            remoteApiClass.getCarLocations()

            while (remoteApiClass.featCollection == null
            ) {
                // Wait until featCollection is populated
                delay(1000)
            }

            // featCol is now populated
            featColLiveData.value = remoteApiClass.featCollection
            //}
            Log.d("feat Coll main?", featCol.toString())
            // Wait until the feature collection is retrieved

            if(featCol != null) {
                Log.d("featCol main populated?", featCol.toString())
            }

        }

        mapView.mapboxMap.apply {
            setCamera(
                CameraOptions.Builder()
                    .center(Point.fromLngLat(174.7787, -41.2924))
                    .pitch(0.0)
                    .zoom(13.0)
                    .bearing(0.0)
                    .build()
            )
            loadStyle(Style.STANDARD) {
            }
        }
        
        // Add the map view to the activity (you can also add it to other views as a child)
        setContentView(mapView)
    }

    private fun addGeoJsonSource(style: Style, featureCol: FeatureCollection) {
        val sourceParams = HashMap<String, Value>()
        sourceParams["type"] = Value("geojson")


        val featColString = featureCol.toString()
        Log.d("feat Coll contents pre hasMap", featColString)

        sourceParams["data"] = featureCol.toValue() // Assign featureCol to the "data" key

        Log.d("hashMap", sourceParams.toString())

        val expected = style.addStyleSource("source", Value(sourceParams))

        if (expected.isError) {
            throw RuntimeException("Invalid GeoJson:" + expected.error)
        }

        style.addLayer(
            circleLayer("circle", "source") {
                circleColor(Color.BLACK)
                circleRadius(7.0)
            }
        )
        Log.d("layer status", "added?")
    }

}

