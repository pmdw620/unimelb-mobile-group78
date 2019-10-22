package com.unimelb.ienv;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TaskDBOpener extends SQLiteOpenHelper {
    public static final String DB_NAME = "mysql1.db";
    private static final int VERSION = 1;
    public TaskDBOpener(Context context) {
        super(context, DB_NAME, null, VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TaskDBModel.TABLE_NAME
                + String.format(
                "("
                        + "%s INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "%s INTEGER, "
                        + "%s INTEGER,"
                        + "%s INTEGER,"
                        + "%s INTEGER"
                        +")"
                , TaskDBModel.ID
                , TaskDBModel.RUBBISH
                , TaskDBModel.DINING
                , TaskDBModel.WALK
                , TaskDBModel.QUIZ
        )) ;
        TaskDBModel task = new TaskDBModel();
        db.insert(TaskDBModel.TABLE_NAME, null, task.toContentValues());
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
