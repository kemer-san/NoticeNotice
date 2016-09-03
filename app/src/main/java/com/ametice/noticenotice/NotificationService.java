package com.ametice.noticenotice;

import android.content.Intent;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

/**
 * クラス名 ：NotificationService
 * 説明    ：通知文字列を取得しブロードキャストする
 * 最終更新 :2015/12/2
 * @version 1.0
 * @author  Y.Hiyoshi(ametis)
 */
public class NotificationService extends NotificationListenerService {

    /*  ブロードキャスト用   */
    private final String NOTIFICATION_TEXT = "NOTIFICATION_TEXT";

    /*  インテント用  */
    public static final String NOTIFICATION_SERVICE = "NOTIFICATION_SERVICE";

    /**
     * サービス開始時のコールバック
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 通知発生時のコールバック
     */
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);

        /*  通知文字列をブロードキャスト  */
        startBroadCast(sbn.getNotification().tickerText.toString());
    }

    /**
     * 通知削除時のコールバック
     */
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
    }

    /**
     * ブロードキャストの送信
     */
    private void startBroadCast(String str){
        Intent intent = new Intent("NOTIFICATION_SERVICE");
        intent.putExtra(NOTIFICATION_TEXT, str);
        sendBroadcast(intent);
    }
}
