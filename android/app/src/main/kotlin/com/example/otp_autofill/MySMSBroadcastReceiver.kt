package com.example.otp_autofill


import android.app.Activity
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel


inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
}

class MySMSBroadcastReceiver(
    private val activity: Activity,
    private val context: Context,
    private val otpRetriever: OtpRetriever,
    binaryMessenger: BinaryMessenger,
    ) :
    MethodChannel.MethodCallHandler, BroadcastReceiver() {

    private val intent = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
    private val channel = MethodChannel(binaryMessenger, "sms_listener")

    fun registerChannel(){
        channel.setMethodCallHandler(this)
    }
    fun dispose(){
        context.unregisterReceiver(this)
        Log.w(TAG, "dispose")
    }
    private fun startRetriever(){
        val client = SmsRetriever.getClient(activity)
        val task = client.startSmsRetriever()
        task.addOnSuccessListener {
            Log.w(TAG, "addOnSuccessListener")
            context.registerReceiver(this,intent)
        }
        task.addOnFailureListener {
            Log.w(TAG, "addOnFailureListener")
        }
    }



    private fun emitOtp(otp: String?){
        Log.w(TAG, "otp emitted")
        if(otp!=null){
            channel.invokeMethod("otp", otp)
        }
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        if(call.method=="startListening"){
            startRetriever()
            result.success("start listening succesfully")
        }
        else if(call.method=="dispose"){
            dispose()
            result.success("receiver disposed")
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.w(TAG, "onReceive")

        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras: Bundle? = intent.extras
            val status: Status? = extras?.parcelable(SmsRetriever.EXTRA_STATUS)
            Log.w(TAG, "SmsRetriever.SMS_RETRIEVED_ACTION")

            when (status?.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    Log.w(TAG, "CommonStatusCodes.SUCCESS")
                    val message = extras.getString(SmsRetriever.EXTRA_SMS_MESSAGE)
                    val otp = otpRetriever.retrieveOtpFromMessage(message)
                    emitOtp(otp)
                }
                CommonStatusCodes.TIMEOUT -> {
                    Log.w(TAG, "timeout")
                }
                else -> {
                    Log.w(TAG, "unexpected error")
                }
            }
        }

    }



}