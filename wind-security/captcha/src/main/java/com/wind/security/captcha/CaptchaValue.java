package com.wind.security.captcha;

import lombok.Getter;

import java.beans.Transient;

/**
 * 验证码值
 *
 * @author wuxp
 * @date 2023-09-24 12:52
 **/
public interface CaptchaValue {

    /**
     * @return 验证码值，用于验证
     */
    String getValue();

    /**
     * 不进行持久化，减少存储压力
     *
     * @return 验证码内容，用于展示或发送给用户
     */
    @Transient
    String getContent();

    static CaptchaValue of(String value, String content) {
        return new ImmtableCaptchaValue(value, content);
    }

    @Getter
    class ImmtableCaptchaValue implements CaptchaValue {

        /**
         * 验证码值
         */
        private final String value;

        /**
         * 验证码内容
         */
        private final String content;

        ImmtableCaptchaValue(String value, String content) {
            this.value = value;
            this.content = content;
        }

        /**
         * 不进行持久化
         */
        @Transient
        public String getContent() {
            return content;
        }
    }
}
