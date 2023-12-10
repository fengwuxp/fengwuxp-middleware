package com.wuxp.mybatisplus.processor.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_user")
public class ExampleUser extends AbstractEntity {

    private String userName;

    @TableField("is_deleted")
    private Boolean deleted;

}
