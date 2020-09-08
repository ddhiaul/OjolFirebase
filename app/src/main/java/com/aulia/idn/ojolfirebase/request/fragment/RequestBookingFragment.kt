package com.aulia.idn.ojolfirebase.request.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aulia.idn.ojolfirebase.R
import com.aulia.idn.ojolfirebase.model.Booking
import com.aulia.idn.ojolfirebase.utils.Constant
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RequestBookingFragment : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_item_list, container, false)
    }

    private fun exPlore(){
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference(Constant.tb_booking)
        val data = ArrayList<Booking>()
        val query = myRef.orderByChild("driver")
            .equalTo("")
        query.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (issue in snapshot.children){
                    val dataFirebase = issue.getValue(Booking::class.java)
                    val booking = Booking()
                    booking.tanggal = dataFirebase?.tanggal
                    booking.uid = dataFirebase?.uid
                    booking.lokasiAwal = dataFirebase?.lokasiAwal
                    booking.latAwal = dataFirebase?.latAwal
                    booking.lonAwal = dataFirebase?.lonAwal
                    booking.latTujuan = dataFirebase?.latTujuan
                    booking.lokasiTujuan = dataFirebase?.lokasiTujuan
                    booking.jarak = dataFirebase?.jarak
                    booking.harga = dataFirebase?.harga
                    booking.status = dataFirebase?.status

                    data.add(booking)
                    showData(data)
                }
            }

        })
    }

    private fun showData(data: ArrayList<Booking>) {

    }

}