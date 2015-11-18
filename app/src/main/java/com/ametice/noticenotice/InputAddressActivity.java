package com.ametice.noticenotice;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;

/**
 * Created by Masato on 2015/09/26.
 */
public class InputAddressActivity extends Activity {

    // region "GUI部品"

    Button btnSendMessage;
    EditText txtEmailAddress;

    /**
     * コンポーネントのハンドラを取得します。
     */
    private void findComponents() {

        btnSendMessage = (Button) findViewById(R.id.btnSendMessage);
        txtEmailAddress = (EditText) findViewById(R.id.txtEmailAddress);
    }

    // endregion

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // レイアウトをセット
        setContentView(R.layout.input_address);

        //　コンポーネントのハンドラを取得
        findComponents();

        //次へボタン押下時のアクション
        btnSendMessage.setOnClickListener(btnSendMessageOnClickListener);
    }

    private final View.OnClickListener btnSendMessageOnClickListener = new View.OnClickListener() {
        // 次へボタン押下時のアクション
        @Override
        public void onClick(View v) {

            // 入力されたメールアドレスの取得
            String inputAddress = txtEmailAddress.getText().toString();

            // メールアドレスの入力チェック・エラーメッセージの表示
            if (!isValidEmailAddress(inputAddress)) {
                // 入力されたメールアドレスが不正だった場合

                // エラーメッセージの表示
                showErrorAlertDialog();

                // イベント処理を抜ける
                return;
            }


            // メッセージ画面の起動
            Intent intent = new Intent(getApplicationContext(), MessageActivity.class);

            intent.putExtra("EmailAddress","notice@notice.com");

            // 画面の呼び出し
            startActivity(intent);
        }
    };


    /**
     * メールアドレスの入力チェックを行います。
     *
     * @param target チェックするアドレスの文字列
     * @return 正しいメールアドレスならtrue
     */
    private boolean isValidEmailAddress(String target) {
        // TODO 既存のバリデータがあれば、それを使う

        return true;
    }

    /**
     * エラーメッセージを表示します。
     */
    private void showErrorAlertDialog() {

        AlertDialog.Builder errorDialog = new AlertDialog.Builder(this);

        errorDialog
                .setTitle("入力エラー")
                .setMessage("正しいメールアドレスを入力してください。")
                .setPositiveButton("OK", null)
                .show();

        // TextEditにフォーカス(できてる？)
        txtEmailAddress.requestFocus();
    }
}

