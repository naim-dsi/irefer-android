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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ItemAddActivity extends Activity {
	private String searchText = "";
	private DbAdapter	dba;
	private TextView textView; 
	private TextView footerView;
	private ArrayAdapter<String> autoCompleteAdapter;
    Object idArr[] = new Object[]{"0"};
    Object nameArr[] =  new Object[]{"no match found"};
    Object addArr[] =  new Object[]{""};
    Object extraArr[] =  new Object[]{""};
    int selectedItemIdx = -1;
    int opr = 0;
    int oprType = -1;
    protected Handler systemtaskHandler = new Handler();
	Runnable systemTaskRunner = new Runnable() {
		public void run()
        {
        	try
    		{
        		
        		autoCompleteAdapter.clear();
                
                String jsonData = "";
                try {
                	if(opr == DbAdapter.PRACTICE)
                		jsonData = getDataFromURL(ABC.WEB_URL+"practice/json?code="+searchText.toString());
                	if(opr == DbAdapter.HOSPITAL)
                		jsonData = getDataFromURL(ABC.WEB_URL+"hospital/json?code="+searchText.toString());
                	if(opr == DbAdapter.SPECIALTY)
                		jsonData = getDataFromURL(ABC.WEB_URL+"speciality/json?type=1&code="+searchText.toString());
                	if(opr == DbAdapter.INSURANCE)
                		jsonData = getDataFromURL(ABC.WEB_URL+"insurance/json?code="+searchText.toString());
                	if(opr == DbAdapter.COUNTY)
                		jsonData = getDataFromURL(ABC.WEB_URL+"county/json?code="+searchText.toString());
                	//System.out.println("SMM:INTERNET::"+ABC.WEB_URL+"practice/json?code="+s.toString());
                } catch(Exception ex) {
                	Toast.makeText(ItemAddActivity.this, "Failed to load data from intenet.", Toast.LENGTH_SHORT).show();
                	System.out.println("SMM:ERROR::"+ex);
                }
                
                //System.out.println("SMM:JSON::"+jsonData);

                try {
                	Map<String, Object[]> map = parseJSONData(jsonData);
                	nameArr = map.get("nameArr");
                	addArr = map.get("addArr");
                	idArr = map.get("idArr");
                	extraArr = map.get("extraArr");
                } catch(Exception ex) {
                	Toast.makeText(ItemAddActivity.this, "Failed to parse JSON data.", Toast.LENGTH_SHORT).show();
                	System.out.println(ex);
                	Log.d("JSON:ERROR", ex.getMessage(),ex);
                	//nameArr = new Object[]{"11","22"};
                }
                
                System.out.println("SMM:INFO::"+nameArr.length);
                footerView.setText("Add from list");
                
                for (int i=0; i < nameArr.length; i++) {
                     autoCompleteAdapter.add((String) nameArr[i]);
                        //System.out.println("SMM:INFO::"+nameArr[i]);
                }
                textView.setFocusableInTouchMode(true);
                textView.requestFocus();
                InputMethodManager inputMethodManager = (InputMethodManager) ItemAddActivity.this
                        .getSystemService(ItemAddActivity.this.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(textView, InputMethodManager.SHOW_IMPLICIT);
//                if(textView.requestFocus()) {
//                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//                }
                
        		
    		}
            catch(Exception ex)
    		{
            	ex.printStackTrace();
    			
    		}
        }
    };
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_add_list);
        
        Intent intent = getIntent();
        opr = this.getIntent().getIntExtra("opr", 0);
        oprType = this.getIntent().getIntExtra("opr_type", -1);
        
        if(opr ==  DbAdapter.PRACTICE)
        	setTitle( getString( R.string.app_name ) + " - Type to Add Practice");
        if(opr ==  DbAdapter.HOSPITAL)
        	setTitle( getString( R.string.app_name ) + " - Type to Add Hospital");
        if(opr ==  DbAdapter.SPECIALTY)
        	setTitle( getString( R.string.app_name ) + " - Type to Add Specialty");
        if(opr ==  DbAdapter.INSURANCE)
        	setTitle( getString( R.string.app_name ) + " - Type to Add Insurance");
        if(opr ==  DbAdapter.COUNTY)
        	setTitle( getString( R.string.app_name ) + " - Type to Add County");     
        dba = new DbAdapter(this);
        dba.open();
        /*
        Button regBtn = (Button) findViewById(R.id.pcpRegSubmit);
        regBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				long estId = dba.insert(DbAdapter.USERS, new String[]{"1", "02-08-2011"});
				try {
		        	String res = getDataFromURL(ABC.WEB_URL+"user/register?last_name=from_emo&first_name=check&email=www");
		        } catch(Exception ex) {
		        	Toast.makeText(ItemAddActivity.this, "Failed to send data from intenet.", Toast.LENGTH_SHORT).show();
		        	System.out.println("SMM:ERROR::"+ex);
		        }
				Toast.makeText(ItemAddActivity.this, "Your registration request submited. Please wait for mail.", Toast.LENGTH_SHORT).show();	
			}
		});
        */
        autoCompleteAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line); 
    	autoCompleteAdapter.setNotifyOnChange(true); // This is so I don't have to manually sync whenever changed 
    	textView = (TextView)findViewById(R.id.item_search_text_edit);
    	footerView = (TextView)findViewById(R.id.item_search_footer);
    	//EditText.setAdapter(autoCompleteAdapter);
    	
    	//autoCompleteAdapter.add("Shamim");
    	
    	ListView itemListView = (ListView)findViewById(R.id.itemList);
    	itemListView.setAdapter(autoCompleteAdapter);
    	
    	
    	final TextWatcher textChecker = new TextWatcher() {
    		 
	        public void afterTextChanged(Editable s) {
	        	//textView.setEnabled(true);
	        }
	        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	        	//textView.setEnabled(false);
	        }
	 
	        public void onTextChanged(CharSequence s, int start, int before, int count) {
	        	searchText = s.toString();
	        	systemtaskHandler.removeCallbacks( systemTaskRunner );
                systemtaskHandler.postDelayed( systemTaskRunner, 1500 );
	        	
	                
	        }
	    };
	    textView.addTextChangedListener(textChecker);

	    itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	    	
    		public void onItemClick(AdapterView<?> parent, View v, int position, long id){
    			selectedItemIdx = position;
    			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
    		        public void onClick(DialogInterface dialog, int which) {
    		            switch (which){
    		            case DialogInterface.BUTTON_POSITIVE:
    		            	Cursor cr = dba.fetchByNetId(opr, Long.parseLong((String)idArr[selectedItemIdx]));
    		            	if(cr != null ) {
    		            		if(cr.getCount() > 0) {
    		            			Toast.makeText(ItemAddActivity.this, "Already added.", Toast.LENGTH_SHORT).show();		
    		            			break;
    		            		}
    		            		cr.close();
    		            	}
    		            	if(opr == dba.SPECIALTY) {
    		            		dba.insert(opr, new String[]{(String)idArr[selectedItemIdx], (String)nameArr[selectedItemIdx],(String)addArr[selectedItemIdx], "0" });
    		            		dba.needToSync();
    		            	} else if(opr == dba.COUNTY){
    		            		dba.insert(opr, new String[]{(String)idArr[selectedItemIdx], (String)nameArr[selectedItemIdx],(String)addArr[selectedItemIdx], (String)extraArr[selectedItemIdx] });
    		            		dba.needToSync();
    		            	}else
    		            		dba.insert(opr, new String[]{(String)idArr[selectedItemIdx], (String)nameArr[selectedItemIdx],(String)addArr[selectedItemIdx] });
    		            	Toast.makeText(ItemAddActivity.this, (String)nameArr[selectedItemIdx]+" added.", Toast.LENGTH_SHORT).show();
    		            	//Yes button clicked
    		                break;

    		            case DialogInterface.BUTTON_NEGATIVE:
    		                //No button clicked
    		                break;
    		            }
    		        }
    		    };
    			
    			AlertDialog.Builder builder = new AlertDialog.Builder(ItemAddActivity.this);
    		    builder.setMessage("Are you sure to add this item?").setPositiveButton("Yes", dialogClickListener)
    		        .setNegativeButton("No", dialogClickListener).show();
    		  		
    		}
	    });
	    
	  //Added by faisal
	    Button allItemAddButton = (Button) findViewById(R.id.all_item_add_button);
	    allItemAddButton.setOnClickListener(new View.OnClickListener() {        	
	        public void onClick(View view) {	 
	        	int j=0;
	        	for(int i = 0;i<idArr.length;i++) {
	        		Cursor cr = dba.fetchByNetId(opr, Long.parseLong((String)idArr[i]));
	            	if(cr != null) {
	            		if(cr.getCount() > 0)   continue;;
	            		cr.close();
	            	}
	        		dba.insert(opr, new String[]{(String)idArr[i], (String)nameArr[i], "", "0" });
	        		j++;
	        	}
	        	Toast.makeText(ItemAddActivity.this, j + " Items added", Toast.LENGTH_SHORT).show();
	        }
        });
	    if(opr ==  DbAdapter.COUNTY){
	    	systemtaskHandler.removeCallbacks( systemTaskRunner );
	        systemtaskHandler.postDelayed( systemTaskRunner, 50 );
	    }
	    //Added by faisal up to this

    }
	
	public Map<String, Object[]> parseJSONData(String jsonData) throws Exception {
		//System.out.println("SMM::JSON::"+jsonData);
		HashMap<String, Object[]> map = new HashMap<String, Object[]>();
		List<String> idList = new ArrayList<String>();
		List<String> nameList = new ArrayList<String>();
		List<String> addList = new ArrayList<String>();
		List<String> extraList = new ArrayList<String>();
		JSONTokener jsonTokener = new JSONTokener(jsonData);
    	//JSONObject object = (JSONObject) jsonTokener.nextValue();
    	JSONArray arr = (JSONArray) jsonTokener.nextValue();
    	for(int i=1; i < arr.length(); i++) { 
    		JSONObject object = arr.getJSONObject(i);
    		String id   = object.getString("id");
    		String name = object.getString("name");
    		String address = (opr ==  DbAdapter.PRACTICE ? object.getString("add_line1") : "");
    		String extra = ((opr ==  DbAdapter.COUNTY && i>0) ? object.getString("state_id") : "0");
    		idList.add(id);
    		nameList.add(name);
    		addList.add(address);
    		extraList.add(extra);
    		//System.out.println("SMM:CODE::"+code);
    	}
    	map.put("idArr", idList.toArray());
    	map.put("nameArr", nameList.toArray());
    	map.put("addArr", addList.toArray());
    	map.put("extraArr", extraList.toArray());
    	return map;
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
