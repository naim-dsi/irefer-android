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

public class DoctorReportDialog extends Dialog {
    
	//DoctorDetailActivity ctx;
	//final DbAdapter dba;
	
	public DoctorReportDialog(final DoctorDetailActivity ctx, int style, 
			final DbAdapter dba,
			final int doctorId,
			final String userId){
		super(ctx, style);
		//this.dba = dba;
		this.setContentView(R.layout.doctor_report_dialog);
		this.setCancelable(true);
		/* TODO: save button */
		Button reportSaveBtn = (Button) this.findViewById(R.id.doc_report_dialog_save);
		Button closeButton = (Button) this.findViewById(R.id.doc_report_dialog_close);
		   
		reportSaveBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String str = ((EditText)findViewById(R.id.doc_report_text)).getText().toString()+"\n";
				String str2 = Utils.getCurrentTime();
				CheckBox chkBox1 = (CheckBox)findViewById(R.id.doc_report_checkbox1);
				CheckBox chkBox2 = (CheckBox)findViewById(R.id.doc_report_checkbox2);
				CheckBox chkBox3 = (CheckBox)findViewById(R.id.doc_report_checkbox3);
				CheckBox chkBox4 = (CheckBox)findViewById(R.id.doc_report_checkbox4);
				if(chkBox1.isChecked())
					str += "Provider not in Plan\n";
				if(chkBox2.isChecked())
					str += "Address or phone number incorrect\n";
				if(chkBox3.isChecked())
					str += "Incorrect speciality\n";
				if(chkBox4.isChecked())
					str += "Hospital list incorrect\n";
				long reportId = dba.insert(DbAdapter.DOC_REPORT, new String[]{doctorId+"", userId, str, str2,""});
				//ctx.reportTxt.setText(str2+"\n"+str);
				//reportLayout.setVisibility(View.VISIBLE);
				//reportTxt.setVisibility(View.VISIBLE);
				//reportTextEdit.setVisibility(View.GONE);
				//reportBtn.setVisibility(View.GONE);
				Toast.makeText(ctx, "Report Saved", Toast.LENGTH_SHORT).show();
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
