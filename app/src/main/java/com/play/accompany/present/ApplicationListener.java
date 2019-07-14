package com.play.accompany.present;

import com.play.accompany.bean.TopGameBean;

import java.util.List;

public interface ApplicationListener {

     interface GameListListener{
         void onGameListener(List<TopGameBean> list);
     }

     interface AttentionListListener{
         void onAttentionListener(List<String> list);
     }

}
