package com.ametice.noticenotice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;

/**
 * Created by Masato on 2015/11/21.
 */
public class InputPassCodeActivity extends Activity {

    /**
     * コンポーネント
     */
    private EditText txtPassCode;
    private Button btnBackToSetting;
    private Button btnSendMessage;

    /**
     * コンポーネントのハンドラを取得する
     */
    private void findComponents() {

        txtPassCode = (EditText) findViewById(R.id.txtPasscode);
        btnBackToSetting = (Button) findViewById(R.id.btnBackToSetting);
        btnSendMessage = (Button) findViewById(R.id.btnSendMessage);
    }

    /**
     * 初期化処理
     */
    private void initialize() {

        // コンポーネントのハンドラを取得
        findComponents();

        // イベントリスナの設定
        btnSendMessage.setOnClickListener(onSendMessageClick);
        btnBackToSetting.setOnClickListener(onBackToSettingeClick);

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

    /**
     * 認証ボタンクリックリスナ
     */
    private View.OnClickListener onSendMessageClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            // 入力されたパスコードを取得
            String inputPassCode = txtPassCode.getText().toString();

            // 入力されたパスコードと内部に保存されているパスコード
            // を比較し、結果に応じた処理を行う
            if (compareInternalPassCodeWith(inputPassCode)) {
                // 一致したときの処理
                // メッセージ画面の起動
                Intent intent = new Intent(getApplicationContext(), MessageActivity.class);

                // Modeに自画面のクラス名を設定
                intent.putExtra("Mode", InputPassCodeActivity.class.getName());

                /* 端末内部に「認証済み」のフラグを設定 */
                new NoticeSaveData(getApplicationContext()).saveUserRegistration(true);

                // 画面の呼び出し
                startActivity(intent);
            } else {
                // 不一致の時の処理

                // トーストを表示
                Toast
                        .makeText(
                                InputPassCodeActivity.this,
                                "送信されたパスコードと一致しません。",
                                Toast.LENGTH_SHORT)
                        .show();
            }
        }
    };

    /**
     * 初期画面戻りボタンクリックリスナ
     */
    private View.OnClickListener onBackToSettingeClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // 初期画面へ遷移
            Intent intent = new Intent(getApplicationContext(), InitSettingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    };

    /**
     * onCreateメソッド
     *
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //レイアウトファイルを指定
        setContentView(R.layout.input_passcode);

        // 初期化処理
        initialize();
    }

    /**
     * 内部に保存されているパスコードと入力されたパスコードを比較する
     *
     * @param input 比較を行うパスコード
     * @return 比較して正しければtrue
     */
    private boolean compareInternalPassCodeWith(String input) {

        // 内部データを取り出す
        NoticeSaveData nsd = new NoticeSaveData(this.getApplicationContext());

        String internalPassCode = nsd.loadUserPassCode();

        // for GC
        nsd = null;

        // 比較結果を返却する
        return internalPassCode.equals(input);
    }
}