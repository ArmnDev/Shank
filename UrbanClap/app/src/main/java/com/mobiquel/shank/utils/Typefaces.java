package com.mobiquel.shank.utils;

import java.util.Hashtable;

import android.content.Context;
import android.graphics.Typeface;

public class Typefaces {

	private static final Hashtable<String, Typeface> cache = new Hashtable<String, Typeface>();

	public static Typeface get(Context context,String font)
	{
		synchronized (cache) {
			if(!cache.containsKey(font))
			{
				Typeface typeface = Typeface.createFromAsset(context.getAssets(),String.format("%s.otf",font));
				cache.put(font, typeface);
			}


		}
		return cache.get(font);

	}
	
	public static Typeface getTTF(Context context,String font)
	{
		synchronized (cache) {
			if(!cache.containsKey(font))
			{
				Typeface typeface = Typeface.createFromAsset(context.getAssets(),String.format("%s.ttf",font));
				cache.put(font, typeface);
			}


		}
		return cache.get(font);

	}

}
