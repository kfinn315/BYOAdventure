package com.bingcrowsby.byoadventure.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bingcrowsby.byoadventure.Controller.CustomGson;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import static com.bingcrowsby.byoadventure.Model.AdventureDbOpenHelper.ROW_DATE_CREATED;
import static com.bingcrowsby.byoadventure.Model.AdventureDbOpenHelper.ROW_DATE_UPDATED;
import static com.bingcrowsby.byoadventure.Model.AdventureDbOpenHelper.ROW_ID;
import static com.bingcrowsby.byoadventure.Model.AdventureDbOpenHelper.ROW_OBJECT;
import static com.bingcrowsby.byoadventure.Model.AdventureDbOpenHelper.ROW_TITLE;
import static com.bingcrowsby.byoadventure.Model.AdventureDbOpenHelper.TABLE_NAME;

/**
 * Created by kevinfinn on 1/27/15.
 */
public class AdventureDatasource {
    static final String tag = "advDatasource";
    AdventureDbOpenHelper dbHelper;
    SQLiteDatabase database;
    static AdventureDatasource datasource;
    private Gson gson;


    public AdventureDatasource(Context context) {
        dbHelper = new AdventureDbOpenHelper(context);
        gson = CustomGson.getGson();
    }

    public static AdventureDatasource getInstance(Context context){
        if(datasource == null){
            datasource = new AdventureDatasource(context);
        }

        return datasource;
    }

    public void open(){
        Log.i(tag,"open database");

        database = dbHelper.getWritableDatabase();
    }


    public void close(){
        Log.i(tag,"close database");

        dbHelper.close();
    }

    public long saveAdventure(AdventureObject object){
        Log.i(tag,"save adventure "+object.madventureTitle);

        String title = object.madventureTitle;
        Integer id = object.id;
        ContentValues values = new ContentValues();
        String jsonstrng = gson.toJson(object);
        values.put(ROW_OBJECT, jsonstrng);
        values.put(ROW_DATE_UPDATED, System.currentTimeMillis());


        if(id==null) { //new adventure, is not in db
            values.put(ROW_TITLE, title);
            values.put(ROW_DATE_CREATED, System.currentTimeMillis());
            return database.insert(TABLE_NAME, null, values);
        }
        else{
            return database.update(TABLE_NAME, values, ROW_ID+" = "+id, null);
        }
    }

    public long insertAdventureObject(AdventureObject object){
        String title = object.madventureTitle;
//        Integer id = object.id;
        ContentValues values = new ContentValues();
        String jsonstrng = gson.toJson(object);
        values.put(ROW_OBJECT, jsonstrng);
        values.put(ROW_TITLE, title);
        values.put(ROW_DATE_CREATED, object.mdateCreated.getTime());
        values.put(ROW_DATE_UPDATED, object.mdateUpdated.getTime());
        Log.i("test",values.toString());
        return database.insert(TABLE_NAME, null, values);
    }

    public AdventureObject getAdventure(long id){
        Log.i(tag,"get adventure "+id);

        AdventureObject object = null;
        Cursor cursor = database.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+ROW_ID+" = "+id, null);
        if(cursor.moveToFirst()){
            object = AdventureObject.fromCursor(cursor);
        }
        cursor.close();
        return object;
    }


    private Cursor getAll(){
        return database.rawQuery("select * from " + TABLE_NAME, null);

    }
    public Cursor getAdventureCursor(){
        Cursor cursor = getAll();
        cursor.moveToFirst();
        return cursor;
    }
    public ArrayList<AdventureObject> getAllAdventures(){
        Log.i(tag,"get all adventures");

        ArrayList adventureList = new ArrayList();

        Cursor cursor = getAdventureCursor();

        while(cursor.moveToNext()){
            adventureList.add(AdventureObject.fromCursor(cursor));
        }

        return adventureList;
    }

    public void saveAdventures(List<AdventureObject> advList){
        Log.i(tag,"saving adventure list");
        for(AdventureObject adv : advList){
            saveAdventure(adv);
        }
    }

    public int clearAdventures(){
        return database.delete(TABLE_NAME,null,null);
    }
     public int getCount(){
         Cursor cursor = database.rawQuery("SELECT Count(*) FROM "+TABLE_NAME,null);
         cursor.moveToFirst();
         return cursor.getInt(0);
     }

    public boolean isOpen(){
        if(database == null)
            return false;

        return database.isOpen();
    }
}