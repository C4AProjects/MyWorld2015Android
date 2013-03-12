package org.un.myworld;

import org.un.myworld.data.sync.DB_Adapter;
import org.un.myworld.data.sync.Sync;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class VotingCompleteActivity extends Activity {

	private static final String TAG="VotingCompleteActivity";
	public static TextView textVotestoSyncCount;
	private DB_Adapter db;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		//language config
    	//Preferences.configureLanguage(this);
    	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_finish_vote);
		
		
		db=new DB_Adapter(this);
		//get vote count
		db.open();
		textVotestoSyncCount=(TextView)findViewById(R.id.votes_count_label);
		textVotestoSyncCount.setText(getString(R.string.text_votes_count_label,String.valueOf(db.getTotalVotes())));
		db.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.voting_complete, menu);
		return true;
	}
	
	//go back to main
	public void btnHome_clicked(View v){
		Intent intentHome = new Intent(getApplicationContext(), HomeActivity.class);
		intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(intentHome,HomeActivity.START_ANY_ACTIVITY_REQUEST);
		Log.i(TAG,"Result: "+HomeActivity.START_ANY_ACTIVITY_REQUEST);
		finish();
		//startService(new Intent(VotingCompleteActivity.this,Sync.class));
    	//Log.d(TAG, "Upload Votes Clicked");
	}
	
	//back to the voting page
	public void btnAdd_clicked(View view){
		Intent intentNewVote = new Intent(getApplicationContext(), VotesListAdapter.class);
		intentNewVote.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(intentNewVote,HomeActivity.START_ANY_ACTIVITY_REQUEST);
		
		finish();
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
