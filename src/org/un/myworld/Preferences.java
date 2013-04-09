/*App Preferences class.
 * @author Adams J.Opiyo, Coders4Africa Kenya
 * @Modified March 3rd 2013
 * */
package org.un.myworld;

import java.util.Locale;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class Preferences extends PreferenceActivity implements OnPreferenceChangeListener,OnSharedPreferenceChangeListener{
	
	//variable declaration
	private final static String TAG="Preferences";
	public static Configuration config;
	public static Locale locale;
	public static String languagePrefix="";
	protected static SharedPreferences sharedPrefs;
	protected static ListPreference country,language;
	protected static EditTextPreference partnerId;
	
	public static final String KEY_COUNTRY_LIST_PREFERENCE="country";
	public static final String KEY_LANGUAGE_LIST_PREFERENCE="language";
	public static final String KEY_PARTNER_ID_EDIT_TEXT_PREFERENCE="partID";
	
	protected static Editor edit; //for the preferences

	/** Called when the activity is first created. */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
    protected void onCreate(final Bundle savedInstanceState)
    {
		//configure language
		//languagePrefix=sharedPrefs.getString(KEY_LANGUAGE_LIST_PREFERENCE, "");
		//configureLanguage(this);
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= 11){//target honeycomb and above
        	//setContentView(R.layout.settings_window);
        	//setTheme(R.style.FullscreenTheme);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
        }else{
        	//setContentView(R.layout.settings_window);
        	addPreferencesFromResource(R.xml.preferences);
        }
        //initialize preference items
       // sharedPrefs=PreferenceManager.getDefaultSharedPreferences(this);
        country=(ListPreference)findPreference(KEY_COUNTRY_LIST_PREFERENCE);
        language=(ListPreference)findPreference(KEY_LANGUAGE_LIST_PREFERENCE);
        partnerId=(EditTextPreference)findPreference(KEY_PARTNER_ID_EDIT_TEXT_PREFERENCE);
    }

	//inner class to implement the PreferenceFragment
    public static class MyPreferenceFragment extends PreferenceFragment implements OnPreferenceChangeListener,OnSharedPreferenceChangeListener
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            //Preferences.setContentView(R.layout.settings_window);
            addPreferencesFromResource(R.xml.preferences);
        }

		@Override
		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {
			if(key.equals(KEY_COUNTRY_LIST_PREFERENCE)){
				Log.i(TAG,"Country changed");
			}else if(key.equals(KEY_LANGUAGE_LIST_PREFERENCE)){
				//change locale settings
				
				Log.i(TAG,"Language changed");
			}else if(key.equals(KEY_PARTNER_ID_EDIT_TEXT_PREFERENCE)){
				Log.i(TAG,"Partner changed");
			}
			storeSharedPrefs(key, sharedPrefs.getString(key, ""));
			
		}

		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public void onResume() {
		    super.onResume();
		    getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

		}

		@Override
		public void onPause() {
		    getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
		    super.onPause();
		}
		
		
    }
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflate = new MenuInflater(this);
		inflate.inflate(R.menu.settings, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override //to know which menu item was clicked
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_home:
			//setResult(RESULT_CANCELED);
			menuItemSave_Clicked();
			break;
		case R.id.menu_save:			
			menuItemHome_Clicked();
			break;

		default:
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public boolean onPreferenceChange(Preference pref, Object newValue) {
		/*if(pref.equals(country)){
			//Log.i(TAG,"Country changed");
		}else if(pref==language){
			//Log.i(TAG,"Language changed");
		}else if(pref==partnerId){
			//Log.i(TAG,"Partner changed");
		}
		if((String)newValue !=""){
			//this.storeSharedPrefs("", value)
		}*/
		return false;
	}
	
    //store changed shared 
	protected static void storeSharedPrefs(String key, String value) {
		edit.putString(key, value);
		edit.commit(); // Committing changes
		
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPref, String key) {
		if(key.equals(KEY_COUNTRY_LIST_PREFERENCE)){
			Log.i(TAG,"Country changed");
		}else if(key.equals(KEY_LANGUAGE_LIST_PREFERENCE)){
			//change locale settings
			languagePrefix=sharedPrefs.getString(key, "");
			Log.i(TAG,"Language changed");
		}else if(key.equals(KEY_PARTNER_ID_EDIT_TEXT_PREFERENCE)){
			Log.i(TAG,"Partner changed");
		}
		
		storeSharedPrefs(key, sharedPrefs.getString(key, ""));
	}
	
	 @Override
	    protected void onResume() {
	        super.onResume();

	        // Set up a listener whenever a key changes            
	        sharedPrefs.registerOnSharedPreferenceChangeListener(this);
	    }
	@Override
    protected void onPause() {
        super.onPause();

        // Unregister the listener whenever a key changes            
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(this);    
    }
	
	@Override
	public void onBackPressed(){
		menuItemHome_Clicked();
	}
	
	public static void configureLanguage(Context context){
		//language config
		locale = new Locale(languagePrefix);
        Locale.setDefault(locale);
        config = new Configuration(context.getResources().getConfiguration());
	    config.locale = locale ;
	    context.getResources().updateConfiguration(config,context.getResources().getDisplayMetrics());
	}

   private void menuItemHome_Clicked(){
	   //check if settings are not blank
	   Log.i(TAG,"Pref PartnerID: "+Preferences.sharedPrefs.getString(KEY_PARTNER_ID_EDIT_TEXT_PREFERENCE, null));
	   Log.i(TAG,"Pref Country: "+Preferences.sharedPrefs.getString(KEY_COUNTRY_LIST_PREFERENCE, null));
	   if(Preferences.sharedPrefs.getString(KEY_PARTNER_ID_EDIT_TEXT_PREFERENCE, null)==null || Preferences.sharedPrefs.getString(KEY_COUNTRY_LIST_PREFERENCE, null)==null|| Preferences.sharedPrefs.getString(KEY_PARTNER_ID_EDIT_TEXT_PREFERENCE, null)=="" || Preferences.sharedPrefs.getString(KEY_COUNTRY_LIST_PREFERENCE, null)==""){
		   this.missingSettingsAlert();
		   }else{
		  // do nothing and go back
		   setResult(RESULT_CANCELED);
		   finish();
	   }
   }
   
   private void menuItemSave_Clicked(){
	   //check if settings are not blank
	   Log.i(TAG,"Pref PartnerID: "+Preferences.sharedPrefs.getString(KEY_PARTNER_ID_EDIT_TEXT_PREFERENCE, null));
	   Log.i(TAG,"Pref Country: "+Preferences.sharedPrefs.getString(KEY_COUNTRY_LIST_PREFERENCE, null));
	   if(Preferences.sharedPrefs.getString(KEY_PARTNER_ID_EDIT_TEXT_PREFERENCE, null)==null || Preferences.sharedPrefs.getString(KEY_COUNTRY_LIST_PREFERENCE, null)==null|| Preferences.sharedPrefs.getString(KEY_PARTNER_ID_EDIT_TEXT_PREFERENCE, null)=="" || Preferences.sharedPrefs.getString(KEY_COUNTRY_LIST_PREFERENCE, null)==""){
		 this.missingSettingsAlert();
	   }else{
		  // save current settings and go back
		   storeSharedPrefs(KEY_PARTNER_ID_EDIT_TEXT_PREFERENCE, sharedPrefs.getString(KEY_PARTNER_ID_EDIT_TEXT_PREFERENCE, null));
		   storeSharedPrefs(KEY_COUNTRY_LIST_PREFERENCE, sharedPrefs.getString(KEY_COUNTRY_LIST_PREFERENCE, null));
		   finish();
	   }
   }
   
   
   private void missingSettingsAlert() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.pref_partner_info_missing)
				.setCancelable(false)
				.setIcon(R.drawable.info)
				.setPositiveButton(R.string.pref_partner_info_set,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}

	
}