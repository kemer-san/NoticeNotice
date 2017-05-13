package com.ametice.noticenotice.util;

import android.app.ActivityManager;
import android.content.Context;
import java.util.ArrayList;
import java.util.List;

/**
 * クラス名 ：NoticeUtility
 * 説明    ：NoticeNotice汎用ユーティリティ
 * 最終更新 :2016/1/23
 * @version 1.0
 * @author  Y.Hiyoshi(ametis)
 */
public class NoticeUtility {

    /**
     * サービスが起動しているかを確認する
     * @param serviceName 確認したいサービスのクラス名
     * @param context コンテキスト
     */
    public static boolean isRunService(String serviceName,Context context){
        /*  サービスの一覧を取得  */
        ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = am.getRunningServices(Integer.MAX_VALUE);

        /*  クラス名リスト */
        ArrayList<String> serviceNameList = new ArrayList<String>();

        /*  クラス名を取得しリストに格納 */
        for(ActivityManager.RunningServiceInfo serviceInfo : serviceList){
            serviceNameList.add(serviceInfo.service.getClassName());
        }

        /*  引数のクラス名が含まれていたらtrueを返す  */
        return serviceNameList.contains(serviceName);
    }

    /**
     * 表示用時刻フォーマット変換
     * @param hour 時間
     * @param minute 分
     * @return 表示用時刻文字列"時間:分"
     */
    public static String toTimeFormat(int hour, int minute){
        return Integer.toString(hour) + ":" + Integer.toString(minute);
    }
}
