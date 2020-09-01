package com.aulia.idn.ojolfirebase.ui.home

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.aulia.idn.ojolfirebase.R
import com.aulia.idn.ojolfirebase.activity.WaitingDriverActivity
import com.aulia.idn.ojolfirebase.model.Booking
import com.aulia.idn.ojolfirebase.model.ChangeFormat
import com.aulia.idn.ojolfirebase.model.ResultRoute
import com.aulia.idn.ojolfirebase.model.RoutesItem
import com.aulia.idn.ojolfirebase.network.NetworkModule
import com.aulia.idn.ojolfirebase.network.RequestNotification
import com.aulia.idn.ojolfirebase.utils.Constant
import com.aulia.idn.ojolfirebase.utils.Constant.key
import com.aulia.idn.ojolfirebase.utils.GPSTrack
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.fragment_home.*
import okhttp3.ResponseBody
import okhttp3.Route
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.lang.StringBuilder
import java.util.*
import kotlin.math.tan

class HomeFragment : Fragment(), OnMapReadyCallback {

    var map: GoogleMap? = null

    var tanggal: String? = null
    var latAwal: Double? = null
    var lonAwal: Double? = null
    var latAkhir: Double? = null
    var lonAkhir: Double? = null

    var jarak: String? = null
    var dialog: Dialog? = null

    var keyy : String? = null
    private var auth: FirebaseAuth? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(
            R.layout.fragment_home,
            container, false
        )
        auth = FirebaseAuth.getInstance()
        return view
    }

    //menampilkan maps k fragment
    override fun onMapReady(p0: GoogleMap?) {
        map = p0
        map?.uiSettings?.isMyLocationButtonEnabled = false
        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(
            LatLng(-6.3088652, 106.682188), 12f
        ))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //menginisialisasi dri mapview
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { this }

        showPermission()
        visibleView(false)
        keyy?.let { bookingHistoryUse(it) }
        tv_home_awal?.onClick {
            takeLocation(1)
        }

        tv_home_tujuan.onClick {
            takeLocation(2)
        }
        cv_home_bottom?.onClick {
            if (tv_home_awal?.text?.isNotEmpty()!!
                && tv_home_tujuan.text.isNotEmpty()){
                insertServer()
            }else{
                toast("tidak boleh kosong").show()
                view.let { Snackbar.make(it, "tidak boleh kosong",
                Snackbar.LENGTH_SHORT).show() }
            }
        }
    }

    private fun showPermission() {
        showGPS()
        if (activity?.let {
                ContextCompat.checkSelfPermission(it,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
            } != PackageManager.PERMISSION_GRANTED)

            if (activity?.let{
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        it, android.Manifest.permission.ACCESS_FINE_LOCATION
                    )
                }!!) {
                showGPS()
            }else{
                requestPermissions(
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ), 1
                )
            }
    }

    //insert data booking k realtime database
    private fun insertServer(){
        val currentTime = Calendar.getInstance().time
        tanggal = currentTime.toString()
        insertRequest(
            currentTime.toString(),
            auth?.uid.toString(),
            tv_home_awal.text.toString(),
            latAwal,
            lonAwal,
            tv_home_tujuan.text.toString(),
            latAkhir,
            lonAkhir,
            tv_home_price.text.toString(),
            jarak.toString()
        )
    }

    private fun insertRequest(
        tanggal: String,
        uid: String,
        lokasiAwal: String,
        latAwal: Double?,
        lonAwal: Double?,
        lokasiTujuan: String,
        latTujuan: Double?,
        lonTujuan: Double?,
        harga: String,
        jarak: String
    ) : Boolean{
        val booking = Booking()
        booking.tanggal = tanggal
        booking.uid = uid
        booking.lokasiAwal = lokasiAwal
        booking.latAwal = latAwal
        booking.lonAwal = lonAwal
        booking.lokasiTujuan = lokasiTujuan
        booking.latTujuan = latTujuan
        booking.lonTujuan = lonTujuan
        booking.jarak = jarak
        booking.harga = harga
        booking.status = 1
        booking.driver = ""

        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference(Constant.tb_booking)
        keyy = database.reference.push().key
        val k = keyy

        pushNotif(booking)
        k?.let { bookingHistoryUse(it) }

        myRef.child(keyy ?: "").setValue(booking)

        return true
    }

    private fun pushNotif(booking: Booking){
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Driver")
        myRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (issue in snapshot.children){
                    val token = issue.child("token")
                        .getValue(String::class.java)

                    println(token.toString())
                    val request = RequestNotification()
                    request.token = token
                    request.sendNotificationModel = booking

                    NetworkModule.getServiceFcm().sendChatNotification(request)
                        .enqueue(object: Callback<ResponseBody>{
                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                Log.d("network failed : ", t.message)
                            }

                            override fun onResponse(
                                call: Call<ResponseBody>,
                                response: Response<ResponseBody>
                            ) {
                                response.body()
                                Log.d("response server", response.message())
                            }
                        })
                }
            }

        })
    }

    //menampilkan lokasi user berdasarkan gps device
    private fun showGPS(){
        val gps = context?.let { GPSTrack(it) }

        if (gps?.canGetLocation()!!){
            latAwal = gps.latitude
            lonAwal = gps.longitude

            showMarker(latAwal ?: 0.0, lonAwal ?: 0.0, "My location")

            val name = showName(latAwal ?: 0.0, lonAwal ?: 0.0)

            tv_home_awal.text = name
        }else gps.showSettingGPS()
    }

    @SuppressLint("CheckResult")
    private fun route(){
        val origin = latAwal.toString() + "," + lonAwal.toString()
        val dest = latAkhir.toString() + "," + lonAkhir.toString()

        NetworkModule.getService()
            .actionRoute(origin, dest, Constant.API_KEY)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({t: ResultRoute? ->
                showData(t?.routes)
            },{})
    }

    //nampilin harga rute
    private fun showData(routes: List<RoutesItem?>?) {
        visibleView(true)

        if (routes != null ){
            val point = routes[0]?.overviewPolyline?.points
            jarak = routes[0]?.legs?.get(0)?.distance?.text
            val jarakValue = routes[0]?.legs?.get(0)?.distance?.value
            val waktu = routes[0]?.legs?.get(0)?.duration?.text

            tv_home_waktu_distance.text = waktu + "(" + jarak + ")"

            val pricex = jarakValue?.toDouble()?.let { Math.round(it) }

            val price = pricex?.div(1000.0)?.times(2000.0)
            val price2 = ChangeFormat.toRupiahFormat2(price.toString())

            tv_home_price.text = "Rp. " + price2
        }else{
            alert {
                message = "data"
            }.show()
        }
    }

    private fun visibleView(status: Boolean) {
        if (status){
            cv_home_bottom?.visibility = View.VISIBLE
            btn_home_next?.visibility = View.VISIBLE
        }else{
            cv_home_bottom?.visibility = View.GONE
            btn_home_next?.visibility = View.GONE
        }
    }

    //proses mengarahkan autocomplete google place
    fun takeLocation(status: Int){
        try {
            context?.applicationContext?.let { Places.initialize(
                it, Constant.API_KEY
            )}
            val fields = arrayListOf(
                Place.Field.ID, Place.Field.NAME,
                Place.Field.LAT_LNG, Place.Field.ADDRESS
            )
            val intent = context?.applicationContext?.let {
                Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN
                , fields).build(it)
            }
            startActivityForResult(intent, status)
        } catch (e: GooglePlayServicesRepairableException){

        }catch (e: GooglePlayServicesNotAvailableException){

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1){
            if (resultCode == RESULT_OK) {
                val place = data?.let { Autocomplete.getPlaceFromIntent(it) }

                latAwal = place?.latLng?.latitude
                lonAwal = place?.latLng?.longitude

                tv_home_awal.text = place?.address.toString()
                showMainMarker(
                    latAwal ?: 0.0, lonAwal ?: 0.0,
                    place?.address.toString()
                )

                Log.i("location", "place : " + place?.name)
            }else if (resultCode == AutocompleteActivity.RESULT_ERROR){
                val status = data?.let { Autocomplete.getStatusFromIntent(it) }

                Log.i("location", status?.statusMessage)
            }else if (resultCode == RESULT_CANCELED){

            }
        }else{
            if (resultCode == RESULT_OK){
                val place = data?.let { Autocomplete.getPlaceFromIntent(it) }

                latAkhir = place?.latLng?.latitude
                lonAkhir = place?.latLng?.longitude

                tv_home_tujuan.text = place?.address.toString()
                showMarker(latAkhir ?: 0.0, lonAwal ?: 0.0,
                    place?.address.toString())

                route()
                Log.i("location", "place"+place?.name)
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR){
                val status = data?.let { Autocomplete.getStatusFromIntent(it) }
                Log.i("location", status?.statusMessage)
            }else if (resultCode == RESULT_CANCELED){

            }
        }
    }

    //GEOCODER
    //utk nerjemahin dri koordinat jdi nama lokasi
    private fun showName(lat: Double, lon: Double): String? {
        var name = ""
        var geocoder = Geocoder(context, Locale.getDefault())
        try {
            val address = geocoder.getFromLocation(lat, lon, 1)

            if (address.size > 0){
                val fetchedAddress = address.get(0)
                val strAddress = StringBuilder()

                for (i in 0..fetchedAddress.maxAddressLineIndex){
                    name = strAddress.append(fetchedAddress.getAddressLine(i))
                        .append("").toString()
                }
            }
        }catch (e: Exception){

        }
        return name
    }

    //nampilin lokasi pake marker
    //marker origin
    private fun showMainMarker(lat: Double, lon: Double, msg: String){
        val res = context?.resources
        val marker1 = BitmapFactory
            .decodeResource(res, R.drawable.placeholder)
        val smallMarker = Bitmap
            .createScaledBitmap(marker1, 80, 120, false)
        val coordinate = LatLng(lat, lon)

        //buat pin baru di android
        map?.addMarker(MarkerOptions().position(coordinate)
            .title(msg).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)))
        //ngatur zoom camera
        map?.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 16f))
        //biar posisi markernya selalu ada d tengah
        map?.moveCamera(CameraUpdateFactory.newLatLng(coordinate))
    }

    //marker destination
    private fun showMarker(lat: Double, lon: Double, msg: String) {
        val coordinat = LatLng(lat, lon)

        map?.addMarker(MarkerOptions()
            .position(coordinat)
            .title(msg)
        )
        map?.animateCamera(CameraUpdateFactory
            .newLatLngZoom(coordinat, 16f))

        map?.moveCamera(CameraUpdateFactory.newLatLng(coordinat))
    }

    override fun onResume() {
        keyy?.let { bookingHistoryUse(it) }
        mapView?.onResume()
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    private fun bookingHistoryUse(it: String) {
        showDialog(true)
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference(Constant.tb_booking)

        myRef.child(key).addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val booking = snapshot.getValue(Booking::class.java)
                if (booking?.driver != ""){
                    startActivity<WaitingDriverActivity>(Constant.key to key)
                    showDialog(false)
                }
            }

        })
    }

    private fun showDialog(status: Boolean) {
        dialog = Dialog(activity!!)
        dialog?.setContentView(R.layout.dialogwaitingdriver)

        if (status){
            dialog?.show()
        }else dialog?.dismiss()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1){
            showGPS()
        }
    }
}