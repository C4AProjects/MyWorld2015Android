/*
 * Copyright (C) 2011 Cyril Mottier (http://www.cyrilmottier.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.un.myworld;



import java.util.HashMap;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;



public class VotesListAdapter extends ListActivity{

    private static final String VOTE_STATES = "vote_states";
    
    public static String [] PRIORITY_COLOR,PRIORITY_CODE,PRIORITY_TITLE,PRIORITY_DESCRIPTION;
   

    private VotesAdapter mAdapter;
    private boolean[] mVoteStates;
    EditText userPriority; static Button nextButton;TextView voteInstruction, textPriorityDescription;
    RelativeLayout lowerBar;
    ListView listView;
    
    //static String userVotes[]=new String[6]; //array to hold user's priorities
	static int countVotes=0; //we need a maximum of 6 votes
	 HashMap<String, String> userVotes = new HashMap<String, String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	//language config
    	Preferences.configureLanguage(this);
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vote);
        
        //initialize some values
        lowerBar=(RelativeLayout)findViewById(R.id.lower_bar);
		voteInstruction=(TextView)findViewById(R.id.vote_instructions);
		//priorityDescription=(TextView)findViewById(R.id.mdg_description);
		userPriority=(EditText)findViewById(R.id.add_option);
		nextButton=(Button)findViewById(R.id.finishVote);
        
        PRIORITY_COLOR=this.getResources().getStringArray(R.array.priority_color);
        PRIORITY_CODE=this.getResources().getStringArray(R.array.priority_code);
        PRIORITY_TITLE=this.getResources().getStringArray(R.array.priority_title);
        PRIORITY_DESCRIPTION=this.getResources().getStringArray(R.array.priority_description);

        // The following code allows the Activity to restore its state after it
        // has been killed by the system (low memory condition, configuration
        // change, etc.)
        if (savedInstanceState != null) {
        	mVoteStates = savedInstanceState.getBooleanArray(VOTE_STATES);
        } else {
        	mVoteStates = new boolean[PRIORITY_TITLE.length];
        }
        
      
       
        listView=getListView();//(ListView)findViewById(R.id.list);
        mAdapter = new VotesAdapter(VotesListAdapter.this);
        //setListAdapter(mAdapter);
        listView.setAdapter(mAdapter);
      //priorityDescription=(TextView)findViewById(R.id.mdg_description);
        
      //show hidden views
       if(lowerBar.getVisibility()==View.GONE){lowerBar.setVisibility(View.VISIBLE);}
       if(voteInstruction.getVisibility()==View.GONE){voteInstruction.setVisibility(View.VISIBLE);}
        
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBooleanArray(VOTE_STATES, mVoteStates);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
       // Log.i("Clicked",""+PRIORITY_CODE[position]);
    	//l.getChildAt(position).getVisibility();
    	
    	/*if(l.getChildAt(position).getId()==R.id.mdg_description){
    		textPriorityDescription=(TextView)findViewById(l.getChildAt(position).getId());
    		Log.i("Desc is: ",""+textPriorityDescription.getVisibility());
    	}*/
    	
    	super.onListItemClick(l, v, position, id);
    	TextView text_desc=(TextView)v.findViewById(R.id.mdg_description);
    	//hide or show description
		if (text_desc.getVisibility() == View.VISIBLE) {
			//hide the text
			text_desc.setVisibility(View.GONE);
		}else {
			//show
			text_desc.setVisibility(View.VISIBLE);
		}
      
       
        
    }

   
    private static class VotesViewHolder {
    	public TextView text_code,text_title,text_desc;
		public ImageView img_mdgColor;
		public CheckBox checkbox;
    }

    
   /* public class VotesAdapter extends BaseAdapter {
    	
    	public  final List<ModelListItem> list;
    	private final Activity context;
    	
    	//boolean checkAll_flag = false;
    	//boolean checkItem_flag = false;
    	
    	//Constructor
    	/*public VotesAdapter(Activity context, List<ModelListItem> list) {
    		super(context, R.layout.option_row, list);
    		this.context = context;
    		this.list = list;
    	}*/
     private class VotesAdapter extends BaseAdapter {
    	 
    	//variables
    	    private Activity activity;
    	 //   private ArrayList<String[]> data;
    	  //  private static LayoutInflater inflater=null;
    	 
    	 //constructor
    	    public VotesAdapter(Activity a) {
    	        activity = a;
    	        //data=d;
    	       // inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	    }

        @Override
        public int getCount() {
            return PRIORITY_TITLE.length;
        }

        @Override
        public String getItem(int position) {
            return PRIORITY_TITLE[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            VotesViewHolder viewHolder = null;

            if (convertView == null) {
            	LayoutInflater inflator = activity.getLayoutInflater();
				convertView = inflator.inflate(R.layout.option_row, null);
               // convertView = getLayoutInflater().inflate(R.layout.option_row, parent, false);

                viewHolder = new VotesViewHolder();
				viewHolder.text_code = (TextView) convertView.findViewById(R.id.option_code);
				viewHolder.text_title = (TextView) convertView.findViewById(R.id.mdg_title);
				viewHolder.text_desc = (TextView) convertView.findViewById(R.id.mdg_description);
				viewHolder.img_mdgColor = (ImageView) convertView.findViewById(R.id.option_color);
				viewHolder.checkbox = (CheckBox) convertView.findViewById(R.id.mdg_check);

                ((CheckBox) convertView.findViewById(R.id.mdg_check)).setOnClickListener(mVoteCheckboxClickListener);
                
                ((TextView) convertView.findViewById(R.id.mdg_description)).setOnClickListener(mVoteCheckboxClickListener);

                convertView.setTag(viewHolder);
            } else {
            	viewHolder = (VotesViewHolder) convertView.getTag();
            }

            /*
             * The Android API provides the OnCheckedChangeListener interface
             * and its onCheckedChanged(CompoundButton buttonView, boolean
             * isChecked) method. Unfortunately, this implementation suffers
             * from a big problem: you can't determine whether the checking
             * state changed from code or because of a user action. As a result
             * the only way we have is to prevent the CheckBox from callbacking
             * our listener by temporary removing the listener.
             */
            viewHolder.checkbox.setOnCheckedChangeListener(null);
            viewHolder.checkbox.setChecked(mVoteStates[position]);
            viewHolder.checkbox.setOnCheckedChangeListener(mVoteCheckedChanceChangeListener);

            viewHolder.text_code.setText(PRIORITY_CODE[position]);
			viewHolder.text_title.setText(PRIORITY_TITLE[position]);
			viewHolder.text_desc.setText(PRIORITY_DESCRIPTION[position]);
			viewHolder.img_mdgColor.setImageResource(getResources().getIdentifier(PRIORITY_COLOR[position] , "drawable", getPackageName()));
			//viewHolder.checkbox.setChecked(list.get(position).isSelected());

            return convertView;
        }
    }

    private OnClickListener mVoteCheckboxClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            final int position = getListView().getPositionForView(v);
            if (position != ListView.INVALID_POSITION) {
                //do something
            	 CheckBox checkBox = (CheckBox)v.findViewById(R.id.mdg_check);
                 //checkBox.setChecked(!checkBox.isChecked());
            	if(mVoteStates[position]==true){
            		
            		
            		if(countVotes>=6){
            			Log.i("Check Limit: ","of 6");
            			checkBox.setChecked(false);
            			//enable next button since user has picked 6 votes as required
            			
            			nextButton.setEnabled(true);
            		}else{
            			countVotes=countVotes+1;
            			Log.i("isChecked: ",""+PRIORITY_CODE[position]);
            			Log.i("Count at: ",""+countVotes);
            			//put vote in the basket :)
            			userVotes.put(String.valueOf(countVotes), String.valueOf(PRIORITY_CODE[position]));
            			if(countVotes==6){
            				nextButton.setEnabled(true);
            			}
            		}
            		
            	}else{
            		userVotes.remove(countVotes);//remove the unchecked vote
            		if(countVotes>0){
            			countVotes=countVotes-1;
            		}
            		
            		Log.i("unChecked: ",""+PRIORITY_CODE[position]);
            		Log.i("Count at: ",""+countVotes);
            		//disable next button as vote limit has reduced by one
            		nextButton.setEnabled(false);
            	}
            }
 
        }
    };
    

    private OnCheckedChangeListener mVoteCheckedChanceChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            final int position = getListView().getPositionForView(buttonView);
            if (position != ListView.INVALID_POSITION) {
                mVoteStates[position] = isChecked;
            }
        }
    };
    
    //go to the voting activity
  	public void btnFinishVote_Click(View view)
  	{
  		Intent vote = new Intent(getApplicationContext(), FinishVoteActivity.class);
  		
  		//priorities must be =6
  		
  		Bundle b=new Bundle(); 
  		//get user selected priorities pass the data to the user info intent

  		//b.putStringArray("chosen_priorities", userVotes);
  		vote.putExtra("chosen_priorities", userVotes);
  		vote.putExtra("own_priority", this.userPriority.getText().toString());
  		
		vote.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(vote);
		countVotes=0; //reset count votes value
		userVotes.clear();//clear all collected votes
		
		//close current activity
		//finish();
  	}
}
