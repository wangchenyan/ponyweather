/**
 * 2015-3-26
 */
package me.wcy.weather.database;

import java.sql.SQLException;

import me.wcy.weather.model.LifeIndex;
import me.wcy.weather.model.Weather;
import me.wcy.weather.model.WeatherInfo;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * @author wcy
 * 
 */
public class DBHelper extends OrmLiteSqliteOpenHelper {
	private static final String DATABASE_NAME = "weather";
	private static final int DATABASE_VERSION = 1;

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase arg0, ConnectionSource arg1) {
		try {
			TableUtils.createTableIfNotExists(connectionSource, Weather.class);
			TableUtils.createTableIfNotExists(connectionSource,
					WeatherInfo.class);
			TableUtils
					.createTableIfNotExists(connectionSource, LifeIndex.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, ConnectionSource arg1, int arg2,
			int arg3) {
		try {
			TableUtils.dropTable(connectionSource, Weather.class, true);
			TableUtils.dropTable(connectionSource, WeatherInfo.class, true);
			TableUtils.dropTable(connectionSource, LifeIndex.class, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		onCreate(arg0, arg1);
	}

}
