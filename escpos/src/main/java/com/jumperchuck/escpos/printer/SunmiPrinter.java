package com.jumperchuck.escpos.printer;

import android.content.Context;

import com.jumperchuck.escpos.command.PrinterCommander;
import com.jumperchuck.escpos.constant.PrinterStatus;

import java.io.IOException;

/**
 * 商米内置打印机
 */
public class SunmiPrinter extends EscPosPrinter {

    private PrinterCommander.Reader reader;

    private SunmiPrinter(Builder builder) {
        super(builder);
    }

    @Override
    public void connect() {
        if (isConnected()) {
            return;
        }
        sendStatusBroadcast(PrinterStatus.CONNECTING);
        connection.connect();
        if (isConnected()) {
            // 开启读取打印机返回数据线程
            reader = commander.createReader(this);
            reader.startRead();
            sendStatusBroadcast(PrinterStatus.CONNECTED);
        } else {
            // 连接失败, 重连一次
            connection.connect();
            if (isConnected()) {
                // 开启读取打印机返回数据线程
                reader = commander.createReader(this);
                reader.startRead();
                sendStatusBroadcast(PrinterStatus.CONNECTED);
            } else {
                sendStatusBroadcast(PrinterStatus.CONNECT_TIMEOUT);
            }
        }
    }

    @Override
    public void disconnect() {
        if (reader != null) {
            reader.cancelRead();
            reader = null;
        }
        if (isConnected()) {
            sendStatusBroadcast(PrinterStatus.DISCONNECTED);
        }
        connection.disconnect();
    }

    @Override
    public PrintResult print(Paper paper) {
        return null;
    }

    @Override
    public PrinterStatus getPrinterStatus() {
        if (!isConnected() || reader == null) {
            return PrinterStatus.DISCONNECTED;
        }
        PrinterStatus status = PrinterStatus.UNKNOWN_ERROR;
        try {
            status = reader.updateStatus(soTimeout);
        } catch (IOException e) {
            status = PrinterStatus.DISCONNECTED;
        }
        sendStatusBroadcast(status);
        return status;
    }

    public static class Builder extends EscPosPrinter.Builder<Builder> {
        public Builder(Context context) {
            super(context);
        }

        @Override
        public SunmiPrinter build() {
            return new SunmiPrinter(this);
        }
    }
}
