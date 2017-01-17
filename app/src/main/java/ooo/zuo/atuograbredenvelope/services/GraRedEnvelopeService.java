package ooo.zuo.atuograbredenvelope.services;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.PendingIntent;
import android.os.Parcelable;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

import static android.view.accessibility.AccessibilityEvent.*;

public class GraRedEnvelopeService extends AccessibilityService {
    private static final String TAG = "RedEnvelope";
    private long contentEventTime = 0;


    private boolean isBackToFront = false;

    public GraRedEnvelopeService() {
    }

    /**
     * 1、 在聊天页面收到新消息，先content_changed,再scrolled,两个EventTime在log上相差2.推理time在10以内就可以认为是来新消息了
     * 在手动滚动聊天信息时，也会发生先content_changed再scrolled的顺序，但是两个eventTime间隔略大，在100以上。 but
     * 消息往上滚动距离较远时，是不会自动滚动显示新消息的。因此不会触发这个事件检测。
     *
     * 以上当我没说，实践失败，eventTime间隔不确定.....
     *
     *
     * 2、当处在非聊天页面，微信会显示通知，逻辑与微信处在后台处理逻辑一致。 3、通过微信通知进入聊天页面打开红包的逻辑： 获取微信通知中的内容，检查内容中是否包含“[微信红包]“，当然如果消息中包含这个文字也会触发233333，
     * 打开通知，依次触发以下事件 TYPE_WINDOW_STATE_CHANGED， （TYPE_WINDOW_CONTENT_CHANGED 微信重启会发生）
     * TYPE_VIEW_FOCUSED， TYPE_VIEW_SCROLLED， TYPE_WINDOW_CONTENT_CHANGED
     * 这个时候聊天消息页面打开了，默认是显示最新的消息，可以进行红包检测了。
     */
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int type = event.getEventType();
//        Log.d(TAG, event.toString());

        switch (type) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED: {
                handleNotification(event);
            }
            break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED: {
                contentEventTime = event.getEventTime();
                int contentChangeTypes = event.getContentChangeTypes();
                switch (contentChangeTypes){
                    case CONTENT_CHANGE_TYPE_UNDEFINED:{
                        Log.d(TAG, "contentChangeTypes: UNDEFINED");
                    }
                    break;
                    case CONTENT_CHANGE_TYPE_TEXT:{
                        Log.d(TAG, "contentChangeTypes: TEXT");
                    }
                    break;
                    case CONTENT_CHANGE_TYPE_SUBTREE:{
                        Log.d(TAG, "contentChangeTypes: SUBTREE");
                    }
                    break;
                    case CONTENT_CHANGE_TYPE_CONTENT_DESCRIPTION:{
                        Log.d(TAG, "contentChangeTypes: DESCRIPTION");
                    }
                    break;
                }
//                AccessibilityNodeInfo source = event.getSource();
//                String s = source.getText().toString();
//                Log.d(TAG, "onAccessibilityEvent: "+s);
                if (isBackToFront) {
                    openLuckyMoney();
                }
            }
            break;
            case AccessibilityEvent.TYPE_VIEW_SCROLLED: {

//                if (contentEventTime == 0) {
//                    return;
//                }
//                long l = event.getEventTime() - contentEventTime;
//                if (l < 10) {
//                    Log.d(TAG, "onAccessibilityEvent: <10");
//                    openLuckyMoney();
//                }

            }
            break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED: {
                String className = event.getClassName().toString();
                if (className.equals("com.tencent.mm.ui.LauncherUI")) {
                    // 2016/11/3 003 微信刚启动或者后台转前台会收到这个消息
//                    openLuckyMoney();
                } else if (className.contains("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI")) {
                    openRedEnvelope(event);
                } else if (className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI")) {
                    Log.d(TAG, "onAccessibilityEvent: LuckyMoneyDetailUI");
                    performGlobalAction(GLOBAL_ACTION_BACK);
                    performGlobalAction(GLOBAL_ACTION_HOME);
                }
                break;
            }
        }

    }

    /**
     * 点击 开
     */
    private void openRedEnvelope(AccessibilityEvent event) {
        AccessibilityNodeInfo source = event.getSource();
        List<AccessibilityNodeInfo> infos = source.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/be_");
        for (AccessibilityNodeInfo info : infos) {
            info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }


    }


    /**
     * 检查最新的消息是否是红包并打开
     */
    private void openLuckyMoney() {

        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode == null) {
            Log.d(TAG, "openLuckyMoney: node is null");
            return;
        }

        for (int i = 0; i < rootNode.getChildCount(); i++) {
            AccessibilityNodeInfo child = rootNode.getChild(i);

            //拿到window的内容区域，剩下的就是状态栏和导航栏了。
            if (child.getClassName().toString().equals("android.widget.FrameLayout")) {
                //查找聊天消息ListView
                List<AccessibilityNodeInfo> nodes = child.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/a1d");
                child.recycle();

                if (nodes != null && nodes.size() > 0) {
                    //获取ListView，也就一个ListView
                    AccessibilityNodeInfo listView = nodes.get(0);

                    if (listView != null) {
                        int childCount = listView.getChildCount();
                        //获取最新的一条信息
                        AccessibilityNodeInfo message = listView.getChild(childCount - 1);
                        //检查最新消息是否是红包
                        if (hasLuckyMoney(message)) {
                            //是红包获取可点击的容器
                            List<AccessibilityNodeInfo> money = message.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/a48");
                            message.recycle();
                            //进行点击
                            if (money != null && money.size() > 0) {
                                for (int j = 0; j < money.size(); j++) {
                                    AccessibilityNodeInfo info = money.get(j);
                                    if (info != null) {
                                        info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                        isBackToFront = false;
                                        info.recycle();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        rootNode.recycle();
    }


    /**
     * 检查是否包含“微信红包”
     */
    private boolean hasLuckyMoney(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) {
            return false;
        }
        //左下角“微信红包”的TextView
        List<AccessibilityNodeInfo> infos = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/a57");
        if (infos != null && infos.size() > 0) {
            //一般也就找到一个TextView
            for (int i = 0; i < infos.size(); i++) {
                //判断当前TextView是不是红包，因为群收款也是用的该TextView
                AccessibilityNodeInfo info = infos.get(0);
                if (info != null && info.getText().toString().contains("微信红包")) {
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public void onInterrupt() {

    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
    }


    private void handleNotification(AccessibilityEvent event) {
        List<CharSequence> texts = event.getText();
        if (texts == null || texts.isEmpty()) {
            return;
        }
        for (CharSequence text : texts) {
            Log.d(TAG, "handleNotification: " + text);
            if (text.toString().contains("[微信红包]")) {
                Parcelable parcelableData = event.getParcelableData();
                if (parcelableData != null && parcelableData instanceof Notification) {
                    Notification notification = (Notification) parcelableData;
                    PendingIntent contentIntent = notification.contentIntent;
                    try {
                        contentIntent.send();
                        isBackToFront = true;
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
