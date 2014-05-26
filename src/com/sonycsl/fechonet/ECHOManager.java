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
import java.util.Timer;
import java.util.TimerTask;

import com.sonycsl.echo.Echo;
import com.sonycsl.echo.eoj.device.DeviceObject;
import com.sonycsl.echo.processing.defaults.DefaultNodeProfile;
import com.sonycsl.fechonet.FPlugInterface.Plug;

public class ECHOManager extends FPlugCallback {
	private FWidgetProvider.WidgetService mMain ;

	FPowerDistributionBoardMetering powerDist ;
	DeviceObject[][] devs ;

	public ECHOManager(FWidgetProvider.WidgetService ws) {
		mMain = ws ;
	}
	
	public void setupEchoNode(){
		
		int plen = mMain.fPlug.plugs.length ;

		powerDist = new FPowerDistributionBoardMetering(plen) ;
		DeviceObject[] doarr = new DeviceObject[plen*3+1] ;
		doarr[0] = powerDist ;
		devs = new DeviceObject[plen][3] ;

		for( int pi=0;pi<plen;++pi){
			//devs[pi] = new DeviceObject[3] ;
			doarr[1+pi*3  ] = devs[pi][0] = new FIlluminanceSensor() ;
			doarr[1+pi*3+1] = devs[pi][1] = new FTemperatureSensor() ;
			doarr[1+pi*3+2] = devs[pi][2] = new FHumiditySensor() ;
		}

		try {
			Echo.start( new DefaultNodeProfile(),doarr );
			echoPollingTimer = new Timer() ;
			echoPollingTimer.scheduleAtFixedRate( new PollingTimerTask(),1000,15000);
		} catch( IOException e){
			e.printStackTrace();
		}
	}
	private Timer echoPollingTimer ;
	private class PollingTimerTask extends TimerTask {
		@Override
		public void run() {
			ECHOManager.this.updateValues() ;
		}
	}

	void updateValues(){
		for( int pi=0;pi<mMain.fPlug.plugs.length;++pi){
			mMain.fPlug.plugs[pi].updateLight();
			mMain.fPlug.plugs[pi].updateTemperature();
			mMain.fPlug.plugs[pi].updateHumidity();
			mMain.fPlug.plugs[pi].updateElectricity();
			//mainActivity.fPlug.plugs[pi].updateElectricity_Cum();
		}
	}
	
	// FPlug value receivers
	@Override
    public void onRecvLight(Plug plug, byte[] result){
		int pi ;
		for( pi=0;pi<mMain.fPlug.plugs.length;++pi ){
			if( mMain.fPlug.plugs[pi] == plug) break ;
		}
		if( pi == mMain.fPlug.plugs.length )
			return ;	// No corresponding plug found

		FIlluminanceSensor d = ((FIlluminanceSensor)devs[pi][0]) ;
		d.mVal_1[0] = result[0] ;
		d.mVal_1[1] = result[1] ;

		int tempdata = (int) (result[1] & 0xff);
 	   	tempdata += ((int) result[0] & 0xff) * 256;
 	   	System.out.println("Light : "+tempdata) ;
    }

	@Override
    public void onRecvTemperature(Plug plug, byte[] result){
		int pi ;
		for( pi=0;pi<mMain.fPlug.plugs.length;++pi ){
			if( mMain.fPlug.plugs[pi] == plug) break ;
		}
		if( pi == mMain.fPlug.plugs.length )
			return ;	// No corresponding plug found

		FTemperatureSensor d = ((FTemperatureSensor)devs[pi][1]) ;
		d.mVal[0] = result[0] ;
		d.mVal[1] = result[1] ;

		
		int tempdata = (int) result[0] & 0xff;
		double doubledata ;
        if (tempdata > 0x7f) // negative reading
        {
            tempdata = (((int) result[0] & 0xff) * 256 + (int) (result[1] & 0xff)) & 0xffffffff;
            doubledata = tempdata;
        }else{
            tempdata = ((int) result[0] & 0xff) * 256 + (int) (result[1] & 0xff);
            doubledata = tempdata;
        }
        //F-Plugのデータ(温度)の値に対しては0.1倍する必要がある
        doubledata = doubledata / 10;
        System.out.println("Temperature : "+doubledata) ;
    }

	@Override
    public void onRecvHumidity(Plug plug, byte[] result){
		int pi ;
		for( pi=0;pi<mMain.fPlug.plugs.length;++pi ){
			if( mMain.fPlug.plugs[pi] == plug) break ;
		}
		if( pi == mMain.fPlug.plugs.length )
			return ;	// No corresponding plug found

		FHumiditySensor d = ((FHumiditySensor)devs[pi][2]) ;
		d.mVal[0] = result[0] ;

		
		System.out.println("Humidity : "+(result[0]&0xff)) ;
    }
    
	@Override
    public void onRecvElectricity(Plug plug, byte[] result){
		int pi ;
		for( pi=0;pi<mMain.fPlug.plugs.length;++pi ){
			if( mMain.fPlug.plugs[pi] == plug) break ;
		}
		if( pi == mMain.fPlug.plugs.length )
			return ;	// No corresponding plug found

		powerDist.mVals[pi][0] = result[0] ;
		powerDist.mVals[pi][1] = result[1] ;
		int tempdata = (int) result[0] & 0xff;
		double doubledata ;
        if (tempdata > 0x7f) // negative reading
        {
            tempdata = (((int) result[0] & 0xff) * 256 + (int) (result[1] & 0xff)) & 0xffffffff;
            doubledata = tempdata;
        }else{
            tempdata = ((int) result[0] & 0xff) * 256 + (int) (result[1] & 0xff);
            doubledata = tempdata;
        }
        //F-Plugのデータ(消費電力)の値に対しては0.1倍する必要がある
        doubledata = doubledata / 10;		

		System.out.println("Electricity : "+doubledata) ;
    }
    public void onRecvElectricity_Cum(Plug plug, byte[] result){
		int pi ;
		for( pi=0;pi<mMain.fPlug.plugs.length;++pi ){
			if( mMain.fPlug.plugs[pi] == plug) break ;
		}
		if( pi == mMain.fPlug.plugs.length )
			return ;	// No corresponding plug found

		powerDist.mVals_Cum[pi][0] = result[0] ;
		powerDist.mVals_Cum[pi][1] = result[1] ;

		int tempdata = (int) result[0] & 0xff;
		double doubledata ;
        if (tempdata > 0x7f) // negative reading
        {
            tempdata = (((int) result[0] & 0xff) * 256 + (int) (result[1] & 0xff)) & 0xffffffff;
            doubledata = tempdata;
        }else{
            tempdata = ((int) result[0] & 0xff) * 256 + (int) (result[1] & 0xff);
            doubledata = tempdata;
        }
        //F-Plugのデータ(消費電力)の値に対しては0.1倍する必要がある
        doubledata = doubledata / 10;		

		System.out.println("Electricity_Cum : "+doubledata) ;
	}

}
