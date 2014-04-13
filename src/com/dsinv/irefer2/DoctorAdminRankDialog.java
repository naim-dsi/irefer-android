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

public class DoctorAdminRankDialog extends Dialog {
    
	//DoctorDetailActivity ctx;
	//final DbAdapter dba;
	Button btnArr[] = new Button[10];
	RatingBar ratingBar = null;
	RatingBar adminRatingBar = null;
	int rankIdArr[] = {R.id.doc_admin_rank_dialog_btn_1,
			R.id.doc_admin_rank_dialog_btn_2,
			R.id.doc_admin_rank_dialog_btn_3,
			R.id.doc_admin_rank_dialog_btn_4,
			R.id.doc_admin_rank_dialog_btn_5,
			R.id.doc_admin_rank_dialog_btn_6,
			R.id.doc_admin_rank_dialog_btn_7,
			R.id.doc_admin_rank_dialog_btn_8,
			R.id.doc_admin_rank_dialog_btn_9,
			R.id.doc_admin_rank_dialog_btn_10
	};
	
	public DoctorAdminRankDialog(final DoctorDetailActivity ctx, int style, 
			final DbAdapter dba,
			final int doctorId,
			final String docName,
			final String userId,
			final int userRankValue,
			final int adminRankValue,
			final boolean isOnlineSearch){
		super(ctx, style);
		setContentView(R.layout.doctor_admin_rankdialog);
		setCancelable(true);
        //there are a lot of settings, for dialog, check them all out!
		ratingBar = (RatingBar)findViewById(R.id.doc_own_dialog_ratingbar);
        ratingBar.setRating(userRankValue);
        
        adminRatingBar = (RatingBar)findViewById(R.id.doc_admin_dialog_ratingbar);
        adminRatingBar.setRating(adminRankValue);
        
        for(int i=0; i<10; i++) {
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
        /////////////////////////////////////////////////////////////////////////////////////////////
        for(int i=5; i<10; i++) {
			btnArr[i] = (Button)findViewById(rankIdArr[i]);
        }
        btnArr[5].setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				for(int i=5; i<10; i++)
					btnArr[i].setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.gray_round_icon_48));
				btnArr[5].setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.yellow_round_icon_48));	
				adminRatingBar.setRating(1);
		}});
        btnArr[6].setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				for(int i=5; i<10; i++)
					btnArr[i].setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.gray_round_icon_48));
				for(int i=5; i<=6; i++)
					btnArr[i].setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.yellow_round_icon_48));
				adminRatingBar.setRating(2);
		}});
        btnArr[7].setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				for(int i=5; i<10; i++)
					btnArr[i].setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.gray_round_icon_48));
				for(int i=5; i<=7; i++)
					btnArr[i].setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.yellow_round_icon_48));
				adminRatingBar.setRating(3);
		}});
        btnArr[8].setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				for(int i=5; i<10; i++)
					btnArr[i].setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.gray_round_icon_48));
				for(int i=5; i<=8; i++)
					btnArr[i].setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.yellow_round_icon_48));
				adminRatingBar.setRating(4);
		}});
        btnArr[9].setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				for(int i=5; i<10; i++)
					btnArr[i].setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.gray_round_icon_48));
				for(int i=5; i<=9; i++)
					btnArr[i].setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.yellow_round_icon_48));
				adminRatingBar.setRating(5);
		}});
        for(int i=5; i<adminRankValue+5; i++) {
			Button btn = (Button)findViewById(rankIdArr[i]);
			btn.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.yellow_round_icon_48));
			//btn.set
		}
        /////////////////////////////////////////////////////////////////////////////////////////////
        //set up text
        TextView text = (TextView)findViewById(R.id.rank_dialog_doctor_name_text);
        text.setText(docName);

        //set up button
        Button updateButton = (Button) findViewById(R.id.doc_admin_rank_dialog_button);
        updateButton.setOnClickListener(new View.OnClickListener() {
        @Override
            public void onClick(View v) {
        		try {
        			dba.rankDoctor(doctorId, Math.round(ratingBar.getRating()));
        			dba.rankAdminDoctor(doctorId, Math.round(adminRatingBar.getRating()));
        			Log.d("NR::", ""+doctorId+" "+Math.round(ratingBar.getRating()));
        			//rankTxt.setText(Math.round(ratingBar.getRating()));
        			if(isOnlineSearch) {
            			String stringUrl = ABC.WEB_URL+"userDocRank/rank?user_id="+Utils.userId+"&doc_id="+doctorId+"&rank="+Math.round(ratingBar.getRating()) ;
        				String res = Utils.getDataFromURL(stringUrl);
        				Log.d("NI::",stringUrl);
        				if(res.equals("saved")){
        					Toast.makeText(ctx, "Rank has been Updated", Toast.LENGTH_SHORT).show();
        				}
        				else{
        					Toast.makeText(ctx, "Rank has not been updated to online storage.", Toast.LENGTH_SHORT).show();
        				}
        				
        				stringUrl = ABC.WEB_URL+"userDocRank/paRank?user_id="+
        						Utils.userId+"&doc_id="+doctorId+"&rank="+Math.round(adminRatingBar.getRating())  ;
        				res = Utils.getDataFromURL(stringUrl);
        				Log.d("NI::",stringUrl);
        				if(res.equals("saved")){
        					Toast.makeText(ctx, "Admin Rank has been Updated", Toast.LENGTH_SHORT).show();
        				}
        				else{
        					Toast.makeText(ctx, "Admin Rank has not been updated to online storage.", Toast.LENGTH_SHORT).show();
        				}
        			} else{
        				Toast.makeText(ctx, "Rank has been Updated", Toast.LENGTH_SHORT).show();
        			}
    				ctx.rankTxt.setText(""+Math.round(ratingBar.getRating()));
    				ctx.userRankValue=Math.round(ratingBar.getRating());
    				ctx.adminRankValue=Math.round(adminRatingBar.getRating());
	        			
				} catch(Exception ex) {
					Toast.makeText(ctx, "Failed to update rank ", Toast.LENGTH_SHORT).show();
					ex.printStackTrace();
					return;
				}
        		dismiss();
            }
        });
       
	}
	
}