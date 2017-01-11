package ooo.zuo.atuograbredenvelope.services;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.PendingIntent;
import android.os.Parcelable;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class GraRedEnvelopeService extends AccessibilityService {
    private static final String TAG = "RedEnvelope";

    private boolean[] clickable = new boolean[]{true,true,true};

    public GraRedEnvelopeService() {
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int type = event.getEventType();
        Log.d(TAG, event.toString());

        switch (type) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED: {
                handleNotification(event);
            }
            break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:{
                openLuckyMoney(event);
            }
            break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED: {
                String className = event.getClassName().toString();
                if (className.equals("com.tencent.mm.ui.LauncherUI")) {
                    // 2016/11/3 003 微信刚启动或者后台转前台会收到这个消息
                    openLuckyMoney(event);
                } else if (className.contains("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI")) {
                    openRedEnvelope(event);
                } else if (className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI")) {
                    clickable[0] = false;
                }
                break;
            }
        }

    }

    /**
     * 点击打开
     * @param event
     */
    private void openRedEnvelope(AccessibilityEvent event) {
        AccessibilityNodeInfo source = event.getSource();
        List<AccessibilityNodeInfo> infos = source.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/be_");
        Log.d(TAG, "openRedEnvelope: source infos size:"+infos.size());
        for (AccessibilityNodeInfo info : infos) {
            info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }


    }

//    private void getOpenButtonList(AccessibilityNodeInfo node) {
//        if (node == null) {
//            return ;
//        }
//        if (node.getClassName().equals("android.widget.Button")) {
//            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//            return ;
//        }
//        int childCount = node.getChildCount();
//        for (int i = 0; i < childCount; i++) {
//            AccessibilityNodeInfo child = node.getChild(i);
//            if (child!=null){
//                getOpenButtonList(child);
//            }
//        }
//
//    }


    private void openLuckyMoney(AccessibilityEvent event) {

        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode == null) {
            Log.d(TAG, "openLuckyMoney: node is null");
            return;
        }

        Log.d(TAG, "openLuckyMoney: rootNode->"+rootNode);
        for (int i = 0; i < rootNode.getChildCount(); i++) {
            AccessibilityNodeInfo child = rootNode.getChild(i);
            if (child.getClassName().toString().equals("android.widget.FrameLayout")){
                Log.d(TAG, "openLuckyMoney: frameLayout:"+i);
                for (int j = 0; j < child.getChildCount(); j++) {
                    AccessibilityNodeInfo nodeInfo = child.getChild(j);
                    AccessibilityNodeInfo luckyMoney = findLuckyMoney(nodeInfo);
                    if (luckyMoney!=null){
                        Log.d(TAG, "openLuckyMoney: LinearLayout:"+j);
                    }
                }




            }

        }


//        //聊天列表
//        AccessibilityNodeInfo listView = findListView(rootNode);
//        if (listView!=null) {
//            int childCount = listView.getChildCount();
//            //最新一条信息
//            AccessibilityNodeInfo child = listView.getChild(childCount - 1);
//            //找红包
//            AccessibilityNodeInfo luckyMoney = findLuckyMoney(child);
//
//            if (luckyMoney == null) {
//                //没有红包结束
//                return;
//            }
//            boolean performAction = luckyMoney.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//            Log.i(TAG, "openLuckyMoney: 我点" + performAction);
//            AccessibilityNodeInfo parent = luckyMoney.getParent();
//            while (parent != null&&clickable[0]) {
//                parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                parent = parent.getParent();
//            }
//            clickable[0] =true;
//            if (parent!=null){
//                parent.recycle();
//            }
//            listView.recycle();
//            child.recycle();
//            luckyMoney.recycle();
//            rootNode.recycle();
//
//        }

    }


    private AccessibilityNodeInfo findListView(AccessibilityNodeInfo nodeInfo){

        if (nodeInfo.getClassName().toString().equals("android.widget.ListView")) {
            return nodeInfo;
        }else {
            int childCount = nodeInfo.getChildCount();
            for (int i = 0; i < childCount; i++) {
                AccessibilityNodeInfo view = findListView(nodeInfo.getChild(i));
                if (view!=null&&view.getClassName().toString().equals("android.widget.ListView")){
                    return view;
                }
            }
        }
        return null;
    }

    private AccessibilityNodeInfo findLuckyMoney(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) {
            return null;
        }
        int childCount = nodeInfo.getChildCount();
        if (childCount == 0) {
            CharSequence text = nodeInfo.getText();
            if (text != null && text.toString().contains("领取红包")) {
                Log.i(TAG, "findLuckyMoney: 找到");
                return nodeInfo;
            }
        } else {
            for (int i = nodeInfo.getChildCount() - 1; i >= 0; i--) {
                AccessibilityNodeInfo child = nodeInfo.getChild(i);
                if (child != null) {
                    AccessibilityNodeInfo info = findLuckyMoney(child);
                    if (info != null) {
                        return info;
                    }
                }
            }
        }
        return null;
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
                        clickable[0] = true;
                        clickable[1] = true;
                        clickable[2] = true;
                        contentIntent.send();
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
