package com.dsinv.irefer2;

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

import com.dsinv.irefer2.R;
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

public class ActivationFormActivity extends Activity {

	TextView codeView;
    TextView emailView;
    
    String actCode = "";
    String uEmail = "";
    
	private DbAdapter	dba;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activation_form);
        setTitle( getString( R.string.app_name ) + " - App Activation Form");
        
        codeView = (TextView) findViewById(R.id.activationCode);
        emailView = (TextView) findViewById(R.id.actEmailText); 
     
        dba = new DbAdapter(this);
        dba.open();
        
        Button regBtn = (Button) findViewById(R.id.actSubmit);
        regBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				/* there should be only one user */
				//System.out.println("SMM::INFO:LNAME::["+lNameView.getText()+"]");
				if(codeView == null || codeView.getText() == null || codeView.getText().toString().equals("")) {
					Toast.makeText(ActivationFormActivity.this, "Please enter Activation Code.", Toast.LENGTH_SHORT).show();
					return;
				}
				if(emailView == null || emailView.getText() == null || emailView.getText().toString().equals("")) {
					Toast.makeText(ActivationFormActivity.this, "Please enter your email.", Toast.LENGTH_SHORT).show();
					return;
				} else if(!Utils.checkEmail(emailView.getText().toString())) {
					Toast.makeText(ActivationFormActivity.this, "Invalid email.", Toast.LENGTH_SHORT).show();
					return;
				}
				
				actCode = codeView.getText() == null ? "" : codeView.getText().toString();
		        uEmail = emailView.getText() == null ? "" : emailView.getText().toString();
		        
		        //Cursor cr = dba.fetchAll(dba.USERS);
		        //if(cr != null && cr.getCount() > 0) {
		        	//cr.moveToFirst();
		        	//int uId = cr.getInt(0);
		        	//String lName = cr.getString(2); 
		        	//String fName = cr.getString(3);
		        	//String pId = cr.getString(6);
		        	//if(uEmail.equals(cr.getString(4))) {
		        		try {
		        			String res = ""; 
				        	String urlString = ""; 
				        	urlString = ABC.WEB_URL+"user/activate?code="+actCode+"&email="+uEmail;
				        	int i=1;
				        	Log.d("NI","URL::"+urlString);
							res = getDataFromURL(urlString);
							Log.d("NI","JSONDATA::"+res);
							if(!res.equals("failed")){
					        	try {
					        		parseJSONDataAndSaveUser(res);
					        		
					        		Toast.makeText(ActivationFormActivity.this, "Thank You for registering " + getString( R.string.app_name ) , Toast.LENGTH_SHORT).show();
					        		System.out.println("SMM:REGISTERED::"+res);
					        		setResult(RESULT_OK);
					        		finish();
					        	} catch(Exception ex) {
					        		//Toast.makeText(ActivationFormActivity.this, res+"! Please try again with correct information.", Toast.LENGTH_SHORT).show();
					        		Log.e("NI","ERROR::"+ex.getMessage());
					        		//System.out.println("SMM:NOT REGISTERED::"+ex+res);
						        	ex.printStackTrace();
					        	}
							}
							else{
								Toast.makeText(ActivationFormActivity.this, "Activation failed, Please check email address and activation code.", Toast.LENGTH_SHORT).show();
							}
				        } catch(Exception ex) {
				        	Toast.makeText(ActivationFormActivity.this, "Failed to connect to server, Please check intenet setting.", Toast.LENGTH_SHORT).show();
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
    	JSONObject object1 = arr.getJSONObject(1);
    	JSONObject object2 = arr.getJSONObject(2);
    	
    	dba.delete(DbAdapter.USERS, 0);
    	dba.delete(DbAdapter.PRACTICE, 0);
    	dba.delete(DbAdapter.HOSPITAL, 0);
    	dba.delete(DbAdapter.COUNTY, 0);
    	
    	String pracId = object0.getString("my_practice_id");
    	if(pracId == null || pracId.equals("null")) pracId = "0";
    	
    	String docId = object0.getString("doc_id");
    	if(docId == null || docId.equals("null")) docId = "0";
    	
    	String hospId = object0.getString("my_hospital_id");
    	if(hospId == null || hospId.equals("null")) hospId = "0";
    	
    	String cntyId = object2.getString("id");
    	if(cntyId == null || cntyId.equals("null")) cntyId = "0";
    	
		long userId = dba.insert(DbAdapter.USERS, new String[]{object0.getString("id"), 
				object0.getString("last_name"), object0.getString("first_name"), object0.getString("email"),
				object0.getString("activation_code"),
				pracId, hospId, cntyId, "1", "1", "1", "0",docId});
		
		if(!pracId.equals("0")) {
			dba.insert(DbAdapter.PRACTICE, new String[]{object1.getString("id"), 
				object1.getString("name"), object1.getString("add_line_1") });
		} else if(!hospId.equals("0")) {
			dba.insert(DbAdapter.HOSPITAL, new String[]{object1.getString("id"), 
				object1.getString("name"), object1.getString("add_line_1") });
		}
		dba.insert(DbAdapter.COUNTY, new String[]{object2.getString("id"), 
			object2.getString("name"), object2.getString("county_code"), object2.getString("state_id") });
		
    	//dba.update(DbAdapter.USERS, uId, new String[]{"0", lName, fName, uEmail, actCode, pId, "0" });
	}
	
	public String getDataFromURL(String urlStr) throws Exception {
		System.out.println("SMM::URL::"+urlStr);
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
