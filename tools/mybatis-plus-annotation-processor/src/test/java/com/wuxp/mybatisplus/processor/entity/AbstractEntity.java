package com.wuxp.mybatisplus.processor.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public abstract class AbstractEntity {

    @TableId
    private Long id;

    private Date gmtCreate;

    private Date gmtModified;
}
