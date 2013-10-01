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
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class DoctorRankDialog extends Dialog {
    
	//DoctorDetailActivity ctx;
	//final DbAdapter dba;
	Button btnArr[] = new Button[5];
	RatingBar ratingBar = null;
	int rankIdArr[] = {R.id.doc_rank_dialog_btn_1,
			R.id.doc_rank_dialog_btn_2,
			R.id.doc_rank_dialog_btn_3,
			R.id.doc_rank_dialog_btn_4,
			R.id.doc_rank_dialog_btn_5};
	
	public DoctorRankDialog(final DoctorDetailActivity ctx, int style, 
			final DbAdapter dba,
			final int doctorId,
			final String docName,
			final String userId,
			final int userRankValue,
			final boolean isOnlineSearch){
		super(ctx, style);
		setContentView(R.layout.doctor_rank_dialog);
		setCancelable(true);
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
        			Log.d("NR::", ""+doctorId+Math.round(ratingBar.getRating()));
        			//rankTxt.setText(Math.round(ratingBar.getRating()));
        			if(isOnlineSearch){
        				String res = Utils.getDataFromURL(ABC.WEB_URL+"userDocRank/rank?user_id="+
        					userId+"&doc_id="+doctorId+"&rank="+Math.round(ratingBar.getRating()) );
        				Toast.makeText(ctx, " "+res, Toast.LENGTH_SHORT).show();
        			} else
        				Toast.makeText(ctx, "Rank updated to "+Math.round(ratingBar.getRating()), Toast.LENGTH_SHORT).show();
        				ctx.rankTxt.setText(""+Math.round(ratingBar.getRating()));
	        			
				} catch(Exception ex) {
					//Toast.makeText(ctx, "Failed to rank ", Toast.LENGTH_SHORT).show();
					ex.printStackTrace();
				}
        		dismiss();
            }
        });
       
	}
	
}
