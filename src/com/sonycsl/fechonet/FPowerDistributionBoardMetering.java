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

import com.sonycsl.echo.eoj.device.housingfacilities.PowerDistributionBoardMetering;

public class FPowerDistributionBoardMetering extends
		PowerDistributionBoardMetering {
	byte[] mStatus = {0x30};
	byte[] mLocation = {0x00};
	byte[] mFaultStatus = {0x42};
	byte[] mManufacturerCode = {0,0,0};
	
	byte[][] mVals ;
	byte[][] mVals_Cum ;

	public FPowerDistributionBoardMetering(int plen) {
		// TODO Auto-generated constructor stub
		mVals = new byte[plen][2] ;
		mVals_Cum = new byte[plen][2] ;
	}
	@Override
	protected void setupPropertyMaps() {
		super.setupPropertyMaps();
		addGetProperty(EPC_MEASURED_INSTANTANEO_US_AMOUNT_OF_ELECTRIC_ENERGY) ;
		for(int i=0;i<32;++i)
			addGetProperty((byte)(EPC_MEASUREMENT_CHANNEL1+i));
	}

	@Override
	protected byte[] getMeasuredCumulativeAmountOfElectricEnergyNormalDirection() {
		// TODO Auto-generated method stub
		double mVal_d = 0 ;
		for( int pi=0;pi<mVals_Cum.length;++pi){
			mVal_d += (((long)(mVals_Cum[pi][0]&0xff))<<8) + (mVals_Cum[pi][1]&0xff) ;
		}
		long mVal = (long)(mVal_d / 10000) ; // 0.1W to kW(h) 
		return new byte[]{(byte)((mVal>>24)&0xff),(byte)((mVal>>16)&0xff),(byte)((mVal>>8)&0xff),(byte)(mVal&0xff)};
	}

	@Override
	protected byte[] getMeasuredCumulativeAmountOfElectricEnergyReverseDirection() {
		// TODO Auto-generated method stub
		return getMeasuredCumulativeAmountOfElectricEnergyNormalDirection();
	}

	private byte[] mUnit = {1} ; // 0.1kWh
	@Override
	protected byte[] getUnitForCumulativeAmountsOfElectricEnergy() {
		// TODO Auto-generated method stub
		return mUnit;
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

	@Override
	protected byte[] getMeasuredInstantaneoUsAmountOfElectricEnergy() {
		double mVal_d = 0 ;
		for( int pi=0;pi<mVals.length;++pi){
			mVal_d += (((long)(mVals[pi][0]&0xff))<<8) + (mVals[pi][1]&0xff) ;
		}
		long mVal = (long)(mVal_d / 10) ; // 0.1W to W 
		return new byte[]{(byte)((mVal>>24)&0xff),(byte)((mVal>>16)&0xff),(byte)((mVal>>8)&0xff),(byte)(mVal&0xff)};
	}

	byte[] b08 = {0,0,0,0,0,0,0,0} ;
	private byte[] bmc(int ch){
		if( mVals.length < ch ) return null;
		 
		b08[2] = mVals[ch-1][0] ;
		b08[3] = mVals[ch-1][1] ;
		return b08 ;
	}
	@Override
	protected byte[] getMeasurementChannel1() {	return bmc(1) ;	}
	@Override
	protected byte[] getMeasurementChannel2() {	return bmc(2) ;	}
	@Override
	protected byte[] getMeasurementChannel3() {	return bmc(3) ;	}
	@Override
	protected byte[] getMeasurementChannel4() {	return bmc(4) ;	}
	@Override
	protected byte[] getMeasurementChannel5() {	return bmc(5) ;	}
	@Override
	protected byte[] getMeasurementChannel6() {	return bmc(6) ;	}
	@Override
	protected byte[] getMeasurementChannel7() {	return bmc(7) ;	}
	@Override
	protected byte[] getMeasurementChannel8() {	return bmc(8) ;	}

	@Override
	protected byte[] getMeasurementChannel9() {	return bmc(9) ;	}
	@Override
	protected byte[] getMeasurementChannel10() {	return bmc(10) ;	}
	@Override
	protected byte[] getMeasurementChannel11() {	return bmc(11) ;	}
	@Override
	protected byte[] getMeasurementChannel12() {	return bmc(12) ;	}
	@Override
	protected byte[] getMeasurementChannel13() {	return bmc(13) ;	}
	@Override
	protected byte[] getMeasurementChannel14() {	return bmc(14) ;	}
	@Override
	protected byte[] getMeasurementChannel15() {	return bmc(15) ;	}
	@Override
	protected byte[] getMeasurementChannel16() {	return bmc(16) ;	}

	@Override
	protected byte[] getMeasurementChannel17() {	return bmc(17) ;	}
	@Override
	protected byte[] getMeasurementChannel18() {	return bmc(18) ;	}
	@Override
	protected byte[] getMeasurementChannel19() {	return bmc(19) ;	}
	@Override
	protected byte[] getMeasurementChannel20() {	return bmc(20) ;	}
	@Override
	protected byte[] getMeasurementChannel21() {	return bmc(21) ;	}
	@Override
	protected byte[] getMeasurementChannel22() {	return bmc(22) ;	}
	@Override
	protected byte[] getMeasurementChannel23() {	return bmc(23) ;	}
	@Override
	protected byte[] getMeasurementChannel24() {	return bmc(24) ;	}

	@Override
	protected byte[] getMeasurementChannel25() {	return bmc(25) ;	}
	@Override
	protected byte[] getMeasurementChannel26() {	return bmc(26) ;	}
	@Override
	protected byte[] getMeasurementChannel27() {	return bmc(27) ;	}
	@Override
	protected byte[] getMeasurementChannel28() {	return bmc(28) ;	}
	@Override
	protected byte[] getMeasurementChannel29() {	return bmc(29) ;	}
	@Override
	protected byte[] getMeasurementChannel30() {	return bmc(30) ;	}
	@Override
	protected byte[] getMeasurementChannel31() {	return bmc(31) ;	}
	@Override
	protected byte[] getMeasurementChannel32() {	return bmc(32) ;	}

}
