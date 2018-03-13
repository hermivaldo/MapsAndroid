package com.example.logonrm.mapsandroid

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*
import android.content.DialogInterface
import android.Manifest
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log


class MapsActivity : AppCompatActivity(), OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener{

    override fun onLocationChanged(location: Location?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProviderEnabled(provider: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProviderDisabled(provider: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.i(this.localClassName, "Erro de conexao")
    }

    override fun onConnected(p0: Bundle?) {
        checkPermission()

        val minhaLocalizacao = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiCliente)
        if (minhaLocalizacao != null) {
            adicionarMarcador(minhaLocalizacao.latitude, minhaLocalizacao.longitude, "Não sou Shakira, mas estou aqui")

        }
    }

    private fun checkPermission() {
        val permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i("", "Permissão para gravar negada")

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                val builder = AlertDialog.Builder(this)

                builder.setMessage("Necessária a permissao para GPS")
                        .setTitle("Permissao Requerida")

                builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, id ->
                    requestPermission()
                })

                val dialog = builder.create()
                dialog.show()

            } else {
                requestPermission()
            }
        }
    }

    private val REQUEST_GPS: Int = 0

    fun requestPermission() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_GPS)
    }


    override fun onConnectionSuspended(p0: Int) {
        Log.i(this.localClassName, "SUSPENSO")
    }


    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_GPS -> {
                if (grantResults.size == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.i(this.localClassName, "Permissão negada pelo usuário")
                } else {
                    Log.i(this.localClassName, "Permissao concedida pelo usuario")
                }
                return

            }
        }
    }




   @Synchronized fun callConnection (){
        mGoogleApiCliente = GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API).build()
        mGoogleApiCliente.connect()
    }

    private lateinit var mMap: GoogleMap
    private lateinit var mGoogleApiCliente: GoogleApiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        btPesquisar.setOnClickListener {
            val geocoder = Geocoder(this)
            var address : List<Address>?

            address = geocoder.getFromLocationName(etEndereco.text.toString(), 1)
            if (address.size > 0){
                val location = address[0]
                adicionarMarcador(location.latitude, location.longitude, location.locality)
            }else {
                Toast.makeText(this, "Endereço não localizado", Toast.LENGTH_SHORT).show()
            }
        }

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

    fun adicionarMarcador(latitude: Double, longitude: Double, descrition: String){
        val sydney = LatLng(latitude, longitude)
        mMap.clear()
        mMap.addMarker(MarkerOptions()
                .position(sydney)
                .title(descrition)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.calendar)))

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15F))
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        callConnection()
        // Add a marker in Sydney and move the camera

    }
}
