package com.example.demo3;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    private EditText etName, etAddress, etPhone, etFruits, etPrice, etHours;
    private ImageButton btnBack, btnFavorite;
    private Button btnSave, btnNavigate;
    private Farm farm;
    private DBHelper dbHelper;
    private int currentUserId = 1;
    private String farmId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        dbHelper = new DBHelper(this);

        farmId = getIntent().getStringExtra("farm_id");
        if (farmId == null) farmId = "1";

        farm = FarmData.getFarmById(this, farmId);
        if (farm == null) {
            Toast.makeText(this, "数据加载失败", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // ========== 绑定控件 ==========
        etName = findViewById(R.id.et_name);
        etAddress = findViewById(R.id.et_address);
        etPhone = findViewById(R.id.et_phone);
        etFruits = findViewById(R.id.et_fruits);
        etPrice = findViewById(R.id.et_price);
        etHours = findViewById(R.id.et_hours);
        btnBack = findViewById(R.id.btn_back);
        btnFavorite = findViewById(R.id.btn_favorite);
        btnSave = findViewById(R.id.btn_save);
        btnNavigate = findViewById(R.id.btn_navigate);
        Button btnDelete = findViewById(R.id.btn_delete);

        // 绑定评分控件
        RatingBar ratingBar = findViewById(R.id.ratingBar);
        TextView tvRatingValue = findViewById(R.id.tv_rating_value);
        float rating = (float) farm.getRating();
        ratingBar.setRating(rating);
        tvRatingValue.setText(" (" + String.format("%.1f", rating) + ")");  // ✅ 已格式化

        ratingBar.setOnRatingBarChangeListener((ratingBar1, ratingValue, fromUser) -> {
            tvRatingValue.setText(" (" + String.format("%.1f", ratingValue) + ")");
        });

        // ========== 动态设置图标（根据名称） ==========
        TextView tvIcon = findViewById(R.id.tv_farm_icon);
        String name = farm.getName();

        if (name.contains("草莓")) {
            tvIcon.setText("🍓");
        } else if (name.contains("樱桃")) {
            tvIcon.setText("🍒");
        } else if (name.contains("葡萄")) {
            tvIcon.setText("🍇");
        } else if (name.contains("桃") || name.contains("梨")) {
            tvIcon.setText("🍑");
        } else {
            tvIcon.setText("🏡");
        }

        // ========== 显示数据 ==========
        etName.setText(farm.getName());
        etAddress.setText(farm.getAddress());
        etPhone.setText(farm.getPhone());
        etFruits.setText(farm.getFruits());
        etPrice.setText(farm.getPrice());
        etHours.setText(farm.getHours());

        updateFavoriteButton();

        // ========== 按钮监听 ==========
        btnBack.setOnClickListener(v -> finish());

        btnFavorite.setOnClickListener(v -> {
            if (dbHelper.isFavorite(currentUserId, farmId)) {
                dbHelper.removeFavorite(currentUserId, farmId);
                Toast.makeText(this, "已取消收藏", Toast.LENGTH_SHORT).show();
            } else {
                dbHelper.addFavorite(currentUserId, farmId);
                Toast.makeText(this, "已收藏", Toast.LENGTH_SHORT).show();
            }
            updateFavoriteButton();
        });

        // 保存时获取评分
        btnSave.setOnClickListener(v -> {
            farm.setName(etName.getText().toString());
            farm.setAddress(etAddress.getText().toString());
            farm.setPhone(etPhone.getText().toString());
            farm.setFruits(etFruits.getText().toString());
            farm.setPrice(etPrice.getText().toString());
            farm.setHours(etHours.getText().toString());
            farm.setRating(ratingBar.getRating());

            boolean success = dbHelper.updateFarm(farm);
            if (success) {
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
            }
        });

        btnNavigate.setOnClickListener(v -> {
            Uri uri = Uri.parse("geo:" + farm.getLat() + "," + farm.getLng() + "?q=" + farm.getName());
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        });

        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("确认删除")
                    .setMessage("确定要删除\"" + farm.getName() + "\"吗？")
                    .setPositiveButton("删除", (dialog, which) -> {
                        boolean success = dbHelper.deleteFarmWithFavorites(farmId);
                        if (success) {
                            Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "删除失败", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });
    }

    private void updateFavoriteButton() {
        if (dbHelper.isFavorite(currentUserId, farmId)) {
            btnFavorite.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            btnFavorite.setImageResource(android.R.drawable.btn_star_big_off);
        }
    }
}