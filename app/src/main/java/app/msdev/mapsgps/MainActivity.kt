package app.msdev.mapsgps

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    var latitude : Double = 0.00
    var longitude : Double = 0.00
    var gpsActive : Boolean = true

    var txtLatitude : TextView? = null
    var txtLongitude : TextView? = null
    var locationManager : LocationManager? = null

    var requiredPermissions : Array<String> = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    val APP_PERMISSIONS_ID = 2021

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initComponents()

        locationManager = application.getSystemService(LOCATION_SERVICE) as LocationManager
        gpsActive = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (gpsActive) {
            getCoordinates()
        } else {
            latitude = 0.00
            longitude = 0.00

            txtLatitude!!.setText(getFormatGeopoint(latitude))
            txtLongitude!!.setText(getFormatGeopoint(longitude))

            Toast.makeText(this, "Coordenadas não encontradas", Toast.LENGTH_LONG).show()
        }
    }

    private fun initComponents() {
        txtLatitude = findViewById(R.id.txtValueLatitude)
        txtLongitude = findViewById(R.id.txtValueLongitude)
    }

    private fun getCoordinates() {
        val permissionActive: Boolean = getRequestPermissionLocation()

        if (permissionActive)
            getCaptureUltimaLocationValidates()
    }

    private fun getRequestPermissionLocation(): Boolean {
        // seta as permissões necesárias para acessar o GPS
        Toast.makeText(this,"Verificando permissões...", Toast.LENGTH_LONG).show()

        var permissionDeny : MutableList<String> = ArrayList()
        var checkPermissions : Int

        for (permission in this.requiredPermissions) {
            checkPermissions = ContextCompat.checkSelfPermission(this@MainActivity , permission)

            if (checkPermissions != PackageManager.PERMISSION_GRANTED) {
                permissionDeny.add(permission)
            }
        }

       return if (!permissionDeny.isEmpty()) {
           ActivityCompat.requestPermissions(this@MainActivity, permissionDeny.toTypedArray(), APP_PERMISSIONS_ID)
           false
       } else {
           true
       }
    }

    private fun getCaptureUltimaLocationValidates() {
        // Devolve a latitude e longitude do GPS

        @SuppressLint("MissingPermission")
        var location : Location? = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)

        if (location != null) {
            latitude = location.latitude
            longitude = location.longitude
        } else {
            latitude = 0.00
            longitude = 0.00
        }

        txtLatitude!!.setText(getFormatGeopoint(latitude))
        txtLongitude!!.setText(getFormatGeopoint(longitude))

        Toast.makeText(this,"Coordenadas obtidas com sucesso", Toast.LENGTH_LONG).show()
    }

    private fun getFormatGeopoint(value: Double): String? {
        val decimalFormat = DecimalFormat("#.####")

        return decimalFormat.format(value)
    }
}