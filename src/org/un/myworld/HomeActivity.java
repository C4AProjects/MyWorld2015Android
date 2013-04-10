package org.un.myworld;


import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class HomeActivity extends Activity {
	//declarations
	static final int START_PREFERENCES_REQUEST = 200,START_ANY_ACTIVITY_REQUEST=450,RESULT_CLOSE_ALL_ACTIVITY=1000;
	
	static final String TAG="HomeActivity";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	//language config
    	Preferences.sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    	Preferences.languagePrefix=Preferences.sharedPrefs.getString(Preferences.KEY_LANGUAGE_LIST_PREFERENCE, "");
    	Preferences.configureLanguage(this);
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        
        
        Preferences.edit = Preferences.sharedPrefs.edit();
       
        Log.i(TAG,"Country config: "+Preferences.sharedPrefs.getString("country", null));
        Log.i(TAG,"PartnerID config: "+Preferences.sharedPrefs.getString("partID", null));
        
        //check if partner info has already been set before
        if(Preferences.sharedPrefs.getString("country", null)=="" || Preferences.sharedPrefs.getString("partID", null)=="" ||Preferences.sharedPrefs.getString("country", null)==null || Preferences.sharedPrefs.getString("partID", null)==null){
        	 Log.i(TAG,"Settings Found: FALSE ");
        	settingsAlert();
        	
        }/*else{
        	Log.i(TAG,"Settings Found: TRUE ");
        	//changeSettingsAlert();
        }*/
        
    }
    
    private void settingsAlert() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.pref_partner_info_reminder)
				.setCancelable(false)
				.setIcon(R.drawable.info)
				.setPositiveButton(R.string.pref_partner_info_set_button,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
								//start settings window
								Intent i = new Intent(getApplicationContext(), Preferences.class);
								startActivityForResult(i,START_PREFERENCES_REQUEST);
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}
    
    private void changeSettingsAlert() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.pref_partner_info_change)
				.setCancelable(false)
				.setIcon(R.drawable.info)
				.setPositiveButton(R.string.pref_partner_info_change_button,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								//open settings window
								Intent i = new Intent(getApplicationContext(), Preferences.class);
								startActivityForResult(i,START_PREFERENCES_REQUEST);
							}
						});
		builder.setNegativeButton(R.string.pref_partner_info_change_ignore_button,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }
    
   
    
  /*  @Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflate = new MenuInflater(this);
		inflate.inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}*/

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {//start different activities based on the selected menu item
		switch (item.getItemId()) {
		case R.id.menu_settings:
			Intent i = new Intent(this, Preferences.class);
			startActivityForResult(i,START_PREFERENCES_REQUEST);
			break;

		default:
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
    
  //go to the voting activity
  	public void btnBeginVote_Click(View view)
  	{
  		/**/if(Preferences.sharedPrefs.getString("partID", null)==null || Preferences.sharedPrefs.getString("country", null)==null || Preferences.sharedPrefs.getString("partID", null)=="" || Preferences.sharedPrefs.getString("country", null)==""){
  			settingsAlert();
  		}else{
  		//start the vote entry activity
  		Intent vote = new Intent(getApplicationContext(), VotesListAdapter.class);
		vote.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(vote,START_ANY_ACTIVITY_REQUEST);
  		}
		//close the home activity
		finish();
  	}
  	
  //go to the saved votes activity
  	public void btnSavedVotes_Click(View view)
  	{
  		/**/if(Preferences.sharedPrefs.getString("partID", null)==null || Preferences.sharedPrefs.getString("country", null)==null ||Preferences.sharedPrefs.getString("partID", null)=="" || Preferences.sharedPrefs.getString("country", null)==""){
  			Intent i = new Intent(this, Preferences.class);
  			startActivityForResult(i,START_PREFERENCES_REQUEST);
  		}else{
  		//start the savedvotes activity
  	  		Intent intentSavedVote = new Intent(getApplicationContext(), SavedVotesActivity.class);
  	  		intentSavedVote.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
  			//startActivity(vote);
  			startActivityForResult(intentSavedVote,START_ANY_ACTIVITY_REQUEST);
  		}
  		

			//close the home activity
			finish();
  	}
  	
  	//open settings window
  	public void btnSettings_Click(View view){
  		Intent i = new Intent(this, Preferences.class);
		startActivityForResult(i,START_PREFERENCES_REQUEST);
  	}
  	
  //close the application completely
  	public void btnExitApp_Click(View view){
  		setResult(RESULT_CLOSE_ALL_ACTIVITY);
	    finish();
  	}
  	
  	@Override
  	public void onBackPressed(){
  		//ignore the back button
  	}
  	
  	
  	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
  	    switch(resultCode)
  	    {
  	    case HomeActivity.RESULT_CLOSE_ALL_ACTIVITY:
  	        setResult(HomeActivity.RESULT_CLOSE_ALL_ACTIVITY);
  	        finish();
  	    }
  	    super.onActivityResult(requestCode, resultCode, data);
  	}

    
}
