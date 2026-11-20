package com.example.demo3;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "farm.db";
    private static final int DATABASE_VERSION = 1;

    // 表名
    public static final String TABLE_USER = "user";
    public static final String TABLE_FARM = "farm";
    public static final String TABLE_FAVORITE = "favorite";

    // 建表语句
    private static final String CREATE_USER_TABLE =
            "CREATE TABLE " + TABLE_USER + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT UNIQUE, " +
                    "password TEXT)";

    private static final String CREATE_FARM_TABLE =
            "CREATE TABLE " + TABLE_FARM + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "farm_id TEXT UNIQUE, " +
                    "name TEXT, " +
                    "address TEXT, " +
                    "lat REAL, " +
                    "lng REAL, " +
                    "rating REAL, " +
                    "fruits TEXT, " +
                    "price TEXT, " +
                    "phone TEXT, " +
                    "hours TEXT)";

    private static final String CREATE_FAVORITE_TABLE =
            "CREATE TABLE " + TABLE_FAVORITE + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_id INTEGER, " +
                    "farm_id TEXT, " +
                    "add_time TEXT)";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建三张表
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_FARM_TABLE);
        db.execSQL(CREATE_FAVORITE_TABLE);

        // 预置采摘园数据
        insertFarmData(db);
    }
    // 插入新采摘园
    public boolean insertFarm(Farm farm) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("farm_id", farm.getId());
        values.put("name", farm.getName());
        values.put("address", farm.getAddress());
        values.put("lat", farm.getLat());
        values.put("lng", farm.getLng());
        values.put("rating", farm.getRating());
        values.put("fruits", farm.getFruits());
        values.put("price", farm.getPrice());
        values.put("phone", farm.getPhone());
        values.put("hours", farm.getHours());

        long result = db.insert(TABLE_FARM, null, values);
        db.close();
        return result != -1;
    }
    // 更新采摘园信息
    public boolean updateFarm(Farm farm) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", farm.getName());
        values.put("address", farm.getAddress());
        values.put("phone", farm.getPhone());
        values.put("fruits", farm.getFruits());
        values.put("price", farm.getPrice());
        values.put("hours", farm.getHours());
        values.put("rating", farm.getRating());  // ← 添加这一行

        int result = db.update(TABLE_FARM, values, "farm_id = ?", new String[]{farm.getId()});
        db.close();
        return result > 0;
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 升级时删除旧表，重新创建
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FARM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITE);
        onCreate(db);
    }
    // 预置采摘园数据
    private void insertFarmData(SQLiteDatabase db) {
        String insertSql = "INSERT INTO " + TABLE_FARM +
                " (farm_id, name, address, lat, lng, rating, fruits, price, phone, hours) VALUES " +
                "('1', '绿源草莓采摘园', '郑州市惠济区绿源路', 34.85, 113.62, 4.8, '草莓、圣女果', '入园30元/人，带走20元/斤', '0371-88881234', '9:00-18:00')," +
                "('2', '樱桃沟生态园', '郑州市二七区樱桃沟', 34.68, 113.58, 4.6, '樱桃、桑葚', '入园25元/人，带走15元/斤', '0371-88885678', '8:30-19:00')," +
                "('3', '黄河滩葡萄园', '郑州市荥阳市黄河滩', 34.92, 113.41, 4.7, '葡萄', '免费入园，带走10元/斤', '0371-88889012', '9:00-18:30')," +
                "('4', '四季水果庄园', '郑州市中牟县郑开大道', 34.73, 114.02, 4.9, '草莓、桃、梨', '入园20元/人，带走15元/斤', '0371-88883456', '8:00-19:00')";
        db.execSQL(insertSql);
    }
    // 删除采摘园
    public boolean deleteFarm(String farmId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_FARM, "farm_id = ?", new String[]{farmId});
        db.close();
        return result > 0;
    }
    // 删除采摘园及其收藏记录
    public boolean deleteFarmWithFavorites(String farmId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_FARM, "farm_id = ?", new String[]{farmId});
            db.delete(TABLE_FAVORITE, "farm_id = ?", new String[]{farmId});
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            db.endTransaction();
            db.close();
        }
    }
    // 添加收藏
    public void addFavorite(int userId, String farmId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "INSERT INTO " + TABLE_FAVORITE + " (user_id, farm_id, add_time) VALUES (?, ?, datetime('now'))";
        db.execSQL(sql, new Object[]{userId, farmId});
        db.close();
    }

    // 删除收藏
    public void removeFavorite(int userId, String farmId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "DELETE FROM " + TABLE_FAVORITE + " WHERE user_id=? AND farm_id=?";
        db.execSQL(sql, new Object[]{userId, farmId});
        db.close();
    }

    // 检查是否已收藏
    public boolean isFavorite(int userId, String farmId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_FAVORITE + " WHERE user_id=? AND farm_id=?",
                new String[]{String.valueOf(userId), farmId});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }

    // 获取用户的所有收藏（返回farm_id列表）
    public List<String> getUserFavorites(int userId) {
        List<String> favorites = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT farm_id FROM " + TABLE_FAVORITE + " WHERE user_id=?",
                new String[]{String.valueOf(userId)});
        while (cursor.moveToNext()) {
            favorites.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return favorites;
    }
}