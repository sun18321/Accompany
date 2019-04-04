package com.play.accompany.view;

import android.content.Intent;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.play.accompany.R;
import com.play.accompany.base.BaseActivity;

public class RankActivity extends BaseActivity {
    public static final String ACTIVITY_TITLE = "title";
    private String mTitle = "title";
    private RecyclerView mRecyclerView;

    @Override
    protected int getLayout() {
        return R.layout.activity_rank;
    }

    @Override
    protected String getTag() {
        return "rankActivity";
    }

    @Override
    protected void parseIntent() {
        super.parseIntent();

        Intent intent = getIntent();
        if (intent != null) {
            mTitle = intent.getStringExtra(ACTIVITY_TITLE);
        }
    }

    @Override
    protected void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView tvTitle = findViewById(R.id.tv_title);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        tvTitle.setText(mTitle);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RankActivity.this.finish();
            }
        });
        mRecyclerView = findViewById(R.id.recycler);
    }
}
