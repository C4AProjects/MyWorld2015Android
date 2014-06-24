package org.un.myworld;

import org.un.myworld.util.SystemUiHider;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class MyWorldLauncher extends Activity {
	
	
	public static String [] LANGUAGE_CODE,LANGUAGE_NAME,LANGUAGE_FLAG;
	private static final String TAG="MyWorldLauncher";
	private int pos=0;
    private LanguageAdapter adapter;
    private ListView contentView;
   
    
    static final int START_ANY_ACTIVITY_REQUEST=450;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 
		//initialize sharePrefs variable
		Preferences.sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

		setContentView(R.layout.activity_my_world_launcher);
		
		
		 //initialize pref editor
		 Preferences.edit = Preferences.sharedPrefs.edit();

		
		//final View contentView = findViewById(R.id.fullscreen_content);
		//final ListView contentView;// = (ListView)findViewById(R.id.list);
		
		LANGUAGE_NAME=this.getResources().getStringArray(R.array.pref_language_item);
		LANGUAGE_CODE=this.getResources().getStringArray(R.array.pref_language_value);
		LANGUAGE_FLAG=this.getResources().getStringArray(R.array.pref_language_value);
		
		contentView=(ListView)findViewById(R.id.listview);
        adapter = new LanguageAdapter(LANGUAGE_NAME,this);
        //setListAdapter(mAdapter);
        contentView.setAdapter(adapter);

		
	}
	

	public void launchHomeActivity(){
		//start home activity
		Intent intentHome = new Intent(getApplicationContext(), HomeActivity.class);
		intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(intentHome,START_ANY_ACTIVITY_REQUEST);
		finish();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		
	}

	

	
	//view items holder
	 private static class LanguagesViewHolder {
	    	public TextView text_code,text_title;
			public ImageView img_language_flag;
			
	    }
	
   //inner class to define our language adapter
	private class LanguageAdapter extends BaseAdapter {

        private String[] mData;
        
      //variables
	    private Activity activity;
	
	

        public LanguageAdapter(String[] data,Activity a) {
            mData = data;
            activity = a;
        }

        public void changeData(String[] data) {
            mData = data;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mData.length;
        }

        @Override
        public String getItem(int position) {
            return mData[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LanguagesViewHolder viewHolder = null;

            if (convertView == null) {
            	LayoutInflater inflator = activity.getLayoutInflater();
				convertView = inflator.inflate(R.layout.language_row, null);
              

                viewHolder = new LanguagesViewHolder();
				viewHolder.text_code = (TextView) convertView.findViewById(R.id.language_code);
				viewHolder.text_title = (TextView) convertView.findViewById(R.id.language_title);
				viewHolder.img_language_flag = (ImageView) convertView.findViewById(R.id.language_flag);
				
                
               // ((TextView) convertView.findViewById(R.id.mdg_description)).setOnClickListener(mVoteCheckboxClickListener);

                convertView.setTag(viewHolder);
            } else {
            	viewHolder = (LanguagesViewHolder) convertView.getTag();
            }

           

            viewHolder.text_code.setText(LANGUAGE_CODE[position]);
			viewHolder.text_title.setText(LANGUAGE_NAME[position]);
			viewHolder.img_language_flag.setImageResource(getResources().getIdentifier(LANGUAGE_FLAG[position] , "drawable", getPackageName()));
			//viewHolder.checkbox.setChecked(list.get(position).isSelected());
			
			//register an on click listener
			((TextView) convertView.findViewById(R.id.language_title)).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					 final int position = contentView.getPositionForView(v);
					// TODO check if mdg_code is visible
					Log.i(TAG,"Language Picked: "+LANGUAGE_CODE[position]);
						
						//btnUseLanguage.setText(LANGUAGE_NAME[position]);
						Preferences.languagePrefix=LANGUAGE_CODE[position];
						Log.i(TAG,"Key: "+Preferences.KEY_LANGUAGE_LIST_PREFERENCE);
						Log.i(TAG,"Value: "+Preferences.languagePrefix);
						Preferences.storeSharedPrefs(Preferences.KEY_LANGUAGE_LIST_PREFERENCE, Preferences.languagePrefix);
						
						//start home activity
						launchHomeActivity();
						
					
				}
			});

            return convertView;
        }
    }
    }

