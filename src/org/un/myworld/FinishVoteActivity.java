/**
 * @author JohnAdamsy
 * @modified March 10th 2013 1506H EAT
 * @description Class stores user votes to the local db. Takes user to the next window (Add more/go to main)
 * */
package org.un.myworld;

import java.util.Calendar;
import java.util.HashMap;

import org.un.myworld.data.sync.DB_Adapter;


import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;


public class FinishVoteActivity extends Activity {
	
	//UI elements
	Spinner spinnerAge, spinnerGender,spinnerCountry,spinnerEducation;
	AutoCompleteTextView autoCompleteCountry;
	EditText txtPartnerID;
	TextView errorLabel;
	ArrayAdapter<CharSequence> adapter;
	HashMap<String, String> selectedPriorities;
	String suggestedPriority,corePriorities;
	DB_Adapter db;
	Intent priority_page;
	static final String TAG="FinishVoteActivity" ;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		//language config
		//Preferences.configureLanguage(this);
		
		//initialize sharePrefs variable
		Preferences.sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.voter_info);
		setTheme(android.R.style.Theme);
		
		//Initialize UI elements from layout
		spinnerAge=(Spinner)findViewById(R.id.spinner_age);
		spinnerGender=(Spinner)findViewById(R.id.spinner_gender);
		//spinnerCountry=(Spinner)findViewById(R.id.spinner_country);
		spinnerEducation=(Spinner)findViewById(R.id.spinner_education);
		//txtPartnerID=(EditText)findViewById(R.id.editText_PartnerID);
		errorLabel=(TextView)findViewById(R.id.validation_status);
		//autoCompleteCountry=(AutoCompleteTextView)findViewById(R.id.autocomplete_voter_country);
		
		//add listeners to the spinners
		spinnerAge.setOnItemSelectedListener(new OnClassSpinnerSelectedListener());
		spinnerGender.setOnItemSelectedListener(new OnClassSpinnerSelectedListener());
		//spinnerCountry.setOnItemSelectedListener(new OnClassSpinnerSelectedListener());
		spinnerEducation.setOnItemSelectedListener(new OnClassSpinnerSelectedListener());
		
		//populate spinners
		this.populateSpinner();
		
		//get data passed within the intent
		
		Intent priority_page=getIntent();
		
		
		selectedPriorities = (HashMap<String, String>)priority_page.getSerializableExtra("chosen_priorities");
		suggestedPriority=priority_page.getStringExtra("own_priority");
		//corePriorities=selectedPriorities;
		Log.i("User Priority: ",suggestedPriority);
		//Log.i("Chosen Priority 1: ",selectedPriorities.get("1"));
		//Log.i("Chosen Priority 2: ",selectedPriorities.get("2"));
		//Log.i("Chosen Priority 3: ",selectedPriorities.get("3"));
		//Log.i("Chosen Priority 4: ",selectedPriorities.get("4"));
		//Log.i("Chosen Priority 5: ",selectedPriorities.get("5"));
		//Log.i("Chosen Priority 6: ",selectedPriorities.get("6"));
		
		
		
		//System.out.println("Data: "+priority_page.getSerializableExtra("chosen_priorities"));
		
		//set country names
		//this.autoCompleteCountryNames();
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.finish_vote, menu);
		return true;
	}
	
	//method to populate the two spinners
	private void populateSpinner(){
		
		//Age
		// Create an ArrayAdapter using the string array and a default spinner layout
		adapter = ArrayAdapter.createFromResource(this,
		        R.array.age, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinnerAge.setAdapter(adapter);
		
	//gender
	// Create an ArrayAdapter using the string array and a default spinner layout
			adapter = ArrayAdapter.createFromResource(this,
			        R.array.gender, android.R.layout.simple_spinner_item);
			// Specify the layout to use when the list of choices appears
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// Apply the adapter to the spinner
			spinnerGender.setAdapter(adapter);
			
			//country
			// Create an ArrayAdapter using the string array and a default spinner layout
			/*adapter = ArrayAdapter.createFromResource(this,
			        R.array.countries_un, android.R.layout.simple_spinner_item);
			// Specify the layout to use when the list of choices appears
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// Apply the adapter to the spinner
			spinnerCountry.setAdapter(adapter);*/
			
			//education
			// Create an ArrayAdapter using the string array and a default spinner layout
			adapter = ArrayAdapter.createFromResource(this,
			        R.array.education_level, android.R.layout.simple_spinner_item);
			// Specify the layout to use when the list of choices appears
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// Apply the adapter to the spinner
			spinnerEducation.setAdapter(adapter);
	}
	
	private void autoCompleteCountryNames(){
		// Get the string array
		String[] countries = getResources().getStringArray(R.array.countries_un);
		// Create the adapter and set it to the AutoCompleteTextView 
		ArrayAdapter<String> adapter = 
		        new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, countries);
		autoCompleteCountry.setAdapter(adapter);
	}
	
	//inner class to register a listener to our spinner
		public class OnClassSpinnerSelectedListener implements OnItemSelectedListener {

		    public void onItemSelected(AdapterView<?> parent,
		        View view, int pos, long id) {
		     
		      if(pos!=0){
		     //do something
		      }
		    }

		    public void onNothingSelected(AdapterView parent) {
		      // Do nothing.
		    }
		}//end of the inner class OnClassSpinnerSelectedListener
		
		//land user on the thank you message page
		public void btnfinishVote_Click(View view){
			if(isNotBlank()==true){
			storeUserPrioritiesLocally();
			Intent intentSubmit = new Intent(getApplicationContext(), VotingCompleteActivity.class);
			intentSubmit.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(intentSubmit,HomeActivity.START_ANY_ACTIVITY_REQUEST);
			
			//close current activity
			//setResult(HomeActivity.RESULT_CLOSE_ALL_ACTIVITY);
			finish();
			}
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
		
		private void storeUserPrioritiesLocally(){
			
			double random_id=Math.random();
			
			String r_id=String.valueOf(random_id).substring(String.valueOf(random_id).lastIndexOf(".")+2, String.valueOf(random_id).length());
			
			//Date vote_date=new Date();
			
			Calendar now = Calendar.getInstance();   // This gets the current date and time.
			String vote_date=String.valueOf(now.get(Calendar.DAY_OF_MONTH)+"/"+(now.get((Calendar.MONTH))+1)+"/"+now.get(Calendar.YEAR)+" "
							  +now.get(Calendar.HOUR_OF_DAY)+":"+now.get(Calendar.MINUTE)+":"+now.get(Calendar.SECOND));
			//String vote_date=String.valueOf(now);
			
			
			//int DOB=now.get(Calendar.YEAR); //get current year in int
			
			//DOB=DOB-Integer.valueOf((spinnerAge.getSelectedItem().toString()));
			
			int DOB=Integer.valueOf((spinnerAge.getSelectedItem().toString()));
			
			//Toast.makeText(getApplicationContext(), "Random: "+r_id, Toast.LENGTH_LONG).show();
			
			Log.i("Random ID",""+r_id);
			Log.i(TAG,"Vote Timestamp: "+vote_date);
			
			
			db=new DB_Adapter(getApplicationContext());
			
			
			//get PartnerID from shared prefs file
			String partnerID=Preferences.sharedPrefs.getString(Preferences.KEY_PARTNER_ID_EDIT_TEXT_PREFERENCE, "");
			String partnerCountry=Preferences.sharedPrefs.getString(Preferences.KEY_COUNTRY_LIST_PREFERENCE, "");
			String city="n/a",region="n/a",latitude="n/a",longitude="n/a",reason="n/a";
			String corePriorityList="";
			String uniqID=r_id;
			String gender=String.valueOf(spinnerGender.getSelectedItemPosition());
			String education=String.valueOf(spinnerEducation.getSelectedItemPosition());
			
			//user chosen priorities
			for(int i=1;i<=6;i++){
				//db.save_priority(i, selectedPriorities.get('"'+i+'"'), null, r_id);
				if(i==6){
					corePriorityList+=selectedPriorities.get(String.valueOf(i));
				}else{
					corePriorityList+=selectedPriorities.get(String.valueOf(i))+",";
				}
			}
			Log.i(TAG,"CorePriorityList: "+corePriorityList);
			//vote-additional info --country will retrieved from the shared prefs
			
			//Toast.makeText(getApplicationContext(), "Option Code: "+selectedPriorities.get('"'+i+'"'), Toast.LENGTH_SHORT).show();
			db.save_prioritylist(Long.valueOf(uniqID), corePriorityList, suggestedPriority);
			db.save_vote(Long.valueOf(uniqID), Long.valueOf(uniqID), partnerID, partnerCountry, city, region, latitude, longitude, vote_date.toString(), gender, String.valueOf(DOB), education, Long.valueOf(uniqID), reason);
			//db.save_vote(r_id, spinnerCountry.getSelectedItem().toString(), null, null, null, null, suggestedPriority, vote_date.toString(), null, txtPartnerID.getText().toString().trim());
			
			//voter information
			//db.save_voter(r_id, spinnerGender.getSelectedItem().toString(), String.valueOf(DOB), spinnerEducation.getSelectedItem().toString(), null);		
		
		}
			//ensure user information has been provided
		private boolean isNotBlank(){
			/*if (txtPartnerID.getText().toString().trim().equalsIgnoreCase("")){
				errorLabel.setText(this.getResources().getString(R.string.partner_id_required));
				txtPartnerID.requestFocus();
				return false;
			}*/
			if (spinnerGender.getSelectedItemPosition()==0){
				errorLabel.setText(this.getResources().getString(R.string.gender_required));
				return false;
			}
			if (spinnerAge.getSelectedItemPosition()==0){
				errorLabel.setText(this.getResources().getString(R.string.age_required));
				spinnerGender.requestFocus();
				return false;
			}
		/*	if (spinnerCountry.getSelectedItemPosition()==0){
				errorLabel.setText(this.getResources().getString(R.string.country_required));
				spinnerCountry.requestFocus();
				return false;
			}*/
			
			if (spinnerEducation.getSelectedItemPosition()==0){
				errorLabel.setText(this.getResources().getString(R.string.education_required));
				spinnerEducation.requestFocus();
				return false;
			}
			return true;
		}
		
		@Override
		public void onBackPressed(){
			Intent intentBack = new Intent(getApplicationContext(), VotesListAdapter.class);
			intentBack.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
			startActivityForResult(intentBack,HomeActivity.START_ANY_ACTIVITY_REQUEST);
			finish();
		}

}
