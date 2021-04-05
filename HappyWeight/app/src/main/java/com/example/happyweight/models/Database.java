package com.example.happyweight.models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class Database extends SQLiteOpenHelper {

    public static final String DBNAME = "happyweight.db";
    public static final int VERSION = 6;

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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + WeightTrackerTableConst.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + WeightGoalTableConst.TABLE);
        onCreate(db);
    }

}
