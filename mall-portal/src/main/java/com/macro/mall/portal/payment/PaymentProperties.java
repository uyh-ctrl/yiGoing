package com.macro.mall.portal.payment;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Payment credentials are supplied only through environment variables.
 * Private keys and certificates must never be committed to the repository.
 */
@Component
@ConfigurationProperties(prefix = "easygoing.payment")
public class PaymentProperties {
    private boolean allowManualConfirmation;
    private final Alipay alipay = new Alipay();
    private final WechatPay wechatPay = new WechatPay();

    public boolean isAllowManualConfirmation() { return allowManualConfirmation; }
    public void setAllowManualConfirmation(boolean allowManualConfirmation) { this.allowManualConfirmation = allowManualConfirmation; }
    public Alipay getAlipay() { return alipay; }
    public WechatPay getWechatPay() { return wechatPay; }

    public static class Alipay {
        private boolean enabled;
        private String appId;
        private String privateKey;
        private String publicKey;
        private String notifyUrl;
        private String returnUrl;
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public String getAppId() { return appId; }
        public void setAppId(String appId) { this.appId = appId; }
        public String getPrivateKey() { return privateKey; }
        public void setPrivateKey(String privateKey) { this.privateKey = privateKey; }
        public String getPublicKey() { return publicKey; }
        public void setPublicKey(String publicKey) { this.publicKey = publicKey; }
        public String getNotifyUrl() { return notifyUrl; }
        public void setNotifyUrl(String notifyUrl) { this.notifyUrl = notifyUrl; }
        public String getReturnUrl() { return returnUrl; }
        public void setReturnUrl(String returnUrl) { this.returnUrl = returnUrl; }
    }

    public static class WechatPay {
        private boolean enabled;
        private String appId;
        private String mchId;
        private String mchSerialNo;
        private String privateKey;
        private String apiV3Key;
        private String platformCertificate;
        private String notifyUrl;
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public String getAppId() { return appId; }
        public void setAppId(String appId) { this.appId = appId; }
        public String getMchId() { return mchId; }
        public void setMchId(String mchId) { this.mchId = mchId; }
        public String getMchSerialNo() { return mchSerialNo; }
        public void setMchSerialNo(String mchSerialNo) { this.mchSerialNo = mchSerialNo; }
        public String getPrivateKey() { return privateKey; }
        public void setPrivateKey(String privateKey) { this.privateKey = privateKey; }
        public String getApiV3Key() { return apiV3Key; }
        public void setApiV3Key(String apiV3Key) { this.apiV3Key = apiV3Key; }
        public String getPlatformCertificate() { return platformCertificate; }
        public void setPlatformCertificate(String platformCertificate) { this.platformCertificate = platformCertificate; }
        public String getNotifyUrl() { return notifyUrl; }
        public void setNotifyUrl(String notifyUrl) { this.notifyUrl = notifyUrl; }
    }
}
