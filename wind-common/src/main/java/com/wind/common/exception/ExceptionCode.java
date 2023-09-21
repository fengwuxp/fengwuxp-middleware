package com.wind.common.exception;

import com.wind.common.WindConstants;
import com.wind.common.enums.DescriptiveEnum;

import static com.wind.common.WindConstants.SUCCESSFUL_CODE;

/**
 * 异常码描述接口，建议用枚举实现
 *
 * @author wuxp
 */
public interface ExceptionCode extends DescriptiveEnum {

    /**
     * 成功
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
     * 通用错误
     */
    ExceptionCode DEFAULT_ERROR = new ExceptionCode() {
        @Override
        public String getCode() {
            return WindConstants.DEFAULT_ERROR_CODE;
        }

        @Override
        public String getDesc() {
            return "失败";
        }
    };

    /**
     * @return 异常码
     */
    String getCode();
}
