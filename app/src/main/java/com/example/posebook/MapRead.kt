package com.example.posebook

import android.util.Log
import com.example.posebook.manager.LocationManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

//fun MapsFragment.readSimple(){
//    var getdata = object : ValueEventListener{
//        override fun onDataChange(snapshot: DataSnapshot) {
//            var sb = StringBuilder()
//            for(i in snapshot.children){
//                var cName = i.child("cityLocation").child("cityName").getValue()
//                sb.append("${i.key} $cName")
//            }
//            Log.d("MapsFragment", "cName.value.toString()")
//
//        }
//
//        override fun onCancelled(error: DatabaseError) {
//
//        }
//    }
//}
//
//fun MapsFragment.testFun(){
//    Log.d("MapsFragment", "masdasdastest")
//}