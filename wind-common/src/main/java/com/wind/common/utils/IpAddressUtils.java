package com.wind.common.utils;


/**
 * 获取 http 请求的 来源 ip ip 地址
 *
 * @author wxup
 */
public final class IpAddressUtils {

    private IpAddressUtils() {
        throw new AssertionError();
    }


    /**
     * 是否为一个有效的 ip 地址
     *
     * @param ip ip 地址
     * @return if <code>true</code> 是
     */
    public static boolean isValidIp(String ip) {
        return isIpV6(ip) || isIpV4(ip);
    }

    /**
     * IPv6的地址长度为128位，是IPv4地址长度的4倍。于是IPv4点分十进制格式不再适用，采用十六进制表示。IPv6有3种表示方法。
     * 一、冒分十六进制表示法
     * 　　格式为X:X:X:X:X:X:X:X，其中每个X表示地址中的16b，以十六进制表示，例如：
     * 　　ABCD:EF01:2345:6789:ABCD:EF01:2345:6789
     * 　　这种表示法中，每个X的前导0是可以省略的，例如：
     * 　　2001:0DB8:0000:0023:0008:0800:200C:417A→ 2001:DB8:0:23:8:800:200C:417A
     * 二、0位压缩表示法
     * 　　在某些情况下，一个IPv6地址中间可能包含很长的一段0，可以把连续的一段0压缩为“::”。但为保证地址解析的唯一性，地址中”::”只能出现一次，例如：
     * 　　FF01:0:0:0:0:0:0:1101 → FF01::1101
     * 　　0:0:0:0:0:0:0:1 → ::1
     * 　　0:0:0:0:0:0:0:0 → ::
     * 三、内嵌IPv4地址表示法
     * 　　为了实现IPv4-IPv6互通，IPv4地址会嵌入IPv6地址中，此时地址常表示为：X:X:X:X:X:X:d.d.d.d，前96b采用冒分十六进制表示，而最后32b地址则使用IPv4的点分十进制表示，例如::192.168.0.1与::FFFF:192.168.0.1就是两个典型的例子，注意在前96b中，压缩0位的方法依旧适用
     * <p>
     * {@link java.net.InterfaceAddress#getAddress()}
     *
     * @param ip ip地址
     * @return 是否ipv6地址
     */
    public static boolean isIpV6(String ip) {

        if (ip == null || !ip.contains(":")) {
            return false;
        }

        //0:0:0:0:0:0:0:0 → ::
        if ("::".equals(ip)) {
            return true;
        }

        // 0 位压缩表示法
        if (ip.contains("::")) {
            String[] sections = ip.split(":");
            for (String section : sections) {
                try {
                    if (section.isEmpty() || section.contains(".") && isIpV4(section)) {
                        continue;
                    }

                    if (Integer.parseInt(section, 16) < 0) {
                        return false;
                    }

                } catch (Exception e) {
                    return false;
                }
            }
            return true;
        }


        /**
         *   三、内嵌IPv4地址表示法
         *         为了实现IPv4 - IPv6互通，IPv4地址会嵌入IPv6地址中，此时地址常表示为：X:
         *         X:
         *         X:
         *         X:
         *         X:
         *         X:
         *         d.d.d.d，前96b采用冒分十六进制表示，而最后32b地址则使用IPv4的点分十进制表示，例如:: 192.168 .0 .1 与::FFFF:192.168 .0 .1 就是两个典型的例子，注意在前96b中，
         *         压缩0位的方法依旧适用 '
         */
        String[] sections = ip.split(":");

        boolean hasIpV4 = ip.contains(".");

        if ((hasIpV4 && sections.length != 7)
                || sections.length != 8) {
            return false;
        }


        for (String section : sections) {
            try {

                if (section.contains(".") && isIpV4(section)) {
                    continue;
                }

                if (Integer.parseInt(section, 16) < 0) {
                    return false;
                }

            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }


    public static boolean isIpV4(String ip) {

        if (ip == null || !ip.contains(".")) {
            return false;
        }

        String[] sections = ip.split("\\.");

        if (sections.length != 4) {
            return false;
        }

        for (String section : sections) {
            try {
                if (Integer.parseInt(section) < 0) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }

        return true;
    }

}
