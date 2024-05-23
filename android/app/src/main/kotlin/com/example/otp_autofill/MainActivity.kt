package com.example.otp_autofill

import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine

class MainActivity : FlutterActivity() {

private var otpReceiver: OtpRetriever? = null

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        val binaryMessenger = flutterEngine.dartExecutor.binaryMessenger
        otpReceiver = SmsRetriever(this, context, binaryMessenger)
        otpReceiver!!.registerChannel()
    }

    override fun onDestroy() {
        super.onDestroy()
        otpReceiver?.dispose()
    }
}
