package com.example.demo3;

import android.widget.Button;
import android.content.Intent;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import java.util.List;

public class MapActivity extends AppCompatActivity {

    private MapView mapView;
    private BaiduMap baiduMap;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private List<Farm> farms;
    private MapStatusUpdate update;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_CODE);
        } else {
            initMap();
        }
    }

    private void initMap() {
        SDKInitializer.setAgreePrivacy(getApplicationContext(), true);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_map);
        mapView = findViewById(R.id.bmapView);
        baiduMap = mapView.getMap();
        dbHelper = new DBHelper(this);

        // 底部导航栏
        Button btnNavMap = findViewById(R.id.btn_nav_map);
        Button btnNavList = findViewById(R.id.btn_nav_list);
        Button btnNavFavorite = findViewById(R.id.btn_nav_favorite);

        btnNavMap.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
        btnNavList.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
        btnNavFavorite.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));

        btnNavMap.setOnClickListener(v -> Toast.makeText(this, "当前在地图页", Toast.LENGTH_SHORT).show());
        btnNavList.setOnClickListener(v -> startActivity(new Intent(this, ListActivity.class)));
        btnNavFavorite.setOnClickListener(v -> startActivity(new Intent(this, FavoriteActivity.class)));

        // 固定显示郑州
        LatLng zhengzhou = new LatLng(34.75, 113.65);
        update = MapStatusUpdateFactory.newLatLngZoom(zhengzhou, 12f);
        baiduMap.setMapStatus(update);

        farms = FarmData.getFarmList(this);

        // 添加标记并用 extraInfo 存储 ID
        for (Farm farm : farms) {
            LatLng point = new LatLng(farm.getLat(), farm.getLng());
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(android.R.drawable.ic_menu_mylocation);

            MarkerOptions optionMarker = new MarkerOptions()
                    .position(point)
                    .title(farm.getName())
                    .icon(icon);

            Marker marker = (Marker) baiduMap.addOverlay(optionMarker);

            Bundle bundle = new Bundle();
            bundle.putString("farm_id", farm.getId());
            marker.setExtraInfo(bundle);
        }

        // 点击标记：从 extraInfo 取 ID
        baiduMap.setOnMarkerClickListener(marker -> {
            Bundle bundle = marker.getExtraInfo();
            String farmId = bundle != null ? bundle.getString("farm_id") : null;

            if (farmId != null && !farmId.isEmpty()) {
                startActivity(new Intent(MapActivity.this, DetailActivity.class).putExtra("farm_id", farmId));
            } else {
                Toast.makeText(MapActivity.this, "无法获取采摘园ID", Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        // 回到郑州按钮
        Button btnCenter = findViewById(R.id.btn_center);
        if (btnCenter != null) {
            btnCenter.setOnClickListener(v -> {
                if (update != null) baiduMap.animateMapStatus(update);
                Toast.makeText(this, "已回到郑州", Toast.LENGTH_SHORT).show();
            });
        }

        // 长按地图：添加新采摘园
        baiduMap.setOnMapLongClickListener(point -> {
            double lat = point.latitude;
            double lng = point.longitude;

            EditText input = new EditText(MapActivity.this);
            input.setHint("请输入采摘园名称（如：张三家葡萄园）");

            new AlertDialog.Builder(MapActivity.this)
                    .setTitle("添加新采摘园")
                    .setView(input)
                    .setPositiveButton("添加", (dialog, which) -> {
                        String name = input.getText().toString().trim();
                        if (name.isEmpty()) {
                            Toast.makeText(MapActivity.this, "名称不能为空", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String newId = String.valueOf(System.currentTimeMillis());
                        String address = "用户添加的位置";
                        double rating = 5.0;
                        String fruits = "待补充";
                        String price = "待补充";
                        String phone = "待补充";
                        String hours = "待补充";

                        Farm newFarm = new Farm(newId, name, address, lat, lng, rating, fruits, price, phone, hours);

                        boolean success = dbHelper.insertFarm(newFarm);

                        if (success) {
                            Toast.makeText(MapActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                            reloadMapMarkers();
                        } else {
                            Toast.makeText(MapActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });

        Toast.makeText(this, "已添加 " + farms.size() + " 个采摘园", Toast.LENGTH_SHORT).show();
    }

    private void reloadMapMarkers() {
        baiduMap.clear();

        farms = FarmData.getFarmList(this);

        for (Farm farm : farms) {
            LatLng point = new LatLng(farm.getLat(), farm.getLng());
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(android.R.drawable.ic_menu_mylocation);

            MarkerOptions optionMarker = new MarkerOptions()
                    .position(point)
                    .title(farm.getName())
                    .icon(icon);

            Marker marker = (Marker) baiduMap.addOverlay(optionMarker);

            Bundle bundle = new Bundle();
            bundle.putString("farm_id", farm.getId());
            marker.setExtraInfo(bundle);
        }

        Toast.makeText(this, "已加载 " + farms.size() + " 个采摘园", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initMap();
        } else {
            Toast.makeText(this, "需要位置权限", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override protected void onResume() { super.onResume(); if (mapView != null) mapView.onResume(); }
    @Override protected void onPause() { super.onPause(); if (mapView != null) mapView.onPause(); }
    @Override protected void onDestroy() { super.onDestroy(); if (mapView != null) mapView.onDestroy(); }
}