package com.example.roomdatabaseexampleone

import android.widget.AdapterView
import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [Contact::class], version = 2)
abstract class ContactDatabase: RoomDatabase() {

    abstract fun ContactDAO(): ContactDAO //Data Access Objects

}