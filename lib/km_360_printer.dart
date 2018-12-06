import 'dart:async';

import 'package:flutter/services.dart';

class Km360Printer {
  static const MethodChannel _channel = const MethodChannel('km_360_printer');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  Future<String> connect(String name, String address) async {
    Map<String, String> params = {"name": name, "address": address};
    final String connectCode = await _channel.invokeMethod('connect', params);
    return connectCode;
  }

  Future<String> printQrCode(String qrCode,
      String workOrderCode,
      String productCode,
      int quantity,
      int runCardVersion,
      int printTimeStamp,
      List<PrinterWorkOrderStep> steps,) async {
    List<Map<String, Object>> stepList = [];
    steps.forEach((step) {
      stepList.add({
        "name": step.name,
        "isFinished": step.isFinished,
      });
    });
    Map<String, Object> params = {
      "qrCode": qrCode,
      "workOrderCode": workOrderCode,
      "productCode": productCode,
      "quantity": quantity,
      "runCardVersion": runCardVersion,
      "printTimeStamp": printTimeStamp,
      "steps": stepList,
    };
    final String resultInfo = await _channel.invokeMethod(
        'printQrCode', params);
    return resultInfo;
  }
}

class PrinterWorkOrderStep {
  final String name;
  final bool isFinished;

  PrinterWorkOrderStep(this.name, this.isFinished);
}
