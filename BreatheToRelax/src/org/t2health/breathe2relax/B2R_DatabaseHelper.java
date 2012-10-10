package org.t2health.breathe2relax;

import java.sql.SQLException;

import org.t2health.lib.db.DatabaseOpenHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class B2R_DatabaseHelper extends DatabaseOpenHelper {
	private static final String TAG = B2R_DatabaseHelper.class.getSimpleName();

	public B2R_DatabaseHelper(Context context, String dbName,
			CursorFactory cursorFactory, int dbVersion) {
		super(context, dbName, cursorFactory, dbVersion);
	}

	@Override
	public void onCreate(SQLiteDatabase database,
			ConnectionSource connectionSource) {

		Log.v(TAG, "OnCreate DB");

		try {
			TableUtils.createTable(connectionSource, B2R_MoodTrackingsTable.class);

		} catch (SQLException e) {
			Log.d("B2R_DatabaseHelper", "Exception", e);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase database,
			ConnectionSource connectionSource, int oldVersion, int newVersion) {

		Log.v(TAG, "OnUpdate DB");
	}

}
