package com.aulia.idn.ojolfirebase.ui.dashboard.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aulia.idn.ojolfirebase.R
import com.aulia.idn.ojolfirebase.model.Booking
import kotlinx.android.synthetic.main.history_item.view.*

class HistoryAdapter(
    private val mValues: List<Booking>
): RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
    //manggil layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.history_item, parent, false)
        return ViewHolder(view)
    }

    //menghitung jumlah item
    override fun getItemCount(): Int = mValues.size

    //mengisi view dengan data
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mAwal.text = item.lokasiAwal
        holder.mTanggal.text = item.tanggal
        holder.mTujuan.text = item.lokasiTujuan
    }
    //menginisialisasi
    class ViewHolder(mView: View)
        : RecyclerView.ViewHolder(mView) {

        var mAwal : TextView = mView.tv_awal
        var mTujuan : TextView = mView.tv_tujuan
        var mTanggal : TextView = mView.item_tanggal
    }

}