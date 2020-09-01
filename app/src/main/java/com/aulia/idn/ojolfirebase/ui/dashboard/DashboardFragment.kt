package com.aulia.idn.ojolfirebase.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.aulia.idn.ojolfirebase.R
import com.aulia.idn.ojolfirebase.model.Booking
import com.aulia.idn.ojolfirebase.ui.dashboard.adapter.HistoryAdapter
import com.aulia.idn.ojolfirebase.utils.Constant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_dashboard.*
import java.lang.IllegalStateException

class DashboardFragment : Fragment() {

    private var auth : FirebaseAuth? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.fragment_dashboard,
            container, false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        auth?.uid?.let { bookingHistoryUser(it) }
    }

    //mengambil data dri firebase
    private fun bookingHistoryUser(uid:String){
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference(Constant.tb_booking)

        val data = ArrayList<Booking>()
        var query = myRef.orderByChild("uid")
            .equalTo(uid)

        query.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshots: DataSnapshot) {
                for (issue in snapshots.children){
                    val dataFirebase = issue.getValue(Booking::class.java)
                    val booking = Booking()

                    booking.tanggal = dataFirebase?.tanggal
                    booking.uid = dataFirebase?.uid
                    booking.lokasiAwal = dataFirebase?.uid
                    booking.latAwal = dataFirebase?.latAwal
                    booking.lonAwal = dataFirebase?.lonAwal
                    booking.latTujuan = dataFirebase?.latTujuan
                    booking.lonTujuan = dataFirebase?.lonTujuan
                    booking.lokasiTujuan = dataFirebase?.lokasiTujuan
                    booking.jarak = dataFirebase?.jarak
                    booking.harga = dataFirebase?.harga
                    booking.status = dataFirebase?.status

                    data.add(booking)
                    showdata(data)
                }
            }
        })
    }

    //nge set data recyclerview dri adapter
    private fun showdata(data: java.util.ArrayList<Booking>) {
        if (data != null){
            try {
                rv1.adapter = HistoryAdapter(data)
                rv1.layoutManager = LinearLayoutManager(context)
            }catch (e: IllegalStateException){

            }
        }
    }
}