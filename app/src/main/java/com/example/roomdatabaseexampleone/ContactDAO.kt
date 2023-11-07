package com.example.roomdatabaseexampleone

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


@Dao
interface ContactDAO {

    @Insert
    suspend fun insert(contact: Contact) //here 'contact' is object of "Contact' class

    @Update
    suspend fun update(contact: Contact)

    @Delete
    suspend fun delete(contact: Contact)

    @Query("SELECT * FROM contact")
    fun getContact() : LiveData<List<Contact>>
}