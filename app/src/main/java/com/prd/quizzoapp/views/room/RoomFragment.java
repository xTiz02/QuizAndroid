package com.prd.quizzoapp.views.room;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.prd.quizzoapp.views.quiz.QuizActivity;
import com.prd.quizzoapp.R;
import com.prd.quizzoapp.databinding.FragmentRoomBinding;
import com.prd.quizzoapp.util.Data;

import org.jetbrains.annotations.Nullable;


public class RoomFragment extends Fragment {

    private UserAdapter userAdapter;
    private FragmentRoomBinding binding;

    public RoomFragment() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_room, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentRoomBinding.bind(view);
        userAdapter = new UserAdapter(Data.getUsers());
        binding.rvUsers.setHasFixedSize(true);
        binding.rvUsers.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvUsers.setAdapter(userAdapter);

        //Copiar en portapapeles el código de la sala
        binding.btnCopy.setOnClickListener(v -> {
            String roomCode = binding.tvCode.getText().toString();
            ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Room Code", roomCode);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), "Código de la sala copiado", Toast.LENGTH_SHORT).show();
        });
        binding.btnStartGame.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), QuizActivity.class);
            //enviar una lista de preguntas al QuizActivity
            //Cada jugador estara esperando las preguntas del webSocket
             intent.putExtra("questions", Data.getQuestions());
            startActivity(intent);
        });

        binding.roomConfig.setOnClickListener(v -> {
            //Cargar CreateRoomFragment en el contenedor del main activity
            NavController navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);
            navController.navigate(R.id.action_roomFragment_to_createRoomFragment);
        });

    }
}