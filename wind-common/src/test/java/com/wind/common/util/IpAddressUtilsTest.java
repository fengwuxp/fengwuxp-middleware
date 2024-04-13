package com.wind.common.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author wuxp
 * @date 2023-11-21 14:17
 **/
class IpAddressUtilsTest {


    @Test
    void testGetLocalIpv4() {
        String host = IpAddressUtils.getLocalIpv4();
        Assertions.assertNotEquals("127.0.0.1", host);
        Assertions.assertTrue(IpAddressUtils.isIpV4(host));
        Assertions.assertFalse(IpAddressUtils.isIpV6(host));
        Assertions.assertTrue(IpAddressUtils.isValidIp(host));
    }
}
