package com.landleaf.mvvm;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.List;

/**
 * Author：Lei on 2020/12/2
 */
public class FirstFragment extends Fragment {

    private Button message;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.tasks_fragment,container,false);
        message = inflate.findViewById(R.id.message);
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),TasksActivity.class));
//                Navigation.findNavController(v).navigate(R.id.action_firstFragment_to_secondFragment);
            }
        });
        TasksViewModel mViewModel = ViewModelProviders.of(getActivity()).get(TasksViewModel.class);
        mViewModel.read().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                Log.d("FirstFragment", strings.toString());
            }
        });
        return inflate;
    }
}
