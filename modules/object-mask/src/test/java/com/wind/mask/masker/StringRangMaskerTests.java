package com.wind.mask.masker;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.wind.mask.masker.StringRangMasker.MAX_MASK_SIZE;

/**
 * @author wuxp
 * @date 2024-08-07 17:21
 **/
public class StringRangMaskerTests {

    @Test
    void testBase() {
        String text = "12345";
        Assertions.assertEquals("******", StringRangMasker.secret().mask(text));
        Assertions.assertEquals(text, new StringRangMasker(5, 7).mask(text));
        Assertions.assertEquals("12***", new StringRangMasker(2, 5).mask(text));
        Assertions.assertEquals("***45", new StringRangMasker(0, 3).mask(text));
        Assertions.assertEquals("12***", new StringRangMasker(2, 7).mask(text));
    }

    @Test
    void testMobilePhone() {
        String mask = StringRangMasker.phone().mask("18900100123");
        Assertions.assertEquals("189****0123", mask);
    }

    @Test
    void testLargeText() {
        int size = 10000;
        String text = RandomStringUtils.randomAlphanumeric(size);
        Assertions.assertEquals(20 + MAX_MASK_SIZE, new StringRangMasker(10, size - 10).mask(text).length());
    }
}
