package com.wind.mask.masker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author wuxp
 * @date 2024-08-07 17:21
 **/
public class StringRangMaskerTests {


    @Test
    void testBase() {
        Assertions.assertEquals("******", StringRangMasker.secret().mask("12345"));
        Assertions.assertEquals("12345", new StringRangMasker(5, 7).mask("12345"));
        Assertions.assertEquals("12***", new StringRangMasker(2, 5).mask("12345"));
        Assertions.assertEquals("***45", new StringRangMasker(0, 3).mask("12345"));
    }

    @Test
    void testMobilePhone() {
        String mask = StringRangMasker.phone().mask("18900100123");
        Assertions.assertEquals("189****0123", mask);
    }
}
