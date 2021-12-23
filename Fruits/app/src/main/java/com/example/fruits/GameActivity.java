package com.example.fruits;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.FragmentManager;

public class GameActivity extends AppCompatActivity {

    AnswerTextFragment answerTextFragment = new AnswerTextFragment();
    AnswerCameraFragment answerCameraFragment = new AnswerCameraFragment();

    String correctAnswer;
    String answer = "jeruk";
    String namaFile;
    public static ArrayList<Question> listQuestion = new ArrayList<>();
    Random rand = new Random();
    int menu = 1;

    ImageView ivImageSoal;
    TextView tvHasilJawab;
    Button btnJawab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        ivImageSoal = findViewById(R.id.ivImageSoal);
        btnJawab = findViewById(R.id.btnJawab);
        ImageView menuJawabText = findViewById(R.id.menuJawabText);
        ImageView menuJawabImage = findViewById(R.id.menuJawabImage);
        ImageView btnNextQuestion = findViewById(R.id.btnNextQuestion);
        ImageView btnBeforeQuestion = findViewById(R.id.btnBeforeQuestion);
        tvHasilJawab = findViewById(R.id.tvHasilJawab);

        btnNextQuestion.setOnClickListener(btnOperasi);
        btnBeforeQuestion.setOnClickListener(btnOperasi);
        menuJawabText.setOnClickListener(btnOperasi);
        menuJawabImage.setOnClickListener(btnOperasi);
        btnJawab.setOnClickListener(btnOperasi);

        makeQuestion();
        setCurrentQuestion();

        getSupportFragmentManager().beginTransaction().replace(R.id.answerFragment, answerTextFragment).commit();
    }

    private void makeQuestion() {
        Drawable temp ;
        Question newQuestion;

        temp = AppCompatResources.getDrawable(this, R.mipmap.ic_apple);
        newQuestion = new Question("apel", temp);
        listQuestion.add(newQuestion);

        temp = AppCompatResources.getDrawable(this, R.mipmap.ic_banana);
        newQuestion = new Question("pisang", temp);
        listQuestion.add(newQuestion);

        temp = AppCompatResources.getDrawable(this, R.mipmap.ic_orange);
        newQuestion = new Question("jeruk", temp);
        listQuestion.add(newQuestion);
    }

    public void setAnswer(String answer){
        this.answer=answer;
    }

    View.OnClickListener btnOperasi = new View.OnClickListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.menuJawabText:
                    menu = 1;
                    getSupportFragmentManager().beginTransaction().replace(R.id.answerFragment, answerTextFragment).commit();
                    break;
                case R.id.menuJawabImage:
                    menu = 2;
                    getSupportFragmentManager().beginTransaction().replace(R.id.answerFragment, answerCameraFragment).commit();
                    break;
                case R.id.btnNextQuestion:
                case R.id.btnBeforeQuestion:
                    tvHasilJawab.setText("HASIL");
                    tvHasilJawab.setTextColor(getResources().getColor(R.color.fruit1));
                    correctAnswer = null;
                    setCurrentQuestion();
                    break;
                case R.id.btnJawab:
                    if(menu == 1){
                        FragmentManager fm = getSupportFragmentManager();
                        AnswerTextFragment fragment = (AnswerTextFragment) fm.findFragmentById(R.id.answerFragment);
                        answer = fragment.getAnswer();
                    }
//                    Toast.makeText(getBaseContext(),answer,Toast.LENGTH_SHORT).show();
                    cekJawaban();
                    break;
            }
        }
    };

    private void setCurrentQuestion() {
        int temp = rand.nextInt(3);
        Question currentQuestion = listQuestion.get(temp);
        ivImageSoal.setImageDrawable(currentQuestion.getImage());
        correctAnswer = currentQuestion.getName();
    }

    private void cekJawaban() {
        String currentAnswer = answer.toLowerCase();
        boolean temp = false;
        switch (correctAnswer) {
            case "apel":
                temp = currentAnswer.equals("apel") || currentAnswer.equals("apple");
                break;
            case "pisang":
                temp = currentAnswer.equals("pisang") || currentAnswer.equals("banana");
                break;
            case "jeruk":
                temp = currentAnswer.equals("jeruk") || currentAnswer.equals("orange");
                break;
        }
        if (temp==true){
            tvHasilJawab.setText("BENAR");
            tvHasilJawab.setTextColor(getResources().getColor(R.color.benar));
        }
        else{
            tvHasilJawab.setText("SALAH");
            tvHasilJawab.setTextColor(getResources().getColor(R.color.salah));
        }
    }

}