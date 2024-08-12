package com.wind.mask.masker;

import com.google.common.collect.ImmutableMap;
import com.wind.mask.masker.json.MapObjectMasker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wuxp
 * @date 2024-08-09 11:19
 **/
 class MapObjectMaskerTests {

     private final MapObjectMasker masker = new MapObjectMasker();

    @Test
    void testMask() {
        Map<String, Object> maps = buildMaps();
        Object mask = masker.mask(maps, Arrays.asList("$.data.values[0].ak", "$.ak"));
        Assertions.assertEquals(maps,mask);
        Assertions.assertTrue(maps.toString().contains("***"));
        Assertions.assertTrue(mask.toString().contains("***"));
    }

    Map<String, Object> buildMaps() {
        Map<String, Object> sensitiveMaps = new HashMap<>();
        List<Map<String, String>> values = new ArrayList<>();
        Map<String, String> item = new HashMap<>();
        item.put("ak", "0001");
        values.add(item);
        sensitiveMaps.put("data", new HashMap<>(ImmutableMap.of("values", values)));
        return sensitiveMaps;
    }
}
