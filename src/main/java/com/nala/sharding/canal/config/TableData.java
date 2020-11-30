package com.nala.sharding.canal.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 *
 * canal解析数据
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class TableData implements Serializable {

    /**
     * 主键ID
     */
    private String id;

    /***
     * 处理该条数据的处理器
     */
    private String handler;

    /**
     * 表名
     */
    private String tableName;

    /***
     * 数据库名称
     */
    private String dbName;
}
