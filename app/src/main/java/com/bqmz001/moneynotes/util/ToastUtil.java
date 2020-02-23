package com.bqmz001.moneynotes.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.preference.PreferenceManager;


public class ToastUtil {
    private static boolean zuAnMode;
    private static Context mContext;
    private static SharedPreferences pref;

    //检查
    public static final int NO_NAME = 0;
    public static final int NO_COST = 1;
    public static final int NO_CONTENT = 2;
    public static final int NO_SUMMARY = 3;
    public static final int NO_TIME = 4;
    public static final int NO_BUDGET = 5;
    public static final int NO_COLOR = 6;
    //添加和删除
    public static final int SUCCESS_ADD = 7;
    public static final int SUCCESS_DEL = 8;
    public static final int FAILED_ADD = 9;
    public static final int FAILED_DEL = 10;
    public static final int SUCCESS_CHANGE = 23;
    public static final int FAILED_CHANGE = 24;
    //指纹相关
    public static final int SUCCESS_ADD_FINGERPRINT = 11;
    public static final int SUCCESS_DEL_FINGERPRINT = 12;
    public static final int FAILED_ADD_FINGERPRINT = 13;
    public static final int FAILED_DEL_FINGERPRINT = 14;
    public static final int SUCCESS_UNLOCK = 15;
    public static final int SUCCESS_AUTH = 16;
    public static final int FAILED_UNLOCK = 17;
    public static final int FAILED_AUTH = 18;
    public static final int VERSION_TOO_LOW = 19;
    public static final int DONOT_SUPPORT_FINGERPRINT = 20;
    public static final int NOT_LOCKSCREEN = 21;
    public static final int NOT_FINGERPRINT = 22;

    //其他
    public static final int AT_LEAST_ONE_CLASSIFICATION = 25;
    public static final int DO_NOT_DELETE_DEFAULT_OR_NOW = 26;

    public static final int PLEASE_SELECT_START_AND_END = 27;
    public static final int START_GREATER_THEN_STOP = 28;

    public static void init(Context context) {
        mContext = context;
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        zuAnMode = pref.getBoolean("ZuAnMode", false);

    }

    public static void refreshSwitch(boolean newValue) {
        zuAnMode = newValue;
    }


    public static void show(int content) {
        if (zuAnMode) {
            switch (content) {
                case 0:
                    Toast.makeText(mContext, "你🐴没名字", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(mContext, "没💴玩个屁", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(mContext, "买啥羞耻东西不敢写啊", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(mContext, "留图不留种，菊花万人捅", Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    Toast.makeText(mContext, "没时间玩你🐴呢", Toast.LENGTH_SHORT).show();
                    break;
                case 5:
                    Toast.makeText(mContext, "没💴你个穷B", Toast.LENGTH_SHORT).show();
                    break;
                case 6:
                    Toast.makeText(mContext, "你有本事给我点颜色看看啊", Toast.LENGTH_SHORT).show();
                    break;
                case 7:
                    Toast.makeText(mContext, "你🐴保住了", Toast.LENGTH_SHORT).show();
                    break;
                case 8:
                    Toast.makeText(mContext, "你🐴保住了", Toast.LENGTH_SHORT).show();
                    break;
                case 9:
                    Toast.makeText(mContext, "你🐴没保住", Toast.LENGTH_SHORT).show();
                    break;
                case 10:
                    Toast.makeText(mContext, "你🐴没保住", Toast.LENGTH_SHORT).show();
                    break;
                case 11:
                    Toast.makeText(mContext, "大猪蹄子封印成功", Toast.LENGTH_SHORT).show();
                    break;
                case 12:
                    Toast.makeText(mContext, "大猪蹄子解封成功", Toast.LENGTH_SHORT).show();
                    break;
                case 13:
                    Toast.makeText(mContext, "你个大猪蹄子竟然没封印住", Toast.LENGTH_SHORT).show();
                    break;
                case 14:
                    Toast.makeText(mContext, "玉皇大帝不给你面子", Toast.LENGTH_SHORT).show();
                    break;
                case 15:
                    Toast.makeText(mContext, "嗯，是这个封印", Toast.LENGTH_SHORT).show();
                    break;
                case 16:
                    Toast.makeText(mContext, "嗯，是这个封印", Toast.LENGTH_SHORT).show();
                    break;
                case 17:
                    Toast.makeText(mContext, "嗯，不是这个封印", Toast.LENGTH_SHORT).show();
                    break;
                case 18:
                    Toast.makeText(mContext, "嗯，不是这个封印", Toast.LENGTH_SHORT).show();
                    break;
                case 19:
                    Toast.makeText(mContext, "万年底层不更新，你手机是lowB", Toast.LENGTH_SHORT).show();
                    break;
                case 20:
                    Toast.makeText(mContext, "都啥时候了连个带指纹的手机都买不起", Toast.LENGTH_SHORT).show();
                    break;
                case 21:
                    Toast.makeText(mContext, "你不怕你手机里的大姐姐被人看到吗", Toast.LENGTH_SHORT).show();
                    break;
                case 22:
                    Toast.makeText(mContext, "你还没封印你的大猪蹄子，先去印一个去再来封", Toast.LENGTH_SHORT).show();
                    break;
                case 23:
                    Toast.makeText(mContext, "你🐴保住了", Toast.LENGTH_SHORT).show();
                    break;
                case 24:
                    Toast.makeText(mContext, "你\uD83D\uDC34没保住", Toast.LENGTH_SHORT).show();
                    break;
                case 25:
                    Toast.makeText(mContext, "你活该单身", Toast.LENGTH_SHORT).show();
                    break;
                case 26:
                    Toast.makeText(mContext, "大义灭亲吗？", Toast.LENGTH_SHORT).show();
                    break;
                case 27:
                    Toast.makeText(mContext, "光搞一半就不搞了，你在勾引我吗？", Toast.LENGTH_SHORT).show();
                    break;
                case 28:
                    Toast.makeText(mContext, "时光倒流X你\uD83D\uDC34", Toast.LENGTH_SHORT).show();
                    break;
                case 29:
                    break;

            }
        } else {
            switch (content) {
                case 0:
                    Toast.makeText(mContext, "请填写名称", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(mContext, "请填写消费金额", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(mContext, "请填写消费内容", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(mContext, "请填写备注", Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    Toast.makeText(mContext, "请设置时间", Toast.LENGTH_SHORT).show();
                    break;
                case 5:
                    Toast.makeText(mContext, "请设置预算", Toast.LENGTH_SHORT).show();
                    break;
                case 6:
                    Toast.makeText(mContext, "请设置颜色", Toast.LENGTH_SHORT).show();
                    break;
                case 7:
                    Toast.makeText(mContext, "添加成功", Toast.LENGTH_SHORT).show();
                    break;
                case 8:
                    Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
                    break;
                case 9:
                    Toast.makeText(mContext, "添加失败", Toast.LENGTH_SHORT).show();
                    break;
                case 10:
                    Toast.makeText(mContext, "删除失败", Toast.LENGTH_SHORT).show();
                    break;
                case 11:
                    Toast.makeText(mContext, "指纹解锁设置成功", Toast.LENGTH_SHORT).show();
                    break;
                case 12:
                    Toast.makeText(mContext, "指纹解锁移除成功", Toast.LENGTH_SHORT).show();
                    break;
                case 13:
                    Toast.makeText(mContext, "指纹解锁设置失败", Toast.LENGTH_SHORT).show();
                    break;
                case 14:
                    Toast.makeText(mContext, "指纹解锁移除失败", Toast.LENGTH_SHORT).show();
                    break;
                case 15:
                    Toast.makeText(mContext, "解锁成功", Toast.LENGTH_SHORT).show();
                    break;
                case 16:
                    Toast.makeText(mContext, "验证成功", Toast.LENGTH_SHORT).show();
                    break;
                case 17:
                    Toast.makeText(mContext, "解锁失败", Toast.LENGTH_SHORT).show();
                    break;
                case 18:
                    Toast.makeText(mContext, "验证失败", Toast.LENGTH_SHORT).show();
                    break;
                case 19:
                    Toast.makeText(mContext, "系统版本过低，不支持指纹功能", Toast.LENGTH_SHORT).show();
                    break;
                case 20:
                    Toast.makeText(mContext, "手机不支持指纹功能", Toast.LENGTH_SHORT).show();
                    break;
                case 21:
                    Toast.makeText(mContext, "尚未设置锁屏，请设置锁屏并添加一个指纹", Toast.LENGTH_SHORT).show();
                    break;
                case 22:
                    Toast.makeText(mContext, "尚未添加指纹，请在设置中添加一个指纹", Toast.LENGTH_SHORT).show();
                    break;
                case 23:
                    Toast.makeText(mContext, "修改成功", Toast.LENGTH_SHORT).show();
                    break;
                case 24:
                    Toast.makeText(mContext, "修改失败", Toast.LENGTH_SHORT).show();
                    break;
                case 25:
                    Toast.makeText(mContext, "最少保留一个分类", Toast.LENGTH_SHORT).show();
                    break;
                case 26:
                    Toast.makeText(mContext, "不允许删除当前用户或默认用户", Toast.LENGTH_SHORT).show();
                    break;
                case 27:
                    Toast.makeText(mContext, "开始和/或结束时间尚未选择，请将两个滚轮同时拨动方可选择时间", Toast.LENGTH_SHORT).show();
                    break;
                case 28:
                    Toast.makeText(mContext, "开始时间大于结束时间，请重新选择", Toast.LENGTH_SHORT).show();
                    break;
                case 29:
                    break;

            }
        }
    }

    public static void showContent(String content) {
        Toast.makeText(mContext, content, Toast.LENGTH_SHORT).show();
    }

}
