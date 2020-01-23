package com.bruhascended.quickfiles.ui.main

public class File (var id: Long) {

    lateinit var name: String
    lateinit var ext: String
    lateinit var link: String
    var timeCreated: Long = 0

    var deleted: Boolean = false
    var public: Boolean = false
}