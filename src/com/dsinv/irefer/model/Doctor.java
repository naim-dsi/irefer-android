package com.dsinv.irefer.model;

import org.json.JSONObject;

public class Doctor {
	public int id;
	public String lastName;   
	public String firstName;
	public String midName;
	public String gender;   
	public String language;
	public String phone;
	public String degree;
	public String grade;
	;
	public Doctor(){}

	public Doctor(JSONObject obj) {
		try {
			this.id = Integer.parseInt(obj.getString("id"));
			this.lastName = obj.getString("last_name") == null ? "" :obj.getString("last_name"); 
			this.firstName = obj.getString("first_name") == null ? "" :obj.getString("first_name");
			this.midName = obj.getString("mid_name") == null ? "" :obj.getString("mid_name"); 
			this.gender = obj.getString("gender").equals("1") ? "Male" :"Female";
			this.phone = obj.getString("doc_phone") == null ? "" :obj.getString("doc_phone");
			this.degree = obj.getString("degree") == null ? "" :obj.getString("degree");
			this.language = obj.getString("language") == null ? "" :obj.getString("language");
			this.grade = obj.getString("grade") == null ? "" :obj.getString("grade");
		} catch (Exception ex){
			this.id = 0;
		}
	}
	
	public String fullName() {
		return firstName +" "+midName+" "+lastName;
	}
}
