package com.play.accompany.design;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.play.accompany.R;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;

public class TypeDialog extends AlertDialog.Builder {
    private Context mContext;
    private List<Integer> mSelectList;
    private List<String> mList = new ArrayList<>();


    public TypeDialog(@NonNull Context context, List<Integer> list) {
        super(context);

        mContext = context;
        mSelectList = list;

        init();
    }

    private void init() {
        mList.add("王者荣耀");
        mList.add("英雄联盟");
        mList.add("dota");
        mList.add("绝地求生");
        mList.add("吃鸡");
        mList.add("守望先锋");
        mList.add("魔兽争霸");
        mList.add("仙剑奇侠传");
        mList.add("三国志");
        mList.add("穿越火线");
        mList.add("跑跑卡丁车");
        mList.add("传奇");
        mList.add("帝国时代");
        mList.add("王者荣耀");
        mList.add("英雄联盟");
        mList.add("dota");
        mList.add("绝地求生");
        mList.add("吃鸡");
        mList.add("守望先锋");
        mList.add("魔兽争霸");
        mList.add("仙剑奇侠传");
        mList.add("三国志");
        mList.add("穿越火线");
        mList.add("跑跑卡丁车");
        mList.add("传奇");
        mList.add("帝国时代");



        View view = LayoutInflater.from(mContext).inflate(R.layout.type_select, null);
        final TagFlowLayout flowLayout = view.findViewById(R.id.id_flowlayout);
        TagAdapter<String> adapter = new TagAdapter<String>(mList) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) LayoutInflater.from(mContext).inflate(R.layout.flowlayout_text, flowLayout, false);
                tv.setText(s);
                return tv;
            }
        };
        flowLayout.setAdapter(adapter);

        setView(view);

    }

    public String getData() {
        return "data";
    }
}
