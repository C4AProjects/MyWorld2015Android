/**
 * @author JohnAdamsy
 * @modified March 19th 2013
 * @description Class displays the saved_votes layout
 * */
package org.un.myworld;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;


import org.un.myworld.data.sync.DB_Adapter;
import org.un.myworld.data.sync.Sync;


import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;



public class SavedVotesActivity extends Activity {
	
	
	static final String TAG="SavedVotesActivity" ;
	public static String today,intentErrorContentFromExtra="NaN";
	public static int intentVotesCountFromExtra=-1;
	public static ProgressBar progressSpinIcon;
	public static TextView textSectionDescription,textUploadProcessLabel,textVotesNotSent,textPartnerID,textCanvasserCountry,textSendVoteEmphasis;
	public static Button btnSyncVotes,btnBack;
    private DB_Adapter db;
    public static String [] COUNTRY_NAME;
    public static String [] COLUMN_NAMES;
    public static boolean votesBeenSend=false;
    private boolean exportComplete;
    IntentFilter intentFilter; //to receive service intent completion intents
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		//language config
		Preferences.configureLanguage(this);
		
		//initialize sharePrefs variable
		Preferences.sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.saved_votes);
		setTheme(android.R.style.Theme);
		
		
		//---intent to filter for the broadcast intent---
		intentFilter = new IntentFilter();
		intentFilter.addAction("VOTES_UPLOAD_ACTION");
		//---register the receiver---
		registerReceiver(intentReceiver, intentFilter);
		
		COUNTRY_NAME=this.getResources().getStringArray(R.array.countries_un);
		COLUMN_NAMES=new String[]{"VoteID","PartnerID","Country","Gender","Age","Votes","SuggestedPriority","DateCollected"};
	
		progressSpinIcon=(ProgressBar)findViewById(R.id.progressIcon);
		textUploadProcessLabel=(TextView)findViewById(R.id.section_votes_upload_process);
		textVotesNotSent=(TextView)findViewById(R.id.section_votes_not_sent);
		textSendVoteEmphasis=(TextView)findViewById(R.id.section_send_vote_emphasis);
		textSectionDescription=(TextView)findViewById(R.id.section_description);
		textPartnerID=(TextView)findViewById(R.id.parterid_label);
		textCanvasserCountry=(TextView)findViewById(R.id.partercountry_label);
		btnSyncVotes=(Button)findViewById(R.id.save_votes);
		btnBack=(Button)findViewById(R.id.back_to_main);
		
		db=new DB_Adapter(getApplicationContext());
		db.open(); //open db
		Calendar now = Calendar.getInstance();   // This gets the current date and time.
		SavedVotesActivity.today= now.get(Calendar.DAY_OF_MONTH)+"/"+(now.get((Calendar.MONTH))+1)+"/"+now.get(Calendar.YEAR); //get current date
		Log.i(TAG,"Date: "+today);
		textUploadProcessLabel.setText(getString(R.string.saved_votes_upload_process_label,String.valueOf(db.getTotalVotes())));
		textVotesNotSent.setText(getString(R.string.saved_votes_not_sent_count_label,SavedVotesActivity.today,db.getTotalVotes()));
		if(Preferences.sharedPrefs.getString(Preferences.KEY_PARTNER_ID_EDIT_TEXT_PREFERENCE, null).trim()!=null && Preferences.sharedPrefs.getString(Preferences.KEY_COUNTRY_LIST_PREFERENCE, null)!=null){
		textPartnerID.setText(getString(R.string.saved_votes_partner_id_label,Preferences.sharedPrefs.getString(Preferences.KEY_PARTNER_ID_EDIT_TEXT_PREFERENCE, null).trim()));
		textCanvasserCountry.setText(getString(R.string.saved_votes_partner_country_label,COUNTRY_NAME[Integer.valueOf(Preferences.sharedPrefs.getString(Preferences.KEY_COUNTRY_LIST_PREFERENCE, null))-1]));
		}
		
		//display alert dialog
		if(votesBeenSend==true){
			if(intentErrorContentFromExtra.equals("OK")){
				//display success alert
				Log.i(TAG,"Success!");
				uploadSuccessAlert(this,intentVotesCountFromExtra);
				}else{
				//display error alert
					Log.i(TAG,"Error!");
			        uploadErrorAlert(this);
			}
		}
		
		//reset votesBeenSend
		votesBeenSend=false;
		
		
		if(db.getTotalVotes()==0){//hide sync button on zero votes found
			btnSyncVotes.setVisibility(View.GONE);
		}
		db.close(); //close db
	}

	//broad cast receiver to listen to the IntentService
	private BroadcastReceiver intentReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "IntentService complete");
		
		//and start a fresh copy
		//restart the savedvotes activity
		//finish();
  		Intent intentSavedVote = new Intent(getBaseContext(), SavedVotesActivity.class);
  		intentSavedVote.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		intentErrorContentFromExtra=intent.getStringExtra("Error").toString();
		intentVotesCountFromExtra=Integer.valueOf(intent.getIntExtra("Sent_Vote_Count", 0));
		votesBeenSend=true;
		startActivity(intentSavedVote);
		
		//Log.i(TAG,"Extra: "+intent.getIntExtra("Sent_Vote_Count", 0));
		//Log.i(TAG,"Extra: "+intent.getStringExtra("Error"));
		
		
		}
		};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.saved_votes, menu);
		return true;
	}
	
	@Override //to know which menu item was clicked
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_home:
			//setResult(RESULT_CANCELED);
			Intent intentBackHome = new Intent(getApplicationContext(), HomeActivity.class);
	  		intentBackHome.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivityForResult(intentBackHome,HomeActivity.START_ANY_ACTIVITY_REQUEST);
			break;
		case R.id.menu_settings:			
			Intent i = new Intent(this, Preferences.class);
			startActivityForResult(i,HomeActivity.START_PREFERENCES_REQUEST);
			break;

		default:
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	@Override
	public void onBackPressed(){
		//check if sync is in progress and disallow the navigation
	}
	
	//unregister the intent receiver since we don't need it in this state
	public void onPause(){
		if(intentReceiver!=null){
			unregisterReceiver(intentReceiver);
			intentReceiver=null;
		}
		
		super.onPause();
	}
	
	//unregister the intent receiver since we don't need it in this state
	public void onDestroy(){
		if(intentReceiver!=null){
		unregisterReceiver(intentReceiver);
		intentReceiver=null;
		}
		super.onResume();
	}
	
		//land user on the home screen
		public void btnBack_Click(View view){
			
			Intent intentHome = new Intent(getApplicationContext(), HomeActivity.class);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intentHome);
			
			//close current activity
			finish();
			}
		
		//start the sync service
		public void btnSyncVotes_Click(View view){
			
			//check if there is net
			if(this.is_connected()){
				//start sync service
				startService(new Intent(SavedVotesActivity.this,Sync.class));
				textVotesNotSent.setVisibility(View.GONE);
				textSendVoteEmphasis.setVisibility(View.GONE);
				textSectionDescription.setVisibility(View.GONE);
				textUploadProcessLabel.setVisibility(View.VISIBLE);
				db.open(); //open db
				textUploadProcessLabel.setText(getString(R.string.saved_votes_upload_process_label,String.valueOf(db.getTotalVotes())));
				db.close();
				progressSpinIcon.setVisibility(View.VISIBLE);
				btnSyncVotes.setVisibility(View.GONE);
				btnBack.setEnabled(false);
			}else{
				//inform the user of the missing net
				progressSpinIcon.setVisibility(View.VISIBLE);
				textUploadProcessLabel.setVisibility(View.VISIBLE);
				textUploadProcessLabel.setText(R.string.saved_votes_no_internet_reminder);
				textVotesNotSent.setVisibility(View.GONE);
				textSendVoteEmphasis.setVisibility(View.GONE);
				textSectionDescription.setVisibility(View.GONE);
				progressSpinIcon.setVisibility(View.VISIBLE);
				btnSyncVotes.setVisibility(View.VISIBLE);
				//this.showDataSettings();
				btnBack.setEnabled(true);
			}
			
			}
		
		//initiate the export procedure and attempt an email intent
		public void btnEmailVotes_Click(View view){
			//initiate csv export procedure
			new DoAttemptExportVotesToCSV().execute();
		}
		
		/**
		 * @desscription -an async task to help with the votes export and eventually email
		 * @author John Adamsy
		 * */
		/*class DoAttemptExportVotesToCSV: defines an asynchronous task*/
	    private class DoAttemptExportVotesToCSV extends AsyncTask<Void, Integer, Integer>{/*our methods will need these types specified here as params*/
	    	
	    	/**
			 * Before starting background thread Start the sending vote activity
			 * */
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				Toast.makeText(getBaseContext(), "Initiating process...", Toast.LENGTH_LONG).show();
			}
	    	
	    	@Override
	    	protected Integer doInBackground(Void... sendJSON){
	    		Calendar now=Calendar.getInstance();
	    		String fileName="MyWorld_data_"+now.get(Calendar.DAY_OF_MONTH)+"_"+(now.get(Calendar.MONTH)+1)+"_"+now.get(Calendar.YEAR)+"_"+now.get(Calendar.HOUR_OF_DAY)+""+now.get(Calendar.MINUTE)+""+now.get(Calendar.SECOND)+".csv";
	    		exportDataToCSV(fileName);
	    		
	    		return 0;
	    	}
	    	
	    	@Override
	    	 protected void onProgressUpdate(Integer... progress){
	    	    	Log.i(TAG, String.valueOf(progress[0])+"% complete");
	    	    	
	    	    	//Toast.makeText(getBaseContext(), String.valueOf(progress[0])+"% complete", Toast.LENGTH_LONG).show();
	    	    }
	    	 
	    	@Override
	    	 protected void onPostExecute(Integer result){
	    	    	Toast.makeText(getBaseContext(), "Export Complete", Toast.LENGTH_LONG).show();
	    	    	//attempt to start the email intent here
	    	    	
	    	    }
	    	
	    	private Boolean exportDataToCSV(String outPutFile) {
	    	    Log.i(TAG, "CSV Export");
	    	    exportComplete = false;
	    	    int i = 0;
	    	    String csvHeader = "";
	    	    String csvValues = "";
	    	    for (i = 0; i < COLUMN_NAMES.length; i++) {
	    	        if (csvHeader.length() > 0) {
	    	            csvHeader += ",";
	    	        }
	    	        csvHeader += "\"" + COLUMN_NAMES[i] + "\"";
	    	    }

	    	    csvHeader += "\n";
	    	    Log.i(TAG, "header=" + csvHeader);
	    	    db.open();
	    	    
	    	        File sdDir = Environment.getExternalStorageDirectory();
	    	        if(sdDir.exists() && sdDir.canWrite()){
	    	        	File myDir=new File(sdDir.getAbsolutePath()+"/myworld");
	    	        	myDir.mkdir(); //create dir 
	    	        	if(myDir.exists() && myDir.canWrite()){
	    	        		File file=new File(myDir.getAbsolutePath()+"/"+outPutFile);
	    	        	    try{
	    	        	    	file.createNewFile();
	    	        	    } catch (IOException e) {
	    	        	    	exportComplete = false;
	    		    	        Log.i(TAG, "IOException: " + e.getMessage());
	    	        	    	}
	    	        	    if(file.exists() & file.canWrite()){
	    	        	    	try{
	    	        	    	FileWriter csvWriter = new FileWriter(file);
	    		    	        BufferedWriter outStream = new BufferedWriter(csvWriter);
	    		    	        Cursor cursor = db.getCSVFriendlyResultSet("N","Y");
	    		    	        if (cursor != null) {
	    		    	        	outStream.write(csvHeader);
	    		    	            while (cursor.moveToNext()) {
	    		    	                csvValues = Long.toString(cursor.getLong(0)) + ",";//voteid
	    		    	                csvValues += cursor.getString(1)//parnterID
	    		    	                        + ",";
	    		    	                csvValues += cursor.getInt(2)//country
	    		    	                        + ",";
	    		    	                csvValues += cursor.getInt(4)//gender
	    		    	                        + ",";
	    		    	                csvValues += cursor.getInt(3)//age
	    		    	                        + ",";
	    		    	                csvValues += "\"" + cursor.getString(5) + "\",";//core priorities
	    		    	                csvValues += "\"" + cursor.getString(6) + "\",";//suggested priority
	    		    	                csvValues += "\"" + cursor.getString(7) + "\","//voted date
	    		    	                        + "\n";
	    		    	                outStream.write(csvValues);
	    		    	            }
	    		    	            cursor.close();
	    		    	        }
	    		    	        outStream.close();
	    		    	        exportComplete = true;
	    		    	    } catch (IOException e) {
	    		    	    	exportComplete = false;
	    		    	        Log.i(TAG, "IOException: " + e.getMessage());
	    		    	    }
	    		    	   
	    	        	    }//close if file.exists
	    	        	}//close if mydir exists
	    	        }// if sd card exists
	    	        db.close();
		    	    return exportComplete;
	    	}//close method 
	    	        
	    	
	    }/*end of inner class DoAttemptExportVotesToCSV*/
		
		public boolean is_connected(){
			ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo active_mobile_connection = connectivityManager.getActiveNetworkInfo();
			return active_mobile_connection != null && active_mobile_connection.isConnectedOrConnecting();
		}
		
		//upload error alert dialog
    	private void uploadErrorAlert(Context context) {
    	/**/	AlertDialog.Builder builder_error = new AlertDialog.Builder(context);
    		builder_error.setMessage(R.string.alert_dialog_upload_error_msg)
    				.setCancelable(false)
    				.setIcon(R.drawable.info)
    				.setTitle(R.string.alert_dialog_upload_error_title)
    				.setPositiveButton(R.string.pref_partner_info_set,
    						new DialogInterface.OnClickListener() {
    							public void onClick(DialogInterface dialog_error, int id) {
    								dialog_error.cancel();
    								
    							}
    						});
    		AlertDialog alert_error = builder_error.create();
    		alert_error.show();
    	}
    	
    	//upload success alert dialog
    	private void uploadSuccessAlert(Context context,int num_of_votes) {
    		AlertDialog.Builder builder_success = new AlertDialog.Builder(context);
    		builder_success.setMessage(getString(R.string.alert_dialog_upload_success_msg,num_of_votes))
    				.setCancelable(false)
    				.setIcon(R.drawable.info)
    				.setTitle(R.string.alert_dialog_upload_sucess_title)
    				.setPositiveButton(R.string.pref_partner_info_set,
    						new DialogInterface.OnClickListener() {
    							public void onClick(DialogInterface dialog_success, int id) {
    								dialog_success.cancel();	
    							}
    						});
    		AlertDialog alert_success = builder_success.create();
    		alert_success.show();
    	}

}
