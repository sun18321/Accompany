package com.play.accompany.design;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.play.accompany.R;
import com.play.accompany.bean.TopGameBean;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TypeDialog extends AlertDialog.Builder {
    private Context mContext;
    private Set<Integer> mSelectSet;
    private List<String> mAllList = new ArrayList<>();
    private TagFlowLayout mFlowLayout;

    public TypeDialog(@NonNull Context context, List<TopGameBean> allList, Set<Integer> selectSet) {
        super(context);

        mContext = context;
        mSelectSet = selectSet;

        mAllList.clear();
        for (TopGameBean topGameBean : allList) {
            mAllList.add(topGameBean.getName());
        }
        init();
    }

    private void init() {

        View view = LayoutInflater.from(mContext).inflate(R.layout.type_select, null);
        mFlowLayout = view.findViewById(R.id.id_flowlayout);
        TagAdapter<String> adapter = new TagAdapter<String>(mAllList) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) LayoutInflater.from(mContext).inflate(R.layout.flowlayout_text, mFlowLayout, false);
                tv.setText(s);
                return tv;
            }
        };
        if (!mSelectSet.isEmpty()) {
            adapter.setSelectedList(mSelectSet);
        }
        mFlowLayout.setAdapter(adapter);
        setView(view);
    }


    public Set<Integer> getSelectSet() {
        if (mFlowLayout != null) {
            return mFlowLayout.getSelectedList();
        }
        return null;
    }
}
