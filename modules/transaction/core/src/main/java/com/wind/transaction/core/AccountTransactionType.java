package com.wind.transaction.core;

import com.wind.common.enums.DescriptiveEnum;

import javax.validation.constraints.NotBlank;

/**
 * @author wuxp
 * @date 2024-10-10 22:08
 **/
public interface AccountTransactionType extends DescriptiveEnum {

    /**
     * @return 交易类型名称
     */
    @NotBlank
    String name();
}
