package com.prd.quizzoapp.views.quiz;

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
import com.prd.quizzoapp.model.entity.Score;

import java.util.ArrayList;

public class QuizActivity extends AppCompatActivity {

    private ActivityQuizBinding binding;
    private ArrayList<Question> questions;
    private CountDownTimer timer;
    private boolean allowPlaying = true;
    private double timeLeft = 0;
    private int position = 0;
    private Question currentQuestion;
    private ArrayList<Score> scoresList = new ArrayList<>();
    private double score = 0.0;
    private boolean marketCorrect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        questions = (ArrayList<Question>) getIntent().getSerializableExtra("questions");
        binding.pbProgress.setMax(questions.size());
        binding.tvProgress.setText("1/" + questions.size());
        currentQuestion = questions.get(position);
        startTimer();
        setQuestion();
        setOptions();
        binding.btnNext.setOnClickListener(v -> onNext());
        binding.btnNext.setEnabled(false);
        binding.btnNext.setVisibility(View.GONE);



    }

    private void onNext() {
        binding.btnNext.setEnabled(false);
        binding.btnNext.setVisibility(View.GONE);
        Score scoreModel = new Score(
                currentQuestion.getUuid(),
                score,
                20 - timeLeft,
                marketCorrect
        );
        System.out.println("Score: " + scoreModel);
        scoresList.add(scoreModel);
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

    private void clearLinearLayout() {
        binding.linearLayout.removeAllViews();
    }

    private void endGame() {
        Toast.makeText(this, "Fin del juego", Toast.LENGTH_SHORT).show();
        System.out.println("Scores: " + scoresList);
        Intent intent = new Intent(this, LeaderBoardActivity.class);
        startActivity(intent);
        finish();
    }

    private void setOptions() {
        currentQuestion.getOptions().forEach(this::addOptionButton);
    }

    private void setQuestion() {
        binding.tvQuestion.setText(currentQuestion.getQuestion());
    }

    private void startTimer() {
        binding.circularProgressBar.setMax(20);
        binding.circularProgressBar.setProgress(20);

        timer = new CountDownTimer(20*1000, 1000) {//el primer valor es el tiempo total y el segundo es el intervalo de tiempo en milisegundos
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
                binding.btnNext.setEnabled(true);
                binding.btnNext.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    private void addOptionButton(QuestionOption option) {
        AppCompatButton button = new AppCompatButton(this);
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
        button.setTextSize(20);
        button.setAllCaps(false);
        button.setTypeface(null, Typeface.BOLD);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allowPlaying) {
                    timer.cancel();
                    v.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.button_1));
                    showCorrectAnswer(v);
                    binding.btnNext.setVisibility(View.VISIBLE);
                    binding.btnNext.setEnabled(true);
                    allowPlaying = false;
                }
            }
        });
        binding.linearLayout.addView(button);
    }

    private void setScore(boolean correct) {
        double score1 = correct ? 1.0 : 0.0;
        double score2 = timeLeft / 20;
        score = score1 + score2;
        //enviar resultado a la base de datos
    }

    private void showCorrectAnswer(View btnPressed) {
        boolean correct = (boolean) btnPressed.getTag();
        if (correct) {
            marketCorrect = true;
            Toast.makeText(this, "Respuesta correcta", Toast.LENGTH_SHORT).show();
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


/*
* // Referencia al nodo de usuarios en una sala específica
DatabaseReference userRef = database.getReference("rooms").child("idSala").child("users").child("user1");

// Actualizar el nombre y el estado de listo del usuario
Map<String, Object> userUpdates =  new HashMap<>();
userUpdates.put("name", "John");
userUpdates.put("readyToPlay", true);

userRef.updateChildren(userUpdates)
        .addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Firebase", "Usuario actualizado.");
            } else {
                Log.d("Firebase", "Error al actualizar el usuario.", task.getException());
            }
        });*/


/*
* // Obtener la referencia de la base de datos
FirebaseDatabase database = FirebaseDatabase.getInstance();
DatabaseReference usersRef = database.getReference("rooms").child("idSala").child("users");

// Agregar un ChildEventListener para escuchar cambios específicos en los usuarios
usersRef.addChildEventListener(new ChildEventListener() {
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
        // Se llama cuando se añade un nuevo usuario
        User newUser = dataSnapshot.getValue(User.class);
        Log.d("Firebase", "Nuevo usuario añadido: " + newUser.getName());
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
        // Se llama cuando cambia un atributo de un usuario
        User updatedUser = dataSnapshot.getValue(User.class);
        Log.d("Firebase", "Usuario actualizado: " + updatedUser.getName() + " está listo: " + updatedUser.isReadyToPlay());
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        // Se llama cuando un usuario es eliminado
        User removedUser = dataSnapshot.getValue(User.class);
        Log.d("Firebase", "Usuario eliminado: " + removedUser.getName());
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
        // Si se reorganizan los nodos (no común en este caso)
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        // Error de lectura
        Log.e("Firebase", "Error: " + databaseError.getMessage());
    }
});*/

/*
* // Obtener la referencia de la base de datos
FirebaseDatabase database = FirebaseDatabase.getInstance();
DatabaseReference roomsRef = database.getReference("rooms");

// Crear un nuevo nodo con un idSala único
String idSala = roomsRef.push().getKey();  // Genera un ID único para la nueva sala
if (idSala != null) {
    // Crear un mapa con los valores de la sala
    Map<String, Object> newRoom = new HashMap<>();
    newRoom.put("code", "ABC123");
    newRoom.put("questionNumber", 1);
    newRoom.put("timer", 30);

    // Insertar la nueva sala en la base de datos
    roomsRef.child(idSala).setValue(newRoom)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // La inserción fue exitosa
                    Log.d("Insert", "Sala creada con éxito.");
                } else {
                    // Hubo un error
                    Log.d("Insert", "Error al crear la sala.", task.getException());
                }
            });
}*/

/*
* @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        // Inicializar la referencia de la sala
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        roomRef = database.getReference("rooms").child(idSala);

        // Configurar el listener para escuchar cambios en el nodo de la sala
        listenForRoomChanges();
    }

    private void listenForRoomChanges() {
        roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Aquí puedes obtener los valores de la sala cuando cambian
                if (dataSnapshot.exists()) {
                    String code = dataSnapshot.child("code").getValue(String.class);
                    Long questionNumber = dataSnapshot.child("questionNumber").getValue(Long.class);
                    Long timer = dataSnapshot.child("timer").getValue(Long.class);

                    // Actualizar la interfaz de usuario o realizar alguna acción
                    updateUI(code, questionNumber, timer);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar errores
                Log.w("RoomActivity", "loadRoom:onCancelled", databaseError.toException());
            }
        });
    }*/