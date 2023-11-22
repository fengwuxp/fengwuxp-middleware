package com.wind.common.utils;

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
    }
}
