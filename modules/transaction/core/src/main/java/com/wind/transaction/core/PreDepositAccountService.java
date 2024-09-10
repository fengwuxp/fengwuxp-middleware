package com.wind.transaction.core;

import com.wind.transaction.core.request.RechargePreDepositRequest;
import com.wind.transaction.core.request.WithdrawPreDepositRequest;

/**
 * 预存款账号服务
 *
 * @author wuxp
 * @date 2024-05-14 16:54
 * @see TransactionAccount
 * @see BasedOnFreezeTransactionAccountService
 **/
public interface PreDepositAccountService {

    /**
     * 充值
     *
     * @param request 充值请求
     */
    void recharge(RechargePreDepositRequest request);

    /**
     * 提现
     *
     * @param request 提现请求
     */
    void withdraw(WithdrawPreDepositRequest request);

    /**
     * 支付
     */
    void pay();

    /**
     * 扣手续费
     */
    void fee();

    /**
     * 退款
     */
    void reimburse();

}
