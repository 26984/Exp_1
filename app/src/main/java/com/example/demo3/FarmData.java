package com.example.demo3;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

public class FarmData {

    public static List<Farm> getFarmList(Context context) {
        List<Farm> farms = new ArrayList<>();
        try {
            DBHelper dbHelper = new DBHelper(context);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.TABLE_FARM, null);
            while (cursor.moveToNext()) {
                Farm farm = new Farm(
                        cursor.getString(cursor.getColumnIndex("farm_id")),
                        cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getString(cursor.getColumnIndex("address")),
                        cursor.getDouble(cursor.getColumnIndex("lat")),
                        cursor.getDouble(cursor.getColumnIndex("lng")),
                        cursor.getDouble(cursor.getColumnIndex("rating")),
                        cursor.getString(cursor.getColumnIndex("fruits")),
                        cursor.getString(cursor.getColumnIndex("price")),
                        cursor.getString(cursor.getColumnIndex("phone")),
                        cursor.getString(cursor.getColumnIndex("hours"))
                );
                farms.add(farm);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            return getHardcodedFarmList();
        }
        if (farms.isEmpty()) {
            return getHardcodedFarmList();
        }
        return farms;
    }

    public static Farm getFarmById(Context context, String id) {
        try {
            DBHelper dbHelper = new DBHelper(context);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.TABLE_FARM + " WHERE farm_id=?", new String[]{id});
            if (cursor.moveToFirst()) {
                Farm farm = new Farm(
                        cursor.getString(cursor.getColumnIndex("farm_id")),
                        cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getString(cursor.getColumnIndex("address")),
                        cursor.getDouble(cursor.getColumnIndex("lat")),
                        cursor.getDouble(cursor.getColumnIndex("lng")),
                        cursor.getDouble(cursor.getColumnIndex("rating")),
                        cursor.getString(cursor.getColumnIndex("fruits")),
                        cursor.getString(cursor.getColumnIndex("price")),
                        cursor.getString(cursor.getColumnIndex("phone")),
                        cursor.getString(cursor.getColumnIndex("hours"))
                );
                cursor.close();
                db.close();
                return farm;
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            return getHardcodedFarmById(id);
        }
        return getHardcodedFarmById(id);
    }

    public static List<Farm> getHardcodedFarmList() {
        List<Farm> farms = new ArrayList<>();
        farms.add(new Farm("1", "绿源草莓采摘园", "郑州市惠济区绿源路", 34.85, 113.62, 4.8, "草莓、圣女果", "入园30元/人，带走20元/斤", "0371-88881234", "9:00-18:00"));
        farms.add(new Farm("2", "樱桃沟生态园", "郑州市二七区樱桃沟", 34.68, 113.58, 4.6, "樱桃、桑葚", "入园25元/人，带走15元/斤", "0371-88885678", "8:30-19:00"));
        farms.add(new Farm("3", "黄河滩葡萄园", "郑州市荥阳市黄河滩", 34.92, 113.41, 4.7, "葡萄", "免费入园，带走10元/斤", "0371-88889012", "9:00-18:30"));
        farms.add(new Farm("4", "四季水果庄园", "郑州市中牟县郑开大道", 34.73, 114.02, 4.9, "草莓、桃、梨", "入园20元/人，带走15元/斤", "0371-88883456", "8:00-19:00"));
        return farms;
    }

    public static Farm getHardcodedFarmById(String id) {
        for (Farm farm : getHardcodedFarmList()) {
            if (farm.getId().equals(id)) {
                return farm;
            }
        }
        return null;
    }
}