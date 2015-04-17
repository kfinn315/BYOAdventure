package com.bingcrowsby.byoadventure.Controller;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bingcrowsby.byoadventure.Model.AdventureDbOpenHelper;
import com.bingcrowsby.byoadventure.Model.AdventureObject;
import com.bingcrowsby.byoadventure.R;

/**
 * Created by kevinfinn on 4/10/15.
 */
public class AdventureAdapter extends CursorAdapter{
    static String tag = "AdventureAdapter";
//    List<AdventureObject> list = null;


        public AdventureAdapter(Context context, Cursor cursor) {
            super(context, cursor);
//            list = dataList;
        }

//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            if (convertView == null)
//                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
//
//            //selectedIndexList = position;
//            Log.i(tag, "getView " + position);
//            TextView textView = (TextView) convertView.findViewById(R.id.text1);
//
//            textView.setText(list.get(position).madventureTitle);
//            textView.setFocusable(false);
//            textView.setClickable(false);
//
//            convertView.setFocusable(false);
//            convertView.setClickable(false);
//            return convertView;
//        }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View newView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);

        //selectedIndexList = position;
//        Log.i(tag, "getView " + position);
        return newView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textView = (TextView) view.findViewById(R.id.text1);

        textView.setText(cursor.getString(cursor.getColumnIndex(AdventureDbOpenHelper.ROW_TITLE)));
        textView.setFocusable(false);
        textView.setClickable(false);

        view.setFocusable(false);
        view.setClickable(false);
    }

//    @Override
//        public int getCount() {
//            return list.size();
//        }

//        @Override
//        public void notifyDataSetChanged() {
//            super.notifyDataSetChanged();
//
//            Log.i(tag, "notify dataset changed for list adapter");
//
//        }

//        @Override
//        public AdventureObject getItem(int position) {
//            Log.i(tag, "list adapter retrieves item " + position);
//            try {
//                return super.getItem(position);
//            } catch (IndexOutOfBoundsException e) {
//                return null;
//            }
//        }


    @Override
    public AdventureObject getItem(int position) {
        Cursor cursor = getCursor();
        if(cursor.moveToPosition(position))
            return AdventureObject.fromCursor(cursor);
        else return null;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetInvalidated() {
        super.notifyDataSetInvalidated();
    }

    @Override
    protected void onContentChanged() {
        super.onContentChanged();
    }


    @Override
    public Cursor swapCursor(Cursor newCursor) {
        return super.swapCursor(newCursor);
    }
}
