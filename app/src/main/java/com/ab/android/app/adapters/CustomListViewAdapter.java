package com.ab.android.app.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.android.app.R;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomListViewAdapter extends BaseAdapter{
    private static LayoutInflater inflater = null;

    private Context mContext = null;
    private ArrayList<HashMap<String, String>> mContacts = null;

    public CustomListViewAdapter(Context context, ArrayList<HashMap<String, String>> contacts) {
        mContext = context;
        mContacts = contacts;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mContacts.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View view = convertView;

        if (convertView == null) {
            view = inflater.inflate(R.layout.list_item, null);
            TextView fullname = (TextView) view.findViewById(R.id.fullname);
            TextView contactId = (TextView) view.findViewById(R.id.contactId);
            ImageView contactPicture = (ImageView) view.findViewById(R.id.contactImage);

            HashMap<String, String> mContact = new HashMap<>();
            mContact = mContacts.get(position);

            fullname.setText(mContact.get("fullname"));
            contactId.setText(mContact.get("contactId"));
            contactPicture.setImageDrawable(mContext.getResources().getDrawable(R.drawable.common_full_open_on_phone));
        }

        return view;
    }

    @Nullable
    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }
}
