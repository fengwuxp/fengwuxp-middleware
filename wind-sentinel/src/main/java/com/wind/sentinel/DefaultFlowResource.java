package com.wind.sentinel;

import com.alibaba.csp.sentinel.EntryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wuxp
 * @date 2024-03-07 15:44
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DefaultFlowResource implements FlowResource {

    private String name;

    private EntryType entryType;

    private int resourceType;

    private String contextName;

    private String origin;
}
