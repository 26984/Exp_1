package com.example.demo3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private CheckBox cbRemember;
    private Button btnLogin, btnRegister;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 绑定控件
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        cbRemember = findViewById(R.id.cb_remember);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);

        // SharedPreferences
        prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);

        // 加载记住的账号
        loadSavedInfo();

        // 登录按钮
        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
                return;
            }
            // ========== 添加用户协议验证 ==========
            CheckBox cbAgree = findViewById(R.id.cb_agree);
            if (cbAgree == null) {
                // 如果布局中没有这个控件，先不验证
                Toast.makeText(this, "布局中未找到用户协议控件", Toast.LENGTH_SHORT).show();
            } else if (!cbAgree.isChecked()) {
                Toast.makeText(this, "请阅读并同意《用户协议》", Toast.LENGTH_SHORT).show();
                return;
            }

            // 数据库验证
            DBHelper dbHelper = new DBHelper(this);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM user WHERE username=? AND password=?",
                    new String[]{username, password});

            if (cursor.moveToFirst()) {
                // 登录成功
                saveLoginInfo(username, password, cbRemember.isChecked());
                Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MapActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
            }
            cursor.close();
            db.close();
        });

        // 注册按钮
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
        // 淡入动画
        TextView tvTitle = findViewById(R.id.tv_title);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        tvTitle.startAnimation(fadeIn);
        // 协议文字点击弹窗
        TextView tvAgreement = findViewById(R.id.tv_agreement);
        if (tvAgreement != null) {
            tvAgreement.setOnClickListener(v -> {
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("用户协议")
                        .setMessage("欢迎使用果香寻踪！\n\n一、服务条款\n1. 本App仅供学习交流使用...\n\n二、隐私保护\n1. 用户信息仅保存在本地...\n\n三、免责声明\n1. 采摘园信息仅供参考...")
                        .setPositiveButton("同意", null)
                        .show();
            });
        }
    }

    private void loadSavedInfo() {
        boolean isRemember = prefs.getBoolean("remember", false);
        if (isRemember) {
            String username = prefs.getString("username", "");
            String password = prefs.getString("password", "");
            etUsername.setText(username);
            etPassword.setText(password);
            cbRemember.setChecked(true);
        }
    }

    private void saveLoginInfo(String username, String password, boolean isRemember) {
        SharedPreferences.Editor editor = prefs.edit();
        if (isRemember) {
            editor.putString("username", username);
            editor.putString("password", password);
            editor.putBoolean("remember", true);
        } else {
            editor.remove("username");
            editor.remove("password");
            editor.putBoolean("remember", false);
        }
        editor.apply();
    }
}