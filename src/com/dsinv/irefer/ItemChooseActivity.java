package com.dsinv.irefer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
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
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ItemChooseActivity extends Activity {

	//private DbAdapter	dba;
	private TextView textView; 
	//private TextView footerView;
	private Button newBtn;
	private ArrayAdapter<String> autoCompleteAdapter;
	private SimpleAdapter simpleAdapter;
	ListView itemListView;
	private String searchText = "";
	Object idArr[] = new Object[]{"0"};
    Object nameArr[] =  new Object[]{"no match found"};
    Object addArr[] =  new Object[]{""};
    int selectedItemIdx = -1;
    int opr = 0;
    int userType = 0;
    int _id = 0;
    String _name, _address;
    List<Map<String,String>> docList = new ArrayList<Map<String,String>>();
    List docList2;
    String userId = "";
    
    ProgressDialog dialog;
    protected Handler systemtaskHandler = new Handler();
   	Runnable systemTaskRunner = new Runnable() {
   		public void run()
           {
           	try
       		{
	           	 populateListData(searchText);
		    	    textView.setFocusableInTouchMode(true);
	             textView.requestFocus();
	             InputMethodManager inputMethodManager = (InputMethodManager) ItemChooseActivity.this
	                     .getSystemService(ItemChooseActivity.this.INPUT_METHOD_SERVICE);
	             inputMethodManager.showSoftInput(textView, InputMethodManager.SHOW_IMPLICIT);
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
        setContentView(R.layout.item_choose_list);
        
        //Intent intent = getIntent();
        opr      = this.getIntent().getIntExtra("opr", 0);
        userType = this.getIntent().getIntExtra("user_type", 0);
        _id      = this.getIntent().getIntExtra("_id", 0);
        _name    = this.getIntent().getStringExtra("_name");
        _address = this.getIntent().getStringExtra("_address");
        userId = getIntent().getStringExtra("userId");
        
        newBtn = (Button) findViewById(R.id.createNewBtn);
        TextView keyView = (TextView)findViewById(R.id.item_chose_text_edit);
        
        if(opr ==  DbAdapter.PRACTICE) {
        	setTitle( getString( R.string.app_name ) + " - Choose your Practice");
        	newBtn.setVisibility(View.GONE);
        } else if(opr ==  DbAdapter.HOSPITAL) {
        	setTitle( getString( R.string.app_name ) + " - Choose your Hospital");
        	newBtn.setVisibility(View.GONE);
        } else if(opr ==  DbAdapter.SPECIALTY) {
        	setTitle( getString( R.string.app_name ) + " - Choose your Speciality");
        	newBtn.setVisibility(View.GONE);
        } else if(opr ==  DbAdapter.DOCTOR) {
        	setTitle("Choose your Name");
        	newBtn.setVisibility(View.VISIBLE);
        	keyView.setHint("Enter your last name");
        	newBtn.setText("Click, if you don�t find your name");
        }
        
        //dba = new DbAdapter(this);
        //dba.open();
        itemListView = (ListView)findViewById(R.id.itemChoseList);
    	
        if(opr == DbAdapter.DOCTOR) { 
        	simpleAdapter = new SimpleAdapter(this,
        			docList,
        			R.layout.doctor_row_lite,
        			new String[] {"docTitile1","docTitile2"},
        			new int[] {R.id.doc_lite_title1, R.id.doc_lite_title2});
        	simpleAdapter.notifyDataSetChanged();
        	//simpleAdapter.setNotifyOnChange(true); // This is so I don't have to manually sync whenever changed 
        	itemListView.setAdapter(simpleAdapter);
        } else {	
        	autoCompleteAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line); 
        	autoCompleteAdapter.setNotifyOnChange(true); // This is so I don't have to manually sync whenever changed 
        	itemListView.setAdapter(autoCompleteAdapter);	
        }
        textView = (TextView)findViewById(R.id.item_chose_text_edit);
    	//footerView = (TextView)findViewById(R.id.item_chose_footer);
    	
    	
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
//                
	    	    
	        }
	    };
	    textView.addTextChangedListener(textChecker);

	    itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	    	
    		public void onItemClick(AdapterView<?> parent, View v, int position, long id){
    			selectedItemIdx = position;
    			if(opr == DbAdapter.PRACTICE) {
    				Intent intent = new Intent(ItemChooseActivity.this, ItemChooseActivity.class);
    				intent.putExtra("opr", DbAdapter.SPECIALTY);
    				intent.putExtra("user_type", 1);
    				intent.putExtra("_id", Integer.parseInt((String)idArr[selectedItemIdx]));
    				intent.putExtra("_name", (String)nameArr[selectedItemIdx]);
    				intent.putExtra("_address", (String)addArr[selectedItemIdx]);
    				System.out.println("SMM::PRAC_ID::"+(String)idArr[selectedItemIdx]);
    				startActivityForResult(intent, 1100);
    			} else if(opr == DbAdapter.HOSPITAL) {
    				Intent intent = new Intent(ItemChooseActivity.this, ItemChooseActivity.class);
    				intent.putExtra("opr", DbAdapter.SPECIALTY);
    				intent.putExtra("user_type", 2);
    				intent.putExtra("_id", Integer.parseInt((String)idArr[selectedItemIdx]));
    				intent.putExtra("_name", (String)nameArr[selectedItemIdx]);
    				intent.putExtra("_address", (String)addArr[selectedItemIdx]);
    				System.out.println("SMM::PRAC_ID::"+(String)idArr[selectedItemIdx]);
    				startActivityForResult(intent, 1100);
    				//Intent intent = new Intent(ItemChooseActivity.this, RegFormActivity.class);
    				//intent.putExtra("prac_id", Integer.parseInt((String)idArr[selectedItemIdx+1]));
    				//intent.putExtra("prac_name", (String)nameArr[selectedItemIdx+1]);
    				//intent.putExtra("prac_address", (String)addArr[selectedItemIdx+1]);
    				//System.out.println("SMM::HOSP_ID::"+(String)idArr[selectedItemIdx]);
    				//startActivityForResult(intent, 1100);
    			} else if(opr == DbAdapter.SPECIALTY) {
    				Intent intent = new Intent(ItemChooseActivity.this, PCPRegFormActivity.class);
    				intent.putExtra("user_type", userType);
    				intent.putExtra("prac_id", _id);
    				intent.putExtra("prac_name", _name);
    				intent.putExtra("prac_address", _address);
    				intent.putExtra("spec_id", Integer.parseInt((String)idArr[selectedItemIdx]));
    				intent.putExtra("spec_name", (String)nameArr[selectedItemIdx]);
    				System.out.println("SMM::SPEC_ID::"+(String)idArr[selectedItemIdx]);
    				startActivityForResult(intent, 1100);
    			} else if(opr == DbAdapter.DOCTOR) {
    				Intent intent = new Intent(ItemChooseActivity.this, PCPRegFormActivity.class);
    				try {
    					JSONObject obj = (JSONObject)docList2.get(selectedItemIdx);
    					intent.putExtra("user_type", userType);
    					intent.putExtra("doc_id", obj.getInt("id"));
    					intent.putExtra("prac_id", obj.getInt("prac_id"));
        				//System.out.println("SMM::PRAC_ID="+obj.getInt("prac_id"));
        				intent.putExtra("prac_name", obj.getString("prac_name"));
        				intent.putExtra("prac_address", obj.getString("add_line_1"));
    				} catch(Exception ex){
    					System.out.println("SMM::ERROR::"+ex);	
    				}
        			intent.putExtra("doc_id", Integer.parseInt((String)idArr[selectedItemIdx]));
    				System.out.println("SMM::DOC_ID::"+(String)idArr[selectedItemIdx]);
    				startActivityForResult(intent, 1100);
    			} 
    		  		
    		}
	    });
	    
	    newBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(ItemChooseActivity.this, ItemChooseActivity.class);
				if(userType == 1)
					intent.putExtra("opr", DbAdapter.PRACTICE);
				if(userType == 2)
					intent.putExtra("opr", DbAdapter.HOSPITAL);
				//intent.putExtra("_id", _id);
				//intent.putExtra("_name", _name);
				//intent.putExtra("_address", _address);
				startActivityForResult(intent, 1100);
				/*
				Intent intent = new Intent(ItemChooseActivity.this, PCPRegFormActivity.class);
				intent.putExtra("prac_id", _id);
				intent.putExtra("prac_name", _name);
				intent.putExtra("prac_address", _address);
				startActivityForResult(intent, 1100);
				*/
			}
		});
    }
	
	@Override
    public void onStart(){   
		super.onStart();
		/*
		dialog = ProgressDialog.show(ItemChoseActivity.this, "", 
            "Downloading. Please wait...", true);
		dialog.show();
	
		Thread background = new Thread (new Runnable() {
           public void run() {
        	   //Looper.prepare();
        	   try {
        		   populateListData("");
        	   } catch (Exception ex){
        		   
        	   } finally{
        		   dialog.dismiss();
        	   }
           }
		});
	
		background.start();
		*/
	}
	
	private void populateListData(String s) {
		
       
        try {
        	 String urlString = "";
             String jsonData = "";
			 s = URLEncoder.encode(s, "UTF-8");
			 s=s.replace("+", "%20");
		
	        try {
	         	if(opr == DbAdapter.PRACTICE)
	         		urlString = ABC.WEB_URL+"practice/json?code="+s;
	         	if(opr == DbAdapter.HOSPITAL)
	         		urlString = ABC.WEB_URL+"hospital/json?code="+s;
	         	if(opr == DbAdapter.SPECIALTY){
	         		if(userType == 1)
	         			urlString = ABC.WEB_URL+"speciality/json?type=2&code="+s;
	         		if(userType == 2)
	         			urlString = ABC.WEB_URL+"speciality/json?type=1&code="+s;
	         	}
	         	if(opr == DbAdapter.DOCTOR) {
	         		if(userType == 1)
	         			urlString = ABC.WEB_URL+"doctor/jsonLite?prac_ids=1&doc_name="+s;
	         			
	         		if(userType == 2)
	         			urlString = ABC.WEB_URL+"doctor/jsonLite?hosp_ids=1&doc_name="+s;
	         	}
	         	Log.d("NI","URL::"+urlString);
	         	//urlString = java.net.URLEncoder.encode(urlString);
	         	//Log.d("NI","Encoded URL::"+urlString);
	         	jsonData = getDataFromURL(urlString);
				Log.d("NI","JSONDATA::"+jsonData);
	         	
	         		
	         } catch(Exception ex) {
	         	Toast.makeText(ItemChooseActivity.this, "Failed to load data from intenet.", Toast.LENGTH_SHORT).show();
	         	System.out.println("SMM:ERROR::"+ex);
	         }
	     
	        //System.out.println("SMM222:JSON::"+jsonData);
	        docList = new ArrayList<Map<String,String>>();
	         try {
	         	Map<String, Object[]> map = parseJSONData(jsonData);
	         	nameArr = map.get("nameArr");
	         	addArr = map.get("addArr");
	         	idArr = map.get("idArr");
	         } catch(Exception ex) {
	         	Toast.makeText(ItemChooseActivity.this, "Failed to parse JSON data.", Toast.LENGTH_SHORT).show();
	         	System.out.println(ex);
	         	Log.d("JSON:ERROR", ex.getMessage(),ex);
	         	//nameArr = new Object[]{"11","22"};
	         }
	         System.out.println("SMM111:DOC-LENGTH::"+docList.size());
	         if(opr == DbAdapter.DOCTOR) {
	         	simpleAdapter = new SimpleAdapter(this,
	         			docList,
	         			R.layout.doctor_row_lite,
	         			new String[] {"docTitile1","docTitile2"},
	         			new int[] {R.id.doc_lite_title1, R.id.doc_lite_title2});
	         	//simpleAdapter.setNotifyOnChange(true); // This is so I don't have to manually sync whenever changed 
	         	simpleAdapter.notifyDataSetChanged();
	         	itemListView.setAdapter(simpleAdapter);
	         	return;
	         }
	         System.out.println("SMM:JSON-LENGTH::"+nameArr.length);
	         //footerView.setText((String)nameArr[0]);
	         autoCompleteAdapter.clear();
	         int i = 0;//(opr == DbAdapter.DOCTOR ? 0 : 1);
	         for (; i < nameArr.length; i++) {
	              autoCompleteAdapter.add((String) nameArr[i]);
	                 //System.out.println("SMM:INFO::"+nameArr[i]);
	         }
        } catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Map<String, Object[]> parseJSONData(String jsonData) throws Exception {
		HashMap<String, Object[]> map = new HashMap<String, Object[]>();
		List<String> idList = new ArrayList<String>();
		List<String> nameList = new ArrayList<String>();
		List<String> addList = new ArrayList<String>();
		docList2 = new ArrayList();
		JSONTokener jsonTokener = new JSONTokener(jsonData);
    	//JSONObject object = (JSONObject) jsonTokener.nextValue();
    	JSONArray arr = (JSONArray) jsonTokener.nextValue();
    	for(int i=1; i < arr.length(); i++) { 
    		JSONObject object = arr.getJSONObject(i);
    		String id   = object.getString("id");
    		String name = object.getString("name");
    		String address = (opr ==  DbAdapter.PRACTICE || opr ==  DbAdapter.HOSPITAL ? object.getString("add_line1") : "");
    		idList.add(id);
    		nameList.add(name);
    		addList.add(address);
    		
    		if(opr == DbAdapter.DOCTOR) {     	
    			String pracName = object.getString("prac_name");
    			Long pracId = object.getLong("prac_id");
    			Long cntyId = object.getLong("county_id");
    			address = object.getString("add_line_1");
    			addDocToList(name, pracName, address, pracId, cntyId);
    			docList2.add(object);
    		}
    		//System.out.println("SMM:CODE::"+name);
    	}
    	map.put("idArr", idList.toArray());
    	map.put("nameArr", nameList.toArray());
    	map.put("addArr", addList.toArray());
    	return map;
	}
	
	private void addDocToList(String name, String pracName, String pracAdd, Long pracId, Long cntyId) {
		HashMap<String,String> temp = new HashMap<String,String>();
		temp.put("docTitile1", name);
		temp.put("docTitile2", pracName);
		temp.put("pracAdd", pracAdd);
		temp.put("pracId", pracId.toString());
		temp.put("cntyId", cntyId.toString());
		docList.add(temp);
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
