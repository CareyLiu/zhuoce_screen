package com.zhuoce.screen;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.danikula.videocache.BuildConfig;
import com.danikula.videocache.HttpProxyCacheServer;
import com.google.gson.Gson;
import com.google.zxing.common.StringUtils;
import com.rairmmd.andmqtt.AndMqtt;
import com.rairmmd.andmqtt.MqttConnect;
import com.rairmmd.andmqtt.MqttSubscribe;
import com.sample.Utils;
import com.zhuoce.mqtt.DoMqttValue;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Properties;
import java.util.UUID;


public class MyApp extends Application {
    private HttpProxyCacheServer proxy;
    DoMqttValue doMqttValue;

    @Override
    public void onCreate() {
        super.onCreate();
        KLog.init(BuildConfig.DEBUG, "[VideoCache]:");
        doMqttValue = new DoMqttValue(getApplicationContext());
        setMqttConnect();


    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public static HttpProxyCacheServer getProxy(Context context) {
        MyApp app = (MyApp) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(this)
                .cacheDirectory(Utils.getVideoCacheDir(this))
                .build();
    }

    private String UserId = "icabinet";

    public void setMqttConnect() {


        if (!TextUtils.isEmpty(UserId)) {  //登录再订阅mqtt  如果获取不到 userId 证明没有登录 所有需要优化一下
            if (isAppProcess()) {
                AndMqtt.getInstance().init(this);
                MqttConnect builder = new MqttConnect();
                builder.setClientId(UserId)//连接服务器
                        .setPort(9096)
//                        .setPort(9093)
                        .setAutoReconnect(true)
                        .setCleanSession(true)
                        .setKeepAlive(60)
                        .setCleanSession(true)
                        .setLastWill("K.", "wit/server/01/" + UserId, 2, true)
                        .setUserName("witandroid")
                        .setUserPassword("aG678om34buysdi")
                        .setServer(getMqttUrl()).setTimeout(1);

                builder.setMessageListener(new MqttCallbackExtended() {
                    @Override
                    public void connectComplete(boolean reconnect, String serverURI) {
                        Log.i("Rair", "(MainActivity.java:29)-connectComplete:-&gt;连接完成");
                    }

                    @Override
                    public void connectionLost(Throwable cause) {
                        Log.i("Rair", "(MainActivity.java:34)-connectionLost:-&gt;连接丢失" + cause.getMessage().toString());
                        //UIHelper.ToastMessage(context, "网络不稳定持续连接中", Toast.LENGTH_SHORT);

                    }

                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {

                        doMqttValue.doValue(getApplicationContext(), topic, message.toString());
                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {
                        try {
                            Log.i("Rair", "消息送达完成： " + token.getMessage().toString());
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }

                    }
                });

                AndMqtt.getInstance().connect(builder, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.i("Rair", "(MainActivity.java:51)-onSuccess:-&gt;连接成功");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.i("Rair", "(MainActivity.java:56)-onFailure:-&gt;连接失败" + exception.getMessage() + "..." + exception.getLocalizedMessage());
                        //System.out.println("exception.getMessage" + exception.getMessage());
                    }
                });
            }
        }
    }

    public String getMqttUrl() {
        if (Urls.SERVER_URL.equals("https://shop.hljsdkj.com/")) {
            return "tcp://mqtt.hljsdkj.com";
        } else {
            return "tcp://mqrn.hljsdkj.com";
        }

    }

    public void sendRx(int strValue, String strContent) {
        Notice n = new Notice();
        n.type = strValue;
//                            n.content = message.toString();
        n.content = strContent;
        RxBus.getDefault().sendRx(n);
    }


    /**
     * 获取App安装包信息
     *
     * @return
     */
    public PackageInfo getPackageInfo() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if (info == null) info = new PackageInfo();
        return info;
    }

    /**
     * 获取App唯一标识
     *
     * @return
     */
    public String getAppId() {
        String uniqueID = getProperty(AppConfig.CONF_APP_UNIQUEID);
        if (TextUtils.isEmpty(uniqueID)) {
            uniqueID = UUID.randomUUID().toString();
            setProperty(AppConfig.CONF_APP_UNIQUEID, uniqueID);
        }
        return uniqueID;
    }


    public void setProperties(Properties ps) {
        AppConfig.getAppConfig(this).set(ps);
    }

    public Properties getProperties() {
        return AppConfig.getAppConfig(this).get();
    }

    public void setProperty(String key, String value) {
        AppConfig.getAppConfig(this).set(key, value);
    }

    public String getProperty(String key) {
        return AppConfig.getAppConfig(this).get(key);
    }

    /**
     * 判断该进程是否是app进程
     *
     * @return
     */


    public boolean isAppProcess() {
        JinChengUtils jinChengUtils = new JinChengUtils(getApplicationContext());

        String processName = jinChengUtils.getProcessName();
        if (processName == null || !processName.equalsIgnoreCase(this.getPackageName())) {
            return false;
        } else {
            return true;
        }
    }


}
