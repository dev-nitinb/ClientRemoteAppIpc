package com.example.serversideapp

import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager

const val MSG_GET_MULTIPLICATION = 1
class RemoteService: Service() {

    private val TAG="RemoteService"

    lateinit var mMessenger:Messenger

    //local handler
    inner class IncomingHandler: Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what){
                MSG_GET_MULTIPLICATION ->{
                    val dataBundle=msg.data
                    val num1 = dataBundle.getInt("num1")
                    val num2 = dataBundle.getInt("num2")
                    Log.i(TAG,"Message received at remote")
                    Log.i(TAG,"o/p $num1 * $num2 = ${num1*num2}")
                    changeUiMessage("$num1 * $num2 = ${num1*num2}")
                }

                else->
                    super.handleMessage(msg)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.i(TAG,"Service onBind")
        //create messenger using handler
        mMessenger= Messenger(IncomingHandler())
        return mMessenger.binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG,"Service onStartCommand")
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG,"Service onDestroy")
    }

    fun changeUiMessage(msg:String){
        //send broadcast to activity regarding result
        val intent=Intent("update_message")
        intent.putExtra("msg",msg)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
}