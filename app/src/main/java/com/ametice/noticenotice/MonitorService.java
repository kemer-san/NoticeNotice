package com.ametice.noticenotice;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * クラス名 ：MonitorService
 * 説明    ：通知取得の監視を行うサービス
 * 最終更新 :2016/2/20
 * @version 1.2
 * @author  Y.Hiyoshi(ametis)
 */
public class MonitorService extends Service {

    /*  ブロードキャスト用   */
    private final String NOTIFICATION_TEXT = "NOTIFICATION_TEXT";

    /*  インテント用  */
    public static final String NOTIFICATION_SERVICE = "NOTIFICATION_SERVICE";

    /*  ブロードキャスト文字列識別用   */
    private final String ON_RECIEVE = "ON_RECIEVE";
    private final String ON_RECIEVE_TIME = "ON_RECIEVE_TIME";

    /*  分単位   */
    private static final int MINUTE = 1000 * 60;

    /*  確認間隔    */
    private int interval;

    /*  通知文字列リスト  */
    private ArrayList<String> noticeList = new ArrayList<String>();

    /*  バインダーの生成    */
    private final IBinder mBinder = new MonitorServiceLocalBinder();

    @Override
    public void onCreate() {
        //レシーバー登録
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NOTIFICATION_SERVICE);
        registerReceiver(NotificationBroadcasts, intentFilter) ;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("onStartCommand", "onStartCommand Received start id " + startId + ": " + intent);

        /*  インテントから文字列を取得   */
        String str = intent.getStringExtra(ON_RECIEVE);

        /*  確認タイミングの識別子が検出された場合   */
        if(str != null && str.equals(ON_RECIEVE_TIME)){
            Toast.makeText(this, "onStartCommand#"+str, Toast.LENGTH_SHORT).show();

            /*  次の通知時刻を設定   */
            setTimer();

            /*  送信する曜日・時刻範囲の場合   */
            if(isRunDayOfWeek() == true && isRunTime() == true){
                /*  メール送信   */
                sendMail();
            }

        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        /*  登録したレシーバを解除する   */
        unregisterReceiver(NotificationBroadcasts);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent){
    }

    @Override
    public boolean onUnbind(Intent intent){
        /*  戻り値をtrueに設定 */
        return true;
    }

    /**
     * 通知サービスの起動
     */
    public void onNotice(){
        /*  デバッグ用   */
        Log.v("MonitorService", "onNotice");

        /*  通知取得サービスのインテントを生成    */
        Intent service = new Intent(MonitorService.this, NotificationService.class);

        /*  通知サービスの開始   */
        //startService(service);

        /*  確認間隔の設定 */
        setCheckInterval();

        setTimer();
    }

    /**
     * 通知サービスの停止
     */
    public void offNotice(){
        /*  デバッグ用   */
        Log.v("MonitorService", "offNotice");

        /*  通知取得サービスのインテントを生成    */
        Intent service = new Intent(MonitorService.this, NotificationService.class);

        /*  通知サービスの停止   */
        //stopService(service);
    }

    /**
     * 通知リストへの挿入
     */
    private void insertNoticeList(String text) {
        /*  引数の文字列が通知リストにない場合は格納   */
        if(!noticeList.contains(text)) {
            noticeList.add(text);
        }
    }

    /**
     * 確認間隔を設定
     */
    private void setCheckInterval(){
        /*  ユーザーが設定した確認間隔を取得    */
        interval = Integer.parseInt(new NoticeSaveData(this).loadCheckInterval());
        //Toast.makeText(this, Integer.toString(interval), Toast.LENGTH_SHORT).show();
    }

    /**
     * 通知を監視する曜日であるか確認
     * @return 判定値
     */
    private boolean isRunDayOfWeek() {
        /*  システムの曜日を取得  */
        Calendar cal = Calendar.getInstance();
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

        /*  今日の曜日が曜日指定設定でTRUEであるか確認    */
        NoticeSaveData nsd = new NoticeSaveData(this);
        if(nsd.loadDayOfWeek(dayOfWeek) == true)return true;

        return false;
    }

    /**
     * 通知を監視する時刻であるか確認
     * @return 判定値
     */
    private boolean isRunTime() {
            /*  現在日時・時刻の取得 */
        Calendar currentTime = Calendar.getInstance();
        currentTime.getTime();

            /*  現在の年、月、日の取り出し  */
        int year = currentTime.get(Calendar.YEAR);
        int month = currentTime.get(Calendar.MONTH);
        int day = currentTime.get(Calendar.DATE);

            /*  設定から確認開始時刻の取得 */
        String settingStartTime = new NoticeSaveData(getApplicationContext()).loadStartTime();
        String settingEndTime = new NoticeSaveData(getApplicationContext()).loadEndTime();

            /*  設定から確認開始時刻の時、分の取り出し  */
        String splitStartTime[] = settingStartTime.split(":", 2);
        int startHour = Integer.parseInt(splitStartTime[0]);
        int startMinute = Integer.parseInt(splitStartTime[1]);

            /*  確認開始時刻比較用インスタンスの生成  */
        Calendar startTime = Calendar.getInstance();
        startTime.set(year, month, day, startHour, startMinute, 0);

            /*  設定から確認開始時刻の時、分の取り出し  */
        String splitEndTime[] = settingEndTime.split(":", 2);
        int endHour = Integer.parseInt(splitEndTime[0]);
        int endMinute = Integer.parseInt(splitEndTime[1]);

            /*  確認開始時刻比較用インスタンスの生成  */
        Calendar endTime = Calendar.getInstance();
        endTime.set(year, month, day, endHour, endMinute, 0);

        if(0 <= startTime.compareTo(endTime)){
            //startTime.set(year, month, day, startHour, startMinute, 0);
            //endTime.set(year, month, day + 1, endHour, endMinute, 0);

            endTime.add(Calendar.DATE, 1);
        };


        int diff1 = currentTime.compareTo(startTime);
        int diff2 = currentTime.compareTo(endTime);

        /*  確認開始時刻より大きく、確認終了時刻より小さい場合   */
        if(0 <= diff1 && diff2 <= 0)return true;

        return false;
    }

    /*  サービス接続用バインダー  */
    public class MonitorServiceLocalBinder extends Binder {
        /*  サービスの取得 */
        MonitorService getService() {
            return MonitorService.this;
        }
    }

    /*  ブロードキャストレシーバー   */
    private BroadcastReceiver NotificationBroadcasts = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String noticeText = intent.getStringExtra(NOTIFICATION_TEXT);
            Toast.makeText(context, noticeText, Toast.LENGTH_LONG).show();

            /*  通知文字列をリストに挿入    */
            insertNoticeList(noticeText);
        }
    };

    /**
     * メール送信のタイマーを設定する
     */
    private void setTimer() {
        /*  インテントを生成    */
        Intent intent = new Intent(getApplicationContext(), MonitorService.class);
        intent.putExtra(ON_RECIEVE, ON_RECIEVE_TIME);
        PendingIntent sender = PendingIntent.getService(getApplicationContext(), 0, intent, 0);

        /*  AlarmManagerを生成    */
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

        /*  確認間隔を設定    */
        long startMillis = System.currentTimeMillis() + interval * MINUTE;
        long windowLengthMillis = 1000*4;

        /*  次回のメール送信タイミングを設定    */
        am.setWindow(AlarmManager.RTC_WAKEUP, startMillis, windowLengthMillis, sender);
    }

    /**
     * メールを送信する
     */
    private void sendMail(){
        StringBuilder sb = new StringBuilder();

        /*  通知文字列の取り出し  */
        for(String noticeText : noticeList){
            sb.append(noticeText + "\n");
        }

        /*  メール送信   */
        //stub

        /*  通知文字列の初期化   */
        noticeList.clear();
    }
}