package com.ametice.noticenotice;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

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

            /*  メッセージを整形    */
        String message1 = Address + "へ送信します。";
        String message2 = "よろしいですか？";

        /*  表示用ラベルをセット  */
        msgText1.setText(message1);
        msgText2.setText(message2);
        btn.setText("送信");

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

                // パスコード入力画面へ遷移
                Intent intent = new Intent(getApplicationContext(), InputPassCodeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
        String message1 = "認証が成功しました。";
        String message2 = Address;

        /*  表示用ラベルをセット  */
        msgText1.setText(message1);
        msgText2.setText(message2);
        btn.setText("完了");

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
}
