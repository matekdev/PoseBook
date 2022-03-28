package com.example.posebook

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.posebook.manager.Review
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.photo_location_viewer_review_template.*

interface OnRemoveButtonTapListener
{
    fun onRemoveButtonTapped ()
}

class MapReviewFragment(val markerData: MarkerData) :
    BottomSheetDialogFragment() {
    var testarr = arrayOf<Review>()
    var database = FirebaseDatabase.getInstance().reference

    private lateinit var caller: OnRemoveButtonTapListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is OnRemoveButtonTapListener){
            caller = context
        }

    }

    companion object {
        const val tag = "SubmitMapReviewPopupFragment"
    }

    override fun getTheme(): Int {
        return R.style.RoundedBottomSheetDialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.photo_location_viewer_review_template, container, false)
        val myButton = view.findViewById<Button>(R.id.mapUserReviewCloseButton)

        val locationName = view.findViewById<TextView>(R.id.mapReviewLocationTitle)
        locationName.text = markerData.address

        view.findViewById<Button>(R.id.mapUserReviewCloseButton).setOnClickListener {
            dismiss()
        }

        val recycleView = view.findViewById<RecyclerView>(R.id.userReviewRv)
        recycleView.layoutManager = LinearLayoutManager (activity as Context)
        recycleView.adapter = MyAdapter(arrayOf(markerData.review))

        return view
    }

    /****************************/
    class MyViewHolder(inflater: LayoutInflater,
                       parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.review_row,
            parent,false))

    internal inner class MyAdapter (private val array: Array<String>):
            RecyclerView.Adapter<MyViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return MyViewHolder(inflater, parent)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            (holder.itemView as TextView).text = array[position]
        }

        override fun getItemCount() = array.size

    }
    /****************************/
}



/****************************/
class MyAdapter(private val array : Array<String>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>(){
    class MyViewHolder (val textView: TextView) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.review_row,parent,false) as TextView
        val newViewHolder = MyViewHolder (textView)
        return newViewHolder
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.textView.text = array[position]
    }

    override fun getItemCount() = array.size
}
/****************************/