package org.cybrosys.customisation;

import java.util.ArrayList;
import java.util.UUID;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TintDatabase extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "TintDB";

	public TintDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

		String CREATE_USER_ACCOUNT = "CREATE TABLE IF NOT EXISTS RESTORE ( "
				+ Constants.ID + " TEXT  , " + Constants.URL + " TEXT) ";
		db.execSQL(CREATE_USER_ACCOUNT);

	}

	public void insertUrl(String url, UUID uuid) {
		if (url == null)
			return;
		ContentValues cv = new ContentValues();
		cv.put(Constants.URL, url);
		cv.put(Constants.ID, uuid.toString());
		SQLiteDatabase database = this.getWritableDatabase();
		try {
			String CREATE_USER_ACCOUNT = "CREATE TABLE IF NOT EXISTS RESTORE ( "
					+ Constants.ID + " TEXT  , " + Constants.URL + " TEXT) ";
			database.execSQL(CREATE_USER_ACCOUNT);

			String exstQuery = "SELECT  " + Constants.ID
					+ " FROM RESTORE WHERE " + Constants.ID + " = ?";
			Cursor cursor = database.rawQuery(exstQuery,
					new String[] { uuid.toString() });
			
			
			if (cursor.getCount() == 0) {
				
				database.insertOrThrow("RESTORE", null, cv);

			}
			cursor.close();
		} catch (Exception e) {
		}
		database.close();
		
	}

	public void clearDb() {
		SQLiteDatabase database = this.getWritableDatabase();
		database.delete("RESTORE", null, null);
	}

	public ArrayList<String> getUrls() {
		ArrayList<String> urls = new ArrayList<String>();
		SQLiteDatabase database = this.getWritableDatabase();
		String query = "SELECT  " + Constants.URL + " FROM RESTORE";
		Cursor c = database.rawQuery(query, null);
		c.moveToFirst();
		do {
			if (c.getCount() > 0 )
				urls.add(c.getString(c.getColumnIndex(Constants.URL)));
		} while (c.moveToNext());
		c.close();
		database.close();
		return urls;
	}
	public void removeClosedTabData(UUID uuid){
		SQLiteDatabase database = this.getWritableDatabase();
		database.delete("RESTORE", Constants.ID + " = ?", new String[]{uuid.toString()});
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
}
