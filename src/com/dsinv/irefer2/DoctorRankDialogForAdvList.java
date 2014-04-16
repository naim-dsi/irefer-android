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
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class DoctorRankDialogForAdvList extends Dialog {
    
	//DoctorDetailActivity ctx;
	//final DbAdapter dba;
	Button btnArr[] = new Button[5];
	RatingBar ratingBar = null;
	int rankIdArr[] = {R.id.doc_rank_dialog_btn_1,
			R.id.doc_rank_dialog_btn_2,
			R.id.doc_rank_dialog_btn_3,
			R.id.doc_rank_dialog_btn_4,
			R.id.doc_rank_dialog_btn_5};
	
	public DoctorRankDialogForAdvList(final DoctorListAdvanceActivity ctx, int style, 
			final DbAdapter dba,
			final int isOnlineSearch, final int position){
		super(ctx, style);
		setContentView(R.layout.doctor_rank_dialog);
		setCancelable(true);
		final Map<String, String> data = ctx.data.get(position);
		//there are a lot of settings, for dialog, check them all out!
		int userRankValue = Integer.parseInt((String)data.get("u_rank"));
		final int doctorId= Integer.parseInt((String)data.get("docId"));
		String docName = (String)data.get("docTitile1");
        //there are a lot of settings, for dialog, check them all out!
		ratingBar = (RatingBar)findViewById(R.id.doc_dialog_ratingbar);
        ratingBar.setRating(userRankValue);
		
        for(int i=0; i<5; i++) {
			btnArr[i] = (Button)findViewById(rankIdArr[i]);
        }
        btnArr[0].setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				for(int i=0; i<5; i++)
					btnArr[i].setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.gray_round_icon_48));
				btnArr[0].setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.yellow_round_icon_48));	
				ratingBar.setRating(1);
		}});
        btnArr[1].setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				for(int i=0; i<5; i++)
					btnArr[i].setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.gray_round_icon_48));
				for(int i=0; i<=1; i++)
					btnArr[i].setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.yellow_round_icon_48));
				ratingBar.setRating(2);
		}});
        btnArr[2].setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				for(int i=0; i<5; i++)
					btnArr[i].setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.gray_round_icon_48));
				for(int i=0; i<=2; i++)
					btnArr[i].setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.yellow_round_icon_48));
				ratingBar.setRating(3);
		}});
        btnArr[3].setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				for(int i=0; i<5; i++)
					btnArr[i].setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.gray_round_icon_48));
				for(int i=0; i<=3; i++)
					btnArr[i].setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.yellow_round_icon_48));
				ratingBar.setRating(4);
		}});
        btnArr[4].setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				for(int i=0; i<5; i++)
					btnArr[i].setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.gray_round_icon_48));
				for(int i=0; i<=4; i++)
					btnArr[i].setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.yellow_round_icon_48));
				ratingBar.setRating(5);
		}});
        for(int i=0; i<userRankValue; i++) {
			Button btn = (Button)findViewById(rankIdArr[i]);
			btn.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.yellow_round_icon_48));
			//btn.set
		}
        //set up text
        TextView text = (TextView)findViewById(R.id.rank_dialog_text1);
        text.setText(docName);

        //set up button
        Button updateButton = (Button) findViewById(R.id.doc_rank_dialog_button);
        updateButton.setOnClickListener(new View.OnClickListener() {
        @Override
            public void onClick(View v) {
        		try {
        			dba.rankDoctor(doctorId, Math.round(ratingBar.getRating()));
        			Log.d("NR::", ""+doctorId+" "+Math.round(ratingBar.getRating()));
        			//rankTxt.setText(Math.round(ratingBar.getRating()));
        			if(isOnlineSearch==1) {
            			String stringUrl = ABC.WEB_URL+"userDocRank/rank?user_id="+Utils.userId+"&doc_id="+doctorId+"&rank="+Math.round(ratingBar.getRating()) ;
        				String res = Utils.getDataFromURL(stringUrl);
        				Log.d("NI::",stringUrl);
        				if(res.equals("saved")){
        					Toast.makeText(ctx, "Rank has been updated", Toast.LENGTH_SHORT).show();
        				}
        				else{
        					Toast.makeText(ctx, "Rank has not been updated to online storage.", Toast.LENGTH_SHORT).show();
        				}
        			} else
        			{
        				Toast.makeText(ctx, "Rank has been updated.", Toast.LENGTH_SHORT).show();
        			}
        			data.put("u_rank",String.valueOf(Math.round(ratingBar.getRating())));
        			//ctx.data.remove(position);
        			ctx.data.set(position,data);
        			ctx.adapter.notifyDataSetChanged();
	        			
				} catch(Exception ex) {
					Toast.makeText(ctx, "Failed to update rank ", Toast.LENGTH_SHORT).show();
					ex.printStackTrace();
				}
        		dismiss();
            }
        });
       
	}
	
}
