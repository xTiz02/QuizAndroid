package com.prd.quizzoapp.views.room;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;
import com.prd.quizzoapp.R;
import com.prd.quizzoapp.databinding.FragmentCreateRoomBinding;
import com.prd.quizzoapp.model.entity.Category;
import com.prd.quizzoapp.model.entity.SubCategory;
import com.prd.quizzoapp.util.CategoryEnum;
import com.prd.quizzoapp.util.Data;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;


public class CreateRoomFragment extends Fragment {

    private CreateRoomFragmentArgs args;
    private FragmentCreateRoomBinding binding;
    private ArrayList<SubCategory> selectedSubCategories = new ArrayList<>();


    public CreateRoomFragment() {
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
        return inflater.inflate(R.layout.fragment_create_room, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentCreateRoomBinding.bind(view);
        //si hay argumentos
        args = CreateRoomFragmentArgs.fromBundle(requireArguments());

        binding.btnCopy.setOnClickListener(v -> {
            String roomCode = binding.tvCode.getText().toString();
            ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Room Code", roomCode);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), "Código de la sala copiado", Toast.LENGTH_SHORT).show();
        });
        if(args.getCategoriesList()!=null){
            ((ArrayList<Category>) args.getCategoriesList())
                    .forEach(category -> initChipCategory(category, Data.getSubCategories()));

            binding.btnAddSubCategory.setOnClickListener(v -> {
                String subCategory = binding.edtSubCategory.getText().toString().trim();
                if(existSubCategory(subCategory)){
                    return;
                }
                addSubCategory(subCategory);
                selectedSubCategories.add(new SubCategory(subCategory, CategoryEnum.RANDOM));
                binding.edtSubCategory.setText("");
            });

        }else {
            String idRoom = args.getIdRoom();
            Toast.makeText(getContext(), "Cargando configuración: "+ idRoom, Toast.LENGTH_SHORT).show();
            //traer de la bd firestore
            binding.btnCreateRoom.setText("Actualizar sala");
        }

        binding.btnCreateRoom.setOnClickListener(v -> {
            createRoom();
        });
    }

    private void initChipCategory(Category category, ArrayList<SubCategory> subCategories) {
        final Chip chip = new Chip(getContext());
        chip.setText(category.getCategory().getName());
        chip.setCheckable(true);
        chip.setCloseIconVisible(false);
        chip.setEnabled(false);
        binding.chipGroupCategories.addView(chip);

        selectedSubCategories = subCategories.stream()
                .filter(sub-> Objects.equals(sub.getCategory(),category.getCategory()))
                .collect(Collectors.toCollection(ArrayList::new));

        if(!selectedSubCategories.isEmpty()){
            selectedSubCategories.forEach(subCategory -> {
                addSubCategory(subCategory.getName());
            });
        }
    }

    public void addSubCategory(String name){
        if(name.isEmpty()){
            return;
        }
        final Chip chip = new Chip(getContext());
        chip.setText(name);
        chip.setCheckable(false);
        chip.setCloseIconVisible(true);
        chip.setEnabled(true);

        // Listener para la "X" del chip
        chip.setOnCloseIconClickListener((v) -> {
            // Eliminar el chip del ChipGroup
            binding.chipGroupSubCategories.removeView(chip);

            // Opcional: Eliminar la subcategoría de la lista de datos si es necesario
            selectedSubCategories.remove(selectedSubCategories.stream()
                    .filter(subCategory -> subCategory.getName().equals(name))
                    .findFirst().get());
        });

        binding.chipGroupSubCategories.addView(chip);
    }

    public boolean existSubCategory(String name){
        return selectedSubCategories.stream().anyMatch(subCategory -> subCategory.getName().equalsIgnoreCase(name));
    }

    public void createRoom() {

        if(binding.chipGroupQuestions.getCheckedChipIds().isEmpty()){
            Toast.makeText(getContext(), "Selecciona una pregunta", Toast.LENGTH_SHORT).show();
            return;
        }
        if(binding.chipGroupTime.getCheckedChipIds().isEmpty()){
            Toast.makeText(getContext(), "Selecciona un tiempo", Toast.LENGTH_SHORT).show();
            return;
        }
        /*int selectedChipId = binding.chipGroupQuestions.getCheckedChipId();
        Chip selectedChip = binding.chipGroupQuestions.findViewById(selectedChipId);
        int tiempo = binding.chipGroupTime.getCheckedChipId();
        Chip selectedChipTime = binding.chipGroupTime.findViewById(tiempo);*/

    }
}