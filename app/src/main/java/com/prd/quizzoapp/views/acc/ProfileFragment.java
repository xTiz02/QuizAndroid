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
import com.prd.quizzoapp.R;
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
    private LoadingService ls;
    private FirebaseStorage storage;
    private FirebaseAuth auth;
    private User user;
    private Uri setImageUri;
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentProfileBinding.bind(view);
        auth = FirebaseAuth.getInstance();
        ls = new LoadingService(getContext());
        storage = FirebaseStorage.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("users");
        dbRef.child(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    user = snapshot.getValue(User.class);
                    binding.edtUsername.setText(user.getUsername());
                    binding.tvEmailId.setText(user.getEmail());
                    binding.edtDescp.setText(user.getDescription());
                    Picasso.get().load(user.getImg()).into(binding.userProfilePic);
                }else {
                    Toast.makeText(getContext(), "Error al obtener el usuario", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Util.showLog("ProfileFragment", "Error al obtener el usuario");
                Toast.makeText(getContext(), "Error al obtener el usuario", Toast.LENGTH_SHORT).show();
            }
        });

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
                if(setImageUri!=null){
                    StorageReference storageReference = storage.getReference().child("upload").child(auth.getUid().toString());
                    storageReference.putFile(setImageUri).addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                String finalImageUri = uri.toString();
                                map.put("img", finalImageUri);
                                dbRef.child(auth.getUid()).updateChildren(map).addOnCompleteListener(task1 -> {
                                    if(task1.isSuccessful()){
                                        Toast.makeText(getContext(), "Usuario actualizado", Toast.LENGTH_SHORT).show();
                                    }else {
                                        Util.showLog("ProfileFragment", "Error al guardar usuario");
                                        Toast.makeText(getContext(), "Error al guardar usuario", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            });
                        }else {
                            Util.showLog("ProfileFragment", "Error al subir imagen");
                            Toast.makeText(getContext(), "Error al subir imagen", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    dbRef.child(auth.getUid()).updateChildren(map).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Usuario actualizado", Toast.LENGTH_SHORT).show();
                        } else {
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
                            Util.showLog("ProfileFragment", "Usuario actualizado en la sala");
                        }else {
                            Util.showLog("ProfileFragment", "Error al actualizar usuario en la sala");
                        }
                    });
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
        return inflater.inflate(R.layout.fragment_profile, container, false);
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
}