/**
 * Author:Adams, C4A Kenya
 * */
package org.un.imports;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;
import org.un.myworld.HomeActivity;
import org.un.myworld.R;
import org.un.myworld.SavedVotesActivity;
import org.un.myworld.VotingCompleteActivity;
import org.un.myworld.data.sync.Sync;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Looper;
import android.util.Log;
import android.view.View;

public class JSONParser {

	protected static InputStream is = null;
	protected static JSONObject jObj=null,MyWorldServerJSON= null;
	protected static String json = "",json_string="";
	private static final String TAG="JSONParser";
	//private DB_Adapter db;
	public int RESPONSE_CODE=0;
	public static int vote_count;
	public static boolean sent,sync_done=false;
	public Thread threadJSONWorker;

	// constructor
	public JSONParser() {
		JSONParser.vote_count=-1;
        JSONParser.sent=false;
        this.threadJSONWorker=new Thread();
	}

	// function get json from url
		// by making HTTP POST or GET method
		public JSONObject makeHttpRequest(String url, String method,
				List<NameValuePair> params) {

			// Making HTTP request
			try {
				
				// check for request method
				if(method == "POST"){
					// request method is POST
					// defaultHttpClient
					DefaultHttpClient httpClient = new DefaultHttpClient();
					HttpPost httpPost = new HttpPost(url);
					httpPost.setEntity(new UrlEncodedFormEntity(params));

					HttpResponse httpResponse = httpClient.execute(httpPost);
					HttpEntity httpEntity = httpResponse.getEntity();
					is = httpEntity.getContent();
					
				}else if(method == "GET"){
					// request method is GET
					DefaultHttpClient httpClient = new DefaultHttpClient();
					String paramString = URLEncodedUtils.format(params, "utf-8");
					url += "?" + paramString;
					HttpGet httpGet = new HttpGet(url);

					HttpResponse httpResponse = httpClient.execute(httpGet);
					HttpEntity httpEntity = httpResponse.getEntity();
					is = httpEntity.getContent();
				}			
				

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(
						is, "iso-8859-1"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				is.close();
				json = sb.toString();
			} catch (Exception e) {
				Log.e("Buffer Error", "Error converting result " + e.toString());
			}

			// try parse the string to a JSON object
			try {
				jObj = new JSONObject(json);
			} catch (JSONException e) {
				Log.e("JSON Parser", "Error parsing data " + e.toString());
			}

			// return JSON String
			return jObj;

		}
	
	public JSONObject getJSONFromAssets(String uri, Context ctx) {

		// Try opening the file
		    AssetManager assetManager = ctx.getAssets();
		  

		try {
			is = assetManager.open(uri);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			json = sb.toString();
			Log.e("JSON", json);
		} catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e.toString());
		}

		// try parse the string to a JSON object
		try {
			jObj = new JSONObject(json);			
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}

		// return JSON String
		return jObj;

	}
	
	/**
	 * @description method to send votes 
	 * @param URL-the api server, json-the data to be sent
	 * */
	//method to post json to server
		public void sendJson(final String URL,final JSONObject json,final long vote_id,final int total_votes) {
			        int total=total_votes;
	                DefaultHttpClient client = new DefaultHttpClient();
	                HttpConnectionParams.setConnectionTimeout(client.getParams(),60000); //Timeout Limit
	                HttpResponse response;
	                JSONParser.jObj = json;
	                
	               Log.i(TAG,"JSON Out: "+jObj.toString());

	                try {
	                    HttpPost post = new HttpPost(URL);
	                    //json.put("key", key);
	                    //json.put("test", test);
	                    StringEntity se = new StringEntity( JSONParser.jObj.toString());  
	                   // System.out.println("JSON Sent: "+se);
	                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
	                    post.setEntity(se);
	                    response = client.execute(post);
	                    
	                    RESPONSE_CODE=response.getStatusLine().getStatusCode();
	                     Log.i(TAG,"Status Code: "+RESPONSE_CODE);
	                    /*Checking response */
	                    if(RESPONSE_CODE==200 && response!=null){
	                    	JSONParser.is = response.getEntity().getContent(); //Get the data in the entity
	                       Log.i(TAG,"Response: Not null");
	            			try {
	            				BufferedReader reader = new BufferedReader(new InputStreamReader(
	            						is, "iso-8859-1"), 8);
	            				StringBuilder sb = new StringBuilder();
	            				String line = null;
	            				while ((line = reader.readLine()) != null) {
	            					sb.append(line + "\n");
	            				}
	            				JSONParser.is.close();
	            				JSONParser.json_string = sb.toString();
	            				//Log.i(TAG, "Response String: "+json_string);
	            			} catch (Exception e) {
	            				Log.e("Buffer Error", "Error converting result " + e.toString());
	            			}

	            			// try parse the string to a JSON object
	            			try {
	            				JSONParser.MyWorldServerJSON = new JSONObject(JSONParser.json_string);
	            				//on success delete the sent vote from sqlite db call 
	            				
	            						//Log.d("API: ", "Sucess: "+JSONParser.MyWorldServerJSON.getInt("success"));
	            					Sync.server_reponse=JSONParser.MyWorldServerJSON.getInt("success");
	            					//on success delete the sent vote from sqlite db call 
	            					if(Sync.server_reponse==1){
	            						//call DB_Adapter function to delete record
	            						Sync.db.deletePrioritylist(vote_id);
	            						Sync.db.deleteVote(vote_id);
	            						
	            						//Sync.db.close();
            							Log.i(TAG, "Vote: Sent");
            							Sync.db.open();
            							vote_count=Sync.db.getTotalVotes();
            							Sync.db.close();
            							Log.i(TAG, "Vote Count: "+vote_count);
            							sent=true;
            							total--;
	            						
	            						}else{
	            							total--;
	            							Log.i(TAG, "Response: "+JSONParser.MyWorldServerJSON.toString());
	            							Log.i(TAG, "Vote: Not Sent");
	            							sent=false;
	            						}
	            					
	            					    if(total==0){
	            					    	JSONParser.sync_done=true;
	            					    	Log.i(TAG, "Sync Complete: Yes");
	            					    	
	            					    }
	            				
	            			} catch (JSONException e) {
	            				Log.e("JSON Parser", "Error parsing data " + e.toString());
	            			}
	            			//Log.i(TAG,"API Response: "+MyWorldServerJSON.toString()); 
	                    }

	                } catch(Exception e) {
	                    e.printStackTrace();
	                    //Log.e(TAG,e.toString());
	                    //createDialog("Error", "Cannot Establish Connection");
	                }

	               // Log.i(TAG,"Thread State: "+threadJSONWorker.getState().toString());
	                
	               
	        
	       
	       // return JSONParser.MyWorldServerJSON;
	        
			
	    }
	
	
	
}
