package com.prd.quizzoapp.views.home;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import com.prd.quizzoapp.R;
import com.prd.quizzoapp.databinding.FragmentHomeBinding;
import com.prd.quizzoapp.util.Data;

import org.jetbrains.annotations.Nullable;


public class HomeFragment extends Fragment {

    private CategoryAdapter categoryAdapter;
    private FragmentHomeBinding binding;

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
        binding = FragmentHomeBinding.bind(view);
        categoryAdapter = new CategoryAdapter(Data.getCategories(),requireContext(),checkButtonRoom());
        binding.rvCcategory.setHasFixedSize(true);
        binding.rvCcategory.setLayoutManager(new GridLayoutManager(requireContext(),2));
        binding.rvCcategory.setAdapter(categoryAdapter);

        binding.btnCreateRoom.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.button_disabled));
        binding.btnCreateRoom.setCompoundDrawableTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.dark_gray_1)));
        binding.btnCreateRoom.setEnabled(false);

        binding.btnCreateRoom.setOnClickListener(v -> {
            //Cargar CreateRoomFragment en el contenedor del main activity

            NavController navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);

            //Enviar la lista de categorias al fragmento CreateRoomFragment
            HomeFragmentDirections.ActionHomeFragmentToCreateRoomFragment action =
                    HomeFragmentDirections.actionHomeFragmentToCreateRoomFragment();
            action.setCategoriesList(categoryAdapter.getSelectedCategories());
            navController.navigate(action);


        });


    }

    public CategoryAdapter.OnClickCategory checkButtonRoom(){
        return () -> {
            if (!categoryAdapter.getSelectedCategories().isEmpty()){
                binding.btnCreateRoom.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.button_back));
                binding.btnCreateRoom.setCompoundDrawableTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.dark_gray_3)));
               binding.btnCreateRoom.setEnabled(true);
                System.out.println("Habilitado");
            }else {
                binding.btnCreateRoom.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.button_disabled));
                binding.btnCreateRoom.setCompoundDrawableTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.dark_gray_1)));
                binding.btnCreateRoom.setEnabled(false);
                System.out.println("Deshabilitado");
            }
        };
    }


}