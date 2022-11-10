package com.boosters.promise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.UiThread
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val database = Firebase.database("https://promise-c3e7d-default-rtdb.firebaseio.com/")
        val myRef = database.getReference("message")
        //myRef.setValue("hello!!")
        //myRef.push().setValue("안녕")
        //myRef.push().setValue("하세요")

        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }
        mapFragment.getMapAsync(this)
    }
    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        val coord = LatLng(37.5154133, 126.9071288)
        val locationOverlay = naverMap.locationOverlay
        locationOverlay.isVisible = true
        locationOverlay.position = coord
        val cameraUpdate = CameraUpdate.scrollTo(coord)
        naverMap.moveCamera(cameraUpdate)
        val marker = Marker()
        marker.position = coord
        marker.map = naverMap
    }
}