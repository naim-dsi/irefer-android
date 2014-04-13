package com.dsinv.irefer2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

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

public class DoctorReportDialog extends Dialog {
    
	//DoctorDetailActivity ctx;
	//final DbAdapter dba;
	
	public DoctorReportDialog(final DoctorDetailActivity ctx, int style, 
			final DbAdapter dba,
			final int doctorId,
			final String userId,
			final Boolean isOnlineSearch){
		super(ctx, style);
		//this.dba = dba;
		this.setContentView(R.layout.doctor_report_dialog);
		this.setCancelable(true);
		/* TODO: save button */
		Button reportSaveBtn = (Button) this.findViewById(R.id.doc_report_dialog_save);
		Button closeButton = (Button) this.findViewById(R.id.doc_report_dialog_close);
		   
		reportSaveBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
					try{
					String str = ((EditText)findViewById(R.id.doc_report_text)).getText().toString()+"\n";
					String str2 = Utils.getCurrentTime();
					long dtMili = System.currentTimeMillis();
					String format = "yyyy-MM-dd HH:mm:ss";
					SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
				    //System.out.format("%30s %s\n", format, sdf.format(new Date(0)));
				    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
				    str2 = sdf.format(new Date(dtMili));
				    Log.d("NI","Current Time::"+str2);
					CheckBox chkBox1 = (CheckBox)findViewById(R.id.doc_report_checkbox1);
					CheckBox chkBox2 = (CheckBox)findViewById(R.id.doc_report_checkbox2);
					CheckBox chkBox3 = (CheckBox)findViewById(R.id.doc_report_checkbox3);
					CheckBox chkBox4 = (CheckBox)findViewById(R.id.doc_report_checkbox4);
					str=str.trim();
					if(chkBox1.isChecked()){
						if(str.equals("")){
							str = "Provider not in Plan.";
						}
						else{
							str += "\nProvider not in Plan.";
						}
					}	
					if(chkBox2.isChecked()){
						if(str.equals("")){
							str = "Address or phone number incorrect.";
						}
						else{
							str += "\nAddress or phone number incorrect.";
						}
					}	
						
					if(chkBox3.isChecked()){
						if(str.equals("")){
							str = "Incorrect speciality.";
						}
						else{
							str += "\nIncorrect speciality.";
						}
					}	
						
					if(chkBox4.isChecked()){
						if(str.equals("")){
							str = "Hospital list incorrect.";
						}
						else{
							str += "\nHospital list incorrect.";
						}
					}
					Log.d("NI","Report::"+str);
					String strX=str;	
					String strX2=str2;	
					long reportId = dba.insert(DbAdapter.DOC_REPORT, new String[]{doctorId+"", Utils.userId+"", str, str2,""});
					if(isOnlineSearch){
						try {
							str = URLEncoder.encode(str, "utf-8");
						} catch (UnsupportedEncodingException e1) {
							// TODO Auto-generated catch block
							str=strX;
							e1.printStackTrace();
						}
						try {
							str2 = URLEncoder.encode(str2, "utf-8");
						} catch (UnsupportedEncodingException e1) {
							// TODO Auto-generated catch block
							str2=strX2;
							e1.printStackTrace();
						}
						//ctx.reportTxt.setText(str2+"\n"+str);
						//reportLayout.setVisibility(View.VISIBLE);
						//reportTxt.setVisibility(View.VISIBLE);
						//reportTextEdit.setVisibility(View.GONE);
						//reportBtn.setVisibility(View.GONE);
						
						String jsonData = "";       
						try {
							String urlStr = ABC.WEB_URL+"doctor/report?doc_id=" + Utils.userId+"&ref_doc_id="+doctorId+"&report="+str.trim()+"&time="+str2;
							Log.d("NI","URL::"+urlStr);
							jsonData = getDataFromURL(urlStr);
							Log.d("NI","Response::"+jsonData);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							Toast.makeText(ctx, "Report has been Saved to online storage.", Toast.LENGTH_SHORT).show();
							e.printStackTrace();
						}
					}
					Toast.makeText(ctx, "Report has been Saved", Toast.LENGTH_SHORT).show();
					
					Cursor cr = dba.fetchReportByDocId(doctorId);
			        if(cr != null && cr.getCount() > 0) {
			        	String report = cr.getString(3);
			        	String report_time = cr.getString(4);
			        	cr.close();
			        	if(!report.equals("")) {
			        		
			    			ctx.reportLayout.setVisibility(View.VISIBLE);
			    			ctx.reportTxt.setText(strX2+"\n"+strX);
			    			ctx.reportTxt.setVisibility(View.VISIBLE);
			    		}
			        }
				}
				catch(Exception ex){
					ex.printStackTrace();
					Toast.makeText(ctx, "Report has not been Saved", Toast.LENGTH_SHORT).show();
					return;
				}
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
