package com.aulia.idn.ojolfirebase.activity

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aulia.idn.ojolfirebase.MainActivity
import com.aulia.idn.ojolfirebase.R
import com.aulia.idn.ojolfirebase.model.Booking
import com.aulia.idn.ojolfirebase.model.Driver
import com.aulia.idn.ojolfirebase.utils.Constant
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_waiting_driver.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity

class WaitingDriverActivity : AppCompatActivity(), OnMapReadyCallback {

    var database : FirebaseDatabase? = null
    var key: String? = null
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiting_driver)

        key = intent.getStringExtra(Constant.key)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map)
        as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        btn_home_home.onClick {
            startActivity<MainActivity>()
        }
        database = FirebaseDatabase.getInstance()
        val myRef = database?.getReference(Constant.tb_booking)

        myRef?.child(key?: "")
            ?.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshots: DataSnapshot) {
                    val booking = snapshots.getValue(Booking::class.java)
                    showData(booking)
                }
            })
    }

    //mengambil koordinat driver
    @SuppressLint("SetTextI18n")
    private fun showData(booking: Booking?) {
        tv_home_awal.text = booking?.lokasiAwal
        tv_home_tujuan.text = booking?.lokasiTujuan
        tv_home_price.text = booking?.harga +
                "(" + booking?.jarak + ")"

        val myRef = database?.getReference("Driver")
        var query = myRef?.orderByChild("uid")
            ?.equalTo(booking?.driver)
        query?.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (issue in snapshot.children){
                    val driver = Driver()
                    val d = issue.getValue(Driver::class.java)

                    driver.latitude = d?.latitude
                    driver.longtitude = d?.longtitude
                    tv_home_name_driver.text = d?.name
                    showMaker(driver, d?.name.toString())
                }
            }

        })
    }

    private fun showMaker(driver: Driver, name: String) {
        val res = this.resources
        val marker1 = BitmapFactory.decodeResource(res, R.mipmap.ic_transjek)
        val smallmarker = Bitmap.createScaledBitmap(marker1,
        200, 200, false)

        val sydney = driver.latitude?.toDouble()?.let {
            driver.longtitude?.toDouble()?.let {
                it1 -> LatLng(it, it1)
            }
        }
        mMap.addMarker(sydney?.let { MarkerOptions()
            .position(it)
            .title(name)
            .icon(BitmapDescriptorFactory.fromBitmap(smallmarker))})
        mMap.moveCamera(CameraUpdateFactory
            .newLatLng(sydney))
        mMap.animateCamera(CameraUpdateFactory
            .newLatLngZoom(sydney, 16f))
    }

}