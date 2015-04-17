package com.bingcrowsby.byoadventure;

import android.content.Context;
import android.database.Cursor;

import com.bingcrowsby.byoadventure.Model.AdventureDatasource;
import com.bingcrowsby.byoadventure.Model.AdventureObject;

import java.util.List;

/**
 * Created by kevinfinn on 4/9/15.
 */
public class AdventureData {
    private static AdventureData adventureData;
//    private List<AdventureObject> adventureInventory;
    private static AdventureDatasource datasource = null;

    public static AdventureData getInstance(Context context){
        if(adventureData == null)
            adventureData = new AdventureData(context);
        return adventureData;
    }
    private AdventureData(Context context){
        if(datasource == null) {
            datasource = new AdventureDatasource(context);
            datasource.open();
        }
    }
    public void open(){
        if(!datasource.isOpen())
            datasource.open();
    }
    public void close(){
        if(datasource.isOpen())
            datasource.close();
        adventureData = null;
    }
//    public void saveAdventureInventory() {
//        datasource.saveAdventures(adventureInventory);
//    }

    public long saveAdventure(AdventureObject adventure) {
        //update cache
//        if (index != null && index >= 0 && index < adventureInventory.size())
//            adventureInventory.add(index, adventure);

        datasource.open();
        long id = datasource.saveAdventure(adventure);
        datasource.close();

        return id;
    }

//    public void loadAdventureInventory() {
//        adventureInventory = datasource.getAllAdventures();
//    }

    public int clearAdventureInventory() {
        return datasource.clearAdventures();
    }

    public List<AdventureObject> getAdventureList() {
        return datasource.getAllAdventures();
    }

    public Cursor getAdventureCursor(){
        return datasource.getAdventureCursor();
    }

    public int getIndexOfAdventureWithId(int id) {
        List<AdventureObject> list = getAdventureList();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).id == id)
                return i;
        }
        return -1;
    }

//    public AdventureObject getAdventure(int index) {
//        getAdventureList();
//        if (adventureInventory != null && (index >= 0 && index < adventureInventory.size()))
//            return adventureInventory.get(index);
//
//        return null;
//    }

//    public int getAdventureId(int index) {
//        AdventureObject adventureObject = getAdventure(index);
//        if (adventureObject != null)
//            return adventureObject.id;
//        else
//            return -1;
//    }

    public AdventureObject getAdventureFromId(long id) {

        if (id > 0) {
            datasource.open();
            return datasource.getAdventure(id);
        }

        return null;
    }

    public int getAdventureCount() {
        datasource.open();
        return datasource.getCount();
    }

//    public String getAdventureTitle(int position) {
//        AdventureObject adv = getAdventureList().get(position);
//        if(adv!=null&&adv.madventureTitle!=null)
//            return "ADVENTURE '"+ adv.madventureTitle +"'";
//        else
//            return "ADVENTURE ???";
//    }

}
