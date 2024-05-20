import 'package:flutter/material.dart';

class Input extends StatelessWidget {
  final TextEditingController controller;
  const Input(this.controller, {super.key});

  @override
  Widget build(BuildContext context) {
    return Expanded(
      child: Padding(
        padding: const EdgeInsets.all(8.0),
        child: TextField(
          controller: controller,
          maxLength: 1,
          autofillHints: const [AutofillHints.oneTimeCode],
          keyboardType: TextInputType.number,
        ),
      ),
    );
  }
}
