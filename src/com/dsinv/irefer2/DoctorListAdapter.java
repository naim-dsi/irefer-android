package com.dsinv.irefer2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsinv.irefer2.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class DoctorListAdapter extends SimpleAdapter {
	
	private DbAdapter dba;
	Context ctx;
	//final CharSequence[] items = {"One (1)", "Two (2)", "Three (3)", "Four (4)", "Five (5)"};
	Dialog dialog; 
	RatingBar ratingBar;
	int onlineFlag = 0;
	int rankValue = 0;
	
	Button btnArr[] = new Button[5];
	
	int rankIdArr[] = {R.id.doc_rank_dialog_btn_1,
				R.id.doc_rank_dialog_btn_2,
				R.id.doc_rank_dialog_btn_3,
				R.id.doc_rank_dialog_btn_4,
				R.id.doc_rank_dialog_btn_5};
	
    public DoctorListAdapter(Context context, DbAdapter	dba,List<? extends Map<String, ?>> data,
            int resource, String[] from, int[] to,int onlineFlag) {
        super(context, data, resource, from, to);
        this.ctx = context;
        this.dba = dba;
        this.onlineFlag=onlineFlag;
    }
    
    public DoctorListAdapter(Context context,List<? extends Map<String, ?>> data,
            int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        this.ctx = context;
        this.onlineFlag = 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	try{
	    	View itemView = null;
	
	        if (convertView == null) {
	            LayoutInflater inflater = (LayoutInflater) parent.getContext()
	                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            itemView = inflater.inflate(R.layout.doctor_row, null);
	        } else {
	            itemView = convertView;
	        }
	        //if (convertView == null) {
	        //    convertView = getLayoutInflater().inflate(R.layout.list_layout,
	        //            null);
	        //}
	
	        final HashMap<String, Object> data = (HashMap<String, Object>) getItem(position);
	
	        ((TextView) itemView.findViewById(R.id.doc_title1))
	                .setText((String) data.get("docTitile1"));
	        ((TextView) itemView.findViewById(R.id.doc_title2))
	        .setText((String) data.get("docTitile2"));
	        ((TextView) itemView.findViewById(R.id.doc_title3))
	        .setText((String) data.get("docTitile3"));
	        ((TextView) itemView.findViewById(R.id.doc_row_doc_id))
	        .setText((String) data.get("docId"));
	        
	        LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(
	        	    LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT );
	        	
	        if("SHOW_MORE".equals(data.get("docId")) || "NO_RESULT".equals(data.get("docId"))){
	        	itemView.findViewById(R.id.list_call_button).setVisibility(View.GONE);
	        	itemView.findViewById(R.id.list_ratingbar_small).setVisibility(View.GONE);
	        	itemView.findViewById(R.id.list_rank_button).setVisibility(View.GONE);
	        	itemView.findViewById(R.id.doc_rank_title1).setVisibility(View.GONE);
	        	itemView.findViewById(R.id.doc_rank_title2).setVisibility(View.GONE);
	        	itemView.findViewById(R.id.doc_title4).setVisibility(View.GONE);
	        	param1.setMargins(5, 0, 0, 0); //left,top,right, bottom
	        	itemView.findViewById(R.id.doc_title2).setLayoutParams(param1);
	        	//itemView.findViewById(R.id.doc_pic).setVisibility(View.GONE);
	        	((TextView)itemView.findViewById(R.id.doc_title1)).setTextColor(Color.CYAN);
	        	return itemView; 
	        } else {
	        	itemView.findViewById(R.id.list_call_button).setVisibility(View.VISIBLE);
	        	itemView.findViewById(R.id.list_ratingbar_small).setVisibility(View.VISIBLE);
	        	itemView.findViewById(R.id.list_rank_button).setVisibility(View.VISIBLE);
	        	itemView.findViewById(R.id.doc_rank_title1).setVisibility(View.VISIBLE);
	        	itemView.findViewById(R.id.doc_rank_title2).setVisibility(View.VISIBLE);
	        	itemView.findViewById(R.id.doc_title4).setVisibility(View.VISIBLE);
	        	param1.setMargins(0, 0, 0, 0); //left,top,right, bottom
	        	itemView.findViewById(R.id.doc_title2).setLayoutParams(param1);
	        	//itemView.findViewById(R.id.doc_pic).setVisibility(View.VISIBLE);
	        	((TextView)itemView.findViewById(R.id.doc_title1)).setTextColor(Color.WHITE);
	        }
	        
	        TextView rankText  = (TextView)itemView.findViewById(R.id.doc_rank_title2);
	        RatingBar userRank = (RatingBar)itemView.findViewById(R.id.list_ratingbar_small);
	        try {
	        	if(!Utils.isEmpty((String)data.get("u_rank"))) {
	        		rankText.setText((String)data.get("u_rank"));
	        	}
	        	if(!Utils.isEmpty((String)data.get("grade"))) {
	        		userRank.setRating(Integer.parseInt((String)data.get("grade")));
	        	}
	        } catch(Exception ex){
	        	ex.printStackTrace();
	        }
	        
	        Button callBtn = (Button) itemView.findViewById(R.id.list_call_button);
	        callBtn.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Log.d("NI::",data.get("docPhone").toString());
	            	if(!data.get("docPhone").toString().equals("")){
						DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
			    		        switch (which){
			    		            case DialogInterface.BUTTON_POSITIVE:
			    		            	
				    		            	Intent sIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+(String) data.get("docPhone")));
				    		            	ctx.startActivity(sIntent);
			    		            	
			    		            	break;
		
			    		            case DialogInterface.BUTTON_NEGATIVE:
			    		                //No button clicked
			    		                break;
			    		        }
			    		    }
						};
						AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		    		    builder.setMessage((String) data.get("docPhone")).setPositiveButton("Call", dialogClickListener)
		    		        .setNegativeButton("Cancel", dialogClickListener).show();
	            	}
	            	else
	            	{
	            		Toast.makeText(ctx, "No phone number given.", Toast.LENGTH_SHORT).show(); 
	            	}
					
				}
			});
	        Button rankBtn = (Button) itemView.findViewById(R.id.list_rank_button);
	        rankBtn.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					//System.out.println("RANK CLICKED");
					//Toast.makeText(ctx, items[0], Toast.LENGTH_SHORT).show();
					
					//set up dialog
	                dialog = new Dialog(ctx, R.style.FullHeightDialog);
	                dialog.setContentView(R.layout.doctor_rank_dialog);
	                dialog.setCancelable(true);
	                //there are a lot of settings, for dialog, check them all out!
	                for(int i=0; i<5; i++) {
	        			btnArr[i] = (Button)dialog.findViewById(rankIdArr[i]);
	                }
	                btnArr[0].setOnClickListener(new View.OnClickListener() {
	        			public void onClick(View v) {
	        				for(int i=0; i<5; i++)
	        					btnArr[i].setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.gray_round_icon_48));
	        				btnArr[0].setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.yellow_round_icon_48));	
	        				rankValue = 1;
	        		}});
	                btnArr[1].setOnClickListener(new View.OnClickListener() {
	        			public void onClick(View v) {
	        				for(int i=0; i<5; i++)
	        					btnArr[i].setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.gray_round_icon_48));
	        				for(int i=0; i<=1; i++)
	        					btnArr[i].setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.yellow_round_icon_48));
	        				rankValue = 2;
	        		}});
	                btnArr[2].setOnClickListener(new View.OnClickListener() {
	        			public void onClick(View v) {
	        				for(int i=0; i<5; i++)
	        					btnArr[i].setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.gray_round_icon_48));
	        				for(int i=0; i<=2; i++)
	        					btnArr[i].setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.yellow_round_icon_48));
	        				rankValue = 3;
	        		}});
	                btnArr[3].setOnClickListener(new View.OnClickListener() {
	        			public void onClick(View v) {
	        				for(int i=0; i<5; i++)
	        					btnArr[i].setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.gray_round_icon_48));
	        				for(int i=0; i<=3; i++)
	        					btnArr[i].setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.yellow_round_icon_48));
	        				rankValue = 4;
	        		}});
	                btnArr[4].setOnClickListener(new View.OnClickListener() {
	        			public void onClick(View v) {
	        				for(int i=0; i<5; i++)
	        					btnArr[i].setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.gray_round_icon_48));
	        				for(int i=0; i<=4; i++)
	        					btnArr[i].setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.yellow_round_icon_48));
	        				rankValue = 5;
	        		}});
	                ratingBar = (RatingBar)dialog.findViewById(R.id.doc_dialog_ratingbar);
	                try {
	                	if(!Utils.isEmpty((String)data.get("u_rank"))) {
	                		int rankV = Integer.parseInt((String)data.get("u_rank"));
	                		rankValue = rankV;
	                		ratingBar.setRating(rankV);
	                		for(int i=0; i<rankV; i++) {
	                			Button btn = (Button)dialog.findViewById(rankIdArr[i]);
	                			btn.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.yellow_round_icon_48));
	                			//btn.set
	                		}	
	                	} else {
	                		System.out.println("u_rank is empty at row data");
	                	}
	                } catch(Exception ex){
	                	ex.printStackTrace();
	                }
	                //set up text
	                TextView text = (TextView) dialog.findViewById(R.id.rank_dialog_text1);
	                text.setText((String)data.get("docTitile1"));
	 
	                //set up button
	                Button updateButton = (Button) dialog.findViewById(R.id.doc_rank_dialog_button);
	                updateButton.setOnClickListener(new View.OnClickListener() {
	                @Override
	                    public void onClick(View v) {
	                		//int rankValue = Math.round(ratingBar.getRating());
	                		try {
	                			String res = "";
	                			dba.rankDoctor(Integer.parseInt((String)data.get("docId")), rankValue);
	                			//if(onlineFlag == 1) {
	                			if(true){//naim
	                				String stringUrl = ABC.WEB_URL+"userDocRank/rank?user_id="+
		                					data.get("userId")+"&doc_id="+data.get("docId")+"&rank="+rankValue  ;
	                				res = getDataFromURL(stringUrl);
	                				Log.d("NI::",stringUrl);
	                				Toast.makeText(ctx, " "+res, Toast.LENGTH_SHORT).show();
	                			} else {
	                				
	                				Log.d("NR::", ""+Integer.parseInt((String)data.get("docId"))+rankValue);
	                				Toast.makeText(ctx, "Rank Updated to "+rankValue, Toast.LENGTH_SHORT).show();
	                			}
	                			data.put("u_rank",Integer.toString(rankValue));
	                			DoctorListAdapter.this.notifyDataSetChanged();
							} catch(Exception ex) {
								Toast.makeText(ctx, "Failed to rank ", Toast.LENGTH_SHORT).show();
								ex.printStackTrace();
							}
	                		dialog.dismiss();
	                    }
	                });
	                //now that the dialog is set up, it's time to show it    
	                dialog.show();
					
					
									
				}
			});
	        return itemView;
    	}
    	catch(Exception ex){
    		return null;
    	}
    }
    
    public String getDataFromURL(String urlStr) throws Exception {
    	System.out.println(urlStr);
    	URL url = new URL(urlStr);
	    URLConnection urlCon = url.openConnection();
	    BufferedReader in = new BufferedReader(
	                            new InputStreamReader(
	                            urlCon.getInputStream()));
	    String data = "";
	    String line = "";
	    
        while ((line = in.readLine()) != null)
        	data += line;
            //System.out.println(inputLine);
	    in.close();
	    return data;
    }
}