package com.play.accompany.design;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.play.accompany.R;
import com.play.accompany.bean.GameProperty;
import com.play.accompany.bean.TopGameBean;
import com.play.accompany.view.AccompanyApplication;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TypeDialog extends AlertDialog.Builder {
    private Context mContext;
    private List<String> mAllList = new ArrayList<>();
    private TagFlowLayout mFlowLayout;
    private List<TopGameBean> mOriginList;
    private SelectListenr mListener;

    public TypeDialog(@NonNull Context context, List<TopGameBean> allList, SelectListenr listener) {
        super(context);

        mListener = listener;
        mContext = context;
        mOriginList = allList;
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
        mFlowLayout.setMaxSelectCount(1);
        mFlowLayout.setAdapter(adapter);
        setView(view);

        setNegativeButton(AccompanyApplication.getContext().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        setPositiveButton(AccompanyApplication.getContext().getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mListener != null) {
                    mListener.onSelect(getSelectSet());
                }
            }
        });
    }

    private TopGameBean getSelectSet() {
        TopGameBean bean = new TopGameBean();
        Set<Integer> set = mFlowLayout.getSelectedList();
        for (Integer integer : set) {
            bean.setName(mOriginList.get(integer).getName());
            bean.setTypeId(mOriginList.get(integer).getTypeId());
        }
        return bean;
    }

    public interface SelectListenr{
        void onSelect(TopGameBean bean);
    }
}
