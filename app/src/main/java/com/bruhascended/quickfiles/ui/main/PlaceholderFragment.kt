package com.bruhascended.quickfiles.ui.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.OvershootInterpolator
import android.widget.GridView
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.bruhascended.quickfiles.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.ClassCastException


class PlaceholderFragment (context: Context) : Fragment() {

    private val mContext = context

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_main, container, false)
        val gridView: GridView = root.findViewById(R.id.filesGridView)
        val floatButton: FloatingActionButton = root.findViewById(R.id.refresh)

        floatButton.setOnClickListener {
            floatButton.clearAnimation()
            ViewCompat.animate(floatButton)
                .rotation(0.0F)
                .withLayer()
                .setDuration(0L)
                .setInterpolator(OvershootInterpolator(10.0F))
                .start()
            ViewCompat.animate(floatButton)
                .rotation(360.0F)
                .withLayer()
                .setDuration(5000L)
                .setInterpolator(OvershootInterpolator(10.0F))
                .start()

            val database = FirebaseDatabase.getInstance()

            database.reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    var set : MutableSet<Long> = mutableSetOf()
                    if (dataSnapshot.child("public").value != null) {
                        set = HashSet(dataSnapshot.child("public").value as ArrayList<Long>)
                    }

                    val data : ArrayList<File> = arrayListOf()

                    try {
                        if (dataSnapshot.child("files").value != null) {
                            val rawData: ArrayList<Map<String, String>> = dataSnapshot.child("files").value as ArrayList<Map<String, String>>

                            for ((key, child) in rawData.withIndex()) {
                                if (child != null) {
                                    val file = File(key.toLong())

                                    file.ext = child["ext"].toString()
                                    file.name = child["name"].toString()

                                    file.timeCreated = child["time"].toString().toLong()
                                    file.link = child["link"].toString()

                                    file.public = set.contains(file.id)

                                    data.add(file)
                                }
                            }
                        }
                    } catch (e: ClassCastException) {
                        if (dataSnapshot.child("files").value != null) {
                            val rawData: Map<String, Map<String, String>> = dataSnapshot.child("files").value as Map<String, Map<String, String>>

                            for ((key, child) in rawData) {
                                val file = File(key.toLong())

                                file.ext = child["ext"].toString()
                                file.name = child["name"].toString()

                                file.timeCreated = child["time"].toString().toLong()
                                file.link = child["link"].toString()

                                file.public = set.contains(file.id)

                                data.add(file)
                            }
                        }
                    }

                    gridView.adapter = FileGridViewAdaptor(mContext, data, set)
                    floatButton.clearAnimation()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }

        return root
    }

}