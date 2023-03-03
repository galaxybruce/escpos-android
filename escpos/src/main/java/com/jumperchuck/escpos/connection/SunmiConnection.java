package com.jumperchuck.escpos.connection;

import android.content.Context;

import com.jumperchuck.escpos.constant.ConnectType;
import com.sunmi.peripheral.printer.InnerPrinterCallback;
import com.sunmi.peripheral.printer.InnerPrinterException;
import com.sunmi.peripheral.printer.InnerPrinterManager;
import com.sunmi.peripheral.printer.SunmiPrinterService;

import java.io.IOException;
import java.util.Vector;

public class SunmiConnection extends PrinterConnection {
    private SunmiPrinterService service;
    private Context context;

    private final InnerPrinterCallback callback = new InnerPrinterCallback() {
        @Override
        protected void onConnected(SunmiPrinterService service) {
            SunmiConnection.this.service = service;
        }

        @Override
        protected void onDisconnected() {
            SunmiConnection.this.service = null;
        }
    };

    public SunmiPrinterService getService() {
        return service;
    }

    private boolean isConnect;

    public SunmiConnection(Context context) {
        this.context = context;
    }

    @Override
    public ConnectType connectType() {
        return ConnectType.SUNMI;
    }

    @Override
    public void connect() {
        try {
            InnerPrinterManager.getInstance().bindService(context, callback);
        } catch (InnerPrinterException e) {
            e.printStackTrace();
        }
        isConnect = true;
    }

    @Override
    public void disconnect() {
        isConnect = false;
        try {
            InnerPrinterManager.getInstance().unBindService(context, callback);
        } catch (InnerPrinterException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isConnected() {
        return isConnect && service != null;
    }

    @Override
    public void writeData(byte[] data, int off, int len) throws IOException {

    }

    @Override
    public void writeData(Vector<Byte> data, int off, int len) throws IOException {

    }

    @Override
    public int readData(byte[] bytes) throws IOException {
        return -1;
    }
}
