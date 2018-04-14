package com.ab.android.app.fragments;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.ab.android.app.Manifest;
import com.ab.android.app.R;

import static android.support.v4.content.PermissionChecker.checkSelfPermission;

public class ConFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener {

    private static final String[] FROM_COLUMNS = {
            Contacts.DISPLAY_NAME_PRIMARY,
            Contacts.DISPLAY_NAME_PRIMARY
    };

    private static final String[] PROJECTION = {
            Contacts._ID,
            Contacts.LOOKUP_KEY,
            Contacts.DISPLAY_NAME_PRIMARY
    };

    private static final int[] TO_IDS = {
            R.id.fullname,
            R.id.contactId
    };

    private static final String SELECTION = Contacts.DISPLAY_NAME_PRIMARY + " LIKE ?";
    private String mSearchString = "a";
    private String[] mSelectionArgs = { mSearchString };

    private static final int CONTACT_ID_INDEX = 0;
    private static final int LOOKUP_KEY_INDEX = 1;

    ListView mContactList;
    long mContactId;
    String mContactKey;
    Uri mContactUri;
    private SimpleCursorAdapter mCursorAdapter;

    public ConFragment() {}

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContactList = getActivity().findViewById(R.id.contactsListView);
        mCursorAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.list_item,
                null, FROM_COLUMNS, TO_IDS,
                0);
        mContactList.setAdapter(mCursorAdapter);
        mContactList.setOnItemClickListener(this);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        mSelectionArgs[0] = "%" + mSearchString + "%";
        return new CursorLoader(
                getActivity(),
                Contacts.CONTENT_URI,
                PROJECTION,
                SELECTION,
                mSelectionArgs,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View item, int position, long rowId) {
        Cursor cursor = ((CursorAdapter) parent.getAdapter()).getCursor();
        cursor.moveToPosition(position);
        mContactId = cursor.getLong(CONTACT_ID_INDEX);
        mContactKey = cursor.getString(LOOKUP_KEY_INDEX);
        mContactUri = Contacts.getLookupUri(mContactId, mContactKey);
        Toast.makeText(getContext(), mContactKey, Toast.LENGTH_LONG).show();
    }
}
