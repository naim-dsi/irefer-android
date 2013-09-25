package com.dsinv.irefer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Selector;
import java.util.ArrayList;
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
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SetupActivity extends Activity {
	
	TextView headline1 = null;
	TextView headline2 = null;
	TextView label1 = null;
	TextView label2 = null;
	TextView label3 = null;
	TextView label4 = null;
	TextView label5 = null;
	TextView label5a = null;
	TextView label5b = null;
	TextView label5c = null;
	TextView label5d = null;
	TextView label6 = null;
	private Button showAllBtn;
	private View layout1;
	private View layout2;
	private View layout3;
	private View layout4;
	private View layout5;
	
	String pracIds = "";   //see onStart method
	String hospIds = "";   //see onStart method
	String specIds = "";   //see onStart method
	String insuIds = "";   //see onStart method
	String cntyIds = "";   //see onStart method
	
	int myPracId = 0;
	int myHospId = 0;
	int myCntyId = 0;
	int myStatId = 0;
	int userId = 0;
	int needToSync = 0;
	int fromPageId = 0;
	int docReceived = 0;
	String myCounty = "";
	
	ProgressDialog dialog;
    int increment = 1;
    private DbAdapter	dba;
    //private List docJsonList = new ArrayList();
    private TextView footerView = null;
    
    private List pracInsertList;
    private List hospInsertList;
    
    private List docInsertList;
    private List docFTSList;
    private List docIdList;
    
    private Map pracMap, hospMap, insuMap, specMap, cntyMap;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_page);
            //setTitle("iRefer");
        setTitle( getString( R.string.app_name ) );
        
        fromPageId = this.getIntent().getIntExtra(Utils.pageId, -1);
        overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_leave);
        //Text = (Button) findViewById(R.id.sync_button1);
        headline1= (TextView)findViewById(R.id.setup_headline1);
        headline2= (TextView)findViewById(R.id.setup_headline2);
        label1= (TextView)findViewById(R.id.setup_label1);
        label2= (TextView)findViewById(R.id.setup_label2);
        label3= (TextView)findViewById(R.id.setup_label3);
        label4= (TextView)findViewById(R.id.setup_label4);
        label5= (TextView)findViewById(R.id.setup_label5);
        label5a= (TextView)findViewById(R.id.setup_label5a);
        label5b= (TextView)findViewById(R.id.setup_label5b);
        label5c= (TextView)findViewById(R.id.setup_label5c);
        label5d= (TextView)findViewById(R.id.setup_label5d);
        label6= (TextView)findViewById(R.id.setup_label6);
        
        layout1= (View)findViewById(R.id.setup_layout1);
        layout2= (View)findViewById(R.id.setup_layout2);
        layout3= (View)findViewById(R.id.setup_layout3);
        layout4= (View)findViewById(R.id.setup_layout4);
        layout5= (View)findViewById(R.id.setup_layout5);
        
        
        //footerView = (TextView)findViewById(R.id.setup_footer);
        
        dba = new DbAdapter(this);
        dba.open();
        
        String userName = "";
        String userAddress = "";
        
        Cursor cr0 = dba.fetchAll(dba.USERS);
        if(cr0 != null && cr0.getCount() > 0) {
        	cr0.moveToFirst();
        	userId   = cr0.getInt(1);
        	myPracId = cr0.getInt(6);
        	myHospId = cr0.getInt(7);
        	myCntyId = cr0.getInt(8);
        	needToSync = cr0.getInt(9);
        	userName = cr0.getString(2) + " " + cr0.getString(3);
        	cr0.close();
        }
        if(dba.getCount(DbAdapter.DOCTOR) == 0) {
        	label6.setText("Please sync doctor database.");
        	label6.setTextColor(Color.RED);
        }
        
        if(myPracId > 0) {
        	Cursor cr1 = dba.fetchByNetId(dba.PRACTICE, myPracId);  //getMyPractice();
            if(cr1 != null) {
            	cr1.moveToFirst();
            	if(cr1.getCount() > 0) {
            		userAddress = cr1.getString(2);
            		//System.out.println("SMM::PRAC::"+cr1.getString(2)+" ADDRESS::"+cr1.getString(3));
            	}
            	cr1.close();
            }
        } else if(myHospId > 0) {
        	Cursor cr1 = dba.fetchByNetId(dba.HOSPITAL, myHospId);  //getMyPractice();
            if(cr1 != null) {
            	cr1.moveToFirst();
            	if(cr1.getCount() > 0) {
            		userAddress = cr1.getString(2);
            		//System.out.println("SMM::HOSP::"+cr1.getString(2)+" ADDRESS::"+cr1.getString(3));
            	}
            	cr1.close();
            }
        }
        if(myCntyId > 0) {
        	Cursor cr1 = dba.fetchByNetId(dba.COUNTY, myCntyId);  //getMyPractice();
            if(cr1 != null) {
            	cr1.moveToFirst();
            	if(cr1.getCount() > 0) {
            		userAddress = userAddress+", "+cr1.getString(2);
            		myCounty = cr1.getString(2);
            		myStatId = cr1.getInt(4);
            	}
            	cr1.close();
            }
        }
        if(myStatId > 0) {
        	Cursor cr1 = dba.fetchByNetId(dba.STATE, myStatId);  //getMyPractice();
            if(cr1 != null) {
            	cr1.moveToFirst();
            	if(cr1.getCount() > 0) {
            		userAddress = userAddress+" "+cr1.getString(3);	
            	}
            	cr1.close();
            }
        }
        headline1.setText(userName);
		headline2.setText(userAddress);
		
        /*
        Button regBtn = (Button) findViewById(R.id.sync_button1);
        regBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
		        dialog = new ProgressDialog(SetupActivity.this);
		        //dialog.setCancelable(true);
		        dialog.setMessage("Downloading...");
		        // set the progress to be horizontal
		        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		        // reset the bar to the default value of 0
		        dialog.setProgress(0);
		        
		        dialog.setMax(5);
		        // display the progressbar
		        dialog.show();
		 
		        // create a thread for updating the progress bar
		        Thread background = new Thread (new Runnable() {
		           public void run() {
		               try {
		                   // enter the code to be run while displaying the progressbar.
		                   //
		                   // This example is just going to increment the progress bar:
		                   // So keep running until the progress value reaches maximum value
		                   while (dialog.getProgress() < dialog.getMax()) {
		                       // wait 500ms between each update
		                       Thread.sleep(1000);
		 
		                       // active the update handler
		                       progressHandler.sendMessage(progressHandler.obtainMessage());
		                       
		                       
		                   }
		                   
		               } catch (java.lang.InterruptedException e) {
		                   // if something fails do something smart
		               } finally{
		            	   dialog.dismiss();
		               }
		           }
		        });
		 
		        // start the background thread
		        background.start();
		        
				label1.setVisibility(View.VISIBLE);				
				label2.setVisibility(View.VISIBLE);
				label3.setVisibility(View.VISIBLE);
				label4.setVisibility(View.VISIBLE);
				label5.setVisibility(View.VISIBLE);
			}
		});
        */
        Button regBtn2 = (Button) findViewById(R.id.sync_button2);
        regBtn2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
    		        public void onClick(DialogInterface dialogI, int which) {
    		            switch (which){
    		            case DialogInterface.BUTTON_POSITIVE:
    		            	dialog = ProgressDialog.show(SetupActivity.this, "", 
    		                        "Starting sync.", true);
    						dialog.setTitle("Syncing, this may take few minutes");
    						dialog.show();
    		            	try {
    		            		//dba.deleteAll(DbAdapter.DOCTOR);
    		            		//dba.deleteAll(DbAdapter.DOC_FTS);
    		            		//dba.deleteAll(DbAdapter.INSURANCE);
    		            		//dba.deleteAll(DbAdapter.SPECIALTY);
    		            		//dba.deleteAllSettingPractice();
    		            		//dba.deleteAllSettingHospital();
					
    		            		new SyncDoctorTask().execute();
    		            	} catch(Exception ex){
    		            		ex.printStackTrace();
    		            		dialog.dismiss();
    		            	}
    		            case DialogInterface.BUTTON_NEGATIVE:
    		                //No button clicked
    		                break;
    		            }
    		        }
				};
				AlertDialog.Builder builder = new AlertDialog.Builder(SetupActivity.this);
    		    builder.setMessage("Depending on the number of counties you selected and your internet connection speed, this download may take at least several minutes.").setPositiveButton("Yes", dialogClickListener)
    		        .setNegativeButton("No", dialogClickListener).show();
				
			}
		});
        
        Button regBtn3 = (Button) findViewById(R.id.reset_button);
        regBtn3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
    		        public void onClick(DialogInterface dialog, int which) {
    		            switch (which){
    		            case DialogInterface.BUTTON_POSITIVE:
    		            	dba.deleteAllSettingPractice();
    		            	dba.deleteAllSettingHospital();
    		            	dba.delete(DbAdapter.SPECIALTY, 0);
    		            	dba.delete(DbAdapter.INSURANCE, 0);
    		            	dba.delete(DbAdapter.DOCTOR, 0);
    		            	if(myPracId > 0)
    		            		label1.setText("1 Practices");
    		            	else
    		            		label1.setText("0 Practices");
    		            	if(myHospId > 0)	
    		            		label2.setText("1 Hospital");
    		            	else
    		            		label2.setText("0 Hospital");
    		            	label3.setText("0 Speciality");
    		            	label4.setText("0 insurance");
    		            	label5.setText("1 county");
    		            	label6.setText("Download the doctor database");
    		            	label6.setTextColor(Color.RED);
    		            	Toast.makeText(SetupActivity.this, "Reset Done.", Toast.LENGTH_SHORT).show();
    		            	break;

    		            case DialogInterface.BUTTON_NEGATIVE:
    		                //No button clicked
    		                break;
    		            }
    		        }
    		    };
    			
    			AlertDialog.Builder builder = new AlertDialog.Builder(SetupActivity.this);
    		    builder.setMessage("Are you sure to delete all settings? Only the registration info will be kept.").setPositiveButton("Delete", dialogClickListener)
    		        .setNegativeButton("Cancel", dialogClickListener).show();
				//Intent intent = new Intent(SetupActivity.this, ActivationFormActivity.class);
            	//intent.putExtra("operation", 1);
	            //startActivityForResult(intent, 1100);				
			}
		});
        
        showAllBtn = (Button) findViewById(R.id.setup_show_all_button);
        showAllBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if("Basic View".equals(showAllBtn.getText())) {
					showAllBtn.setText("Show All Setting");
					layout1.setVisibility(View.GONE);
					layout2.setVisibility(View.GONE);
					layout3.setVisibility(View.GONE);
					layout4.setVisibility(View.GONE);
					//layout5.setVisibility(View.GONE);
				} else {
					showAllBtn.setText("Basic View");
					layout1.setVisibility(View.VISIBLE);
					layout2.setVisibility(View.VISIBLE);
					layout3.setVisibility(View.VISIBLE);
					layout4.setVisibility(View.VISIBLE);
					//layout5.setVisibility(View.VISIBLE);
				}
			}
        });
        
        Button tmplBtn = (Button) findViewById(R.id.apply_template_button);
        tmplBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
    		        public void onClick(DialogInterface dialog, int which) {
    		            switch (which){
    		            case DialogInterface.BUTTON_POSITIVE:
    		            	TemplateProcessor tp = new TemplateProcessor(SetupActivity.this, dba);
							if(myPracId > 0)
								tp.updateSetupPCP(myPracId, myCntyId);
							else if(myHospId > 0)
								tp.updateSetupHospitalist(myHospId);
    		            	Toast.makeText(SetupActivity.this, "Reset Done.", Toast.LENGTH_SHORT).show();
    		            	break;

    		            case DialogInterface.BUTTON_NEGATIVE:
    		                //No button clicked
    		                break;
    		            }
    		        }
    		    };
    			
    			AlertDialog.Builder builder = new AlertDialog.Builder(SetupActivity.this);
    		    builder.setMessage("Reset Template Data?.").setPositiveButton("Download", dialogClickListener)
    		        .setNegativeButton("Cancel", dialogClickListener).show();
				//Intent intent = new Intent(SetupActivity.this, ActivationFormActivity.class);
            	//intent.putExtra("operation", 1);
	            //startActivityForResult(intent, 1100);				
			}
		});
        
        Button addBtn1 = (Button) findViewById(R.id.add_button1);
        addBtn1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(SetupActivity.this, ItemAddActivity.class);
            	intent.putExtra("opr", DbAdapter.PRACTICE);
            	startActivityForResult(intent, 1100);				
			}
		});
        
        //if(myPracId > 0)
        //	addBtn1.setVisibility(View.INVISIBLE);
        
        Button addBtn2 = (Button) findViewById(R.id.add_button2);
        addBtn2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(SetupActivity.this, ItemAddActivity.class);
            	intent.putExtra("opr", DbAdapter.HOSPITAL);
	            startActivityForResult(intent, 1100);				
			}
		});
        
        //if(myHospId > 0)
        //	addBtn2.setVisibility(View.INVISIBLE);
        
        Button addBtn3 = (Button) findViewById(R.id.add_button3);
        addBtn3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(SetupActivity.this, ItemAddActivity.class);
            	intent.putExtra("opr", DbAdapter.SPECIALTY);
	            startActivityForResult(intent, 1100);				
			}
		});
        Button addBtn4 = (Button) findViewById(R.id.add_button4);
        addBtn4.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(SetupActivity.this, ItemAddActivity.class);
            	intent.putExtra("opr", DbAdapter.INSURANCE);
	            startActivityForResult(intent, 1100);				
			}
		});
        Button addBtn5 = (Button) findViewById(R.id.add_button5);
        addBtn5.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(SetupActivity.this, ItemAddActivity.class);
            	intent.putExtra("opr", DbAdapter.COUNTY);
            	startActivityForResult(intent, 1100);				
			}
		});
        
        LinearLayout layout1 = (LinearLayout) findViewById(R.id.setup_layout1);
        layout1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(SetupActivity.this, ItemViewActivity.class);
				intent.putExtra("opr", DbAdapter.PRACTICE);
				intent.putExtra("myPracId", myPracId);
				intent.putExtra("myHospId", myHospId);
				intent.putExtra("userId", userId);
	            startActivityForResult(intent, 1100);				
			}
		});
        
        LinearLayout layout2 = (LinearLayout) findViewById(R.id.setup_layout2);
        layout2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(SetupActivity.this, ItemViewActivity.class);
				intent.putExtra("opr", DbAdapter.HOSPITAL);
				intent.putExtra("myHospId", myHospId);
            	startActivityForResult(intent, 1100);				
			}
		});
        
        LinearLayout layout3 = (LinearLayout) findViewById(R.id.setup_layout3);
        layout3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(SetupActivity.this, ItemViewActivity.class);
				intent.putExtra("opr", DbAdapter.SPECIALTY);
            	startActivityForResult(intent, 1100);				
			}
		});
        
        LinearLayout layout4 = (LinearLayout) findViewById(R.id.setup_layout4);
        layout4.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(SetupActivity.this, ItemViewActivity.class);
				intent.putExtra("opr", DbAdapter.INSURANCE);
            	startActivityForResult(intent, 1100);				
			}
		});
        
        LinearLayout layout5 = (LinearLayout) findViewById(R.id.setup_layout5);
        layout5.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(SetupActivity.this, ItemViewActivity.class);
				intent.putExtra("opr", DbAdapter.COUNTY);
				intent.putExtra("myCntyId", myCntyId);
	            startActivityForResult(intent, 1100);				
			}
		});
        
        Button searchBtn = (Button) findViewById(R.id.setup_bottom_button3);
        searchBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(fromPageId == Utils.FILTER_PAGE) {
					finish();
				} else {
					Intent intent = new Intent(SetupActivity.this, FilterPageActivity.class);
					intent.putExtra(Utils.pageId, Utils.SETUP_PAGE);
					startActivityForResult(intent, 1100);
				}
			}
		});
        Button backBtn = (Button) findViewById(R.id.setup_bottom_button1);
        backBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(SetupActivity.this, AboutPageActivity.class);
            	startActivityForResult(intent, 1006);			
			}
		});
        Button textSearchBtn = (Button) findViewById(R.id.setup_bottom_button2);
        textSearchBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(SetupActivity.this, DoctorListAdvanceActivity.class);
            	startActivityForResult(intent, 1200);			
			}
		});
        
    }
    
    @Override
    public void onBackPressed () {
        super.onBackPressed ();
        this.overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_back);
    }
	
	public String getDataFromURL(String urlStr) throws Exception {
		System.out.println("SMM::"+(System.currentTimeMillis()/1000)%1000+"::URL::"+urlStr);
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
	    System.out.println("SMM::"+(System.currentTimeMillis()/1000)%1000+"::CLOSED");
	    return data;
    }
    
	private void insertDoctor(String[] arr){
		docInsertList.add(arr);
	}
	
	private void insertDoctorFTS(String fts, String docId){			
		docFTSList.add(fts);
		docIdList.add(docId);
	}
	
	private void saveDoctorFTS(JSONObject jsonObj) throws Exception{
		String ftsText = "";
    	
    	if(!Utils.isEmptyNumber(jsonObj.getString("c14")))
    			ftsText += ("".equals(ftsText) ? "" : ",") + (String)pracMap.get(jsonObj.getString("c14"));
    	if(!Utils.isEmpty(jsonObj.getString("c15")))
    			ftsText += ("".equals(ftsText) ? "" : ",") +  (String)specMap.get(jsonObj.getString("c15"));
    	if(!Utils.isEmpty(jsonObj.getString("c16")))
    			ftsText += ("".equals(ftsText) ? "" : ",") +  (String)hospMap.get(jsonObj.getString("c16"));
    	if(!Utils.isEmpty(jsonObj.getString("c17")))
    			ftsText += ("".equals(ftsText) ? "" : ",") +  (String)insuMap.get(jsonObj.getString("c17"));
    	if(!Utils.isEmpty(jsonObj.getString("c18")))
    			ftsText += ("".equals(ftsText) ? "" : ",") +  (String)cntyMap.get(jsonObj.getString("c18"));
    	
		ftsText += ("".equals(ftsText) ? "" : ",") +  jsonObj.getString("c1");
		ftsText += ("".equals(ftsText) ? "" : ",") +  jsonObj.getString("c2");
		ftsText += ("".equals(ftsText) ? "" : ",") +  jsonObj.getString("c3");
		ftsText += ("".equals(ftsText) ? "" : ",") +  jsonObj.getString("c4");
		ftsText += ("".equals(ftsText) ? "" : ",") +  jsonObj.getString("c5");
		ftsText += ("".equals(ftsText) ? "" : ",") +  jsonObj.getString("c6");
		ftsText += ("".equals(ftsText) ? "" : ",") +  jsonObj.getString("c10");
		ftsText += ("".equals(ftsText) ? "" : ",") +  jsonObj.getString("c11");
    	
		insertDoctorFTS(ftsText, jsonObj.getString("id"));
	}
	
    private void saveDoctor(JSONObject jsonObj) throws Exception{
    	//System.out.println("SMM::FTS-TEXT::"+ftsText);
		//long docId = dba.insertDoctor(ftsText, new String[]{jsonObj.getString("id"),
		insertDoctor(new String[]{jsonObj.getString("id"),
				jsonObj.getString("c1"),jsonObj.getString("c2"),
				(Utils.isEmpty(jsonObj.getString("c3")) ?"":jsonObj.getString("c3")),
				jsonObj.getString("c4"),
				jsonObj.getString("c5"),jsonObj.getString("c6"),
				jsonObj.getString("c7"),
				jsonObj.getString("c8"),
				(jsonObj.getString("c9").equals("null") ? "": jsonObj.getString("c9")),
				(jsonObj.getString("c14").equals("null") ? "0": jsonObj.getString("c14")),
				(jsonObj.getString("c16").equals("null") ? "0": jsonObj.getString("c16")),
				(jsonObj.getString("c15").equals("null") ? "0": jsonObj.getString("c15")),
				(jsonObj.getString("c17").equals("null") ? "0" : jsonObj.getString("c17")),
				jsonObj.getString("c19"), "0", "0",
				(jsonObj.getString("c13").equals("1") ? "1" : "0"),
				jsonObj.getString("c11"),jsonObj.getString("c10"),
				jsonObj.getString("c12"),
				(Utils.isEmpty(jsonObj.getString("u_rank")) ? "0" : jsonObj.getString("u_rank")),
				(Utils.isEmpty(jsonObj.getString("up_rank")) ? "0" : jsonObj.getString("up_rank")),
				(Utils.isEmpty(jsonObj.getString("c21")) ? "0" : jsonObj.getString("c21")),
				(Utils.isEmpty(jsonObj.getString("c22")) ? "0" : jsonObj.getString("c22")),
				(Utils.isEmpty(jsonObj.getString("c23")) ? "0" : jsonObj.getString("c23")),
				(Utils.isEmpty(jsonObj.getString("c24")) ? "0" : jsonObj.getString("c24"))
			});
		//System.out.println("SMM::DOC-SYNC::NEW-DOC-Id::"+docId);
    }
	
	
	@Override
    public void onStop() {
        super.onStop();
        //dba.close();
	}
    
	public void loadPractice() {
		pracMap = new HashMap();
		Cursor cr = dba.fetchAll(DbAdapter.PRACTICE);
    	if(cr != null) {
    		label1.setText(cr.getCount()+" Practices");
    		if(cr.getCount() > 0) {
            	cr.moveToFirst();
            	for(int i=0; i<cr.getCount(); i++) {
            		if( i>0 )  pracIds = pracIds+",";
            		pracIds = pracIds+cr.getInt(1);
            		pracMap.put(cr.getInt(1)+"", cr.getString(2));
            		cr.moveToNext();
            	}
            }
    		cr.close();
    	}
	}
	
	@Override
    public void onStart(){   
		super.onStart();
		pracIds = hospIds = insuIds = specIds = cntyIds = "";
		
		pracMap = new HashMap();
		hospMap = new HashMap();
		insuMap = new HashMap();
		specMap = new HashMap();
		cntyMap = new HashMap();

		Cursor cr0 = dba.fetchAll(dba.USERS);
        if(cr0 != null && cr0.getCount() > 0) {
        	cr0.moveToFirst();
        	needToSync = cr0.getInt(9);
        	cr0.close();
        }
		if(needToSync == 1) {
			label5d.setVisibility(View.VISIBLE);
		} else {
			label5d.setVisibility(View.GONE);
		}
		
		label1.setText(dba.getCount(DbAdapter.PRACTICE)+" Practices");
		label2.setText(dba.getCount(DbAdapter.HOSPITAL)+" Hospital");
		label3.setText(dba.getCount(DbAdapter.SPECIALTY)+" Speciality");
		label4.setText(dba.getCount(DbAdapter.INSURANCE)+" Insurance");
		long cntyCount = dba.getCount(DbAdapter.COUNTY);
		if(cntyCount == 1)
			label5.setText(myCounty);
		else	
			label5.setText(cntyCount+" County");
		
		if(!Utils.isOnline(this)) {
			AlertDialog dialog = ProgressDialog.show(SetupActivity.this, "", 
                    "Loading user profile...", true);
			dialog.show();
			TemplateProcessor tp = new TemplateProcessor(this, dba);
			tp.updateDocReport(userId);
			tp.updateSearchCount(userId);
			dialog.dismiss();
		}
		
		if(!Utils.isOnline(this)) {
			label5a.setText("You need to have internet connectivity to Sync & setup the application.  Once you are done, you can search OffLine");
			label5a.setTextColor(Color.RED);
			label5b.setVisibility(View.GONE);
			label5c.setVisibility(View.GONE);
		} else {
			label5a.setText("�"+myCounty+"� is your default county.  If you would like to search for providers from other counties, please select additional counties now (Max is 4).");
			label5b.setVisibility(View.VISIBLE);
			label5c.setVisibility(View.VISIBLE);
			label5b.setText("Then Select �Download the doctor database�");
			label5c.setText("To clear the database and start over, �Reset All�.");
		}
		
		/*
		Cursor cr = dba.fetchAll(DbAdapter.HOSPITAL);
    	if(cr != null) {
    		label2.setText(cr.getCount()+" Hospital");
    		if(cr.getCount() > 0) {
            	cr.moveToFirst();
            	for(int i=0; i<cr.getCount(); i++) {
            		if( i>0 )  hospIds = hospIds+",";
            		hospIds = hospIds+cr.getInt(1);
            		hospMap.put(cr.getInt(1)+"", cr.getString(2));
            		cr.moveToNext();
            	}
            }
    		cr.close();
    	}
    	cr = dba.fetchAll(DbAdapter.SPECIALTY);
    	if(cr != null) {
    		label3.setText(cr.getCount()+" Specialty");
    		if(cr.getCount() > 0) {
            	cr.moveToFirst();
            	for(int i=0; i<cr.getCount(); i++) {
            		if( i>0 )  specIds = specIds+",";
            		specIds = specIds+cr.getInt(1);
            		specMap.put(cr.getInt(1)+"", cr.getString(2));
            		cr.moveToNext();
            	}
            }
    		cr.close();
    	}
    	cr = dba.fetchAll(DbAdapter.INSURANCE);
    	if(cr != null) {
    		label4.setText(cr.getCount()+" Insurance");
    		if(cr.getCount() > 0) {
            	cr.moveToFirst();
            	for(int i=0; i<cr.getCount(); i++) {
            		if( i>0 )  insuIds = insuIds+",";
            		insuIds = insuIds+cr.getInt(1);
            		insuMap.put(cr.getInt(1)+"", cr.getString(2));
            		cr.moveToNext();
            	}
            }
    		cr.close();
    	}
    	*/
    	Cursor cr = dba.fetchAll(DbAdapter.COUNTY);
    	if(cr != null) {
    		if(cr.getCount() > 0) {
            	cr.moveToFirst();
            	for(int i=0; i<cr.getCount(); i++) {
            		if( i>0 )  cntyIds = cntyIds+",";
            		cntyIds = cntyIds+cr.getInt(1);
            		//cntyMap.put(cr.getInt(1)+"", cr.getString(2));
            		cr.moveToNext();
            	}
            }
    		cr.close();
    	}
    	
    	//if(needToSync == 1) {
        //   	label6.setText("Please Sync Doctors!");
        //   	label6.setTextColor(Color.RED);
        //} else {
           	label6.setText(dba.getDoctorCount()+" Doctors Synced");
           	label6.setTextColor(Color.WHITE);
        //}
        
    }
    Handler progressHandler = new Handler() {
        public void handleMessage(Message msg) {
            dialog.incrementProgressBy(increment);
        }
    };
    
    private class SyncDoctorTask extends AsyncTask<URL, Integer, Long> {
    	
    	public Object fetch(URL url) throws MalformedURLException,IOException {
    		Object content = url.getContent();
    		return content;
    	}
    
    	public void parseJSONData(String jsonData, int opt, String type) throws Exception {
        	System.out.println("SMM::"+(System.currentTimeMillis()/1000)%1000+"::JSON-START");
    		JSONTokener jsonTokener = new JSONTokener(jsonData);
        	JSONArray arr = (JSONArray) jsonTokener.nextValue();
        	
        	if(opt == DbAdapter.PRACTICE)
    			dba.deleteAllSettingPractice();
    		else if(opt == DbAdapter.HOSPITAL)
    			dba.deleteAllSettingHospital();
    		else if(opt == DbAdapter.SPECIALTY) {
    			//if("1".equals(type))
    			dba.deleteAll(opt);
    		} else
    			dba.deleteAll(opt);
        	
        	//System.out.println("SMM::"+(System.currentTimeMillis()/1000)%1000+"::JSON-PARSED");
        	int saved =0, failed = 0;
        	//docReceived = arr.length();
        	int i=0, j=arr.length();
        	System.out.println("["+opt+"] LEN="+j);
        	if(opt == DbAdapter.INSURANCE || opt == DbAdapter.SPECIALTY || opt == DbAdapter.STATE)
        		i = 1;
        	while(i < j) { 
        		JSONObject jsonObj = arr.getJSONObject(i++);
        		//System.gc();
        		switch(opt){
        		case DbAdapter.PRACTICE:
        			if((myPracId+"").equals(jsonObj.getString("id")))
        				break;
        			//dba.insert(opt,
        			if(pracInsertList == null)
        				pracInsertList = new ArrayList();
        			pracInsertList.add(new String[]{jsonObj.getString("id"),
							 	jsonObj.getString("name"), jsonObj.getString("add_line_1")} );
        			pracMap.put(jsonObj.getString("id"), jsonObj.getString("name"));
        			break;
        		case DbAdapter.HOSPITAL:
        			if((myHospId+"").equals(jsonObj.getString("id")))
            			break;
        			if(hospInsertList == null)
        				hospInsertList = new ArrayList();
        			//dba.insert(opt, 
        			hospInsertList.add(new String[]{jsonObj.getString("id"),
							 	jsonObj.getString("name"), jsonObj.getString("add_line_1")} );
        			hospMap.put(jsonObj.getString("id"), jsonObj.getString("name"));
        			break;
        		case DbAdapter.SPECIALTY:
        			dba.insert(opt, new String[]{jsonObj.getString("id"),
							 	jsonObj.getString("name"), "", jsonObj.getString("group_id")} );
        			specMap.put(jsonObj.getString("id"), jsonObj.getString("name"));
        			break;
        		case DbAdapter.INSURANCE:
        			dba.insert(opt, new String[]{jsonObj.getString("id"),
							 	jsonObj.getString("name"), ""} );
        			insuMap.put(jsonObj.getString("id"), jsonObj.getString("name"));
        			break;
        		case DbAdapter.STATE:
        			dba.insert(opt, new String[]{jsonObj.getString("id"),
							 	jsonObj.getString("name"), jsonObj.getString("code")} );
        			break;
        		}
        	}
        	System.out.println("SMM::"+(System.currentTimeMillis()/1000)%1000+"::JSON-FINISH");
    		if(opt == DbAdapter.PRACTICE) {
    			dba.insert(DbAdapter.PRACTICE, pracInsertList);
    			pracInsertList = null;
    			System.gc();
    		} else if(opt == DbAdapter.HOSPITAL){
    			dba.insert(DbAdapter.HOSPITAL, hospInsertList);
    			hospInsertList = null;
    			System.gc();
    		}
    		jsonTokener = null;
    		arr = null;
    		System.gc();
    	}
    	
    	public void parseDoctorJSONData(String jsonData) throws Exception {
        	System.out.println("SMM::"+(System.currentTimeMillis()/1000)%1000+"::JSON-START");
    		JSONTokener jsonTokener = new JSONTokener(jsonData);
        	JSONArray arr = (JSONArray) jsonTokener.nextValue();
        	System.out.println("SMM::"+(System.currentTimeMillis()/1000)%1000+"::JSON-PARSED");
        	int saved =0, failed = 0;
        	docReceived = arr.length();
        	docInsertList = new ArrayList();;
            for(int i=0, j=arr.length(); i < j; i++) { 
        		JSONObject object = arr.getJSONObject(i);
        		//try {
        			saveDoctor(object);
        			saved++;
        		//} catch(Exception ex){
        		//	ex.printStackTrace();
        		//	failed++;
        		//}
        		if((i+1)%100 == 0) publishProgress(i+1);
        		object = null;
        	}
        	//System.out.println("SMM::"+(System.currentTimeMillis()/1000)%1000+"::JSON-FINISH");
        	dba.insertDoctor(docFTSList, docInsertList);
        	docInsertList = null;
        	docFTSList = null;
        	System.gc();
        	publishProgress(-6);
        	
        	
        	int x = 0, y=2000;
        	docFTSList = new ArrayList();
            docIdList = new ArrayList();
        	for(int i=0, j=arr.length(); i < j; i++) { 
        		
        		if( i>0 && i%2000 == 0){
        			//System.out.println("SMMM:::::::::::::::2000::::::::::::::"+i);
        			dba.insertDoctorFTS(docFTSList, docIdList);
                	docFTSList = null;
                	docIdList = null;
                	System.gc();
        			docFTSList = new ArrayList();
                    docIdList = new ArrayList();
        		}
        		JSONObject object = arr.getJSONObject(i);
        		saveDoctorFTS(object);
        		object = null;
        	}
        	dba.insertDoctorFTS(docFTSList, docIdList);
        	docFTSList = null;
        	docIdList = null;
        	System.gc();
        	System.out.println("SMM::"+(System.currentTimeMillis()/1000)%1000+"::JSON-SAVED");
    	}
    	
        protected Long doInBackground(URL... urls) {
        	//int count = urls.length;
        	
        	String jsonData = "";
        	String urlString = "";
            int start = 0, end = Utils.docSyncStep;
			boolean flag = true;
			try {
				publishProgress(-1);
				urlString = ABC.WEB_URL+"practice/jsonCounty?cnty_id="+cntyIds;
				jsonData = getDataFromURL(urlString);
				Log.d("NI","URL Practice::"+urlString);
				Log.d("NI","JSONDATA Practice::"+jsonData);
				parseJSONData(jsonData, DbAdapter.PRACTICE, null);
				
				publishProgress(-2);
				urlString = ABC.WEB_URL+"hospital/jsonCounty?cnty_id="+cntyIds;
				jsonData = getDataFromURL(urlString);
				Log.d("NI","URL Hospital::"+urlString);
				Log.d("NI","JSONDATA Hospital::"+jsonData);
				parseJSONData(jsonData, DbAdapter.HOSPITAL, null);
				
				publishProgress(-4);
				urlString = ABC.WEB_URL+"speciality/json?limit=1000";
				jsonData = getDataFromURL(urlString);
				Log.d("NI","URL Speciality::"+urlString);
				Log.d("NI","JSONDATA Speciality::"+jsonData);
				parseJSONData(jsonData, DbAdapter.SPECIALTY, "");
				
				publishProgress(-3);
				urlString = ABC.WEB_URL+"insurance/json";
				jsonData = getDataFromURL(urlString);
				Log.d("NI","URL Insurance::"+urlString);
				Log.d("NI","JSONDATA Insurance::"+jsonData);
				parseJSONData(jsonData, DbAdapter.INSURANCE, null);
				
				/*
				jsonData = getDataFromURL(ABC.WEB_URL+"speciality/json?type=2");
				parseJSONData(jsonData, DbAdapter.SPECIALTY, "2");
				
				jsonData = getDataFromURL(ABC.WEB_URL+"speciality/json?type=3");
				parseJSONData(jsonData, DbAdapter.SPECIALTY, "3");
				*/
				if(dba.getCount(DbAdapter.STATE) == 0){
					urlString = ABC.WEB_URL+"state/json";
					jsonData = getDataFromURL(urlString);
					Log.d("NI","URL State::"+urlString);
					Log.d("NI","JSONDATA State::"+jsonData);
					parseJSONData(jsonData, DbAdapter.STATE, null);	
				}
				dba.deleteAll(DbAdapter.DOCTOR);
				dba.deleteAll(DbAdapter.DOC_FTS);
				while(flag) {
					String limit  = start+","+Utils.docSyncStep; 
					try {		
						//dialog.setMessage("Downloading Doctor Data...");
						publishProgress(-5);
						urlString = ABC.WEB_URL+"doctor/json?prac_ids=1&cnty_ids="+cntyIds
								//+"&insu_ids="+insuIds
								//+"&spec_ids="+specIds+"&hosp_ids="+hospIds
								+"&limit="+limit+"&user_id="+userId;
						jsonData = getDataFromURL(urlString);
						Log.d("NI","URL Doctor::"+urlString);
						//Log.d("NI","JSONDATA Doctor::"+jsonData);
						docReceived = 0;
						publishProgress(0);
						parseDoctorJSONData(jsonData);
						//publishProgress(100);
	                	
						//System.out.println("SMM:INTERNET::"+ABC.WEB_URL+"practice/json?code="+s.toString());
					} catch(Exception ex) {
						//Toast.makeText(SetupActivity.this, "Failed to load data from intenet.", Toast.LENGTH_SHORT).show();
						//System.out.println("SMM:ERROR::"+ex);
						ex.printStackTrace();
						flag = false;
					}
					//footerView.setText(end+" doctor added");
					if(docReceived < end-start || end >= Utils.docSyncLimit)
						flag = false;
					
					start = end;
					end = end+Utils.docSyncStep;
               	}
           } catch (Exception e) {
        	   //System.out.println("SMM::ERROR::"+e);
        	   e.printStackTrace();
               // if something fails do something smart
           }
           return 0L;
        }

        protected void onProgressUpdate(Integer... progress) {
        	if(progress[0] == -1)
        		dialog.setMessage("Syncing Practice...");
        	else if(progress[0] == -2)
        		dialog.setMessage("Syncing Hospital...");
        	else if(progress[0] == -3)
        		dialog.setMessage("Syncing Insurance...");
        	else if(progress[0] == -4)
        		dialog.setMessage("Syncing Speciality...");
        	else if(progress[0] == -5)
        		dialog.setMessage("Syncing Doctors...");
        	else if(progress[0] == -6)
        		dialog.setMessage("Preparing Doctor Database.");
        	else if(progress[0] == 0)
        		dialog.setMessage("Analyzing data...");
        	else if(progress[0] > 0)
        		dialog.setMessage("Indexing data...("+progress[0]+"/"+docReceived+")");
            //setProgressPercent(progress[0]);
        }

        protected void onPostExecute(Long result) {
        	dialog.dismiss();
    	   	dba.synced();
    	   	/*
    	   	Cursor cr1 = dba.fetchAll(DbAdapter.DOCTOR);
       		if(cr1 != null) {
       			label6.setText(cr1.getCount()+" Doctors Synced");
       			cr1.close();
       		}
       		*/
       		label1.setText(dba.getCount(DbAdapter.PRACTICE)+" Practices");
    		label2.setText(dba.getCount(DbAdapter.HOSPITAL)+" Hospital");
    		label3.setText(dba.getCount(DbAdapter.SPECIALTY)+" Speciality");
    		label4.setText(dba.getCount(DbAdapter.INSURANCE)+" Insurance");
    		label5.setText(dba.getCount(DbAdapter.COUNTY)+" Counties");
    		label6.setText(dba.getDoctorCount()+" Doctors Synced");
    		label5d.setVisibility(View.GONE);
   			
        }
    }
    
}