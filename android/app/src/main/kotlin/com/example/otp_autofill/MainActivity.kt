package com.example.otp_autofill

import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine

class MainActivity : FlutterActivity() {

private var smsReceiver: MySMSBroadcastReceiver? = null

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        val binaryMessenger = flutterEngine.dartExecutor.binaryMessenger
        val otpRetriever = OtpRetriever()
        smsReceiver = MySMSBroadcastReceiver(this,context,binaryMessenger, otpRetriever)
        smsReceiver!!.registerChannel()
    }

    override fun onDestroy() {
        super.onDestroy()
        smsReceiver?.dispose()
    }

}
