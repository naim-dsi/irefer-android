package com.dsinv.irefer;

import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.List;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Debug;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
//import android.widget.Toast;

public class DbAdapter {

	public static final String PK = "_id";
	
	public static final int NO				= -1;
	
	public static final int USERS	   = 0;
	public static final int PRACTICE   = 1;
	public static final int HOSPITAL   = 2;
	public static final int INSURANCE  = 3;
	public static final int SPECIALTY  = 4;
	public static final int RESOURCE   = 5;
	public static final int DOCTOR     = 6;
	public static final int COUNTY     = 7;
	public static final int DOC_FTS    = 8;
	public static final int DOC_REPORT = 9;
	public static final int STATISTICS = 10;
	public static final int STATE      = 11;
	public static final int DOC_PRACTICE      = 12;
	public static final int DOC_HOSPITAL      = 13;
	public static final int DOC_INSURANCE      = 14;
	public static final int DOC_SPECIALTY      = 15;
	public static final int DOC_PLAN      = 16;
	public static final int DOC_ACO      = 17;
	public static final int PLAN      = 18;
	public static final int ACO      = 19;

	/*NON DB*/
	public static final int OFFICE_HOUR = 100;
	public static final int LANGUAGE = 101;

	public static final String tname[] = {
		"t_users",			
		"t_practice",			
		"t_hospital",
		"t_insurance",
		"t_specialty",
		"t_resource",
		"t_doctor",
		"t_county",
		"t_doc_fts",
		"t_doc_report",
		"t_statistics",
		"t_state",//NEW//  
		"t_doc_practice",
		"t_doc_hospital",
		"t_doc_insurance",
		"t_doc_speciality",
		"t_doc_plan",
		"t_doc_aco",
		"t_plan",
		"t_aco"};
	//5324
	public static final String tcols[][] = {
		/* users	  */	{	PK,	"user_id", "last_name", "first_name", "email", "act_code", "my_prac_id",
			"my_hos_id", "my_county_id", "need_to_sync", "update_setting", "rank_doc", "rank_doc_practice", "doc_id"},
		/* practice   */	{	PK,	"prac_id", "name", "address"},
		/* hospital   */	{	PK,	"hos_id", "name", "address"},
		/* insurance  */    {	PK,	"ins_id", "name", "address"},
		/* specialty  */	{	PK,	"spec_id", "name", "address", "spec_type"},
		/* resource   */	{	PK,	"res_id", "name", "address"},
		/* doctor     */	{	PK,	"doc_id", "last_name", "first_name", "mid_name",  //4
			"degree", "doc_phone", "language", "grade", "gender", "image_url",        //10
			"prac_id", "hosp_id", "spec_id", "insu_id", //14 
			 "zip_code", "county_id", "res_flag", "see_patient",  // 18
            "doc_fax", "npi", "office_hour", "u_rank", "up_rank", "quality", //24
            "cost", "rank_user_number", "avg_rank","prac_ids", "hosp_ids", "spec_ids", "insu_ids",
            "plan_ids", "aco_ids", "prac_names", "hosp_names", "spec_names", "insu_names", "plan_names","aco_names"}, 
		/* county   */	    {	PK,	"county_id", "name", "code", "state_id"},   
		/* doc fts  */	    {	"doc_id", "text"},
		/* doc report  */	{	PK, "doc_id", "user_id", "text", "report_time", "submit_time"},
		/* statistics  */   {	PK, "count_type", "count"},
		/* county   */	    {	PK,	"state_id", "name", "code"}, //NEW//  
		/* doc_practice */	{	PK,	"practice_id", "doc_id" },
		/* doc_hospital */  {	PK,	"hospital_id", "doc_id" },
		/* doc_insurance */ {	PK,	"insurance_id", "doc_id" },
		/* doc_specialty */ {	PK,	"speciality_id", "doc_id" },
		/* doc_plan */  	{	PK,	"plan_id", "doc_id" },
		/* doc_aco */  		{	PK,	"aco_id", "doc_id" },
		/* plan */  		{	PK,	"plan_id", "name" , "insurance_id" },
		/* aco */  			{	PK,	"aco_id", "name" },
		
	};
	
	private static final int tcref[][] = {
		{NO, NO, NO, NO, NO, NO, NO, NO, NO, NO, NO, NO,NO, NO},
		{NO, NO, NO, NO},
		{NO, NO, NO, NO},
		{NO, NO, NO, NO},
		{NO, NO, NO, NO, NO},
		{NO, NO, NO, NO},
		{NO, NO, NO, NO, NO, NO, NO, NO, NO, NO, NO, NO, NO, NO, NO, NO, NO, NO, NO, NO, NO, NO, NO, NO, NO, NO, NO, NO, NO, NO, NO, NO, NO, NO, NO, NO, NO, NO, NO, NO},
		{NO, NO, NO, NO, NO},
		{NO, NO},
		{NO, NO, NO, NO, NO, NO},
		{NO, NO, NO},
		{NO, NO, NO, NO},//NEW//  
		{NO, NO, NO},
		{NO, NO, NO},
		{NO, NO, NO},
		{NO, NO, NO},
		{NO, NO, NO},
		{NO, NO, NO},
		{NO, NO, NO, NO},
		{NO, NO, NO}
	};
	
	private static final String ttypes[] = {
		".ittttiiiiiiii", ".itt", ".itt", ".itt", ".itti", ".itt", 
		".ittttttiitiiiitiiitttiiiiiftttttttttttt", ".itti", "tt", "iiittt", "iii", ".itt",//NEW//  
		".ii", ".ii", ".ii", ".ii", ".ii", ".ii", ".iti", ".it"
	};

	private static final String tuniques[] = {
		"..YYY.........", "....", "....", "....", ".....", "....", 
		"..............................................", ".....", "..", "......", "...", "....",//NEW//
		"...","...","...","...","...","...","....","..."
	};
	
	private static final String tnulls[] = {
		"..............", ".YY.", ".YY.", ".YY.", ".YY..", ".YY.", 
		"..............................................", ".YY..", "..", "......", "...", "....",//NEW// 
		"YYY","YYY","YYY","YYY","YYY","YYY","YY.Y","YY."
	};
/*	
	private static final int tcvar[][] = {
		{},			{6},			{1},
		{},			{},				{1,2},
		{1},		{1,2,5,6},
		{},			{}
	};
	
	private static final int tcref[][] = {
		{},			{TIMEZONE},		{AIRPORT},
		{},			{},				{TIMEZONE, PERSON},
		{TRIP},		{TRIP, TRIPDAY, AIRPORT, AIRPORT},
		{},			{}
	};*/
	
//	flightleg = [flight].[src_airport].[dst_airport].[Y].[M].[D].[h].[m] >>>> so unique!!

	public static final String KEY_PASSWORD = "password";
	
	private static final String DEFAULT_PASSWORD = "123";
	
	private static final String TAG					= "DbAdapter";
    private static final String DATABASE_NAME 		= "irefer_db";
    private static final int	DATABASE_VERSION	= 1;
    
    private DbHelper 		dbHelper;
    private SQLiteDatabase	db;
    private SQLiteDatabase	dbr;

    private final Activity	act;
    private final Context 	ctx;
	private final TextView 	debug;

    public DbAdapter(Context ctx) {
        this.ctx = ctx;
        this.act = (Activity)ctx;
        this.debug = null;
    }
    public DbAdapter(Context ctx, TextView debug) {
        this.ctx = ctx;
        this.act = (Activity)ctx;
        this.debug = debug;
        debug.setVisibility(View.VISIBLE);
        debug.setTextSize(8F);
    }
    
    public DbAdapter open() throws SQLException {
    	if(debug!=null)	dbHelper = new DbHelper(ctx, debug);
    	else			dbHelper = new DbHelper(ctx);
        
    	//dbHelper.close();
    	db = dbHelper.getWritableDatabase();
        dbr = dbHelper.getReadableDatabase();
        	
//    	db.execSQL("DROP TABLE IF EXISTS " + tname[TRIPCREW]);
        dbHelper.onCreate(db);
        dbHelper.onCreate(dbr);
        
//        dbHelper.dropTables(db);
        
        /////////////////
//        for(int i = 0;i < tname.length; i++)
//        	db.execSQL("DROP TABLE IF EXISTS " + tname[i]);
        /////////////////////////////////
        
        return this;
    }
    
    public void close() {
        dbHelper.close();
    }

    private String matchRow(long rowId){
    	if(rowId < 1)
    		return null;
    	return PK + " = " + rowId;
    }
    
    /******************
    * POPULATED VIEWS * 
    ******************/
    
    public class StringAndLong extends Pair<String, Long> implements Comparable<StringAndLong>{
    	public StringAndLong(String first, Long second) {
			super(first, second);
		}
		public int compareTo(StringAndLong that) {
			int comparisionFirst = this.first.compareTo(that.first); 
			if(comparisionFirst==0)
				return this.second.compareTo(that.second);
			return comparisionFirst;
		}
    }
    
    public String getDataRecursive(String format, int i, Cursor cur, int tableId){
    	StringBuilder token = new StringBuilder("");
    	while(true){
    		if(format.charAt(i)=='{'){
    			int col = cur.getColumnIndexOrThrow(new String(token));
    			int nextTableId = tcref[tableId][col];
    			Cursor nextCur = this.fetch(nextTableId, cur.getLong(col));
    	        act.startManagingCursor(cur);
    			return getDataRecursive(format, i+1, nextCur, nextTableId);
    		}
    		if(format.charAt(i)=='}'){
    			int col = cur.getColumnIndexOrThrow(new String(token));
    			return cur.getString(col);
    		}
    		
    		token.append(format.charAt(i));
    		i++;
    	}
    }
    
    //cur must be before calling managed
    public String getFormattedDataSingleRow(int tableId, String format, Cursor cur){
    	StringBuilder temp = new StringBuilder("");
    	for(int j=0;j<format.length();j++){
    		if(format.charAt(j)=='{'){
    			String s = getDataRecursive(format, j+1, cur, tableId);
    			temp.append(s);
    			int k = 1;
    			while(k>0){
    				j++;
    				if(format.charAt(j)=='{')		k++;
    				else if(format.charAt(j)=='}')	k--;
    			}
    		}
    		else
    			temp.append(format.charAt(j));
    	}
    	return new String(temp);
    }
        
    /**********************************************
    * CREATE CONTENT VALUES FOR INSERT AND UPDATE * 
    **********************************************/
    private String toSQLDate(String date) throws RuntimeException{
    	StringTokenizer	st = null;
    	int[]			order = null;
    	
    	//yyyy-mm-dd
    	if(date.charAt(4)=='-'){
    		st = new StringTokenizer(date, "-");
    		order = new int[]{0,1,2};
    	}
    	//yyyy.mm.dd
    	else if(date.charAt(4)=='.'){
    		st = new StringTokenizer(date, ".");
    		order = new int[]{0,1,2};
    	}
    	//mm-dd-yyyy
    	else if(date.charAt(2)=='-'){
    		st = new StringTokenizer(date, "-");
    		order = new int[]{2,0,1};
    	}
    	//mm.dd.yyyy
    	else if(date.charAt(2)=='.'){
    		st = new StringTokenizer(date, ".");
    		order = new int[]{2,0,1};
    	}
    	String[] tok = new String[3];
    	tok[0] = st.nextToken();
    	tok[1] = st.nextToken();
    	tok[2] = st.nextToken();
    	return tok[order[0]] + "-" + tok[order[1]] + "-" + tok[order[2]];
    }

    private String toSQLTime(String time) throws RuntimeException{
    	if(time.length()==4){
    		return	time.charAt(0) + time.charAt(1) + ":" +   
    				time.charAt(2) + time.charAt(3) + ":00";
    	}
    	return time + ":00";
    }
    
    private String toSQLTimeStamp(String date,String time) throws RuntimeException{
    	return toSQLDate(date) + " " + toSQLTime(time);   
    }
    
    private ContentValues formatInstance(int tableId, String[] data) throws RuntimeException{
        ContentValues cv = new ContentValues();
        switch(tableId){
        
        	case USERS:
                cv.put(tcols[USERS][1], new Integer(data[0]));
                cv.put(tcols[USERS][2], data[1]);
                cv.put(tcols[USERS][3], data[2]);
                cv.put(tcols[USERS][4], data[3]);
                cv.put(tcols[USERS][5], data[4]);
                cv.put(tcols[USERS][6], new Integer(data[5]));
                cv.put(tcols[USERS][7], new Integer(data[6]));
                cv.put(tcols[USERS][8], new Integer(data[7]));
                cv.put(tcols[USERS][9], new Integer(data[8]));
                cv.put(tcols[USERS][10], new Integer(data[9]));
                cv.put(tcols[USERS][11], new Integer(data[10]));
                cv.put(tcols[USERS][12], new Integer(data[11]));
                cv.put(tcols[USERS][13], new Integer(data[12]));
                return cv;
                
        	case PRACTICE:
            	cv.put(tcols[PRACTICE][1], new Integer(data[0]));
            	cv.put(tcols[PRACTICE][2], data[1]);
            	cv.put(tcols[PRACTICE][3], data[2]);
            	return cv;
            
        	case HOSPITAL:
            	cv.put(tcols[HOSPITAL][1], new Integer(data[0]));
            	cv.put(tcols[HOSPITAL][2], data[1]);
            	cv.put(tcols[HOSPITAL][3], data[2]);
            	return cv;
        	
        	case INSURANCE:
            	cv.put(tcols[INSURANCE][1], new Integer(data[0]));
            	cv.put(tcols[INSURANCE][2], data[1]);
            	cv.put(tcols[INSURANCE][3], data[2]);
        		return cv;
        	
        	case SPECIALTY:
            	cv.put(tcols[SPECIALTY][1], new Integer(data[0]));
            	cv.put(tcols[SPECIALTY][2], data[1]);
            	cv.put(tcols[SPECIALTY][3], data[2]);
            	cv.put(tcols[SPECIALTY][4], new Integer(data[3]));
            	return cv;
        		
        	case RESOURCE:
            	cv.put(tcols[RESOURCE][1], new Integer(data[0]));
            	cv.put(tcols[RESOURCE][2], data[1]);
            	cv.put(tcols[RESOURCE][3], data[2]);
        		return cv;
        	case DOCTOR:
        		//System.out.println("SMM::TCOL-LENGTH="+tcols[DOCTOR].length);
        		cv.put(tcols[DOCTOR][1], new Integer(data[0]));
                cv.put(tcols[DOCTOR][2], data[1]);
                cv.put(tcols[DOCTOR][3], data[2]);
                cv.put(tcols[DOCTOR][4], data[3]);
                cv.put(tcols[DOCTOR][5], data[4]);
                cv.put(tcols[DOCTOR][6], data[5]);
                cv.put(tcols[DOCTOR][7], data[6]);
                cv.put(tcols[DOCTOR][8], new Integer(data[7]));
                cv.put(tcols[DOCTOR][9], new Integer(data[8]));
                cv.put(tcols[DOCTOR][10], data[9]);
                cv.put(tcols[DOCTOR][11], new Integer(data[10]));
                cv.put(tcols[DOCTOR][12], new Integer(data[11]));
                cv.put(tcols[DOCTOR][13], new Integer(data[12]));
                cv.put(tcols[DOCTOR][14], new Integer(data[13]));
                cv.put(tcols[DOCTOR][15], data[14]);
                cv.put(tcols[DOCTOR][16], new Integer(data[15]));
                cv.put(tcols[DOCTOR][17], new Integer(data[16]));
                cv.put(tcols[DOCTOR][18], new Integer(data[17]));
                cv.put(tcols[DOCTOR][19], data[18]);
                cv.put(tcols[DOCTOR][20], data[19]);
                cv.put(tcols[DOCTOR][21], data[20]);
                cv.put(tcols[DOCTOR][22], new Integer(data[21]));
                cv.put(tcols[DOCTOR][23], new Integer(data[22]));
                cv.put(tcols[DOCTOR][24], new Integer(data[23]));
                cv.put(tcols[DOCTOR][25], new Integer(data[24]));
                cv.put(tcols[DOCTOR][26], new Integer(data[25]));
                cv.put(tcols[DOCTOR][27], new Double(data[26]));
            	cv.put(tcols[DOCTOR][28], data[27]);
            	cv.put(tcols[DOCTOR][29], data[28]);
            	cv.put(tcols[DOCTOR][30], data[29]);
            	cv.put(tcols[DOCTOR][31], data[30]);
            	cv.put(tcols[DOCTOR][32], data[31]);
            	cv.put(tcols[DOCTOR][33], data[32]);
            	cv.put(tcols[DOCTOR][34], data[33]);
            	cv.put(tcols[DOCTOR][35], data[34]);
            	cv.put(tcols[DOCTOR][36], data[35]);
            	cv.put(tcols[DOCTOR][37], data[36]);
            	cv.put(tcols[DOCTOR][38], data[37]);
            	cv.put(tcols[DOCTOR][39], data[38]);
            	return cv;	     
        	case COUNTY:
            	cv.put(tcols[COUNTY][1], new Integer(data[0]));
            	cv.put(tcols[COUNTY][2], data[1]);
            	cv.put(tcols[COUNTY][3], data[2]);
            	cv.put(tcols[COUNTY][4], new Integer(data[3]));
            	return cv;
        	case STATE:
            	cv.put(tcols[STATE][1], new Integer(data[0]));
            	cv.put(tcols[STATE][2], data[1]);
            	cv.put(tcols[STATE][3], data[2]);
            	return cv;
        	case DOC_FTS:
            	cv.put(tcols[DOC_FTS][0], data[0]);
            	cv.put(tcols[DOC_FTS][1], data[1]);
            	return cv;
        	case STATISTICS:
            	cv.put(tcols[STATISTICS][1], new Integer(data[0]));
            	cv.put(tcols[STATISTICS][2], new Integer(data[1]));
            	return cv;
        	case DOC_REPORT:
            	cv.put(tcols[DOC_REPORT][1], new Integer(data[0]));
            	cv.put(tcols[DOC_REPORT][2], new Integer(data[1]));
            	cv.put(tcols[DOC_REPORT][3], data[2]);
            	cv.put(tcols[DOC_REPORT][4], data[3]);
            	cv.put(tcols[DOC_REPORT][5], data[4]);
            	return cv;
        	case PLAN:
            	cv.put(tcols[PLAN][1], new Integer(data[0]));
            	cv.put(tcols[PLAN][2], data[1]);
            	cv.put(tcols[PLAN][3], new Integer(data[2]));
            	return cv;
        	case ACO:
            	cv.put(tcols[ACO][1], new Integer(data[0]));
            	cv.put(tcols[ACO][2], data[1]);
            	return cv;	
        	case DOC_PRACTICE:
            	cv.put(tcols[DOC_PRACTICE][1], new Integer(data[0]));
            	cv.put(tcols[DOC_PRACTICE][2], new Integer(data[1]));
            	return cv;	
        	case DOC_HOSPITAL:
        		cv.put(tcols[DOC_HOSPITAL][1], new Integer(data[0]));
            	cv.put(tcols[DOC_HOSPITAL][2], new Integer(data[1]));
            	return cv;	
        	case DOC_INSURANCE:
            	cv.put(tcols[DOC_INSURANCE][1], new Integer(data[0]));
            	cv.put(tcols[DOC_INSURANCE][2], new Integer(data[1]));
            	return cv;	
        	case DOC_SPECIALTY:
            	cv.put(tcols[DOC_SPECIALTY][1], new Integer(data[0]));
            	cv.put(tcols[DOC_SPECIALTY][2], new Integer(data[1]));
            	return cv;	
        	case DOC_PLAN:
            	cv.put(tcols[DOC_PLAN][1], new Integer(data[0]));
            	cv.put(tcols[DOC_PLAN][2], new Integer(data[1]));
            	return cv;	
        	case DOC_ACO:
            	cv.put(tcols[DOC_ACO][1], new Integer(data[0]));
            	cv.put(tcols[DOC_ACO][2], new Integer(data[1]));
            	return cv;	
            	
        }
        return null;
    }	
	
    /*************************
    * INSERT, UPDATE, DELETE * 
    *************************/
    private String getNameById(int tableId, int rowId) {
    	Cursor cr = fetchByNetId(tableId, rowId);
    	String name = "";
    	if(cr != null) {
    		cr.moveToFirst();
    		if(cr.getCount() > 0) {
    			name = cr.getString(2);
    			//if(!Utils.isEmpty(cr.getString(3)))
    			//	name += ","+cr.getString(3);
    		}
    		cr.close();
    	}
		return name;
	}
	
    public void populateFTS(long docId, String[] data){
    	String text = "";
    	try {
    		if(!Utils.isEmpty(data[10]))
    			text += ("".equals(text) ? "" : ",") + getNameById(PRACTICE,  Integer.parseInt(data[10]));
    		if(!Utils.isEmpty(data[11]))
    			text += ("".equals(text) ? "" : ",") + getNameById(HOSPITAL,  Integer.parseInt(data[11]));
    		if(!Utils.isEmpty(data[12]))
    			text += ("".equals(text) ? "" : ",") + getNameById(SPECIALTY, Integer.parseInt(data[12]));
    		if(!Utils.isEmpty(data[13]))
    			text += ("".equals(text) ? "" : ",") + getNameById(INSURANCE, Integer.parseInt(data[13]));
    		if(!Utils.isEmpty(data[16]))
    			text += ("".equals(text) ? "" : ",") + getNameById(COUNTY,    Integer.parseInt(data[16]));
    	} catch(Exception ex){
    		ex.printStackTrace();
    		//continue
    	}
    	//System.out.println("SMM::FTS::SPEC-ID::"+data[12]+"::SPEC-CODE::"+getNameById(SPECIALTY, Integer.parseInt(data[12])));
    	text += ("".equals(text) ? "" : ",") + data[2];
    	text += ("".equals(text) ? "" : ",") + data[3];
    	text += ("".equals(text) ? "" : ",") + data[4];
    	text += ("".equals(text) ? "" : ",") + data[5];
    	text += ("".equals(text) ? "" : ",") + data[6];
    	text += ("".equals(text) ? "" : ",") + data[7];
    	text += ("".equals(text) ? "" : ",") + data[15];  //zip
    	text += ("".equals(text) ? "" : ",") + data[19];  //fax
    	text += ("".equals(text) ? "" : ",") + data[20];  //npi
    	
    	//System.out.println("SMM::FTS-DATA::"+text);
        //db.execSQL("INSERT INTO "+tname[DOC_FTS]+"(doc_id, text) values( "+docId+", '"+text+"')");
        db.insert(tname[DOC_FTS], null, formatInstance(DOC_FTS, new String[]{ docId+"", text}));
        //System.out.println("DOC FTS updated for docId="+docId);
    }
        
    public long insertDoctor(String ftsText, String[] data) throws RuntimeException{
    	long id = db.insert(tname[DOCTOR], null, formatInstance(DOCTOR, data));
    	db.insert(tname[DOC_FTS], null, formatInstance(DOC_FTS, new String[]{ id+"", ftsText}));
    	return id;
    }
    
    public void insertAllDoctorFTS(){
    	db.beginTransaction();
    	Cursor cr = dbr.query(tname[DOCTOR], tcols[DOCTOR], null, null, null, null, "u_rank, grade, first_name");
    	if(cr != null && cr.getCount() > 0) {
    		cr.moveToFirst();
    		for(int i=0, j=cr.getCount(); i<j; i++) {
    			String ftsText = cr.getString(2).toLowerCase()+","+cr.getString(3).toLowerCase()+","+cr.getString(4).toLowerCase();
    			try {
    	    		if(!Utils.isEmpty(cr.getInt(11)+""))
    	    			ftsText += ("".equals(ftsText) ? "" : ",") + getNameById(PRACTICE,  cr.getInt(11));
    	    		if(!Utils.isEmpty(cr.getInt(12)+""))
    	    			ftsText += ("".equals(ftsText) ? "" : ",") + getNameById(HOSPITAL,  cr.getInt(12));
    	    		if(!Utils.isEmpty(cr.getInt(13)+""))
    	    			ftsText += ("".equals(ftsText) ? "" : ",") + getNameById(SPECIALTY, cr.getInt(13));
    	    		if(!Utils.isEmpty(cr.getInt(14)+""))
    	    			ftsText += ("".equals(ftsText) ? "" : ",") + getNameById(INSURANCE, cr.getInt(14));
    	    		if(!Utils.isEmpty(cr.getInt(16)+""))
    	    			ftsText += ("".equals(ftsText) ? "" : ",") + getNameById(COUNTY,    cr.getInt(16));
    	    	} catch(Exception ex){
    	    		ex.printStackTrace();
    	    		//continue
    	    	}
    			db.insert(tname[DOC_FTS], null, formatInstance(DOC_FTS, new String[]{ cr.getInt(1)+"", ftsText}));
    			cr.moveToNext();
    		}
    	}
    	db.setTransactionSuccessful();
    	db.endTransaction();
    }
    
    public void insertDoctor(List ftsArr, List dataArr) throws RuntimeException{
    	db.beginTransaction();
    	try{
    		int ix=0, jx=0, tblQueryGen=0;
    		StringBuilder docSQL = new StringBuilder("") ;
    		StringBuilder hospitalSQL = new StringBuilder("") ;
    		StringBuilder specialitySQL = new StringBuilder("") ;
    		StringBuilder insuranceSQL = new StringBuilder("") ;
    		StringBuilder practiceSQL = new StringBuilder("") ;
    		StringBuilder planSQL = new StringBuilder("") ;
    		StringBuilder acoSQL = new StringBuilder("") ;
    		JSONTokener jsonTokener;
	    	JSONArray arr;
	    	JSONObject jsonObj;
	    	
	    	for(int i=0, j=dataArr.size(); i<j; i++) {
	    		//String ftsText = (String)ftsArr.get(i);
	    		String[] data = (String[])dataArr.get(i);
	    		long id = db.insert(tname[DOCTOR], null, formatInstance(DOCTOR, data));
//	    		if(i==0)
//    			{
//	    			docSQL.append("INSERT INTO '"+tname[DOCTOR]+"' ") ;
//	    			docSQL.append(" SELECT ");
//    				docSQL.append("NULL");
//        			docSQL.append(" AS ");
//        			docSQL.append("'"+tcols[DOCTOR][tblQueryGen]+"' ");
//        			
//	    			for(tblQueryGen = 1 ; tblQueryGen<tcols[DOCTOR].length ; tblQueryGen++)
//	    			{
//	    				docSQL.append(", ");
//	    				
//	    				if(dbHelper.getType(ttypes[DOCTOR].charAt(tblQueryGen))=="INTEGER")
//	    				{
//	    					docSQL.append(new Integer(data[tblQueryGen-1]));
//	    				}
//	    				else if(dbHelper.getType(ttypes[DOCTOR].charAt(tblQueryGen))=="TEXT")
//	    				{
//	    					docSQL.append("'"+data[tblQueryGen-1]+"' ");
//	    				}
//	    				else if(dbHelper.getType(ttypes[DOCTOR].charAt(tblQueryGen))=="DATE")
//	    				{
//	    					docSQL.append("'"+data[tblQueryGen-1]+"' ");
//	    				}
//	    				else if(dbHelper.getType(ttypes[DOCTOR].charAt(tblQueryGen))=="REAL")
//	    				{
//	    					docSQL.append(new Double(data[tblQueryGen-1]));
//	    				}
//	    				else
//	    				{
//	    					docSQL.append("NULL");
//	    				}
//	    				docSQL.append(" AS ");
//	        			docSQL.append("'"+tcols[DOCTOR][tblQueryGen]+"' ");
//	    	        }
//	    		}
//	    		else
//	    		{
//	    			docSQL.append(" UNION ");
//    				docSQL.append(" SELECT ");
//    				docSQL.append("NULL");
//    				for(tblQueryGen = 1 ; tblQueryGen<tcols[DOCTOR].length ; tblQueryGen++)
//	    			{
//    					docSQL.append(", ");
//    					if(dbHelper.getType(ttypes[DOCTOR].charAt(tblQueryGen))=="INTEGER")
//	    				{
//	    					docSQL.append(new Integer(data[tblQueryGen-1]));
//	    				}
//	    				else if(dbHelper.getType(ttypes[DOCTOR].charAt(tblQueryGen))=="TEXT")
//	    				{
//	    					docSQL.append("'"+data[tblQueryGen-1]+"' ");
//	    				}
//	    				else if(dbHelper.getType(ttypes[DOCTOR].charAt(tblQueryGen))=="DATE")
//	    				{
//	    					docSQL.append("'"+data[tblQueryGen-1]+"' ");
//	    				}
//	    				else if(dbHelper.getType(ttypes[DOCTOR].charAt(tblQueryGen))=="REAL")
//	    				{
//	    					docSQL.append(new Double(data[tblQueryGen-1]));
//	    				}
//	    				else
//	    				{
//	    					docSQL.append("NULL");
//	    				}
//	    			}
//	    		}
	    		
//	    		if(!data[27].equals("0")){
//	    			//hospital
//	    			try{
//		    			jsonTokener = new JSONTokener(data[27]);
//		            	arr = (JSONArray) jsonTokener.nextValue();
//		            	ix=0;
//		            	jx=arr.length();
//		            	if(jx>ix){
//		            		while(ix < jx) { 
//			            		jsonObj = arr.getJSONObject(ix);
//			            		ix++;
//			            		if(!jsonObj.getString("id").equals("")||!jsonObj.getString("id").equals("0"))
//			            		{	
//				            		if(hospitalSQL.toString().equals(""))
//			            			{
//				            			hospitalSQL.append("INSERT INTO '"+tname[DOC_HOSPITAL]+"' ") ;
//				            			hospitalSQL.append(" SELECT ");
//				            			hospitalSQL.append("NULL");
//				            			hospitalSQL.append(" AS ");
//				            			hospitalSQL.append("'"+tcols[DOC_HOSPITAL][0]+"' ");
//				            			hospitalSQL.append(", ");
//				            			hospitalSQL.append(jsonObj.getString("id"));
//				            			hospitalSQL.append(" AS ");
//				            			hospitalSQL.append("'"+tcols[DOC_HOSPITAL][1]+"' ");
//				            			hospitalSQL.append(", ");
//				            			hospitalSQL.append(new Integer(data[0]));
//				            			hospitalSQL.append(" AS ");
//				            			hospitalSQL.append("'"+tcols[DOC_HOSPITAL][2]+"' ");
//			            			}
//				            		else
//			            			{
//				            			hospitalSQL.append(" UNION ");
//			            				hospitalSQL.append(" SELECT ");
//			            				hospitalSQL.append("NULL");
//			            				hospitalSQL.append(", ");
//			            				hospitalSQL.append(jsonObj.getString("id"));
//			            				hospitalSQL.append(", ");
//			            				hospitalSQL.append(new Integer(data[0]));
//				            			
//			            			}
//			            		}
//			            	}
//			        		//http://stackoverflow.com/questions/15613377/insert-multiple-rows-in-sqlite-error-error-code-1
//		            	}
//	    			}
//	    			catch(Exception ex)
//	    			{
//	    				ex.printStackTrace();
//	    			}
//	            	
//	    		}
//	    		
//				if(!data[28].equals("0")){
//				    //speciality
//				    try{
//						jsonTokener = new JSONTokener(data[28]);
//		            	arr = (JSONArray) jsonTokener.nextValue();
//		            	ix=0;
//		            	jx=arr.length();
//		            	if(jx>ix){
//		            		while(ix < jx) { 
//			            		jsonObj = arr.getJSONObject(ix);
//			            		ix++;
//			            		if(!jsonObj.getString("id").equals("")||!jsonObj.getString("id").equals("0"))
//			            		{
//			            			if(specialitySQL.toString().equals(""))
//			            			{
//			            				specialitySQL.append("INSERT INTO '"+tname[DOC_SPECIALTY]+"' ") ;
//			            				specialitySQL.append(" SELECT ");
//			            				specialitySQL.append("NULL");
//			            				specialitySQL.append(" AS ");
//			            				specialitySQL.append("'"+tcols[DOC_SPECIALTY][0]+"' ");
//			            				specialitySQL.append(", ");
//			            				specialitySQL.append(jsonObj.getString("id"));
//			            				specialitySQL.append(" AS ");
//			            				specialitySQL.append("'"+tcols[DOC_SPECIALTY][1]+"' ");
//			            				specialitySQL.append(", ");
//			            				specialitySQL.append(new Integer(data[0]));
//			            				specialitySQL.append(" AS ");
//			            				specialitySQL.append("'"+tcols[DOC_SPECIALTY][2]+"' ");
//			            			}
//				            		else
//			            			{
//				            			specialitySQL.append(" UNION ");
//				            			specialitySQL.append(" SELECT ");
//				            			specialitySQL.append("NULL");
//				            			specialitySQL.append(", ");
//				            			specialitySQL.append(jsonObj.getString("id"));
//				            			specialitySQL.append(", ");
//				            			specialitySQL.append(new Integer(data[0]));
//				            			
//			            			}
//			            		}
//			            	}
//			        		
//		            	}
//		            	
//	            	}
//	    			catch(Exception ex)
//	    			{
//	    				ex.printStackTrace();
//	    			}
//				}
//				
//				if(!data[29].equals("0")){
//					//insurance
//					try{
//						jsonTokener = new JSONTokener(data[29]);
//		            	arr = (JSONArray) jsonTokener.nextValue();
//		            	ix=0;
//		            	jx=arr.length();
//		            	if(jx>ix){
//		            		while(ix < jx) { 
//			            		jsonObj = arr.getJSONObject(ix);
//			            		ix++;
//			            		if(!jsonObj.getString("id").equals("")||!jsonObj.getString("id").equals("0"))
//			            		{
//			            			if(insuranceSQL.toString().equals(""))
//			            			{
//				            			insuranceSQL.append("INSERT INTO '"+tname[DOC_INSURANCE]+"' ") ;
//			            				insuranceSQL.append(" SELECT ");
//			            				insuranceSQL.append("NULL");
//				            			insuranceSQL.append(" AS ");
//				            			insuranceSQL.append("'"+tcols[DOC_INSURANCE][0]+"' ");
//				            			insuranceSQL.append(", ");
//				            			insuranceSQL.append(jsonObj.getString("id"));
//				            			insuranceSQL.append(" AS ");
//				            			insuranceSQL.append("'"+tcols[DOC_INSURANCE][1]+"' ");
//				            			insuranceSQL.append(", ");
//				            			insuranceSQL.append(new Integer(data[0]));
//				            			insuranceSQL.append(" AS ");
//				            			insuranceSQL.append("'"+tcols[DOC_INSURANCE][2]+"' ");
//			            			}
//				            		else
//			            			{
//			            				insuranceSQL.append(" UNION ");
//			            				insuranceSQL.append(" SELECT ");
//			            				insuranceSQL.append("NULL");
//				            			insuranceSQL.append(", ");
//				            			insuranceSQL.append(jsonObj.getString("id"));
//				            			insuranceSQL.append(", ");
//				            			insuranceSQL.append(new Integer(data[0]));
//				            			
//			            			}
//			            		}
//			            	}
//			        		
//		            	}
//		            	
//	            	}
//	    			catch(Exception ex)
//	    			{
//	    				ex.printStackTrace();
//	    			}
//				}
//				
//				if(!data[30].equals("0")){
//					//practice
//					try{
//						jsonTokener = new JSONTokener(data[30]);
//		            	arr = (JSONArray) jsonTokener.nextValue();
//		            	ix=0;
//		            	jx=arr.length();
//		            	if(jx>ix){
//		            		while(ix < jx) { 
//			            		jsonObj = arr.getJSONObject(ix);
//			            		ix++;
//			            		if(!jsonObj.getString("id").equals("")||!jsonObj.getString("id").equals("0"))
//			            		{
//			            			if(practiceSQL.toString().equals(""))
//			            			{
//				            			practiceSQL.append("INSERT INTO '"+tname[DOC_PRACTICE]+"' ") ;
//			            				practiceSQL.append(" SELECT ");
//			            				practiceSQL.append("NULL");
//				            			practiceSQL.append(" AS ");
//				            			practiceSQL.append("'"+tcols[DOC_PRACTICE][0]+"' ");
//				            			practiceSQL.append(", ");
//				            			practiceSQL.append(jsonObj.getString("id"));
//				            			practiceSQL.append(" AS ");
//				            			practiceSQL.append("'"+tcols[DOC_PRACTICE][1]+"' ");
//				            			practiceSQL.append(", ");
//				            			practiceSQL.append(new Integer(data[0]));
//				            			practiceSQL.append(" AS ");
//				            			practiceSQL.append("'"+tcols[DOC_PRACTICE][2]+"' ");
//			            			}
//				            		else
//			            			{
//			            				practiceSQL.append(" UNION ");
//			            				practiceSQL.append(" SELECT ");
//			            				practiceSQL.append("NULL");
//				            			practiceSQL.append(", ");
//				            			practiceSQL.append(jsonObj.getString("id"));
//				            			practiceSQL.append(", ");
//				            			practiceSQL.append(new Integer(data[0]));
//				            			
//			            			}
//			            		}
//			            	}
//			            	
//		            	}
//		            	
//	            	}
//	    			catch(Exception ex)
//	    			{
//	    				ex.printStackTrace();
//	    			}
//				}
//				
//				if(!data[31].equals("0")){
//					//plan
//					try{
//						jsonTokener = new JSONTokener(data[31]);
//		            	arr = (JSONArray) jsonTokener.nextValue();
//		            	ix=0;
//		            	jx=arr.length();
//		            	if(jx>ix){
//		            		while(ix < jx) { 
//			            		jsonObj = arr.getJSONObject(ix);
//			            		ix++;
//			            		if(!jsonObj.getString("id").equals("")||!jsonObj.getString("id").equals("0"))
//			            		{
//			            			if(planSQL.toString().equals(""))
//			            			{
//				            			planSQL.append("INSERT INTO '"+tname[DOC_PLAN]+"' ") ;
//			            				planSQL.append(" SELECT ");
//			            				planSQL.append("NULL");
//				            			planSQL.append(" AS ");
//				            			planSQL.append("'"+tcols[DOC_PLAN][0]+"' ");
//				            			planSQL.append(", ");
//				            			planSQL.append(jsonObj.getString("id"));
//				            			planSQL.append(" AS ");
//				            			planSQL.append("'"+tcols[DOC_PLAN][1]+"' ");
//				            			planSQL.append(", ");
//				            			planSQL.append(new Integer(data[0]));
//				            			planSQL.append(" AS ");
//				            			planSQL.append("'"+tcols[DOC_PLAN][2]+"' ");
//			            			}
//				            		else
//			            			{
//			            				planSQL.append(" UNION ");
//			            				planSQL.append(" SELECT ");
//			            				planSQL.append("NULL");
//				            			planSQL.append(", ");
//				            			planSQL.append(jsonObj.getString("id"));
//				            			planSQL.append(", ");
//				            			planSQL.append(new Integer(data[0]));
//				            			
//			            			}
//			            		}
//			            	}
//			            	
//		            	}
//		            	
//	            	}
//	    			catch(Exception ex)
//	    			{
//	    				ex.printStackTrace();
//	    			}
//				}
//				
//				if(!data[32].equals("0")){
//					//aco
//					try{
//						jsonTokener = new JSONTokener(data[32]);
//		            	arr = (JSONArray) jsonTokener.nextValue();
//		            	ix=0;
//		            	jx=arr.length();
//		            	if(jx>ix){
//		            		while(ix < jx) { 
//			            		jsonObj = arr.getJSONObject(ix);
//			            		ix++;
//			            		if(!jsonObj.getString("id").equals("")||!jsonObj.getString("id").equals("0"))
//			            		{
//			            			if(acoSQL.toString().equals(""))
//			            			{
//				            			acoSQL.append("INSERT INTO '"+tname[DOC_ACO]+"' ") ;
//			            				acoSQL.append(" SELECT ");
//			            				acoSQL.append("NULL");
//				            			acoSQL.append(" AS ");
//				            			acoSQL.append("'"+tcols[DOC_ACO][0]+"' ");
//				            			acoSQL.append(", ");
//				            			acoSQL.append(jsonObj.getString("id"));
//				            			acoSQL.append(" AS ");
//				            			acoSQL.append("'"+tcols[DOC_ACO][1]+"' ");
//				            			acoSQL.append(", ");
//				            			acoSQL.append(new Integer(data[0]));
//				            			acoSQL.append(" AS ");
//				            			acoSQL.append("'"+tcols[DOC_ACO][2]+"' ");
//			            			}
//				            		else
//			            			{
//			            				acoSQL.append(" UNION ");
//			            				acoSQL.append(" SELECT ");
//			            				acoSQL.append("NULL");
//				            			acoSQL.append(", ");
//				            			acoSQL.append(jsonObj.getString("id"));
//				            			acoSQL.append(", ");
//				            			acoSQL.append(new Integer(data[0]));
//				            			
//			            			}
//			            		}
//			            	}
//			        		
//		            	}
//		            	
//	            	}
//	    			catch(Exception ex)
//	    			{
//	    				ex.printStackTrace();
//	    			}
//				}
				
	    		//db.insert(tname[DOC_FTS], null, formatInstance(DOC_FTS, new String[]{ id+"", ftsText}));
	    	}
//	    	db.beginTransaction();
//	    	try{
//		    	docSQL.append(" ; ");
//		    	db.execSQL(docSQL.toString());
//		    	if(!hospitalSQL.toString().equals(""))
//		    	{
//		    		hospitalSQL.append(" ; ");
//		    		db.execSQL(hospitalSQL.toString());
//		    	}
//		    	if(!practiceSQL.toString().equals(""))
//		    	{
//		    		practiceSQL.append(" ; ");
//		    		db.execSQL(practiceSQL.toString());
//		    	}
//		    	if(!specialitySQL.toString().equals(""))
//		    	{
//		    		specialitySQL.append(" ; ");
//		    		db.execSQL(specialitySQL.toString());
//		    	}
//		    	if(!insuranceSQL.toString().equals(""))
//		    	{
//		    		insuranceSQL.append(" ; ");
//		    		db.execSQL(insuranceSQL.toString());
//		    	}
//		    	if(!planSQL.toString().equals(""))
//		    	{
//		    		planSQL.append(" ; ");
//		    		db.execSQL(planSQL.toString());
//		    	}
//		    	if(!acoSQL.toString().equals(""))
//				{
//		    		acoSQL.append(" ; ");
//		    		db.execSQL(acoSQL.toString());
//				}
//	    	}
//			catch(Exception ex)
//			{
//				ex.printStackTrace();
//			}
	    	db.setTransactionSuccessful();
	    	db.endTransaction();
	    	
    	}
    	catch(Exception ex)
    	{
    		Log.e("NI", ex.getMessage());
    		ex.printStackTrace();
    		
    	}
    	
    }
    
    public void insertDoctorFTS(List ftsArr, List dataArr) throws RuntimeException{
    	db.beginTransaction();
    	for(int i=0, j=dataArr.size(); i<j; i++) {
    		String ftsText = (String)ftsArr.get(i);
    		String id = (String)dataArr.get(i);
    		db.insert(tname[DOC_FTS], null, formatInstance(DOC_FTS, new String[]{ id, ftsText}));
    		//System.out.println("SMM::FTS::ID="+id+"::STR="+ftsText);
    	}
    	db.setTransactionSuccessful();
    	db.endTransaction();
    }
    //returns ROWID or -1
    public long insert(int tableId, String[] data) throws RuntimeException{
    	//return db.insert(tname[tableId], null, formatInstance(tableId, data));
    	long id = db.insert(tname[tableId], null, formatInstance(tableId, data));
        return id;
    }
    
    public void insert(int tableId, List dataArr) throws RuntimeException{
    	//return db.insert(tname[tableId], null, formatInstance(tableId, data));
    	db.beginTransaction();
    	for(int i=0, j=dataArr.size(); i<j; i++) {
    		String[] data = (String[])dataArr.get(i);
    		db.insert(tname[tableId], null, formatInstance(tableId, data));
    	}
    	db.setTransactionSuccessful();
    	db.endTransaction();
    }

    public long getCount(int tableId){
    	//System.out.println("SMM:::::"+DatabaseUtils.longForQuery(db, "SELECT count(DISTINCT doc_id) from t_doctor", null));
    	return DatabaseUtils.queryNumEntries(db, tname[tableId]);
    }
    
    public long getDoctorCount(){
    	return DatabaseUtils.longForQuery(db, "SELECT count(DISTINCT doc_id) from t_doctor", null);
    }
    
    public void rankDoctor(int docId, int rank){
    	db.execSQL("UPDATE t_doctor SET u_rank = '"+rank+"' WHERE doc_id = '"+docId+"'");
    }
    
    public void synced(){
    	db.execSQL("UPDATE t_users SET need_to_sync = 0");
    }
    
    public void needToSync(){
    	db.execSQL("UPDATE t_users SET need_to_sync = 1");
    }
    
    public void settingUpdate(int v){
    	db.execSQL("UPDATE t_users SET update_setting = "+v);
    }
    			//returns 1(success) or 0(fail), CAN'T return > 1
    public int update(int tableId, long rowId, String[] data) throws RuntimeException {
        return db.update(tname[tableId], formatInstance(tableId, data), matchRow(rowId), null);
    }
    
    //returns TRUE or FALSE
    public boolean delete(int tableId, long rowId) throws RuntimeException {
    	if(tableId == COUNTY || tableId == SPECIALTY) 
            needToSync();
    	return db.delete(tname[tableId], matchRow(rowId), null) > 0;
    }
    
    public boolean deleteAll(int tableId) throws RuntimeException {
    	if(tableId == COUNTY || tableId == SPECIALTY) 
        	needToSync();
    	return db.delete(tname[tableId], null, null) > 0;
    }
    
    public boolean deleteAllSettingPractice() throws RuntimeException {
    	Cursor cr = fetchAll(this.USERS);
    	int pracId = 0;
    	if(cr != null && cr.getCount() > 0) {
        	cr.moveToFirst();
        	pracId = cr.getInt(6);
        	cr.close();
        }
    	return db.delete(tname[PRACTICE], "prac_id !="+pracId, null) > 0;
    }
    
    public boolean deleteAllSettingHospital() throws RuntimeException {
    	Cursor cr = fetchAll(this.USERS);
    	int hospId = 0;
    	if(cr != null && cr.getCount() > 0) {
        	cr.moveToFirst();
        	hospId = cr.getInt(7);
        	cr.close();
        }
    	return db.delete(tname[HOSPITAL], "hos_id !="+hospId, null) > 0;
    }
    
    /********
    * FETCH * 
    ********/
    
    //returns CURSOR or NULL
    public Cursor fetchAll(int tableId) throws RuntimeException {
    	
    	if(tableId == 0) {
    		return dbr.query(tname[tableId], tcols[tableId], null, null, null, null, "last_name");
    	} else if(tableId == 6) {
    		return dbr.query(tname[tableId], tcols[tableId], null, null, null, null, "u_rank, grade, first_name");
    	} else if(tableId >= 8) {
    		return dbr.query(tname[tableId], tcols[tableId], null, null, null, null, null);
    	} else {
    		return dbr.query(tname[tableId], tcols[tableId], null, null, null, null, "name");
    	}
    }

    //returns CURSOR or NULL
    public Cursor fetch(int tableId, long rowId) throws RuntimeException {
        Cursor cur = dbr.query(true, tname[tableId], tcols[tableId], matchRow(rowId),
        		null, null, null, null, null);
        if(cur!=null)
        	cur.moveToFirst();
        return cur;
    }

   //returns CURSOR or NULL
    public Cursor fetchReportByDocId(long docId) {
    	Cursor cur = dbr.query(true, tname[DOC_REPORT], tcols[DOC_REPORT], 
        		"doc_id = "+docId,
        		null, null, null, null, null);
        if(cur!=null)
        	cur.moveToFirst();
        return cur;
    }

    public Cursor fetchByNetId(int tableId, long netId, String columnName) {
    	Cursor cur = dbr.query(true, tname[tableId], tcols[tableId], 
        		columnName+"="+netId,
        		null, null, null, null, null);
        if(cur!=null)
        	cur.moveToFirst();
        return cur;
    }
    
    public Cursor fetchByNetId(int tableId, long netId) {
    	Cursor cur = dbr.query(true, tname[tableId], tcols[tableId], 
    			tcols[tableId][1]+"="+netId,
        		null, null, null, null, null);
        if(cur!=null)
        	cur.moveToFirst();
        return cur;
    }
    //////////////////////////////////////////////////////////////////////////////////
    ////////////////******************************************/////////////////////////

    
    
    
    
    public Cursor fetchInsurance(int tableId, String docins) {
    	Cursor cur = dbr.query(true, tname[tableId], tcols[tableId], 
    			tcols[tableId][1]+" IN ("+docins+")",
        		null, null, null, null, null);
        if(cur!=null)
        	cur.moveToFirst();
        return cur;
    }
    public Cursor fetchdocInsurance(int tableId, long netId) {
    	Cursor cur = dbr.query(true, tname[tableId], tcols[tableId], 
    			tcols[tableId][2]+"="+netId,
        		null, null, null, null, null);
        if(cur!=null)
        	cur.moveToFirst();
        return cur;
    }
    
    public Cursor fetchdocSpeciality(int tableId, long netId) {
    	Cursor cur = dbr.query(true, tname[tableId], tcols[tableId], 
    			tcols[tableId][2]+"="+netId,
        		null, null, null, null, null);
        if(cur!=null)
        	cur.moveToFirst();
        return cur;
    }
    public Cursor fetchAco(int tableId, String docins) {
    	Cursor cur = dbr.query(true, tname[tableId], tcols[tableId], 
    			tcols[tableId][1]+" IN ("+docins+")",
        		null, null, null, null, null);
        if(cur!=null)
        	cur.moveToFirst();
        return cur;
    }
    
    public Cursor fetchAcoSpeciality(int tableId, long netId) {
    	Cursor cur = dbr.query(true, tname[tableId], tcols[tableId], 
    			tcols[tableId][2]+"="+netId,
        		null, null, null, null, null);
        if(cur!=null)
        	cur.moveToFirst();
        return cur;
    }
    public Cursor fetchSpeciality(int tableId, String docins) {
    	Cursor cur = dbr.query(true, tname[tableId], tcols[tableId], 
    			tcols[tableId][1]+" IN ("+docins+")",
        		null, null, null, null, null);
        if(cur!=null)
        	cur.moveToFirst();
        return cur;
    }
    
    public Cursor fetchdocHospital(int tableId, long netId) {
    	Cursor cur = dbr.query(true, tname[tableId], tcols[tableId], 
    			tcols[tableId][2]+"="+netId,
        		null, null, null, null, null);
        if(cur!=null)
        	cur.moveToFirst();
        return cur;
    }
    public Cursor fetchHospital(int tableId, String docins) {
    	Cursor cur = dbr.query(true, tname[tableId], tcols[tableId], 
    			tcols[tableId][1]+" IN ("+docins+")",
        		null, null, null, null, null);
        if(cur!=null)
        	cur.moveToFirst();
        return cur;
    }
    
    public Cursor fetchdocPractice(int tableId, long netId) {
    	Cursor cur = dbr.query(true, tname[tableId], tcols[tableId], 
    			tcols[tableId][2]+"="+netId,
        		null, null, null, null, null);
        if(cur!=null)
        	cur.moveToFirst();
        return cur;
    }
    public Cursor fetchPractice(int tableId, String docins) {
    	Cursor cur = dbr.query(true, tname[tableId], tcols[tableId], 
    			tcols[tableId][1]+" IN ("+docins+")",
        		null, null, null, null, null);
        if(cur!=null)
        	cur.moveToFirst();
        return cur;
    }
    public int getDoctorIdbyUserId(int tableId, String userId) {
    	Cursor cur = dbr.query(true, tname[tableId], tcols[tableId], 
    			tcols[tableId][1]+"="+userId,
        		null, null, null, null, null);
        if(cur!=null)
        	cur.moveToFirst();
         int s = cur.getInt(13);
        
        return s;
    }
    
    

    ///////////////////***********************************************///////////////////
    //////////////////////////////////////////////////////////////////////////////////
    public Cursor fetchByNetIds(int tableId, String netId) {
    	Cursor cur = dbr.query(true, tname[tableId], tcols[tableId], 
    			tcols[tableId][1]+" IN ("+netId+")",
        		null, null, null, null, null);
        if(cur!=null)
        	cur.moveToFirst();
        return cur;
    }
    
    private int showCount(){
    	Cursor cur = dbr.query(true, tname[STATISTICS], tcols[STATISTICS], null,
        		null, null, null, null, null);
        if(cur!=null && cur.getCount() > 0){
        	cur.moveToFirst();
        	//System.out.println("-------------COUNT="+cur.getInt(2));
        	return cur.getInt(2);
        }else 
        	//System.out.println("-------------COUNT EMPTY");
        return -1;
    }
    public Cursor searchDoctor(String insuIds, String specIds, String hospIds, String cntyIds, String docName, 
    		String zipCode, String languages, String limit, int searchOrder) {
    	/*
    	System.out.println("SMM::SEARCH-SQL::(last_name LIKE '%"+docName+"%'"+" OR first_name LIKE '%"+docName+"%'"+" OR mid_name LIKE '%"+docName+"%' )"+
        		(Utils.isEmpty(specIds) ? "" : "AND spec_id IN ("+specIds+") ")+
        		(Utils.isEmpty(hospIds) ? "" : "AND hosp_id IN ("+hospIds+") ")+
        		(Utils.isEmpty(insuIds) ? "" : "AND insu_id IN ("+insuIds+") "));
    	*/
    	if(showCount() > -1){
    		db.execSQL("UPDATE t_statistics set count = count+1");
    		//System.out.println("SMM::::::::::::::INCSREASING COUNT");
    	} else {
    		deleteAll(STATISTICS);
    		insert(STATISTICS, new String[]{"1","1"});
    		//System.out.println("SMM::::::::::::::INSERTING COUNT");
    	}
    	//showCount();
		
    	String arr[] = languages.split(",");
    	String langStr = "";
    	for(int i=0, j=arr.length; i<j; i++) {
    		if(i == 0)
    			langStr = "AND ( language LIKE '%"+arr[i]+"%'";
    		else
    			langStr = langStr + " OR language LIKE '%"+arr[i]+"%'";
    	}
    	if(!Utils.isEmpty(langStr))
    		langStr = langStr +")";
    	
    	String orderStr = "u_rank DESC, grade DESC, first_name, last_name";
    	
    	if(searchOrder == 1)
    		orderStr = "grade DESC, u_rank DESC, first_name, last_name";
    	else if(searchOrder == 2)
    		orderStr = "first_name, u_rank DESC, grade DESC, last_name";
    	else if(searchOrder == 3)
    		orderStr = "last_name, u_rank DESC, grade DESC, first_name";
    	
    	Cursor cur = dbr.query(true, tname[DOCTOR], tcols[DOCTOR], 
        		"(last_name LIKE '%"+docName+"%'"+" OR first_name LIKE '%"+docName+"%'"+" OR mid_name LIKE '%"+docName+"%' )"+
        		(Utils.isEmptyNumber(specIds) ? "" : "AND spec_id IN ("+specIds+") ")+
        		(Utils.isEmptyNumber(hospIds) ? "" : "AND hosp_id IN ("+hospIds+") ")+
        		(Utils.isEmptyNumber(insuIds) ? "" : "AND insu_id IN ("+insuIds+") ")+
        		(Utils.isEmpty(zipCode) ? "" : "AND zip_code = '"+zipCode+"')")+
        		(Utils.isEmpty(langStr) ? "" : langStr),
        		null, null, null, orderStr, limit == null ? "50" : limit);
        if(cur!=null)
        	cur.moveToFirst();
        return cur;
    }
    
    public int countDoctor(String insuIds, String specIds, String hospIds, String cntyIds, String docName, 
    		String zipCode, String languages, String limit) {
    	
    	String arr[] = languages.split(",");
    	String langStr = "";
    	for(int i=0, j=arr.length; i<j; i++) {
    		if(i == 0)
    			langStr = "AND ( language LIKE '%"+arr[i]+"%'";
    		else
    			langStr = langStr + " OR language LIKE '%"+arr[i]+"%'";
    	}
    	if(!Utils.isEmpty(langStr))
    		langStr = langStr +")";
    	
    	Cursor cur = dbr.query(true, tname[DOCTOR], new String[]{"doc_id"}, 
        		"(last_name LIKE '%"+docName+"%'"+" OR first_name LIKE '%"+docName+"%'"+" OR mid_name LIKE '%"+docName+"%' )"+
        		(Utils.isEmptyNumber(specIds) ? "" : "AND spec_id IN ("+specIds+") ")+
        		(Utils.isEmptyNumber(hospIds) ? "" : "AND hosp_id IN ("+hospIds+") ")+
        		(Utils.isEmptyNumber(insuIds) ? "" : "AND insu_id IN ("+insuIds+") ")+
        		(Utils.isEmpty(zipCode) ? "" : "AND zip_code = '"+zipCode+"')")+
        		(Utils.isEmpty(langStr) ? "" : langStr),
        		null, null, null, "u_rank DESC, grade DESC, first_name", null);
        
    	if(cur!=null)
    		return cur.getCount();
    	return 0;
    }
    
    public Cursor fetchPracticeByCode(String code) throws RuntimeException {
        Cursor cur = dbr.query(true, tname[PRACTICE], tcols[PRACTICE], 
        		"name LIKE '%"+code+"%'",
        		null, null, null, null, null);
        if(cur!=null)
        	cur.moveToFirst();
        return cur;
    }
    
    public Cursor getMyPractice() throws RuntimeException {
        Cursor cur = fetchAll(this.USERS);
        Cursor cur1 = null;
        if(cur!=null) {
        	cur.moveToFirst();
        	int pracId = cur.getInt(6);
        	//System.out.println("SMM::MY-PRAC-ID::"+pracId);
        	cur.close();
        	cur1 = dbr.query(true, tname[this.PRACTICE], tcols[this.PRACTICE], "prac_id = "+pracId,
            		null, null, null, null, null);
            //fetch(this.PRACTICE, pracId);
        	if(cur1!=null)
            	cur1.moveToFirst();
        }
        return cur1;
    }
    
    public Cursor fetchDoctorByName(String code) throws RuntimeException {
        Cursor cur = dbr.query(true, tname[DOCTOR], tcols[DOCTOR], 
        		"last_name LIKE '%"+code+"%' OR first_name LIKE '%"+code+"%' OR mid_name LIKE '%"+code+"%'",
        		null, null, null, "u_rank DESC, grade DESC, first_name", "50");
        if(cur!=null)
        	cur.moveToFirst();
        return cur;
    }
    
    public Cursor fetchDoctorFTS2(String code) throws RuntimeException {
        String str = "";
        String arr[] = code.split(" ");
        String arg[] = new String[arr.length];
        for(int i=0, j=arr.length; i<j; i++) {
    		str += ("".equals(str) ? " WHERE " : " AND ")+ "LOWER(doc.last_name) LIKE ? ";
    		arg[i] = "%"+arr[i].toLowerCase()+"%";
        }
        //String arr2[] = new String[]{"doc._id","doc.doc_id","doc.first_name", "doc.last_name", 
        //		"doc.mid_name", "doc.degree", "doc.doc_phone", "doc.language", "doc.grade","doc.u_rank"};
        
    	String sql = "SELECT DISTINCT doc._id, doc.doc_id, doc.first_name, doc.last_name, doc.mid_name, doc.degree, doc.doc_phone, doc.language, doc.grade, doc.u_rank "+
						   //",(doc.first_name || ' ' || doc.mid_name || ' ' || doc.last_name || ' ' || doc.degree || ' ' || "+
						   //"doc.language || ' ' || doc.grade || ' ' || doc.doc_phone || ' ' || doc.doc_fax || ' ' || doc.npi || ' ' || doc.office_hour) all_text "+
						   "FROM t_doctor doc, t_practice prac "+
						   //"LEFT JOIN t_practice prac ON (doc.prac_id = prac.prac_id) "+
						   //"LEFT JOIN t_hospital hos ON (doc.hosp_id = hos.hos_id) "+
						   //"LEFT JOIN t_specialty spec ON (doc.spec_id = spec.spec_id) "+
						   //"LEFT JOIN t_insurance ins ON (doc.insu_id = ins.ins_id) "+
						   //"LEFT JOIN t_county county ON (doc.county_id = county.county_id) "+
						   str+" AND doc.prac_id = prac.prac_id ";   
    	sql += "order by doc.u_rank desc, doc.last_name limit 100";
    	System.out.println("SMM::SQL="+sql);
    	Cursor cur = dbr.rawQuery(sql, arg);
        if(cur!=null) {
        	cur.moveToFirst();
        	System.out.println("SMM::LEN="+cur.getCount());
        }
        return cur;
    }
    
    public Cursor fetchDoctorFTS(String code) throws RuntimeException {
        String[] args = code.split(" ");
        String key = "";
        if(args != null && args.length > 0) {
        	StringBuffer sb = new StringBuffer();
        	for(int i=0; i<args.length; i++)
        		sb.append(args[i]+"*");
        	key = sb.toString();
        } else 
        	key = code;
        //System.out.println("FTS KEY="+key);
        Cursor cur0 = dbr.query(true, tname[DOC_FTS], new String[]{"doc_id"}, 
            "text MATCH '"+key+"'",
            null, null, null, null, "50");
        String ids = "";
        if(cur0 != null){
        	cur0.moveToFirst();
        	for(int i=0; i<cur0.getCount(); i++){
        		if(i == 0)
        			ids = cur0.getString(0);
        		else 
        			ids = ids+","+cur0.getString(0);
        		cur0.moveToNext();
        	}
        	cur0.close();
        }
        //System.out.println("FTS IDS="+ids);
        Cursor cur = dbr.query(true, tname[DOCTOR], tcols[DOCTOR], 
            "doc_id IN ("+ids+")",
            null, null, null, "u_rank DESC, grade DESC, first_name", "50");
        if(cur!=null)
            cur.moveToFirst();
        return cur;
    }
    
    /*
    //returns CURSOR or NULL
    public Cursor fetchFramingByQuestion(int secId, int qesIdx) throws RuntimeException {
        Cursor cur = dbr.query(true, tname[OPENING_FRAMING], tcols[OPENING_FRAMING], 
        		"sec_id="+secId+" AND qes_idx="+qesIdx,
        		null, null, null, null, null);
        if(cur!=null)
        	cur.moveToFirst();
        return cur;
    }
    
    //returns CURSOR or NULL
    public Cursor fetchDimensionByFraming(int framingId) throws RuntimeException {
        Cursor cur = dbr.query(true, tname[OPENING_DIMENSION], tcols[OPENING_DIMENSION], 
        		"frameing_id="+framingId,
        		null, null, null, null, null);
        if(cur!=null)
        	cur.moveToFirst();
        return cur;
    }
    public Cursor fetchDimensionById(int id) throws RuntimeException {
        Cursor cur = dbr.query(true, tname[OPENING_DIMENSION], tcols[OPENING_DIMENSION], 
        		"_id="+id,
        		null, null, null, null, null);
        if(cur!=null)
        	cur.moveToFirst();
        return cur;
    }
    */
    //returns STRING or NULL
    public String fetchFormattedDataSingleRow(int tableId, long rowId, String format) throws RuntimeException {
    	Cursor cur = fetch(tableId, rowId);
    	if(cur==null)
    		return null;
    	
    	act.startManagingCursor(cur);
        return getFormattedDataSingleRow(tableId, format, cur);
    }
    
    public int[] getPkList(Cursor cr) {
    	if(cr == null) return null;
    	int arr[] = new int[cr.getCount()];
    	cr.moveToFirst();
    	for(int i=0,j=cr.getCount(); i<j; i++){
    		arr[i] = cr.getInt(0);
    		cr.moveToNext();
    	}
    	return arr;
    }
    /******************************
    * Database Helper Inner Class * 
    ******************************/
    private static class DbHelper extends SQLiteOpenHelper {

    	private final TextView debug;
    	
        DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.debug = null;
        }

        DbHelper(Context context, TextView debug) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.debug = debug;
            debug.setVisibility(View.VISIBLE);
        }
        
        private String getType(char x){
        	if(x=='i')	return "INTEGER";
        	if(x=='t')	return "TEXT";
        	if(x=='d')	return "DATE";
        	if(x=='f')	return "REAL"; //floating point
        	return null;
        }
        private String createTableCommand(int id){
        	if(id == DOC_FTS){
                return "CREATE VIRTUAL TABLE "+tname[DOC_FTS]+" using FTS3(doc_id, text)";
            }

        	StringBuilder s = new StringBuilder("CREATE TABLE ");
        	s.append(tname[id]);
        	s.append(" (");
        	s.append(PK);
        	s.append(" INTEGER PRIMARY KEY AUTOINCREMENT");
        	
        	for(int i=1;i<tcols[id].length;i++){
        		s.append(", ");
        		s.append(tcols[id][i] + " " + getType(ttypes[id].charAt(i)) );
        		if(tnulls[id].charAt(i)=='Y')
        			s.append(" NOT NULL");
        		if(tuniques[id].charAt(i)=='Y')
        			s.append(" UNIQUE");
        	}
        
        	//", CONSTRAINT ### FOREIGN KEY(***) REFERENCES @@@(_id)
/*        	for(int i=0;i<tcvar[id].length;i++){
        		s.append(", CONSTRAINT ");
        		s.append("c" + tname[id] + tcols[id][tcvar[id][i]]);
        		s.append(" FOREIGN KEY(" + tcols[id][tcvar[id][i]] + ")");
        		s.append(" REFERENCES " + tname[tcref[id][i]] + "(" + PK + ")");
        	}*/
        	for(int i=0;i<tcols[id].length;i++){
        		if(tcref[id][i]==NO)
        			continue;
	    		s.append(", CONSTRAINT ");
	    		s.append("c" + tname[id] + tcols[id][i]);
	    		s.append(" FOREIGN KEY(" + tcols[id][i] + ")");
	    		s.append(" REFERENCES " + tname[tcref[id][i]] + "(" + PK + ")");
	    	}
        	s.append(")");
        	
        	return new String(s);
        }
        
        private void createTables(SQLiteDatabase db) throws RuntimeException{
        	for(int i=0;i<tname.length;i++){
	        	try{
	        		db.execSQL(createTableCommand(i));
		        	if(debug!=null)	debug.append("<" + tname[i] + ">\n");
	        	}
	        	catch(SQLException ex){
	        		if(debug!=null)	debug.append("<" + ex.getMessage() + ">\n");
	        	}
        	}
        }
        
        private void dropTables(SQLiteDatabase db){
            for(int i = 0;i < tname.length; i++)
            	db.execSQL("DROP TABLE IF EXISTS " + tname[i]);
        }
        
        @Override
        public void onCreate(SQLiteDatabase db) throws RuntimeException{
        	//Create all tables
        	//dropTables(db);
        	
        	createTables(db);

/*        	tcolmap = (TreeMap<String, Integer>[]) new TreeMap[tname.length];
        	for(int i=0;i<tname.length;i++){
        		tcolmap[i].clear();
        		for(int j=0;j<tcols[i].length;j++)
        			tcolmap[i].put(tcols[i][j], j);
        	}*/
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            dropTables(db);
            onCreate(db);
        }
    }
}
