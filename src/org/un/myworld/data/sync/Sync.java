package org.un.myworld.data.sync;



import java.util.Date;


import org.json.JSONException;
import org.json.JSONObject;
import org.un.imports.JSONParser;
import org.un.myworld.R;



import android.app.IntentService;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;


/**
 * 
 * @author kinyua, Adamsy
 * @lastmodified Adamsy, March 19th 2012
 * @description -  Service that saves vote data based on internet connectivity
 * 				** First check for unsynced data, if existent.. sync it if net available.. if not register receiver.
 * 				** When connectivity is available, data posted to the server, otherwise
 * 				** the broadcast receiver is registered and listens for net connectivity.
 * 				** When acquired.. sync is done -> read data in json form, do post to server, clear the data from sqlite.
 * 				** If service is killed b4 sync.. onDestroy, we unregister the receiver
 * 				
 * ********* Unimplemented --> Checker for whether voter has voted b4.
 */
public class Sync extends IntentService {
	public Sync() {
		super(String.valueOf(R.string.title_service_name));
		// TODO Auto-generated constructor stub
	}


	private static final String TAG = "com.data.sync";
	private BroadcastReceiver broadcastReceiver = null;
	private double latitude;
	private double longitude;
	public static DB_Adapter db;
	private final String API_ACCESS_KEY="API_ACCESS_KEY";//the app key
	private final int TEST_CODE=1; //1-for testing..other for posting
	public static boolean sync_done=false,sync_cancelled=false;
	private int loop_value,total_votes,progressCount=0,service_result=0;
	JSONParser jsonParser = new JSONParser();
	private ProgressDialog pDialog;
	public static int server_reponse=0;
	public static String id_returned="",error_via_intent="";
	public static final String POST_URL = "https://apps.myworld2015.org/vote.php";
	
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	
	/**
	 * @description - get location -- single location
	 * @author kinyua
	 *
	 */
     class LocationHelper {
		public double latitude = 0.0 ;
		public double longitude = 0.0;
		LocationManager locationManager;
		LocationListener locationListener;
	    public void getLocation(Context context) {

	        locationListener = new LocationListener() {

	            @Override
	            public void onStatusChanged(String provider, int status, Bundle extras) {
	                Log.v("LocationListener", "onStatusChanged");
	            }    

	            @Override
	            public void onProviderEnabled(String provider) {
	                Log.v("LocationListener", "onProviderEnabled");                
	            }

	            @Override
	            public void onProviderDisabled(String provider) {
	                Log.v("LocationListener", "onProviderDisabled");
	            }

	            @Override
	            public void onLocationChanged(Location location) {
	                Log.v("LocationListener", "onLocationChanged");
	                latitude = location.getLatitude();
	                longitude = location.getLongitude();
	            }
	        };

	        locationManager = (LocationManager)context.getSystemService(Service.LOCATION_SERVICE);

	        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, Looper.myLooper());
	    }
	    
	}
	
	
	public void getLocation() {
	    // load all available Location providers
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    // determine the last known location within 1 hours available from cache
	    Location myLocation = null;
	    long time_delta = 3600000; // One hour 
	    Location last_known = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	    long time_now = new Date().getTime();
	    
	    if( last_known != null ){
	    	long t = last_known.getTime();
	    	//if location is not older than one hour, set it as our location
	    	if( (time_now - t ) <= time_delta ){
	    		myLocation = last_known;
	    	}
	    }
	    //if we don't have a location yet
	    if( myLocation == null ){
	    	LocationHelper locationHelper = new LocationHelper();
	    	locationHelper.getLocation(this);
	    	
	    	if( locationHelper.latitude != 0.0 && locationHelper.longitude != 0.0 ){
	    		this.setLatitude(locationHelper.latitude);
	    		this.setLongitude(locationHelper.longitude);
	    		locationHelper.locationManager.removeUpdates(locationHelper.locationListener);
	    	}else{
	    		this.setLatitude(0.00);
	    		this.setLongitude(0.00);
	    	}
	    	
	    } else{
	    	
	    	this.setLatitude(myLocation.getLatitude());
	    	this.setLongitude(myLocation.getLongitude());
	   
	    }
	    
	}
	
	
	
	private void installListener() {
		
        if (broadcastReceiver == null) {

            broadcastReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					 Bundle extras = intent.getExtras();
					 NetworkInfo info = (NetworkInfo) extras.getParcelable("networkInfo");
					 State state = info.getState();
	                 Log.d(TAG, info.toString() + " "+ state.toString());
	                
	                 if (state == State.CONNECTED) {
	                	/*If internet available... sync*/	
	                	Log.d(TAG, "Connection-Acquired");
	                	/*try {
							do_sync();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}*/
	                 }
				}
            };

            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.setPriority(2147483647);
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(broadcastReceiver, intentFilter);
        }
    }
	
	/*Boolean method to check if internet is available*/
	public boolean is_connected(){
		ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo active_mobile_connection = connectivityManager.getActiveNetworkInfo();
		return active_mobile_connection != null && active_mobile_connection.isConnectedOrConnecting();
	}

	
	/**
	 * @description - If net available, save local,
	 * 					else register the broadcast receiver to listen for net availability.
	 * */
	public void do_save(){
		
		if(is_connected()){
			Log.d(TAG, "Connected--");
			//do post here to online database.
			
	
		}else{
			Log.d(TAG, "Disconnected");
			//save here to sqlite database.
			
			//register the receiver.
			installListener();
			Log.d(TAG, "Receiver registered");
		}
		
	}
	/* (non-Javadoc)
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
		//unregister broadcast receiver
		if( broadcastReceiver != null ){
			unregisterReceiver(broadcastReceiver);
			broadcastReceiver = null;
		}
		
	}
	
	/**
	 * @desscription -an async task to help with the votes upload easily
	 * @author John Adamsy
	 * */
	/*class DoPushVotesToServer: defines an assynchronous task*/
    private class DoPushVotesToServer extends AsyncTask<Void, Integer, Integer>{/*our methods will need these types specified here as params*/
    	
    	/**
		 * Before starting background thread Start the sending vote activity
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//Toast.makeText(getBaseContext(), "Initiating process...", Toast.LENGTH_LONG).show();
			/*pDialog = new ProgressDialog(getBaseContext());
			pDialog.setTitle("Sending Votes");
			pDialog.setIcon(R.drawable.info);
			pDialog.setMessage(getString(R.string.saved_votes_upload_process_label,total_votes));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();*/
		}
    	
    	@Override
    	protected Integer doInBackground(Void... sendJSON){
    		int count=progressCount;
    		Log.d(TAG, "Connection-Acquired");
        	try {
        		//while(Sync.sync_cancelled==false){
        			this.do_sync();
        			
        			//if(Sync.sync_cancelled==true) break;
        		//}
				
        		
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
        	return JSONParser.vote_sent_count;
    	}
    	
    	@Override
    	 protected void onProgressUpdate(Integer... progress){
    	    	Log.i(TAG, String.valueOf(progress[0])+"% uploaded");
    	    	
    	    	//Toast.makeText(getBaseContext(), String.valueOf(progress[0])+"% complete", Toast.LENGTH_LONG).show();
    	    	/*for(int i=0;i<total_votes;i++){
        			//calculate the percentage uploaded and report its progress
        			publishProgress((int) (((i+1)/(float)total_votes)*100));
        		}*/
    	    }
    	 
    	@Override
    	 protected void onPostExecute(Integer result){//un used
    		//pDialog.dismiss();
    			/**/service_result=result;
    	    	if(service_result>0){//send a broadcast that the upload is complete
    	    		Intent broadcastIntent = new Intent();
    	    		broadcastIntent.setAction("VOTES_UPLOAD_ACTION");
    	    		broadcastIntent.putExtra("Sent_Vote_Count", result);
    	    		broadcastIntent.putExtra("Error","OK");
    	    		getBaseContext().sendBroadcast(broadcastIntent);
    	    		//Toast.makeText(getBaseContext(), "Uploaded "+result+" vote(s)", Toast.LENGTH_LONG).show();
    	    		//uploadSuccessAlert(getBaseContext(),result);
    	    		}else{
    	    			Intent broadcastIntent = new Intent();
        	    		broadcastIntent.setAction("VOTES_UPLOAD_ACTION");
        	    		broadcastIntent.putExtra("Error",Sync.error_via_intent);
        	    		getBaseContext().sendBroadcast(broadcastIntent);
    	    		}
    	    	
    	    	stopSelf(); //<--equivalent to the stopService() method  
    		
    	    }
    	
    	 /*Synchronize if available.*/
    	private void do_sync() throws JSONException{
    		db.open(); //open db
    		total_votes=db.getTotalVotes();
    		db.close();
    		loop_value=0;
    		
    			JSONObject vote_details;	 
                   
    		for(long vote:db.getAllPriorityIds()){//loop thru all the votes
    			loop_value++;
    		
    		try {
    			//db.open();
    			//vote_details = db.get_vote(API_ACCESS_KEY, TEST_CODE,vote); //--test params
    			vote_details = db.get_vote(API_ACCESS_KEY,vote); //--live params
    		 
    		//Log.i(TAG,"Vote_Details: "+vote_details.toString());
    		
    		//if not null..
    		if( vote_details != null ){
    			//pull data from sqlite db --> the data is in vote_details. POST_URL(Private final string)
    			
    			//post them
    			jsonParser.sendJson(POST_URL, vote_details, vote,total_votes);
    		}
    		
    		} catch (JSONException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		
    		if(JSONParser.error_caught!=""){
				Sync.error_via_intent=JSONParser.error_caught;
				Log.i(TAG,JSONParser.error_caught+" occured");
				Sync.sync_cancelled=true;
				
				//break;
			}else{
				Sync.sync_cancelled=false;
			}
    		
    		}//end of the for each vote loop
    		
    		//unregister the receiver once we update vote on the server
    			Log.d(TAG, "sync-done-receiver-uregistered");
    		if( broadcastReceiver != null ){
    			unregisterReceiver(broadcastReceiver);
    			
    			broadcastReceiver = null;
    		}
    		//Log.d(TAG, "sync-method");
    		
    		//db.close(); //close db
    	}//close public void do_sync()
    }/*end of inner class DoBackgroundTask*/


	@Override
	protected void onHandleIntent(Intent intent) {
		//---send a broadcast to inform the activity
		// that the votes have been uploaded---
		Log.d(TAG, "on-start");
		db = new DB_Adapter(this);
		
		//If there is any unsynced data..
		//try {
			db.open();
			if(db.getTotalVotes()>0){
				progressCount=db.getTotalVotes();
				db.close();
				if(is_connected())
				new DoPushVotesToServer().execute();//start the async task for pushing votes
			}
			
		//} catch (JSONException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			//Log.e(TAG,e.toString());
		//}
		
		
		do_save();
		Log.i(TAG,"Service Done: "+service_result);
		
	}
	
	
    
   
}
