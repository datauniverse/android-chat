package com.ab.android.app.fragments

import android.support.v4.app.Fragment
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.ab.android.app.R

/**
 * Created by abhil on 31-03-2018.
 */
class ContactsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_contacts, container, false)

        val listView = rootView.findViewById(R.id.contactsListView) as ListView
        val listItems = arrayOf("A - a", "B - b", "C - c")
        val adapter = ArrayAdapter<String>(
                this.context,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                listItems)
        listView.adapter = adapter
        listView.onItemClickListener = AdapterView.OnItemClickListener() {
            parent: AdapterView<*>, view: View, position: Int, id: Long ->
            val clickedValue = listView.getItemAtPosition(position).toString()
            Toast.makeText(this.context, clickedValue, Toast.LENGTH_LONG).show()
        }

        return rootView
    }
}