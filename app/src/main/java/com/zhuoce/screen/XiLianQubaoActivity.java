package com.zhuoce.screen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;

public class XiLianQubaoActivity extends Activity {
    @BindView(R.id.rl_tupian)
    RelativeLayout rlTupian;
    @BindView(R.id.tv_zimu)
    TextView tvZimu;
    @BindView(R.id.tv_hanzi)
    TextView tvHanzi;
    @BindView(R.id.rl_qujianma)
    RelativeLayout rlQujianma;
    @BindView(R.id.rl_shoujimima)
    RelativeLayout rlShoujimima;
    @BindView(R.id.ll_fanhui)
    LinearLayout llFanhui;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_qubao);
        ButterKnife.bind(this);

        rlQujianma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuJianActivity.actionStart(XiLianQubaoActivity.this, "1");
            }
        });

        rlShoujimima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuJianActivity.actionStart(XiLianQubaoActivity.this, "2");
            }
        });

        llFanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    /**
     * 用于其他Activty跳转到该Activity
     *
     * @param context
     */
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, XiLianQubaoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
