package com.wang17.reloginweixin.activity;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.wang17.reloginweixin.DataContext;
import com.wang17.reloginweixin.R;
import com.wang17.reloginweixin.Setting;
import com.wang17.reloginweixin._Session;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button button_open;
    private EditText editText_pwd;
    private RadioGroup radioGroup_who;
    private Setting pwd;
    private Setting who;

    DataContext context;


    protected void onCreate(Bundle savedInstanceState) {
// TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
        initialize();

        // 单选框 - 投票对象
        radioGroup_who.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int selectId = group.getCheckedRadioButtonId();
                RadioButton radioButton_select = (RadioButton) findViewById(selectId);
                context.editSetting("who", selectId + "");
                _Session.WHO = radioButton_select.getText().toString();
            }
        });

        // 按钮 - 打开“辅助工具”面板
        button_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick(button_open);
            }
        });

        // 文本框 - 密码
        editText_pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                context.editSetting("pwd", _Session.PWD);
                _Session.PWD = editText_pwd.getText().toString().isEmpty() ? "qq351489" : editText_pwd.getText().toString();
            }
        });
    }

    private void initialize() {
        // 上下文 - 数据库
        context = new DataContext(MainActivity.this);
        button_open = (Button) findViewById(R.id.button_open);
        editText_pwd = (EditText) findViewById(R.id.editText_pwd);
        radioGroup_who = (RadioGroup) findViewById(R.id.radioGroup_who);

        pwd = context.getSetting("pwd");
        if (pwd != null) {
            editText_pwd.setText(pwd.getValue());
        } else {
            context.addSetting("pwd", "");
        }

        who = context.getSetting("who");
        RadioButton radioButton = (RadioButton) findViewById(R.id.radioButton_wsh);
        if (who != null) {
            radioButton = (RadioButton) findViewById(Integer.parseInt(who.getValue()));
        } else {
            context.addSetting("who", R.id.radioButton_wsh + "");
        }
        radioButton.setChecked(true);
        _Session.WHO = radioButton.getText().toString();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        onButtonClick(button_open);
    }

    @Override
    protected void onDestroy() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onDestroy();
    }

    public void onButtonClick(View view) {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
    }

    private void updateServiceStatus() {
        boolean ServiceEnabled = false;
        // 循环遍历所有服务，查看是否开启
        AccessibilityManager accessibilityManager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> accessibilityServices = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_SPOKEN);
        for (AccessibilityServiceInfo info : accessibilityServices) {
            if (info.getId().equals(getPackageName() + "/.MyService")) {
                ServiceEnabled = true;
                break;
            }
        }
        if (ServiceEnabled) {
            button_open.setText("关闭测试服务");
            // Prevent screen from dimming
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            button_open.setText("开启测试服务");
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }
}
