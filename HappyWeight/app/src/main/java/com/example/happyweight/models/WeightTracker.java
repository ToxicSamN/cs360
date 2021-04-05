package com.example.happyweight.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.icu.text.AlphabeticIndex;
import android.util.Log;
import android.widget.DatePicker;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WeightTracker extends Database {

    public WeightTracker(@Nullable Context context) {
        super(context);
    }

    public WeightRecord[] getAllUserWeightRecords(@NonNull String user_id){
        SQLiteDatabase db = getWritableDatabase();
        WeightRecord[] records;

        String sql_query = "SELECT _id, user, date, weight FROM " + Database.WeightTrackerTableConst.TABLE +
                " WHERE user = ?";
        Cursor cursor = db.rawQuery(sql_query, new String[] { user_id });
        // deserialize all of the records
        records = this.DeserializeRecords(cursor);
        cursor.close();

        return records;
    }

    public WeightRecord getWeightRecord(@NonNull String _id){
        SQLiteDatabase db = getWritableDatabase();
        WeightRecord record = new WeightRecord();

        String sql_query = "SELECT _id, user, date, weight FROM " + Database.WeightTrackerTableConst.TABLE +
                " WHERE _id = ?";
        Cursor cursor = db.rawQuery(sql_query, new String[] { _id });
        // only one record will be returned here, no need for a while() loop for cursor.moveToNext()
        // cursor starts at -1 so need to moveToNext() in order to get the first and only record
        cursor.moveToNext();
        this.DeserializeRecord(cursor);
        cursor.close();

        return record;
    }

    public long addWeightRecord(@NonNull String user_id, @NonNull String date, @NonNull int weight){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues vals = new ContentValues();
        vals.put(Database.WeightTrackerTableConst.USER, user_id);
        vals.put(Database.WeightTrackerTableConst.DATE, date);
        vals.put(Database.WeightTrackerTableConst.WEIGHT, weight);

        return db.insert(Database.WeightTrackerTableConst.TABLE, null, vals);
    }

    public long updateWeightRecord(@NonNull String _id, @NonNull String user_id, @NonNull String date, @NonNull int weight){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues vals = new ContentValues();
        vals.put(Database.WeightTrackerTableConst.USER, user_id);
        vals.put(Database.WeightTrackerTableConst.DATE, date);
        vals.put(Database.WeightTrackerTableConst.WEIGHT, weight);

        return db.update(Database.WeightTrackerTableConst.TABLE, vals,"_id = ?", new String[]{_id});
    }

    public long deleteWeightRecord(@NonNull String _id){
        SQLiteDatabase db = getWritableDatabase();

        return db.delete(Database.WeightTrackerTableConst.TABLE, "_id = ?", new String[]{ _id });
    }

    private WeightRecord DeserializeRecord(Cursor cursor) {
        WeightRecord record = new WeightRecord();

        // build the record object
        record.setId(cursor.getInt(
                cursor.getColumnIndexOrThrow(Database.WeightTrackerTableConst.ID)
        ));
        record.setUser(cursor.getString(
                cursor.getColumnIndexOrThrow(Database.WeightTrackerTableConst.USER)
        ));
        record.setDate(cursor.getString(
                cursor.getColumnIndexOrThrow(Database.WeightTrackerTableConst.DATE)
        ));
        record.setWeight(cursor.getInt(
                cursor.getColumnIndexOrThrow(Database.WeightTrackerTableConst.WEIGHT)
        ));

        return record;
    }

    private WeightRecord[] DeserializeRecords(Cursor cursor) {
        WeightRecord[] records = new WeightRecord[cursor.getCount()];
        int index = 0;

        // cursor starts at -1 so need to moveToNext() in order to get the first and only record
        while(cursor.moveToNext()) {
            records[index]  = this.DeserializeRecord(cursor);
            index++;
        }
        return records;
    }

    public String getDateFormat(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "MM-dd-yyyy", Locale.getDefault());
        return dateFormat.format(date);
    }

    public Date convertStringToDate(String sDate){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        try {
            Date date = simpleDateFormat.parse(sDate);
            return date;
        }catch (ParseException ex) {
            Log.v("Exception", ex.getLocalizedMessage());
        }
        return null;
    }

}
