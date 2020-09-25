package com.example.clientsideapp

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView

const val MSG_GET_MULTIPLICATION = 1

class MainActivity : AppCompatActivity() {
    val TAG="ClientMainActivity"
    lateinit var btnGetResult:Button
    lateinit var etNumber1: AppCompatEditText
    lateinit var etNumber2:AppCompatEditText
    lateinit var tvResult: AppCompatTextView
    lateinit var mContext: Context
    private lateinit var serviceIntent:Intent

    //messenger for service
    lateinit var requestMessenger: Messenger
    //messenger to fetch reply
    lateinit var receivedMessenger:Messenger

    var mBound:Boolean=false

    //invoked while connecting with service
    private val serviceConnection=object: ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, iBinder: IBinder?) {
            Log.i(TAG,"Service connected")
            requestMessenger= Messenger(iBinder)
            receivedMessenger= Messenger(ReceivedMessageHandler())
            mBound=true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.i(TAG,"Service not connected! \nOpen remote app..")
            mBound=false
            Toast.makeText(mContext,"Unable to bind the service",Toast.LENGTH_SHORT).show()
        }
    }

    //handler for reply messenger
    inner class ReceivedMessageHandler: Handler(){
        override fun handleMessage(msg: Message) {
            Log.i(TAG,"Message received at activity")
            when(msg.what){
                MSG_GET_MULTIPLICATION->{
                    tvResult.visibility=View.VISIBLE

                    val dataBundle=msg.data
                    val msg = dataBundle.getString("msg")
                    tvResult.text="Result Received: $msg"
                    Log.i(TAG,"$msg")

                    //text view gone after 20 sec
                    Handler().postDelayed({
                        tvResult.visibility=View.GONE
                        }, 20000)
                }
                else->
                    super.handleMessage(msg)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //bind view in xml file
        bindView()

        //on click listener for all views
        setListener()
    }

    override fun onStart() {
        super.onStart()
        //bound service
        serviceIntent= Intent()
        //remote app package
        serviceIntent.component = ComponentName("com.example.serversideapp","com.example.serversideapp.RemoteService")
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        //unbind service
        if(mBound){
            unbindService(serviceConnection)
            mBound=false
        }
    }

    private fun bindView(){
        btnGetResult=findViewById(R.id.btnGetResult)
        etNumber1=findViewById(R.id.etNumber1)
        etNumber2=findViewById(R.id.etNumber2)
        tvResult=findViewById(R.id.tvResult)

        mContext=this
    }

    private fun setListener(){
        btnGetResult.setOnClickListener{
            var num1=etNumber1.text.toString()
            var num2=etNumber2.text.toString()

            if(mBound){
                //check input nu,ber are not empty
                when {
                    num1.isEmpty() -> {
                        etNumber1.error = "Num1 empty"
                        etNumber1.requestFocus()
                    }
                    num2.isEmpty() -> {
                        etNumber2.error="Num1 empty"
                        etNumber2.requestFocus()
                    }
                    else -> {
                        //send input to service using messenger
                        getMultiplicationResult(num1.toInt(),num2.toInt())
                    }
                }
            }
            else{
                Toast.makeText(mContext,"Service not bound",Toast.LENGTH_SHORT).show()
            }

        }

    }

    private fun getMultiplicationResult(num1:Int, num2:Int){
        if(mBound){
            val msg = Message.obtain(null, MSG_GET_MULTIPLICATION, 0, 0)
            val bundle= Bundle()
            bundle.putInt("num1",num1)
            bundle.putInt("num2",num2)
            msg.data=bundle
            msg.replyTo=receivedMessenger
            try {
                //send message to service
                requestMessenger.send(msg)
                Toast.makeText(mContext,"Message sent! \nCheck remote app..",Toast.LENGTH_SHORT).show()
                Log.i(TAG,"Message sent")

            } catch (e: Exception) {
                Log.i(TAG,"Error ${e.message}")
            }

        }
    }
}