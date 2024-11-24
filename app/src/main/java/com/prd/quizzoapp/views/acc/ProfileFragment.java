package com.prd.quizzoapp.views.acc;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.prd.quizzoapp.databinding.FragmentProfileBinding;
import com.prd.quizzoapp.model.entity.User;
import com.prd.quizzoapp.model.service.LoadingService;
import com.prd.quizzoapp.util.DataSharedPreference;
import com.prd.quizzoapp.util.Util;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;


public class ProfileFragment extends Fragment {

    private DatabaseReference dbRef,dbRoomRef;
    private FragmentProfileBinding binding;
    private FirebaseStorage storage;
    private FirebaseAuth auth;
    private User user;
    private Uri setImageUri;
    private ValueEventListener eventListener;
    private LoadingService loadingService;
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onDestroy() {//4
        super.onDestroy();
        Util.showLog("ProfileFragment", "onDestroy");
        removeListener();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {//1
        super.onCreate(savedInstanceState);
        initServices();
        Util.showLog("ProfileFragment", "onCreate");
    }

    @Override
    public void onStart() {//3
        super.onStart();
        //initListener();
        Util.showLog("ProfileFragment", "onStart");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {//2
        super.onViewCreated(view, savedInstanceState);
        Util.showLog("ProfileFragment", "onViewCreated");

        binding.userProfilePic.setOnClickListener(v->{
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Selecionar Imagen"), 10);
        });


        binding.btnsave.setOnClickListener(v->{
            if(validateFields()){
                String username = binding.edtUsername.getText().toString().trim();
                String desc = binding.edtDescp.getText().toString().trim();
                Map<String,Object> map = new HashMap<>();
                if(!user.getUsername().equals(username)) {
                    map.put("username", username);
                }
                if(!user.getDescription().equals(desc)) {
                    map.put("description", desc);
                }
                if(map.isEmpty() && setImageUri==null){
                    Toast.makeText(getContext(), "No hay cambios", Toast.LENGTH_SHORT).show();
                    return;
                }
                loadingService.showLoading("Guardando...");
                if(setImageUri!=null){
                    StorageReference storageReference = storage.getReference().child("upload").child(auth.getUid().toString());
                    storageReference.putFile(setImageUri).addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                String finalImageUri = uri.toString();
                                map.put("img", finalImageUri);
                                dbRef.updateChildren(map).addOnCompleteListener(task1 -> {
                                    if(task1.isSuccessful()){
                                        Toast.makeText(getContext(), "Usuario actualizado", Toast.LENGTH_SHORT).show();
                                    }else {
                                        loadingService.hideLoading();
                                        Util.showLog("ProfileFragment", "Error al guardar usuario");
                                        Toast.makeText(getContext(), "Error al guardar usuario", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            });
                        }else {
                            loadingService.hideLoading();
                            Util.showLog("ProfileFragment", "Error al subir imagen");
                            Toast.makeText(getContext(), "Error al subir imagen", Toast.LENGTH_SHORT).show();
                        }
                    });

                }else {
                    dbRef.updateChildren(map).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Usuario actualizado", Toast.LENGTH_SHORT).show();
                        } else {
                            loadingService.hideLoading();
                            Util.showLog("ProfileFragment", "Error al guardar usuario");
                            Toast.makeText(getContext(), "Error al guardar usuario", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                if(DataSharedPreference.getData(Util.ROOM_UUID_KEY,getContext())!=null){
                    dbRoomRef = FirebaseDatabase.getInstance().getReference("rooms");
                    dbRoomRef.child(DataSharedPreference.getData(Util.ROOM_UUID_KEY,getContext()))
                            .child("usersRoom").child(auth.getUid())
                            .updateChildren(map).addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            loadingService.hideLoading();
                            Util.showLog("ProfileFragment", "Usuario actualizado en la sala");
                        }else {
                            loadingService.hideLoading();
                            Util.showLog("ProfileFragment", "Error al actualizar usuario en la sala");
                        }
                    });
                }else {
                    loadingService.hideLoading();
                }
            }

        });
    }

    private boolean validateFields() {
        String username = binding.edtUsername.getText().toString().trim();
        String desc = binding.edtDescp.getText().toString().trim();
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(desc)){
            Toast.makeText(getContext(), "Por favor complete los campos", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(username.length()<4){
            binding.edtUsername.setError("La contraseña debe tener al menos 6 caracteres");
            return false;
        }
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==10){
            if (data!=null){
                setImageUri = data.getData();
                binding.userProfilePic.setImageURI(setImageUri);
            }
        }
    }

    @Override
    public void onDestroyView() {//3s cuando se cambia de fragmento
        super.onDestroyView();
        removeListener();
        Util.showLog("ProfileFragment", "onDestroyView");
    }



    @Override
    public void onResume() {//se llama cuando el fragmento se vuelve visible para el usuario 4
        super.onResume();
        /*if (isAdded() && binding != null) {//isAdded() Devuelve true si el fragmento está actualmente asociado a su actividad y visible al usuario.
            initListener(); // Vuelve a agregar el listener cuando el fragmento vuelve a ser visible
        }*/
        initListener();
        Util.showLog("ProfileFragment", "onResume");
    }

    @Override
    public void onPause() {//1s cunado se abre una subpantalla por encima
        super.onPause();
        //removeListener();
        Util.showLog("ProfileFragment", "onPause");
    }


    public void initListener() {
        if(eventListener==null){
            Util.showLog("ProfileFragment", "Se agrego el listener");
            eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    user = snapshot.getValue(User.class);
                    if(user!=null){
                        Util.showLog("ProfileFragment", "Usuario obtenido");
                        binding.edtUsername.setText(user.getUsername());
                        binding.edtDescp.setText(user.getDescription());
                        if(user.getImg()!=null){
                            Picasso.get().load(user.getImg()).into(binding.userProfilePic);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Util.showLog("ProfileFragment", "Error al obtener usuario");
                }
            };
            dbRef = FirebaseDatabase.getInstance().getReference("users").child(auth.getUid());
            dbRoomRef = FirebaseDatabase.getInstance().getReference("rooms");
            dbRef.addValueEventListener(eventListener);
        }
    }

    public void initServices() {
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        loadingService = new LoadingService(requireContext());
    }

    public void removeListener() {
        if(eventListener!=null){
            Util.showLog("ProfileFragment", "Se removio el listener");
            dbRef.removeEventListener(eventListener);
            eventListener = null;
        }
    }



}