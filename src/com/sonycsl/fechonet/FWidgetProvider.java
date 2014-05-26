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
import com.sonycsl.fechonet.R;
import android.app.Notification;
import android.app.NotificationManager;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

public class FWidgetProvider extends AppWidgetProvider {
    public static class WidgetService extends Service {
    	public FPlugInterface fPlug = new FPlugInterface() ;
    	private ECHOManager echoManager ;

    	@Override
    	public int onStartCommand(Intent intent, int flags, int startId) {
    		Toast.makeText(this, "Service started", Toast.LENGTH_LONG).show();
    		if( echoManager == null ){
    			(new Thread(new Runnable(){
    				@Override
    	        	public void run() {
    	        		echoManager = new ECHOManager(WidgetService.this) ;
    	        		fPlug.setup(/*context,*/echoManager) ;
    	        		echoManager.setupEchoNode();
    	        	}
    			})).start() ;
    		}
    		
    		  Notification notification = new NotificationCompat.Builder(this)
    		   .setContentTitle("FECHONET")
    		   .setSmallIcon(R.drawable.logo)
    		   .build();
    		  
    		  NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
    		  manager.notify(1, notification);
    		  
    		  startForeground(1,notification);
    		return START_STICKY_COMPATIBILITY;
    	}
        
        @Override
        public IBinder onBind(Intent in) {
            return null;
        }
    }
    
	@Override
	public void onEnabled(Context context){
		//Toast.makeText(context, "Widget Enabled", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		//Toast.makeText(context, "Widget Updated", Toast.LENGTH_LONG).show();
	 
		Intent in = new Intent(context, WidgetService.class);
	    context.startService(in);
	}
	
}
