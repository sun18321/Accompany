package com.play.accompany.design;

import android.content.Context;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.play.accompany.R;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.List;
import java.util.Set;

public class PriceDialog extends AlertDialog.Builder {
    private Context mContext;
    private List<Integer> mList;
    private TagFlowLayout mFlowLayout;

    public PriceDialog(Context context, List<Integer> list) {
        super(context);

        mContext = context;
        mList = list;
        init();
    }

    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.type_select, null);
        mFlowLayout = view.findViewById(R.id.id_flowlayout);
        TagAdapter<Integer> adapter = new TagAdapter<Integer>(mList) {
            @Override
            public View getView(FlowLayout parent, int position, Integer integer) {
                TextView tv = (TextView) LayoutInflater.from(mContext).inflate(R.layout.flowlayout_text, mFlowLayout, false);
                tv.setText(String.valueOf(integer));
                return tv;
            }
        };

        mFlowLayout.setMaxSelectCount(1);
        mFlowLayout.setAdapter(adapter);
        setView(view);
    }

    public int getSelect() {
        Set<Integer> set = mFlowLayout.getSelectedList();
        int i = 0;
        for (Integer integer : set) {
            i = integer;
        }
        return mList.size() > i ? mList.get(i) : -1;
    }
}
