package com.wang17.reloginweixin;

import android.accessibilityservice.AccessibilityService;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;

public class MyService extends AccessibilityService {

    //    String pwd = "qq351489";
    String summary = "武城乐视";
    private int clickTag = 0;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
/*        Log.i("wang","onServiceConnected");
      AccessibilityServiceInfo info = new AccessibilityServiceInfo();
      info.packageNames = installPackge ; //监听过滤的包名
      info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK; //监听哪些行为
      info.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN; //反馈
      info.notificationTimeout = 100; //通知的时间
      setServiceInfo(info);*/
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();//事件类型
        String className = event.getClassName().toString();
        Log.i("wang",className);
        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
//                switchClassName(event);
                switchClickTag();
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
//                switchClassName(event);
                switchClickTag();
                break;
        }
    }

    private void switchClassName(AccessibilityEvent event) {
        String className = event.getClassName().toString();
        if (className.equals("com.tencent.mm.plugin.setting.ui.setting.SettingsUI")) {
            clickTag = 2;
        } else if (className.equals("com.tencent.mm.ui.base.h")) {
            clickTag = 3;
        } else if (className.equals("com.tencent.mm.ui.account.LoginHistoryUI")) {
            clickTag = 5;
        } else if (className.equals("com.tencent.smtt.webkit.WebView")) {
            clickTag = 9;
        }
    }

    private void switchClickTag() {
        switch (clickTag) {
            case 0:
                clickTag += clickBtn("设置") ? 1 : 0;
                break;
            case 1:
                clickTag += clickBtn("退出") ? 1 : 0;
                break;
            case 2:
                clickTag += clickBtn("退出当前帐号") ? 1 : 0;
                break;
            case 3:
                clickTag += clickBtn("退出") ? 1 : 0;
                break;
            case 4:
                clickTag += clickBtn("密码登录") ? 1 : 0;
                break;
            case 5:
                clickTag += clipPassword(_Session.PWD) ? 1 : 0;
                break;
            case 6:
                clickTag += clickChat(summary) ? 1 : 0;
                break;
            case 7:
                clickTag += clickBtnContain(_Session.WHO + summary) ? 1 : 0;
                break;
            case 8:
                clickTag += haveComeInWebView() ? 1 : 0;
                break;
            case 9:
                clickTag += backToMe() ? 1 : 0;
                break;
            case 10:
                clickTag = clickBtn("我") ? 0 : clickTag;
                break;
        }
    }

    private void clickBack(AccessibilityEvent event) {

    }

    private boolean haveComeInWebView() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            listTotal.clear();
            recursionNode(nodeInfo);
            for (AccessibilityNodeInfo node : listTotal) {
                if (node.getClassName().toString().equals("com.tencent.smtt.webkit.WebView")) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean backToMe() {
        boolean haveWebView = false;
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        AccessibilityNodeInfo backBtn = null;
        if (nodeInfo != null) {
            listTotal.clear();
            recursionNode(nodeInfo);
            try {
                for (AccessibilityNodeInfo node : listTotal) {
                    if (node.getContentDescription() != null && node.getContentDescription().toString().equals("返回")) {
                        backBtn = node;
                    }
                    if (node.getClassName().toString().equals("com.tencent.smtt.webkit.WebView")) {
                        haveWebView = true;
                    }
                }
                if (!haveWebView) {
                    if (backBtn != null)
                        return clickView(backBtn);
                }
            } catch (Exception e) {
                String msg = e.getMessage();
                msg = msg;
            }
        }
        return false;
    }

    private boolean clipPassword(String password) {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            listTotal.clear();
            recursionNode(nodeInfo);
            AccessibilityNodeInfo passwrodNode = null, loginNode = null;
            for (AccessibilityNodeInfo node : listTotal) {
                if (node.getClassName().toString().equals("android.widget.EditText")) {
                    passwrodNode = node;
                } else if (node.getText() != null && "登录".equals(node.getText().toString())) {
                    loginNode = node;
                }
            }
            if (passwrodNode != null & loginNode != null) {
                //android>21 = 5.0时可以用ACTION_SET_TEXT
                // android>18 3.0.1可以通过复制的手段,先确定焦点，再粘贴ACTION_PASTE
                // 使用剪切板
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("text", password);
                clipboard.setPrimaryClip(clip);
                //焦点    （n是AccessibilityNodeInfo对象）
                passwrodNode.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                //粘贴进入内容
                passwrodNode.performAction(AccessibilityNodeInfo.ACTION_PASTE);

                // 点击登录
                return clickView(loginNode);
            }
        }
        return false;
    }

    private boolean clickView(AccessibilityNodeInfo node) {
        if (node.isClickable()) {
            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            return true;
        } else {
            AccessibilityNodeInfo parent = node.getParent();
            while (parent != null) {
                if (parent.isClickable()) {
                    parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    return true;
                }
                parent = parent.getParent();
            }
        }
        return false;
    }

    /**
     * 查找到
     */
    List<AccessibilityNodeInfo> listTotal = new ArrayList<AccessibilityNodeInfo>();

    private boolean clickChat(String contentDescription) {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            listTotal.clear();
            recursionNode(nodeInfo);
            for (AccessibilityNodeInfo node : listTotal) {
                if (node.getContentDescription() != null && node.getContentDescription().toString().contains(contentDescription)) {
                    return clickView(node);
                }
            }
        }
        return false;
    }

    private boolean clickBtn(String btnText) {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            listTotal.clear();
            recursionNode(nodeInfo);
            for (AccessibilityNodeInfo node : listTotal) {
                if (node.getText() != null && node.getText().toString().equals(btnText)) {
                    return clickView(node);
                }
            }
        }
        return false;
    }

    private boolean clickBtnContain(String btnText) {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            listTotal.clear();
            recursionNode(nodeInfo);
            for (AccessibilityNodeInfo node : listTotal) {
                if (node.getText() != null && node.getText().toString().contains(btnText)) {
                    return clickView(node);
                }
            }
        }
        return false;
    }

    private void clickBtn2(String btnText) {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        recursionNodeInfo(rootNode, btnText);
    }

    public boolean recursionNode(AccessibilityNodeInfo info) {
        if (info.getChildCount() == 0) {
            listTotal.add(info);
        } else {
            for (int i = 0; i < info.getChildCount(); i++) {
                if (info.getChild(i) != null) {
                    recursionNode(info.getChild(i));
                }
            }
        }
        return false;
    }


    /**
     * 打印一个节点的结构
     *
     * @param info
     */
    public void recursionNodeInfo(AccessibilityNodeInfo info, String btnText) {
        if (info.getChildCount() == 0) {
            if (info.getText() != null) {
                String text = info.getText().toString();
                if (btnText.equals(info.getText().toString())) {
                    //这里有一个问题需要注意，就是需要找到一个可以点击的View
                    if (info.isClickable()) {
                        info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        clickTag++;
                    } else {
                        AccessibilityNodeInfo parent = info.getParent();
                        while (parent != null) {
                            if (parent.isClickable()) {
                                parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                clickTag++;
                            }
                            parent = parent.getParent();
                        }
                    }

                }
            }

        } else {
            for (int i = 0; i < info.getChildCount(); i++) {
                if (info.getChild(i) != null) {
                    recursionNodeInfo(info.getChild(i), btnText);
                }
            }
        }
    }


    @Override
    public void onInterrupt() {
    }


}
