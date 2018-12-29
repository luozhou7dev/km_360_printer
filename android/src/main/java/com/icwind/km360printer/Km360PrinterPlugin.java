package com.icwind.km360printer;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;

import com.application.print.PrintPort;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * Km360PrinterPlugin
 */
public class Km360PrinterPlugin implements MethodCallHandler {

    private PrintPort printPort = new PrintPort();

    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "km_360_printer");
        channel.setMethodCallHandler(new Km360PrinterPlugin());
    }

    @Override
    public void onMethodCall(final MethodCall call, final Result result) {
        String method = call.method;
        if (method.equals("getPlatformVersion")) {
            result.success("Android " + android.os.Build.VERSION.RELEASE);
        } else if (method.equals("connect")) {
            if (mBluetoothAdapter == null) {
                result.success("BLUETOOTH_NOT_NULL");
                return;
            }
            if (!mBluetoothAdapter.isEnabled()) {
                result.success("BLUETOOTH_DISABLED");
                return;
            }
            final String name = call.argument("name");
            final String address = call.argument("address");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Boolean isConnected = printPort.connect(name, address);
                    if (isConnected) {
                        result.success("SUCCESS");
                    } else {
                        result.success("FAILED");
                    }
                }
            }).start();
        } else if (method.equals("getPrinterStatus")) {
            result.success(printPort.printerStatus());
        } else if (method.equals("printLogisticsQrCode")) {
            if (!printPort.isConnected()) {
                result.success("NO_CONNECTION");
                return;
            }
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm");
            @SuppressLint("UseSparseArrays") Map<Integer, Integer> yCoordinatesMap = new HashMap<>();
            yCoordinatesMap.put(0, 140);
            yCoordinatesMap.put(1, 140);
            yCoordinatesMap.put(2, 170);
            yCoordinatesMap.put(3, 170);
            yCoordinatesMap.put(4, 200);
            yCoordinatesMap.put(5, 200);
            yCoordinatesMap.put(6, 230);
            yCoordinatesMap.put(7, 230);

            String qrCode = call.argument("qrCode");
            long printTimeStamp = call.argument("printTimeStamp");
            String workOrderCode = call.argument("workOrderCode");
            String productCode = call.argument("productCode");
            int quantity = call.argument("quantity");
            int runCardVersion = call.argument("runCardVersion");
            List<Map<String, Object>> steps = call.argument("steps");

            printPort.pageSetup(560, 400 - 8);
            printPort.drawQrCode(20, -10, qrCode, 0, 10, 0);
            printPort.drawText(20, 220, qrCode, 2, 0, 0, false, false);
            printPort.drawText(20, 250, "打印时间：" + simpleDateFormat.format(new Date(printTimeStamp)), 1, 0, 0, false, false);

            printPort.drawText(250, -10, "工单：" + workOrderCode, 2, 0, 0, false, false);
            printPort.drawText(250, 20, "产品：" + productCode, 2, 0, 0, false, false);
            printPort.drawText(250, 50, "数量：" + quantity + " PCS", 2, 0, 0, false, false);

            printPort.drawText(250, 100, "RunCard：" + runCardVersion, 2, 0, 0, false, false);

            int index;
            for (index = 0; index < steps.size(); index++) {
                Map<String, Object> currentStep = steps.get(index);
                String finishIcon = (Boolean) currentStep.get("isFinished") ? "√" : "□";
                String stepName = currentStep.get("name").toString();
                Integer xCoordinate = index % 2 == 0 ? 280 : 420;

                printPort.drawText(xCoordinate - 30, yCoordinatesMap.get(index) - 5, finishIcon, 2, 0, 1, false, false);
                printPort.drawText(xCoordinate, yCoordinatesMap.get(index), stepName, 1, 0, 0, false, false);
            }
            result.success(printPort.print(0, 0));
        } else if (method.equals("printWmsQrCode")) {
            if (!printPort.isConnected()) {
                result.success("NO_CONNECTION");
                return;
            }
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm");

            String qrCode = call.argument("qrCode");
            String productName = call.argument("productName");
            String productCode = call.argument("productCode");
            String productSpec = call.argument("productSpec");
            int quantity = call.argument("quantity");
            long printTimeStamp = call.argument("printTimeStamp");

            printPort.pageSetup(560, 400 - 8);
            printPort.drawQrCode(20, -10, qrCode, 0, 10, 0);
            printPort.drawText(20, 220, qrCode, 2, 0, 0, false, false);
            printPort.drawText(20, 250, "打印时间：" + simpleDateFormat.format(new Date(printTimeStamp)), 1, 0, 0, false, false);

            printPort.drawText(250, -10, "料件编号：" + productCode, 2, 0, 0, false, false);
            printPort.drawText(250, 30, "品名：" + productName, 2, 0, 0, false, false);
            printPort.drawText(250, 70, "规格：" + productSpec, 2, 0, 0, false, false);
            printPort.drawText(250, 110, "数量：" + quantity + " PCS", 2, 0, 0, false, false);

            result.success(printPort.print(0, 0));
        } else {
            result.notImplemented();
        }
    }
}
