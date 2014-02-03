package com.dsinv.irefer2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.dsinv.irefer2.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ResourceDetailActivity extends Activity {
    
	private DbAdapter dba;	
	private Intent intent;
	private ProgressDialog dialog;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_detail_temp);
        setTitle( getString( R.string.app_name ) + " - Resource Details");
        dba = new DbAdapter(this);
        dba.open();
        intent = getIntent();
        int doctorId = intent.getIntExtra("doctor_id", 8030);        
        boolean isOnlineSearch = intent.getBooleanExtra("is_online_search", false);
        if(isOnlineSearch) {        	
        	new DownloadDoctorDetailsTask().execute(doctorId);
        	//populateData( loadDoctorDetailsFromWeb((long)doctorId) );
        }else {
        	populateData( loadDoctorDetailsFromDatabase((long)doctorId) );	
        }        
    }
	
	private Map<String, String> loadDoctorDetailsFromDatabase(Long doctorId) {
        Cursor cr = dba.fetch(DbAdapter.DOCTOR, doctorId);        
        Map<String, String> data = new HashMap<String, String>();
        Long practiceId = 0L;
        Long hospitalId = 0L;
        Long specId = 0L;
        Long insuranceId = 0L;
        if(cr != null && cr.getCount() > 0) {
        	cr.moveToFirst();
        	data.put("first_name", cr.getString(3));
        	data.put("last_name", cr.getString(2));
        	data.put("mid_name", cr.getString(4));
        	data.put("degree", cr.getString(5));
        	data.put("phone", cr.getString(6));
        	data.put("language", cr.getString(7));
        	data.put("grade", cr.getString(8));
        	data.put("gender", (Long.parseLong(cr.getString(9)) == 1 ? "Male" : "Female"));
        	
        	practiceId= Long.parseLong(cr.getString(11));
        	hospitalId = Long.parseLong(cr.getString(12));
        	specId = Long.parseLong(cr.getString(13));;
        	insuranceId = Long.parseLong(cr.getString(14));
        	cr.close();
        }
        cr = dba.fetchByNetId(DbAdapter.PRACTICE, practiceId, "prac_id");
        if(cr != null && cr.getCount() > 0) {
        	data.put("practice_name", cr.getString(2));        	
        	cr.close();
        }
        cr = dba.fetchByNetId(DbAdapter.HOSPITAL, hospitalId, "hos_id");
        if(cr != null && cr.getCount() > 0) {
        	data.put("hospital_name", cr.getString(2));
        	cr.close();
        }
        cr = dba.fetchByNetId(DbAdapter.SPECIALTY, specId, "spec_id");
        if(cr != null && cr.getCount() > 0) {
        	data.put("speciality", cr.getString(2));        	
        	cr.close();
        }
        cr = dba.fetchByNetId(DbAdapter.INSURANCE, insuranceId, "ins_id");
        if(cr != null && cr.getCount() > 0) {
        	data.put("insurance", cr.getString(2));        	
        	cr.close();
        }        
        return data;
	}
			
	private void populateData(final Map<String, String> data) {
		TextView doctorFullNameTxt = (TextView) findViewById(R.id.doctor_full_name);
		doctorFullNameTxt.setText(data.get("first_name") + " " + data.get("mid_name") + " " + data.get("last_name"));
		
		String dgl = "";
		if(data.get("degree") != null && !data.get("degree").equals("")) {
			dgl += "Degree: " + data.get("degree");
		}		
		if(data.get("grade") != null && !data.get("grade").equals("")) {
			dgl += " Grade: " + data.get("grade") + "\n";
		}
		if(data.get("language") != null && !data.get("language").equals("")) {
			dgl += "Language: " + data.get("language") + "\n";
		}
		if(data.get("gender") != null && !data.get("gender").equals("")) {
			dgl += "Gender: " + data.get("gender") + "\n";
		}
		if(data.get("phone") != null && !data.get("phone").equals("")) {
			dgl += "Phone: " + data.get("phone");
		}
		TextView dglTxt = (TextView) findViewById(R.id.doctor_detail_label1);		
		if(dgl.equals("")) {
			( (LinearLayout) findViewById(R.id.doc_detail_holder_one) ).setVisibility(View.GONE);
		}else {			
			dglTxt.setText(dgl);
		}
		
		String phsi = "";
		if(data.get("practice_name") != null && !data.get("practice_name").equals("")) {
			phsi += "Practice: " + data.get("practice_name");
		}		
		if(data.get("hospita_name") != null && !data.get("hospita_name").equals("")) {
			phsi += " Hospital: " + data.get("hospita_name");
		}
		if(data.get("speciality") != null && !data.get("speciality").equals("")) {
			phsi += " Speciality: " + data.get("speciality");
		}
		if(data.get("insurance") != null && !data.get("insurance").equals("")) {
			phsi += " Insurance: " + data.get("insurance");
		}
		
		TextView phsiTxt = (TextView) findViewById(R.id.doctor_detail_label2);		
		if(phsi.equals("")) {
			( (LinearLayout) findViewById(R.id.doc_detail_holder_two) ).setVisibility(View.GONE);
		}else {			
			phsiTxt.setText(phsi);
		}
		
		Button back = (Button) findViewById(R.id.back_to_filter_button);
		back.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				intent.putExtra("close_me", true);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
		
		Button call = (Button) findViewById(R.id.call_button);
		call.setText("Call Resource");
		call.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {				
				if(data.get("phone") != null && !data.get("phone").equals("")) {
					startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + data.get("phone"))));	
				}				            	
			}
		});
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
   
    private JSONObject downloadDoctors(Integer docId) throws Exception {
        String jsonData = "";       
        jsonData = getDataFromURL(ABC.WEB_URL+"doctor/docJson?doc_id=" + docId);
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
            dialog = ProgressDialog.show(ResourceDetailActivity.this, "", "loading data ...", true);
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
                    System.out.println(jsonObject.toString());
                    System.out.println(jsonObject.getString("first_name") + "::::::");
                    data.put("first_name", jsonObject.getString("first_name"));
                    data.put("last_name", jsonObject.getString("last_name"));
                    data.put("mid_name", jsonObject.getString("mid_name"));
                    data.put("degree", jsonObject.getString("degree"));
                    data.put("phone", jsonObject.getString("doc_phone"));
                    data.put("language", jsonObject.getString("language"));
                    data.put("grade", jsonObject.getString("grade"));
                    data.put("gender", jsonObject.getString("gender"));
                    data.put("practice_name", jsonObject.getString("practice_id"));
                    data.put("hospital_name", jsonObject.getString("speciality_id"));
                    data.put("speciality", jsonObject.getString("hospital_id"));
                    data.put("insurance", jsonObject.getString("insurance_id"));
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
	
}
