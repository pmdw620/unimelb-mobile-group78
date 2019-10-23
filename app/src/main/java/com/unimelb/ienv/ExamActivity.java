package com.unimelb.ienv;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.io.File;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileWriter;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import static com.unimelb.ienv.MainActivity.currentUser;

public class ExamActivity<count> extends AppCompatActivity {
    private int count;
    private int current;
    private  boolean wrongMode;
    private int currentScore;
    private FirebaseFirestore firedb;
    private Map<String, Object> user = new HashMap<>();
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);
        final List<Question> list = getQuestion();

        count = list.size();
        current = 0;
        wrongMode=false;

        context = this;
        final TextView tv_question = findViewById(R.id.question);
        final RadioButton[] radioButtons = new RadioButton[4];
        final TextView location = findViewById(R.id.quiz_index);
        radioButtons[0] = findViewById(R.id.answerA);
        radioButtons[1] = findViewById(R.id.answerB);
        radioButtons[2] = findViewById(R.id.answerC);
        radioButtons[3] = findViewById(R.id.answerD);
        ImageButton btn_previous = findViewById(R.id.btn_previous);
        ImageButton btn_next = findViewById(R.id.btn_next);
        final RadioGroup radioGroup = findViewById(R.id.radioGroup);
        //为控件赋值
        Question q = list.get(0);
        tv_question.setText(q.question);
        radioButtons[0].setText(q.answerA);
        radioButtons[1].setText(q.answerB);
        radioButtons[2].setText(q.answerC);
        radioButtons[3].setText(q.answerD);
        location.setText("     Location: 1/5");


        btn_next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (current < count - 1) {
                    current++;
                    Question q = list.get(current);
                    tv_question.setText(q.question);
                    radioButtons[0].setText(q.answerA);
                    radioButtons[1].setText(q.answerB);
                    if (q.answerC != null){
                        radioButtons[2].setText(q.answerC);
                    }
                    else{
                        radioButtons[2].setVisibility(View.INVISIBLE);
                    }
                    if (q.answerD != null){
                        radioButtons[3].setText(q.answerD);
                    }
                    else{
                        radioButtons[3].setVisibility(View.INVISIBLE);
                    }
                    location.setText("     Location: "+(current+1)+"/5");
                    radioGroup.clearCheck();
                    if (q.selectedAnswer != -1) {
                        radioButtons[q.selectedAnswer].setChecked(true);
                    }

                }
                else {
                    new AlertDialog.Builder(ExamActivity.this)
                            .setTitle("Tips")
                            .setMessage("You have reached the last question,Do you want to submit it?")
                            .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    final List<Integer> wrongList = checkAnswer(list);
                                    firedb = FirebaseFirestore.getInstance();
                                    String username = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    SQLiteOpenHelper dbHelper = new TaskDBOpener(context);
                                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                                    TaskDBModel task = new TaskDBModel();

                                    Cursor cursor = db.rawQuery("select * from TaskCompleter ",
                                            null);
                                    int id=1,dining=0,walk=0,quiz=0,rubbish = 0;
                                    while (cursor.moveToNext()) {
                                        id = Integer.parseInt(cursor.getString(0));
                                        rubbish = Integer.parseInt(cursor.getString(1));
                                        dining = Integer.parseInt(cursor.getString(2));
                                        walk = Integer.parseInt(cursor.getString(3));
                                        quiz = Integer.parseInt(cursor.getString(4));
                                    }
                                    task.setDining(dining);
                                    task.setQuiz((5 - wrongList.size()));
                                    task.setWalk(walk);
                                    task.setRubbish(rubbish);
                                    db.update(TaskDBModel.TABLE_NAME, task.toContentValues(),"id = ?", new String[]{String.valueOf(id)});
                                    firedb.collection("UserCollection").document(username).update("totalPoints", FieldValue.increment(5 - wrongList.size()));
                                    firedb.collection("UserCollection").document(username).update("currWeekPoints", FieldValue.increment(5 - wrongList.size()));
                                    if (wrongList.size() != 5) {
                                        new AlertDialog.Builder(ExamActivity.this)
                                                .setTitle("Congratulations")
                                                .setMessage("You got " + (list.size() - wrongList.size()) + " questions right and " + wrongList.size() + " wrong, you earn " + (list.size() - wrongList.size()) + " points")
                                                .setNegativeButton("finish", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int which) {
                                                        ExamActivity.this.finish();
                                                    }
                                                }).show();
                                    }
                                    else{
                                        new AlertDialog.Builder(ExamActivity.this)
                                                .setTitle("What a pity!")
                                                .setMessage("All questions are wrong. You get the chance to try again")
                                                .setNegativeButton("finish", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int which) {
                                                        ExamActivity.this.finish();
                                                    }
                                                }).show();
                                    }
                                }
                            })
                            .setNegativeButton("Let me think again", null)
                            .show();
                }
            }
        });
        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (current > 0)
                {
                    current--;
                    Question q = list.get(current);
                    tv_question.setText(q.question);
                    radioButtons[0].setText(q.answerA);
                    radioButtons[1].setText(q.answerB);
                    radioButtons[2].setText(q.answerC);
                    radioButtons[3].setText(q.answerD);


                    radioGroup.clearCheck();
                    if (q.selectedAnswer != -1) {
                        radioButtons[q.selectedAnswer].setChecked(true);
                    }

                }

            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                for (int i = 0; i < 4; i++) {
                    if (radioButtons[i].isChecked() == true) {
                        list.get(current).selectedAnswer = i;
                        break;
                    }
                }

            }
        });
    }

    private List<Integer> checkAnswer(List<Question> list) {
        List<Integer> wrongList = new ArrayList<Integer>();
        for(int i=0;i<list.size();i++)
        {
            if(list.get(i).answer!=list.get(i).selectedAnswer){
                wrongList.add(i);
            }
        }
        return wrongList;
    }
    public List<Question> getQuestion(){
        List<Question> question_list = new ArrayList<Question>();
        Set<Integer> question_no=new HashSet<Integer>();
        while(true) {
            int z = (int) (Math.random() * 48 + 1);
            question_no.add(z);
            if (question_no.size() >= 5) {
                break;
            }
        }
        try {
            InputStream input=getResources().openRawResource(R.raw.eco_quiz);
            InputStreamReader reader=new InputStreamReader(input);
            BufferedReader br = new BufferedReader(reader);
            String line = null;
            while ((line = br.readLine()) != null) {
                String[] question = line.split("/t");
                if (question_no.contains(Integer.parseInt(question[0]))) {
                    Question q = new Question();
                    q.ID = Integer.parseInt(question[0]);
                    q.question = question[1];
                    q.answerA = question[2];
                    q.answerB = question[3];
                    if (question.length == 7) {
                        q.answerC = question[4];
                        q.answerD = question[5];
                        q.explaination = question[6];
                    } else if (question.length == 6) {
                        q.answerC = question[4];
                        q.answerD = null;
                        q.explaination = question[5];
                    } else {
                        q.answerC = null;
                        q.answerD = null;
                        q.explaination = question[4];
                    }
                    q.selectedAnswer = -1;
                    if (q.explaination.equals("A")) {
                        q.answer = 1;

                    } else if (q.explaination.equals("B")) {
                        q.answer = 2;

                    } else if (q.explaination.equals("C")) {
                        q.answer = 3;

                    } else {
                        q.answer = 4;
                    }
                    question_list.add(q);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return question_list;
    }
}
