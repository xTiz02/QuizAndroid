package com.prd.quizzoapp.views.quiz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.prd.quizzoapp.R;
import com.prd.quizzoapp.databinding.ActivityQuizBinding;
import com.prd.quizzoapp.model.entity.Question;
import com.prd.quizzoapp.model.entity.QuestionOption;
import com.prd.quizzoapp.model.entity.Room;
import com.prd.quizzoapp.model.entity.RoomConfig;
import com.prd.quizzoapp.model.entity.Score;
import com.prd.quizzoapp.model.service.ResultService;
import com.prd.quizzoapp.model.service.RoomService;
import com.prd.quizzoapp.model.service.intf.ActionCallback;
import com.prd.quizzoapp.model.service.intf.DataActionCallback;
import com.prd.quizzoapp.util.DataSharedPreference;
import com.prd.quizzoapp.util.Util;

import java.util.ArrayList;

public class QuizActivity extends AppCompatActivity {

    private ActivityQuizBinding binding;
    private ArrayList<Question> questions;
    private CountDownTimer timer;
    private ResultService resultService;
    private boolean allowPlaying = true;
    private double timeLeft = 0;
    private int position = 0;
    private Question currentQuestion;
    private final ArrayList<Score> scoresList = new ArrayList<>();
    private double score = 0.0;
    private boolean marketCorrect = false;
    private RoomService rs;
    private RoomConfig rc;

    @Override
    protected void onStart() {
        super.onStart();
        rs = new RoomService(this);
        rs.getRoomByUuid(DataSharedPreference.getData(Util.ROOM_UUID_KEY, this), new DataActionCallback<Room>() {
            @Override
            public void onSuccess(Room data) {
                rc = data.getRoomConfig();
                binding.tvTimer.setText(String.valueOf(rc.getTimeOfQuestion()));
            }

            @Override
            public void onFailure(Exception e) {
                Util.showLog("QuizActivity", "Error al obtener la sala para el quiz");
            }
        });
    }
    @SuppressLint("MissingSuperCall")//no permitir regresar
    @Override
    public void onBackPressed() {
        //no permitir regresar
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        resultService = new ResultService(this);

        questions = (ArrayList<Question>) getIntent().getSerializableExtra("questions");
        binding.pbProgress.setMax(questions.size());
        binding.tvProgress.setText("1/" + questions.size());
        currentQuestion = questions.get(position);
        setQuestion();
        //esperar 2 segundos
        new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }
            @Override
            public void onFinish() {
                setOptions();
                startTimer();
            }
        }.start();
        /*binding.btnNext.setOnClickListener(v -> onNext());
        binding.btnNext.setEnabled(false);
        binding.btnNext.setVisibility(View.GONE);*/



    }

    private void onNext() {
       /* binding.btnNext.setEnabled(false);
        binding.btnNext.setVisibility(View.GONE);*/
        Score scoreModel = new Score(
                currentQuestion.getUuid(),
                score,
                rc.getTimeOfQuestion() - timeLeft,
                marketCorrect
        );
        System.out.println("Score: " + scoreModel);
        scoresList.add(scoreModel);
        resultService.updateScore(
                DataSharedPreference.getData(Util.ROOM_UUID_KEY, this),
                scoresList,
                new ActionCallback() {
                    @Override
                    public void onSuccess() {
                        score = 0.0;
                        if (position < questions.size() - 1) {
                            timer.cancel();
                            position++;
                            clearLinearLayout();
                            currentQuestion = questions.get(position);
                            setQuestion();
                            setOptions();
                            binding.pbProgress.setProgress(position + 1);
                            binding.tvProgress.setText((position + 1) + "/" + questions.size());
                            allowPlaying = true;
                            startTimer();
                        } else {
                            endGame();
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(QuizActivity.this, "Error al guardar el score", Toast.LENGTH_LONG).show();
                        /*if(questions.size() == scoresList.size()){
                            Intent intent = new Intent(QuizActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }*/

                    }
                }
        );

    }

    private void clearLinearLayout() {
        binding.linearLayout.removeAllViews();
    }

    private void endGame() {
        Toast.makeText(this, "Fin del juego", Toast.LENGTH_SHORT).show();
        Util.showLog("QuizActivity", "Fin del juego");
        System.out.println("Scores: " + scoresList);
        Intent intent = new Intent(this, LeaderBoardActivity.class);
        startActivity(intent);
        finish();
    }

    private void setOptions() {
        currentQuestion.getOptions().forEach(this::addOptionButton);
    }

    private void setQuestion() {
        if(currentQuestion.getQuestion().length()>40){
            binding.tvQuestion.setTextSize(20);
        }
        binding.tvQuestion.setText(currentQuestion.getQuestion());
    }

    private void startTimer() {
        binding.circularProgressBar.setMax(20);
        binding.circularProgressBar.setProgress(20);

        timer = new CountDownTimer(rc.getTimeOfQuestion()* 1000L, 1000) {//el primer valor es el tiempo total y el segundo es el intervalo de tiempo en milisegundos
            @Override
            public void onTick(long millisUntilFinished) {
                binding.circularProgressBar.incrementProgressBy(-1);
                binding.tvTimer.setText(String.valueOf(millisUntilFinished / 1000));
                timeLeft = (double) (millisUntilFinished / 1000);
            }
            @Override
            public void onFinish() {
                allowPlaying = false;
                for (int i = 0; i < binding.linearLayout.getChildCount(); i++) {
                    AppCompatButton button = (AppCompatButton) binding.linearLayout.getChildAt(i);
                    if ((boolean) button.getTag()) {
                        button.setBackground(ContextCompat.getDrawable(button.getContext(), R.drawable.green_cricle));
                        break;
                    }
                    button.setEnabled(false);
                }
                Toast.makeText(QuizActivity.this, "Se acabó el tiempo", Toast.LENGTH_SHORT).show();
                marketCorrect = false;
                setScore(false);
                new CountDownTimer(4000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }
                    @Override
                    public void onFinish() {
                        onNext();
                    }
                }.start();
            }
        }.start();
    }

    private void addOptionButton(QuestionOption option) {
        AppCompatButton button = new AppCompatButton(this);
        if(option.getOption().length()>25){
            button.setTextSize(15);
        }else {
            button.setTextSize(20);
        }
        button.setText(option.getOption());
        button.setTag(option.isCorrect());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 15, 0, 0);
        button.setLayoutParams(layoutParams);
        Drawable background = ContextCompat.getDrawable(this, R.drawable.gray_button_bg);
        button.setBackground(background);
        button.setAllCaps(false);
        button.setTypeface(null, Typeface.BOLD);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allowPlaying) {
                    timer.cancel();
                    v.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.button_1));
                    showCorrectAnswer(v);
                    /*binding.btnNext.setVisibility(View.VISIBLE);
                    binding.btnNext.setEnabled(true);*/
                    allowPlaying = false;
                    //esperar 2 segundos
                    onNext();
                }
            }
        });
        binding.linearLayout.addView(button);
    }

    private void setScore(boolean correct) {
        double score1 = correct ? 1.0 : 0.0;
        double score2 = Math.round((timeLeft / rc.getTimeOfQuestion())*100) / 100.0;
        score = score1 + score2;
        //convertir a string
        String scoreStr = String.valueOf(score);
        //crear un numero redondeado
        String[] parts = scoreStr.split("\\.");
        if (parts.length > 1) {
            if (parts[1].length() > 2) {
                scoreStr = parts[0] + "." + parts[1].substring(0, 2);
            }
        }else{
            scoreStr = parts[0] + ".00";
        }
        double scoreFinal = Double.parseDouble(scoreStr);
        score = scoreFinal;
        System.out.println("Score: " + score);


        //enviar resultado a la base de datos
    }

    private void showCorrectAnswer(View btnPressed) {
        boolean correct = (boolean) btnPressed.getTag();
        if (correct) {
            marketCorrect = true;
        } else {
            marketCorrect = false;
            for (int i = 0; i < binding.linearLayout.getChildCount(); i++) {
                AppCompatButton button = (AppCompatButton) binding.linearLayout.getChildAt(i);
                if ((boolean) button.getTag()) {
                    button.setBackground(ContextCompat.getDrawable(this, R.drawable.green_cricle));
                    break;
                }
                button.setEnabled(false);
            }
        }
        setScore(correct);
    }
}


