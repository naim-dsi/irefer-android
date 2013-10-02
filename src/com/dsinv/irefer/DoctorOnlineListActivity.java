package com.dsinv.irefer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.dsinv.irefer.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class DoctorOnlineListActivity extends Activity {
	
	private TextView textView; 
	private TextView footerView;
	private TextView filterView;
	private ListView itemListView;
	private ArrayAdapter<String> autoCompleteAdapter;        
    private ProgressDialog dialog;
    private List<JSONObject> docJsonList = new ArrayList<JSONObject>();
    
	private String hospIds = "";   
	private String specIds = "";   
	private String insuIds = "";   
	private String cntyIds = "";
	private String zip = "";
	private String userId = "";
	List docList = null;
    DoctorListAdapter adapter;
	//List<Map<String,String>> data;
	
	private Integer ONLINE_LIST_REQUEST_CODE = 1100;
        		         
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //System.out.println("---------------------");
        initializeFilterIDs();
        setContentView(R.layout.doctor_list);
        setTitle( getString( R.string.app_name ) + " - Specialist Search");                        
        
        autoCompleteAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line); 
    	autoCompleteAdapter.setNotifyOnChange(true); 
    	textView = (TextView)findViewById(R.id.doc_search_text_edit);
    	footerView = (TextView)findViewById(R.id.doc_search_footer);
    	View pBar = (View)findViewById(R.id.filter_top_wrapper);
    	pBar.setVisibility(View.GONE);
    	/*
    	filterView = (TextView)findViewById(R.id.filter_selected_values);
    	String filterStr = getFilterStr("Specialty", getIntent().getStringExtra("spec"));
    	filterStr += " | "+getFilterStr("Inurance", getIntent().getStringExtra("insu"));
    	filterStr += " | "+getFilterStr("Hospital", getIntent().getStringExtra("hosp"));
    	filterStr += " | "+getFilterStr("County", getIntent().getStringExtra("cnty"));
    	filterView.setText(filterStr);
    	*/
    	String docName = getIntent().getStringExtra("docName");
    	if(docName == null || "null".equals(docName)) docName = "";
    	
    	itemListView = (ListView)findViewById(R.id.doctor_list);
    	
    	dialog = ProgressDialog.show(DoctorOnlineListActivity.this, "", "loading data ...", true);     	
    	new DownloadDoctorTask().execute();
    	
    						    	    	
        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	
    		public void onItemClick(AdapterView<?> parent, View v, final int position, long id){
    			Intent intent = new Intent(DoctorOnlineListActivity.this, DoctorDetailActivity.class);
            	intent.putExtra("doctor_id", Integer.parseInt((String)((Map)docList.get(position)).get("id")));
            	intent.putExtra("user_id", userId);
            	intent.putExtra("is_online_search", true);
                startActivityForResult(intent, ONLINE_LIST_REQUEST_CODE);	
                /*
    			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
    		        public void onClick(DialogInterface dialog, int which) {
    		            switch (which){
    		            case DialogInterface.BUTTON_POSITIVE:
    		            	
    		            	break;
    		            case DialogInterface.BUTTON_NEGATIVE:
    		            	Intent sIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:+8801819479002"));
    		            	startActivity(sIntent);
    		                break;
    		            }
    		        }
    		    };
    			
    			AlertDialog.Builder builder = new AlertDialog.Builder(DoctorOnlineListActivity.this);
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
	                new DownloadDoctorTask().execute(s);
	                textView.setFocusableInTouchMode(true);
	                textView.requestFocus();
	                InputMethodManager inputMethodManager = (InputMethodManager) DoctorOnlineListActivity.this
	                        .getSystemService(DoctorOnlineListActivity.this.INPUT_METHOD_SERVICE);
	                inputMethodManager.showSoftInput(textView, InputMethodManager.SHOW_IMPLICIT);
//	                
	        }
	    };
	    textView.addTextChangedListener(textChecker);
	    
//	    Button orderBtn = (Button) findViewById(R.id.order_flip);
//        orderBtn.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				//sort the list.
//			}
//		});	    
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.doc_list_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.sort_by_grade:
	        sortByGrade();
	        return true;
	    case R.id.sort_by_rank:
	        sortByRank();
	        return true;
	    case R.id.sort_by_name:
	        sortByName();
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	private void sortByGrade(){
		Collections.sort(docList, new ListOfMapComparator("grade", true));
		itemListView.setAdapter(adapter);
	}
	
	private void sortByRank(){
		Collections.sort(docList, new ListOfMapComparator("u_rank", true));
		itemListView.setAdapter(adapter);
	}

	private void sortByName(){
		Collections.sort(docList, new ListOfMapComparator("docTitile1"));
		itemListView.setAdapter(adapter);
	}
	
	private String getFilterStr(String label, String str) {
		if("None".equalsIgnoreCase(str) || "All".equalsIgnoreCase(str) || "NA".equalsIgnoreCase(str))
	    		return label+": All";
	    	else
	    		return str;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {		
		if(data != null 
				&& data.getBooleanExtra("close_me", false) 
				&& requestCode == ONLINE_LIST_REQUEST_CODE 
				&& resultCode == RESULT_OK) {
			finish();	
		}		
	}
	
    private void parseJSONData(String jsonData) throws Exception {
		docJsonList.clear();
		JSONTokener jsonTokener = new JSONTokener(jsonData);
    	JSONArray arr = (JSONArray) jsonTokener.nextValue();
    	for(int i=0; i < arr.length(); i++) { 
    		JSONObject object = arr.getJSONObject(i);
    		docJsonList.add(object);    		
    	}
	}
	
	private String getDataFromURL(String urlStr) throws Exception {
		System.out.println("SMM::URL::"+urlStr);
	    URL url = new URL(urlStr);
	    URLConnection urlCon = url.openConnection();
	    BufferedReader in = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
	    String data = "";
	    String line = "";	    
        while ((line = in.readLine()) != null) {
        	data += line;
        }
	    in.close();
	    return data;
    }
	
	private void downloadDoctors(CharSequence s) throws Exception {
		String jsonData = "";
		//System.out.println("SMM::DOC-SYNC::URL::"+ABC.WEB_URL+"doctor/json?prac_ids=1&insu_ids="+insuIds+
		//			"&spec_ids="+specIds+"&hosp_ids="+hospIds+"&limit="+"0,100");
		
		//jsonData = getDataFromURL(ABC.WEB_URL+"doctor/json?prac_ids=1&doc_name=" + (s == null ? "" : s.toString() ) );
		jsonData = getDataFromURL(ABC.WEB_URL+"doctor/json?prac_ids=1&limit=0,50&insu_ids=" + insuIds +
				"&spec_ids=" + specIds + "&hosp_ids=" + hospIds +"&user_id="+userId+"&zip="+zip+"&doc_name=" + (s == null ? "" : s.toString() ) );		
		
		parseJSONData(jsonData);
		
	}
	
	private void initializeFilterIDs() {		
		hospIds = getIntent().getStringExtra("hospitalId");		
		specIds = getIntent().getStringExtra("specialityId");   
		insuIds = getIntent().getStringExtra("insuranceId"); 
		cntyIds = getIntent().getStringExtra("countyId");
		userId  = getIntent().getStringExtra("userId");
		zip     = Utils.isEmpty(getIntent().getStringExtra("zip")) ? "" : getIntent().getStringExtra("zip");
		
	}
	
	private void prepareDataForAdapter() {
		//List<Map<String, String>> l = new ArrayList<Map<String, String>>();
		docList = new ArrayList();
		try {
			Map idMap = new HashMap();
			for(JSONObject obj : docJsonList) {
				if(idMap.get(obj.getString("id")) != null)
            		continue;
            	idMap.put(obj.getString("id"),obj.getString("id"));
            	
				Map<String, String> m = new HashMap<String, String>();
				m.put("id", obj.getString("id"));
				m.put("docTitile1", obj.getString("c2") +" " + 
						(Utils.isEmpty(obj.getString("c3")) ? "" : obj.getString("c3")+" ") + 						
						obj.getString("c1"));
				m.put("docTitile2", "Degree: " + obj.getString("c4") + " Phone:" + obj.getString("c5"));
				m.put("docTitile3", "Grade: " + obj.getString("c7") + " Language:" + obj.getString("c6"));
				m.put("docPhone", obj.getString("c5"));
            	m.put("docId", obj.getString("id"));
            	m.put("userId", userId);
            	m.put("u_rank", Utils.isEmpty(obj.getString("u_rank")) ? "0" : obj.getString("u_rank"));
				docList.add(m);
				//System.out.println("SMM::"+m.get("docTitile1")+"::RANK:"+m.get("u_rank"));
			}
		}catch(Exception e) {
			//Toast.makeText(DoctorOnlineListActivity.this, "data parsing error ...", Toast.LENGTH_SHORT).show();
			System.out.println("data parsing error ...");
			e.printStackTrace();
		}
		//return l;
	}
	
	private class DownloadDoctorTask extends AsyncTask<CharSequence, Integer, Integer> {
		private final Integer STATUS_OK = 0;
		private final Integer STATUS_ERROR = 1;
		
		@Override
		protected Integer doInBackground(CharSequence...params) {			
			try {
				downloadDoctors(params.length == 0 ? null : params[0]);
				return this.STATUS_OK;
			} catch (Exception e) {
				return this.STATUS_ERROR;
			}			
		}
		@Override
		protected void onProgressUpdate(Integer... progress) {
			//nothing to update on progress ...
	    }
		@Override
	     protected void onPostExecute(Integer result) {
			if(result == this.STATUS_ERROR) {					
					Toast.makeText(DoctorOnlineListActivity.this, "network error ...", Toast.LENGTH_SHORT).show();
					DoctorOnlineListActivity.this.finish();
			}else {
                //docList = new ArrayList<Map<String,String>>();
                                
                prepareDataForAdapter();
                adapter = new DoctorListAdapter(DoctorOnlineListActivity.this,
		        		docList, R.layout.doctor_row,
		        		new String[] {"docId", "docTitile1","docTitile2","docTitile3", "u_rank"},
		        		new int[] {R.id.doc_row_doc_id, R.id.doc_title1, R.id.doc_title2, 
        				R.id.doc_title3, R.id.list_ratingbar_small}
        		);
                itemListView.setAdapter(adapter);				
			}
			footerView.setText(docJsonList.size() + " Items found");
			dialog.dismiss();
	     }
	}
	
}

