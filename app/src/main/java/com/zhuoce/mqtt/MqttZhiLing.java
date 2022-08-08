package com.zhuoce.mqtt;

import android.util.Log;
import android.widget.Toast;

import com.rairmmd.andmqtt.AndMqtt;
import com.rairmmd.andmqtt.MqttPublish;
import com.rairmmd.andmqtt.MqttSubscribe;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;

public class MqttZhiLing {
    public static void publish(String topic, String msg) {
        AndMqtt.getInstance().publish(new MqttPublish()
                .setMsg(msg)
                .setQos(2).setRetained(false)
                .setTopic(topic), new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Log.i("Rair", "TOPIC:" + topic + "发送的数据:" + msg);

            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Log.i("Rair", "(MainActivity.java:84)-onFailure:-&gt;发布失败");
            }
        });
    }

    public static void dingYue(String topic) {
        AndMqtt.getInstance().subscribe(new MqttSubscribe().setQos(2).setTopic(topic), new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Log.i("Rair", "订阅--TOPIC:" + topic);
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

            }
        });
    }

    /**
     * 请求二维码地址
     *
     * @param topic
     */
    public static void qingQiuErWeiMa(String topic) {
        AndMqtt.getInstance().subscribe(new MqttSubscribe().setQos(2).setTopic(Addr.ccidAddr), new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                //  Toast.makeText(context, "订阅成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

            }
        });
    }

}
