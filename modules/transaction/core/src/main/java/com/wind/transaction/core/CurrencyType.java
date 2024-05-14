package com.wind.transaction.core;

import com.wind.common.enums.DescriptiveEnum;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * 币种类型
 * 参见：https://en.wikipedia.org/wiki/ISO_4217
 *
 * @author wuxp
 * @date 2023-09-27 18:48
 **/
@Getter
public enum CurrencyType implements DescriptiveEnum {

    UNKNOWN("-1", "unknown", "未知", "??"),

    USD("840", "USD", "美元", "$"),

    USDT("000", "USDT", "泰达币", "T"),

    GBR("10000", "GBR", "芽布令", "??"),

    CNY("156", "CNY", "人民币", "￥"),

    EUR("978", "EUR", "欧元", "€"),

    HKD("344", "HKD", "港币", "HK$"),

    AUD("036", "AUD", "澳元", "A$"),

    CAD("124", "CAD", "加元", "C$"),

    GBP("826", "GBP", "英镑", "£"),

    JPY("392", "JPY", "日元", "¥"),

    CHF("756", "CHF", "瑞士法郎", "CHF"),

    KRW("410", "KRW", "韩币", "₩"),

    SGD("702", "SGD", "新币", "S$"),

    RUB("643", "RUB", "卢布", "₽"),

    TWD("901", "TWD", "新台币", "NT$"),

    THB("764", "THB", "泰铢", "฿"),

    IDR("360", "IDR", "印尼盾", "Rp"),

    COP("170", "COP", "哥伦比亚比索", "COL$"),

    PHP("608", "PHP", "菲律宾比索", "₱"),

    BDT("050", "BDT", "孟加拉塔卡", "৳"),

    INR("356", "INR", "印度卢比", "₹"),

    TRY("949", "TRY", "土耳其里拉", "₺"),

    PLN("985", "PLN", "波兰兹罗提", "zł"),

    CLP("152", "CLP", "智利比索", "$"),

    VND("704", "VND", "越南盾", "₫"),

    MYR("458", "MYR", "马来西亚林吉特", "RM"),

    PKR("586", "PKR", "巴基斯坦卢比", "P.Re"),

    BRL("986", "BRL", "巴西雷亚尔", "R$"),

    PEN("604", "PEN", "秘鲁新索尔", "S/"),

    MXN("484", "MXN", "墨西哥比索", "Mex$"),

    SEK("752", "SEK", "瑞典克朗", "kr"),

    ZAR("710", "ZAR", "南非", "Mex$"),

    NZD("554", "NZD", "新西兰元", "NZ$"),

    AED("784", "AED", "阿拉伯联合酋长国", "د.إ"),

    DKK("208", "DKK", "丹麦克朗", "kr"),

    EGP("818", "EGP", "埃及", "E£"),

    ARS("32", "ARS", "阿根廷比索", "$"),
    ;

    /**
     * 货币国际代码
     * 数字币代码段大于等于10000
     */
    private final String value;

    /**
     * 通用货币三字码
     */
    private final String enDesc;

    /**
     * 货币描述
     */
    private final String desc;

    /**
     * 货币符号
     */
    private final String sign;

    /**
     * 币种金额精度
     * 2：分
     * 3：厘
     */
    private final Integer precision;

    CurrencyType(String value, String enDesc, String desc, String sign) {
        this(value, enDesc, desc, sign, 2);
    }

    CurrencyType(String value, String enDesc, String desc, String sign, Integer precision) {
        this.value = value;
        this.enDesc = enDesc;
        this.desc = desc;
        this.sign = sign;
        this.precision = precision;
    }

    /**
     * 创建一个货币对象
     *
     * @param amount 货币数额
     * @return 货币对象
     */
    public Money of(int amount) {
        return Money.immutable(amount, this);
    }
}