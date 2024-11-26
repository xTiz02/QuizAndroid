package com.prd.quizzoapp.views.home;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import com.prd.quizzoapp.R;
import com.prd.quizzoapp.databinding.FragmentHomeBinding;
import com.prd.quizzoapp.model.entity.RoomConfig;
import com.prd.quizzoapp.model.service.LoadingService;
import com.prd.quizzoapp.model.service.ResultService;
import com.prd.quizzoapp.model.service.RoomService;
import com.prd.quizzoapp.model.service.intf.ActionCallback;
import com.prd.quizzoapp.model.service.intf.DataActionCallback;
import com.prd.quizzoapp.util.Data;
import com.prd.quizzoapp.util.DataSharedPreference;
import com.prd.quizzoapp.util.Util;

import org.jetbrains.annotations.Nullable;


public class HomeFragment extends Fragment {

    private CategoryAdapter categoryAdapter;
    private FragmentHomeBinding binding;
    private LoadingService ls;
    private RoomService rs;
    private ResultService resultService;

    public HomeFragment() {
        // Required empty public constructor

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @SuppressLint("UseCompatTextViewDrawableApis")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Util.showLog("HomeFragment", "Se presiono el boton de regresar");
            }
        });
        binding = FragmentHomeBinding.bind(view);
        ls = new LoadingService(requireContext());
        rs = new RoomService(requireContext());
        resultService = new ResultService(requireContext());
        categoryAdapter = new CategoryAdapter(Data.getCategories(),
                requireContext(),
                DataSharedPreference.getData(Util.ROOM_UUID_KEY, requireContext())!=null? () -> {}:checkButtonRoom());
        binding.rvCcategory.setHasFixedSize(true);
        binding.rvCcategory.setLayoutManager(new GridLayoutManager(requireContext(),2));
        binding.rvCcategory.setAdapter(categoryAdapter);

        binding.btnCreateRoom.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.button_disabled));
        binding.btnCreateRoom.setCompoundDrawableTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.dark_gray_1)));
        binding.btnCreateRoom.setEnabled(false);

        if(DataSharedPreference.getData(Util.ROOM_UUID_KEY, requireContext())!=null) {
            binding.btnFindRoom.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.button_disabled));
            binding.btnFindRoom.setCompoundDrawableTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.dark_gray_1)));
            binding.btnFindRoom.setEnabled(false);
        }

        binding.btnCreateRoom.setOnClickListener(v -> {
            //Cargar CreateRoomFragment en el contenedor del main activity

            NavController navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);

            //Enviar la lista de categorias al fragmento CreateRoomFragment
            HomeFragmentDirections.ActionHomeFragmentToCreateRoomFragment action =
                    HomeFragmentDirections.actionHomeFragmentToCreateRoomFragment();
            action.setCategoriesList(categoryAdapter.getSelectedCategories());
            navController.navigate(action);


        });

        binding.btnFindRoom.setOnClickListener(v -> {
            //mostrar dialog
            Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.find_room_dialog);
            dialog.setCancelable(true);
            dialog.show();

            Button btnFind = dialog.findViewById(R.id.btnFind);
            EditText etRoomId = dialog.findViewById(R.id.edtCode);
            btnFind.setOnClickListener(v1 -> {

                String edtCode = etRoomId.getText().toString().trim();
                if(edtCode.isEmpty()){
                    etRoomId.setError("Ingrese un código");
                    return;
                }
                ls.showLoading("Buscando sala...");
                rs.findAndJoinRoom(
                        edtCode,
                        new DataActionCallback<RoomConfig>() {
                            @Override
                            public void onSuccess(RoomConfig data) {
                                System.out.println("RoomConfig: "+data);
                                resultService.saveUserResult(
                                        DataSharedPreference.getData(Util.ROOM_UUID_KEY, requireContext()),
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
                                            }
                                        });
                                NavController navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);
                                navController.navigate(R.id.roomFragment);
                                dialog.dismiss();
                                ls.hideLoading();
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Util.showToastLog("No se encontro la sala",requireContext());
                                ls.hideLoading();
                            }
                        }
                );
                //Cargar FindRoomFragment en el contenedor del main activity
                /*NavController navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);
                navController.navigate(R.id.action_homeFragment_to_roomFragment);
                ls.hideLoading();*/

            });
        });

    }

    @SuppressLint("UseCompatTextViewDrawableApis")
    public CategoryAdapter.OnClickCategory checkButtonRoom(){
        return () -> {
            if (!categoryAdapter.getSelectedCategories().isEmpty()) {
                binding.btnCreateRoom.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.button_back));
                binding.btnCreateRoom.setCompoundDrawableTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.dark_gray_3)));
                binding.btnCreateRoom.setEnabled(true);
                System.out.println("Habilitado");
            } else {
                binding.btnCreateRoom.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.button_disabled));
                binding.btnCreateRoom.setCompoundDrawableTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.dark_gray_1)));
                binding.btnCreateRoom.setEnabled(false);
                System.out.println("Deshabilitado");
            }

        };
    }

}