package com.play.accompany.bean;

public class VersionBean {
    /**
     * "major"  : 主版本号 需要强更
     * "minor"  : 次版本号 需要强更
     * "patch"  : bug修改 不用强更
     * "verUrl" : 更新包下载地址，版本号不同，对应下载的后缀名不同
     * 例 major: 1, minor:2, patch:3, 对应的文件：app_1_2_3.apk
     * 完整的下载地址： 97huyou.com/appDonwload/app_1_2_3.apk
     */

    private int major;
    private int minor;
    private int patch;
    private String verUrl;

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public int getPatch() {
        return patch;
    }

    public void setPatch(int patch) {
        this.patch = patch;
    }

    public String getVerUrl() {
        return verUrl;
    }

    public void setVerUrl(String verUrl) {
        this.verUrl = verUrl;
    }
}
