package com.example.roomdatabaseexampleone

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class MyAdapter(var mCtx: Context, var resources: Int, var items:List<Contact>):
    ArrayAdapter<Contact>(mCtx,resources,items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(resources, null)

        val name: TextView = view.findViewById(R.id.nameListItem)
        val phone: TextView = view.findViewById(R.id.phoneListItem)
        val id: TextView = view.findViewById(R.id.idListItem)
        val age: TextView = view.findViewById(R.id.ageListItem)
        val mItem: Contact = items[position]

        name.text = mItem.name
        phone.text = mItem.phone.toString()
        id.text = mItem.id.toString()
        age.text = mItem.age

        return view
    }
}
