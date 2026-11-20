package com.example.demo3;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.database.sqlite.SQLiteDatabase;


public class RegisterActivity extends AppCompatActivity {
    private EditText etUsername, etPassword, etConfirmPassword;
    private Button btnRegister, btnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // 绑定控件
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
        btnLogin = findViewById(R.id.btn_login);

        // 在注册按钮的点击事件中
        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPwd = etConfirmPassword.getText().toString().trim();

            if (username.isEmpty()) {
                Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.isEmpty()) {
                Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(confirmPwd)) {
                Toast.makeText(this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
                return;
            }

// 存入数据库
            DBHelper dbHelper = new DBHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            try {
                db.execSQL("INSERT INTO user (username, password) VALUES (?, ?)",
                        new String[]{username, password});
                Toast.makeText(this, "注册成功！请登录", Toast.LENGTH_SHORT).show();
                finish();
            } catch (Exception e) {
                Toast.makeText(this, "用户名已存在", Toast.LENGTH_SHORT).show();
            }
            db.close();
        });


        // 返回登录按钮
        btnLogin.setOnClickListener(v -> finish());
    }
}