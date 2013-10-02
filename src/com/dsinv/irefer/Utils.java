package com.dsinv.irefer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import java.util.Random;

import com.dsinv.irefer.model.Doctor;

public class Utils {
	
	public static String pageId = "page_id";
	public static int HOME_PAGE   = 11;
	public static int SETUP_PAGE  = 12;
	public static int FILTER_PAGE = 13;
	private static Random randomGenerator = new Random();
    
	public static int docSyncLimit = 15000;
	public static int docSyncStep = 200;
	public static Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9+._%-+]{1,256}" +
            "@" +
            "[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" +
            "(" +
            "." +
            "[a-zA-Z0-9][a-zA-Z0-9-]{0,25}" +
            ")+"
        );
	
	public static String getCurrentTime(){
		Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM,yyyy hh:mm a");
        return sdf.format(cal.getTime());
	}
	public static boolean checkEmail(String email) {
	       return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
	}
	
	public static Doctor getDoctorById(int id) throws Exception {
		String jsonData = getDataFromURL(ABC.WEB_URL+"doctor/docJson?doc_id="+id);
		JSONTokener jsonTokener = new JSONTokener(jsonData);
    	JSONArray arr = (JSONArray) jsonTokener.nextValue();
		return new Doctor(arr.getJSONObject(0));
	}  
	
	public static List<Doctor> getDoctorListFromJSON(String jsonData) throws Exception {
		List<Doctor> docList = new ArrayList<Doctor>();
		JSONTokener jsonTokener = new JSONTokener(jsonData);
    	//JSONObject object = (JSONObject) jsonTokener.nextValue();
    	JSONArray arr = (JSONArray) jsonTokener.nextValue();
    	for(int i=0; i < arr.length(); i++) { 
    		JSONObject object = arr.getJSONObject(i);
    		docList.add(new Doctor(object));
    	}
    	
    	return docList;
	}
	
	public static String getDataFromURL(String urlStr) throws Exception {
		//System.out.println("SMM::URL::"+urlStr);
	    URL url = new URL(urlStr);
	    URLConnection urlCon = url.openConnection();
	    BufferedReader in = new BufferedReader(
	                            new InputStreamReader(urlCon.getInputStream()));
	    String data = "";
	    String line = "";
	    
        while ((line = in.readLine()) != null)
        	data += line;
            //System.out.println(inputLine);
	    in.close();
	    return data;
    }
	
	public static boolean isEmpty(String str) {
		if(str == null || str.trim().length() < 1) return true;
		if("null".equalsIgnoreCase(str))           return true;
		return false;
	} 
	
	public static boolean isEmptyNumber(String str) {
		if(str == null || str.trim().length() < 1) return true;
		if("null".equalsIgnoreCase(str))           return true;
		if("0".equalsIgnoreCase(str))           return true;
		return false;
	}
	
	public static boolean isOnline(Context ctx) {
	    ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    return false;
	}

	public static int getRandomNumber(int from, int to) {
            int rangeDiff = to - from;
            if( from > to ) {
                rangeDiff = from - to;
                from = to;
                to = from + rangeDiff;                
            }
            int rand = randomGenerator.nextInt(rangeDiff);
            return from + rand;
	}
}
