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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ResourceItemOnlineSelectActivity extends Activity {
	
	private TextView textView; 
	private TextView footerView;
	private ArrayAdapter<String> autoCompleteAdapter;
    private Object idArr[] = new Object[]{"0"};
    private Object nameArr[] =  new Object[]{"no match found"};    
    private int opr = 0;
    private ListView itemListView;
    private boolean[] selectionArr;
    private Map<String, String> selectedMap;
            
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
        	setTitle( getString( R.string.app_name ) + " - Type to Add Speciality");
        }else if(opr ==  DbAdapter.INSURANCE) {
        	setTitle( getString( R.string.app_name ) + " - Type to Add Insurance");
        }
                                        
        autoCompleteAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice);
        autoCompleteAdapter.setNotifyOnChange(true);
    	textView = (TextView)findViewById(R.id.item_select_text_edit);
    	footerView = (TextView)findViewById(R.id.item_select_footer);    	    
    	
    	itemListView = (ListView)findViewById(R.id.itemSelectList);
    	itemListView.setAdapter(autoCompleteAdapter);
    	itemListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    	
    	selectionArr = this.getIntent().getBooleanArrayExtra("selectionArr");
    	
    	selectedMap = new HashMap<String, String>();
    	itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	    	
    		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
    			String key = parent.getAdapter().getItem(position).toString();
    			if(selectedMap.get(key) == null) {
    				selectedMap.put(key, "true");
    			}else {
    				selectedMap.remove(key);
    			}
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
                	}                	
                } catch(Exception ex) {                	
                	System.out.println("SMM:ERROR::"+ex);
                }
                
                try {
                	Map<String, Object[]> map = parseJSONData(jsonData);
                	nameArr = map.get("nameArr");
                	idArr = map.get("idArr");
                } catch(Exception ex) {                	
                	Toast.makeText(ResourceItemOnlineSelectActivity.this, "Failed to parse JSON data.", Toast.LENGTH_SHORT).show();
                	System.out.println(ex);
                	Log.d("JSON:ERROR", ex.getMessage(),ex);
                }
                                               
                for (int i = 1, j = 0; i < nameArr.length; i++) {
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
	        }
	    };
	    textView.addTextChangedListener(textChecker);
	    Button doneBtn = (Button) findViewById(R.id.itemSelectBtn);
        doneBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setResult();
				finish();	
			}
		});
    }
	
	private void setResult(){
		this.setResult(RESULT_OK, this.getIntent());		
		String keyArr[] = new String[selectedMap.keySet().size()];
		int i=0;
		for (Iterator<String> it = selectedMap.keySet().iterator(); it.hasNext(); ) {
		    keyArr[i++] = (String)it.next();
		}		
		this.getIntent().putExtra("selectedNameArr", keyArr);
	}
	
	private Map<String, Object[]> parseJSONData(String jsonData) throws Exception {
		HashMap<String, Object[]> map = new HashMap<String, Object[]>();
		List<String> idList = new ArrayList<String>();
		List<String> nameList = new ArrayList<String>();
		JSONTokener jsonTokener = new JSONTokener(jsonData);
    	JSONArray arr = (JSONArray) jsonTokener.nextValue();
    	for(int i=0; i < arr.length(); i++) { 
    		JSONObject object = arr.getJSONObject(i);
    		String id   = object.getString("id");
    		String name = object.getString("name");
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
