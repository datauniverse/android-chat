package com.ab.android.app

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*
import kotlinx.android.synthetic.main.fragment_chats.*

/**
 * Created by abhil on 31-03-2018.
 */
class ChatsTab : Fragment() {

    companion object {
        private val TAG = "MainActivity"
        val ANONYMOUS = "anonymous"
        val DEFAULT_MSG_LENGTH_LIMIT = 1000
        val FRIENDLY_MSG_LENGTH_KEY = "friendly_msg_length"
        val RC_SIGN_IN = 1
        val RC_PHOTO_PICKER = 2
    }

    private var mMessageListView: ListView? = null
    private var mMessageAdapter: MessageAdapter? = null
    private var mProgressBar: ProgressBar? = null
    private var mPhotoPickerButton: ImageButton? = null
    private var mMessageEditText: EditText? = null
    private var mSendButton: Button? = null

    private var mUsername: String? = null

    private var mFirebaseDatabase: FirebaseDatabase? = null
    private var mMessagesDatabaseReference: DatabaseReference? = null
    private var mChildEventListener: ChildEventListener? = null
    private var mFirebaseAuth: FirebaseAuth? = null
    private var mAuthStateListener: FirebaseAuth.AuthStateListener? = null
    private var mFirebaseStorage: FirebaseStorage? = null
    private var mChatPhotosStorageReference: StorageReference? = null
    // private FirebaseRemoteConfig mFirebaseRemoteConfig; TODO: Add Implementation of Firebase Remote Config once you figure out what's wrong

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_chats, container, false)

        mUsername = MainActivity.ANONYMOUS

        mFirebaseDatabase = FirebaseDatabase.getInstance()
        mFirebaseAuth = FirebaseAuth.getInstance()
        mMessagesDatabaseReference = mFirebaseDatabase!!.reference.child("messages")
        mFirebaseStorage = FirebaseStorage.getInstance()
        mChatPhotosStorageReference = mFirebaseStorage!!.reference.child("chat_photos")

        // Initialize references to views
        mProgressBar = rootView.findViewById(R.id.progressBar) as ProgressBar
        mMessageListView = rootView.findViewById(R.id.messageListView) as ListView
        mPhotoPickerButton = rootView.findViewById(R.id.photoPickerButton) as ImageButton
        mMessageEditText = rootView.findViewById(R.id.messageEditText) as EditText
        mSendButton = rootView.findViewById(R.id.sendButton) as Button

        // Initialize message ListView and its adapter
        val friendlyMessages = ArrayList<FriendlyMessage>()
        mMessageAdapter = MessageAdapter(this.context!!, R.layout.item_message, friendlyMessages)
        mMessageListView!!.adapter = mMessageAdapter

        // Initialize progress bar
        mProgressBar!!.visibility = ProgressBar.INVISIBLE

        // ImagePickerButton shows an image picker to upload a image for a message
        mPhotoPickerButton!!.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/jpeg"
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            startActivityForResult(Intent.createChooser(intent, "Complete action using"),
                    ChatsTab.RC_PHOTO_PICKER)
        }

        // Enable Send button when there's text to send
        mMessageEditText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence.toString().trim { it <= ' ' }.length > 0) {
                    mSendButton!!.isEnabled = true
                } else {
                    mSendButton!!.isEnabled = false
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        mMessageEditText!!.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(MainActivity.DEFAULT_MSG_LENGTH_LIMIT))

        // Send button sends a message and clears the EditText
        mSendButton!!.setOnClickListener {
            // TODO: Send messages on click
            val msgText = mMessageEditText!!.text.toString()
            val friendlyMessage = FriendlyMessage(msgText, mUsername!!, "")
            mMessagesDatabaseReference!!.push().setValue(friendlyMessage)
            // Clear input box
            mMessageEditText!!.setText("")
        }

        mAuthStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                onSignedInInitialize(user.displayName)
            } else {
                onSignedOutCleanup()
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setIsSmartLockEnabled(false)
                                .setAvailableProviders(Arrays.asList<AuthUI.IdpConfig>(
                                        AuthUI.IdpConfig.EmailBuilder().build(),
                                        AuthUI.IdpConfig.GoogleBuilder().build()
                                ))
                                .build(),
                        ChatsTab.RC_SIGN_IN)
            }
        }
        /* TODO: Add Implementation of Firebase Remote Config once you figure out what's wrong
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);

        Map<String, Object> defaultConfigMap = new HashMap<>();
        defaultConfigMap.put(FRIENDLY_MSG_LENGTH_KEY, DEFAULT_MSG_LENGTH_LIMIT);
        mFirebaseRemoteConfig.setDefaults(defaultConfigMap);
        */

        return rootView
    }

    private fun onSignedInInitialize(username: String?) {
        mUsername = username
        attachDatabaseReadListener()
    }

    private fun onSignedOutCleanup() {
        mUsername = MainActivity.ANONYMOUS
        mMessageAdapter!!.clear()
        detachDatabaseReadListener()
    }

    private fun attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                    val friendlyMessage = dataSnapshot.getValue(FriendlyMessage::class.java)
                    mMessageAdapter!!.add(friendlyMessage)
                }

                override fun onChildChanged(dataSnapshot: DataSnapshot, s: String) {}

                override fun onChildRemoved(dataSnapshot: DataSnapshot) {}

                override fun onChildMoved(dataSnapshot: DataSnapshot, s: String) {}

                override fun onCancelled(databaseError: DatabaseError) {}
            }
            mMessagesDatabaseReference!!.addChildEventListener(mChildEventListener)
        }
    }

    private fun detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mMessagesDatabaseReference!!.removeEventListener(mChildEventListener!!)
            mChildEventListener = null
        }
    }
}