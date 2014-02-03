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
import com.dsinv.irefer2.DbAdapter;

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
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ItemViewActivity extends Activity {

	private DbAdapter	dba;
	private TextView textView; 
	private TextView footerView;
	private ArrayAdapter<String> autoCompleteAdapter;
    int idArr[];
    int netIdArr[];
    String nameArr[] = new String[]{""};
    String addArr[];
    int selectedItemIdx = -1;
    int opr = 0;
    int myPracId = 0;
	int myHospId = 0;
	int myCntyId = 0;
	int userId = 0;
	int doctorOrResource = 0;
	private CharSequence searchText = "";
    protected Handler systemtaskHandler = new Handler();
   	Runnable systemTaskRunner = new Runnable() {
   		public void run()
           {
           	try
       		{
           		CharSequence s = searchText;
           		autoCompleteAdapter.clear();
                for (int i=0; i < nameArr.length; i++) {
                    if(nameArr[i].toLowerCase().contains(s.toString().toLowerCase())) 
                    	autoCompleteAdapter.add((String) nameArr[i]);
                        //System.out.println("SMM:INFO::"+nameArr[i]);
                }
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
        setContentView(R.layout.item_view_list);
        overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_leave);
        
        Intent intent = getIntent();
        opr = this.getIntent().getIntExtra("opr", 0);
        userId = this.getIntent().getIntExtra("userId", 0);
        myPracId = this.getIntent().getIntExtra("myPracId", 0);
        myHospId = this.getIntent().getIntExtra("myHospId", 0);
        myCntyId = this.getIntent().getIntExtra("myCntyId", 0);
        
        doctorOrResource = this.getIntent().getIntExtra("doctorOrResource", 0);
        
        System.out.println("SMM::MY-CNTY-ID::"+myCntyId);
        
        if(opr ==  DbAdapter.PRACTICE)
        	setTitle( getString( R.string.app_name ) + " - Practice");
        if(opr ==  DbAdapter.HOSPITAL)
        	setTitle( getString( R.string.app_name ) + " - Hospital");
        if(opr ==  DbAdapter.SPECIALTY)
        	setTitle( getString( R.string.app_name ) + " - Specialty");
        if(opr ==  DbAdapter.INSURANCE)
        	setTitle( getString( R.string.app_name ) + " - Insurance");
        if(opr ==  DbAdapter.COUNTY)
        	setTitle( getString( R.string.app_name ) + " - County");     
        dba = new DbAdapter(this);
        dba.open();
        
        loadData();
    	
        autoCompleteAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line); 
    	autoCompleteAdapter.setNotifyOnChange(true); // This is so I don't have to manually sync whenever changed 
    	textView = (TextView)findViewById(R.id.item_view_text_edit);
    	
    	ListView itemListView = (ListView)findViewById(R.id.itemViewList);
    	itemListView.setAdapter(autoCompleteAdapter);
    	registerForContextMenu(itemListView);
    	
    	for (int i=0; i < nameArr.length; i++) {
    		if(!nameArr[i].equals("no match found")){
    			autoCompleteAdapter.add((String) nameArr[i]);
    		}
        }
    	
    	final TextWatcher textChecker = new TextWatcher() {
    		 
	        public void afterTextChanged(Editable s) {
	        	//textView.setEnabled(true);
	        }
	        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	        	//textView.setEnabled(false);
	        }
	 
	        public void onTextChanged(CharSequence s, int start, int before, int count) {
	        	searchText = s;
	        	systemtaskHandler.removeCallbacks( systemTaskRunner );
                systemtaskHandler.postDelayed( systemTaskRunner, 1500 );
                
	        	
	        }
	    };
	    textView.addTextChangedListener(textChecker);

	    itemListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				selectedItemIdx = arg2;
				return false;
			}
	    	
		});
	    
	    itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	    	
    		public void onItemClick(AdapterView<?> parent, View v, int position, long id){
    			if(doctorOrResource == 1){
    				Intent intent = new Intent(ItemViewActivity.this, FilterPageActivity.class);
					startActivityForResult(intent, 1100);
    			} else if(doctorOrResource == 2){
    				Intent intent = new Intent(ItemViewActivity.this, ResourceFilterPageActivity.class);
    				startActivityForResult(intent, 1100);
    			} 
    			/*
    			selectedItemIdx = position;
    			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
    		        public void onClick(DialogInterface dialog, int which) {
    		            switch (which){
    		            case DialogInterface.BUTTON_POSITIVE:
    		            	//Yes button clicked
    		                break;

    		            case DialogInterface.BUTTON_NEGATIVE:
    		                //No button clicked
    		                break;
    		            }
    		        }
    		    };
    			AlertDialog.Builder builder = new AlertDialog.Builder(ItemViewActivity.this);
    		    builder.setMessage("Are you sure to add this item?").setPositiveButton("Yes", dialogClickListener)
    		        .setNegativeButton("No", dialogClickListener).show();
    		    */
    		}
	    });
	    
    }

	private boolean deleteRow(int idx) {
		//System.out.println("SMM::MY-CNTY-ID::"+myCntyId+"=="+idArr[idx]+"::OPR::"+opr+"=="+DbAdapter.COUNTY);
		if(opr == DbAdapter.PRACTICE && netIdArr[idx] == myPracId )
			return false;
		if(opr == DbAdapter.HOSPITAL && netIdArr[idx] == myHospId )
			return false;
		if(opr == DbAdapter.COUNTY && netIdArr[idx] == myCntyId )
			return false;
		dba.delete(opr, idArr[idx]);
		return true;
	}
	
	private void loadData() {
		Cursor cr = dba.fetchAll(opr);
		if(cr != null) {
			if(cr.getCount() > 0) {
				cr.moveToFirst();
				nameArr = new String[cr.getCount()];
				idArr = new int[cr.getCount()];
				netIdArr = new int[cr.getCount()];
				for(int i=0; i<cr.getCount(); i++) {
					nameArr[i] = cr.getString(2);
					idArr[i] = cr.getInt(0);             //local id used only for delete
					netIdArr[i] = cr.getInt(1); 
					cr.moveToNext();
				}
			}
			cr.close();
		}
	}
	
	@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle(nameArr[selectedItemIdx]);
		if(myHospId > 0 && opr == dba.PRACTICE) {
			menu.add(0, v.getId(), 0, "Request for Activation");
			menu.add(0, v.getId(), 0, "Activate");
		}
		menu.add(0, v.getId(), 0, "Delete");
		menu.add(0, v.getId(), 0, "Cancel");
	}
	
	@Override  
	public boolean onContextItemSelected(MenuItem item) {  
	    if(item.getTitle()=="Delete"){
	    	if(this.deleteRow(selectedItemIdx)) {
	    		Toast.makeText(ItemViewActivity.this, "Item deleted.", Toast.LENGTH_SHORT).show();
	    	} else {
	    		Toast.makeText(ItemViewActivity.this, "Can not deleted this item.", Toast.LENGTH_SHORT).show();
	    		return false;
	    	}
	    	loadData();
	    	autoCompleteAdapter.clear();
	    	for (int i=0; i < nameArr.length; i++) {
                	autoCompleteAdapter.add((String) nameArr[i]);
            }
	    } else if(item.getTitle()=="Request for Activation") {
	    	try {
    			String res = getDataFromURL(ABC.WEB_URL+"paUser/req?user_id="+
    					userId+"&prac_id="+ netIdArr[selectedItemIdx]);
    			Toast.makeText(this.getApplicationContext(), " "+res, Toast.LENGTH_SHORT).show();
			} catch(Exception ex) {
				Toast.makeText(this.getApplicationContext(), "Failed to send request", Toast.LENGTH_SHORT).show();
				ex.printStackTrace();
			}
	    } else if(item.getTitle()=="Activate") {
	    	Intent intent = new Intent(ItemViewActivity.this, ActivatePracticeActivity.class);
	    	intent.putExtra("pracId", netIdArr[selectedItemIdx]+"");
	    	intent.putExtra("pracName", nameArr[selectedItemIdx]);
			intent.putExtra("userId", userId+"");
			startActivityForResult(intent, 1100);	
	    	//return false;
	    }  else {
	    	return false;
	    }
	    return true;  
	}  
	
	@Override
    public void onBackPressed () {
        super.onBackPressed ();
        this.overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_back);
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
