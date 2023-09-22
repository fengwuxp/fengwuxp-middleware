package com.wind.common.exception;

import com.wind.common.WindConstants;
import com.wind.common.enums.DescriptiveEnum;

import static com.wind.common.WindConstants.SUCCESSFUL_CODE;

/**
 * 异常码描述接口，不同的业务场景可以实现该接口做扩展。
 * 建议用枚举实现
 *
 * @author wuxp
 * @see DefaultExceptionCode
 */
public interface ExceptionCode extends DescriptiveEnum {

    /**
     * 表示成功的 code
     */
    ExceptionCode SUCCESSFUL = new ExceptionCode() {
        @Override
        public String getCode() {
            return SUCCESSFUL_CODE;
        }

        @Override
        public String getDesc() {
            return WindConstants.EMPTY;
        }
    };


    /**
     * @return 异常码
     */
    String getCode();
}
