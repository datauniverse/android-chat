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
import com.ab.android.app.adapters.CustomListViewAdapter

/**
 * Created by abhil on 31-03-2018.
 */
class ContactsFragment : Fragment() {

    var listView: ListView? = null
    var customListViewAdapter: CustomListViewAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_contacts, container, false)

        val fullnames = arrayOf(
                "Sachin Tendulkar",
                "Sourav Ganguly",
                "Rahul Dravid",
                "VVS Laxman",
                "Anil Kumble"
        )

        val contactIds = arrayOf(
                "9845065234",
                "8743612984",
                "3487109347",
                "2849372649",
                "9823645983"
        )

        val contactList = ArrayList<HashMap<String, String>>();

        for (i in 0..4) {
            val contact = HashMap<String, String>()

            contact.put("fullname", fullnames[i])
            contact.put("contactId", contactIds[i])

            contactList.add(contact)
        }

        listView = rootView.findViewById<ListView>(R.id.contactsListView)
        customListViewAdapter = CustomListViewAdapter(this.context, contactList)
        listView?.adapter = customListViewAdapter

        return rootView
    }
}