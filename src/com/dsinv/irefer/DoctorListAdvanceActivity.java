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
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class DoctorListAdvanceActivity extends Activity {

	private DbAdapter	dba;
	private TextView textView; 
	private TextView footerView;
	private TextView filterView;
	private ArrayAdapter<String> autoCompleteAdapter;
    Object idArr[] = null;
    Object nameArr[] =  new Object[]{"no match found"};
    String userId = "0";
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_list_advance);
        setTitle( getString( R.string.app_name ) + " - Advance Specialist Search");
        
                
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
    	textView = (TextView)findViewById(R.id.doc_search_adv_text_edit);
    	footerView = (TextView)findViewById(R.id.doc_search_adv_footer);
    	
    	String docName = getIntent().getStringExtra("docName");
    	if(docName == null || "null".equals(docName)) docName = "";
    	
    	String zipCode = getIntent().getStringExtra("zipCode");
    	if(zipCode == null || "null".equals(zipCode)) zipCode = "";
    	
    	String insuranceIds = getIntent().getStringExtra("insuranceId");
    	if(insuranceIds == null || "null".equals(insuranceIds)) insuranceIds = null;
    	
    	String specialityIds = getIntent().getStringExtra("specialityId");
    	if(specialityIds == null || "null".equals(specialityIds)) specialityIds = null;
    	
    	String hospitalIds = getIntent().getStringExtra("hospitalId");
    	if(hospitalIds == null || "null".equals(hospitalIds)) hospitalIds = null;
    	
    	String countyIds = getIntent().getStringExtra("countyId");
    	if(countyIds == null || "null".equals(countyIds)) countyIds = null;
    	
    	userId = getIntent().getStringExtra("userId");
    	/*
    	filterView = (TextView)findViewById(R.id.filter_selected_values);
    	filterView.setMovementMethod(ScrollingMovementMethod.getInstance());
    	filterView.setHorizontallyScrolling(true);
    	filterView.setText("Speciality: "+getIntent().getStringExtra("spec")+
    			" | Insurance: "+getIntent().getStringExtra("insu")+
    			" | Hospital: "+getIntent().getStringExtra("hosp")+
    			" | County: "+getIntent().getStringExtra("cnty")+
    			" | Doctor: "+docName);
    	*/
    	//EditText.setAdapter(autoCompleteAdapter);
    	
    	//autoCompleteAdapter.add("Shamim");
    	List docList = new ArrayList();
    	
    	//faisal > modify (made the variable final to be used inside inner class)
    	final ListView itemListView = (ListView)findViewById(R.id.doctor_list_adv);
    	//itemListView.setOverScrollMode(View.OVER_SCROLL_ALWAYS); 
    	
    	Cursor cr = dba.searchDoctor(insuranceIds,specialityIds,hospitalIds,countyIds, docName, zipCode, "", "0, 100", 0);
    	//Cursor cr = dba.fetchAll(DbAdapter.DOCTOR);
    	if(cr != null) {
    		Map idMap = new HashMap();
    		idArr = new Object[cr.getCount()];
        	//System.out.println("SMM::DOCTOR::COUNT="+cr.getCount());
        	cr.moveToFirst();
        	for(int i=0; i<cr.getCount(); i++, cr.moveToNext()) {
        		if(idMap.get(cr.getInt(1)+"") != null)
            		continue;
            	idMap.put(cr.getInt(1)+"", new Integer(cr.getInt(1)));
            	
        		HashMap<String,String> temp = new HashMap<String,String>();
            	//temp.put("docPic","http://203.202.248.108/dsi/irefer3/css/bg.gif");
        		Cursor cr1 = dba.fetchByNetId(DbAdapter.PRACTICE, cr.getInt(11));
        		String pracName = "";
        		if(cr1 != null) {
        			cr1.moveToFirst();
        			if(cr1.getCount() > 0)
        				pracName = cr1.getString(2);
        			cr1.close();
        		}
            	idArr[i] = new Integer(cr.getInt(1));
            	temp.put("docTitile1", cr.getString(3)+" "+cr.getString(4)+" "+cr.getString(2)+", "+cr.getString(5));
            	temp.put("docTitile2", "Phone: "+cr.getString(6));
            	temp.put("docTitile3", pracName);
            	temp.put("docPhone", cr.getString(6));
            	temp.put("docId", cr.getInt(1)+"");
            	temp.put("userId", userId);
            	temp.put("grade", ""+cr.getInt(8));
            	temp.put("u_rank", Utils.isEmpty(""+cr.getInt(22)) ? "0" : ""+cr.getInt(22));
            	
        		docList.add(temp);
            		
        	}
        	cr.close();
        }

    	DoctorListAdapter adapter = new DoctorListAdapter(
        		this,
        		dba,
        		docList,
        		R.layout.doctor_row,
        		new String[] {"docId", "docTitile1","docTitile2","docTitile3"},
        		new int[] {R.id.doc_row_doc_id, R.id.doc_title1, R.id.doc_title2, R.id.doc_title3}
        		);
    	
        itemListView.setAdapter(adapter);
    	//itemListView.setAdapter(autoCompleteAdapter);
    	
    	
        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	    	
    		public void onItemClick(AdapterView<?> parent, View v, final int position, long id){
    			TextView docIdView = (TextView)v.findViewById(R.id.doc_row_doc_id);
    			Intent intent = new Intent(DoctorListAdvanceActivity.this, DoctorDetailActivity.class);
            	//intent.putExtra("doctor_id", (Integer)idArr[position]);
            	intent.putExtra("doctor_id", Integer.parseInt(docIdView.getText().toString()));
	            startActivityForResult(intent, 1100);	
            	/*
    			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
    		        public void onClick(DialogInterface dialog, int which) {
    		            switch (which){
    		            case DialogInterface.BUTTON_POSITIVE:
    		            	Intent intent = new Intent(DoctorListActivity.this, DoctorDetailActivity.class);
    		            	intent.putExtra("doctor_id", (Integer)idArr[position]);
    			            startActivityForResult(intent, 1100);	
    		            	break;
    		            case DialogInterface.BUTTON_NEGATIVE:
    		            	Intent sIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:+8801819479002"));
    		            	startActivity(sIntent);
    		                break;
    		            }
    		        }
    		    };
    			
    			AlertDialog.Builder builder = new AlertDialog.Builder(DoctorListActivity.this);
    		    builder.setMessage("Please Confirm your need").setPositiveButton("More Info", dialogClickListener)
    		        .setNegativeButton("Call Doctor", dialogClickListener).show();
    		  	*/	
    		}
	    });
        
    	final TextWatcher textChecker = new TextWatcher() {
    		 
	        public void afterTextChanged(Editable s) {
	        	textView.setEnabled(true);
	        }
	        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	        	textView.setEnabled(false);
	        }
	 
	        public void onTextChanged(CharSequence s, int start, int before, int count) {
	                autoCompleteAdapter.clear();
	                 
	                //Cursor cr = dba.fetchAll(dba.PRACTICE);
	                Cursor cr = dba.fetchDoctorFTS(s.toString());
	                
	                //faisal > added
	                List<Map<String,String>> data = new ArrayList<Map<String,String>>();
	                if(cr != null && cr.getCount() > 0) {
	                	Map idMap = new HashMap();
	                	idArr  = new Object[cr.getCount()];
	                	nameArr  = new Object[cr.getCount()];
	                	cr.moveToFirst();
	                	for (int i=0; i < cr.getCount(); i++,cr.moveToNext()) {
	                		if(idMap.get(cr.getInt(1)+"") != null)
	                    		continue;
	                    	idMap.put(cr.getInt(1)+"", new Integer(cr.getInt(1)));
	                    	
	                		idArr[i] = cr.getString(1);
	                		nameArr[i] = cr.getString(2)+" "+cr.getString(4)+" "+cr.getString(3);
		                    autoCompleteAdapter.add((String)nameArr[i]);
		                    HashMap<String,String> row = new HashMap<String,String>();
		                    Cursor cr1 = dba.fetchByNetId(DbAdapter.PRACTICE, cr.getInt(11));
		            		String pracName = "";
		            		if(cr1 != null) {
		            			cr1.moveToFirst();
		            			if(cr1.getCount() > 0)
		            				pracName = cr1.getString(2);
		            			cr1.close();
		            		}
		                	idArr[i] = new Integer(cr.getInt(1));
		                	row.put("docTitile1", cr.getString(3)+" "+cr.getString(4)+" "+cr.getString(2)+", "+cr.getString(5));
		                	row.put("docTitile2", "Phone: "+cr.getString(6));
		                	row.put("docTitile3", pracName);
		                	row.put("docPhone", cr.getString(6));
		                	row.put("docId", cr.getInt(1)+"");
		                	row.put("userId", userId);
		                	row.put("grade", ""+cr.getInt(8));
		                	row.put("u_rank", Utils.isEmpty(""+cr.getInt(22)) ? "0" : ""+cr.getInt(22));
		                	data.add(row);
		                	
		                	DoctorListAdapter adapter = new DoctorListAdapter(
		                			DoctorListAdvanceActivity.this,
		                    		dba,
		                    		data,
		                    		R.layout.doctor_row,
		                    		new String[] {"docId", "docTitile1","docTitile2","docTitile3"},
		                    		new int[] {R.id.doc_row_doc_id, R.id.doc_title1, R.id.doc_title2, R.id.doc_title3}
		                    		);
		                	
		                	itemListView.setAdapter(adapter);
		                    
		                }
	                	footerView.setText(cr.getCount()+" match found");
	                	cr.close();
	                } else {
	                	footerView.setText("No match founr");
	                }
	                System.out.println("SMM:INFO::"+nameArr.length);
	                textView.setFocusableInTouchMode(true);
	                textView.requestFocus();
	                InputMethodManager inputMethodManager = (InputMethodManager) DoctorListAdvanceActivity.this
	                        .getSystemService(DoctorListAdvanceActivity.this.INPUT_METHOD_SERVICE);
	                inputMethodManager.showSoftInput(textView, InputMethodManager.SHOW_IMPLICIT);
//	               
	        }
	    };
	    textView.addTextChangedListener(textChecker);
	    
//	    Button orderBtn = (Button) findViewById(R.id.order_flip);
//        orderBtn.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//			}
//		});
	    
    }
	
	//faisal > starts
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(data != null 
			&& data.getBooleanExtra("close_me", false) 
			&& requestCode == 1100 
			&& resultCode == RESULT_OK) {
			finish();	
		}		
	}
		//faisal > ends
		
	public Map<String, Object[]> parseJSONData(String jsonData) throws Exception {
		HashMap<String, Object[]> map = new HashMap<String, Object[]>();
		List<String> idList = new ArrayList<String>();
		List<String> nameList = new ArrayList<String>();
		JSONTokener jsonTokener = new JSONTokener(jsonData);
    	//JSONObject object = (JSONObject) jsonTokener.nextValue();
    	JSONArray arr = (JSONArray) jsonTokener.nextValue();
    	for(int i=0; i < arr.length(); i++) { 
    		JSONObject object = arr.getJSONObject(i);
    		String id   = object.getString("id");
    		String name = object.getString("name");
    		idList.add(id);
    		nameList.add(name);
    		//System.out.println("SMM:CODE::"+code);
    	}
    	map.put("idArr", idList.toArray());
    	map.put("nameArr", nameList.toArray());
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

