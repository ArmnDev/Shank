package com.mobiquel.urbanclap.mapclasses;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.mobiquel.urbanclap.DAO.DAO;
import com.mobiquel.urbanclap.utils.Preferences;
import com.mobiquel.urbanclap.utils.Utils;

public class GPSReceiver extends BroadcastReceiver
{
	private LocationManager locationManager;
	private Location location;
	private Double longi, lati;
	private Context mContext;
	boolean isGPSEnabled = false;
	boolean canGetLocation = false;
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
	private static final long MIN_TIME_BW_UPDATES = 4000;
	
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		trigger(context);
	}

	public void trigger(Context context) 
	{
		mContext = context;
		getCurrentLocation();
	}

	public Location getLocation() {
		return location;
	}

	private void getCurrentLocation()
	{
		locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

		} else {
			requestLocation();
		}
	}

	private void requestLocation() 
	{
		try 
		{
			locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
			isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			if (!isGPSEnabled) 
			{

			} else 
			{
				if (isGPSEnabled) 
				{
					if (location == null) 
					{
						locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES, new MyLocationListener());
					}

					location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
					if (location != null) 
					{
						longi = location.getLongitude();
						lati = location.getLatitude();
						DAO dao = new DAO(mContext);
						dao.addLocation(Double.toString(lati), Double.toString(longi), Preferences.getInstance().userId);
						Log.e("INSERTED", "LAT_LON");
					}
				}
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	public class MyLocationListener implements LocationListener {
		public void onLocationChanged(final Location loc) {

		}

		public void onProviderDisabled(String provider) {
			Utils.showToast(mContext, "Please enable GPS");
		}

		public void onProviderEnabled(String provider) {
			Utils.showToast(mContext, "GPS is now available");
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {

		}
	}
}