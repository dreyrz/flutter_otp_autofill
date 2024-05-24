package com.example.otp_autofill

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.phone.SmsRetriever
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity() {

    private val smsConsentRequest = 2  // Set to an unused request code
    private val channelName = "sms_listener"
    private var otpReceiver: OtpReceiver? = null
    private var methodChannel: MethodChannel? = null
    private var binaryMessenger: BinaryMessenger? = null
    private var useRetrieverApi: Boolean? = null

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        binaryMessenger = flutterEngine.dartExecutor.binaryMessenger
        methodChannel = MethodChannel(binaryMessenger!!, channelName)
        methodChannel!!.setMethodCallHandler {
                call, result ->
            useRetrieverApi = call.argument("useRetrieverApi")
            if(useRetrieverApi!=null){
                otpReceiver = if(useRetrieverApi == true){
                    FlutterSmsRetriever(this, context, methodChannel!!)
                } else {
                    FlutterSmsUserConsent(this, context)
                }
            }

            when(call.method){
                ("startListening") ->{
                    otpReceiver?.startReceiver()
                    result.success("start listening successfully")
                }
                ("dispose") ->{
                    otpReceiver?.dispose()
                    otpReceiver = null
                    result.success("receiver disposed")
                }
                else ->{
                    result.error("Not found", "method not found", null)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        otpReceiver?.dispose()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            smsConsentRequest ->
                if (resultCode == Activity.RESULT_OK && data != null && useRetrieverApi == false) {
                    val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)

                    val otp =otpReceiver?.retrieveOtpFromMessage(message) // define this function
                    if(otp!=null){
                        methodChannel!!.invokeMethod("otp", otp)
                    }
                } else {
                    Log.w(TAG, "smsConsentRequest else")
                }
        }
    }
}
