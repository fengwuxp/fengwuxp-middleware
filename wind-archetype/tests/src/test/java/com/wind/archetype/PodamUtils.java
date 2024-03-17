package com.wind.archetype;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/**
 * @author wind
 */
public class PodamUtils {

    private static final PodamFactory FACTORY = new PodamFactoryImpl();

    public static <T> T manufacturePojo(Class<T> clazz) {
        return FACTORY.manufacturePojo(clazz);
    }
}
