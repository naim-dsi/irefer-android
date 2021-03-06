package com.dsinv.irefer2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.dsinv.irefer2.R;
import com.dsinv.irefer2.DbAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class DoctorListActivity extends Activity {

	private DbAdapter	dba;
	private TextView textView; 
	private TextView footerView;
	private TextView filterView;
	
	protected Handler systemtaskHandler = new Handler();
	Runnable systemTaskRunner = new Runnable() {
		public void run()
        {
        	try
    		{
        		
                actualRowCount = 0;
                clearDocListDate();
                docList.clear();
                textView.setFocusableInTouchMode(true);
                textView.requestFocus();
                InputMethodManager inputMethodManager = (InputMethodManager) DoctorListActivity.this
                        .getSystemService(DoctorListActivity.this.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(textView, InputMethodManager.SHOW_IMPLICIT);
                if(isOnlineSearch)
            		new DoctorDownloadTask().execute(new String[]{""+showMore});
            	else
            		new DoctorSearchTask().execute(new String[]{"0, "+showMore});
        		
    		}
            catch(Exception ex)
    		{
            	ex.printStackTrace();
    			
    		}
        }
    };
	String insuranceIds;
	String specialityIds;
	String hospitalIds;
	String practiceIds;
	String acoIds;
	String countyIds;
	String zipCode;
	String languages;
	String docName;
	int searchOrder = 4;
	private int resourceFlag = 0;
	private ArrayAdapter<String> autoCompleteAdapter;
    Object idArr[] = null;
    Object nameArr[] =  new Object[]{"no match found"};
    String userId = "0";
    List<HashMap<String, String>> docList = null;
    ListView itemListView;
    DoctorListAdapter adapter;
    int selectedItemIdx = 0;
    int onlineFlag = 0;
	private int showMore = 50;
	
	private int rowCount = 0;
	private int actualRowCount = 0;
	
	View pBar = null;
	ProgressDialog dialogP;
	Set docIdSet = new HashSet();
	boolean isOnlineSearch = false;
	boolean isResourceFlag = false;
	@Override
    public void onCreate(Bundle savedInstanceState) {
			try{
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.doctor_list);
	        setTitle( getString( R.string.app_name ) + " - Specialist Search");
	                
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
	    	textView = (TextView)findViewById(R.id.doc_search_text_edit);
	    	footerView = (TextView)findViewById(R.id.doc_search_footer);
	    	
	    	docName = getIntent().getStringExtra("docName");
	    	if(docName == null || "null".equals(docName)) docName = "";
	    	
	    	zipCode = getIntent().getStringExtra("zipCode");
	    	if(zipCode == null || "null".equals(zipCode)) zipCode = "";
	    	
	    	languages = getIntent().getStringExtra("languages");
	    	if(languages == null || "null".equals(languages)) languages = "";
	    	
	    	insuranceIds = getIntent().getStringExtra("insuranceId");
	    	if(Utils.isEmpty(insuranceIds) || "null".equals(insuranceIds)) insuranceIds = null;
	    	
	    	specialityIds = getIntent().getStringExtra("specialityId");
	    	if(Utils.isEmpty(specialityIds) || "null".equals(specialityIds)) specialityIds = null;
	    	
	    	hospitalIds = getIntent().getStringExtra("hospitalId");
	    	if(Utils.isEmpty(hospitalIds) || "null".equals(hospitalIds)) hospitalIds = null;
	    	
	    	practiceIds = getIntent().getStringExtra("practiceId");
	    	if(Utils.isEmpty(practiceIds) || "null".equals(practiceIds)) practiceIds = null;
	    	
	    	countyIds = getIntent().getStringExtra("countyId");
	    	if(Utils.isEmpty(countyIds) || "null".equals(countyIds)) countyIds = null;
	    	
	    	acoIds = getIntent().getStringExtra("acoId");
	    	if(Utils.isEmpty(acoIds) || "null".equals(acoIds)) countyIds = null;
	    	
	    	userId = getIntent().getStringExtra("userId");
	    	isOnlineSearch = getIntent().getBooleanExtra("is_online_search", false);
	    	isResourceFlag = getIntent().getBooleanExtra("resourceFlag", false);
	    	if(isOnlineSearch){
	    		onlineFlag = 1;
	    	}
	    	else{
	    		onlineFlag = 0;
	    	}
	        if(isResourceFlag){
	        	resourceFlag = 1;
	        	setTitle( getString( R.string.app_name ) + " - Resources Search");
	        }
	        else{
	        	resourceFlag = 0;
	        }
	    	/*
	    	filterView = (TextView)findViewById(R.id.filter_selected_values);
	    	filterView.setMovementMethod(ScrollingMovementMethod.getInstance());
	    	filterView.setHorizontallyScrolling(true);
	    	String filterStr = getFilterStr("Specialty", getIntent().getStringExtra("spec"));
	    	filterStr += " | "+getFilterStr("Inurance", getIntent().getStringExtra("insu"));
	    	filterStr += " | "+getFilterStr("Hospital", getIntent().getStringExtra("hosp"));
	    	filterStr += " | "+getFilterStr("County", getIntent().getStringExtra("cnty"));
	    	filterView.setText(filterStr);
	    	*/
	    	
	    	
	    	//EditText.setAdapter(autoCompleteAdapter);
	    	
	    	//autoCompleteAdapter.add("Shamim");
	    	docList = new ArrayList();
	    	
	    	//faisal > modify (made the variable final to be used inside inner class)
	    	itemListView = (ListView)findViewById(R.id.doctor_list);
	    	//pBar = (ProgressBar)findViewById(R.id.docListProgressBar);
	    	pBar = (View)findViewById(R.id.filter_top_wrapper);
	    	//itemListView.setOverScrollMode(View.OVER_SCROLL_ALWAYS); 
	    	//TextView lbl = new TextView(this);
	    	//lbl.setText("Show more");
	    	//lbl.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	    	//itemListView.addFooterView(lbl);
	    	
	    	dialogP = ProgressDialog.show(this, "", 
	                "Loading. Please wait...", true);
			
	    	
	    	adapter = new DoctorListAdapter(
	        		this, 
	        		dba,
	        		docList,
	        		R.layout.doctor_row,
	        		new String[] {"docId", "docTitile1","docTitile2","docTitile3", "u_rank"},
	        		new int[] {R.id.doc_row_doc_id, R.id.doc_title1, R.id.doc_title2, R.id.doc_title3,
	        		R.id.list_ratingbar_small},
	        		onlineFlag
	        		);
	    	itemListView.setAdapter(adapter);
	    	
	    	
	    	
	    	if(isOnlineSearch)
	    		new DoctorDownloadTask().execute(new String[]{""+showMore});
	    	else
	    		new DoctorSearchTask().execute(new String[]{"0, "+showMore});
	    	dialogP.dismiss();
	        //registerForContextMenu(itemListView);
	    	/*
	        itemListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					selectedItemIdx = arg2;
					return false;
				}
		    	
			});
	        */
	        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		    	
	    		public void onItemClick(AdapterView<?> parent, View v, final int position, long id){
	    			TextView docIdView = (TextView)v.findViewById(R.id.doc_row_doc_id);
	    			
	    			if("SHOW_MORE".equals(docIdView.getText().toString())){
	    				
	    				dialogP = ProgressDialog.show(DoctorListActivity.this, "", 
	                            "Loading. Please wait...", true);
	    				dialogP.show();
	    				if(isOnlineSearch)
	    		    		new DoctorDownloadTask().execute(new String[]{""+(docList.size()+showMore-1)});
	    		    	else	
	    		    		new DoctorSearchTask().execute(new String[]{actualRowCount+", "+showMore});
	    				
	    				return;
	    			} else if("NO_RESULT".equals(docIdView.getText().toString())){
	    				//DoctorListActivity.this.finish();
	    				return;
	        		} else {
	        			Intent intent = new Intent(DoctorListActivity.this, DoctorDetailActivity.class);
	        			intent.putExtra("doctor_id", Integer.parseInt(docIdView.getText().toString()));
	        			intent.putExtra("user_id", userId);
	        			intent.putExtra("position", position+"");
	        			intent.putExtra("is_online_search", isOnlineSearch);
	        			startActivityForResult(intent, 1100);
	        		}
	    		}
		    });
	        
	    	final TextWatcher textChecker = new TextWatcher() {
	    		 
		        public void afterTextChanged(Editable s) {
		        	//Log.d("NI","afterTextChanged - filer textbox : "+s.toString());
		        	//textView.setEnabled(true);
		        	docName = s.toString();
		        	systemtaskHandler.removeCallbacks( systemTaskRunner );
	                systemtaskHandler.postDelayed( systemTaskRunner, 1500 );
	                
		        	
		        }
		        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		        	//textView.setEnabled(false);
		        }
		 
		        public void onTextChanged(CharSequence s, int start, int before, int count) {
		                //autoCompleteAdapter.clear();
		                //super.onTextChanged(s,start,before,count);
		               return;
		                //Cursor cr = dba.fetchAll(dba.PRACTICE);
		                //Cursor cr = dba.fetchDoctorByName(s.toString());
		                /*
		                Cursor cr = dba.searchDoctor(insuranceIds,specialityIds,hospitalIds,countyIds, s.toString(), zipCode, languages,"100");
		            	            
		                List<Map<String,String>> data = new ArrayList<Map<String,String>>();
		                
		                if(cr != null && cr.getCount() > 0) {
		                	Map idMap = new HashMap();
		                	idArr  = new Object[cr.getCount()];
		                	nameArr  = new Object[cr.getCount()];
		                	cr.moveToFirst();
		                	for (int i=0; i < cr.getCount(); i++,cr.moveToNext()) {
		                		if(!docIdSet.add(new Integer(cr.getInt(1))))
		                		//if(idMap.get(cr.getInt(1)+"") != null)
		                    		continue;
		                    	//idMap.put(cr.getInt(1)+"", new Integer(cr.getInt(1)));
		                		idArr[i] = cr.getString(1);
		                		nameArr[i] = cr.getString(2)+" "+cr.getString(4)+" "+cr.getString(3);
			                    autoCompleteAdapter.add((String)nameArr[i]);
			                    //faisal > starts
			                    HashMap<String,String> row = new HashMap<String,String>();
			                    row.put("docTitile1", cr.getString(3)+" "+cr.getString(4)+" "+cr.getString(2));
			                	row.put("docTitile2", "Degree: "+cr.getString(5)+" Phone: "+cr.getString(6));
			                	row.put("docTitile3", "Grade: "+cr.getInt(8)+" Language: "+cr.getString(7));
			                	row.put("docPhone", cr.getString(6));
			                	row.put("docId", cr.getInt(1)+"");
			                	row.put("userId", userId);
			                	data.add(row);
			                	SimpleAdapter simpleAdapter = new SimpleAdapter(DoctorListActivity.this,data,
			                			R.layout.doctor_row,
			                    		new String[] {"docId", "docTitile1","docTitile2","docTitile3"},
			                    		new int[] {R.id.doc_row_doc_id, R.id.doc_title1, R.id.doc_title2, R.id.doc_title3}
			                    );
			                	itemListView.setAdapter(simpleAdapter);
			                    //faisal > ends
			                    
			                }
		                	footerView.setText(cr.getCount()+" match found");
		                	cr.close();
		                } else {
		                	footerView.setText("No match founr");
		                }
		                System.out.println("SMM:INFO::"+nameArr.length);
		                */
		        }
		    };
		    textView.addTextChangedListener(textChecker);
		    
	//	    Button orderBtn = (Button) findViewById(R.id.order_flip);
	//        orderBtn.setOnClickListener(new View.OnClickListener() {
	//			public void onClick(View v) {
	//			}
	//		});
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	    
    }
	private void addDoctorToList(JSONObject obj) throws Exception{
		if(obj.getInt("id")==Utils.doctorId)
			return;
		if(!docIdSet.add(new Integer(obj.getInt("id"))))
    			return;
        HashMap<String,String> m = new HashMap<String,String>();
        //temp.put("docPic","http://203.202.248.108/dsi/irefer3/css/bg.gif");
    	//Cursor cr1 = dba.fetchByNetId(DbAdapter.PRACTICE, cr.getInt(11));
    	//String pracName = "";
    	//if(cr1 != null) {
    		//cr1.moveToFirst();
    		//if(cr1.getCount() > 0)
    			//pracName = cr1.getString(2);
    		//cr1.close();
    	//}
    	//m.put("docId", obj.getString("id"));
        Log.d("NI::","c3="+obj.getString("c3"));
		m.put("docTitile1", obj.getString("c2") +" " + 
				((Utils.isEmpty(obj.getString("c3"))||obj.getString("c3").equals("null")) ? "" : obj.getString("c3")+" ") + obj.getString("c1")+
				", "+((Utils.isEmpty(obj.getString("c4"))||obj.getString("c4").equals("null")) ? "" : obj.getString("c4")));
		//m.put("docTitile2", "Phone:" + obj.getString("c5"));
		m.put("docTitile2", "Phone:" + ((Utils.isEmpty(obj.getString("c5"))||obj.getString("c5").equals("null")) ? "" : obj.getString("c5")));
		//m.put("docTitile3", obj.getString("c8"));  // TODO
		m.put("docTitile3", ((Utils.isEmpty(obj.getString("c8"))||obj.getString("c8").equals("null")) ? "" : obj.getString("c8")));  // TODO
		//m.put("docPhone", obj.getString("c5"));
		m.put("docPhone", ((Utils.isEmpty(obj.getString("c5"))||obj.getString("c5").equals("null")) ? "" : obj.getString("c5")));
		//m.put("docId", obj.getString("id"));
    	m.put("docId", obj.getString("id"));
    	m.put("userId", userId);
    	m.put("grade", ((Utils.isEmpty(obj.getString("c7"))||obj.getString("c7").equals("null")) ? "0" : obj.getString("c7")));
    	m.put("u_rank", Utils.isEmpty(obj.getString("u_rank")) ? "0" : obj.getString("u_rank"));
    	m.put("up_rank", Utils.isEmpty(obj.getString("up_rank")) ? "0" : obj.getString("up_rank"));
        //temp.put("docTitile1", cr.getString(3)+" "+cr.getString(4)+" "+cr.getString(2)+", "+cr.getString(5));
        //temp.put("docTitile2", "Phone: "+cr.getString(6));
        //temp.put("docTitile3", pracName);
        //temp.put("docPhone", cr.getString(6));
        //temp.put("docId", cr.getInt(1)+"");
        //temp.put("userId", userId);
        //temp.put("grade", ""+cr.getInt(8));
        //temp.put("u_rank", Utils.isEmpty(""+cr.getInt(22)) ? "0" : ""+cr.getInt(22));
    	System.out.println("SMM::DOC::ADDED::"+obj.getString("c2") +" " + 
				(Utils.isEmpty(obj.getString("c3")) ? "" : obj.getString("c3")+" ") + obj.getString("c1")+
				", "+obj.getString("c4"));
        docList.add(m);
	}
	
	private void addDoctorToList(Cursor cr){
		try{
			idArr = new Object[cr.getCount()+1];
			Map idMap = new HashMap();
	    	//System.out.println("SMM::DOCTOR::COUNT="+cr.getCount());
			Log.d("NR::", "AISE");
	    	cr.moveToFirst();
	    	for(int i=0; i<cr.getCount(); i++) {
	    		//Log.d("NR::", "AISE2");
	    		actualRowCount = actualRowCount +1;
//	    		if(!docIdSet.add(new Integer(cr.getInt(1))))
//	    		//if(idMap.get(cr.getInt(1)+"") != null)
//	        		continue;
	        	//idMap.put(cr.getInt(1)+"", new Integer(cr.getInt(1)));
	        	
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
	        	temp.put("up_rank", Utils.isEmpty(""+cr.getInt(23)) ? "0" : ""+cr.getInt(23));
	        	temp.put("pa_rank", Utils.isEmpty(""+cr.getInt(23)) ? "0" : ""+cr.getInt(23));
	        	docList.add(temp);
	        	if((docList.size() % showMore) == 0)
	        		break;
	        	
	        	cr.moveToNext();
	    	}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		Map map = (Map)docList.get(selectedItemIdx);
		
		 
		
		menu.setHeaderTitle((String)map.get("docTitile1"));
		menu.add(0, v.getId(), 0, "Rank");
		menu.add(0, v.getId(), 0, "Report Change");
		textView.setFocusableInTouchMode(true);
        textView.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) DoctorListActivity.this
                .getSystemService(DoctorListActivity.this.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(textView, InputMethodManager.SHOW_IMPLICIT);
//        
	}
	
	@Override  
	public boolean onContextItemSelected(MenuItem item) {
		Map map = (Map)docList.get(selectedItemIdx);
		int docId = Integer.parseInt((String)map.get("docId"));
	    if(item.getTitle() == "Rank"){
	    	
	    } else if(item.getTitle()=="Report Change") {
	    	Intent intent = new Intent(DoctorListActivity.this, DoctorDetailActivity.class);
        	intent.putExtra("doctor_id", docId);
        	intent.putExtra("user_id", userId);
        	intent.putExtra("position", selectedItemIdx+"");
        	intent.putExtra("is_online_search", isOnlineSearch);
        	//intent.putExtra("report", 1);
            startActivityForResult(intent, 1100);
	    }
	    return true;  
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
	    case R.id.sort_by_first_name:
	        sortByFirstName();
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	private void sortByGrade(){
		ProgressDialog dialogP2 = ProgressDialog.show(this, "", 
                "Loading. Please wait...", true);
		searchOrder = 1;
		int limit = docList.size()-1;
		clearDocListDate();
		if(isOnlineSearch)
    		new DoctorDownloadTask().execute(new String[]{""+limit});
    	else
    		new DoctorSearchTask().execute(new String[]{"0, "+showMore});
		dialogP2.dismiss();
    	//Collections.sort(docList, new ListOfMapComparator("grade", true));
		itemListView.setAdapter(adapter);
	}
	
	private void sortByRank(){
		ProgressDialog dialogP2 = ProgressDialog.show(this, "", 
                "Loading. Please wait...", true);
		searchOrder = 0;
		int limit = docList.size()-1;
		clearDocListDate();
		if(isOnlineSearch)
    		new DoctorDownloadTask().execute(new String[]{""+limit});
    	else
    		new DoctorSearchTask().execute(new String[]{"0, "+showMore});
    	dialogP2.dismiss();
    	//Collections.sort(docList, new ListOfMapComparator("u_rank", true));
		itemListView.setAdapter(adapter);
	}

	private void sortByName(){
		ProgressDialog  dialogP2 = ProgressDialog.show(this, "", 
                "Loading. Please wait...", true);
		searchOrder = 3;
		int limit = docList.size()-1;
		clearDocListDate();
		if(isOnlineSearch)
    		new DoctorDownloadTask().execute(new String[]{""+limit});
    	else
    		new DoctorSearchTask().execute(new String[]{"0, "+showMore});
    	dialogP2.dismiss();
    	//Collections.sort(docList, new ListOfMapComparator("docTitile1"));
		itemListView.setAdapter(adapter);
	}
	
	private void sortByFirstName(){
		ProgressDialog dialogP2 = ProgressDialog.show(this, "", 
                "Loading. Please wait...", true);
		searchOrder = 2;
		int limit = docList.size()-1;
		clearDocListDate();
		if(isOnlineSearch)
    		new DoctorDownloadTask().execute(new String[]{""+limit});
    	else
    		new DoctorSearchTask().execute(new String[]{"0, "+showMore});
    	dialogP2.dismiss();
    	//Collections.sort(docList, new ListOfMapComparator("docTitile1"));
		itemListView.setAdapter(adapter);
	}

	private void clearDocListDate() {
		actualRowCount = 0;
		docList.clear();
		docIdSet.clear();
	}
	
	private String getFilterStr(String label, String str) {
		if("None".equalsIgnoreCase(str) || "All".equalsIgnoreCase(str) || "NA".equalsIgnoreCase(str))
	    		return label+": All";
	    	else
	    		return str;
	}
	
	//faisal > starts
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(data != null ){
			if(data.getBooleanExtra("close_me", false) 	&& requestCode == 1100	&& resultCode == RESULT_OK) {
				finish();	
				return;
			}
			String u_rank = data.getStringExtra("u_rank").toString();
			String up_rank = data.getStringExtra("up_rank").toString();
			String pa_rank = data.getStringExtra("pa_rank").toString();
			String position = data.getStringExtra("position").toString();
			Log.d("NI::",u_rank);
			Log.d("NI::",up_rank);
			Log.d("NI::",pa_rank);
			Log.d("NI::",position);
			HashMap<String, String> data2 = docList.get(Integer.parseInt(position));
			data2.put("u_rank",String.valueOf(u_rank));
			data2.put("up_rank",String.valueOf(up_rank));
			data2.put("pa_rank",String.valueOf(pa_rank));
			//docListafInteger.parseInt(position));
			docList.set(Integer.parseInt(position),data2);
			adapter.notifyDataSetChanged();
		}	
		else
		{
			Log.d("NR::","AAA");
			finish();	
			return;
			
		}
		
	}
		//faisal > ends
		
	public Map<String, Object[]> parseJSONData(String jsonData) throws Exception {
		HashMap<String, Object[]> map = new HashMap<String, Object[]>();
		JSONTokener jsonTokener = new JSONTokener(jsonData);
    	//JSONObject object = (JSONObject) jsonTokener.nextValue();
    	JSONArray arr = (JSONArray) jsonTokener.nextValue();
    	Log.d("NI::",""+arr.length());
    	for(int i=1; i < arr.length(); i++) { 
    		JSONObject object = arr.getJSONObject(i);
    		try {
    			addDoctorToList(object);
    			
    			actualRowCount++;
    		}catch(Exception ex){
    			ex.printStackTrace();
    		}
    		//System.out.println("SMM:CODE::"+code);
    	}
    	return map;
	}
	
	public String getDataFromURL(String urlStr) throws Exception {
		System.out.println("URL::"+urlStr);
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

	private void addDoctorListFooterItem() {
		if(docList.size() > 0){
		
			String countMsg = "Showing "+docList.size()+" best matches";
		
			HashMap<String,String> temp = new HashMap<String,String>();
			//idArr[docList.size()] = new Integer(-99);
			temp.put("docId", "SHOW_MORE");
			temp.put("docTitile1", "Show More Doctors ...");
			temp.put("docTitile2", countMsg);
			temp.put("docTitile3", "");
			docList.add(temp);
			//System.out.println("SHOW MORE ADDED TO DOC LIST");
		}
		if(docList.size() == 0){
			HashMap<String,String> temp = new HashMap<String,String>();
			//temp.put("docPic","http://203.202.248.108/dsi/irefer3/css/bg.gif");
			//idArr[0] = new Integer(-999);
			temp.put("docId", "NO_RESULT");
			temp.put("docTitile1", "No Doctor Found, Please try again");
			temp.put("docTitile2", "");
			temp.put("docTitile3", "");
			docList.add(temp);
			
		}
	}
	Handler progressHandler = new Handler() {
        public void handleMessage(Message msg) {
            dialogP.incrementProgressBy(0);
        }
    };
    
	private class DoctorSearchTask extends AsyncTask<String, Void, String> {
		
		@Override
		protected void onPreExecute() {
			dialogP.show();
		}
		
		@Override
		protected String doInBackground(String... limits) {
			String response = "";
			//System.out.println("ASYNC TASK STARTED for "+limits[0]);
			
			if(actualRowCount == 0)
				docList.clear();
			for(int i=0, j=0; i<showMore && j<1; j++,i++) {
				Cursor cr = dba.searchDoctor(insuranceIds,specialityIds,hospitalIds,countyIds, docName, zipCode, languages, 
						actualRowCount+", 100", searchOrder, acoIds, resourceFlag,practiceIds);
				
				Log.d("NR::", ""+j+" - "+actualRowCount);
				if(cr != null) {
					if(docList.size() > 0)
						docList.remove(docList.size()-1);
					int prevSize = docList.size();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					addDoctorToList(cr);
					i = docList.size() - prevSize;
					//adapter.notifyDataSetChanged();
					cr.close();
				} 
			}
	    	//System.out.println("ASYNC TASK ALMOST DONE...");
			Log.d("NR::", "** "+actualRowCount);
			addDoctorListFooterItem();
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			
			adapter.notifyDataSetChanged();
			pBar.setVisibility(View.GONE);
			dialogP.dismiss();
			adapter.notifyDataSetChanged();
	    	itemListView.requestLayout();
		}
	}

	private class DoctorDownloadTask extends AsyncTask<String, Void, String> {
		
		@Override
		protected void onPreExecute() {
			dialogP.show();
		}
		
		@Override
		protected String doInBackground(String... limits) {
			String response = "";
			//System.out.println("ASYNC TASK STARTED for "+limits[0]);
			try {
				String jsonData = getDataFromURL(ABC.WEB_URL+"doctor/search?limit="+limits[0]+
						(Utils.isEmpty(insuranceIds) ? "" : "&insu_ids=" + insuranceIds) +
						(Utils.isEmpty(languages) ? "" : "&lang="+languages)+
						"&order="+searchOrder +
						(Utils.isEmpty(countyIds) ? "" : "&cnty_ids="+countyIds) +
						(Utils.isEmpty(specialityIds) ? "" : "&spec_ids=" + specialityIds) + 
						(Utils.isEmpty(hospitalIds) ? "" : "&hosp_ids=" + hospitalIds) +
						"&resourceFlag="+resourceFlag+
						"&user_id="+userId+
						(Utils.isEmpty(zipCode) ? "" :"&zip="+zipCode)+
						(Utils.isEmpty(docName) ? "" : "&doc_name=" +docName));		
			
				if(docList.size() > 0)
					docList.remove(docList.size()-1);
				parseJSONData(jsonData);
			} catch(Exception ex){
				ex.printStackTrace();
			}
			
	    	//System.out.println("ASYNC TASK ALMOST DONE...");
			addDoctorListFooterItem();
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			adapter.notifyDataSetChanged();
			pBar.setVisibility(View.GONE);
			dialogP.dismiss();
			
			adapter.notifyDataSetChanged();
	    	itemListView.requestLayout();
			
		}
	}
}

