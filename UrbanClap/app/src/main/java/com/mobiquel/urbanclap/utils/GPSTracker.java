package com.mobiquel.urbanclap.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

public class GPSTracker extends Service implements LocationListener {

	private final Context mContext;
	boolean isGPSEnabled = false;
	boolean isNetworkEnabled = false;
	boolean canGetLocation = false;
	Location location = null; // location
	double latitude; // latitude
	double longitude; // longitude
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
	private static final long MIN_TIME_BW_UPDATES = 1000 * 5 * 1; // 1 minute
	protected LocationManager locationManager;

	public GPSTracker(Context context) 
	{
		this.mContext = context;
		getLocation();
	}

	public Location getLocation() 
	{
		try 
		{
			locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
			isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			if (!isGPSEnabled && !isNetworkEnabled) 
			{
				// no network provider is enabled
			} 
			else 
			{
				this.canGetLocation = true;
				if (isNetworkEnabled) 
				{
					locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
					Log.d("Network", "Network Enabled");
					if (locationManager != null) 
					{
						location = locationManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (location != null) 
						{
							latitude = location.getLatitude();
							longitude = location.getLongitude();
						}
					}
				}
				// if GPS Enabled get lat/long using GPS Services
				if (isGPSEnabled)
				{
					if (location == null) 
					{
						locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
						Log.d("GPS", "GPS Enabled");
						if (locationManager != null) 
						{
							location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if (location != null) 
							{
								latitude = location.getLatitude();
								longitude = location.getLongitude();
							}
						}
					}
				}
			}

		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return location;
	}

	/**
	 * Stop using GPS listener Calling this function will stop using GPS in your
	 * app
	 * */
	public void stopUsingGPS() 
	{
		if (locationManager != null) 
		{
			locationManager.removeUpdates(GPSTracker.this);
		}
	}

	/**
	 * Function to get latitude
	 * */
	public double getLatitude() 
	{
		location = getLocation();
		if (location != null) 
		{
			latitude = location.getLatitude();
		}
		return latitude;
	}

	/**
	 * Function to get longitude
	 * */
	public double getLongitude() 
	{
		location = getLocation();
		if (location != null) 
		{
			longitude = location.getLongitude();
		}
		return longitude;
	}

	/**
	 * Function to check GPS/wifi enabled
	 * 
	 * @return boolean
	 * */
	public boolean canGetLocation() 
	{
		return this.canGetLocation;
	}

	/**
	 * Function to show settings alert dialog On pressing Settings button will
	 * lauch Settings Options
	 * */
	public void showSettingsAlert(final Context c) 
	{
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(c);
		alertDialog.setTitle("GPS is settings");
		alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
		alertDialog.setPositiveButton("Settings",
				new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int which) 
			{
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				c.startActivity(intent);
			}
		});

		alertDialog.setNegativeButton("Cancel",	new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int which) 
			{
				dialog.cancel();
			}
		});
		if(!((Activity)mContext).isFinishing())
		{
		    alertDialog.show();
		}
		
	}

	@Override
	public void onLocationChanged(Location location) 
	{
		
	}

	@Override
	public void onProviderDisabled(String provider) 
	{
		showSettingsAlert(mContext);
	}

	@Override
	public void onProviderEnabled(String provider) 
	{
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) 
	{
		
	}

	@Override
	public IBinder onBind(Intent arg0) 
	{
		return null;
	}
}