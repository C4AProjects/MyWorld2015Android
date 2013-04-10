package org.un.myworld;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.un.imports.JSONParser;
import org.un.imports.ModelListItem;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class VotingActivity extends Activity{
	
	// Progress Dialog
	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
	JSONArray option=null;
	
	ArrayList<HashMap<String, String>> optionList;
	
	private static final String GET_URL = "http://10.0.2.2/EventsWallet/getEventsJSON.php";
	//private static final String EVENT_URL = "http://nibomba.com/iwapi/dataservice/";
	private static String service_type="options";
	
	
	//--json keys
    static final String TAG_OPTION = "option"; // the json object
	static final String KEY_CODE = "optionCode";
	static final String KEY_TITLE = "optionTitle";
	static final String KEY_DESC = "optionDescription";
	static final String KEY_COLOR = "optionColor";
	
	static String userVotes[]=new String[6]; //array to hold user's priorities
	static int countVotes=0; //we need a maximum of 6 votes
	
	
	ListView listView;//the options list
	
	List<ModelListItem> listModel = new ArrayList<ModelListItem>();
	MyworldCustomListAdapter adapter; //customized adapter
	RelativeLayout lowerBar;
	TextView voteInstruction,text_code,text_title,text_color,text_desc;
	EditText userPriority; static Button nextButton;
	ImageView imgMDGColor;
	CheckBox mdgCheck;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		//language config
    	Preferences.configureLanguage(this);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vote);
		
		//Initialize hashmap to hold the options
		optionList=new ArrayList<HashMap<String, String>>();
		
		lowerBar=(RelativeLayout)findViewById(R.id.lower_bar);
		voteInstruction=(TextView)findViewById(R.id.vote_instructions);
		userPriority=(EditText)findViewById(R.id.add_option);
		nextButton=(Button)findViewById(R.id.finishVote);
		
		
		new LoadVotingOptions().execute();
		
		
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.voting, menu);
		return true;
	}
	
	/**
	 * Background Async Task to construct our UI
	 * */
	class LoadVotingOptions extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(VotingActivity.this);
			pDialog.setMessage("Preparing ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting Options JSON
		 * */
		@Override
		protected String doInBackground(String... args) {
			
			// Building Parameters
			/*List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("service", service_type));
						
			// getting JSON string from URL
			JSONObject json = jsonParser.makeHttpRequest(GET_URL, "GET",params);*/
			
			// getting JSON string from URL
			JSONObject json = jsonParser.getJSONFromAssets("options.json", getApplicationContext());
			

			// Check your log cat for JSON reponse
			Log.d("Options JSON: ", json.toString());

			try {
				option  = json.getJSONArray(TAG_OPTION);
				// looping through options found
				
				//are there any options?
				if(option.length()>0){
					Log.d("Options JSON: ", option.length()+" options found");
				for (int i = 0; i < option.length(); i++) {
					JSONObject c = option.getJSONObject(i);

					// Storing each json item in variable
					String id = c.getString(KEY_CODE);
					String title = c.getString(KEY_TITLE);
					String descr = c.getString(KEY_DESC);
					String color = c.getString(KEY_COLOR);
					


				// creating new HashMap
				HashMap<String, String> map = new HashMap<String, String>();

				// adding each child node to HashMap key => value
				map.put(KEY_CODE, id);
				map.put(KEY_TITLE, title);
				map.put(KEY_DESC, descr);
				map.put(KEY_COLOR, color);

					// adding HashMap to ArrayList
					optionList.add(map);
					
					//add items to the model, then add model to the list
					listModel.add(new ModelListItem(map.get(KEY_TITLE),map.get(KEY_DESC),map.get(KEY_CODE),map.get(KEY_COLOR)));
				}
				
				}//close 
				else{
					Log.d("Events JSON: ", "0 events found");	
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		@Override
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after rendering UI
			pDialog.dismiss();
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed JSON data into ListView
					 * */
				//	listView=(ListView)findViewById(R.id.list);
					//mdgCheck=(CheckBox)findViewById(R.id.mdg_check);
					
					// Getting adapter by passing xml data ArrayList
			       // adapter=new MyworldCustomListAdapter(VotingActivity.this, optionList);     
					adapter=new MyworldCustomListAdapter(VotingActivity.this, listModel);
			        listView.setAdapter(adapter);
			       

			        // Click event for single list row
			       listView.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent, View v,
								int position, long id) {
							text_code = (TextView) v.getTag(R.id.option_code);
							text_title=(TextView) v.getTag(R.id.mdg_title);
							//text_color=(TextView) v.getTag(R.id.option_color);
							text_desc=(TextView) v.getTag(R.id.mdg_description);
							mdgCheck = (CheckBox) v.getTag(R.id.mdg_check);
							imgMDGColor=(ImageView)v.getTag(R.id.option_color);
							//Toast.makeText(v.getContext(), text_code.getText().toString()+" "+isCheckedOrNot(mdgCheck), Toast.LENGTH_LONG).show();
							//isCheckedOrNot(mdgCheck);


							//hide or show description
							if (text_desc.getVisibility() == View.VISIBLE) {
								//hide the text
								text_desc.setVisibility(View.GONE);
							}else {
								//show
								text_desc.setVisibility(View.VISIBLE);
							}
							
							if(VotingActivity.countVotes==6){
								mdgCheck.setSelected(false);
							}
							

							int pos=listView.getPositionForView(mdgCheck);
							if(pos !=ListView.INVALID_POSITION){
							if(mdgCheck.isChecked()){
								Log.i("Voting Activity","Checked");
							}else{
								Log.i("Voting Activity","unChecked");
							}
							}
							

						}
						
						
					});
			       
			      
			        
			        //display other items
			        if(lowerBar.getVisibility()==View.GONE){lowerBar.setVisibility(View.VISIBLE);}
			        if(voteInstruction.getVisibility()==View.GONE){voteInstruction.setVisibility(View.VISIBLE);}
				}
			});
			

		}
		
		

	}//end of background thread


	 //go to the voting activity
  	public void btnFinishVote_Click(View view)
  	{
  		Intent vote = new Intent(getApplicationContext(), FinishVoteActivity.class);
  		
  		//priorities must be =6
  		
  		Bundle b=new Bundle(); 
  		//get user selected priorities pass the data to the user info intent

      /*  for(int i=0; i<listModel.size();i++){
        	if(listModel.get(i).isSelected()==true){
        		Log.i("Priority: "+1,"Code"+listModel.get(i).getOptionCode());
        	}
        }*/
  		//b.putStringArray("chosen_priorities", userVotes);
  		vote.putExtra("chosen_priorities", userVotes);
  		vote.putExtra("own_priority", this.userPriority.getText().toString());
  		
		vote.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(vote);
		
		//close current activity
		//finish();
  	}
  	
  	
	
	private void isCheckedOrNot(CheckBox checkbox) {
		if(checkbox.isChecked())
			VotingActivity.countVotes++;//increment vote count
		Log.i("isChecked: ","Yes");
		if(VotingActivity.countVotes==5){
			Log.i("Vote Limit Reached: ","Yes");
		}
		else
			VotingActivity.countVotes--;//increment vote count
		Log.i("isChecked: ","No");
	}
  	
}
