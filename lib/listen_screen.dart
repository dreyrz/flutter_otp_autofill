import 'package:flutter/material.dart';
import 'package:otp_autofill/input.dart';

import 'sms_listener.dart';

class ListenScreen extends StatefulWidget {
  const ListenScreen({super.key});

  @override
  State<ListenScreen> createState() => _ListenScreenState();
}

class _ListenScreenState extends State<ListenScreen> {
  late final SmsListener smsListener;
  late final List<TextEditingController> controllers;

  @override
  void initState() {
    smsListener = SmsListener();
    controllers = List.generate(6, (_) => TextEditingController());
    listen();
    super.initState();
  }

  void listen() {
    smsListener.startListening(onOtpReceived: fillInputs);
  }

  void destroy() {
    smsListener.dispose();
  }

  void fillInputs(String otp) {
    debugPrint("fillInputs");
    if (otp.length == 6) {
      for (int i = 0; i < controllers.length; i++) {
        controllers[i].text = otp[i];
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Expanded(
              child: Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  ElevatedButton(
                    onPressed: destroy,
                    child: const Text('stop listening'),
                  ),
                  ElevatedButton(
                    onPressed: listen,
                    child: const Text('start listening'),
                  ),
                ],
              ),
            ),
            Expanded(
              child: Row(
                children: List.generate(6, (i) => Input(controllers[i])),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
