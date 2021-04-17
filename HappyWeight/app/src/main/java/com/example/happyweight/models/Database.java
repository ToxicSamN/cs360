package com.example.happyweight.models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class Database extends SQLiteOpenHelper {

    public static final String DBNAME = "happyweight.db";
    public static final int VERSION = 9;

    public Database(@Nullable Context context) {
        super(context, DBNAME, null, VERSION);
    }

    public static final class WeightGoalTableConst {
        public static final String TABLE = "tblWeightGoal";
        public static final String ID = "_id";
        public static final String USER = "user";
        public static final String WEIGHT = "goal_weight";
    }

    public static final class WeightTrackerTableConst {
        public static final String TABLE = "tblWeightTracker";
        public static final String ID = "_id";
        public static final String USER = "user";
        public static final String DATE = "date";
        public static final String WEIGHT = "weight";
    }

    public static final class UserTableConst {
        public static final String TABLE = "tblUserData";
        public static final String ID = "_id";
        public static final String USERID = "userID";
        public static final String FNAME = "firstName";
        public static final String LNAME = "lastName";
        public static final String SMS = "SMSEnabled";
        public static final String TELNO = "TelephoneNo";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + WeightGoalTableConst.TABLE + " (" +
                WeightGoalTableConst.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                WeightGoalTableConst.USER + " TEXT UNIQUE, " +
                WeightGoalTableConst.WEIGHT + " INTEGER" +
                ")");

        db.execSQL("CREATE TABLE " + WeightTrackerTableConst.TABLE + " (" +
                WeightTrackerTableConst.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                WeightTrackerTableConst.USER + " TEXT, " +
                WeightTrackerTableConst.DATE + " TEXT, " +
                WeightTrackerTableConst.WEIGHT + " INTEGER" +
                ")");
        db.execSQL("CREATE TABLE " + UserTableConst.TABLE + " (" +
                UserTableConst.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                UserTableConst.USERID + " TEXT UNIQUE, " +
                UserTableConst.FNAME + " TEXT, " +
                UserTableConst.LNAME + " TEXT, " +
                UserTableConst.SMS + " INTEGER, " + // BOOLEAN = int 0 and int 1
                UserTableConst.TELNO + " TEXT" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + WeightTrackerTableConst.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + WeightGoalTableConst.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + UserTableConst.TABLE);
        onCreate(db);
    }

}
