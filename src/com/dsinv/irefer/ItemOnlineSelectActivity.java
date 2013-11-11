package com.dsinv.irefer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.dsinv.irefer.R;
import com.dsinv.irefer.DbAdapter;

import android.app.Activity;
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
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ItemOnlineSelectActivity extends Activity {
	
	private TextView textView; 
	private TextView footerView;
	private ArrayAdapter<String> autoCompleteAdapter;
    private Object idArr[] = new Object[]{"0"};
    private Object nameArr[] =  new Object[]{"no match found"};    
    private int opr = 0;
    private ListView itemListView;
    private boolean[] selectionArr;
    private Map<String, String> selectedMap;
    Map nameIdMap;
    private Button doneBtn;
    private CharSequence searchText = "";
    protected Handler systemtaskHandler = new Handler();
   	Runnable systemTaskRunner = new Runnable() {
   		public void run()
           {
           	try
       		{
           		CharSequence s = searchText;
           		autoCompleteAdapter.clear();
                
                String jsonData = "";
                try {
                	if(opr == DbAdapter.PRACTICE) {
                		jsonData = getDataFromURL(ABC.WEB_URL+"practice/json?code="+s.toString());	
                	}else if(opr == DbAdapter.HOSPITAL) {
                		jsonData = getDataFromURL(ABC.WEB_URL+"hospital/json?code="+s.toString());
                	}else if(opr == DbAdapter.SPECIALTY) {
                		jsonData = getDataFromURL(ABC.WEB_URL+"speciality/json?code="+s.toString());
                	}else if(opr == DbAdapter.INSURANCE) {                		
                		jsonData = getDataFromURL(ABC.WEB_URL+"insurance/json?code="+s.toString());                		
                	}else if(opr == DbAdapter.COUNTY) {
                		jsonData = getDataFromURL(ABC.WEB_URL+"county/json?code="+s.toString());
                	}else if(opr == DbAdapter.ACO) {
                		jsonData = getDataFromURL(ABC.WEB_URL+"aco/json?code="+s.toString());
                	}     
                	
                } catch(Exception ex) {                	
                	System.out.println("SMM:ERROR::"+ex);
                }
                
                try {
                	Map<String, Object[]> map = parseJSONData(jsonData);
                	nameArr = map.get("nameArr");
                	idArr = map.get("idArr");
                	
                } catch(Exception ex) {                	
                	Toast.makeText(ItemOnlineSelectActivity.this, "Failed to parse JSON data.", Toast.LENGTH_SHORT).show();
                	System.out.println(ex);
                	Log.d("JSON:ERROR", ex.getMessage(),ex);
                }
                                               
                for (int i = 1, j = 0; i < nameArr.length; i++) {
                	nameIdMap.put((String) nameArr[i], ""+idArr[i]);
                    if( ((String)nameArr[i]).toLowerCase().contains(s)) {                    	
                    	autoCompleteAdapter.add((String) nameArr[i]);
                    	if(selectedMap.get(nameArr[i]) == null) {
                    		itemListView.setItemChecked(j, false);
                    	}else {
                    		itemListView.setItemChecked(j, true);
                    	}
                    	j++;
                    }                 
                }                
                footerView.setText( (nameArr.length - 1)+" items found");    
                textView.setFocusableInTouchMode(true);
                textView.requestFocus();
                InputMethodManager inputMethodManager = (InputMethodManager) ItemOnlineSelectActivity.this
                        .getSystemService(ItemOnlineSelectActivity.this.INPUT_METHOD_SERVICE);
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
        
        setContentView(R.layout.item_select_list);        
        overridePendingTransition(R.anim.drop_enter, R.anim.drop_leave);
        
        opr = this.getIntent().getIntExtra("opr", 0);
        
        if(opr ==  DbAdapter.PRACTICE) {
        	setTitle( getString( R.string.app_name ) + " - Type to Add Practice");
        }else if(opr ==  DbAdapter.HOSPITAL) { 
        	setTitle( getString( R.string.app_name ) + " - Type to Add Hospital");
        }else if(opr ==  DbAdapter.SPECIALTY) {
        	setTitle( getString( R.string.app_name ) + " - Type to Add Specialty");
        }else if(opr ==  DbAdapter.INSURANCE) {
        	setTitle( getString( R.string.app_name ) + " - Type to Add Insurance");
        }else if(opr ==  DbAdapter.ACO){
        	setTitle( getString( R.string.app_name ) + " - Type to Add ACO"); 
        }                                
        autoCompleteAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice);
        autoCompleteAdapter.setNotifyOnChange(true);
    	textView = (TextView)findViewById(R.id.item_select_text_edit);
    	footerView = (TextView)findViewById(R.id.item_select_footer);    	    
    	
    	nameIdMap = new HashMap();
    	
    	//nameArr = this.getIntent().getStringArrayExtra("nameArr");
    	//int idArr1[] = this.getIntent().getIntArrayExtra("idArr");
    	
    	itemListView = (ListView)findViewById(R.id.itemSelectList);
    	itemListView.setAdapter(autoCompleteAdapter);
    	itemListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    	
    	selectionArr = this.getIntent().getBooleanArrayExtra("selectionArr");
    	doneBtn = (Button) findViewById(R.id.itemSelectBtn);
    	doneBtn.setText("Choose All");
    	if(opr == DbAdapter.COUNTY){
    		doneBtn.setText("Done");
    	}
    	doneBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finishActivity();
				//Intent intent = new Intent(SetupActivity.this, ItemAddActivity.class);
				//SetupActivity.this.intent.putExtra("opr", DbAdapter.PRACTICE);
	            //startActivityForResult(intent, 1100);				
			}
		});
    	selectedMap = new HashMap<String, String>();
    	itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	    	
    		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
    			
    			
    			String key = parent.getAdapter().getItem(position).toString();
    			if(selectedMap.get(key) == null) {
    				if(opr == DbAdapter.COUNTY){
    	    			int i = selectedMap.size();
    	    			
    	    			if(i>Utils.COUNTY_LIMIT||i==Utils.COUNTY_LIMIT){
    	    				Toast.makeText(ItemOnlineSelectActivity.this, "Maximum "+Utils.COUNTY_LIMIT+" county can be seleted", Toast.LENGTH_LONG).show();
    	    				//CheckBox cBox = (CheckBox) v.findViewById(id);
    	                    //cBox.toggle();
    	    				itemListView.setItemChecked(position, false);
    	    				return;
    	    			}
        			}
    				selectedMap.put(key, "true");
    			}else {
    				selectedMap.remove(key);
    			}
    			if(selectedMap.isEmpty()) {
    				doneBtn.setText("Choose All");
    			} else {
    				doneBtn.setText("Done");
    				if(opr ==  DbAdapter.SPECIALTY || opr ==  DbAdapter.INSURANCE || opr == DbAdapter.PRACTICE || opr == DbAdapter.ACO)
    					finishActivity();
    			}
    		}
    	});
    	
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
                
//              
	        }
	    };
	    textView.addTextChangedListener(textChecker);
//	    Button doneBtn = (Button) findViewById(R.id.itemSelectBtn);
//		doneBtn.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				setResult();
//				finish();	
//			}
//		});
        
        searchText = "";
    	systemtaskHandler.removeCallbacks( systemTaskRunner );
        systemtaskHandler.postDelayed( systemTaskRunner, 1 );
    }
	private void finishActivity(){
		//System.out.println("SMM:INFO::FINSHING....");
		setResult();
		finish();
		overridePendingTransition(R.anim.drop_leave, R.anim.drop_back);
	}
	private void setResult(){
		this.setResult(RESULT_OK, this.getIntent());		
		String keyArr[] = new String[selectedMap.keySet().size()];
		String idArr1[] = new String[selectedMap.keySet().size()];
		int i=0;
		for (Iterator<String> it = selectedMap.keySet().iterator(); it.hasNext(); i++) {
		    keyArr[i] = (String)it.next();
		    idArr1[i] = (String)nameIdMap.get(keyArr[i]);
		}		
		this.getIntent().putExtra("selectedNameArr", keyArr);
		this.getIntent().putExtra("selectedIdArr", idArr1);
	}
	
	private Map<String, Object[]> parseJSONData(String jsonData) throws Exception {
		HashMap<String, Object[]> map = new HashMap<String, Object[]>();
		List<String> idList = new ArrayList<String>();
		List<String> nameList = new ArrayList<String>();
		JSONTokener jsonTokener = new JSONTokener(jsonData);
    	JSONArray arr = (JSONArray) jsonTokener.nextValue();
    	for(int i=0; i < arr.length(); i++) { 
    		JSONObject object = arr.getJSONObject(i);
    		String id   = (i == 0) ? "0" : object.getString("id");
    		String name = (i == 0) ? "0" : object.getString("name");
    		idList.add(id);
    		nameList.add(name);
    	}
    	map.put("idArr", idList.toArray());
    	map.put("nameArr", nameList.toArray());
    	return map;
	}
	
	private String getDataFromURL(String urlStr) throws Exception {
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
	
}
