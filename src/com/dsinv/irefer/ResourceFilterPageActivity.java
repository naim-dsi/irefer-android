package com.dsinv.irefer;

import com.dsinv.irefer.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

public class ResourceFilterPageActivity extends Activity {
	
	private DbAdapter	dba;
	
	private TextView headline1;
	private TextView headline2;
	private TextView insuranceValue;
	private TextView specialityValue;
	private TextView hospitalValue;
	private TextView countyValue;
	private TextView zipCodeValue;
	private TextView docNameValue;
    //faisal > added
	//private CheckBox onlineSearch;
	private ToggleButton goOnline;
	
	private String[] insuranceNameArr = {"NA"};
	private String[] specialityNameArr = {"NA"};
	private String[] hospitalNameArr = {"NA"};
	private String[] countyNameArr = {"NA"};
	
	private boolean[] insuranceSelection = {false};
	private boolean[] specialitySelection = {false};
	private boolean[] hospitalSelection = {false};
	private boolean[] countySelection = {false};
	
	private int[] insuranceIdArr = {0};
	private int[] specialityIdArr = {0};
	private int[] hospitalIdArr = {0};
	private int[] countyIdArr = {0};
	
	private String insuranceName = "";
	private String insuranceId = "";
	private String specialityName = "";
	private String specialityId = "";
	private String hospitalName = "";
	private String hospitalId = "";
	private String countyName = "";
	private String countyId = "";
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_page);
        setTitle( getString( R.string.app_name ) + " - Search");
        
        headline1= (TextView) findViewById(R.id.filterHeadline1);
        headline2= (TextView) findViewById(R.id.filterHeadline2);
        insuranceValue= (TextView) findViewById(R.id.filterInsuranceValue);
        specialityValue= (TextView) findViewById(R.id.filterSpecialityValue);
        hospitalValue= (TextView) findViewById(R.id.filterHospitalValue);
        countyValue= (TextView) findViewById(R.id.filterCountyValue);
        zipCodeValue= (TextView) findViewById(R.id.zip_code_text_edit);
        docNameValue= (TextView) findViewById(R.id.doc_name_text_edit);
        //faisal > added
        //onlineSearch = (CheckBox) findViewById(R.id.online_search_checkbox);
        goOnline = (ToggleButton) findViewById(R.id.online_search_toggle);
        //System.out.println("SMM::DOC-NAME---------------------------------------="+docNameValue.getText());
        dba = new DbAdapter(this);
        dba.open();
        
        Cursor cr1 = dba.getMyPractice();
        if(cr1 != null && cr1.getCount() > 0) {
        	headline1.setText(cr1.getString(2));
        	headline2.setText(cr1.getString(3));
        }
        
        loadHospital();
        loadInsurance();
        loadSpeciality();
        loadCounty();
        
        View insuranceBtn = (View) findViewById(R.id.filterInsuranceBtn);
        insuranceBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(ResourceFilterPageActivity.this, ResourceItemSelectActivity.class);;
				
				//if( onlineSearch.isChecked() ) {
				if( goOnline.isChecked()) {
					System.out.println("online select activity ...");
					intent = new Intent(ResourceFilterPageActivity.this, ResourceItemOnlineSelectActivity.class);
					intent.putExtra("opr", DbAdapter.INSURANCE);
				}
				
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
				Intent intent = new Intent(ResourceFilterPageActivity.this, ResourceItemSelectActivity.class);;
				
				if( goOnline.isChecked() ) {
					System.out.println("online select activity ...");
					intent = new Intent(ResourceFilterPageActivity.this, ResourceItemOnlineSelectActivity.class);
					intent.putExtra("opr", DbAdapter.SPECIALTY);
				}
				intent.putExtra("nameArr",(String[])specialityNameArr);				
				intent.putExtra("idArr",specialityIdArr);				
				intent.putExtra("selectionArr",specialitySelection);				
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
				Intent intent = new Intent(ResourceFilterPageActivity.this, ResourceItemSelectActivity.class);;
				
				if( goOnline.isChecked() ) {
					System.out.println("online select activity ...");
					intent = new Intent(ResourceFilterPageActivity.this, ResourceItemOnlineSelectActivity.class);
					intent.putExtra("opr", DbAdapter.HOSPITAL);
				}
				intent.putExtra("nameArr",(String[])hospitalNameArr);				
				intent.putExtra("idArr",hospitalIdArr);				
				intent.putExtra("selectionArr",hospitalSelection);				
				startActivityForResult(intent, 103);

//				faisal > commented process changed.						
//				Intent intent = new Intent(FilterPageActivity.this, ItemSelectActivity.class);
//				intent.putExtra("nameArr",(String[])hospitalNameArr);				
//				intent.putExtra("idArr",hospitalIdArr);				
//				intent.putExtra("selectionArr",hospitalSelection);				
//				startActivityForResult(intent, 103);
				//AlertDialog.Builder builder = getDialogBuilder(DbAdapter.HOSPITAL);
				//builder.show();		
			}
		});
        
        View countyBtn = (View) findViewById(R.id.filterCountyBtn);
        countyBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if( goOnline.isChecked() ) {
					Intent intent = new Intent(ResourceFilterPageActivity.this, ResourceItemSelectActivity.class);;
					
					if( goOnline.isChecked() ) {
						System.out.println("online select activity ...");
						intent = new Intent(ResourceFilterPageActivity.this, ResourceItemOnlineSelectActivity.class);
						intent.putExtra("opr", DbAdapter.COUNTY);
					}
					intent.putExtra("nameArr",(String[])hospitalNameArr);				
					intent.putExtra("idArr",hospitalIdArr);				
					intent.putExtra("selectionArr",hospitalSelection);				
					startActivityForResult(intent, 104);					
				}else {
					AlertDialog.Builder builder = getDialogBuilder(DbAdapter.COUNTY);
					builder.show();
				}
//				faisal > commented process changed.				
//				AlertDialog.Builder builder = getDialogBuilder(DbAdapter.COUNTY);
//				builder.show();
			}
		});
        ( (TextView) findViewById(R.id.doc_name_text_edit) ).setHint("Resource Name");
        ( (TextView) findViewById(R.id.sees_in_petient) ).setVisibility(View.GONE);
        Button getDocBtn = (Button) findViewById(R.id.getDoctorBtn);
        getDocBtn.setText("Get Resource");
        getDocBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(ResourceFilterPageActivity.this, ResourceListActivity.class);
				//faisal > starts
				if( goOnline.isChecked() ) {
					intent = new Intent(ResourceFilterPageActivity.this, ResourceOnlineListActivity.class);
				}
				//faisal > ends
				intent.putExtra("insu",insuranceValue.getText());
				intent.putExtra("spec",specialityValue.getText());
				intent.putExtra("hosp",hospitalValue.getText());
				intent.putExtra("cnty",countyValue.getText());
				intent.putExtra("docName",docNameValue.getText().toString());
				intent.putExtra("zipCode",zipCodeValue.getText().toString());
				
				intent.putExtra("insuranceName", insuranceName);
            	intent.putExtra("insuranceId", insuranceId);
            	intent.putExtra("specialityName", specialityName);
            	intent.putExtra("specialityId", specialityId);
            	intent.putExtra("hospitalName", hospitalName);
            	intent.putExtra("hospitalId", hospitalId);
            	intent.putExtra("countyName", countyName);
            	intent.putExtra("countyId", countyId);
            	//intent.putExtra("zipCode", zipCodeValue.getText());
            	//intent.putExtra("docName", docNameValue.getText());
	            startActivityForResult(intent, 1100);				
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if(data == null)
    		return;
    	String[] arr = (String[])data.getStringArrayExtra("selectedNameArr");
    	String[] resultIdArr = (String[])data.getStringArrayExtra("selectedIdArr");
    	System.out.println("SMM::SELECTION-ON-RESULT::"+arr);
    	
    	String str1 = "";
    	String str2 = "";
    	
    	for(int i=0; i<arr.length; i++) {
    		if(i > 0) { 
    			str1 = str1+",";
    			str2 = str2+",";
    		}
    		str1 = str1+arr[i];
    		str2 = str2+resultIdArr[i];
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
    	}else if(requestCode == 104) {
    		countyValue.setText(str1);
    		countyName = str1;
    		countyId = str2;
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
        }
    }
    
    private void loadSpeciality() {
    	Cursor cr = dba.fetchAll(DbAdapter.SPECIALTY);
        if(cr != null && cr.getCount() > 0) {
        	cr.moveToFirst();
        	specialityNameArr = new String[cr.getCount()];
			specialitySelection = new boolean[cr.getCount()];
			specialityIdArr = new int[cr.getCount()];
			for(int i=0; i<cr.getCount(); i++) {
				specialityNameArr[i] = cr.getString(2);
				specialitySelection[i] = false;
				specialityIdArr[i] = cr.getInt(1);
        		cr.moveToNext();
        	}
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
				countyNameArr[i] = cr.getString(2);
				countySelection[i] = false;
				countyIdArr[i] = cr.getInt(1);
        		cr.moveToNext();
        	}
        }
    }

    private AlertDialog.Builder getDialogBuilder(final int opt) {
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(ResourceFilterPageActivity.this);
    	builder.setTitle("Choose Hospital");
    	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int which) {
    			switch (which){
    			case DialogInterface.BUTTON_POSITIVE:
    				//Yes button clicked
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
						countyName = "";
						countyId   = "";
						for(int i=0; i<countySelection.length; i++) {
							if(countySelection[i]) {
								if(!countyName.equals("")){  
									countyName = countyName +",";
									countyId   = countyId +",";
								}
								countyName = countyName +countyNameArr[i].toString();
								countyId   = countyId + new Integer(countyIdArr[i]).toString();
							}
						}
						countyValue.setText(countyName);
					}
				});
				break;
    	}
    	
    	return builder;
    }
}