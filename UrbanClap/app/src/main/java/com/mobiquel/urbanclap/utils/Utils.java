package com.mobiquel.urbanclap.utils;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class Utils {
	
	private static final String EMAIL_PATTERN = 
			"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";


	public static final class MESSAGES
	{

		public static final String ENABLE_INTERNET_SETTING_MESSAGE = "Unable to fetch latest data, kindly enable internet settings!";
		public static final String ERROR_FETCHING_DATA_MESSAGE = "Error fetching data, please try after sometime!";
		public static final String ERROR_NO_DATA_AVAILABLE_MESSAGE = "No data available at this moment!";
		public static final String ERROR_SAVING_SETTING_MESSAGE = "Error saving settings, please try after sometime!";
		public static final String NO_DATA_AVAILABLE_MESSAGE = "No new updates available!";
		public static final String NO_FIELD_BLANK_MESSAGE = "No field can be left blank!";
		public static final String ENTER_VALID_EMAIL = "Please enter a valid email address!";
		public static final String ENTER_VALID_PHONE_NUMBER = "Please enter a valid mobile number!";

	}

	public static void hideKeyboard(Context context)
	{
		View view = ((Activity)context).getCurrentFocus();
		if (view != null) {  
		    InputMethodManager imm = (InputMethodManager)((Activity)context).getSystemService(Context.INPUT_METHOD_SERVICE);
		    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}
	
	public static boolean isValidEmail(String input)
	{
	
		return input.matches(EMAIL_PATTERN);
	}
	
	public static Bitmap decodeSampledBitmapFromResource(Resources res,int resId,int reqWidth,int reqHeight)
	{
		//decoding just to check bounds
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res,resId, options);

		//calculating inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		//decoding bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);

	}
	
	public static int countWords(String s){

	    int wordCount = 0;

	    boolean word = false;
	    int endOfLine = s.length() - 1;

	    for (int i = 0; i < s.length(); i++) {
	        // if the char is a letter, word = true.
	        if (Character.isLetter(s.charAt(i)) && i != endOfLine) {
	            word = true;
	            // if char isn't a letter and there have been letters before,
	            // counter goes up.
	        } else if (!Character.isLetter(s.charAt(i)) && word) {
	            wordCount++;
	            word = false;
	            // last word of String; if it doesn't end with a non letter, it
	            // wouldn't count without this.
	        } else if (Character.isLetter(s.charAt(i)) && i == endOfLine) {
	            wordCount++;
	        }
	    }
	    return wordCount;
	}
	
	public static int calculateInSampleSize(BitmapFactory.Options options,int reqWidth,int reqHeight)
	{
		// actual height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;

		//initial sample size
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}
	public static boolean isNetworkAvailable(Context context) 
	{
		ConnectivityManager connectivityManager 
		= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	public static HashMap<String,String> convertArrayToHashMap(JSONArray inputArray, HashMap<String,String> hoodList)
	{
		for(int i=0;i<inputArray.length();i++)
		{
			try {
				hoodList.put(inputArray.getJSONObject(i).getString("name"),inputArray.getJSONObject(i).getString("hoodId"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return hoodList;
	}

	public static String[] convertJSONToStringArray(JSONArray imageArray)
	{
		List<String> imageUrlList = new LinkedList<String>();
		for(int i=0;i<imageArray.length();i++)
		{
			try 
			{
				imageUrlList.add(imageArray.getJSONObject(i).getString("imageURL"));
			} catch (JSONException e) 
			{
				e.printStackTrace();
			}
		}
		return Arrays.copyOf(imageUrlList.toArray(), imageUrlList.toArray().length, String[].class);

	}

	@SuppressLint("SimpleDateFormat") public long dateStringToLong(String date) throws ParseException
	{
		SimpleDateFormat datetimeFormatter1 = new SimpleDateFormat(
				"yyyy-MM-dd hh:mm:ss");
		Date lFromDate1 = datetimeFormatter1.parse(date);
		System.out.println("gpsdate :" + lFromDate1);
		Timestamp fromTS1 = new Timestamp(lFromDate1.getTime());
		return fromTS1.getTime();
	}
	public static void clearApplicationData(Context context) 
	{
		File cache = context.getCacheDir();
		File appDir = new File(cache.getParent());
		if (appDir.exists()) {
			String[] children = appDir.list();
			for (String s : children) {
				if (!s.equals("lib")) {
					deleteDir(new File(appDir, s));
					Log.i("TAG", "**************** File /data/data/APP_PACKAGE/" + s + " DELETED *******************");
				}
			}
		}
	}
	public static boolean deleteDir(File dir) 
	{
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}
	public static int getDateOfMonth(Date date) throws ParseException{  
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.DATE);
	}
	@SuppressLint("SimpleDateFormat")
	public static String getMonthName(Date date) throws ParseException{  
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		String monthName = new SimpleDateFormat("MMMM").format(cal.getTime());
		return monthName;
	}
	public static int getCurrentYear(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);

		return year;
	}
	public static int getNoOfDaysInMonth(Date date){  
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		return maxDays;
	}
	public static void showToast(Context context, String message)
	{
		Toast.makeText(context,message, Toast.LENGTH_SHORT).show();

	}
	public static boolean validatePhoneNumber(String phoneNo) {
		//validate phone numbers of format "1234567890"
		if (phoneNo.matches("\\d{10}")) return true;
		//validating phone number with -, . or spaces
		else if(phoneNo.matches("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}")) return true;
		//validating phone number with extension length from 3 to 5
		else if(phoneNo.matches("\\d{3}-\\d{3}-\\d{4}\\s(x|(ext))\\d{3,5}")) return true;
		//validating phone number where area code is in braces ()
		else if(phoneNo.matches("\\(\\d{3}\\)-\\d{3}-\\d{4}")) return true;
		//return false if nothing matches the input
		else return false;

	}
	public static void CopyStream(InputStream is, OutputStream os)
	{
		final int buffer_size=1024;
		try
		{
			byte[] bytes=new byte[buffer_size];
			for(;;)
			{
				int count=is.read(bytes, 0, buffer_size);
				if(count==-1)
					break;
				os.write(bytes, 0, count);
			}
		}
		catch(Exception ex){}
	}


	@SuppressWarnings("unused")
	private Context _context;

	// constructor
	public Utils(Context context) {
		this._context = context;
	}

	public static JSONArray remove(final int idx, final JSONArray from) {
	    final List<JSONObject> objs = asList(from);
	    objs.remove(idx);

	    final JSONArray ja = new JSONArray();
	    for (final JSONObject obj : objs) {
	        ja.put(obj);
	    }

	    return ja;
	}
	public static List<JSONObject> asList(final JSONArray ja) {
	    final int len = ja.length();
	    final ArrayList<JSONObject> result = new ArrayList<JSONObject>(len);
	    for (int i = 0; i < len; i++) {
	        final JSONObject obj = ja.optJSONObject(i);
	        if (obj != null) {
	            result.add(obj);
	        }
	    }
	    return result;
	}
	
	public static void sendSMS(String phoneNumber, String message) {
	    ArrayList<PendingIntent> sentPendingIntents = new ArrayList<PendingIntent>();
	    ArrayList<PendingIntent> deliveredPendingIntents = new ArrayList<PendingIntent>();
	     try 
	     {
	        SmsManager sms = SmsManager.getDefault();
	        ArrayList<String> mSMSMessage = sms.divideMessage(message);
	        for (int i = 0; i < mSMSMessage.size(); i++) 
	        {
	        	
	        }
	        sms.sendMultipartTextMessage(phoneNumber, null, mSMSMessage,
	                sentPendingIntents, deliveredPendingIntents);

	    } 
	     catch (Exception e) 
	    {
	        e.printStackTrace();
	    }

	}
}
