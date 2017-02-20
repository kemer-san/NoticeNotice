package com.ametice.noticenotice.service;


import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.ametice.noticenotice.R;
import com.ametice.noticenotice.data.NoticeSaveData;
import com.ametice.noticenotice.google.GmailSender;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.gmail.GmailScopes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

/**
 * クラス名 ：MonitorService
 * 説明    ：通知取得の監視を行うサービス
 * 最終更新 :2017/1/30
 *
 * @author Y.Hiyoshi(ametis)
 * @version 1.3
 */
public class MonitorService extends Service {

	/**
	 * 定数
	 */
	public static final String NOTIFICATION_SERVICE = "NOTIFICATION_SERVICE";   //インテント用
	private final String NOTIFICATION_TEXT = "NOTIFICATION_TEXT";               //ブロードキャスト用
	private final String ON_RECIEVE = "ON_RECIEVE";                             //ブロードキャスト文字列識別用
	private final String ON_RECIEVE_TIME = "ON_RECIEVE_TIME";                   //ブロードキャスト文字列識別用

	/**
	 * フィールド変数宣言
	 */
	private static final int MINUTE = 1000 * 60;                                //分単位（ms）
	private int interval;                                                       //確認間隔
	private ArrayList<String> noticeList = new ArrayList<String>();             //通知文字列リスト
	private final IBinder mBinder = new MonitorServiceLocalBinder();            //バインダー


	/**
	 * onCreate
	 */
	@Override
	public void onCreate() {
		//レシーバー登録
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(NOTIFICATION_SERVICE);
		registerReceiver(notificationBroadcasts, intentFilter);
	}

	/**
	 * startServiceでサービスが開始要求を受けたときのコールバック
	 *
	 * @param intent  インテント
	 * @param flags   フラグ
	 * @param startId 実行ID
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v("onStartCommand", "onStartCommand Received start id " + startId + ": " + intent);

        /*  インテントから文字列を取得   */
		String str = intent.getStringExtra(ON_RECIEVE);

        /*  確認タイミングの識別子が検出された場合   */
		if (str != null && str.equals(ON_RECIEVE_TIME)) {
			Toast.makeText(this, "onStartCommand#" + str, Toast.LENGTH_SHORT).show();

            /*  次の通知時刻を設定   */
			setTimer();

            /*  送信する曜日・時刻範囲の場合   */
			if (isRunDayOfWeek() == true && isRunTime() == true) {
			    /*  通知を取得している場合、非同期通信でメール送信   */
				if (noticeList.isEmpty() == false) {
					// 非同期通信
					Uri.Builder builder = new Uri.Builder();
					AsyncHttpRequest task = new AsyncHttpRequest();
					task.execute(builder);
				}
			}
		}

		return START_STICKY;
	}

	/**
	 * onDestroy
	 */
	@Override
	public void onDestroy() {
	    /*  登録したレシーバを解除する   */
		unregisterReceiver(notificationBroadcasts);
	}

	/**
	 * bindServiceでサービスがバインドされたときのコールバック
	 *
	 * @param intent インテント
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	/**
	 * reBindServiceでサービスが再度バインドされたときのコールバック
	 *
	 * @param intent インテント
	 */
	@Override
	public void onRebind(Intent intent) {
	}

	/**
	 * unBindServiceでサービスがバインドを解除されたときのコールバック
	 *
	 * @param intent インテント
	 */
	@Override
	public boolean onUnbind(Intent intent) {
	    /*  戻り値をtrueに設定 */
		return true;
	}

	/**
	 * 通知サービスの開始時の処理
	 */
	public void onNotice() {
	    /*  デバッグ用   */
		Log.v("MonitorService", "onNotice");

        /*  通知取得サービスのインテントを生成    */
		Intent service = new Intent(MonitorService.this, NotificationService.class);

        /*  通知サービスの開始   */
		startService(service);

        /*  確認間隔の設定 */
		setCheckInterval();

		/** メール送信のタイマーを設定する */
		setTimer();

	}

	/**
	 * 通知サービスの停止時の処理
	 */
	public void offNotice() {
        /*  デバッグ用   */
		Log.v("MonitorService", "offNotice");

        /*  通知取得サービスのインテントを生成    */
		Intent service = new Intent(MonitorService.this, NotificationService.class);

        /*  通知サービスの停止   */
		stopService(service);
	}

	/**
	 * 通知リストへの挿入
	 *
	 * @param text 通知リストへ格納する文字列
	 */
	private void insertNoticeList(String text) {
        /*  引数の文字列が通知リストにない場合は格納   */
		if (!noticeList.contains(text)) {
			noticeList.add(text);
		}
	}

	/**
	 * 確認間隔を設定
	 */
	public void setCheckInterval() {
        /*  ユーザーが設定した確認間隔を取得    */
		interval = Integer.parseInt(new NoticeSaveData(this).loadCheckInterval());
	}

	/**
	 * 通知を監視する曜日であるか判定する
	 *
	 * @return 判定値
	 */
	private boolean isRunDayOfWeek() {
        /*  システムの曜日を取得  */
		Calendar cal = Calendar.getInstance();
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);  //Calenderの仕様で日〜土[1-7]

        /*  Notice上の曜日の数字に変更 */
		dayOfWeek = dayOfWeek - 1;

        /*  今日の曜日が曜日指定設定でTRUEであるか確認    */
		NoticeSaveData nsd = new NoticeSaveData(this);
		if (nsd.loadDayOfWeek(dayOfWeek) == true) return true;

		return false;
	}

	/**
	 * 通知を監視する時刻であるか判定する
	 *
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

	    /*  確認開始時刻より小さい場合   */
		if (0 <= startTime.compareTo(endTime)) {
			endTime.add(Calendar.DATE, 1);
		}

        /*  差分抽出   */
		int diff1 = currentTime.compareTo(startTime);
		int diff2 = currentTime.compareTo(endTime);

        /*  確認開始時刻より大きく、確認終了時刻より小さい場合   */
		if (0 <= diff1 && diff2 <= 0) return true;

		return false;
	}


	/*  ブロードキャストレシーバー   */
	private BroadcastReceiver notificationBroadcasts = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			String noticeText = intent.getStringExtra(NOTIFICATION_TEXT);

            /*  通知文字列をリストに挿入    */
			insertNoticeList(noticeText);

			Log.d("debug_Notice", noticeText);
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
		long windowLengthMillis = 1000 * 4;

        /*  次回のメール送信タイミングを設定    */
		am.setWindow(AlarmManager.RTC_WAKEUP, startMillis, windowLengthMillis, sender);
	}

	// ------------------------------
	// 非同期通信
	// ------------------------------
	public class AsyncHttpRequest extends AsyncTask<Uri.Builder, Void, String> {
		@SuppressLint("UnlocalizedSms")
		@Override
		protected String doInBackground(Uri.Builder... params) {
			try {
				/************************************/
				/** 通知リストから通知文字列の取り出し   */
				/************************************/
                /*  通知文字列の取り出し  */
				StringBuilder sb = new StringBuilder();
				for (String noticeText : noticeList) {
					sb.append(noticeText + "\n");
				}

	            /*  通知文字列の初期化   */
				noticeList.clear();

				/************************************/
				/** メール本文作成                    */
				/************************************/
                /*  メールタイトル    */
				String subject = getString(R.string.mail_subject_notice_send);

                /*  メール本文生成    */
				StringBuilder Mailtext = new StringBuilder();
				String MailtextHeader = getString(R.string.mail_subject_notice_text);
				Mailtext.append(MailtextHeader);
				Mailtext.append("\n\n");
				Mailtext.append(sb.toString());
				Mailtext.append("\n");
				Mailtext.append("以上\n");

				/************************************/
				/** Gmail送信設定                    */
				/************************************/
                /*  送信用Googleインスタンスを生成   */
				GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(MonitorService.this, Arrays.asList(GmailScopes.GMAIL_SEND));

                /*  端末内部からメールアドレスを取得    */
				String toAddress = new NoticeSaveData(getApplicationContext()).loadUserAddress();

				/**  送信元のGmailアカウント名を端末内部の設定ファイルから取得   */
				String gmailAccountName = new NoticeSaveData(getApplicationContext()).loadUserGmailAccount();

                /*  送信用Googleインスタンスに送信元のアカウントを設定  */
				credential.setSelectedAccountName(gmailAccountName);

                /*  Gmail送信インスタンスを生成   */
				GmailSender gs = new GmailSender(credential);

				/** Gmail送信  */
				gs.sendGmail(toAddress, gmailAccountName, subject, Mailtext.toString());

				/** 開発用デバッグ文    */
				Log.d("送信元:", gmailAccountName);
				Log.d("送信先:", toAddress);
				Log.d("メールタイトル:", subject);
				Log.d("メール本文:", Mailtext.toString());

			} catch (Exception e) {
				return e.toString();
			}
			return null;
		}
	}

	/*************************************/
	/** インナークラス                     */
	/*************************************/
	/**
	 * クラス名 MonitorServiceLocalBinder
	 * 説明    ：サービス接続用バインダークラス
	 */
	public class MonitorServiceLocalBinder extends Binder {
		/*  サービスの取得 */
		public MonitorService getService() {
			return MonitorService.this;
		}
	}
}
