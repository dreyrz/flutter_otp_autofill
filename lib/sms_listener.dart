import 'dart:async';

import 'package:flutter/services.dart';

class SmsListener {
  static SmsListener? _singleton;

  factory SmsListener() => _singleton ?? SmsListener._();
  static const _channel = MethodChannel('sms_listener');

  SmsListener._() {
    _channel.setMethodCallHandler(_emitOtpState);
  }

  Future<void> _emitOtpState(MethodCall methodCalled) async {
    if (methodCalled.method == "otp") {
      _otpStream.add(methodCalled.arguments);
    }
  }

  final StreamController<String> _otpStream = StreamController.broadcast();

  Stream<String> get otp => _otpStream.stream;

  Future<void> listenToOtp() async {
    await _channel.invokeMethod('listenToOtp');
  }
}
