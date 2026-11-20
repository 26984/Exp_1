package com.example.demo3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private ListView listView;
    private FarmListAdapter adapter;
    private List<Farm> farmList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // ========== 设置 Toolbar ==========
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("我的采摘园列表");  // ← 设置标题
        toolbar.setNavigationOnClickListener(v -> finish());

        // ========== 底部导航栏 ==========
        Button btnNavMap = findViewById(R.id.btn_nav_map);
        Button btnNavList = findViewById(R.id.btn_nav_list);
        Button btnNavFavorite = findViewById(R.id.btn_nav_favorite);

        // 设置当前页面（列表页）的按钮高亮
        btnNavMap.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
        btnNavList.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
        btnNavFavorite.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));

        btnNavMap.setOnClickListener(v -> {
            Intent intent = new Intent(ListActivity.this, MapActivity.class);
            startActivity(intent);
        });

        btnNavList.setOnClickListener(v -> {
            Toast.makeText(this, "当前在列表页", Toast.LENGTH_SHORT).show();
        });

        btnNavFavorite.setOnClickListener(v -> {
            Intent intent = new Intent(ListActivity.this, FavoriteActivity.class);
            startActivity(intent);
        });

        // ========== 获取数据 ==========
        farmList = FarmData.getFarmList(this);

        if (farmList == null || farmList.isEmpty()) {
            Log.e("ListActivity", "farmList 为空");
            Toast.makeText(this, "暂无采摘园数据", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("ListActivity", "获取到 " + farmList.size() + " 个采摘园");

        // ========== 设置适配器 ==========
        adapter = new FarmListAdapter(farmList, getLayoutInflater());
        listView = findViewById(R.id.listview_farms);
        listView.setAdapter(adapter);

        // ========== 列表项点击事件 ==========
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Farm farm = farmList.get(position);
            Intent intent = new Intent(ListActivity.this, DetailActivity.class);
            intent.putExtra("farm_id", farm.getId());
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 重新从数据库加载数据
        List<Farm> newList = FarmData.getFarmList(this);
        if (newList != null && !newList.isEmpty()) {
            farmList.clear();
            farmList.addAll(newList);
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }
}