package com.wind.common.util;

import com.google.common.collect.ImmutableMap;
import com.wind.common.WindConstants;
import com.wind.common.WindDateFormatPatterns;
import com.wind.common.WindDateFormater;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author wuxp
 * @date 2024-08-05 16:41
 **/
@Disabled
class WindObjectDigestUtilsTests {


    @Test
    void testGenSha256Text() throws Exception {
        WindObjectDigestWindObjectDigestExample target = mockExample();
        String text = WindObjectDigestUtils.genSha256Text(target, WindReflectUtils.getFieldNames(target.getClass()), null, WindConstants.LF);
        Assertions.assertEquals("age=26\n" +
                "birthday=1581379200000\n" +
                "demo=@e3=1.30912&id=demo&k1=k&l2=false&name=\n" +
                "fees=0,23,99\n" +
                "id=1\n" +
                "myDate=1581350400000\n" +
                "myTags=@name=a&value=a1,@name=b&value=b1\n" +
                "name=\n" +
                "names=a,b,c\n" +
                "sex=M\n" +
                "tags=@a1=test&c1=1&zh=@name=001&value=exampleTag\n" +
                "timeOfBirth=1581379975000\n" +
                "yes=false", text);
    }

    @Test
    void testGenSha25TextWithNames() throws Exception {
        WindObjectDigestWindObjectDigestExample target = mockExample();
        String text = WindObjectDigestUtils.genSha256Text(target, Arrays.asList("name", "id", "sex", "myTags", "yes"), null, WindConstants.LF);
        Assertions.assertEquals("id=1\n" +
                "myTags=@name=a&value=a1,@name=b&value=b1\n" +
                "name=\n" +
                "sex=M\n" +
                "yes=false", text);
    }

    @Test
    void testSha256() throws Exception {
        WindObjectDigestWindObjectDigestExample target = mockExample();
        String expected = "e618b66aa8c72343252fb0c80d4052a3a98b69ca634d51fe3ecc30991ea0937f";
        Assertions.assertEquals(expected, WindObjectDigestUtils.sha256(target));
        target.setId(RandomUtils.nextLong());
        Assertions.assertNotEquals(expected, WindObjectDigestUtils.sha256(target));
    }

    @Test
    void testSha256WithPrefix() throws Exception {
        WindObjectDigestWindObjectDigestExample target = mockExample();
        String expected = "4e8d6b471b70e3016e1684e33a595ad97ab05fc2153e7da725077d76e531f52d";
        Assertions.assertEquals(expected, WindObjectDigestUtils.sha256(target, "Example"));
        Assertions.assertNotEquals(expected, WindObjectDigestUtils.sha256(target, "E1"));
        target.setId(RandomUtils.nextLong());
        Assertions.assertNotEquals(expected, WindObjectDigestUtils.sha256(target, "Example"));
    }

    @Test
    void testSha256WithNames() throws Exception {
        List<String> names = Arrays.asList("name", "id", "sex", "myTags", "yes");
        WindObjectDigestWindObjectDigestExample target = mockExample();
        String expected = "1d05a24235efd0faef006338c1356ceebf404191737aae4d3fe54d218a335595";
        Assertions.assertEquals(expected, WindObjectDigestUtils.sha256WithNames(target, names));
        target.setFees(new int[]{1});
        Assertions.assertEquals(expected, WindObjectDigestUtils.sha256WithNames(target, names));
        target.setId(RandomUtils.nextLong());
        Assertions.assertNotEquals(expected, WindObjectDigestUtils.sha256WithNames(target, names));
    }

    private WindObjectDigestWindObjectDigestExample mockExample() throws Exception {
        WindObjectDigestWindObjectDigestExample result = new WindObjectDigestWindObjectDigestExample();
        result.setId(1L);
        result.setTags(ImmutableMap.of("c1", "1", "a1", "test", "zh", new WindObjectDigestTag("001", "exampleTag")));
        result.setSex(WindObjectDigestSex.M);
        result.setAge(26);
        LocalDateTime birthday = LocalDateTime.parse("2020-02-11 00:12:55", WindDateFormater.YYYY_MM_DD_HH_MM_SS.getFormatter());
        result.setTimeOfBirth(birthday);
        result.setBirthday(LocalDate.parse("2020-02-11", WindDateFormater.YYYY_MM_DD.getFormatter()));
        result.setMyDate(DateUtils.parseDate("2020-02-11", WindDateFormatPatterns.YYYY_MM_DD));
        result.setMyTags(Arrays.asList(new WindObjectDigestTag("a", "a1"), new WindObjectDigestTag("b", "b1")));
        result.setNames(new String[]{"a", "b", "c"});
        result.setFees(new int[]{0, 23, 99});
        WindObjectDigestDemo windObjectDigestDemo = new WindObjectDigestDemo();
        windObjectDigestDemo.setId("demo");
        windObjectDigestDemo.setK1("k");
        windObjectDigestDemo.setL2(false);
        windObjectDigestDemo.setE3(1.30912d);
        result.setDemo(windObjectDigestDemo);
        return result;
    }

    @Data
    public static class WindObjectDigestExampleSupper {

        private Long id;

        private Map<String, Object> tags;
    }

    public enum WindObjectDigestSex {
        N,
        M
    }

    @AllArgsConstructor
    public static class WindObjectDigestTag {

        private String name;

        private String value;
    }


    @Data
    public static class WindObjectDigestDemo {

        private String id;

        private String name;

        private String k1;

        private boolean l2;

        private double e3;
    }


    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    @Data
    public static class WindObjectDigestWindObjectDigestExample extends WindObjectDigestExampleSupper {

        private String name;

        private int age;

        private WindObjectDigestSex sex;

        private boolean yes;

        private String[] names;

        private int[] fees;

        private List<WindObjectDigestTag> myTags;

        private LocalDateTime timeOfBirth;

        private LocalDate birthday;

        private Date myDate;

        private WindObjectDigestDemo demo;
    }
}
