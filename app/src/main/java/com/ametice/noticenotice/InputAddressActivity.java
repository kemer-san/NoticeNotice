package com.ametice.noticenotice;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * メールアドレス入力画面
 *
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

    /**
     * メールアドレス入力欄の入力にフィルタをセットする
     */
    private void setInputFilter() {

        if (txtEmailAddress == null) return;

        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                if (source.toString().matches("^[0-9a-zA-Z@¥.¥_¥¥-]+$")) {
                    return source;
                } else {
                    return "";
                }
            }
        };

        txtEmailAddress.setFilters(new InputFilter[]{filter});
    }

    // endregion

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // レイアウトをセット
        setContentView(R.layout.input_address);

        //　コンポーネントのハンドラを取得
        findComponents();

        // 入力フィルタをセット
        setInputFilter();

        // 入力済みのアドレスをセットする
        setExistingAddress();

        //次へボタン押下時のアクション
        btnSendMessage.setOnClickListener(btnSendMessageOnClickListener);
    }

    private final View.OnClickListener btnSendMessageOnClickListener = new View.OnClickListener() {
        // 次へボタン押下時のアクション
        @Override
        public void onClick(View v) {

            // 入力されたメールアドレスの取得
            String inputAddress = txtEmailAddress.getText().toString();

            // 入力が空ならトーストを表示する
            if (inputAddress.length() < 1) {
                Toast
                        .makeText(InputAddressActivity.this, "アドレスが空です", Toast.LENGTH_SHORT)
                        .show();

                // イベント処理を抜ける
                return;
            }

            // メールアドレスの入力チェック・エラーメッセージの表示
            if (!isValidEmailAddress(inputAddress)) {
                // 入力されたメールアドレスが不正だった場合

                // エラーメッセージの表示
                showErrorAlertDialog();

                // イベント処理を抜ける
                return;
            }

            // 登録されているアドレスと比較する
            // 設定クラスを生成する
            NoticeSaveData saveData = new NoticeSaveData(getApplicationContext());

            if (inputAddress.equals(saveData.loadUserAddress())) {
                // 内部データと同じ場合

//                Intent intent = new Intent(getApplicationContext(),InputPasscodeActivity.class);
//                startActivity(intent);

            } else {
                // 内部データと異なっている場合

                // メッセージ画面の起動
                Intent intent = new Intent(getApplicationContext(), MessageActivity.class);

                intent.putExtra("EmailAddress", inputAddress);
                intent.putExtra("Mode", this.getClass().getName());

                // 画面の呼び出し
                startActivity(intent);
            }
        }
    };

    /**
     * すでにアドレスが登録されている場合、画面にセットする
     */
    private void setExistingAddress() {

        NoticeSaveData saveData = new NoticeSaveData(getApplicationContext());

        txtEmailAddress.setText(saveData.loadUserAddress());
    }

    /**
     * メールアドレスの入力チェックを行います。
     *
     * @param target チェックするアドレスの文字列
     * @return 正しいメールアドレスならtrue
     */
    private boolean isValidEmailAddress(String target) {
        // TODO 既存のバリデータがあれば、それを使う

        String EmailRegEx = "^[a-zA-Z0-9!#$%&'_`/=~\\*\\+\\-\\?\\^\\{\\|\\}]+(\\.[a-zA-Z0-9!#$%&'_`/=~\\*\\+\\-\\?\\^\\{\\|\\}]+)*+(.*)@[a-zA-Z0-9][a-zA-Z0-9\\-]*(\\.[a-zA-Z0-9\\-]+)+$";
        return target.matches(EmailRegEx);
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

