package com.example.demo3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.ArrayList;
import java.util.List;
import androidx.core.content.ContextCompat;
public class FavoriteActivity extends AppCompatActivity {

    private ListView listView;
    private TextView tvEmpty;
    private List<Farm> favoriteFarms;
    private FarmListAdapter adapter;
    private DBHelper dbHelper;
    private int currentUserId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        // 设置Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        // ========== 底部导航栏 ==========
        Button btnNavMap = findViewById(R.id.btn_nav_map);
        Button btnNavList = findViewById(R.id.btn_nav_list);
        Button btnNavFavorite = findViewById(R.id.btn_nav_favorite);

        btnNavMap.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
        btnNavList.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
        btnNavFavorite.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));

        btnNavMap.setOnClickListener(v -> {
            Intent intent = new Intent(FavoriteActivity.this, MapActivity.class);
            startActivity(intent);
        });

        btnNavList.setOnClickListener(v -> {
            Intent intent = new Intent(FavoriteActivity.this, ListActivity.class);
            startActivity(intent);
        });

        btnNavFavorite.setOnClickListener(v -> {
            Toast.makeText(this, "当前在收藏页", Toast.LENGTH_SHORT).show();
        });

        dbHelper = new DBHelper(this);
        listView = findViewById(R.id.listview_farms);
        tvEmpty = findViewById(R.id.tv_empty);

        loadFavorites();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavorites();
    }

    private void loadFavorites() {
        List<String> favoriteIds = dbHelper.getUserFavorites(currentUserId);

        favoriteFarms = new ArrayList<>();
        for (String id : favoriteIds) {
            Farm farm = FarmData.getFarmById(this, id);
            if (farm != null) {
                favoriteFarms.add(farm);
            }
        }

        if (favoriteFarms.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);

            adapter = new FarmListAdapter(favoriteFarms, getLayoutInflater());
            listView.setAdapter(adapter);

            listView.setOnItemClickListener((parent, view, position, id) -> {
                Farm farm = favoriteFarms.get(position);
                Intent intent = new Intent(FavoriteActivity.this, DetailActivity.class);
                intent.putExtra("farm_id", farm.getId());
                startActivity(intent);
            });
        }
    }
}