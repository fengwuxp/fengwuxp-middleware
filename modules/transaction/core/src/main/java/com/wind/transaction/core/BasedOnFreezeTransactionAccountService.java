package com.wind.transaction.core;


import com.wind.transaction.core.request.FreezeRequest;
import com.wind.transaction.core.request.TransferRequest;

/**
 * 支出基于 {@link TransactionAccount#getFreezeAmount()}，的交易账户
 * 账户有{@link TransactionAccount#getCurrencyType()} ()}归属，在交易时需要将货币类型转换为一致
 *
 * @author wuxp
 * @date 2023-10-06 08:40
 **/
public interface BasedOnFreezeTransactionAccountService {

    /**
     * 获取一个交易账号
     *
     * @param id 交易账号 ID
     * @return 交易账号
     */
    TransactionAccount getAccount(TransactionAccountId id);

    /**
     * 转入金额到账户，仅累加 {@link TransactionAccount#getAmount()}
     *
     * @param request 转入请求
     */
    void transferIn(TransactionAccountId id, TransferRequest request);

    /**
     * 基于冻结余额的账户支出，累加 {@link TransactionAccount#getExpensesAmount()}，扣除{@link TransactionAccount#getFreezeAmount()}
     * 支出 {@param request#getAmount()} 额度不能大于已冻结额度{@link TransactionAccount#getFreezeAmount()} ()}
     *
     * @param id      账户标识
     * @param request 转出请求
     */
    void transferOut(TransactionAccountId id, TransferRequest request);

    /**
     * 冻结并支出
     * 1：冻结账户余额
     * 2：支出
     *
     * @param id      账户标识
     * @param request 转出请求
     */
    void freezeTransferOut(TransactionAccountId id, TransferRequest request);

    /**
     * 退回金额到账户，仅累加 {@link TransactionAccount#getRefundedAmount()}
     *
     * @param id      账户标识
     * @param request 退款请求
     */
    void reimburse(TransactionAccountId id, TransferRequest request);

    /**
     * 冻结账户一部分额度，仅累加 {@link TransactionAccount#getFreezeAmount()}
     * 冻结额度不能大于可用额度 {@link TransactionAccount#getAvailableAmount()}
     *
     * @param id      账户标识
     * @param request 冻结请求
     */
    void freeze(TransactionAccountId id, FreezeRequest request);

    /**
     * 解冻{@link TransactionAccount#getFreezeAmount()}已被冻结一部分额度
     * 解冻额度不能大于已冻结额度{@link TransactionAccount#getFreezeAmount()}
     *
     * @param id      账户标识
     * @param request 解冻请求
     */
    void unfreeze(TransactionAccountId id, FreezeRequest request);

    /**
     * @param id 账户标识
     * @return 是否支持该类型账户
     */
    boolean supports(TransactionAccountId id);
}
