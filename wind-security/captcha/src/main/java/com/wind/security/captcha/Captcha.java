package com.wind.security.captcha;

import com.wind.common.enums.DescriptiveEnum;
import lombok.Data;

import java.beans.Transient;
import java.time.Duration;
import java.util.Date;

/**
 * 验证码，在特定场景保证安全或验证身份
 *
 * @author wuxp
 * @date 2023-09-23 21:15
 **/
public interface Captcha extends CaptchaValue {

    /**
     * 可以是用户的手机号码、邮箱，或者 uuid
     *
     * @return 验证码所有者
     */
    String getOwner();

    /**
     * @return 验证码类型
     */
    CaptchaType getType();

    /**
     * @return 验证码使用场景
     */
    CaptchaUseScene getUseScene();

    /**
     * @return 已验证次数
     */
    int getVerificationCount();

    /**
     * @return 允许验证次数
     */
    int getAllowVerificationTimes();

    /**
     * @return 验证码失效时间
     */
    Date getExpireTime();

    /**
     * 记录一次验证次数
     *
     * @return {@link #getVerificationCount()} 值更新后的验证码对象
     */
    Captcha increase();

    /**
     * @return 是否有效
     */
    @Transient
    default boolean isEffective() {
        if (getExpireTime() == null) {
            // 过期时间没有，表示已过期
            return false;
        }
        // 验证次数小于允许验证次数 且 未过期
        return getVerificationCount() < getAllowVerificationTimes() &&
                getExpireTime().getTime() > System.currentTimeMillis();
    }

    /**
     * 验证码类型
     *
     * @author wuxp
     * @date 2023-09-24 09:41
     **/
    interface CaptchaType extends DescriptiveEnum {

        /**
         * @return 是否存在多个验证码同时进行验证
         */
        boolean isSupportMultiple();
    }


    /**
     * 验证码使用场景
     *
     * @author wuxp
     * @date 2023-09-24 09:41
     **/
    interface CaptchaUseScene extends DescriptiveEnum {
    }

    /**
     * 验证码配置
     *
     * @author wuxp
     * @date 2023-09-24 12:25
     **/
    interface CaptchaConfiguration {

        /**
         * @return 验证码有效时长
         */
        Duration getEffectiveTime();

        /**
         * @return 最大可以验证次数
         */
        int getMaxAllowVerificationTimes();

    }

    /**
     * 验证码发送流控配置
     */
    @Data
    class CaptchaFlowControl {

        /**
         * 流控统计窗口
         */
        private Duration window = Duration.ofHours(1);

        /**
         * 在流控统计间隔时间内允许发送的次数
         */
        private int speed = 5;
    }

}
