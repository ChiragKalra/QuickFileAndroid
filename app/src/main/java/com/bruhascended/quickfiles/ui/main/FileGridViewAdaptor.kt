package com.bruhascended.quickfiles.ui.main

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bruhascended.quickfiles.R
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage


class FileGridViewAdaptor (context: Context, data: ArrayList<File>, set: MutableSet<Long>) : BaseAdapter() {

    private val mContext: Context = context
    private val files: ArrayList<File> = data
    private val publicFiles: MutableSet<Long> = set

    override fun getCount(): Int {
        return files.count()
    }

    override fun getItem(position: Int): Any {
        return files[position]
    }

    override fun getItemId(position: Int): Long {
        return files[position].id
    }

    @SuppressLint("ViewHolder", "SetTextI18n")
    override fun getView(
        position: Int, convertView: View?, parent: ViewGroup
    ): View {
        val database = FirebaseDatabase.getInstance()

        val layoutInflater = LayoutInflater.from(mContext)
        val root = layoutInflater.inflate(R.layout.item_file, parent, false)

        val rootLayout : LinearLayout = root.findViewById(R.id.rootLinearLayout)
        val switch: Switch = root.findViewById(R.id.publicSwitch)
        val textView: TextView = root.findViewById(R.id.fileNameText)
        val imageButton: ImageButton = root.findViewById(R.id.deleteButton)

        if (files[position].deleted) {
            rootLayout.visibility = LinearLayout.GONE
        }

        textView.text = files[position].name + '.' + files[position].ext

        switch.isChecked = files[position].public
        switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                publicFiles.add(files[position].id)
            } else {
                publicFiles.remove(files[position].id)
            }

            database.getReference("public").setValue(publicFiles.toList())
        }

        imageButton.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(mContext)
            builder.setMessage("Permanently Delete this File?")
            builder.setCancelable(true)

            builder.setPositiveButton(
                "Yes"
            ) { dialog, _ ->
                files[position].deleted = true
                database.getReference("files/" + files[position].id).removeValue()

                val storage: FirebaseStorage = FirebaseStorage.getInstance()
                storage.getReferenceFromUrl(files[position].link).delete()

                publicFiles.remove(files[position].id)
                database.getReference("public").setValue(publicFiles.toList())

                rootLayout.visibility = LinearLayout.GONE

                dialog.cancel()
            }

            builder.setNegativeButton(
                "Cancel"
            ) { dialog, _ -> dialog.cancel() }

            val alert: AlertDialog = builder.create()
            alert.show()

        }

        return root
    }
}