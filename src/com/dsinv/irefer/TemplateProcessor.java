package com.dsinv.irefer;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;

public class TemplateProcessor {

	private DbAdapter dba;
	private Activity ctx;
	
	public TemplateProcessor(IreferActivity ireferActivity, DbAdapter dba) {
		this.ctx = ireferActivity;
		this.dba = dba;
	}
	
	public TemplateProcessor(SetupActivity activity, DbAdapter dba) {
		this.ctx = activity;
		this.dba = dba;
	}
	
	
	public void updateSearchCount(int userId) {
    	ProgressDialog dialogP = ProgressDialog.show(ctx, "", 
                "Loading. Please wait...", true);
    	dialogP.show();
    	try {
    		Cursor cr = dba.fetchAll(DbAdapter.STATISTICS);
    		int count = -1;
    		if(cr != null && cr.getCount() > 0) {
            	cr.moveToFirst();
            	count = cr.getInt(2);
    			cr.close();
            }
    		String res = "";
    		System.out.println("SMM::COUNT="+count);
    		if(count>0)
    			res = Utils.getDataFromURL(ABC.WEB_URL+"searchStatistics/setCount?user_id="+userId+"&count="+count);	
    	} catch(Exception ex) {
    		ex.printStackTrace();
    	}
    	dialogP.dismiss();
    }
	
	public void updateDocReport(int userId) {
    	ProgressDialog dialogP = ProgressDialog.show(ctx, "", 
                "Loading. Please wait...", true);
    	dialogP.show();
    	try {
    		Cursor cr = dba.fetchAll(DbAdapter.DOC_REPORT);
    		String str = "";
    		if(cr != null && cr.getCount() > 0) {
            	cr.moveToFirst();
            	for(int i=0; i<cr.getCount(); i++) {
            		if(i == 0)
            			str = cr.getInt(1) +","+cr.getString(3);
            		else
            			str = str+"|"+cr.getInt(1) +","+cr.getString(3);
            		cr.moveToNext();
            	}
    			cr.close();
            }
    		String res = "";
    		if(!Utils.isEmpty(str))
    			res = Utils.getDataFromURL(ABC.WEB_URL+"doctorComment/comment2?user_id="+userId+"&var="+str);
    		if(res.contains("saved"))
    			dba.deleteAll(DbAdapter.DOC_REPORT);
    	} catch(Exception ex) {
    		ex.printStackTrace();
    	}
    	dialogP.dismiss();
    }
	public void updateDocRank(int userId) {
    	ProgressDialog dialogP = ProgressDialog.show(ctx, "", 
                "Loading. Please wait...", true);
    	dialogP.show();
    	try {
    		Cursor cr = dba.fetchAll(DbAdapter.DOC_REPORT);
    		String str = "";
    		if(cr != null && cr.getCount() > 0) {
            	cr.moveToFirst();
            	for(int i=0; i<cr.getCount(); i++) {
            		if(i == 0)
            			str = cr.getInt(1) +","+cr.getString(3);
            		else
            			str = str+"|"+cr.getInt(1) +","+cr.getString(3);
            		cr.moveToNext();
            	}
    			cr.close();
            }
    		String res = "";
    		if(!Utils.isEmpty(str))
    			res = Utils.getDataFromURL(ABC.WEB_URL+"userDocRank/rank2?user_id="+userId+"&val="+str);
    		
    	} catch(Exception ex) {
    		ex.printStackTrace();
    	}
    	dialogP.dismiss();
    }
	public void updateSetupPCP(int myPracId, int myCntyId) {
    	ProgressDialog dialogP = ProgressDialog.show(ctx, "", 
                "Loading. Please wait...", true);
    	dialogP.show();
    	try {
    		
    		String jsonDataP = Utils.getDataFromURL(ABC.WEB_URL+"practice/jsonCounty?cnty_id="+myCntyId);
    		parseJSONData(jsonDataP, DbAdapter.PRACTICE);
    		
    		//String jsonData = Utils.getDataFromURL(ABC.WEB_URL+"hospital/jsonTmpl?prac_id="+myPracId);
    		//parseJSONData(jsonData, DbAdapter.HOSPITAL);
    		
    		//String jsonData2 = Utils.getDataFromURL(ABC.WEB_URL+"insurance/jsonTmpl?prac_id="+myPracId);
    		//parseJSONData(jsonData2, DbAdapter.INSURANCE);
    		
    		String jsonData3 = Utils.getDataFromURL(ABC.WEB_URL+"speciality/json?type=1");
    		parseSpecJSONData(jsonData3, DbAdapter.SPECIALTY, "1");
    		
    		String jsonData4 = Utils.getDataFromURL(ABC.WEB_URL+"speciality/json?type=2");
    		parseSpecJSONData(jsonData4, DbAdapter.SPECIALTY, "2");
    		
    		String jsonData5 = Utils.getDataFromURL(ABC.WEB_URL+"speciality/json?type=3");
    		parseSpecJSONData(jsonData5, DbAdapter.SPECIALTY, "3");
    		
    		String jsonData6 = Utils.getDataFromURL(ABC.WEB_URL+"county/jsonTmpl?prac_id="+myPracId);
    		parseJSONData(jsonData6, DbAdapter.COUNTY);
    		
    	} catch(Exception ex) {
    		ex.printStackTrace();
    	}
    	dialogP.dismiss();
    }
    
    public void updateSetupHospitalist(int myHospId) {
    	ProgressDialog dialogP = ProgressDialog.show(ctx, "", 
                "Loading. Please wait...", true);
    	dialogP.show();
    	try {
    		String jsonData = Utils.getDataFromURL(ABC.WEB_URL+"practice/jsonTmpl?hosp_id="+myHospId);
    		System.out.println("SMM::ID="+DbAdapter.PRACTICE);
    		parseJSONData(jsonData, DbAdapter.PRACTICE);
    		
    		String jsonData2 = Utils.getDataFromURL(ABC.WEB_URL+"insurance/jsonTmpl?hosp_id="+myHospId);
    		parseJSONData(jsonData2, DbAdapter.INSURANCE);
    		
    		String jsonData3 = Utils.getDataFromURL(ABC.WEB_URL+"speciality/json?type=1");
    		parseSpecJSONData(jsonData3, DbAdapter.SPECIALTY, "1");
    		
    		String jsonData4 = Utils.getDataFromURL(ABC.WEB_URL+"speciality/json?type=2");
    		parseSpecJSONData(jsonData4, DbAdapter.SPECIALTY, "2");
    		
    		String jsonData5 = Utils.getDataFromURL(ABC.WEB_URL+"speciality/json?type=3");
    		parseSpecJSONData(jsonData5, DbAdapter.SPECIALTY, "3");
    		
    		String jsonData6 = Utils.getDataFromURL(ABC.WEB_URL+"county/jsonTmpl?hosp_id="+myHospId);
    		parseJSONData(jsonData6, DbAdapter.COUNTY);
    		
    	} catch(Exception ex) {
    		ex.printStackTrace();
    	}
    	dialogP.dismiss();
    }

    public void parseJSONData(String jsonData, int tableId) throws Exception {
		//System.out.println("SMM::JSON::["+tableId+"]"+jsonData);
    	JSONTokener jsonTokener = new JSONTokener(jsonData);
    	JSONArray arr = (JSONArray) jsonTokener.nextValue();
    	dba.delete(tableId, 0);
    	for(int i=0; i < arr.length(); i++) { 
    		JSONObject jsonObj = arr.getJSONObject(i);
    		//System.out.println("SMM::JSON::["+tableId+"]==["+DbAdapter.COUNTY+"]");
    		if(tableId == DbAdapter.COUNTY)
    			dba.insert(tableId, new String[]{jsonObj.getString("id"),
						 jsonObj.getString("name"), jsonObj.getString("county_code")} );
    		else
    			dba.insert(tableId, new String[]{jsonObj.getString("id"),
							 jsonObj.getString("name"), ""} );
    		//System.out.println("SMM:CODE::"+jsonObj.getString("county_code"));
    	}
    	//return map;
	}
    
    public void parseSpecJSONData(String jsonData, int tableId, String specType) throws Exception {
		//System.out.println("SMM:DOC-JSON::"+jsonData);
		JSONTokener jsonTokener = new JSONTokener(jsonData);
    	JSONArray arr = (JSONArray) jsonTokener.nextValue();
    	if("1".equals(specType))
    		dba.delete(tableId, 0);
    	for(int i=1; i < arr.length(); i++) { 
    		JSONObject jsonObj = arr.getJSONObject(i);
    		dba.insert(tableId, new String[]{jsonObj.getString("id"),
			   							     jsonObj.getString("name"), "", specType} );
    	}
    	//return map;
	}
	
	
}
