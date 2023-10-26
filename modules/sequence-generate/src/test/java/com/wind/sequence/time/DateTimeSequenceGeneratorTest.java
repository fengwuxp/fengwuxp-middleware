package com.wind.sequence.time;

import com.wind.sequence.NumericSequenceGenerator;
import com.wind.sequence.SequenceGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DateTimeSequenceGeneratorTest {


    @Test
    void testDayNumeric() {
        SequenceGenerator day = DateTimeSequenceGenerator.day(new NumericSequenceGenerator());
        Assertions.assertTrue(day.next().endsWith("00000001"));
        Assertions.assertTrue(day.next().endsWith("00000002"));
        Assertions.assertTrue(day.next().endsWith("00000003"));
        Assertions.assertTrue(day.next().endsWith("00000004"));
    }

    @Test
    void testDayRandomNumeric() {
        SequenceGenerator day = DateTimeSequenceGenerator.day(() -> SequenceGenerator.randomNumeric(8));
        Assertions.assertEquals(16, day.next().length());
    }

    @Test
    void testDay3RandomAlphanumeric() {
        SequenceGenerator day = DateTimeSequenceGenerator.day(() -> SequenceGenerator.randomAlphanumeric(8));
        String next = day.next();
        Assertions.assertEquals(16, next.length());
    }
}

