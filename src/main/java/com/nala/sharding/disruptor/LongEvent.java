package com.nala.sharding.disruptor;

import com.nala.sharding.canal.config.TableData;
import lombok.Data;

/**
 * disruptor时间Event
 */
@Data
public class LongEvent {

    /**
     * 同步canal数据结构
     */
    private TableData tableData;
}
