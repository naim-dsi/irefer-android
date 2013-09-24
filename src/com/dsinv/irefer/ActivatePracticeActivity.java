package com.dsinv.irefer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.dsinv.irefer.R;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ActivatePracticeActivity extends Activity {

	TextView codeView;
    TextView emailView;
    
    String actCode = "";
    String userId = "";
    String pracId = "";
    String pracName = "";
    
	private DbAdapter	dba;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activate_practice);
        setTitle( getString( R.string.app_name ) + " - Practice Activation Form");
        
        userId = getIntent().getStringExtra("userId");
        pracId = getIntent().getStringExtra("pracId");
        pracName = getIntent().getStringExtra("pracName");
    	
        codeView = (TextView) findViewById(R.id.practiceActivationCode);
        TextView label1 = (TextView) findViewById(R.id.practiceActivationLabel1);
        label1.setText(pracName);
        
        dba = new DbAdapter(this);
        dba.open();
        
        Button regBtn = (Button) findViewById(R.id.practiceActSubmit);
        regBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				/* there should be only one user */
				//System.out.println("SMM::INFO:LNAME::["+lNameView.getText()+"]");
				if(codeView == null || codeView.getText() == null || codeView.getText().toString().equals("")) {
					Toast.makeText(ActivatePracticeActivity.this, "Please enter Activation Code.", Toast.LENGTH_SHORT).show();
					return;
				}
				
				actCode = codeView.getText() == null ? "" : codeView.getText().toString();
		        try {
				       	String res = getDataFromURL(ABC.WEB_URL+"paUser/activate?code="+actCode+"&user_id="+userId+
				       			"&prac_id="+pracId);
				       	try {
				       		Toast.makeText(ActivatePracticeActivity.this, res, Toast.LENGTH_SHORT).show();
				       		//System.out.println("SMM:REGISTERED::"+res);
				        	finish();
				       	} catch(Exception ex) {
				       		Toast.makeText(ActivatePracticeActivity.this, res+"! Please try again with correct information.", Toast.LENGTH_SHORT).show();
				        	System.out.println("SMM:NOT REGISTERED::"+ex+res);
				       	}
				 } catch(Exception ex) {
				       	Toast.makeText(ActivatePracticeActivity.this, "Failed to connect to server, Please check intenet setting.", Toast.LENGTH_SHORT).show();
				       	System.out.println("SMM:ERROR::"+ex);
				 }
		        	//} else {
		        		//Toast.makeText(ActivationFormActivity.this, "Info mismatch, Please try again with correct information", Toast.LENGTH_SHORT).show();
			        	//System.out.println("SMM:EMAIL MISMATCH::"+uEmail);
		        	//}
		        //}
				
			}
        });
    }
	
	public void parseJSONDataAndSaveUser(String jsonData) throws Exception {
		
		JSONTokener jsonTokener = new JSONTokener(jsonData);
		JSONArray arr = (JSONArray) jsonTokener.nextValue();
    	JSONObject object0 = arr.getJSONObject(0);// (JSONObject) jsonTokener.nextValue();
    
    	//String pracId = object0.getString("my_practice_id");
    	//if(pracId == null || pracId.equals("null")) pracId = "0";
    	
    	//dba.update(DbAdapter.PRACTICE, pId, new String[]{"0", lName, fName, uEmail, actCode, pId, "0" });
	}
	
	public String getDataFromURL(String urlStr) throws Exception {
	    URL url = new URL(urlStr);
	    URLConnection urlCon = url.openConnection();
	    BufferedReader in = new BufferedReader(
	                            new InputStreamReader(
	                            urlCon.getInputStream()));
	    String data = "";
	    String line = "";
	    
        while ((line = in.readLine()) != null)
        	data += line;
            //System.out.println(inputLine);
	    in.close();
	    return data;
    }
	
}
