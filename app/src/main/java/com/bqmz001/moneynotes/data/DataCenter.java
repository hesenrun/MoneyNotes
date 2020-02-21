package com.bqmz001.moneynotes.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;

import com.bqmz001.moneynotes.entity.Classification;
import com.bqmz001.moneynotes.entity.ClassificationFakeCount;
import com.bqmz001.moneynotes.entity.Note;
import com.bqmz001.moneynotes.entity.User;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.List;

public class DataCenter {

    private static User defaultUser;
    private static User nowUser;

    //刷新用户
    public static void refreshUser() {
        if (defaultUser != null)
            defaultUser = getUser(defaultUser.getId());
        if (nowUser != null)
            nowUser = getUser(nowUser.getId());
    }

    //获取数据库里的默认帐户
    public static User getDefaultUser() {
        if (defaultUser == null)
            defaultUser = LitePal.where("isDefault=?", "1").find(User.class).get(0);
        nowUser = defaultUser;
        return defaultUser;

    }

    //获取当前管理帐户
    public static User getNowUser() {
        if (nowUser != null && defaultUser != null)
            return nowUser;
        else if (nowUser == null && defaultUser != null)
            return defaultUser;
        else if (nowUser == null && defaultUser == null)
            return getDefaultUser();
        return null;
    }

    //设置当前帐户
    public static void setNowUser(User user) {
        nowUser = user;
    }

    //将一个帐户设置为数据库中的默认帐户
    public static void setDefaultUser(User defaultUser) {
        User u = new User();
        u.setDefault(false);
        u.updateAll();
        defaultUser.setDefault(true);
        defaultUser.save();
    }

    //获取用户列表
    public static List<User> getUserList() {
        return LitePal.findAll(User.class);
    }

    //全新APP创建默认帐户和分类
    public static void createNew() {
        User user = new User();
        user.setName("默认帐户");
        user.setColor(Color.parseColor("#03a9f4"));
        user.setBudget(1000);
        user.setDefault(true);
        user.save();

        Classification classification1 = new Classification();
        classification1.setUser(user);
        classification1.setName("衣服饰品");
        classification1.setColor(Color.parseColor("#9C27B0"));
        classification1.save();

        Classification classification2 = new Classification();
        classification2.setUser(user);
        classification2.setName("食品酒水");
        classification2.setColor(Color.parseColor("#E91E63"));
        classification2.save();

        Classification classification3 = new Classification();
        classification3.setUser(user);
        classification3.setName("居家物业");
        classification3.setColor(Color.parseColor("#3F51B5"));
        classification3.save();

        Classification classification4 = new Classification();
        classification4.setUser(user);
        classification4.setName("行车交通");
        classification4.setColor(Color.parseColor("#03A9F4"));
        classification4.save();

        Classification classification5 = new Classification();
        classification5.setUser(user);
        classification5.setName("交通通讯");
        classification5.setColor(Color.parseColor("#009688"));
        classification5.save();

        Classification classification6 = new Classification();
        classification6.setUser(user);
        classification6.setName("休闲娱乐");
        classification6.setColor(Color.parseColor("#00BCD4"));
        classification6.save();

        Classification classification7 = new Classification();
        classification7.setUser(user);
        classification7.setName("学习进修");
        classification7.setColor(Color.parseColor("#CDDC39"));
        classification7.save();

        Classification classification8 = new Classification();
        classification8.setUser(user);
        classification8.setName("人情往来");
        classification8.setColor(Color.parseColor("#FF9800"));
        classification8.save();

        Classification classification9 = new Classification();
        classification9.setUser(user);
        classification9.setName("医疗保健");
        classification9.setColor(Color.parseColor("#F44336"));
        classification9.save();

        Classification classification10 = new Classification();
        classification10.setUser(user);
        classification10.setName("金融保险");
        classification10.setColor(Color.parseColor("#4CAF50"));
        classification10.save();

        Classification classification11 = new Classification();
        classification11.setUser(user);
        classification11.setName("其他杂项");
        classification11.setColor(Color.parseColor("#607D8B"));
        classification11.save();
    }

    //新建帐户后新建分类
    public static void newUserCreateClassification(User newUser) {
        Classification classification1 = new Classification();
        classification1.setUser(newUser);
        classification1.setName("衣服饰品");
        classification1.setColor(Color.parseColor("#9C27B0"));
        classification1.save();

        Classification classification2 = new Classification();
        classification2.setUser(newUser);
        classification2.setName("食品酒水");
        classification2.setColor(Color.parseColor("#E91E63"));
        classification2.save();

        Classification classification3 = new Classification();
        classification3.setUser(newUser);
        classification3.setName("居家物业");
        classification3.setColor(Color.parseColor("#3F51B5"));
        classification3.save();

        Classification classification4 = new Classification();
        classification4.setUser(newUser);
        classification4.setName("行车交通");
        classification4.setColor(Color.parseColor("#03A9F4"));
        classification4.save();

        Classification classification5 = new Classification();
        classification5.setUser(newUser);
        classification5.setName("交通通讯");
        classification5.setColor(Color.parseColor("#009688"));
        classification5.save();

        Classification classification6 = new Classification();
        classification6.setUser(newUser);
        classification6.setName("休闲娱乐");
        classification6.setColor(Color.parseColor("#00BCD4"));
        classification6.save();

        Classification classification7 = new Classification();
        classification7.setUser(newUser);
        classification7.setName("学习进修");
        classification7.setColor(Color.parseColor("#CDDC39"));
        classification7.save();

        Classification classification8 = new Classification();
        classification8.setUser(newUser);
        classification8.setName("人情往来");
        classification8.setColor(Color.parseColor("#FF9800"));
        classification8.save();

        Classification classification9 = new Classification();
        classification9.setUser(newUser);
        classification9.setName("医疗保健");
        classification9.setColor(Color.parseColor("#F44336"));
        classification9.save();

        Classification classification10 = new Classification();
        classification10.setUser(newUser);
        classification10.setName("金融保险");
        classification10.setColor(Color.parseColor("#4CAF50"));
        classification10.save();

        Classification classification11 = new Classification();
        classification11.setUser(newUser);
        classification11.setName("其他杂项");
        classification11.setColor(Color.parseColor("#607D8B"));
        classification11.save();
    }

    //获取单个用户
    public static User getUser(int user_id) {
        return LitePal.find(User.class, user_id);
    }

    //更新或保存单个用户
    public static boolean saveUser(User user) {
        if (user.isDefault() == true) {
            ContentValues values = new ContentValues();
            values.put("isdefault", "0");
            LitePal.updateAll(User.class, values, "isdefault = ?", "1");
        }
        return user.save();
    }

    //删除用户
    public static void deleteUser(User user) {
        LitePal.deleteAll(Note.class, "user_id=?", user.getId() + "");
        LitePal.deleteAll(Classification.class, "user_id=?", user.getId() + "");
        LitePal.delete(User.class, user.getId());
    }

    //获取分类列表
    public static List<Classification> getClassificationList(User user) {
        List<Classification> classificationList = LitePal.where("user_id=?", "" + user.getId()).find(Classification.class);
        return classificationList;
    }

    //获取一个分类
    public static Classification getClassification(int classification_id) {
        return LitePal.find(Classification.class, classification_id);
    }

    //删除分类
    public static void deleteClassification(Classification classification) {
        LitePal.deleteAll(Note.class, "classification_id=?", classification.getId() + "");
        LitePal.delete(Classification.class, classification.getId());
    }

    //保存/更新分类
    public static boolean saveClassification(Classification classification) {
        return classification.save();
    }

    //获取笔记列表
    public static List<Note> getNoteList(User user) {
        return LitePal.where("user_id=?", "" + user.getId()).order("time desc").find(Note.class, true);
    }

    public static List<Note> getNoteList(User user, long startTime, long endTime) {
        return LitePal.where("user_id=? and time between ? and ?", "" + user.getId(), startTime + "", endTime + "").order("time desc").find(Note.class, true);
    }

    //获取笔记
    public static Note getNote(int note_id) {
        return LitePal.find(Note.class, note_id, true);
    }

    //保存笔记
    public static boolean saveNote(Note note) {
        return note.save();
    }

    //删除笔记
    public static void deleteNote(Note note) {
        LitePal.delete(Note.class, note.getId());
    }



    /*
    下为统计部分，上为一般增删改查操作
     */

    public static List<ClassificationFakeCount> getPieChartData(long startTime, long stopTime, User user) {
        List<ClassificationFakeCount> list = new ArrayList<>();

        Cursor cursor = LitePal.findBySQL("SELECT\n" +
                "\tclassification_id as id,\n" +
                "\tclassification.name as name,\n" +
                "\tclassification.color as color,\n" +
                "\tCOUNT( cost ) as count,\n" +
                "\tSUM( cost ) as sum \n" +
                "FROM\n" +
                "\tnote\n" +
                "\tJOIN classification ON note.classification_id\t= classification.id\n" +
                "\twhere time BETWEEN " + startTime + " and " + stopTime + " and note.user_id =" + user.getId() + "\n" +
                "\tGROUP BY classification_id\n" +
                "\tORDER BY classification_id");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                int color = cursor.getInt(cursor.getColumnIndex("color"));
                int count = cursor.getInt(cursor.getColumnIndex("count"));
                float sum = cursor.getFloat(cursor.getColumnIndex("sum"));
                list.add(new ClassificationFakeCount(id, name, color, sum, count));
            } while (cursor.moveToNext());
        }
        return list;
    }
}
