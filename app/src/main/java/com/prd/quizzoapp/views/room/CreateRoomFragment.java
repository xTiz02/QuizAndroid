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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.chip.Chip;
import com.prd.quizzoapp.R;
import com.prd.quizzoapp.databinding.FragmentCreateRoomBinding;
import com.prd.quizzoapp.model.entity.Category;
import com.prd.quizzoapp.model.entity.Room;
import com.prd.quizzoapp.model.entity.SubCategory;
import com.prd.quizzoapp.model.service.ActionCallback;
import com.prd.quizzoapp.model.service.DataActionCallback;
import com.prd.quizzoapp.model.service.LoadingService;
import com.prd.quizzoapp.model.service.RoomService;
import com.prd.quizzoapp.util.CategoryEnum;
import com.prd.quizzoapp.util.Data;
import com.prd.quizzoapp.util.DataSharedPreference;
import com.prd.quizzoapp.util.Util;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


public class CreateRoomFragment extends Fragment {

    private CreateRoomFragmentArgs args;
    private FragmentCreateRoomBinding binding;
    private ArrayList<SubCategory> selectedSubCategories = new ArrayList<>();
    private ArrayList<Category> selectedCategories = new ArrayList<>();
    private RoomService roomService;
    private String code;
    private String idRoom;
    private LoadingService ls;
    private Room currentRoom;


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
        ls = new LoadingService(getContext());
        roomService = new RoomService(getContext());
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
            code = Util.generateRoomCode();
            binding.tvCode.setText(code);
            selectedCategories = (ArrayList<Category>) args.getCategoriesList();
            selectedCategories
                    .forEach(category -> {

                        initChipStartCategories(category, Data.getSubCategories());
                    });

        }else {
            ls.showLoading("Cargando configuración...");
            idRoom = DataSharedPreference.getData(Util.ROOM_UUID_KEY, requireContext());
            binding.btnCreateRoom.setText("Actualizar sala");
            roomService.getRoomByUuid(idRoom, new DataActionCallback<Room>() {
                @Override
                public void onSuccess(Room data) {
                    currentRoom = data;
                    code = data.getRoomConfig().getCode();
                    binding.tvCode.setText(code);
                    selectedCategories = data.getCategories();
                    if(data.getSubCategories() != null){
                        selectedSubCategories = data.getSubCategories().stream()
                                .map(Data::getSubCategoryByName)
                                .collect(Collectors.toCollection(ArrayList::new));
                    }else {
                        selectedSubCategories = new ArrayList<>();
                    }
                    selectedCategories.forEach(
                            category -> initChipCategory(category)
                    );
                    initChipSubCategories(selectedSubCategories);

                    for (int i = 0; i < binding.chipGroupQuestions.getChildCount(); i++) {
                        Chip chip = (Chip) binding.chipGroupQuestions.getChildAt(i);
                        if (chip.getText().toString().equals(String.valueOf(data.getRoomConfig().getQuestions()))) {
                            chip.setChecked(true);
                            break;
                        }
                    }

                    for (int i = 0; i < binding.chipGroupTime.getChildCount(); i++) {
                        Chip chip = (Chip) binding.chipGroupTime.getChildAt(i);
                        if (chip.getText().toString().equals(String.valueOf(data.getRoomConfig().getTimeOfQuestion()))){
                            chip.setChecked(true);
                            break;
                        }
                    }
                    ls.hideLoading();
                }
                @Override
                public void onFailure(Exception e) {
                    Util.showToastLog("Error al obtener sala", getContext());
                    ls.hideLoading();
                }
            });
        }

        binding.btnAddSubCategory.setOnClickListener(v -> {
            String subCategory = binding.edtSubCategory.getText().toString().trim();
            if(existSubCategory(subCategory)){
                return;
            }
            addSubCategory(subCategory);
            selectedSubCategories.add(new SubCategory(subCategory, CategoryEnum.RANDOM));
            binding.edtSubCategory.setText("");
        });

        binding.btnNewCode.setOnClickListener(v -> {
            code = Util.generateRoomCode();
            binding.tvCode.setText(code);
        });

        binding.btnCreateRoom.setOnClickListener(v -> {
            if(args.getCategoriesList()!=null && idRoom == null){
                createRoom();
            }else {
                updateRoom();
            }
        });
    }
    private void initChipCategory(Category category) {
        final Chip chip = new Chip(getContext());
        chip.setText(category.getCategory().getName());
        chip.setCheckable(true);
        chip.setCloseIconVisible(false);
        chip.setEnabled(false);
        binding.chipGroupCategories.addView(chip);
    }
    private void initChipSubCategories(ArrayList<SubCategory> subCategories) {
        subCategories.forEach(subCategory -> {
            addSubCategory(subCategory.getName());
        });
    }

    private void initChipStartCategories(Category category, ArrayList<SubCategory> subCategories) {
        initChipCategory(category);
        ArrayList<SubCategory> subCategoriesOfCategory = subCategories.stream()
                    .filter(sub -> Objects.equals(sub.getCategory(), category.getCategory()))
                    .collect(Collectors.toCollection(ArrayList::new));

        if(!subCategoriesOfCategory.isEmpty()){
            initChipSubCategories(subCategoriesOfCategory);
        }
        selectedSubCategories.addAll(subCategoriesOfCategory);
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
            binding.chipGroupSubCategories.removeView(chip);
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
            binding.chipGroupQuestions.requestFocus();
            Toast.makeText(getContext(), "Selecciona una cantidad preguntas", Toast.LENGTH_SHORT).show();
            return;
        }
        if(binding.chipGroupTime.getCheckedChipIds().isEmpty()){
            binding.chipGroupTime.requestFocus();
            Toast.makeText(getContext(), "Selecciona un tiempo", Toast.LENGTH_SHORT).show();
            return;
        }
        ls.showLoading("Creando sala...");
        Chip selectedChip = binding.chipGroupQuestions.findViewById(binding.chipGroupQuestions.getCheckedChipId());
        Chip selectedChipTime = binding.chipGroupTime.findViewById(binding.chipGroupTime.getCheckedChipId());
        roomService.createRoom(
                code,
                Integer.parseInt(selectedChip.getText().toString()),
                Integer.parseInt(selectedChipTime.getText().toString()),
                selectedCategories,
                selectedSubCategories.stream().map(SubCategory::getName).collect(Collectors.toCollection(ArrayList::new)),
                new ActionCallback() {
                    @Override
                    public void onSuccess() {
                        NavController navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);
                        navController.navigate(R.id.roomFragment);
                        ls.hideLoading();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        ls.hideLoading();
                        Util.showToastLog("Error al crear sala", getContext());
                    }

                });
    }


    public void updateRoom(){
        if(binding.chipGroupQuestions.getCheckedChipIds().isEmpty()){
            binding.chipGroupQuestions.requestFocus();
            Toast.makeText(getContext(), "Selecciona una cantidad preguntas", Toast.LENGTH_SHORT).show();
            return;
        }
        if(binding.chipGroupTime.getCheckedChipIds().isEmpty()){
            binding.chipGroupTime.requestFocus();
            Toast.makeText(getContext(), "Selecciona un tiempo", Toast.LENGTH_SHORT).show();
            return;
        }
        ls.showLoading("Actualizando sala...");
        Map<String, Object> newRoom = new HashMap<>();
        Set<String> subCategoriesSet = selectedSubCategories.stream()
                .map(SubCategory::getName).collect(Collectors.toSet());
        Set<String> currentSubCategoriesSet = new HashSet<>(currentRoom.getSubCategories());
        //ver si las categorias seleccionadas son las mismas que las actuales

        if(!subCategoriesSet.equals(currentSubCategoriesSet)){
            newRoom.put("subCategories", subCategoriesSet);
        }

        Chip selectedChip = binding.chipGroupQuestions.findViewById(binding.chipGroupQuestions.getCheckedChipId());
        Chip selectedChipTime = binding.chipGroupTime.findViewById(binding.chipGroupTime.getCheckedChipId());
        if(currentRoom.getRoomConfig().getQuestions() != Integer.parseInt(selectedChip.getText().toString())){
            newRoom.put("questions", Integer.parseInt(selectedChip.getText().toString()));
        }
        if(currentRoom.getRoomConfig().getTimeOfQuestion() != Integer.parseInt(selectedChipTime.getText().toString())){
            newRoom.put("timeOfQuestion", Integer.parseInt(selectedChipTime.getText().toString()));

        }

        if(!code.equals(currentRoom.getRoomConfig().getCode())){
            newRoom.put("code", code);
        }
        if(newRoom.isEmpty()){
            Util.showToastLog("No hay cambios", getContext());
            ls.hideLoading();
            return;
        }
        System.out.println(newRoom);
        roomService.updateRoom(
                idRoom,
                newRoom,
                new ActionCallback() {
                    @Override
                    public void onSuccess() {
                        NavController navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);
                        navController.navigate(R.id.roomFragment);
                        ls.hideLoading();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        //volver a cargar la pantalla
                        NavController navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);
                        navController.navigate(R.id.createRoomFragment);
                        ls.hideLoading();
                        Util.showToastLog("Error al actualizar sala", getContext());
                    }
                });
    }
}