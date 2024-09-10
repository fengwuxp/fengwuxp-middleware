package com.wind.metrics;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * @author wuxp
 * @date 2024-09-10 11:24
 **/
class CompositeTaggerTests {

    private static final Collection<Tag> TAGS = new HashSet<>();

    private final CompositeTagger tagger = new CompositeTagger(Collections.singleton(new ExampleTagger()));

    @BeforeEach
    void setup() {
        TAGS.clear();
    }

    @Test
    void testTagging() {
        Example data = new Example();
        Tag tag = Tag.of("example", "A");
        tagger.tagging(data, tag);
        Assertions.assertTrue(TAGS.contains(tag));
    }

    @Test
    void testFullTagging() {
        Example data = new Example();
        tagger.fullTagging(data, "example", "B");
        Assertions.assertTrue(TAGS.contains(Tag.of("example", "B")));
        Tag tag = Tag.of("example", "A");
        tagger.fullTagging(data, Collections.singletonList(tag));
        Assertions.assertTrue(TAGS.contains(tag));
    }


    static class ExampleTagger implements WindTagger {

        @Override
        public void tagging(@NotNull Object target, @NotNull Tag tag) {
            TAGS.add(tag);
        }

        @Override
        public void fullTagging(@NotNull Object target, @NotNull Collection<Tag> tags) {
            TAGS.addAll(tags);
        }

        @Override
        public boolean supports(Class<?> rawType) {
            return Example.class == rawType;
        }
    }

    @Data
    static class Example {

        private Long id;

        private String name;
    }
}
