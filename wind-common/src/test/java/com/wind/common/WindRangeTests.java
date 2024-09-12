package com.wind.common;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.Date;

/**
 * @author wuxp
 * @date 2024-08-28 19:48
 **/
class WindRangeTests {

    @Test
    void testContains() {
        Assertions.assertTrue(WindRange.between(0, 10).contains(0));
        Assertions.assertTrue(WindRange.between(0, 10).contains(10));
        Assertions.assertTrue(WindRange.leftCloseRightOpen(0, 10).contains(0));
        Assertions.assertFalse(WindRange.leftCloseRightOpen(0, 10).contains(10));
    }

    @Test
    void testDate() throws Exception {
        WindRange<Date> range = WindRange.between(
                DateUtils.parseDate("2024-08-08", WindDateFormatPatterns.YYYY_MM_DD),
                DateUtils.parseDate("2024-08-09", WindDateFormatPatterns.YYYY_MM_DD));
        Assertions.assertTrue(range.contains(DateUtils.parseDate("2024-08-08 12", WindDateFormatPatterns.YYYY_MM_DD_HH)));
    }

    @Test
    void testLocalTime() {
        WindRange<ChronoLocalDateTime<?>> range = WindRange.between(
                LocalDateTime.parse("2024-08-08 00:00:00", WindDateFormater.YYYY_MM_DD_HH_MM_SS.getFormatter()),
                LocalDateTime.parse("2024-08-09 00:00:00", WindDateFormater.YYYY_MM_DD_HH_MM_SS.getFormatter()));
        Assertions.assertTrue(range.contains(LocalDateTime.parse("2024-08-08 12:01:05", WindDateFormater.YYYY_MM_DD_HH_MM_SS.getFormatter())));
    }
}
