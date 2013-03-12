/**
 * @author JohnAdamsy
 * @modified March 10th 2013
 * @description Class displays the saved_votes layout
 * */
package org.un.myworld;

import java.util.Calendar;

import org.un.imports.JSONParser;
import org.un.myworld.data.sync.DB_Adapter;
import org.un.myworld.data.sync.Sync;


import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;


public class SavedVotesActivity extends Activity {
	
	
	static final String TAG="SavedVotesActivity" ;
	public static String today;
	public static ProgressBar progressSpinIcon;
	public static TextView textSectionDescription,textUploadProcessLabel,textVotesNotSent,textPartnerID,textCanvasserCountry;
	public static Button btnSyncVotes,btnBack;
    private DB_Adapter db;
    public static String [] COUNTRY_NAME;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		//language config
		//Preferences.configureLanguage(this);
		
		//initialize sharePrefs variable
		Preferences.sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.saved_votes);
		setTheme(android.R.style.Theme);
		
		COUNTRY_NAME=this.getResources().getStringArray(R.array.countries_un);
	
		progressSpinIcon=(ProgressBar)findViewById(R.id.progressIcon);
		textUploadProcessLabel=(TextView)findViewById(R.id.section_votes_upload_process);
		textVotesNotSent=(TextView)findViewById(R.id.section_votes_not_sent);
		textSectionDescription=(TextView)findViewById(R.id.section_description);
		textPartnerID=(TextView)findViewById(R.id.parterid_label);
		textCanvasserCountry=(TextView)findViewById(R.id.partercountry_label);
		btnSyncVotes=(Button)findViewById(R.id.save_votes);
		btnBack=(Button)findViewById(R.id.back_to_main);
		
		db=new DB_Adapter(getApplicationContext());
		db.open(); //open db
		Calendar now = Calendar.getInstance();   // This gets the current date and time.
		SavedVotesActivity.today= now.get(Calendar.DATE)+"/"+(now.get((Calendar.MONTH))+1)+"/"+now.get(Calendar.YEAR); //get current date
		Log.i(TAG,"Date: "+today);
		textUploadProcessLabel.setText(getString(R.string.saved_votes_upload_process_label,String.valueOf(db.getTotalVotes())));
		textVotesNotSent.setText(getString(R.string.saved_votes_not_sent_count_label,SavedVotesActivity.today,db.getTotalVotes()));
		textPartnerID.setText(getString(R.string.saved_votes_partner_id_label,Preferences.sharedPrefs.getString(Preferences.KEY_PARTNER_ID_EDIT_TEXT_PREFERENCE, null).trim()));
		textCanvasserCountry.setText(getString(R.string.saved_votes_partner_country_label,COUNTRY_NAME[Integer.valueOf(Preferences.sharedPrefs.getString(Preferences.KEY_COUNTRY_LIST_PREFERENCE, null))-1]));
		
		if(db.getTotalVotes()==0){//hide sync button on zero votes found
			btnSyncVotes.setVisibility(View.GONE);
		}
		db.close(); //close db
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}
	
	@Override
	public void onBackPressed(){
		//check if sync is in progress and disallow the navigation
	}
	
	
		//land user on the home screen
		public void btnBack_Click(View view){
			
			Intent intentHome = new Intent(getApplicationContext(), HomeActivity.class);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intentHome);
			
			//start sync service
			//startService(new Intent(FinishVoteActivity.this,Sync.class));
        	//Log.d(TAG, "Sync Service Started");
			
			//close current activity
			finish();
			}
		
		//start the sync service
		public void btnSyncVotes_Click(View view){
			
			//textVotesNotSent.setVisibility(View.GONE);
			//textSectionDescription.setVisibility(View.GONE);
			//textUploadProcessLabel.setVisibility(View.VISIBLE);
			//progressSpinIcon.setVisibility(View.VISIBLE);
			//btnSyncVotes.setVisibility(View.GONE);
			//btnBack.setEnabled(false);
			
			//start sync service
			startService(new Intent(SavedVotesActivity.this,Sync.class));
			
			Intent intentSendingVote = new Intent(getApplicationContext(), SendingVoteActivity.class);
			intentSendingVote.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intentSendingVote);
			
			finish();
			/*if(JSONParser.sync_done==true){
				//undo the unhide or hide views
				SavedVotesActivity.textVotesNotSent.setVisibility(View.VISIBLE);
				SavedVotesActivity.textSectionDescription.setVisibility(View.VISIBLE);
				SavedVotesActivity.textUploadProcessLabel.setVisibility(View.GONE);
				SavedVotesActivity.progressSpinIcon.setVisibility(View.GONE);
				SavedVotesActivity.btnBack.setEnabled(true);
				
				textVotesNotSent.setText(getString(R.string.saved_votes_not_sent_count_label,SavedVotesActivity.today,db.getTotalVotes()));
			}*/
        	//Log.d(TAG, "Sync Service Started");
			
			//close current activity
			//finish();
			}

}
