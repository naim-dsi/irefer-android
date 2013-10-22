package com.dsinv.irefer;

import com.dsinv.irefer.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class FilterPageActivity extends Activity {
	
	private DbAdapter	dba;
	
	private TextView headline1;
	private TextView headline2;
	private TextView insuranceValue;
	private TextView specialityValue;
	private TextView hospitalValue;
	private TextView practiceValue;
	private TextView countyValue;
	private TextView languageValue;
	private TextView acoValue;
	private TextView officeHourValue;
	private TextView zipCodeValue;
	private TextView docNameValue;
	
	private View officeHourBtn;
	private View languageBtn;
	private View acoBtn;
	private Button showAdvBtn;
	
	private Button onlineBtn;
	private Button offlineBtn;
	private Button docBtn;
	private Button resBtn;
	
	//private ToggleButton goOnline;
	//private ToggleButton isResource;
	
	private String[] insuranceNameArr = {"NA"};
	private String[] specialityNameArr = {"NA"};
	private String[] specialityResNameArr = {"NA"};
	private String[] hospitalNameArr = {"NA"};
	private String[] practiceNameArr = {"NA"};
	private String[] countyNameArr = {"NA"};
	private String[] languageArr = {"NA"};
	private String[] acoArr = {"NA"};
	private String[] officeHourArr = {"NA"};
	
	private boolean[] insuranceSelection = {false};
	private boolean[] specialitySelection = {false};
	private boolean[] specialityResSelection = {false};
	private boolean[] hospitalSelection = {false};
	private boolean[] practiceSelection = {false};
	private boolean[] countySelection = {false};
	private boolean[] languageSelection = {false};
	private boolean[] acoSelection = {false};
	private boolean[] officeHourSelection = {false};
	
	private int[] insuranceIdArr = {0};
	private int[] specialityIdArr = {0};
	private int[] specialityResIdArr = {0};
	private int[] hospitalIdArr = {0};
	private int[] practiceIdArr = {0};
	private int[] countyIdArr = {0};
	private int[] languageIdArr = {0};
	private int[] acoIdArr = {0};
	private int[] officeHourIdArr = {0};
	
	private String insuranceName = "";
	private String insuranceId = "";
	private String specialityName = "";
	private String specialityId = "";
	private String hospitalName = "";
	private String hospitalId = "";
	private String practiceName = "";
	private String practiceId = "";
	private String countyName = "";
	private String countyId = "";

	private String acoId = "";
	private String languageName = "";
	private String acoName = "";
	private String languageId = "";
	private String officeHourName = "";
	private String officeHourId = "";
	
	private int myPracId = 0;
	private int myHospId = 0;
	private int myCntyId = 0;
	private int myStatId = 0;
	private int userId = 0;
	private int fromPageId = 0;
	
	private int onlineFlag = 0;
	private int resourceFlag = 0;

	
	ScrollView scrollView;
	AlertDialog.Builder builder;
	DialogInterface.OnClickListener dialogClickListener;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle( getString( R.string.app_name ) + " - Search");
        
        fromPageId = this.getIntent().getIntExtra(Utils.pageId, -1);
        
       
        //faisal > added
        //onlineSearch = (CheckBox) findViewById(R.id.online_search_checkbox);
        //goOnline   = (ToggleButton) findViewById(R.id.online_search_toggle);
        //isResource = (ToggleButton) findViewById(R.id.filter_res_toggle);
        //System.out.println("SMM::DOC-NAME---------------------------------------="+docNameValue.getText());
        dba = new DbAdapter(this);
        dba.open();
        String userName = "";
        String pracName = "";
        Cursor cr0 = dba.fetchAll(dba.USERS);
        if(cr0 != null && cr0.getCount() > 0) {
        	cr0.moveToFirst();
        	userId = cr0.getInt(1);
        	myPracId = cr0.getInt(6);
        	myHospId = cr0.getInt(7);
        	myCntyId = cr0.getInt(8);
        	userName = cr0.getString(2) + " " + cr0.getString(3);
        	cr0.close();
        }
        if(myPracId > 0 )
        	setContentView(R.layout.filter_page);
        else
        	setContentView(R.layout.filter_page_hosp);
        headline1       = (TextView) findViewById(R.id.filterHeadline1);
        headline2       = (TextView) findViewById(R.id.filterHeadline2);
        insuranceValue  = (TextView) findViewById(R.id.filterInsuranceValue);
        specialityValue = (TextView) findViewById(R.id.filterSpecialityValue);
        hospitalValue   = (TextView) findViewById(R.id.filterHospitalValue);
        practiceValue   = (TextView) findViewById(R.id.filterPracticeValue);
        countyValue     = (TextView) findViewById(R.id.filterCountyValue);
        languageValue   = (TextView) findViewById(R.id.filterLanguageValue);
        acoValue   = (TextView) findViewById(R.id.filterACOValue);
        
        officeHourValue = (TextView) findViewById(R.id.filterOfficeHourValue);
        zipCodeValue    = (TextView) findViewById(R.id.zip_code_text_edit);
        docNameValue    = (TextView) findViewById(R.id.doc_name_text_edit);
        scrollView      = (ScrollView)findViewById(R.id.filterScrollView01);
        
        if(myPracId > 0) {
        	Cursor cr1 = dba.fetchByNetId(dba.PRACTICE, myPracId);  //getMyPractice();
            if(cr1 != null) {
            	cr1.moveToFirst();
            	if(cr1.getCount() > 0) {
            		pracName = cr1.getString(2);
            		//System.out.println("SMM::PRAC::"+cr1.getString(2)+" ADDRESS::"+cr1.getString(3));
            	}
            	cr1.close();
            }
        } else if(myHospId > 0) {
        	Cursor cr1 = dba.fetchByNetId(dba.HOSPITAL, myHospId);  //getMyPractice();
            if(cr1 != null) {
            	cr1.moveToFirst();
            	if(cr1.getCount() > 0) {
            		pracName = cr1.getString(2);
            		//headline1.setText(cr1.getString(2));
            		//headline2.setText(cr1.getString(3));
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
            		countyValue.setText(cr1.getString(2));
            		countyId = cr1.getInt(1)+"";
            		pracName = pracName + "," + cr1.getString(2);
            		myStatId = cr1.getInt(4);
            		//headline2.setText(cr1.getString(3));
            		//System.out.println("SMM::PRAC::"+cr1.getString(2)+" ADDRESS::"+cr1.getString(3));
            	}
            	cr1.close();
            }
        }
        if(myStatId > 0) {
        	Cursor cr1 = dba.fetchByNetId(dba.STATE, myStatId);  //getMyPractice();
            if(cr1 != null) {
            	cr1.moveToFirst();
            	if(cr1.getCount() > 0) {
            		pracName = pracName+" "+cr1.getString(3);	
            	}
            	cr1.close();
            }
        }
        headline1.setText(userName);
		headline2.setText(pracName);
        /*
        Cursor cr1 = dba.getMyPractice();
        if(cr1 != null && cr1.getCount() > 0) {
        	headline1.setText(cr1.getString(2));
        	headline2.setText(cr1.getString(3));
        }
        */
        
        
        offlineBtn = (Button) findViewById(R.id.filter_offline_button);
        onlineBtn = (Button) findViewById(R.id.filter_online_button);
        docBtn = (Button) findViewById(R.id.filter_doctor_button);
        resBtn = (Button) findViewById(R.id.filter_resource_button);
        resourceFlag = onlineFlag = 0;
        
        offlineBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onlineFlag = 0;
				offlineBtn.setBackgroundResource(R.drawable.segment_button_left_on);
				onlineBtn.setBackgroundResource(R.drawable.segment_button_right);
				offlineBtn.setTextColor(Color.rgb(255,255,255));
				onlineBtn.setTextColor(Color.rgb(128,128,128));
			}
		});
        
        onlineBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onlineFlag = 1;
				offlineBtn.setBackgroundResource(R.drawable.segment_button_left);
				onlineBtn.setBackgroundResource(R.drawable.segment_button_right_on);
				offlineBtn.setTextColor(Color.rgb(128,128,128));
				onlineBtn.setTextColor(Color.rgb(255,255,255));
			}
		});
        
        docBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				resourceFlag = 0;
				docBtn.setBackgroundResource(R.drawable.segment_button_left_on);
				resBtn.setBackgroundResource(R.drawable.segment_button_right);
				resBtn.setTextColor(Color.rgb(128,128,128));
				docBtn.setTextColor(Color.rgb(255,255,255));
			}
		});
        
        resBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				resourceFlag = 1;
				docBtn.setBackgroundResource(R.drawable.segment_button_left);
				resBtn.setBackgroundResource(R.drawable.segment_button_right_on);
				docBtn.setTextColor(Color.rgb(128,128,128));
				resBtn.setTextColor(Color.rgb(255,255,255));
			}
		});
        
        View insuranceBtn = (View) findViewById(R.id.filterInsuranceBtn);
        insuranceBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(FilterPageActivity.this, ItemSelectActivity.class);;
				
				//if( onlineSearch.isChecked() ) {
				if( onlineFlag == 1 ) {
					//System.out.println("online select activity ...");
					intent = new Intent(FilterPageActivity.this, ItemOnlineSelectActivity.class);
				}
				intent.putExtra("opr", DbAdapter.INSURANCE);
				intent.putExtra("nameArr",(String[])insuranceNameArr);				
				intent.putExtra("idArr",insuranceIdArr);				
				intent.putExtra("selectionArr",insuranceSelection);				
				startActivityForResult(intent, 101);
				
//				faisal > commented process changed.				
//				Intent intent = new Intent(FilterPageActivity.this, ItemSelectActivity.class);
//				intent.putExtra("nameArr",(String[])insuranceNameArr);				
//				intent.putExtra("idArr",insuranceIdArr);				
//				intent.putExtra("selectionArr",insuranceSelection);				
//				startActivityForResult(intent, 101);				
				//AlertDialog.Builder builder = getDialogBuilder(DbAdapter.INSURANCE);
				//builder.show();		
			}
		});
        
        View specialityBtn = (View) findViewById(R.id.filterSpecialityBtn);
        specialityBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(FilterPageActivity.this, ItemSelectActivity.class);;
				
				if( onlineFlag == 1 ) {
					System.out.println("online select activity ...");
					intent = new Intent(FilterPageActivity.this, ItemOnlineSelectActivity.class);
				}
				if( resourceFlag == 1 ) {
					intent.putExtra("nameArr",(String[])specialityResNameArr);				
					intent.putExtra("idArr",specialityResIdArr);				
					intent.putExtra("selectionArr",specialityResSelection);				
				} else {
					intent.putExtra("nameArr",(String[])specialityNameArr);				
					intent.putExtra("idArr",specialityIdArr);				
					intent.putExtra("selectionArr",specialitySelection);				
				}
				intent.putExtra("opr", DbAdapter.SPECIALTY);
				startActivityForResult(intent, 102);				
				
//				faisal > commented process changed.				
//				Intent intent = new Intent(FilterPageActivity.this, ItemSelectActivity.class);
//				intent.putExtra("nameArr",(String[])specialityNameArr);				
//				intent.putExtra("idArr",specialityIdArr);				
//				intent.putExtra("selectionArr",specialitySelection);				
//				startActivityForResult(intent, 102);
				
				//AlertDialog.Builder builder = getDialogBuilder(DbAdapter.SPECIALTY);
				//builder.show();		
			}
		});
        
        View hospitalBtn = (View) findViewById(R.id.filterHospitalBtn);
        hospitalBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(FilterPageActivity.this, ItemSelectActivity.class);;
				
				if( onlineFlag == 1 ) {
					System.out.println("online select activity ...");
					intent = new Intent(FilterPageActivity.this, ItemOnlineSelectActivity.class);
				}
				intent.putExtra("opr", DbAdapter.HOSPITAL);
				intent.putExtra("nameArr",(String[])hospitalNameArr);				
				intent.putExtra("idArr",hospitalIdArr);				
				intent.putExtra("selectionArr",hospitalSelection);				
				startActivityForResult(intent, 103);
			}
		});

        View practiceBtn = (View) findViewById(R.id.filterPracticeBtn);
        practiceBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(FilterPageActivity.this, ItemSelectActivity.class);;
				
				if( onlineFlag == 1 ) {
					System.out.println("online select activity ...");
					intent = new Intent(FilterPageActivity.this, ItemOnlineSelectActivity.class);
				}
				loadPractice();
				intent.putExtra("opr", DbAdapter.PRACTICE);
				intent.putExtra("nameArr",(String[])practiceNameArr);				
				intent.putExtra("idArr",practiceIdArr);				
				intent.putExtra("selectionArr",practiceSelection);				
				startActivityForResult(intent, 105);
			}
		});

        
        View countyBtn = (View) findViewById(R.id.filterCountyBtn);
        countyBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if( onlineFlag == 1 ) {
					Intent intent = new Intent(FilterPageActivity.this, ItemSelectActivity.class);;
					
					if( onlineFlag == 1 ) {
						System.out.println("online select activity ...");
						intent = new Intent(FilterPageActivity.this, ItemOnlineSelectActivity.class);
					}
					intent.putExtra("opr", DbAdapter.COUNTY);
					intent.putExtra("nameArr",(String[])hospitalNameArr);				
					intent.putExtra("idArr",hospitalIdArr);				
					intent.putExtra("selectionArr",hospitalSelection);				
					startActivityForResult(intent, 104);					
				}else {
					AlertDialog.Builder builder = getDialogBuilder(DbAdapter.COUNTY);
					builder.show();
				}
			}
		});
        
        languageBtn = (View) findViewById(R.id.filterLanguageBtn);
        languageBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				AlertDialog.Builder builder = getDialogBuilder(DbAdapter.LANGUAGE);
				builder.show();
			}
		});
        
        acoBtn = (View) findViewById(R.id.filterACOBtn);
        acoBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if( onlineFlag == 1 ) {
					Intent intent = new Intent(FilterPageActivity.this, ItemSelectActivity.class);
					
					if( onlineFlag == 1 ) {
						System.out.println("online select activity ...");
						intent = new Intent(FilterPageActivity.this, ItemOnlineSelectActivity.class);
					}
					intent.putExtra("opr", DbAdapter.ACO);
					intent.putExtra("nameArr",(String[])acoArr);				
					intent.putExtra("idArr",acoIdArr);				
					intent.putExtra("selectionArr",acoSelection);				
					startActivityForResult(intent, 106);					
				}else {
					AlertDialog.Builder builder = getDialogBuilder(DbAdapter.ACO);
					builder.show();
				}
			}
		});
        
        officeHourBtn = (View) findViewById(R.id.filterOfficeHourBtn);
        officeHourBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				AlertDialog.Builder builder = getDialogBuilder(DbAdapter.OFFICE_HOUR);
				builder.show();	
			}
		});
        
        showAdvBtn = (Button) findViewById(R.id.filter_avvance_button);
        showAdvBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(officeHourBtn.getVisibility() == View.GONE){
					languageBtn.setVisibility(View.VISIBLE);
					//acoBtn.setVisibility(View.VISIBLE);
					officeHourBtn.setVisibility(View.VISIBLE);
					showAdvBtn.setText("Hide Advance Options");	
				} else {
					languageBtn.setVisibility(View.GONE);
					//acoBtn.setVisibility(View.GONE);
					officeHourBtn.setVisibility(View.GONE);
					showAdvBtn.setText("Show Advance Options");
				}
			}
        });
        
        Button getDocBtn = (Button) findViewById(R.id.getDoctorBtn);
        getDocBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				/*
				if("None".equals(insuranceValue.getText()) || insuranceValue.getText() == null || 
					"".equals(insuranceValue.getText())) {
					Toast.makeText(FilterPageActivity.this, "Please select Insurance.", Toast.LENGTH_SHORT).show();
					return;
				}
				if("None".equals(specialityValue.getText()) || specialityValue.getText() == null ||
					"".equals(specialityValue.getText())) {
					Toast.makeText(FilterPageActivity.this, "Please select Speciality.", Toast.LENGTH_SHORT).show();
					return;
				}	
				*/
				Intent intent = new Intent(FilterPageActivity.this, DoctorListActivity.class);
				//faisal > starts
				if( onlineFlag == 1 ) {
					intent.putExtra("is_online_search", true);
					//intent = new Intent(FilterPageActivity.this, DoctorOnlineListActivity.class);
				}
				//faisal > ends
				intent.putExtra("insu",insuranceValue.getText());
				intent.putExtra("spec",specialityValue.getText());
				intent.putExtra("hosp",hospitalValue.getText());
				intent.putExtra("cnty",countyValue.getText());
				intent.putExtra("docName",docNameValue.getText().toString());
				intent.putExtra("zipCode",zipCodeValue.getText().toString());
				intent.putExtra("languages",languageValue.getText().toString());
				
				intent.putExtra("insuranceName", insuranceName);
            	intent.putExtra("insuranceId", insuranceId);
            	intent.putExtra("specialityName", specialityName);
            	intent.putExtra("specialityId", specialityId);
            	intent.putExtra("hospitalName", hospitalName);
            	intent.putExtra("hospitalId", hospitalId);
            	intent.putExtra("acoName", acoName);
            	intent.putExtra("acoId", acoId);
            	intent.putExtra("countyName", countyName);
            	intent.putExtra("countyId", countyId);
            	intent.putExtra("userId", userId+"");
            	//intent.putExtra("zipCode", zipCodeValue.getText());
            	//intent.putExtra("docName", docNameValue.getText());
            	//dba.close();
	            startActivityForResult(intent, 1100);				
			}
		});
        
        Button aboutBtn = (Button) findViewById(R.id.bottomInfoBtn);
        aboutBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(FilterPageActivity.this, AboutPageActivity.class);
            	startActivityForResult(intent, 1006);				
			}
		});
        /*
        Button setupBtn = (Button) findViewById(R.id.filter_bottom_button1);
        setupBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(fromPageId == Utils.SETUP_PAGE) {
					finish();
				} else {
					Intent intent = new Intent(FilterPageActivity.this, SetupActivity.class);
					intent.putExtra(Utils.pageId, Utils.FILTER_PAGE);
					startActivityForResult(intent, 1100);				
				}
        	}
		});
		*/
        Button advBtn = (Button) findViewById(R.id.filter_bottom_button2);
        advBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(FilterPageActivity.this, DoctorListAdvanceActivity.class);
		//faisal > starts
				if( onlineFlag == 1) {
					//intent = new Intent(FilterPageActivity.this, DoctorOnlineListActivity.class);
				}
				startActivityForResult(intent, 1200);
			}
		});
		/*
        dba = new DbAdapter(this);
        dba.open();
        
        Cursor cr = dba.fetchAll(dba.USERS);
        if(cr != null && cr.getCount() > 0) {
        	cr.moveToFirst();
        	userName  = "User: "+cr.getString(3);
        	userName  = userName + " " +cr.getString(2);
        	userEmail = "Email: " +cr.getString(4);
        	pracId = cr.getInt(6);
        	cr.close();
        	reg = 1;
        	System.out.println("USER_NAME="+userName);
        }
        
        if(pracId > 0) {
        	Cursor cr1 = dba.fetchAll(dba.PRACTICE);
        	if(cr1 != null && cr1.getCount() > 0) {
            	cr1.moveToFirst();
            	pracName     = cr1.getString(2);
            	pracAddress  = cr1.getString(3);
            	cr1.close();
            }
        }
        */
        //insuranceValue= (TextView) findViewById(R.id.headLabel1);   
    }
    
    @Override
    public void onStart(){   
		super.onStart();
    
		loadHospital();
        loadInsurance();
        loadSpeciality();
        loadCounty();
        loadLanguage();
        loadACO();
        loadOfficeHour();
    }
    
    public void scrollUpView(View view){
    	scrollView.fullScroll(ScrollView.FOCUS_DOWN);
    }
    
    public void setupClicked(View view){
    	if(fromPageId == Utils.SETUP_PAGE) {
			finish();
		} else {
			Intent intent = new Intent(FilterPageActivity.this, SetupActivity.class);
			intent.putExtra(Utils.pageId, Utils.FILTER_PAGE);
			startActivityForResult(intent, 1100);
		}
    }
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if(data == null)
    		return;
    	String[] arr = (String[])data.getStringArrayExtra("selectedNameArr");
    	String[] resultIdArr = (String[])data.getStringArrayExtra("selectedIdArr");
    	System.out.println("SMM::SELECTION-ON-RESULT::"+arr);
    	
    	String str1 = "All";
    	String str2 = "0";
    	
    	for(int i=0; i<arr.length; i++) {
    		if(i == 0) { 
    			str1 = arr[i];
        		str2 = resultIdArr[i];
    		} else {
    			str1 = str1+","+arr[i];
        		str2 = str2+","+resultIdArr[i];
    		}
    	}
    	//faisal > modified
    	if(requestCode == 101) {
    		insuranceValue.setText(str1);
    		insuranceName = str1;
    		insuranceId = str2;
    	}else if(requestCode == 102) {
    		specialityValue.setText(str1);
    		specialityName = str1;
    		specialityId = str2;
    	}else if(requestCode == 103) {
    		hospitalValue.setText(str1);
    		hospitalName = str1;
    		hospitalId = str2;
    	}else if(requestCode == 105) {
    		practiceValue.setText(str1);
    		practiceName = str1;
    		practiceId = str2;
    	}else if(requestCode == 104) {
    		countyValue.setText(str1);
    		countyName = str1;
    		countyId = str2;
    	}else if(requestCode == 106) {
    		acoValue.setText(str1);
    		acoName = str1;
    		acoId = str2;
    	}
    	
    	//TODO handle here. 
    }

    private void loadHospital() {
    	Cursor cr = dba.fetchAll(DbAdapter.HOSPITAL);
        if(cr != null && cr.getCount() > 0) {
        	cr.moveToFirst();
        	hospitalNameArr = new String[cr.getCount()];
			hospitalSelection = new boolean[cr.getCount()];
			hospitalIdArr = new int[cr.getCount()];
			for(int i=0; i<cr.getCount(); i++) {
        		hospitalNameArr[i] = cr.getString(2);
        		hospitalSelection[i] = false;
        		hospitalIdArr[i] = cr.getInt(1);
        		cr.moveToNext();
        	}
			cr.close();
        }
    }
    
    private void loadPractice() {
    	Cursor cr = dba.fetchAll(DbAdapter.PRACTICE);
        if(cr != null && cr.getCount() > 0) {
        	cr.moveToFirst();
        	practiceNameArr = new String[cr.getCount()];
        	practiceSelection = new boolean[cr.getCount()];
        	practiceIdArr = new int[cr.getCount()];
			for(int i=0; i<cr.getCount(); i++) {
				practiceNameArr[i] = cr.getString(2);
				practiceSelection[i] = false;
				practiceIdArr[i] = cr.getInt(1);
        		cr.moveToNext();
        	}
			cr.close();
        }
    }
    
    private void loadInsurance() {
    	Cursor cr = dba.fetchAll(DbAdapter.INSURANCE);
        if(cr != null && cr.getCount() > 0) {
        	cr.moveToFirst();
        	insuranceNameArr = new String[cr.getCount()];
			insuranceSelection = new boolean[cr.getCount()];
			insuranceIdArr = new int[cr.getCount()];
			for(int i=0; i<cr.getCount(); i++) {
				insuranceNameArr[i] = cr.getString(2);
				insuranceSelection[i] = false;
				insuranceIdArr[i] = cr.getInt(1);
        		cr.moveToNext();
        	}
			cr.close();
        }
    }
    
    private void loadSpeciality() {
    	Cursor cr = dba.fetchAll(DbAdapter.SPECIALTY);
        if(cr != null && cr.getCount() > 0) {
        	cr.moveToFirst();
        	int docTypeCount = 0;
        	int resTypeCount = 0;
        	for(int i=0; i<cr.getCount(); i++) {
				if(cr.getInt(4) < 3)
					docTypeCount++;
				else
					resTypeCount++;
				cr.moveToNext();
        	}
        	specialityNameArr   = new String[docTypeCount];
			specialitySelection = new boolean[docTypeCount];
			specialityIdArr     = new int[docTypeCount];
			
			specialityResNameArr   = new String[resTypeCount];
			specialityResSelection = new boolean[resTypeCount];
			specialityResIdArr     = new int[resTypeCount];
			
			cr.moveToFirst();
			for(int i=0, j=0; i+j<cr.getCount();) {
				if(cr.getInt(4) < 3) {
					specialityNameArr[i]   = cr.getString(2);
					specialitySelection[i] = false;
					specialityIdArr[i]     = cr.getInt(1);
					i++;
				} else {
					specialityResNameArr[j]   = cr.getString(2);
					specialityResSelection[j] = false;
					specialityResIdArr[j]     = cr.getInt(1);
					j++;
				}
				cr.moveToNext();
        	}
			cr.close();
        }
    }

    private void loadCounty() {
    	Cursor cr = dba.fetchAll(DbAdapter.COUNTY);
        if(cr != null && cr.getCount() > 0) {
        	cr.moveToFirst();
        	countyNameArr = new String[cr.getCount()];
			countySelection = new boolean[cr.getCount()];
			countyIdArr = new int[cr.getCount()];
			for(int i=0; i<cr.getCount(); i++) {
//				if(i == 0) {
//					countyName = cr.getString(2);
//					countyId = cr.getString(1);
//				} else {
//					countyName = countyName+","+cr.getString(2);
//					countyId = countyId+","+cr.getString(1);
//				}
				countyNameArr[i] = cr.getString(2);
				//countySelection[i] = true;
				countySelection[i] = false;
				countyIdArr[i] = cr.getInt(1);
        		cr.moveToNext();
        	}
			cr.close();
			countyValue.setText(countyName);
        }
    }
    
    private void loadACO() {
    	
        Cursor cr = dba.fetchAll(DbAdapter.ACO);
        if(cr != null && cr.getCount() > 0) {
        	cr.moveToFirst();
        	acoArr = new String[cr.getCount()];
			acoSelection = new boolean[cr.getCount()];
			acoIdArr = new int[cr.getCount()];
			for(int i=0; i<cr.getCount(); i++) {
				acoArr[i] = cr.getString(2);
				acoSelection[i] = false;
				acoIdArr[i] = cr.getInt(1);
        		cr.moveToNext();
        	}
			
			
			cr.close();
        }
    }

    private void loadLanguage() {
    	ABC.Language arr[] = ABC.Language.values();
    	int count = arr.length;
    	    	
    	languageArr = new String[count];
    	languageSelection = new boolean[count];
    	languageIdArr = new int[count];
		for(int i=0; i<count; i++) {
			languageArr[i] = arr[i].name;
			if(languageValue.getText().toString().equals(arr[i].name))
				languageSelection[i] = false;
			else	
				languageSelection[i] = false;
			languageIdArr[i] = arr[i].id;
        }
    }
    
    private void loadOfficeHour() {
    	ABC.OfficeHour arr[] = ABC.OfficeHour.values();
    	int count = arr.length;
    	    	
    	officeHourArr = new String[count];
    	officeHourSelection = new boolean[count];
    	officeHourIdArr = new int[count];
		for(int i=0; i<count; i++) {
			officeHourArr[i] = arr[i].name;
			if(officeHourValue.getText().toString().equals(arr[i].name))
				officeHourSelection[i] = true;
			else	
				officeHourSelection[i] = false;
			officeHourIdArr[i] = arr[i].id;
        }
    }
    
    private AlertDialog.Builder getDialogBuilder(final int opt) {
    	
    	builder = new AlertDialog.Builder(FilterPageActivity.this);
    	switch(opt){
    		case DbAdapter.COUNTY:
    			builder.setTitle("Choose County");
    			break;
    		case DbAdapter.LANGUAGE:
    			builder.setTitle("Choose Language");
    			break;
    		case DbAdapter.OFFICE_HOUR:
    			builder.setTitle("Choose Office Hour");
    			break;
    		case DbAdapter.ACO:
    			builder.setTitle("Choose ACO");
    			break;
    		default:
    			break;
    	}
    	dialogClickListener = new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int which) {
    			switch (which){
    			case DialogInterface.BUTTON_POSITIVE:
    				//Yes button clicked
    				if(opt == DbAdapter.LANGUAGE){
    					
    				}
    				zipCodeValue.setVisibility(View.GONE);
    				break;
    			case DialogInterface.BUTTON_NEGATIVE:
    				//No button clicked
    				zipCodeValue.setVisibility(View.VISIBLE);
    				break;
    			}
    		}
    	};
    	
    	if(opt == DbAdapter.COUNTY)
    		builder.setNegativeButton("ZIP Code", dialogClickListener);
    		
    	builder.setPositiveButton("Done", dialogClickListener);
    	
    	switch(opt){
			case DbAdapter.HOSPITAL:
				builder.setMultiChoiceItems(hospitalNameArr, hospitalSelection, new DialogInterface.OnMultiChoiceClickListener() {
					public void onClick(DialogInterface dialog, int which,
							boolean isChecked) {
						// TODO Auto-generated method stub
						//String str = "";
						hospitalName = "";
						hospitalId = "";
						for(int i=0; i<hospitalSelection.length; i++) {
							if(hospitalSelection[i]) {
								if(!hospitalName.equals("")){  
									hospitalName = hospitalName +",";
									hospitalId   = hospitalId +",";
								}
								hospitalName = hospitalName +hospitalNameArr[i].toString();
								hospitalId   = hospitalId + new Integer(hospitalIdArr[i]).toString();
							}
						}
						hospitalValue.setText(hospitalName);
					}
				});
				
				break;
			case DbAdapter.INSURANCE:
				builder.setMultiChoiceItems(insuranceNameArr, insuranceSelection, new DialogInterface.OnMultiChoiceClickListener() {
					public void onClick(DialogInterface dialog, int which,
							boolean isChecked) {
						// TODO Auto-generated method stub
						insuranceName = "";
						insuranceId = "";
						for(int i=0; i<insuranceSelection.length; i++) {
							if(insuranceSelection[i]) {
								if(!insuranceName.equals("")){  
									insuranceName = insuranceName +",";
									insuranceId   = insuranceId +",";
								}
								insuranceName = insuranceName +insuranceNameArr[i].toString();
								insuranceId   = insuranceId + new Integer(insuranceIdArr[i]).toString();
							}
						}
						insuranceValue.setText(insuranceName);
					}
				});
				break;
			case DbAdapter.SPECIALTY:
				builder.setMultiChoiceItems(specialityNameArr, specialitySelection, new DialogInterface.OnMultiChoiceClickListener() {
					public void onClick(DialogInterface dialog, int which,
							boolean isChecked) {
						// TODO Auto-generated method stub
						specialityName = "";
						specialityId   = "";
						for(int i=0; i<specialitySelection.length; i++) {
							if(specialitySelection[i]) {
								if(!specialityName.equals("")){ 
									specialityName = specialityName +",";
									specialityId   = specialityId +",";
								}
								specialityName = specialityName +specialityNameArr[i].toString();
								specialityId   = specialityId + new Integer(specialityIdArr[i]).toString();
							}
						}
						specialityValue.setText(specialityName);
					}
				});
				break;
			case DbAdapter.COUNTY:
				builder.setMultiChoiceItems(countyNameArr, countySelection, new DialogInterface.OnMultiChoiceClickListener() {
					public void onClick(DialogInterface dialog, int which,
							boolean isChecked) {
						// TODO Auto-generated method stub
						Log.d("NI::","Selected Item No. "+which);
						Log.d("NI::","Is checked : "+isChecked);
						if(isChecked){
							String totalCounty[] = countyId.split(","); 
							if(totalCounty.length<Utils.COUNTY_LIMIT){
								countyName = "All";
								countyId   = "";
								for(int i=0; i<countySelection.length; i++) {
									if(countySelection[i]) {
										if(!countyName.equals("All")){  
											countyName = countyName +","+countyNameArr[i].toString();
											countyId   = countyId +","+ new Integer(countyIdArr[i]).toString();
										} else {
											countyName = countyNameArr[i].toString();
											countyId   = new Integer(countyIdArr[i]).toString();
										}
									}
								}
								countyValue.setText(countyName);
							}
							else{
								countySelection[which]=false;
								((AlertDialog) dialog).getListView().setItemChecked(which, false); 
								Toast.makeText(FilterPageActivity.this, "Maximum "+Utils.COUNTY_LIMIT+" county can be seleted", Toast.LENGTH_LONG).show();
							}
						}
						else{
							countyName = "All";
							countyId   = "";
							for(int i=0; i<countySelection.length; i++) {
								if(countySelection[i]) {
									if(!countyName.equals("All")){  
										countyName = countyName +","+countyNameArr[i].toString();
										countyId   = countyId +","+ new Integer(countyIdArr[i]).toString();
									} else {
										countyName = countyNameArr[i].toString();
										countyId   = new Integer(countyIdArr[i]).toString();
									}
								}
							}
							countyValue.setText(countyName);
						}
					}
				});
				break;
			case DbAdapter.LANGUAGE:
				builder.setMultiChoiceItems(languageArr, languageSelection, new DialogInterface.OnMultiChoiceClickListener() {
					public void onClick(DialogInterface dialog, int which,
							boolean isChecked) {
						// TODO Auto-generated method stub
						languageName = "All";
						languageId   = "";			
						for(int i=0; i<languageSelection.length; i++) {
							if(languageSelection[i]) {
								if(!languageName.equals("All")){  
									languageName = languageName +","+languageArr[i].toString();
									languageId   = languageId +","+ new Integer(languageIdArr[i]).toString();
								} else {
									languageName = languageArr[i].toString();
									languageId   = new Integer(languageIdArr[i]).toString();
								}
							}
						}
						languageValue.setText(languageName);
					}
				});
				break;
			case DbAdapter.ACO:
				builder.setMultiChoiceItems(acoArr, acoSelection, new DialogInterface.OnMultiChoiceClickListener() {
					public void onClick(DialogInterface dialog, int which,
							boolean isChecked) {
						// TODO Auto-generated method stub
						acoName = "All";
						acoId   = "";			
						for(int i=0; i<acoSelection.length; i++) {
							if(acoSelection[i]) {
								if(!acoName.equals("All")){  
									acoName = acoName +","+acoArr[i].toString();
									acoId   = acoId +","+ new Integer(acoIdArr[i]).toString();
								} else {
									acoName = acoArr[i].toString();
									acoId   = new Integer(acoIdArr[i]).toString();
								}
							}
						}
						acoValue.setText(acoName);
					}
				});
				break;
			case DbAdapter.OFFICE_HOUR:
				builder.setMultiChoiceItems(officeHourArr, officeHourSelection, new DialogInterface.OnMultiChoiceClickListener() {
					public void onClick(DialogInterface dialog, int which,
							boolean isChecked) {
						// TODO Auto-generated method stub
						officeHourName = "All";
						officeHourId   = "";			
						for(int i=0; i<officeHourSelection.length; i++) {
							if(officeHourSelection[i]) {
								if(!officeHourName.equals("All")){  
									officeHourName = officeHourName +","+officeHourArr[i].toString();
									officeHourId   = officeHourId +","+ new Integer(officeHourIdArr[i]).toString();
								} else {
									officeHourName = officeHourArr[i].toString();
									officeHourId   = new Integer(officeHourIdArr[i]).toString();
								}
							}
						}
						officeHourValue.setText(officeHourName);
					}
				});
				break;
    	}
    	
    	return builder;
    }
}