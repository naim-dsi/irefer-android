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
import com.dsinv.irefer.DbAdapter;
import com.dsinv.irefer.model.Doctor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class RegFormActivity extends Activity {

	Spinner userTypeSpinner;
	String strSpinner;
	private DbAdapter	dba;
	private AutoCompleteTextView autoComplete; 
	private ArrayAdapter<String> autoCompleteAdapter;
    Object idArr[] = null;
    Object nameArr[] =  new Object[]{""};
    Object addArr[] =  new Object[]{""};
    String selectedPracticeName = null;
    String selectedPracticeAddress = null;
    
    private int selectedPracticeIdx = -1;
    private int selectedPracticeId = -1;
    
    TextView lNameView;
    TextView fNameView;
    TextView uEmailView;
  
    String lName = "";
    String fName = "";
    String uEmail = "";
    
    int docId = 0;
    int pracId = 0;
    String pracName, pracAddress;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reg_form_prac);
        setTitle( getString( R.string.app_name ) + " - Online Registration Form");
        
        lNameView = (TextView) findViewById(R.id.pcpLastNameText);
        fNameView = (TextView) findViewById(R.id.pcpFirstNameText);
        uEmailView = (TextView) findViewById(R.id.pcpEmailText);
        
        selectedPracticeId = pracId = this.getIntent().getIntExtra("prac_id", 0);
        selectedPracticeName = pracName = this.getIntent().getStringExtra("prac_name");
        selectedPracticeAddress = pracAddress = this.getIntent().getStringExtra("prac_address");
        
        docId       = this.getIntent().getIntExtra("doc_id", 0);
        
        TextView headlineView1 = (TextView) findViewById(R.id.pcpRegHeadline1);
        TextView headlineView2 = (TextView) findViewById(R.id.pcpRegHeadline2);
        headlineView1.setText(pracName);
        headlineView2.setText(pracAddress);
        
        if(docId > 0) {
        	try {
        		Doctor doc = Utils.getDoctorById(docId);
        		lNameView.setText(doc.lastName);
        		fNameView.setText(doc.firstName);
        	} catch(Exception ex){
        		System.out.println("SMM::ERROR::"+ex);
        	}
        }
        
        /*
        userTypeSpinner = (Spinner)findViewById(R.id.pcp_user_type_spinner);
        ArrayAdapter<CharSequence> adapter1 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, nameArr);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userTypeSpinner.setAdapter(adapter1);

        userTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){
                        public void onNothingSelected(AdapterView<?> arg0) {
                        }
						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int arg2, long arg3) {
					        strSpinner = userTypeSpinner.getSelectedItem().toString();
                            Toast.makeText(PCPRegFormActivity.this, "You have selected"+strSpinner, Toast.LENGTH_SHORT).show();
							// TODO Auto-generated method stub
							
						}
        }); 
        */
        dba = new DbAdapter(this);
        dba.open();
        
        Button regBtn = (Button) findViewById(R.id.pcpRegSubmit);
        regBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				/* there should be only one user */
				System.out.println("SMM::INFO:LNAME::["+lNameView.getText()+"]");
				if(lNameView == null || lNameView.getText() == null || lNameView.getText().toString().equals("")) {
					Toast.makeText(RegFormActivity.this, "Please enter yout Last Name.", Toast.LENGTH_SHORT).show();
					return;
				}
				if(fNameView == null || fNameView.getText() == null || fNameView.getText().toString().equals("")) {
					Toast.makeText(RegFormActivity.this, "Please enter yout First Name.", Toast.LENGTH_SHORT).show();
					return;
				}
				if(uEmailView == null || uEmailView.getText() == null || uEmailView.getText().toString().equals("")) {
					Toast.makeText(RegFormActivity.this, "Please enter yout Email.", Toast.LENGTH_SHORT).show();
					return;
				} else if(!Utils.checkEmail(uEmailView.getText().toString())) {
					Toast.makeText(RegFormActivity.this, "Invalid email.", Toast.LENGTH_SHORT).show();
					return;
				}
				/*
				if(autoComplete== null || autoComplete.getText() == null || autoComplete.getText().toString().equals("")) {
					Toast.makeText(PCPRegFormActivity.this, "Please type your practice name.", Toast.LENGTH_SHORT).show();
					return;
				}
				if(selectedPracticeName == null || getPracticeIdByName(selectedPracticeName) == 0) {
					Toast.makeText(PCPRegFormActivity.this, "Please select your practice.", Toast.LENGTH_SHORT).show();
					return;
				}
				*/
				lName  = lNameView.getText() == null ? "" : lNameView.getText().toString();
		        fName  = fNameView.getText() == null ? "" : fNameView.getText().toString();
		        uEmail = uEmailView.getText() == null ? "" : uEmailView.getText().toString();
		        
				dba.delete(dba.USERS, 0);
				long userId = dba.insert(DbAdapter.USERS, new String[]{"0", lName, fName, uEmail, "", "0", new Integer(selectedPracticeId).toString() , "0", "1", "1"});
				dba.delete(dba.HOSPITAL, 0);
				long pId = dba.insert(DbAdapter.HOSPITAL, new String[]{new Integer(selectedPracticeId).toString(), selectedPracticeName , selectedPracticeAddress});
				try {
		        	String res = getDataFromURL(ABC.WEB_URL+"user/register?last_name="+lName+"&first_name="+fName+"&email="+uEmail+"&hosp_id="+selectedPracticeId);
		        } catch(Exception ex) {
		        	Toast.makeText(RegFormActivity.this, "Failed to connect to server, Please check intenet setting.", Toast.LENGTH_SHORT).show();
		        	System.out.println("SMM:ERROR::"+ex);
		        }
				Toast.makeText(RegFormActivity.this, "Your registration request submited. Please wait for mail.", Toast.LENGTH_SHORT).show();	
				finish();
			}
		});
        /*
        autoCompleteAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line); 
        autoCompleteAdapter.setNotifyOnChange(true); // This is so I don't have to manually sync whenever changed 
    	autoComplete = (AutoCompleteTextView)findViewById(R.id.practice_search_text_edit);
    	autoComplete.setAdapter(autoCompleteAdapter);
    	autoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				selectedPracticeName = (String)arg0.getItemAtPosition(arg2);
				selectedPracticeIdx = arg2;
				selectedPracticeId = Integer.parseInt((String)idArr[arg2]);
				selectedPracticeAddress = (String)addArr[arg2];
				System.out.println("SMM:SELECTED_PCP::"+selectedPracticeId);
				System.out.println("SMM:SELECTED_COUNT::"+arg0.getChildCount());
			}
    		
		});
    	
    	final TextWatcher textChecker = new TextWatcher() {
    		 
	        public void afterTextChanged(Editable s) {
	        	autoComplete.setEnabled(true);
	        }
	        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	        	autoComplete.setEnabled(false);
	        }
	 
	        public void onTextChanged(CharSequence s, int start, int before, int count) {
	                autoCompleteAdapter.clear();
	                
	                String jsonData = "";
	                try {
	                	jsonData = getDataFromURL(ABC.WEB_URL+"practice/json?code="+s.toString());
	                } catch(Exception ex) {
	                	Toast.makeText(PCPRegFormActivity.this, "Failed to load data from intenet.", Toast.LENGTH_SHORT).show();
	                	System.out.println("SMM:ERROR::"+ex);
	                }
	                
	                //System.out.println("SMM:JSON::"+jsonData);

	                try {
	                	Map<String, Object[]> map = parseJSONData(jsonData);
	                	nameArr = map.get("nameArr");
	                	addArr = map.get("addArr");
	                	idArr = map.get("idArr");
	                } catch(Exception ex) {
	                	Toast.makeText(PCPRegFormActivity.this, "Failed to parse JSON data.", Toast.LENGTH_SHORT).show();
	                	System.out.println(ex);
	                	Log.d("JSON:ERROR", ex.getMessage(),ex);
	                	//nameArr = new Object[]{"11","22"};
	                }
	                
	                if(nameArr == null || nameArr.length == 0) {
	                	nameArr = new Object[]{"No match found"};
	                }
	                
	                System.out.println("SMM:INFO::"+nameArr.length);
	                for (int i = 0; i < nameArr.length; i++) {
	                        autoCompleteAdapter.add((String) nameArr[i]);
	                        //System.out.println("SMM:INFO::"+nameArr[i]);
	                }
	        }
	    };
	    autoComplete.addTextChangedListener(textChecker);
	    */
    }
	
	private int getPracticeIdByName(String name) {
		try {
			for(int i=0; i < nameArr.length; i++) {
				if(name.equals((String)nameArr[i]))
					return Integer.parseInt((String)idArr[i]);
			}
		} catch(Exception ex){/* return 0 */}
		return 0;
	}
	
	public Map<String, Object[]> parseJSONData(String jsonData) throws Exception {
		HashMap<String, Object[]> map = new HashMap<String, Object[]>();
		List<String> idList = new ArrayList<String>();
		List<String> nameList = new ArrayList<String>();
		List<String> addList = new ArrayList<String>();
		JSONTokener jsonTokener = new JSONTokener(jsonData);
    	//JSONObject object = (JSONObject) jsonTokener.nextValue();
    	JSONArray arr = (JSONArray) jsonTokener.nextValue();
    	for(int i=0; i < arr.length(); i++) { 
    		JSONObject object = arr.getJSONObject(i);
    		String id   = object.getString("id");
    		String name = object.getString("name");
    		String address = object.getString("add_line1");
    		idList.add(id);
    		nameList.add(name);
    		addList.add(address);
    		//System.out.println("SMM:CODE::"+name+" <> FOR ID="+id);
    	}
    	map.put("idArr", idList.toArray());
    	map.put("nameArr", nameList.toArray());
    	map.put("addArr", addList.toArray());
    	return map;
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
