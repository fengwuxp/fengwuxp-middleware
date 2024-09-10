package com.wind.metrics;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * 混合 Tagger
 *
 * @author wuxp
 * @date 2024-09-10 11:09
 **/
@AllArgsConstructor
public class CompositeTagger implements WindTagger {

    private final Collection<WindTagger> delegates;

    @Override
    public void tagging(@NotNull Object target, @NotNull Tag tag) {
        for (WindTagger delegate : delegates) {
            if (delegate.supports(target.getClass())) {
                delegate.tagging(target, tag);
            }
        }
    }

    @Override
    public void fullTagging(@NotNull Object target, @NotNull Collection<Tag> tags) {
        for (WindTagger delegate : delegates) {
            if (delegate.supports(target.getClass())) {
                delegate.fullTagging(target, tags);
            }
        }
    }

    @Override
    public boolean supports(Class<?> rawType) {
        return true;
    }
}
