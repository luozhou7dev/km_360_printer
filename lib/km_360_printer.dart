import 'dart:async';

import 'package:flutter/services.dart';

class Km360Printer {
  static const MethodChannel _channel = const MethodChannel('km_360_printer');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<String> get printerStatus async {
    final String printerStatus = await _channel.invokeMethod('getPrinterStatus');
    return printerStatus;
  }

  static Future<String> connect(String name, String address) async {
    Map<String, String> params = {"name": name, "address": address};
    final String connectCode = await _channel.invokeMethod('connect', params);
    return connectCode;
  }

  static Future<String> printLogisticsQrCode(
    String qrCode,
    String workOrderCode,
    String productCode,
    int quantity,
    int runCardVersion,
    int printTimeStamp,
    List<PrinterWorkOrderStep> steps,
  ) async {
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
    final String resultInfo =
        await _channel.invokeMethod('printLogisticsQrCode', params);
    return resultInfo;
  }

  static Future<String> printWmsQrCode(
      String qrCode,
      String productName,
      String productCode,
      String productSpec,
      int quantity,
      int printTimeStamp) async {
    Map<String, Object> params = {
      "qrCode": qrCode,
      "productName": productName,
      "productCode": productCode,
      "quantity": quantity,
      "productSpec": productSpec,
      "printTimeStamp": printTimeStamp,
    };
    final String resultInfo =
        await _channel.invokeMethod('printWmsQrCode', params);
    return resultInfo;
  }
}

class PrinterWorkOrderStep {
  final String name;
  final bool isFinished;

  PrinterWorkOrderStep({this.name, this.isFinished});
}
