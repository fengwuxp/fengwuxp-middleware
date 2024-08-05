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
import org.junit.jupiter.api.BeforeEach;
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
public class WindObjectDigestUtilsTests {

    private Example example;

    @BeforeEach
    void setup() throws Exception {
        example = new Example();
        example.setId(1L);
        example.setTags(ImmutableMap.of("c1", "1", "a1", "test", "zh", new Tag("001", "exampleTag")));
        example.setSex(Sex.M);
        example.setAge(26);
        LocalDateTime birthday = LocalDateTime.parse("2020-02-11 00:12:55", WindDateFormater.YYYY_MM_DD_HH_MM_SS.getFormatter());
        example.setTimeOfBirth(birthday);
        example.setBirthday(LocalDate.parse("2020-02-11", WindDateFormater.YYYY_MM_DD.getFormatter()));
        example.setMyDate(DateUtils.parseDate("2020-02-11", WindDateFormatPatterns.YYYY_MM_DD));
        example.setMyTags(Arrays.asList(new Tag("a", "a1"), new Tag("b", "b1")));
        example.setNames(new String[]{"a", "b", "c"});
        example.setFees(new int[]{0, 23, 99});
        Demo demo = new Demo();
        demo.setId("demo");
        demo.setK1("k");
        demo.setL2(false);
        demo.setE3(1.30912d);
        example.setDemo(demo);
    }

    @Test
    void testGenSha256Text() {
        String text = WindObjectDigestUtils.genSha256Text(example, WindReflectUtils.getFieldNames(example.getClass()), null, WindConstants.LF);
        Assertions.assertEquals("age=26\n" +
                "birthday=1581379200000\n" +
                "demo=@e3=1.30912&id=demo&k1=k&l2=false&name=\n" +
                "fees=0,23,99\n" +
                "flag=false\n" +
                "id=1\n" +
                "myDate=1581350400000\n" +
                "myTags=@name=a&value=a1,@name=b&value=b1\n" +
                "name=\n" +
                "names=a,b,c\n" +
                "sex=M\n" +
                "tags=@a1=test&c1=1&zh=@name=001&value=exampleTag\n" +
                "timeOfBirth=1581379975000", text);
    }

    @Test
    void testGenSha25TextWithNames() {
        String text = WindObjectDigestUtils.genSha256Text(example, Arrays.asList("name", "id", "sex", "myTags", "flag"), null, WindConstants.LF);
        Assertions.assertEquals("flag=false\n" +
                "id=1\n" +
                "myTags=@name=a&value=a1,@name=b&value=b1\n" +
                "name=\n" +
                "sex=M", text);
    }

    @Test
    void testSha256() {
        Assertions.assertEquals("c0cd9674c66353cabaf7dacbd7b7119dcfb70d47926a15eaed66d98d0167ba2e", WindObjectDigestUtils.sha256(example));
        example.setId(RandomUtils.nextLong());
        Assertions.assertNotEquals("c0cd9674c66353cabaf7dacbd7b7119dcfb70d47926a15eaed66d98d0167ba2e", WindObjectDigestUtils.sha256(example));
    }

    @Test
    void testSha256WithNames() {
        List<String> names = Arrays.asList("name", "id", "sex", "myTags", "flag");
        Assertions.assertEquals("b2fe7c71cdba884afe31b9103111ffb3f728ecc159df68d59bc86f542eaa4834", WindObjectDigestUtils.sha256WithNames(example, names));
        example.setFees(new int[]{1});
        Assertions.assertEquals("b2fe7c71cdba884afe31b9103111ffb3f728ecc159df68d59bc86f542eaa4834", WindObjectDigestUtils.sha256WithNames(example, names));
        example.setId(RandomUtils.nextLong());
        Assertions.assertNotEquals("b2fe7c71cdba884afe31b9103111ffb3f728ecc159df68d59bc86f542eaa4834", WindObjectDigestUtils.sha256WithNames(example, names));
    }

    @Data
    static class ExampleSupper {

        private Long id;

        private Map<String, Object> tags;
    }

    enum Sex {
        N,
        M
    }

    @AllArgsConstructor
    static class Tag {

        private String name;

        private String value;
    }


    @Data
    static class Demo {

        private String id;

        private String name;

        private String k1;

        private boolean l2;

        private double e3;
    }


    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    @Data
    static class Example extends ExampleSupper {

        private String name;

        private int age;

        private Sex sex;

        private boolean flag;

        private String[] names;

        private int[] fees;

        private List<Tag> myTags;

        private LocalDateTime timeOfBirth;

        private LocalDate birthday;

        private Date myDate;

        private Demo demo;
    }
}
