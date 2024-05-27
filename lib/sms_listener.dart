import 'dart:async';

import 'package:flutter/services.dart';

typedef OtpCallback = void Function(String otp);

class SmsListener {
  static final SmsListener _singleton = SmsListener._();
  factory SmsListener() => _singleton;

  SmsListener._() {
    _channel.setMethodCallHandler(_emitOtpState);
  }

  static const _channel = MethodChannel('sms_listener');

  StreamController<String> _otpStream = StreamController.broadcast();

  OtpCallback? _otpCallback;

  Future<void> _emitOtpState(MethodCall methodCalled) async {
    if (methodCalled.method == "otp") {
      _otpCallback?.call(methodCalled.arguments);
      dispose();
    }
  }

  Future<void> startListening({
    OtpCallback? onOtpReceived,
    bool useRetrieverApi = true,
  }) async {
    if (_otpStream.isClosed) {
      _otpStream = StreamController.broadcast();
    }
    await _channel.invokeMethod(
      'startListening',
      {"useRetrieverApi": useRetrieverApi},
    );

    _otpCallback = onOtpReceived;
  }

  Future<void> dispose() async {
    await _channel.invokeMethod('dispose');
    await _otpStream.close();
  }
}
