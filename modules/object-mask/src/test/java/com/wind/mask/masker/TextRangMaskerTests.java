package com.wind.mask.masker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author wuxp
 * @date 2024-08-07 17:21
 **/
public class TextRangMaskerTests {


    @Test
    void testBase() {
        Assertions.assertEquals("******", TextRangMasker.secret().mask("12345"));
        Assertions.assertEquals("12345", new TextRangMasker(5, 7).mask("12345"));
        Assertions.assertEquals("12***", new TextRangMasker(2, 5).mask("12345"));
        Assertions.assertEquals("***45", new TextRangMasker(0, 3).mask("12345"));
    }

    @Test
    void testMobilePhone() {
        String mask = TextRangMasker.phone().mask("18900100123");
        Assertions.assertEquals("189****0123", mask);
    }
}
