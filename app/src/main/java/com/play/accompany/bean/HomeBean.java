package com.play.accompany.bean;

import java.util.List;

public class HomeBean {

    private List<UserInfo> msg;

    private OtherBean other;

    public class OtherBean {
        private List<BannerBean> activity;

        private List<TopGameBean> gameType;

        public List<BannerBean> getActivity() {
            return activity;
        }

        public void setActivity(List<BannerBean> activity) {
            this.activity = activity;
        }

        public List<TopGameBean> getGameType() {
            return gameType;
        }

        public void setGameType(List<TopGameBean> gameType) {
            this.gameType = gameType;
        }
    }

    public List<UserInfo> getMsg() {
        return msg;
    }

    public void setMsg(List<UserInfo> msg) {
        this.msg = msg;
    }

    public OtherBean getOther() {
        return other;
    }

    public void setOther(OtherBean other) {
        this.other = other;
    }
}
