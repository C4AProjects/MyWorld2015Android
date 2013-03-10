package org.un.myworld.data.sync;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 
 * 
 * @author kinyua
 * @description My World database class create databases and associated tables:
 *********************   VoteInfo  *************  PriorityList  ********************************	 Priority  ****************
 *						-VoteId(INT)			 -PriorityListId(LONG)								-PriorityId(INT) 
 *						-VoterId(INT)			 -VoteIDCorePriorities(TEXT)//From priority Ids.	-Title(TEXT)
 *						-PartnerID(INT)			 -SuggestedPriority(TEXT)							-Description(TEXT)
 *						-Country(TEXT)	
 *						-Region_state(TEXT)	
 *						-City(TEXT)
 *					 	-Latitude(DOUBLE)
 *						-Longitude(DOUBLE)
 *						-VotedDate(TEXT)
 *						-Gender(TEXT)
 *						-YearOfBirth(INT)
 *						-Education(TEXT)
 *						-Reason(TEXT)
 *						-PriorityListId(LONG)
 */								
public class DB extends SQLiteOpenHelper {
	private static final String DB_NAME = "DbMyWorld";
	public static final int DB_VERSION = 2;
	
	private static final String TBL_VOTE = "VoteInfo";
	private static final String TBL_PRIORITYLIST = "PriorityList";
	private static final String TBL_PRIORITY = "Priority";
	
	private static final String TAG = "com.data.sync";

	/**
	  * @Constructor
	  * @param context
	  */
	public DB(Context context) {
		 super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		String sql_tbl_vote = "CREATE TABLE VoteInfo('VoteId' LONG PRIMARY KEY NOT NULL,'VoterId' LONG NOT NULL,'PartnerID' TEXT NOT NULL, 'Country' TEXT,'City' TEXT NOT NULL,'Region_state' TEXT NOT NULL,'Latitude' TEXT NOT NULL,'Longitude' TEXT NOT NULL,'Gender' TEXT NOT NULL,'VotedDate' TEXT NOT NULL,'YearOfBirth' INTEGER NOT NULL,'Education' TEXT NOT NULL,'PriorityListId' LONG NOT NULL,'Reason' TEXT)";
		String sql_tbl_proritylist = "CREATE TABLE PriorityList('PriorityListId' LONG PRIMARY KEY NOT NULL,'VoteIDCorePriorities' TEXT NOT NULL,'SuggestedPriority' TEXT)";
		String sql_tbl_prority = "CREATE TABLE Priority('PriorityId' INTEGER PRIMARY KEY NOT NULL,'Title' TEXT,'Description' TEXT)";
		
		db.execSQL(sql_tbl_vote);
		db.execSQL(sql_tbl_proritylist);
		db.execSQL(sql_tbl_prority);
		
		Log.d(TAG, "Db+Tables created");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists " + TBL_PRIORITY);
		db.execSQL("drop table if exists " + TBL_PRIORITYLIST);
		db.execSQL("drop table if exists " + TBL_VOTE);
		
		this.onCreate(db);
		Log.d(TAG, "Upgraded");
	}
}
