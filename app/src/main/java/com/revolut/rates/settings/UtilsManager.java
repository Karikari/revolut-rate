package com.revolut.rates.settings;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.revolut.rates.RateApplication;
import com.revolut.rates.http.models.Country;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;

public class UtilsManager {

    private static String loadJSONFromAsset(Context context, String filname) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(filname);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }


    public static HashMap<String, Country> getCountryData(Context context) {
        return new Gson().fromJson(loadJSONFromAsset(context, "country.json"), new TypeToken<HashMap<String, Country>>() {
        }.getType());
    }

    public static HashMap<String, Object> getRateData(String data) {
        return new Gson().fromJson(data, new TypeToken<HashMap<String, Object>>() {
        }.getType());
    }

    public static String formatCurrency(Double number) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');

        DecimalFormat decimalFormat = new DecimalFormat("####.##", symbols);
        return decimalFormat.format(number);
    }

    public static Drawable getImage(String name){
        try {
            InputStream ims = RateApplication.getContext().getAssets().open("flags/"+name);
            return Drawable.createFromStream(ims, null);
        }catch (Exception e){
            Log.d("TAG", "IOException "+ e.getMessage());
            return null;
        }
    }

}
