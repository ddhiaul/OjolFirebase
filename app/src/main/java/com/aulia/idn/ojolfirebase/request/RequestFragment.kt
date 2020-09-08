package com.aulia.idn.ojolfirebase.request

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aulia.idn.ojolfirebase.R
import com.aulia.idn.ojolfirebase.request.fragment.ProsesBookingFragment
import com.aulia.idn.ojolfirebase.request.fragment.RequestBookingFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_request.*

class RequestFragment : Fragment() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView
        .OnNavigationItemSelectedListener {item ->
        when(item.itemId){
            R.id.request -> {
                setFragment(RequestBookingFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.handle -> {
                setFragment(ProsesBookingFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.complete -> {
                setFragment(CompleteBookingFragment())
                return@OnNavigationItemSelectedListener true
            }
        }
    }

    private fun setFragment(fragment: Fragment) {
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.pager, fragment)?.commit()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_request, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFragment(RequestBookingFragment())
        navigation2.setOnNavigationItemReselectedListener(mOnNavigationItemSelectedListener){

        }
    }
}