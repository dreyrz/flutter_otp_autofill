package com.example.otp_autofill

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import io.flutter.plugin.common.MethodChannel

class FlutterSmsRetriever(
    private val activity: Activity,
    private val context: Context,
    private val channel: MethodChannel,
) : BroadcastReceiver(), OtpReceiver {

    private val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)

    override fun dispose(){
        Log.w(TAG, "FlutterSmsRetriever dispose")
        context.unregisterReceiver(this)
    }
    override fun startReceiver(){
        val client = SmsRetriever.getClient(activity)
        val task = client.startSmsRetriever()
        task.addOnSuccessListener {
            Log.w(TAG, "FlutterSmsRetriever addOnSuccessListener")
            context.registerReceiver(this, intentFilter)
        }
        task.addOnFailureListener {
            Log.w(TAG, "FlutterSmsRetriever addOnFailureListener")
        }
    }

    private fun emitOtp(otp: String?){
        Log.w(TAG, "otp emitted")
        if(otp!=null){
            channel.invokeMethod("otp", otp)
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
                    val otp = retrieveOtpFromMessage(message)
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