package com.electric.bunk.SharePrefrence;

import com.electric.bunk.pojoClasses.GetBookingsListPojo;
import com.electric.bunk.pojoClasses.GetBunkListPojo;

public class SingltonPojo {

    private String webViewLink;

    public String getBunkPath() {
        return bunkPath;
    }

    public void setBunkPath(String bunkPath) {
        this.bunkPath = bunkPath;
    }

    private String bunkPath;
    GetBookingsListPojo getBookingsListPojo;

    public GetBookingsListPojo getGetBookingsListPojo() {
        return getBookingsListPojo;
    }

    public void setGetBookingsListPojo(GetBookingsListPojo getBookingsListPojo) {
        this.getBookingsListPojo = getBookingsListPojo;
    }

    public String getWebViewLink() {
        return webViewLink;
    }

    public void setWebViewLink(String webViewLink) {
        this.webViewLink = webViewLink;
    }

    public String getLoginType() {
        return LoginType;
    }

    public void setLoginType(String loginType) {
        LoginType = loginType;
    }

    private String LoginType;

    private GetBunkListPojo getBunkListPojo;

    public GetBunkListPojo getGetBunkListPojo() {
        return getBunkListPojo;
    }

    public void setGetBunkListPojo(GetBunkListPojo getBunkListPojo) {
        this.getBunkListPojo = getBunkListPojo;
    }
}
