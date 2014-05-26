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

import com.sonycsl.echo.eoj.device.sensor.HumiditySensor;

public class FHumiditySensor extends HumiditySensor {
	byte[] mStatus = {0x30};
	byte[] mLocation = {0x00};
	byte[] mFaultStatus = {0x42};
	byte[] mManufacturerCode = {0,0,0};

	byte[] mVal = new byte[]{0} ;

	public FHumiditySensor() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected byte[] getMeasuredValueOfRelativeHumidity() {
		// TODO Auto-generated method stub
		return mVal;
	}

	@Override
	protected byte[] getOperationStatus() {
		// TODO Auto-generated method stub
		return mStatus;
	}

	@Override
	protected byte[] getFaultStatus() {
		// TODO Auto-generated method stub
		return mFaultStatus;
	}

	@Override
	protected byte[] getInstallationLocation() {
		// TODO Auto-generated method stub
		return mLocation;
	}

	@Override
	protected byte[] getManufacturerCode() {
		// TODO Auto-generated method stub
		return mManufacturerCode;
	}

	@Override
	protected boolean setInstallationLocation(byte[] arg0) {
		// TODO Auto-generated method stub
		mLocation[0] = arg0[0] ;
		return true;
	}

}
