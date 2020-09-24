package com.example.serversideapp

import android.content.*
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import androidx.appcompat.widget.AppCompatTextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class MainActivity : AppCompatActivity() {
    val TAG = "ServerMainActivity"
    lateinit var tvHeader: AppCompatTextView
    lateinit var mContext: Context
    lateinit var mMessageReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindView()
    }

    private fun bindView() {
        tvHeader = findViewById(R.id.tvHeader)
        mContext = this

        //broadcast receiver to fetch output result
        mMessageReceiver=object: BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                tvHeader.text = intent!!.getStringExtra("msg")
            }
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, IntentFilter("update_message"));

    }

}