package com.landleaf.mvvm;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.util.Function;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModelProviders;

import java.util.List;

public class TasksActivity extends AppCompatActivity {

    private TasksViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tasks_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, TasksFragment.newInstance())
                    .commitNow();
        }
        mViewModel = ViewModelProviders.of(this).get(TasksViewModel.class);
        Log.d("TEST", "mViewModel:" + mViewModel);
        mViewModel.getOpenTaskEvent().observe(this, new Observer<Event<String>>() {
            @Override
            public void onChanged(@Nullable Event<String> stringEvent) {
                Toast.makeText(TasksActivity.this, stringEvent.getContentIfNotHandled(), Toast.LENGTH_SHORT).show();
            }
        });
        Transformations.map(mViewModel.getOpenTaskEvent(), new Function<Event<String>, Boolean>() {
            @Override
            public Boolean apply(Event<String> input) {
                return input.hasBeenHandled();
            }
        });
        mViewModel.read().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                Log.d("TEST", "strings:" + strings);
            }
        });
        List<String> value = mViewModel.read().getValue();
        Log.d("TEST", "value:" + value);
    }

}
