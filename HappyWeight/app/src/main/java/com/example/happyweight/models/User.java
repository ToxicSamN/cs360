package com.example.happyweight.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class User extends Database {

    public User(@Nullable Context context) {
        super(context);
    }

    public UserRecord getUserDataById(@NonNull String _id) {
        SQLiteDatabase db = getWritableDatabase();
        UserRecord record = new UserRecord();

        String sql_query = "SELECT _id, userID, firstName, lastName, SMSEnabled, TelephonNo FROM " + Database.UserTableConst.TABLE +
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

    public UserRecord[] getAllUserData() {
        SQLiteDatabase db = getWritableDatabase();
        UserRecord record = new UserRecord();

        String sql_query = "SELECT _id, userID, firstName, lastName, SMSEnabled, TelephoneNo FROM " + Database.UserTableConst.TABLE;
        Cursor cursor = db.rawQuery(sql_query, null);
        if (cursor.getCount() <= 0){
            return null;
        }
        // only one record will be returned here, no need for a while() loop for cursor.moveToNext()
        // cursor starts at -1 so need to moveToNext() in order to get the first and only record
        UserRecord[] records = new UserRecord[cursor.getCount()];
        int index = 0;

        // cursor starts at -1 so need to moveToNext() in order to get the first and only record
        while(cursor.moveToNext()) {
            records[index]  = this.DeserializeRecord(cursor);
            index++;
        }
        cursor.close();

        return records;
    }

    public UserRecord getUserRecordByUserId(@NonNull String user_id) {
        SQLiteDatabase db = getWritableDatabase();
        UserRecord record = new UserRecord();

        String sql_query = "SELECT _id, userID, firstName, lastName, SMSEnabled, TelephoneNo FROM " + Database.UserTableConst.TABLE +
                " WHERE userID = ?";
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

    public long updateUserRecord(@NonNull String user_id, @NonNull int SMS, @NonNull String TelNo){
        long result;
        SQLiteDatabase db = getWritableDatabase();

        ContentValues vals = new ContentValues();
        vals.put(UserTableConst.USERID, user_id);
        vals.put(Database.UserTableConst.SMS, SMS);
        vals.put(Database.UserTableConst.TELNO, TelNo);

        // two step approach
        // attempt to insert record, if conflict then update existing record
        result = db.insertWithOnConflict(Database.UserTableConst.TABLE,null,vals,SQLiteDatabase.CONFLICT_IGNORE);
        if (result == -1) {
            // conflict existed
            result = db.update(Database.UserTableConst.TABLE, vals,"userID = ?", new String[]{ user_id });
        }
        return result;
    }

    public long updateORcreateUserRecord(@NonNull String user_id, @NonNull String firstName, @NonNull String lastName){
        long result;
        SQLiteDatabase db = getWritableDatabase();

        ContentValues vals = new ContentValues();
        vals.put(UserTableConst.USERID, user_id);
        vals.put(UserTableConst.FNAME, firstName);
        vals.put(UserTableConst.FNAME, lastName);

        // two step approach
        // attempt to insert record, if conflict then update existing record
        result = db.insertWithOnConflict(Database.UserTableConst.TABLE,null,vals,SQLiteDatabase.CONFLICT_IGNORE);
        if (result == -1) {
            // conflict existed
            result = db.update(Database.UserTableConst.TABLE, vals,"userID = ?", new String[]{ user_id });
        }
        return result;
    }

    private UserRecord DeserializeRecord(Cursor cursor) {
        UserRecord record = new UserRecord();

        // build the record object
        record.setId(cursor.getInt(
                cursor.getColumnIndexOrThrow(Database.UserTableConst.ID)
        ));
        record.setUserID(cursor.getString(
                cursor.getColumnIndexOrThrow(UserTableConst.USERID)
        ));
        record.setSMS(cursor.getInt(
                cursor.getColumnIndexOrThrow(UserTableConst.SMS)
        ));
        record.setFirstName(cursor.getString(
                cursor.getColumnIndexOrThrow((UserTableConst.FNAME))
        ));
        record.setLastName(cursor.getString(
                cursor.getColumnIndexOrThrow((UserTableConst.LNAME))
        ));
        record.setTelNo(cursor.getString(
                cursor.getColumnIndexOrThrow((UserTableConst.TELNO))
        ));

        return record;
    }
}
