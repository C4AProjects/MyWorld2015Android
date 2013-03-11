/**
 * @author JohnAdamsy
 * @modified March 10th 2013
 * @description Class displays the sending votes layout
 * */
package org.un.myworld;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import org.un.myworld.data.sync.DB_Adapter;
import org.un.myworld.data.sync.Sync;


import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;


public class SendingVoteActivity extends Activity {
	
	
	static final String TAG="SendingVotesActivity" ;
	public static String today;
	public static ProgressBar progressSpinIcon;
	public static TextView textSectionDescription,textUploadProcessLabel,textVotesNotSent,textPartnerID,textCanvasserCountry;
	public static Button btnSyncVotes,btnBack;
    private DB_Adapter db;
    public static String [] COUNTRY_NAME;
    int count;
    Timer timer=new Timer();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		//language config
		//Preferences.configureLanguage(this);
		
		//initialize sharePrefs variable
		Preferences.sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sending_vote);
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
		
		Calendar now = Calendar.getInstance();   // This gets the current date and time.
		SendingVoteActivity.today= now.get(Calendar.DATE)+"/"+(now.get(Calendar.MONTH)+1)+"/"+now.get(Calendar.YEAR); //get current date
		Log.i(TAG,"Date: "+today);
		textUploadProcessLabel.setText(getString(R.string.saved_votes_upload_process_label,String.valueOf(db.getTotalVotes())));
		textVotesNotSent.setText(getString(R.string.saved_votes_not_sent_count_label,SendingVoteActivity.today,db.getTotalVotes()));
		textPartnerID.setText(getString(R.string.saved_votes_partner_id_label,Preferences.sharedPrefs.getString(Preferences.KEY_PARTNER_ID_EDIT_TEXT_PREFERENCE, null).trim()));
		textCanvasserCountry.setText(getString(R.string.saved_votes_partner_country_label,COUNTRY_NAME[Integer.valueOf(Preferences.sharedPrefs.getString(Preferences.KEY_COUNTRY_LIST_PREFERENCE, null))-1]));
		
		if(db.getTotalVotes()==0){//hide sync button on zero votes found
			btnSyncVotes.setVisibility(View.GONE);
		}
		
		
		if(this.is_connected()){//check connectivity availability
		int UPDATE_INTERVAL=db.getTotalVotes()*1000;
		int DELAY=2000;
		count=db.getTotalVotes();
		
		
		
		if(UPDATE_INTERVAL==1000){//increase delay for a single vote
			UPDATE_INTERVAL=UPDATE_INTERVAL*2;
			DELAY=DELAY*2;
		}
		
		//some hacked way of keeping the progress while the sync is ongoing
		timer.scheduleAtFixedRate(new TimerTask(){
    		public void run(){
    			count--;
    			progressSpinIcon.setVisibility(View.VISIBLE);
    			
    			if(count==0){
    				timer.cancel();
    				Log.i(TAG,"Timer Cancelled");
    				
    				//go back to the saved votes activity
    				Intent intentSavedVotes = new Intent(getApplicationContext(), SavedVotesActivity.class);
    				intentSavedVotes.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    				startActivity(intentSavedVotes); 
    				
    				finish();//close this activity
    				
    			
    			}
    		}
    	}, DELAY, UPDATE_INTERVAL+1500);
		
		}else{
			progressSpinIcon.setVisibility(View.VISIBLE);
			textUploadProcessLabel.setText(R.string.saved_votes_no_internet_reminder);
			//this.showDataSettings();
			btnBack.setEnabled(false);
		}
	}
	
	//open the native Network and Connections Settings panel
  	private void showDataSettings() {
  		//Intent intentDataSettings = new Intent(Intent.ACTION_MAIN);
  		//intentDataSettings.setClassName("com.android.phone", "com.android.phone.NetworkSetting");
  		Intent intentDataSettings=new Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS);

  		ComponentName cName = new ComponentName("com.android.phone","com.android.phone.Settings");

  		intentDataSettings.setComponent(cName);
  		startActivity(intentDataSettings);
  	}
	
	/*Boolean method to check if internet is available*/
	public boolean is_connected(){
		ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo active_mobile_connection = connectivityManager.getActiveNetworkInfo();
		return active_mobile_connection != null && active_mobile_connection.isConnectedOrConnecting();
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
			
			textVotesNotSent.setVisibility(View.GONE);
			textSectionDescription.setVisibility(View.GONE);
			textUploadProcessLabel.setVisibility(View.VISIBLE);
			progressSpinIcon.setVisibility(View.VISIBLE);
			btnSyncVotes.setVisibility(View.GONE);
			btnBack.setEnabled(false);
			//start sync service
			startService(new Intent(SendingVoteActivity.this,Sync.class));
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
