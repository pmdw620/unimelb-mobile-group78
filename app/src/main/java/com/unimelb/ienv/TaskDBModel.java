package com.unimelb.ienv;

import android.content.ContentValues;

public class TaskDBModel {
    public static final String TABLE_NAME = "TaskCompleter";
    public static final String ID = "id";
    public static final String RUBBISH= "rubbish";
    public static final String DINING= "dining";
    public static final String WALK = "walk";
    public static final String QUIZ = "quiz";

    int id=1;
    int rubbish=0;
    int dining=0;
    int walk=0;
    int quiz=0;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id= id;
    }

    public int getRubbish() {
        return rubbish;
    }

    public void setRubbish(int rubbish) {
        this.rubbish= rubbish;
    }
    public int getdining() {
        return dining;
    }

    public void setDining(int dining) {
        this.dining= dining;
    }
    public int getWalk() {
        return walk;
    }

    public void setWalk(int walk) {
        this.walk= walk;
    }
    public int getQuiz() {
        return quiz;
    }

    public void setQuiz(int quiz) {
        this.quiz= quiz;
    }

    public ContentValues toContentValues(){
        ContentValues cv = new ContentValues();





        cv.put(ID, id);
        cv.put(RUBBISH,rubbish );
        cv.put(DINING,dining );
        cv.put(WALK,walk );
        cv.put(QUIZ,quiz );
        return cv;
    }

}
