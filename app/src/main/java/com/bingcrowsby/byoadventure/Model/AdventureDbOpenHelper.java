package com.bingcrowsby.byoadventure.Model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by kevinfinn on 7/16/14.
 */
public class AdventureDbOpenHelper extends SQLiteOpenHelper {
    final static String DB_NAME="adventures.db";
    final static int DB_VERSION = 2;

    final static String TABLE_NAME = "Adventures";
    public final static String
            ROW_ID = "_id",
            ROW_TITLE = "title",
            ROW_OBJECT = "object",
            ROW_DATE_CREATED = "datecreated",
            ROW_DATE_UPDATED = "dateupdated";

    final static String CREATE_TABLE =
            "CREATE TABLE "+ TABLE_NAME +" ( "
                    +ROW_ID+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                    +ROW_TITLE+" VARCHAR(50) DEFAULT'',"
                    +ROW_DATE_CREATED+" INTEGER ,"
                    +ROW_DATE_UPDATED+" INTEGER, "
                    +ROW_OBJECT+" TEXT"
                    +")";
    final static String[] ALL_COLUMNS =
            {
                    ROW_TITLE,
                    ROW_OBJECT,
                    ROW_DATE_CREATED,
                    ROW_DATE_UPDATED
            };

    public AdventureDbOpenHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.i("query", CREATE_TABLE);
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if(newVersion > oldVersion) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(sqLiteDatabase);
        }
    }

}
