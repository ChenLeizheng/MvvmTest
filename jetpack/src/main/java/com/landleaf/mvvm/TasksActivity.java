package com.landleaf.mvvm;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

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
        Log.d("TasksActivity", "mViewModel:" + mViewModel);
        mViewModel.getOpenTaskEvent().observe(this, new Observer<Event<String>>() {
            @Override
            public void onChanged(@Nullable Event<String> stringEvent) {
                Toast.makeText(TasksActivity.this, stringEvent.getContentIfNotHandled(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
