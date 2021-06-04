package com.landleaf.everyday;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.landleaf.everyday.adapter.PageAdapter2;
import com.landleaf.everyday.bean.TestBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Author：Lei on 2020/12/7
 */
public class DiffRvActivity extends AppCompatActivity {
    List<TestBean> list = new ArrayList<>();
    private PageAdapter2 pageAdapter2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rv_diff);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        Button btSend = findViewById(R.id.btSend);
        for (int i = 0; i < 6; i++) {
            list.add(new TestBean(i,"在家"+i));
        }
        pageAdapter2 = new PageAdapter2(this, list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(pageAdapter2);

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<TestBean> models = new ArrayList<>();
                for (TestBean testBean : list) {
                    models.add(testBean.clone());
                }
                TestBean testBean = models.get(0);
                testBean.setContent("aaa");
                pageAdapter2.setData(models);
            }
        });
    }
}
