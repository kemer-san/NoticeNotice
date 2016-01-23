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

            /*  送信する曜日の場合   */
            if(isRunDayOfWeek() == true){
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