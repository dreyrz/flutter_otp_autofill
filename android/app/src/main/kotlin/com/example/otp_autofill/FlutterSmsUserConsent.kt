package com.example.otp_autofill

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat.startActivityForResult
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

class FlutterSmsUserConsent(
    private val activity: Activity,
    private val context: Context,
) :  BroadcastReceiver(), OtpReceiver {

    private val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
    private val smsContentRequest = 2

    override fun startReceiver() {
        val task = SmsRetriever.getClient(context).startSmsUserConsent(null)
        task.addOnSuccessListener {
            Log.w(TAG, "FlutterSmsUserConsent addOnSuccessListener")
            context.registerReceiver(this, intentFilter)
        }
        task.addOnFailureListener {
            Log.w(TAG, "FlutterSmsUserConsent addOnFailureListener")
        }
    }

    override fun dispose() {
        Log.w(TAG, "FlutterSmsUserConsent dispose")
        context.unregisterReceiver(this)
    }

    override fun onReceive(context: Context?, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras: Bundle?= intent.extras
            val smsRetrieverStatus: Status? = extras?.parcelable(SmsRetriever.EXTRA_STATUS)

            when (smsRetrieverStatus?.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val consentIntent = extras.parcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT)
                    try {
                        if(consentIntent != null){
                            startActivityForResult(
                                activity,
                                consentIntent,
                                smsContentRequest,
                                null
                            )
                        }
                    } catch (e: ActivityNotFoundException) {
                        Log.w(TAG, "ActivityNotFoundException")
                    }
                }
                CommonStatusCodes.TIMEOUT -> {
                    Log.w(TAG, "consent TIMEOUT")
                }
            }
        }
    }
}