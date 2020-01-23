package com.bruhascended.quickfiles.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bruhascended.quickfiles.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class TokenFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_token, container, false)
        val textView: TextView = root.findViewById(R.id.tokenTextView)
        val tokenButton: Button = root.findViewById(R.id.generateTokenButton)
        val tokenSwitch: Switch = root.findViewById(R.id.tokenSwitch)

        val database = FirebaseDatabase.getInstance()
        val tokenRef = database.getReference("key")
        val permissionRef = database.getReference("upload")

        tokenRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                textView.text = dataSnapshot.value.toString()
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        permissionRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                tokenSwitch.isChecked = dataSnapshot.value as Boolean
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        tokenButton.setOnClickListener {
            val token = (100000..999999).random()
            tokenRef.setValue(token)
        }

        tokenSwitch.setOnCheckedChangeListener { _, isChecked ->
           permissionRef.setValue(isChecked)
        }

        return root
    }

}
