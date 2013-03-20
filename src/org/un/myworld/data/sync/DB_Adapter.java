package org.un.myworld.data.sync;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * 
 * @author kinyua
 * @description Database Adapter, providing management of our sqlite database
 */
public class DB_Adapter {
	
	private DB db;
	private SQLiteDatabase database;
	public static int rowCount;
	
	private Context context;
	

	private static final String TBL_VOTE = "VoteInfo";
	private static final String KEY_VOTE_VoteId = "VoteId";
	private static final String KEY_VOTE_VoterId = "VoterId";
	private static final String KEY_VOTE_PartnerId = "PartnerID";
	private static final String KEY_VOTE_FlagUploaded = "FlagUploaded";
	private static final String KEY_VOTE_BallotID = "BallotID";//returned from the server on a successful vote cast
	private static final String KEY_VOTE_FlagExported = "FlagExported";
	private static final String KEY_VOTE_Country = "Country";
	private static final String KEY_VOTE_Region = "Region_state";
	private static final String KEY_VOTE_City = "City";
	private static final String KEY_VOTE_Latitude = "Latitude";
	private static final String KEY_VOTE_Longitude = "Longitude";
	private static final String KEY_VOTE_VotedDate = "VotedDate";
	private static final String KEY_VOTE_Gender = "Gender";
	private static final String KEY_VOTE_YearOfBirth = "Age";
	private static final String KEY_VOTE_Education = "Education";
	private static final String KEY_VOTE_PriorityListId = "PriorityListId";
	private static final String KEY_VOTE_Reason = "Reason";
	
	private static final String TBL_PriorityList = "PriorityList";
	private static final String KEY_PRIORITY_PriorityListId = "PriorityListId";
	private static final String KEY_PRIORITY_FlagUploaded = "FlagUploaded";
	private static final String KEY_PRIORITY_FlagExported = "FlagExported";
	private static final String KEY_PRIORITY_VoteIDCorePriorities = "VoteIDCorePriorities";//From priority Ids.
	private static final String KEY_PRIORITY_SuggestedPriority = "SuggestedPriority";
	
	private static final String TBL_PRIORITY = "Priority";
	private static final String KEY_PRIORITY_PriorityId = "PriorityId";
	private static final String KEY_PRIORITY_Title = "Title";
	private static final String KEY_PRIORITY_Desc = "Description";
	
	private static final String TAG = "com.data.sync"; 

	/**
	 * 
	 * @param ctx
	 */
	public DB_Adapter(Context ctx){	
		this.context = ctx;
	}
	
	/**
	 * @description Close the database connection
	 */
	public void close(){
		db.close();
	}
	
	/**
	 * 
	 * @return
	 * @throws SQLException
	 */
	public DB_Adapter open() throws SQLException{
		db = new DB(this.context);
		database = db.getWritableDatabase();
		return this;
	}
	
	/**
	 * @description - save vote to the database
	 * @param voteid
	 * @param voterid
	 * @param partner_id
	 * @param country
	 * @param city
	 * @param region
	 * @param latitude
	 * @param longitude
	 * @param vote_date
	 * @param gender
	 * @param year_of_birth
	 * @param education
	 * @param p_list_id
	 * @return
	 */
	public long save_vote(long voteid,long voterid,String partner_id,String country,String city,String region,String latitude,String longitude,String vote_date,String gender,String year_of_birth,String education,long p_list_id,String reason){
		this.open();
		ContentValues values = new ContentValues();
		values.put(KEY_VOTE_VoteId,voteid);
		values.put(KEY_VOTE_VoterId, voterid);
		values.put(KEY_VOTE_PartnerId, partner_id);
		values.put(KEY_VOTE_Country,country);
		values.put(KEY_VOTE_City,city);
		values.put(KEY_VOTE_Region,region);
		values.put(KEY_VOTE_Latitude,latitude);
		values.put(KEY_VOTE_Longitude,longitude);
		values.put(KEY_VOTE_VotedDate,vote_date);
		values.put(KEY_VOTE_Gender,gender);
		values.put(KEY_VOTE_YearOfBirth,year_of_birth);
		values.put(KEY_VOTE_Education,education);
		values.put(KEY_VOTE_PriorityListId, p_list_id);
		values.put(KEY_VOTE_Reason, reason);
		long val =  database.insert(TBL_VOTE, null , values);
		this.close();
		Log.d(TAG, "vote_saved");
		return val;
	}
	
	/**
	 * @description returns a json string in the format
	 * 		{
     *		"key": "50180ae54e20512e61000084",
     *		"votes": [1,2,3,4,5,6],
     *		"suggested": "Lorem ipsum dolor sit amet...",
     *		"reason":"fun",
     *		gender: 1,
     *		age: 25,
     *		country: 185,
     *		education: 3,
     *		test: 1,
     *		partner:"apptest"
	 *     }
	 * @param key
	 * @param test
	 * @return
	 * @throws JSONException
	 */
	//public JSONObject get_vote(String key,int test,long vote_id) throws JSONException{//test params
		public JSONObject get_vote(String key,long vote_id) throws JSONException{ //live params
		//this.open();
		database = db.getWritableDatabase();
		Cursor cursor = database.query(TBL_VOTE, new String [] {KEY_VOTE_VoteId,KEY_VOTE_VoterId,KEY_VOTE_PartnerId,KEY_VOTE_Country,KEY_VOTE_City,KEY_VOTE_Region,KEY_VOTE_Latitude,KEY_VOTE_Longitude,KEY_VOTE_VotedDate,KEY_VOTE_Gender,KEY_VOTE_YearOfBirth,KEY_VOTE_Education,KEY_VOTE_Reason},KEY_VOTE_PriorityListId+"="+vote_id+" AND "+KEY_VOTE_FlagUploaded+"='N'", null,null, null, null);
		
		String votes = null;
		String suggested_p = null;
		String partner_id=null;
		//String reason = null;
		String gender = null;
		String age = null;
		String country = null;
		String education = null;
		JSONObject json = new JSONObject();
		JSONArray data=new JSONArray();
	    //long priority_list_id = 0;
		if( cursor.getCount() > 0 ){
			
		while(cursor.moveToNext()){
 
			//reason = cursor.getString( cursor.getColumnIndex(KEY_VOTE_Reason) );
			gender = cursor.getString( cursor.getColumnIndex(KEY_VOTE_Gender) );
			age = cursor.getString( cursor.getColumnIndex(KEY_VOTE_YearOfBirth) );
			country = cursor.getString( cursor.getColumnIndex(KEY_VOTE_Country) );
			education = cursor.getString( cursor.getColumnIndex(KEY_VOTE_Education) );
			partner_id= cursor.getString( cursor.getColumnIndex(KEY_VOTE_PartnerId) );
			//priority_list_id = cursor.getLong( cursor.getColumnIndex(KEY_VOTE_PriorityListId) );
			
			//Log.i(TAG,"PriorityList IDs: "+priority_list_id);
			
			String my_votes[] = get_prioritylist_byId(vote_id);
			
			votes = my_votes[0];
			Log.i(TAG,"Votes: "+votes);
			//Log.i(TAG,"Votes_: "+votes_);
			String[] vote_to_post = votes.split(",");
			int [] votes_array=new int [6];
			int i=0;
			JSONArray jsonArrayVotes=new JSONArray();
			
			for(String value:vote_to_post){
				
				votes_array[i]=Integer.parseInt(value);
				jsonArrayVotes.put(votes_array[i]);
				i++;
				//Log.i(TAG,"V : "+value);
			}
			
			//Log.i(TAG,"Int Array : "+votes_array.length);
			
			//Log.i(TAG,"Split: "+votes);
			//String [] vote_to_post = my_votes[0];
			suggested_p = my_votes[1];
			
			//Log.i(TAG,"JSON Array "+jsonArrayVotes.toString());
			
			JSONObject singleVote =new JSONObject();
			singleVote.put("key", String.valueOf(key));
			singleVote.put("votes", jsonArrayVotes);
			singleVote.put("suggested", String.valueOf(suggested_p));
			//json.put("reason", reason);
			singleVote.put("gender", Integer.valueOf(gender));
			singleVote.put("age", Integer.valueOf(age));
			singleVote.put("country", Integer.valueOf(country));
			singleVote.put("education", Integer.valueOf(education));
			singleVote.put("partner", String.valueOf(partner_id));
			//singleVote.put("test", Integer.valueOf(test)); //--not needed for the live posting
			//json = new JSONObject("{\"key\":\""+key+"\",\"votes\":\"["+votes+"]\",\"suggested\":\""+suggested_p+"\",\"reason\":\""+reason+"\",gender:\""+gender+"\",age:\""+age+"\",country:\""+country+"\",education:\""+education+"\",test:\""+test+"\"}");
			//Log.i(TAG,"JSONObject: "+json.toString());
			
			//data.put(singleVote);
			json=singleVote;
		}
		
		
		//Log.i(TAG,"Data: "+data.toString());
		
		cursor.close(); //close the cursor
		
		
				
		database.close(); //close the database
		return json;
			//}	//end of the for i
		}else{
			//cursor.close();
			database.close();
			return null;
		}
	}
	
	
	
	/**
	 * @description - Empty vote table
	 * @param id
	 * @return
	 */
	public boolean deleteVote(long id){
		this.open();
		int action=database.delete(TBL_VOTE, KEY_PRIORITY_PriorityListId+"="+id, null);
		this.close();
		return  action > 0;
	}
	
	/**
	 * @description - set upload flag in vote table to Y if vote has been uploaded
	 * @param id
	 * @return
	 */
	public boolean setUploadFlagOnVote(long id,String newValue){
		this.open();
		//database = db.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_VOTE_FlagUploaded,newValue);
		int action=database.update(TBL_VOTE, values, KEY_VOTE_PriorityListId+"="+id, null);
		
		this.close();
		return  action > 0;
	}
	
	/**
	 * @description - set export flag in vote table to Y if vote has been exported
	 * @param id
	 * @return
	 */
	public boolean setExportFlagOnVote(long id,String newValue){
		this.open();
		//database = db.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_VOTE_FlagExported,newValue);
		int action=database.update(TBL_VOTE, values, KEY_VOTE_PriorityListId+"="+id, null);
		this.close();
		return  action > 0;
	}
	
	
	/**
	 * @description - save prioritylist to the database
	 * @param prioritylistid
	 * @param VoteIDCorePriorities
	 * @param SuggestedPriority
	 * @return
	 */
	public long save_prioritylist(long prioritylistid,String VoteIDCorePriorities,String SuggestedPriority){
		this.open();
		ContentValues values = new ContentValues();
		values.put(KEY_PRIORITY_PriorityListId,prioritylistid);
		values.put(KEY_PRIORITY_VoteIDCorePriorities,VoteIDCorePriorities);
		values.put(KEY_PRIORITY_SuggestedPriority,SuggestedPriority);
		long val =  database.insert(TBL_PriorityList, null, values);
		this.close();
		Log.d(TAG, "prioritylist_saved");
		return val;
	}
	/**
	 * @param id
	 * @description - return String array of prioritylist details
	 * @return String [] --> core_priorities @ 0 and sgsted priorities @ 1
	 */
	public String [] get_prioritylist_byId(long id) {
		//this.open();
		Cursor cursor = database.query(TBL_PriorityList, new String [] {KEY_PRIORITY_VoteIDCorePriorities,KEY_PRIORITY_SuggestedPriority},KEY_PRIORITY_PriorityListId+"="+id, null, null, null, null);
		
		String core_p = null;
		String sgstd_p = null;
		if(cursor.getCount() > 0){
			while(cursor.moveToNext()){
				core_p = cursor.getString( cursor.getColumnIndex(KEY_PRIORITY_VoteIDCorePriorities) );
				sgstd_p = cursor.getString( cursor.getColumnIndex(KEY_PRIORITY_SuggestedPriority) );
			}
			//cursor.close();
			//this.close();
			return new String [] {core_p,sgstd_p};
		}
		else{
			//cursor.close();
			//this.close();
			return null;
		}
	}
	
	
	/**
	 * @description -return an array of type long of all the found priority ids
	 * */
	public long [] getAllPriorityIds(){
		long priority_id=0;
		long [] priorities=null;
		this.open();
		//database = db.getWritableDatabase();
		 Cursor cursor=database.query(TBL_PriorityList, 
					new String[] {KEY_PRIORITY_PriorityListId
		}, null, null, null, null, null);
		 if(cursor.getCount() > 0){
			 int next_index=0;
			 priorities=new long[cursor.getCount()];
				while(cursor.moveToNext()){
					priority_id = cursor.getLong( cursor.getColumnIndex(KEY_PRIORITY_PriorityListId) );
					priorities[next_index]=priority_id;
					next_index++;
				}
		 }
		//db.close();
		//cursor.close();
		this.close();
		return priorities;
	}
	
	//get a count of all the locally stored votes
	/**
	 * @description -get count of saved votes
	 * @param none
	 * @return rowCount
	 */
	public int getTotalVotes(){//return total votes with the export flag set to N
		//this.open();
		//database = db.getWritableDatabase();
		 Cursor cursor=database.query(TBL_PriorityList, 
					new String[] {KEY_PRIORITY_PriorityListId
		}, KEY_PRIORITY_FlagUploaded+"='N'", null, null, null, null);
		
		rowCount = cursor.getCount();
		//db.close();
		cursor.close();
		//this.close();
		return rowCount;
	}
	
	/**
	 * @description - empty prioritylist table
	 * @param id
	 * @return
	 */
	public boolean deletePrioritylist(long id){
		this.open();
		//database = db.getWritableDatabase();
		int action=database.delete(TBL_PriorityList, KEY_PRIORITY_PriorityListId+"="+id, null);
		this.close();
		return  action > 0;
	}
	
	/**
	 * @description - set upload flag in prioritylist table to Y if vote has been uploaded
	 * @param id
	 * @return
	 */
	public boolean setUploadFlagOnPrioritylist(long id,String newValue){
		this.open();
		//database = db.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_PRIORITY_FlagUploaded,newValue);
		int action=database.update(TBL_PriorityList, values, KEY_PRIORITY_PriorityListId+"="+id, null);
		
		this.close();
		return  action > 0;
	}
	
	/**
	 * @description - set export flag in prioritylist table to Y if vote has been exported
	 * @param id
	 * @return
	 */
	public boolean setExportFlagOnPrioritylist(long id,String newValue){
		this.open();
		//database = db.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_PRIORITY_FlagExported,newValue);
		int action=database.update(TBL_PriorityList, values, KEY_PRIORITY_PriorityListId+"="+id, null);
		this.close();
		return  action > 0;
	}
	
	/**
	 * @description - save priority to the database
	 * @param priorityid
	 * @param title
	 * @param desc
	 * @return
	 */
	public long save_priority(int priorityid,String title,String desc){
		this.open();
		ContentValues values = new ContentValues();
		values.put(KEY_PRIORITY_PriorityId,priorityid);
		values.put(KEY_PRIORITY_Title,title);
		values.put(KEY_PRIORITY_Desc,desc);
		long val =  database.insert(TBL_PRIORITY, null, values);
		this.close();
		Log.d(TAG, "priority_saved");
		return val;
	}
	/**
	 * @description - return json object of priority details
	 * @return
	 * @throws JSONException
	 */
	public JSONObject get_priority() throws JSONException{
		//this.open();
		Cursor cursor = database.query(TBL_PRIORITY, new String [] {KEY_PRIORITY_PriorityId,KEY_PRIORITY_Title,KEY_PRIORITY_Desc},null, null, null, null, null);
		int priority_id = 0;
		String priority_title = null;
		String priority_desc = null;
		if(cursor.getCount() > 0){
			while(cursor.moveToNext()){
				priority_id = cursor.getInt( cursor.getColumnIndex(KEY_PRIORITY_PriorityId) ); 
				priority_title = cursor.getString( cursor.getColumnIndex(KEY_PRIORITY_Title) );
				priority_desc = cursor.getString( cursor.getColumnIndex(KEY_PRIORITY_Desc) );
			}
			cursor.close();
			this.close();
			JSONObject json = new JSONObject("{\"PriorityId\":\""+priority_id+"\",\"Title\":\""+priority_title+"\",\"Description\":\""+priority_desc+"\"}");
			return json;
		}
		else{
			cursor.close();
			//this.close();
			return null;
		}
	}
	
	/**
	 * @description - empty priority table
	 * @param id
	 * @return
	 */
	public boolean deletePriority(){
		return database.delete(TBL_PRIORITY, null, null) > 0;
	}
	
	/**
	 * @description -returns a join of voteinfo and prioritylist in preparation for a csv file
	 * */
	public Cursor getCSVFriendlyResultSet(String eFlag,String uFlag){
		final String myQuery="SELECT v.VoteId,v.PartnerId,v.Country,v.Gender,v.Age,p.VoteIDCorePriorities,p.SuggestedPriority,v.VotedDate FROM VoteInfo v, PriorityList p WHERE p.PriorityListId=v.PriorityListId AND v.FlagExported=? AND p.FlagUploaded=?";
		 Cursor cursor=database.rawQuery(myQuery, new String[]{eFlag,uFlag});
		 
		return cursor;
	}
}
