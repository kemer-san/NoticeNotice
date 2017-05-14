package com.ametice.noticenotice.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.AsyncTask;
import android.widget.Toast;

import com.ametice.noticenotice.data.NoticeSaveData;
import com.ametice.noticenotice.R;
import com.ametice.noticenotice.mail.MailSender;

/**
 * Created by Masato on 2015/09/26.
 */
public class MessageActivity extends Activity {

    // 画面モード
    public enum Mode {
        Confirm,
        Complete,
    }

    // 画面モード
    public Mode _Mode;

    /*  コンポーネントのハンドル    */
    private TextView msgText1;
    private TextView msgText2;
    private Button btn;
    private Button bkbtn;

    /*  インテント   */
    Intent intent;

    Context context;

    /**
     * コンポーネントのハンドラを取得します。
     */
    private void findComponents() {
        msgText1 = (TextView) findViewById(R.id.msg_text1);
        msgText2 = (TextView) findViewById(R.id.msg_text2);
        btn = (Button) findViewById(R.id.btnMessage);
        bkbtn = (Button) findViewById(R.id.btnBkMessage);
    }

    /**
     * 戻るボタンを無効にする。
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction()==KeyEvent.ACTION_DOWN) {
            if(event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                return false;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        // メール送信用
        //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        //StrictMode.setThreadPolicy(policy);

        // レイアウトを設定
        setContentView(R.layout.message);

        // コンポーネントのハンドラを取得
        findComponents();

        // インテントを取得
        intent = getIntent();

        /* 前画面のクラス名を取得   */
        String className = intent.getStringExtra("Mode");

        if(className.equals(InputAddressActivity.class.getName())){
            _Mode = Mode.Confirm;
        }else{
            _Mode = Mode.Complete;
        }

        /*  アドレス入力画面からの遷移   */
        if (_Mode == Mode.Confirm) {
            onConfirmMode();
        }
        /*  認証画面からの遷移   */
        else {
            onCompleteMode();
        }
    }

    /**
     * 送信確認モードの起動
     */
    private void onConfirmMode() {

        /* 前画面で入力されたメールアドレスを取得   */
        final String Address = intent.getStringExtra("EmailAddress");

        /*  表示用ラベルをセット  */
        msgText1.setText(Address + getString(R.string.message_sendto_1));
        msgText2.setText(getString(R.string.message_sendto_2));
        btn.setText(getString(R.string.button_send));
        bkbtn.setText(getString(R.string.button_back));

        /*  戻るボタンを押下時のアクション */
        bkbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            // メールアドレス入力画面へ戻る
            finish();
            }
        });

        /*  送信ボタン押下時のアクション  */
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* パスコード生成  */
                String passCode = createPassCode();

                //stub SendMail
                Log.d("JavaMail", Address);
                Log.d("passCode", passCode);

                /*  端末内部へメールアドレスとパスコードを保存    */
                NoticeSaveData nsd = new NoticeSaveData(context);
                nsd.saveUserAddress(Address);
                nsd.saveUserPassCode(passCode);

                // 非同期通信
                Uri.Builder builder = new Uri.Builder();
                AsyncHttpRequest task = new AsyncHttpRequest();
                task.execute(builder);

                // パスコード入力画面へ遷移
                Intent intent = new Intent(getApplicationContext(), InputPassCodeActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //新規Activityを呼び出すときにやってもスタックはクリアされない
                startActivity(intent);

            }
        });
    }

    /**
     * 認証完了モードの起動
     */
    private void onCompleteMode() {

        /*  端末内部からメールアドレスを取得    */
        NoticeSaveData nsd = new NoticeSaveData(context);
        String Address = nsd.loadUserAddress();

            /*  メッセージを整形    */
        String message1 = getString(R.string.message_authentication_success);
        String message2 = Address;

        /*  表示用ラベルをセット  */
        msgText1.setText(message1);
        msgText2.setText(message2);
        btn.setText(getString(R.string.button_complete));
        // 認証完了モードの場合、戻るボタンは隠す
        bkbtn.setVisibility(View.INVISIBLE);

        /*  完了ボタン押下時のアクション  */
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 初期画面へ遷移
                Intent intent = new Intent(getApplicationContext(), InitSettingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    /**
     * 4ケタのパスコード生成
     * @return code パスコード
     */
    private String createPassCode(){

        return String.format("%04d", (int) (Math.random() * 10000));
    }

    // ------------------------------
    // 非同期通信
    // ------------------------------
    public class AsyncHttpRequest extends AsyncTask<Uri.Builder, Void, String> {
        @SuppressLint("UnlocalizedSms") @Override
        protected String doInBackground(Builder... params) {
            try {

                /*  端末内部からメールアドレスとパスコードを取得    */
                NoticeSaveData nsd = new NoticeSaveData(context);
                String Address = nsd.loadUserAddress();
                String Subject = getString(R.string.mail_subject_address_setting);
                String MailText = getString(R.string.mail_body_address_setting);
                String passCode = nsd.loadUserPassCode();

                StringBuilder sbBodyText = new StringBuilder();
                sbBodyText.append(MailText);
                sbBodyText.append(passCode);

                // メール送信
                MailSender ms = new MailSender(getApplicationContext());
                ms.send(Address, Subject.toString(), sbBodyText.toString());
            } catch (Exception e) {
                return e.toString();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // トーストを表示
            Toast.makeText(getBaseContext(), getString(R.string.toast_message_passcode_mail_sent), Toast.LENGTH_SHORT).show();
        }
    }

}
