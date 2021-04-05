package com.example.happyweight.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class WeightGoal extends Database {

    public WeightGoal(@Nullable Context context) {
        super(context);
    }





    public WeightGoalRecord getGoalRecordById(@NonNull String _id) {
        SQLiteDatabase db = getWritableDatabase();
        WeightGoalRecord record = new WeightGoalRecord();

        String sql_query = "SELECT _id, user, weight FROM " + Database.WeightGoalTableConst.TABLE +
                " WHERE _id = ?";
        Cursor cursor = db.rawQuery(sql_query, new String[]{_id});
        if (cursor.getCount() <= 0){
            return null;
        }
        // only one record will be returned here, no need for a while() loop for cursor.moveToNext()
        // cursor starts at -1 so need to moveToNext() in order to get the first and only record
        cursor.moveToNext();
        record = this.DeserializeRecord(cursor);
        cursor.close();

        return record;
    }

    public WeightGoalRecord getGoalRecordByUser(@NonNull String user_id) {
        SQLiteDatabase db = getWritableDatabase();
        WeightGoalRecord record = new WeightGoalRecord();

        String sql_query = "SELECT _id, user, goal_weight FROM " + Database.WeightGoalTableConst.TABLE +
                " WHERE user = ?";
        Cursor cursor = db.rawQuery(sql_query, new String[]{user_id});
        if (cursor.getCount() <= 0){
            return null;
        }
        // only one record will be returned here, no need for a while() loop for cursor.moveToNext()
        // cursor starts at -1 so need to moveToNext() in order to get the first and only record
        cursor.moveToNext();
        record = this.DeserializeRecord(cursor);
        cursor.close();

        return record;
    }

    public long updateWeightRecord(@NonNull String user_id, @NonNull int weight){
        long result;
        SQLiteDatabase db = getWritableDatabase();

        ContentValues vals = new ContentValues();
        vals.put(Database.WeightGoalTableConst.USER, user_id);
        vals.put(Database.WeightGoalTableConst.WEIGHT, weight);

        // two step approach
        // attempt to insert record, if conflict then update existing record
        result = db.insertWithOnConflict(Database.WeightGoalTableConst.TABLE,null,vals,SQLiteDatabase.CONFLICT_IGNORE);
        if (result == -1) {
            // conflict existed
            result = db.update(Database.WeightGoalTableConst.TABLE, vals,"user = ?", new String[]{ user_id });
        }
        return result;
    }

    private WeightGoalRecord DeserializeRecord(Cursor cursor) {
        WeightGoalRecord record = new WeightGoalRecord();

        // build the record object
        record.setId(cursor.getInt(
                cursor.getColumnIndexOrThrow(Database.WeightGoalTableConst.ID)
        ));
        record.setUser(cursor.getString(
                cursor.getColumnIndexOrThrow(Database.WeightGoalTableConst.USER)
        ));
        record.setWeight(cursor.getInt(
                cursor.getColumnIndexOrThrow(Database.WeightGoalTableConst.WEIGHT)
        ));

        return record;
    }
}