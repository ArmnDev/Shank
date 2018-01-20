package com.mobiquel.urbanclap;

import android.app.Application;
import android.content.Context;

import com.mobiquel.urbanclap.utils.ConnectivityReceiver;

public class ShankApplication extends Application {
	
	private static ShankApplication instance;

    public ShankApplication() {
    	instance = this;
    }

    @Override
    public void onCreate() 
    {
        super.onCreate();
        instance = this;
    }
    
    public Context getContext() {
    	return instance;
    }
    
    public static synchronized ShankApplication getInstance() {
        return instance;
    }
    
    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}

