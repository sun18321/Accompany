package com.play.accompany.fragment;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.play.accompany.R;
import com.play.accompany.base.BaseFragment;

public class ExportFragment extends BaseFragment {

    private RecyclerView mRecyclerView;
    private View mLoadView;

    @Override
    protected int getLayout() {
        return R.layout.fragment_record;
    }

    @Override
    protected void initViews(View view) {
        mLoadView = view.findViewById(R.id.loading_view);
        mRecyclerView = view.findViewById(R.id.recyclerview);

    }

    @Override
    protected String getFragmentName() {
        return "ExportFragment";
    }

}
