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

import android.app.Activity;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ReferDialog extends Dialog {
    
	//DoctorDetailActivity ctx;
	//final DbAdapter dba;
	
	public ReferDialog(final DoctorDetailActivity ctx, int style, 
			final DbAdapter dba,
			final int doctorId,
			final String userId, final String name){
		super(ctx, style);
		//this.dba = dba;
		this.setContentView(R.layout.doctor_refer);
		this.setCancelable(true);
		/* TODO: save button */
		TextView doctor_name = (TextView) this.findViewById(R.id.doc_report_headline);
		doctor_name.setText("Dr. "+name);
		Button reportSaveBtn = (Button) this.findViewById(R.id.doc_report_dialog_save);
		Button closeButton = (Button) this.findViewById(R.id.doc_report_dialog_close);
		Log.d("NR::", ""+doctorId);
		Log.d("NR::userId::", ""+userId);
		reportSaveBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try{
					String patient_ini = ((EditText)findViewById(R.id.patient_ini)).getText().toString()+"";
					String patient_email = ((EditText)findViewById(R.id.patient_email)).getText().toString()+"";
					if(patient_ini.equals("")){
						Toast.makeText(ctx, "Please give patient initial", Toast.LENGTH_LONG).show();
						return;
					}
					String insurance = "";
					//String str2 = Utils.getCurrentTime();
					CheckBox chkBox1 = (CheckBox)findViewById(R.id.doc_insurance);
					
					if(chkBox1.isChecked())
					{
						insurance = "1";
					}
					else
					{
						insurance = "0";
					}
					int currentDocId = dba.getDoctorIdbyUserId(dba.USERS,Utils.userId+"");
					Log.d("NR::", "X = "+doctorId +", Y = "+currentDocId);
					String jsonData = "";       
			        try {
			        	Log.d("NR::",ABC.WEB_URL+"doctor/referral2?doc_id=" + currentDocId+"&ref_doc_id="+doctorId+"&insurance="+insurance+"&email="+patient_email.trim()+"&initial="+patient_ini.trim());
						jsonData = getDataFromURL(ABC.WEB_URL+"doctor/referral2?doc_id=" + currentDocId+"&ref_doc_id="+doctorId+"&insurance="+insurance+"&email="+patient_email.trim()+"&initial="+patient_ini.trim());
						Log.d("NI::jsonData::",jsonData);
			        } catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			        if(jsonData.equals("Not saved")){
			        	Toast.makeText(ctx, "Error occured, please try again later.", Toast.LENGTH_LONG).show();
			        	return;
			        }
					Toast.makeText(ctx, "Referral Successful", Toast.LENGTH_LONG).show();
					
					dismiss();
				}
				catch(Exception e){
					Log.e("NI::",e.getMessage());
					Toast.makeText(ctx, e.getMessage(), Toast.LENGTH_LONG).show();
				}
				//System.out.println("SMM::reportID="+reportId);
			}
		});
		
		closeButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				dismiss();
				//System.out.println("SMM::reportID="+reportId);
			}
		});
		
		//there are a lot of settings, for dialog, check them all out!
		//set up text
		//TextView text = (TextView) rankDialog.findViewById(R.id.rank_dialog_text1);
		//text.setText(docName);

		//set up button
		
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
	
}
