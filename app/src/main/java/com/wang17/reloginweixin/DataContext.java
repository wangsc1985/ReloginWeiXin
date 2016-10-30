package com.wang17.reloginweixin;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by 阿弥陀佛 on 2015/11/18.
 */
public class DataContext {

    private DatabaseHelper dbHelper;

    public DataContext(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void deleteMemorialDay(UUID id){
        //获取数据库对象
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("memorialDay", "id=?", new String[]{id.toString()});
        //关闭SQLiteDatabase对象
        db.close();
    }

    public Setting getSetting(String key) {

        //获取数据库对象
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        //查询获得游标
        Cursor cursor = db.query("setting", null, "key=?", new String[]{key}, null, null, null);
        //判断游标是否为空
        while (cursor.moveToNext()) {
            Setting setting = new Setting(key, cursor.getString(1));
            return setting;
        }
        return null;
    }

    /**
     * 修改制定key配置，如果不存在则创建。
     * @param key
     * @param value
     */
    public void editSetting(String key, String value) {
        //获取数据库对象
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //使用update方法更新表中的数据
        ContentValues values = new ContentValues();
        values.put("value", value);
        if (db.update("setting", values, "key=?", new String[]{key}) == 0) {
            this.addSetting(key, value);
        }
        db.close();
    }

    public void deleteSetting(String key) {
        //获取数据库对象
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("setting", "key=?", new String[]{key});
//        String sql = "DELETE FROM setting WHERE userId="+userId.toString()+" AND key="+key;
//        addLog(new Log(sql,userId),db);
        //关闭SQLiteDatabase对象
        db.close();
    }

    public void addSetting(String key, String value) {
        //获取数据库对象
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //使用insert方法向表中插入数据
        ContentValues values = new ContentValues();
        values.put("key", key);
        values.put("value", value);
        //调用方法插入数据
        db.insert("setting", "key", values);
        //关闭SQLiteDatabase对象
        db.close();
    }


}
