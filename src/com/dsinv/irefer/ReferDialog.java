package com.dsinv.irefer;

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

import com.dsinv.irefer.R;
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
		reportSaveBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String patient_ini = ((EditText)findViewById(R.id.patient_ini)).getText().toString()+"\n";
				String patient_email = ((EditText)findViewById(R.id.patient_email)).getText().toString()+"\n";
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
				int currentDocId = dba.getDoctorIdbyUserId(dba.USERS,userId);
				Log.d("NR::", "X = "+doctorId +", Y = "+currentDocId);
				String jsonData = "";       
//		        jsonData = getDataFromURL(ABC.WEB_URL+"doctor/referral?doc_id=" + currentDocId+"&ref_doc_id="+doctorId+"&initial="+initial+"&insurance="+insurance+"&email="+email);
//		        JSONTokener jsonTokener = new JSONTokener(jsonData);
//		        JSONArray arr = (JSONArray) jsonTokener.nextValue();
//		        JSONObject x = arr.getJSONObject(0);
				Toast.makeText(ctx, "Referral Successful", Toast.LENGTH_LONG).show();
				
				dismiss();
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
	
}
