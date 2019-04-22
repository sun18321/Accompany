package com.play.accompany.view;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.play.accompany.R;
import com.play.accompany.base.BaseActivity;

import java.util.Locale;

import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imlib.model.Conversation;

public class ConversationActivity extends BaseActivity {
    private String mTitle;

    @Override
    protected int getLayout() {
        return R.layout.activity_conversation;
    }

    @Override
    protected String getTag() {
        return "ConversationActivity";
    }

    @Override
    protected void initViews() {
        initToolbar(mTitle);
    }

    @Override
    protected void parseIntent() {
        super.parseIntent();

        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        String targetId = intent.getData().getQueryParameter("targetId");
        mTitle = intent.getData().getQueryParameter("title");
//        mTargetIds = intent.getData().getQueryParameter("targetIds");
        //intent.getData().getLastPathSegment();//获得当前会话类型
        Conversation.ConversationType conversationType = Conversation.ConversationType.valueOf(intent.getData().getLastPathSegment().toUpperCase(Locale.getDefault()));
        enterFragment(conversationType, targetId);
    }

    /**
     * 加载会话页面 ConversationFragment
     *
     * @param mConversationType
     * @param mTargetId
     */
    private void enterFragment(Conversation.ConversationType mConversationType, String mTargetId) {

        ConversationFragment fragment = (ConversationFragment) getSupportFragmentManager().findFragmentById(R.id.conversation);
        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                .appendPath("conversation").appendPath(mConversationType.getName().toLowerCase())
                .appendQueryParameter("targetId", mTargetId).build();

        if (fragment != null) {
            fragment.setUri(uri);
        }
    }
}
