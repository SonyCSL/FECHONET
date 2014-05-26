/*
The MIT License (MIT)

Copyright (c) 2014 Sony Computer Science Laboratories, Inc.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.

 */
package com.sonycsl.fechonet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.Toast;

public class FPlugInterface {
	private static final String TAG = "FPlugInterface";
    //UID for SPP communication
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final int STATE_NONE      = 0; // we're doing nothing
    public static final int STATE_CONNECTING    = 1; // now initiating an outgoing connection
    public static final int STATE_CONNECTED     = 2; // now connected to a remote device

    
    private BluetoothAdapter mBtAdapter;
	public Plug[] plugs ;

    private boolean DEBUG_Plug = true ;

    private FPlugCallback mFPlugCallback ;

	public FPlugInterface() {}
	
	public void setup(FPlugCallback fc){
		mFPlugCallback = (fc==null?new FPlugCallback():fc) ;
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBtAdapter == null) {
        	plugs = null ;
            return ;
        }

        boolean btEnable = mBtAdapter.isEnabled();
        if(btEnable == true) {
        }else{
            mBtAdapter.enable();
        }
        ArrayList<BluetoothDevice> bda = new ArrayList<BluetoothDevice>() ; 

        try{
            Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
            for(BluetoothDevice device: pairedDevices) {
                //Show those with "F-PLUG"
                if(device.getName() != null && device.getName().indexOf("F-PLUG") != -1) {
                	bda.add(device) ;
                }
            }
        }catch(Exception e) {
        }

        plugs = new Plug[ bda.size() ] ;

        for( int ri=0;ri<bda.size();++ri ){
        	plugs[ri] = new Plug() ;
        	plugs[ri].setDevice(bda.get(ri)) ;
        }
	}
	

    public class Plug {
    	public Plug(){}
    	public void setDevice(BluetoothDevice dev){
    		mDevice = dev ;
    		mConnectThread = new ConnectThread(dev) ;
    		mConnectThread.start();
    	}
    	private BluetoothDevice mDevice ;
        private BluetoothSocket mSocket;

        private ConnectThread mConnectThread;
        private int mState;

        
        public void updateLight(){
            exchange(new char[]{
            		0x10,0x81,0x00,0x01,0x0E,0xF0,0x00,0x00
            		,0x0D,0x00,0x62,0x01,0xE0,0x00}) ;
        }
        
         public void updateTemperature(){
            exchange(new char[]{
            		0x10,0x81,0x00,0x02,0x0E,0xF0,0x00,0x00
            		,0x11,0x00,0x62,0x01,0xE0,0x00}) ;
        }

        public void updateHumidity(){
            exchange(new char[]{
            		0x10,0x81,0x00,0x03,0x0E,0xF0,0x00,0x00
            		,0x12,0x00,0x62,0x01,0xE0,0x00}) ;
        }

        public void updateElectricity(){
            exchange(new char[]{
            		0x10,0x81,0x00,0x04,0x0E,0xF0,0x00,0x00
            		,0x22,0x00,0x62,0x01,0xE2,0x00}) ;
        }

        public void updateElectricity_Cum(){
        	Calendar c = Calendar.getInstance() ;
            exchange(new char[]{
            		0x10,0x82,0x00,0x05,0x11
            		,(char)c.get(Calendar.HOUR_OF_DAY)
            		,(char)c.get(Calendar.MINUTE)
            		,(char)(c.get(Calendar.YEAR)&0xff)
            		,(char)((c.get(Calendar.YEAR)>>8)&0xff)
            		,(char)(c.get(Calendar.MONTH))
            		,(char)(c.get(Calendar.DAY_OF_MONTH))}) ;
        }

        
        private class ConnectThread extends Thread{
            public ConnectThread(BluetoothDevice device) {
                BluetoothSocket tmp = null;
                if(SPP_UUID == null) {
                    if(DEBUG_Plug) Log.d(TAG, "SPP_UUID_null");
                }else{
                    if(DEBUG_Plug) Log.d(TAG, "SPP_UUID"+SPP_UUID);
                }

                try{
                	//BluetoothSocket を初期化する
                    tmp = mDevice.createRfcommSocketToServiceRecord(SPP_UUID);
                } catch (IOException e) {
                    Log.e(TAG, "Socket create() failed", e);
                }
                mSocket = tmp;
            }

            public void run() {
                Log.i(TAG, "BEGIN mConnectThread");
                setName("ConnectThread");

                /* Always cancel discovery because it will slow down a connection*/
                setState(STATE_CONNECTING);

                try{
                	//接続可能にする
                    mSocket.connect();
                } catch (Exception e) {
                    e.printStackTrace();
                    try{
                    //接続不可にする
                        mSocket.close();
                        mSocket = null;
                    } catch (Exception e2) {
                        Log.e(TAG, "unable to close() socket during connection failure", e2);
                    }
                    connectionFailed();
                    return;
                }

                //ConnectThreadをリセットさせる
                synchronized (Plug.this) {
                    mConnectThread = null;
                }

                /* Start the connected thread*/
                connected();
                if(DEBUG_Plug) Log.d(TAG,"mmSocket"+mSocket);

            }

            public void cancel() {
                try {
                    mSocket.close();
                    mSocket = null;
                    if(DEBUG_Plug) Log.d(TAG, "cancel_try");
                } catch (Exception e) {
                    Log.e(TAG, "close() of connect socket failed", e);
                }
            }
        }
        private synchronized void setState(int state) {
            if(DEBUG_Plug) Log.d(TAG, "setState() " + mState + " -> " + state);
            mState = state;
        }

        private void connectionFailed() {
            if(DEBUG_Plug) Log.d(TAG,"connectionFailed");
            setState(STATE_NONE);
        }

        public synchronized void connected() {
            if(DEBUG_Plug) Log.d(TAG, "connected");

            /* Cancel the thread that completed the connection */
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        void exchange(char[] value) {
            byte[] value2 = new byte[value.length];
            for (int i = 0; i < value2.length; i++) {
                value2[i] = (byte) value[i];
            }
            if(mSocket == null) {
                Log.e("test","socket not connected");
                return;
            }
            //(消費電力・照度・温度・湿度)の制御用メッセージを送信
            try {
                mSocket.getOutputStream().write(value2);
            } catch (UnsupportedEncodingException e1) {

                e1.printStackTrace();
                return;
            } catch (IOException e1) {

                e1.printStackTrace();
                return;
            }

            
            //制御用メッセージを受信
            //final Handler handler = new Handler();
            new Thread() {
               public void run() {
                   try {
//                       while (true) {
                           byte[] buf = new byte[16];
                           int len = mSocket.getInputStream().read(buf);
                           if(DEBUG_Plug) Log.d(TAG,"str:"+toHexString(buf));

                           switch(buf[3]){
                           case 1 :
                                FPlugInterface.this.mFPlugCallback.onRecvLight(Plug.this,new byte[]{buf[15],buf[14]});
                                break ;
                           case 2 : // Temperature
                                FPlugInterface.this.mFPlugCallback.onRecvTemperature(Plug.this,new byte[]{buf[15],buf[14]}) ;
                                break ;
                           case 3 : // Humidity
                               FPlugInterface.this.mFPlugCallback.onRecvHumidity(Plug.this,new byte[]{buf[14]}) ;
                               break ;
                           case 4 : // Electricity
                               FPlugInterface.this.mFPlugCallback.onRecvElectricity(Plug.this,new byte[]{buf[15],buf[14]}) ;
                               break ;
                           case 5 : // Electricity_Cum
                               FPlugInterface.this.mFPlugCallback.onRecvElectricity_Cum(Plug.this,new byte[]{buf[7],buf[6]}) ;
                               break ;
                           }
                   }catch(IOException e) {
                	   System.out.println("RECV thread end.") ;
                   }
               }
           }.start();
           if(DEBUG_Plug)     Log.d("test","socket connected and write");
        }
    }

    String toHexString(byte[] bytes) {
        char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] hexChars = new char[bytes.length * 3];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 3] = hexArray[v / 16];
            hexChars[j * 3 + 1] = hexArray[v % 16];
            hexChars[j * 3 + 2] = ',';
        }
        return new String(hexChars);
    }
}
