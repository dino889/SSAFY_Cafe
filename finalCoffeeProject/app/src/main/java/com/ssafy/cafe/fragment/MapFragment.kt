package com.ssafy.cafe.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.ssafy.cafe.R
import com.ssafy.cafe.databinding.FragmentMapBinding

class MapFragment : Fragment() , OnMapReadyCallback{
    private lateinit var mMap:GoogleMap
    private lateinit var binding: FragmentMapBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onMapReady(gMap: GoogleMap?) {
        if (gMap != null) {
            mMap = gMap
        }


    }
}