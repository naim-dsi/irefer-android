package com.dsinv.irefer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.dsinv.irefer.R;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class DoctorDetailActivity extends Activity {
    
	private DbAdapter dba;	
	private Intent intent;
	private ProgressDialog dialog;
	private Dialog rankDialog;
	private Dialog reportDialog;
	private RatingBar ratingBar;
	private EditText reportTextEdit;
	public TextView rankTxt;
	private LinearLayout reportLayout;
	private Button reportBtn;
	private Button reportDialogBtn;
	TextView reportTxt;
	
	Context ctx;
	ImageView docImage;
	ProgressBar pBar;
	RatingBar userRank;
	RatingBar pracRank;
	String userId = "";
	String docName = "";
	int doctorId = -1;
	int userRankValue = 0;
	int userPaRankValue = 0;
	int userGradeValue = 0;
	int userQualityValue = 0;
	int userCostValue = 0;
	int rankUserNumber = 0;
	double avgRank = 0;
	
	int reportFlag = 0;
	boolean isOnlineSearch = false;
	
	int gradeIdArr[] = {R.id.grade_icon_1, R.id.grade_icon_2, R.id.grade_icon_3, R.id.grade_icon_4, R.id.grade_icon_5};
	int qualityIdArr[] = {R.id.quality_icon_1, R.id.quality_icon_2, R.id.quality_icon_3, R.id.quality_icon_4, R.id.quality_icon_5};
	int pexpIdArr[] = {R.id.pexp_icon_1, R.id.pexp_icon_2, R.id.pexp_icon_3, R.id.pexp_icon_4, R.id.pexp_icon_5};
	int costIdArr[] = {R.id.cost_icon_1, R.id.cost_icon_2, R.id.cost_icon_3, R.id.cost_icon_4, R.id.cost_icon_5};

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_detail_temp);
        setTitle( getString( R.string.app_name ) + " - Specialist Details");
        dba = new DbAdapter(this);
        dba.open();
        intent = getIntent();
        
        docImage = (ImageView)findViewById(R.id.doc_detail_pic);
        pBar = (ProgressBar)findViewById(R.id.imageProgressBar);
        userRank = (RatingBar)findViewById(R.id.detail_user_rank);
        //pracRank = (RatingBar)findViewById(R.id.detail_prac_rank);
        
        userId = getIntent().getStringExtra("user_id");
    	
        doctorId = intent.getIntExtra("doctor_id", 0);        
        reportFlag = intent.getIntExtra("report", 0);
        reportLayout = (LinearLayout)findViewById(R.id.doc_detail_report_change);
        reportTextEdit = (EditText)findViewById(R.id.report_change_text_edit);
        reportBtn = (Button) findViewById(R.id.doc_detail_submit_report);
        reportTxt = (TextView) findViewById(R.id.doctor_detail_report_label);
        
        if(reportFlag == 1){
        	reportLayout.setVisibility(View.VISIBLE);
        	reportBtn.setVisibility(View.VISIBLE);
        	reportTextEdit.setVisibility(View.VISIBLE);
        	reportTextEdit.requestFocus();
        }
        reportBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String str = reportTextEdit.getText().toString();
				String str2 = Utils.getCurrentTime();
				long reportId = dba.insert(DbAdapter.DOC_REPORT, new String[]{doctorId+"", userId, str, str2,""});
				TextView reportTxt = (TextView) findViewById(R.id.doctor_detail_report_label);
				reportTxt.setText(str2+"\n"+str);
				reportTxt.setVisibility(View.VISIBLE);
				reportTextEdit.setVisibility(View.GONE);
				reportBtn.setVisibility(View.GONE);
				Toast.makeText(ctx, "Saved", Toast.LENGTH_SHORT).show();
				System.out.println("SMM::reportID="+reportId);
			}
        });
        
        isOnlineSearch = intent.getBooleanExtra("is_online_search", false);
        System.out.println("isOnlineSearch="+isOnlineSearch);
        if(isOnlineSearch) {        	
        	new DownloadDoctorDetailsTask().execute(doctorId);
        	//populateData( loadDoctorDetailsFromWeb((long)doctorId) );
        }else {
        	populateData( loadDoctorDetailsFromDatabase((long)doctorId) );	
        }
        
        ctx = this.getApplicationContext();
        
        Button rankBtn = (Button) findViewById(R.id.doc_detail_rank_button);
        Button referButton = (Button) findViewById(R.id.doc_refer);
        referButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//rankDialog = new Dialog(DoctorDetailActivity.this, R.style.FullHeightDialog);
				rankDialog = new ReferDialog(DoctorDetailActivity.this, R.style.FullHeightDialog,
						dba, doctorId, userId, docName);
				
                //now that the dialog is set up, it's time to show it    
                rankDialog.show();				
			}
		});
        rankBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//rankDialog = new Dialog(DoctorDetailActivity.this, R.style.FullHeightDialog);
				rankDialog = new DoctorRankDialog(DoctorDetailActivity.this, R.style.FullHeightDialog,
						dba, doctorId, docName, userId, userRankValue, isOnlineSearch);
				
                //now that the dialog is set up, it's time to show it    
                rankDialog.show();
			}
		});
        

        Button reportDialogBtn = (Button) findViewById(R.id.doc_detail_report_button);
        reportDialogBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//reportDialog = new Dialog(DoctorDetailActivity.this, R.style.FullHeightDialog);
				reportDialog = new DoctorReportDialog(DoctorDetailActivity.this, R.style.FullHeightDialog,
						dba, doctorId, userId);
			    //now that the dialog is set up, it's time to show it    
                reportDialog.show();				
			}
		});
    }
	
	private Map<String, String> loadDoctorDetailsFromDatabase(Long doctorId) {
        Cursor cr = dba.fetchByNetId(DbAdapter.DOCTOR, doctorId);
        cr.moveToFirst();
        //Insurance///////////////////////////////////////////////////////////////////
//        Cursor cr3 = dba.fetchdocInsurance(DbAdapter.DOC_INSURANCE, doctorId);
        String insurances = "";
        String docins = "";
        if(cr.getString(31).equals(",,")){
        	docins = "0";
        }
        else{
        	
        	docins = "0"+cr.getString(31)+"0";
        }
        
//        cr3.moveToFirst();
//        for(int i=0; i<cr3.getCount(); i++) {
//        	if(i == 0)
//        	{
//        		docins=cr3.getString(1);
//        		cr3.moveToNext();
//        	}
//        	else
//        	{
//	        	docins = docins + " , " +cr3.getString(1);
//	    		cr3.moveToNext();
//        	}
//    	}
        if (docins.length() > 0 && docins.charAt(docins.length()-1)==',') {
        	docins = docins.substring(0, docins.length()-1);
        }
        Log.d("NR::", docins);
        Cursor cr2 = dba.fetchInsurance(DbAdapter.INSURANCE, docins);
        cr2.moveToFirst();        
        for(int i=0; i<cr2.getCount(); i++) {
        	if(i == 0)
        	{
        		insurances = cr2.getString(2);
        		Log.d("NR::", cr2.getString(2));
        		cr2.moveToNext();
        	}
        	else
        	{
	        	insurances = insurances + " , " +cr2.getString(2);	
	        	Log.d("NR::", cr2.getString(2));
	    		cr2.moveToNext();
        	}
    	}
        //Speciality///////////////////////////////////////////////////////////////
//        cr3 = dba.fetchdocSpeciality(DbAdapter.DOC_SPECIALTY, doctorId);
        String specialities = "";
        if(cr.getString(30).equals(",,")){
        	docins = "0";
        }
        else{
        	
        	docins = "0"+cr.getString(30)+"0";
        }
//        cr3.moveToFirst();
//        for(int i=0; i<cr3.getCount(); i++) {
//        	if(i == 0)
//        	{
//        		docins=cr3.getString(1);
//        		cr3.moveToNext();
//        	}
//        	else
//        	{
//	        	docins = docins + " , " +cr3.getString(1);
//	    		cr3.moveToNext();
//        	}
//    	}
        if (docins.length() > 0 && docins.charAt(docins.length()-1)==',') {
        	docins = docins.substring(0, docins.length()-1);
        }
        Log.d("NR::", docins);
        cr2 = dba.fetchSpeciality(DbAdapter.SPECIALTY, docins);
        cr2.moveToFirst();        
        for(int i=0; i<cr2.getCount(); i++) {
        	if(i == 0)
        	{
        		specialities = cr2.getString(2);
        		Log.d("NR::", cr2.getString(2));
        		cr2.moveToNext();
        	}
        	else
        	{
        		specialities = specialities + " , " +cr2.getString(2);	
	        	Log.d("NR::", cr2.getString(2));
	    		cr2.moveToNext();
        	}
    	}
      //Hospitals///////////////////////////////////////////////////////////////
//        cr3 = dba.fetchdocHospital(DbAdapter.DOC_HOSPITAL, doctorId);
        String hospitals = "";
        if(cr.getString(29).equals(",,")){
        	docins = "0";
        }
        else{
        	
        	docins = "0"+cr.getString(29)+"0";
        }
       
//        cr3.moveToFirst();
//        for(int i=0; i<cr3.getCount(); i++) {
//        	if(i == 0)
//        	{
//        		docins=cr3.getString(1);
//        		cr3.moveToNext();
//        	}
//        	else
//        	{
//	        	docins = docins + " , " +cr3.getString(1);
//	    		cr3.moveToNext();
//        	}
//    	}
        if (docins.length() > 0 && docins.charAt(docins.length()-1)==',') {
        	docins = docins.substring(0, docins.length()-1);
        }
        
        String hosp_name = ((Utils.isEmpty(cr.getString(35))||cr.getString(35).equals("null")) ? "" : cr.getString(35));
        
        String see_patient = ((Utils.isEmpty(cr.getString(18))||cr.getString(18).equals("null")||cr.getString(18).equals("0")) ? "" : cr.getString(18));
        
        if(!Utils.isEmpty(hosp_name)){
        	String seePArr[]  = (see_patient).split(",");
        	String hNameArr[] = (hosp_name).split(",");
	        hospitals = "";
	    	for(int i=0; i<hNameArr.length && i<seePArr.length; i++) {
	    		hospitals = Utils.isEmpty(hospitals) ? hNameArr[i] : hospitals+"\n"+hNameArr[i];
	    		if(seePArr[i].equals("0")){
	    			if(!hospitals.equals(""))
	    				hospitals = hospitals+" (N)";
	    		}
	    		else{
	    			if(!hospitals.equals(""))
	    				hospitals = hospitals+" (Y)";
	    		}
	    	}
	    }
        /*
        Log.d("NR::", docins);
        
        cr2 = dba.fetchHospital(DbAdapter.HOSPITAL, docins);
        cr2.moveToFirst();        
        for(int i=0; i<cr2.getCount(); i++) {
        	if(i == 0)
        	{
        		hospitals = "- "+cr2.getString(2)+"\n";
        		Log.d("NR::", cr2.getString(2));
        		cr2.moveToNext();
        	}
        	else if(cr2.getCount() == i+1)
        	{
        		hospitals = hospitals + "- " +cr2.getString(2);	
	        	Log.d("NR::", cr2.getString(2));
	    		cr2.moveToNext();
        	}
        	else
        	{
        		hospitals = hospitals + "- " +cr2.getString(2)+"\n";	
	        	Log.d("NR::", cr2.getString(2));
	    		cr2.moveToNext();
        	}
    	}
    	*/
////////////////////ACO////////////////////////////////////////////        
//        cr3 = dba.fetchdocAco(DbAdapter.DOC_ACO, doctorId);
        String acos = "";
        //docins = "0"+cr.getString(33)+"0";
        if(cr.getString(33).equals(",,")){
        	docins = "0";
        }
        else{
        	
        	docins = "0"+cr.getString(33)+"0";
        }
//        cr3.moveToFirst();
//        for(int i=0; i<cr3.getCount(); i++) {
//        	if(i == 0)
//        	{
//        		docins=cr3.getString(1);
//        		cr3.moveToNext();
//        	}
//        	else
//        	{
//	        	docins = docins + " , " +cr3.getString(1);
//	    		cr3.moveToNext();
//        	}
//    	}
        if (docins.length() > 0 && docins.charAt(docins.length()-1)==',') {
        	docins = docins.substring(0, docins.length()-1);
        }
        Log.d("NR::", docins);
        cr2 = dba.fetchAco(DbAdapter.ACO, docins);
        cr2.moveToFirst();        
        for(int i=0; i<cr2.getCount(); i++) {
        	if(i == 0)
        	{
        		acos = cr2.getString(2);
        		Log.d("NR::", cr2.getString(2));
        		cr2.moveToNext();
        	}
        	else
        	{
        		acos = acos + " , " +cr2.getString(2);	
	        	Log.d("NR::", cr2.getString(2));
	    		cr2.moveToNext();
        	}
    	}        
        
      //Practice///////////////////////////////////////////////////////////////
//        cr3 = dba.fetchdocPractice(DbAdapter.DOC_PRACTICE, doctorId);
        String practices = "";
        //docins = "0"+cr.getString(28)+"0";
        if(cr.getString(34).equals("")){
        	docins = "";
        }
        else{
        	
        	docins = cr.getString(34);
        	//docins = docins.substring(0,docins.length()-1);
        	//docins = docins.substring(1);
        }
        String prac_name = docins;
        
        if(cr.getString(28).equals("")){
        	docins = "";
        }
        else{
        	
        	docins = cr.getString(28);
        	docins = docins.substring(0,docins.length()-1);
        	docins = docins.substring(1);
        }
        String prac_ids = docins;
        
        String up_rank = "";
        if(cr.getString(23).equals("")){
        	up_rank = "";
        }
        else{
        	up_rank = cr.getString(23);
        }
        Log.d("NR::", docins);
        String[] arr = new String[10];
        Arrays.fill(arr, "");
        if (!Utils.isEmpty(up_rank)) {
			arr = up_rank.split(",");
			//insuarr.length
		}
        
        Log.e("NI::","hello:"+prac_name);
        Log.e("NI::","hello:"+prac_ids);
        if(!Utils.isEmpty(prac_name)){
	        String pNameArr[] = (prac_name).split(",");
	        String pIdArr[] = (prac_ids).split(",");
	        //String pName = "";
	        for(int i=0; i<pNameArr.length && i<arr.length && i<pIdArr.length; i++) {
	        	//System.out.println("PRAC="+pNameArr[i]);
	        	
	        	String name = "";
	    		String address= "";
	    		String rank = "";
	    		
	    		name = pNameArr[i];
	    		Log.e("NI::",""+new Integer(pIdArr[i]));
	    		cr2 = dba.fetchByPracId(new Integer(pIdArr[i]));
	    		try{
		    		if(cr2!=null){
		    			//Log.e("NI::",""+cr2.getColumnIndex("address"));
		    			//Log.e("NI::",""+cr2.getCount());
		    			address = cr2.getString(cr2.getColumnIndex("address"));
		    		}
		    		else{
		    			address = "";
		    		}
	    		}
	    		catch(Exception ex){
	    			Log.e("NI::",ex.getMessage());
	    			address = "";
	    		}
	    		rank = arr[i];
	        	
	        	if(i == 0)
	        	{
	        		practices = "- "+name+"\n"+address;
	        		practices = practices +"\nPA Rank: "+rank;
	        		
	        	}
	        	else if(pNameArr.length == i+1)
	        	{
	        		practices = practices + "\n\n- " +name+"\n"+address;	
	        		practices = practices +"\nPA Rank: "+rank;
		    		
	        	}
	        	else
	        	{
	        		practices = practices + "\n\n- " +name+"\n"+address;	
	        		practices = practices +"\nPA Rank: "+rank;
		    		
	        	}
	    	}
        }
        /*
        cr2 = dba.fetchPractice(DbAdapter.PRACTICE, docins);
        cr2.moveToFirst();        
        for(int i=0; i<cr2.getCount(); i++) {
        	if(i == 0)
        	{
        		practices = "- "+cr2.getString(2)+"\n"+cr2.getString(3)+"\n\n";
        		Log.d("NR::", cr2.getString(2));
        		cr2.moveToNext();
        	}
        	else if(cr2.getCount() == i+1)
        	{
        		practices = practices + "- " +cr2.getString(2)+"\n"+cr2.getString(3);	
	        	Log.d("NR::", cr2.getString(2));
	    		cr2.moveToNext();
        	}
        	else
        	{
        		practices = practices + "- " +cr2.getString(2)+"\n"+cr2.getString(3)+"\n\n";	
	        	Log.d("NR::", cr2.getString(2));
	    		cr2.moveToNext();
        	}
    	}
        */
        
        Map<String, String> data = new HashMap<String, String>();
        Long practiceId = 0L;
        Long hospitalId = 0L;
        Long specId = 0L;
        Long insuranceId = 0L;
        String pId = "";
        String hId = "";
        String sId = "";
        String iId = "";
        
        if(cr != null && cr.getCount() > 0) {
        	cr.moveToFirst();
        	data.put("first_name", cr.getString(3));
        	data.put("last_name", cr.getString(2));
        	data.put("mid_name", cr.getString(4));
        	data.put("degree", cr.getString(5));
        	data.put("phone", cr.getString(6));
        	data.put("language", cr.getString(7));
        	data.put("image_url", cr.getString(10));
        	data.put("grade", cr.getString(8));
        	data.put("gender", (Long.parseLong(cr.getString(9)) == 1 ? "Male" : "Female"));
        	data.put("see_patient", cr.getString(18));
        	data.put("u_rank", (Utils.isEmpty(cr.getString(22)) ? "0" : cr.getString(22)));
        	data.put("quality", (Utils.isEmpty(cr.getString(24)) ? "0" : cr.getString(24)));
        	data.put("cost", (Utils.isEmpty(cr.getString(25)) ? "0" : cr.getString(25)));
        	data.put("rank_user_number", (Utils.isEmpty(cr.getString(26)) ? "0" : cr.getString(26)));
        	data.put("avg_rank", (Utils.isEmpty(cr.getString(27)) ? "0" : cr.getString(27)));
        	
        	practiceId= Long.parseLong(cr.getString(11));
        	hospitalId = Long.parseLong(cr.getString(12));
        	specId = Long.parseLong(cr.getString(13));;
        	insuranceId = Long.parseLong(cr.getString(14));
        	
        	for(int i=0; i<cr.getCount(); i++) {
        		pId = Utils.isEmpty(pId) ? cr.getString(11) : pId+","+cr.getString(11);
        		hId = Utils.isEmpty(hId) ? cr.getString(12) : hId+","+cr.getString(12);
        		sId = Utils.isEmpty(sId) ? cr.getString(13) : sId+","+cr.getString(13);
        		iId = Utils.isEmpty(iId) ? cr.getString(14) : iId+","+cr.getString(14);	
        		cr.moveToNext();
        	}
        	
        	cr.close();
        }
        cr = dba.fetchReportByDocId(doctorId);
        if(cr != null && cr.getCount() > 0) {
        	data.put("report", cr.getString(3));
        	data.put("report_time", cr.getString(4));
        	cr.close();
        }
        cr = dba.fetchByNetIds(DbAdapter.PRACTICE, pId);
        if(cr != null && cr.getCount() > 0) {
        	String pName = "";
        	String pAdd = "";
        	for(int i=0; i<cr.getCount(); i++) {
        		pName = Utils.isEmpty(pName) ? cr.getString(2) : pName+"\n\n"+cr.getString(2);
        		pName = pName +"\n"+cr.getString(3);
        		pName = pName +"\nPA Rank: 0";
        		//data.put("practice_add", cr.getString(3));
        		cr.moveToNext();
        	}
        	data.put("practice_name", practices);
    		data.put("practice_add", pAdd);
        	cr.close();
        }
        cr = dba.fetchByNetIds(DbAdapter.HOSPITAL, hId);
        if(cr != null && cr.getCount() > 0) {
        	String hName = "";
        	String hAdd = "";
        	for(int i=0; i<cr.getCount(); i++) {
        		hName = Utils.isEmpty(hName) ? cr.getString(2) : hName+"\n"+cr.getString(2);
        		hName = hName+" (N)";
        		cr.moveToNext();
        	}
        	data.put("hospital_name", hospitals);
        	data.put("hospital_add", hAdd);
        	cr.close();
        }
        cr = dba.fetchByNetIds(DbAdapter.SPECIALTY, sId);
        if(cr != null && cr.getCount() > 0) {
        	String sName = "";
        	for(int i=0; i<cr.getCount(); i++) {
        		sName = Utils.isEmpty(sName) ? cr.getString(2) : sName+","+cr.getString(2);
        		cr.moveToNext();
        	}
        	data.put("speciality", specialities);        	
        	cr.close();
        }
        data.put("acos", acos); 
        /*
        cr = dba.fetchByNetId(DbAdapter.PRACTICE, practiceId);
        if(cr != null && cr.getCount() > 0) {
        	data.put("practice_name", cr.getString(2));
        	data.put("practice_add", cr.getString(3));
        	cr.close();
        }
        cr = dba.fetchByNetId(DbAdapter.HOSPITAL, hospitalId);
        if(cr != null && cr.getCount() > 0) {
        	data.put("hospital_name", cr.getString(2));
        	data.put("hospital_add", cr.getString(3));
        	cr.close();
        }
        cr = dba.fetchByNetId(DbAdapter.SPECIALTY, specId);
        if(cr != null && cr.getCount() > 0) {
        	data.put("speciality", cr.getString(2));        	
        	cr.close();
        }
        */
        cr = dba.fetchByNetId(DbAdapter.INSURANCE, insuranceId);
        if(cr != null && cr.getCount() > 0) {
        	data.put("insurance", insurances);  
        	data.put("insurance_add", cr.getString(3)); 
        	cr.close();
        }
            
        return data;
	}
	
	private void populateData(final Map<String, String> data) {
		TextView doctorFullNameTxt = (TextView) findViewById(R.id.doctor_full_name);
		docName = data.get("first_name") + " " + data.get("mid_name") + " " + data.get("last_name");
		doctorFullNameTxt.setText(docName+", "+data.get("degree"));
	
		//docImage.setVisibility(View.GONE);
		//pBar.setVisibility(View.VISIBLE);
		//int userRankValue = 0;
		//int pracRankValue = 0;
		/*
		try {
			new DownloadFilesTask().execute(new URL("http://203.202.248.108/dsi/irefer3/pics/"+data.get("image_url")));
		} catch(Exception ex){
			ex.printStackTrace();
		}
		*/
		try {
			userRankValue = Integer.parseInt(Utils.isEmpty(data.get("u_rank")) ? "0" : data.get("u_rank"));
		} catch(Exception ex){
			ex.printStackTrace();
		}
		try {
			userGradeValue = Integer.parseInt(Utils.isEmpty(data.get("grade")) ? "0" : data.get("grade"));
		} catch(Exception ex){
			ex.printStackTrace();
		}
		try {
			userQualityValue = Integer.parseInt(Utils.isEmpty(data.get("quality")) ? "0" : data.get("quality"));
		} catch(Exception ex){
			ex.printStackTrace();
		}
                
		try {
			userCostValue = Integer.parseInt(Utils.isEmpty(data.get("cost")) ? "0" : data.get("cost"));
		} catch(Exception ex){
			ex.printStackTrace();
		}

		try {
			rankUserNumber = Integer.parseInt(Utils.isEmpty(data.get("rank_user_number")) ? "0" : data.get("rank_user_number"));
		} catch(Exception ex){
			ex.printStackTrace();
		}

		try {
			avgRank = Double.parseDouble(Utils.isEmpty(data.get("avg_rank")) ? "0" : data.get("avg_rank"));
		} catch(Exception ex){
			ex.printStackTrace();
		}
                
                TextView doctorAvgRank =  (TextView)findViewById(R.id.doctor_avg_rank);
                doctorAvgRank.setText("Avg Rank: " + avgRank + " based on " + rankUserNumber + " PCP votes");
                    
		userRank.setRating(userGradeValue);
		
		System.out.println("Grade="+userGradeValue);
		
		for(int i=0; i<userGradeValue; i++){
			ImageView img =  (ImageView)findViewById(gradeIdArr[i]);
			//img.setBackgroundDrawable(getResources().getDrawable(R.drawable.star_icon));
			img.setImageResource(R.drawable.star_icon);
		}
		for(int i=0; i<userQualityValue; i++){
			ImageView img =  (ImageView)findViewById(qualityIdArr[i]);
			//img.setBackgroundDrawable(getResources().getDrawable(R.drawable.star_icon));
			img.setImageResource(R.drawable.star_icon);
		}

		for(int i=0; i< Utils.getRandomNumber(2,4) ; i++){
			ImageView img =  (ImageView)findViewById(pexpIdArr[i]);
			img.setImageResource(R.drawable.star_icon);
		}

		for(int i=0; i<userCostValue; i++){
			TextView cost =  (TextView)findViewById(costIdArr[i]);
			cost.setTextColor(Color.rgb(255, 165, 0));
		}
		
		if(data.get("report") != null) {
			reportLayout.setVisibility(View.VISIBLE);
			TextView reportTxt = (TextView) findViewById(R.id.doctor_detail_report_label);
			reportTxt.setText(data.get("report_time")+"\n"+data.get("report"));
			reportTxt.setVisibility(View.VISIBLE);
		}
		
		rankTxt = (TextView) findViewById(R.id.doctor_detail_rank_value);
		rankTxt.setText(Utils.isEmpty((String)data.get("u_rank")) ? "0" : data.get("u_rank"));
		
		TextView doctorFullNameTxt1 = (TextView) findViewById(R.id.doctor_full_name1);
		doctorFullNameTxt1.setText(data.get("speciality"));
		//TextView doctorFullNameTxt2 = (TextView) findViewById(R.id.doctor_full_name2);
		//doctorFullNameTxt2.setText(data.get("degree"));
		
		TextView doctorFullNameTxt2 = (TextView) findViewById(R.id.doctor_full_name2);
		doctorFullNameTxt2.setText(data.get("insurance"));
		
		TextView doctorFullNameTxt3 = (TextView) findViewById(R.id.doctor_full_name3);
		doctorFullNameTxt3.setText("Collaborates: Yes");
		
		String dgl = "";
		if(data.get("gender") != null && !data.get("gender").equals("")) {
			dgl += "Sex: " + data.get("gender") + "\n";
		}
		//if(data.get("degree") != null && !data.get("degree").equals("")) {
		//	dgl += "Degree: " + data.get("degree");
		//}
		if(!Utils.isEmpty(data.get("npi"))) {
			dgl += "NPI: " + data.get("npi") + "\n";
		} else 
			dgl += "NPI: NA\n";
		//if(data.get("grade") != null && !data.get("grade").equals("")) {
		//	dgl += "Grade: " + data.get("grade") + "\n";
		//}
		//if(data.get("language") != null && !data.get("language").equals("")) {
		//	dgl += "Language: " + data.get("language") + "\n";
		//}
		
		if(!Utils.isEmpty(data.get("phone"))) {
			dgl += "Phone: " + data.get("phone") + "\n";
		}
		if(!Utils.isEmpty(data.get("fax"))) {
			dgl += "FAX: " + data.get("fax") + "\n";
		}
		if(!Utils.isEmpty(data.get("office_hour"))) {
			dgl += "Office Hour: " + data.get("office_hour") + "\n";
		}
		TextView dglTxt = (TextView) findViewById(R.id.doctor_detail_label1);		
		if(dgl.equals("")) {
			( (LinearLayout) findViewById(R.id.doc_detail_holder_one) ).setVisibility(View.GONE);
		}else {			
			dglTxt.setText(dgl);
		}
		
		TextView dglTxt2 = (TextView) findViewById(R.id.doctor_detail_label2);		
		String phsi = "";
		if(!Utils.isEmpty(data.get("practice_name"))) {
			//String pNameArr[] = ((String)data.get("practice_name")).split("|");
			//String pAddArr[] = ((String)data.get("practice_add")).split("|");
			phsi += "" + data.get("practice_name")+ "\n";
			//phsi += "" + data.get("practice_add")+ "\n";
		}		
		dglTxt2.setText(phsi);
		
		TextView dglTxt3 = (TextView) findViewById(R.id.doctor_detail_label3);		
		String hosp = "";
		if(!Utils.isEmpty(data.get("hospital_name"))) {
			hosp += "" + data.get("hospital_name").replace('|', '\n')+ "\n";
			//if(!Utils.isEmpty(data.get("hospital_add")))
			//	hosp += "" + data.get("hospital_add")+ "\n";
			//hosp += "1".equals(data.get("see_patient")) ? "See Patient" : "" + "\n";
		}
		dglTxt3.setText(hosp);
		
		TextView dglTxt5 = (TextView) findViewById(R.id.doctor_detail_label4);		
		String acos = "";
		if(!Utils.isEmpty(data.get("acos"))) {
			acos += "" + data.get("acos").replace('|', '\n')+ "\n";
			//if(!Utils.isEmpty(data.get("hospital_add")))
			//	hosp += "" + data.get("hospital_add")+ "\n";
			//hosp += "1".equals(data.get("see_patient")) ? "See Patient" : "" + "\n";
		}
		dglTxt5.setText(acos);
		
		TextView dglTxt4 = (TextView) findViewById(R.id.doctor_detail_label_bottom);		
		String note = "";
		if(!Utils.isEmpty(data.get("note"))) {
			note += data.get("note")+ "\n";
		}
		dglTxt4.setText(note);
		
		//if(data.get("speciality") != null && !data.get("speciality").equals("")) {
		//	phsi += " Speciality: " + data.get("speciality");
		//}
		//if(data.get("insurance") != null && !data.get("insurance").equals("")) {
			//phsi += "Insurance: " + data.get("insurance")+ "\n";
			//phsi += "" + data.get("insurance_add")+ "\n";
		//}
		
		//TextView phsiTxt = (TextView) findViewById(R.id.doctor_detail_label2);		
		//if(phsi.equals("")) {
		//	( (LinearLayout) findViewById(R.id.doc_detail_holder_two) ).setVisibility(View.GONE);
		//}else {			
		//	phsiTxt.setText(phsi);
		//}
		
		Button back = (Button) findViewById(R.id.back_to_filter_button);
		back.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				intent.putExtra("close_me", true);
				intent.putExtra("rank", rankTxt.getText().toString());
				
				setResult(RESULT_OK, intent);
				finish();
			}
		});
		
		Button call = (Button) findViewById(R.id.call_button);
		call.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {				
				if(data.get("phone") != null && !data.get("phone").equals("")) {
					startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + data.get("phone"))));	
				}
				else{
            		Toast.makeText(ctx, "No phone number given.", Toast.LENGTH_SHORT).show(); 
            	}
			}
		});
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
   
    private JSONObject downloadDoctors(Integer docId) throws Exception {
        String jsonData = "";
        String stringURL = ABC.WEB_URL+"doctor/docJson?doc_id=" + docId+"&user_id="+userId;
        Log.d("NI::",stringURL);
        jsonData = getDataFromURL(stringURL);
        JSONTokener jsonTokener = new JSONTokener(jsonData);
        JSONArray arr = (JSONArray) jsonTokener.nextValue();
        return arr.getJSONObject(0);
    }       
   
    private class DownloadDoctorDetailsTask extends AsyncTask<Integer, Integer, Integer> {
        private final Integer STATUS_OK = 0;
        private final Integer STATUS_ERROR = 1;
        JSONObject jsonObject;
       
        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(DoctorDetailActivity.this, "", "loading data ...", true);
        }
        @Override
        protected Integer doInBackground(Integer...params) {           
            try {
                jsonObject = downloadDoctors(params[0]);
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
            if(result == STATUS_OK) {               
                try {                   
                    Map<String, String> data = new HashMap<String, String>();
                    //System.out.println(jsonObject.toString());
                    //System.out.println(jsonObject.getString("first_name") + "::::::");
                    data.put("first_name", jsonObject.getString("first_name"));
                    data.put("last_name", jsonObject.getString("last_name"));
                    data.put("mid_name", ((Utils.isEmpty(jsonObject.getString("mid_name"))||jsonObject.getString("mid_name").equals("null")) ? "" : jsonObject.getString("mid_name")));
                    data.put("degree", ((Utils.isEmpty(jsonObject.getString("degree"))||jsonObject.getString("degree").equals("null")) ? "" : jsonObject.getString("degree")));
                    data.put("phone", ((Utils.isEmpty(jsonObject.getString("doc_phone"))||jsonObject.getString("doc_phone").equals("null")) ? "" : jsonObject.getString("doc_phone")));
                    data.put("fax", ((Utils.isEmpty(jsonObject.getString("doc_fax"))||jsonObject.getString("doc_fax").equals("null")) ? "" : jsonObject.getString("doc_fax")));
                    data.put("language", ((Utils.isEmpty(jsonObject.getString("language"))||jsonObject.getString("language").equals("null")) ? "" : jsonObject.getString("language")));
                    data.put("grade", ((Utils.isEmpty(jsonObject.getString("grade"))||jsonObject.getString("grade").equals("null")) ? "" : jsonObject.getString("grade")));
                    data.put("office_hour", ((Utils.isEmpty(jsonObject.getString("office_hour"))||jsonObject.getString("office_hour").equals("null")) ? "" : jsonObject.getString("office_hour")));
                    data.put("npi", ((Utils.isEmpty(jsonObject.getString("npi"))||jsonObject.getString("npi").equals("null")) ? "" : jsonObject.getString("npi")));
                    data.put("image_url", ((Utils.isEmpty(jsonObject.getString("image_url"))||jsonObject.getString("image_url").equals("null")) ? "" : jsonObject.getString("image_url")));
                    data.put("see_patient", ((Utils.isEmpty(jsonObject.getString("see_patient"))||jsonObject.getString("see_patient").equals("null")) ? "" : jsonObject.getString("see_patient")));
                    if(jsonObject.getString("gender") != null && "1".equals(jsonObject.getString("gender")))
                    	data.put("gender", "Male");
                    else if(jsonObject.getString("gender") != null && "0".equals(jsonObject.getString("gender")))
                    	data.put("gender", "Female");
                    else
                    	data.put("gender", "");
                    String prac_name = ((Utils.isEmpty(jsonObject.getString("prac_name"))||jsonObject.getString("prac_name").equals("null")) ? "" : jsonObject.getString("prac_name"));
                    String pNameArr[] = (prac_name).split("\\|");
                    //String pNameArr[] = "abc|hi5| hello".split("|");
                    String add_line_1 = ((Utils.isEmpty(jsonObject.getString("add_line_1"))||jsonObject.getString("add_line_1").equals("null")) ? "" : jsonObject.getString("add_line_1"));
                    String pAddArr[]  = (add_line_1).split("\\|");
                    System.out.println("prac_name="+jsonObject.getString("prac_name"));
                    String pName = "";
                    for(int i=0; i<pNameArr.length && i<pAddArr.length; i++) {
                    	System.out.println("PRAC="+pNameArr[i]);
                		pName = Utils.isEmpty(pName) ? pNameArr[i] : pName+"\n\n"+pNameArr[i];
                		pName = pName +"\n"+pAddArr[i];
                		pName = pName +"\nPA Rank: 0";
                	}
                    
                    data.put("practice_name", pName);
                    data.put("practice_add", "");
                    String hosp_name = ((Utils.isEmpty(jsonObject.getString("hosp_name"))||jsonObject.getString("hosp_name").equals("null")) ? "" : jsonObject.getString("hosp_name"));
                    String hNameArr[] = (hosp_name).split("\\|");
                    String see_patient = ((Utils.isEmpty(jsonObject.getString("see_patient"))||jsonObject.getString("see_patient").equals("null")) ? "" : jsonObject.getString("see_patient"));
                    String seePArr[]  = (see_patient).split("\\|");
                    String hName = "";
                	for(int i=0; i<hNameArr.length && i<seePArr.length; i++) {
                		hName = Utils.isEmpty(hName) ? hNameArr[i] : hName+"\n"+hNameArr[i];
                		if(seePArr[i].equals("0")){
                			if(!hName.equals(""))
                				hName = hName+" (N)";
                		}
                		else{
                			if(!hName.equals(""))
                				hName = hName+" (Y)";
                		}
                	}
                    data.put("hospital_name", hName);
                    data.put("acos", ((Utils.isEmpty(jsonObject.getString("aco_name"))||jsonObject.getString("aco_name").equals("null")) ? "" : jsonObject.getString("aco_name")));
                    data.put("speciality", ((Utils.isEmpty(jsonObject.getString("spec_name"))||jsonObject.getString("spec_name").equals("null")) ? "" : jsonObject.getString("spec_name")));
                    data.put("insurance", ((Utils.isEmpty(jsonObject.getString("insu_name"))||jsonObject.getString("insu_name").equals("null")) ? "" : jsonObject.getString("insu_name")));
                    data.put("u_rank", ((Utils.isEmpty(jsonObject.getString("u_rank"))||jsonObject.getString("u_rank").equals("null")) ? "" : jsonObject.getString("u_rank")));
                    data.put("up_rank", ((Utils.isEmpty(jsonObject.getString("up_rank"))||jsonObject.getString("up_rank").equals("null")) ? "" : jsonObject.getString("up_rank")));
                    data.put("quality", ((Utils.isEmpty(jsonObject.getString("quality"))||jsonObject.getString("quality").equals("null")) ? "" : jsonObject.getString("quality")));
                    data.put("cost", ((Utils.isEmpty(jsonObject.getString("cost"))||jsonObject.getString("cost").equals("null")) ? "" : jsonObject.getString("cost")));
                    data.put("rank_user_number", ((Utils.isEmpty(jsonObject.getString("rank_user_number"))||jsonObject.getString("rank_user_number").equals("null")) ? "" : jsonObject.getString("rank_user_number")));                    
                    data.put("avg_rank", ((Utils.isEmpty(jsonObject.getString("avg_rank"))||jsonObject.getString("avg_rank").equals("null")) ? "" : jsonObject.getString("avg_rank")));
                    populateData(data);
                }catch (Exception e) {
                    System.out.println(e);
                    System.out.println("data exception ...");
                }
            }else {               
                System.out.println("Error happened ...");
            }
            dialog.dismiss();
         }
    }
    
    private class DownloadFilesTask extends AsyncTask<URL, Integer, Long> {
    	
    	Drawable d;
    	
    	public Object fetch(URL url) throws MalformedURLException,IOException {
    		Object content = url.getContent();
    		return content;
    	}
    	
        protected Long doInBackground(URL... urls) {
        	//int count = urls.length;
            Log.d("NR::", "AAA");
        	long totalSize = 0;
            URL url = urls[0];
            try {
    			InputStream is = (InputStream) this.fetch(url);
    			d = Drawable.createFromStream(is, "src");
    		} catch (MalformedURLException e) {
    			e.printStackTrace();
    			return 0L;
    		} catch (IOException e) {
    			e.printStackTrace();
    			return 0L;
    		}
            return totalSize;
        }

        protected void onProgressUpdate(Integer... progress) {
        	Log.d("NR::", "AAA");
            //setProgressPercent(progress[0]);
        }

        protected void onPostExecute(Long result) {
        	docImage.setImageDrawable(d);
        	pBar.setVisibility(View.GONE);
        	docImage.setVisibility(View.VISIBLE);
            //showDialog("Downloaded " + result + " bytes");
        }
    }
	
}
