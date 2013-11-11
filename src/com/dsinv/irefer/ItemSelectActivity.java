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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
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

public class ItemSelectActivity extends Activity {

	private DbAdapter	dba;
	private TextView textView; 
	private TextView footerView;
	private Button doneBtn;
	
	private ArrayAdapter<String> autoCompleteAdapter;
    Object idArr[] = new Object[]{"0"};
    Object nameArr[] =  new Object[]{"no match found"};
    int selectedItemIdx = -1;
    int opr = 0;
    ListView itemListView;
    boolean[] selectionArr;
    Map selectedMap;
    Map nameIdMap;
    
    int rootIdx[];
    private CharSequence searchText = "";
    protected Handler systemtaskHandler = new Handler();
   	Runnable systemTaskRunner = new Runnable() {
   		public void run()
           {
           	try
       		{
           		CharSequence s = searchText;
           		autoCompleteAdapter.clear();
                
                /*
                String jsonData = "";
                try {
                	if(opr == DbAdapter.PRACTICE)
                		jsonData = getDataFromURL(ABC.WEB_URL+"practice/json?code="+s.toString());
                	if(opr == DbAdapter.HOSPITAL)
                		jsonData = getDataFromURL(ABC.WEB_URL+"hospital/json?code="+s.toString());
                	if(opr == DbAdapter.SPECIALTY)
                		jsonData = getDataFromURL(ABC.WEB_URL+"speciality/json?code="+s.toString());
                	if(opr == DbAdapter.INSURANCE)
                		jsonData = getDataFromURL(ABC.WEB_URL+"insurance/json?code="+s.toString());
                	//System.out.println("SMM:INTERNET::"+ABC.WEB_URL+"practice/json?code="+s.toString());
                } catch(Exception ex) {
                	Toast.makeText(ItemSelectActivity.this, "Failed to load data from intenet.", Toast.LENGTH_SHORT).show();
                	System.out.println("SMM:ERROR::"+ex);
                }
                
                //System.out.println("SMM:JSON::"+jsonData);

                try {
                	Map<String, Object[]> map = parseJSONData(jsonData);
                	nameArr = map.get("nameArr");
                	idArr = map.get("idArr");
                } catch(Exception ex) {
                	Toast.makeText(ItemSelectActivity.this, "Failed to parse JSON data.", Toast.LENGTH_SHORT).show();
                	System.out.println(ex);
                	Log.d("JSON:ERROR", ex.getMessage(),ex);
                	//nameArr = new Object[]{"11","22"};
                }
                
                System.out.println("SMM:INFO::"+nameArr.length);
                footerView.setText((String)nameArr[0]);
                */
                for (int i=0, j=0; i < nameArr.length; i++) {
                    if( ((String)nameArr[i]).toLowerCase().contains(s)) { 
                    	autoCompleteAdapter.add((String) nameArr[i]);
                    	//rootIdx[j++] = i;
                    	if(selectedMap.get(nameArr[i]) == null)
                    		itemListView.setItemChecked(j, false);
                    	else
                    		itemListView.setItemChecked(j, true);
                    	j++;
                    }
                        //System.out.println("SMM:INFO::"+nameArr[i]);
                }
                textView.setFocusableInTouchMode(true);
                textView.requestFocus();
                InputMethodManager inputMethodManager = (InputMethodManager) ItemSelectActivity.this
                        .getSystemService(ItemSelectActivity.this.INPUT_METHOD_SERVICE);
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
        Intent intent = getIntent();
        opr = this.getIntent().getIntExtra("opr", 0);
        
        if(opr ==  DbAdapter.PRACTICE)
        	setTitle( getString( R.string.app_name ) + " - Type to Add Practice");
        if(opr ==  DbAdapter.HOSPITAL)
        	setTitle( getString( R.string.app_name ) + " - Type to Add Hospital");
        if(opr ==  DbAdapter.SPECIALTY)
        	setTitle( getString( R.string.app_name ) + " - Type to Add Specialty");
        if(opr ==  DbAdapter.INSURANCE)
        	setTitle( getString( R.string.app_name ) + " - Type to Add Insurance");
        if(opr ==  DbAdapter.ACO)
        	setTitle( getString( R.string.app_name ) + " - Type to Add ACO");        
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
        
        autoCompleteAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice); 
    	//autoCompleteAdapter.setNotifyOnChange(true); // This is so I don't have to manually sync whenever changed 
    	textView = (TextView)findViewById(R.id.item_select_text_edit);
    	footerView = (TextView)findViewById(R.id.item_select_footer);
    	//EditText.setAdapter(autoCompleteAdapter);
    	
    	nameArr = this.getIntent().getStringArrayExtra("nameArr");
    	int idArr1[] = this.getIntent().getIntArrayExtra("idArr");
    	nameIdMap = new HashMap();
    	for (int i=0; i < nameArr.length; i++) {
             autoCompleteAdapter.add((String) nameArr[i]);
             nameIdMap.put((String) nameArr[i], ""+idArr1[i]);
             //System.out.println("SMM:INFO::ID="+idArr1[i]);
        }
        footerView.setText(nameArr.length+" items found");
        rootIdx = new int[nameArr.length];
        
        
    	//autoCompleteAdapter.add("Shamim");
    	
    	itemListView = (ListView)findViewById(R.id.itemSelectList);
    	itemListView.setAdapter(autoCompleteAdapter);
    	itemListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    	
    	selectionArr = this.getIntent().getBooleanArrayExtra("selectionArr");
    	//for (int i=1; i < selectionArr.length; i++) {
    		//itemListView.setItemChecked(i, selectionArr[i]);
    	//}
    	doneBtn = (Button) findViewById(R.id.itemSelectBtn);
    	doneBtn.setText("Choose All");
    	selectedMap = new HashMap();
    	itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	    	
    		public void onItemClick(AdapterView<?> parent, View v, int position, long id){
    			//System.out.println("SMM::["+opr+"]view.text="+parent.getAdapter().getItem(position));
    			String key = parent.getAdapter().getItem(position).toString();
    			if(selectedMap.get(key) == null)
    				selectedMap.put(key, "true");//idArr[position]);
    			else
    				selectedMap.remove(key);
    			if(selectedMap.isEmpty()) {
    				doneBtn.setText("Choose All");
    			} else {
    				doneBtn.setText("Done");
    				if(opr ==  DbAdapter.SPECIALTY || opr ==  DbAdapter.INSURANCE || opr == DbAdapter.PRACTICE || opr == DbAdapter.ACO)
    					finishActivity();
    			}
    			/* only one speciality can be selected */
    			//if(opr ==  DbAdapter.SPECIALTY)
    				//finishActivity();
				
    			//System.out.println("SMM::MAP->size="+selectedMap.size());
    			
    			//selectionArr[position] = !selectionArr[position];
    			//System.out.println("SMM::List["+position+"] = "+selectionArr[position]);
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
	    doneBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finishActivity();
				//Intent intent = new Intent(SetupActivity.this, ItemAddActivity.class);
				//SetupActivity.this.intent.putExtra("opr", DbAdapter.PRACTICE);
	            //startActivityForResult(intent, 1100);				
			}
		});
    }
	
	private void finishActivity(){
		//System.out.println("SMM:INFO::FINSHING....");
		setResult();
		finish();
		overridePendingTransition(R.anim.drop_leave, R.anim.drop_back);
	}
	
	@Override
    public void onStop() {
        super.onStop ();
        dba.close();
    }
	
	@Override
    public void onBackPressed () {
        super.onBackPressed ();
        this.overridePendingTransition(R.anim.drop_leave, R.anim.drop_back);
    }

	private void setResult(){
		this.setResult(RESULT_OK, this.getIntent());
		List<String> list = new ArrayList<String>();
		String keyArr[] = new String[selectedMap.keySet().size()];
		String idArr1[] = new String[selectedMap.keySet().size()];
		int i=0;
		for (Iterator it=selectedMap.keySet().iterator(); it.hasNext(); i++) {
		    keyArr[i] = (String)it.next();
		    idArr1[i] = (String)nameIdMap.get(keyArr[i]);
		}

		System.out.println("SMM::SELECTED-MAP-TO-ARR::"+keyArr);
		this.getIntent().putExtra("selectedNameArr", keyArr);
		this.getIntent().putExtra("selectedIdArr", idArr1);
	}
	
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
