package com.nala.sharding.canal;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.otter.canal.protocol.exception.CanalClientException;
import com.google.common.base.Joiner;
import com.nala.sharding.canal.config.CanalConfig;
import com.nala.sharding.canal.config.TableData;
import com.nala.sharding.disruptor.DisruptorProducer;
import com.nala.tools.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * canal客户端程
 */
@Slf4j
@Component
public class CanalClientExecutor implements DisposableBean, ApplicationListener<ContextRefreshedEvent> {


    @Autowired
    CanalConfig canalConfig;

    /**
     * 是否已经启动
     */
    private static volatile boolean start;

    /***
     * 与server连接器
     */
    private CanalConnector connector;

    /***
     *  需要监听过滤的一些表
     */
    private static List<String> tableNames;

    /**
     * 线程池统一接收，处理请求的
     */
    @Autowired
    @Qualifier("getAsyncExecutor")
    private Executor executor;

    /**
     * disruptor生产者，用于发布事件
     */
    @Autowired
    private DisruptorProducer disruptorProducer;

    /***
     * 初始化
     */
    private void init() {
        log.info(">>> 初始化Canal连接信息.......");

        //创建连接
        log.info(">>> 建立单Canal连接信息：{}", canalConfig.getHost());
        connector = CanalConnectors.newSingleConnector(new InetSocketAddress(canalConfig.getHost(), canalConfig.getPort()),
                canalConfig.getDestination(), canalConfig.getUserName(), canalConfig.getPassword());

        tableNames = canalConfig.getListenerTables();
    }

    /***
     * 观察者模式；当applicationContext被初始化或刷新时；
     * @param contextRefreshedEvent 触发事件
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

        log.info(">>> Canal启动......");
        init();
        // 执行启动
        executor.execute(this::start);
    }

    /***
     * bean卸载时调用
     */
    @Override
    public void destroy() {
        log.error(">>> 即将关闭Canal连接，销毁线程池.....");
        if (start) {
            try {
                connector.disconnect();
            } catch (CanalClientException e) {
                log.error(">>> 关闭Canal连接异常：", e);
            }
        }
    }


    /**
     * 检测连接canal-server是否正常
     */
    private void check() {
        if (log.isDebugEnabled()) {
            log.debug(">>> 开始执行检测Canal连接状态...");
        }
        if (!start) {
            log.warn(">>> 开始执行重连Canal服务...");
            this.start();
        }
    }

    /***
     * 监听处理
     */
    private void start() {
        try {
            //连接；并监听需要监听的数据库
            connector.connect();
            // 订阅指定数据表，由canal进行过滤
            connector.subscribe(Joiner.on(",").join(tableNames));
            start = true;
            log.info(">>> Canal连接成功，订阅DB：【{}】，table：【{}】", canalConfig.getListenerDb(), tableNames);
        } catch (CanalClientException e) {
            log.error(">>> Canal服务连接失败：", e);
            start = false;
        }
        if (start) {
            log.info(">>> Canal服务正式启动：");
            processBinlog();
        }
    }


    /**
     * 批量获取binlog日志
     */
    private void processBinlog() {
        log.info("canal-client start work!");
        //每一次拉取100条数据
        int batchSize = 100;
        Message msg;
        while (true) {
            try {
                msg = connector.getWithoutAck(batchSize);
                long batchId = msg.getId();
                int size = msg.getEntries().size();
                //没有数据，休眠5秒
                if (batchId < 0 || size == 0) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        log.error(">>> 休眠线程中断.......");
                    }
                } else {
                    // 数据处理
                    processData(msg.getEntries());
                }
                connector.ack(batchId);
            } catch (Exception e) {
                // 客户端异常，中断当前循环，等待定时任务重连操作
                log.error(">>> Canal 客户端异常，中断操作，并断开现有连接，等待定时任务定期重连操作...", e);
                this.close();
                break;
            }
        }
    }

    /**
     * 处理binlog日志
     *
     * @param entrys
     */
    private void processData(List<CanalEntry.Entry> entrys) {

        if (log.isDebugEnabled()) {
            log.debug("平台接收到需要处理数据，总数量：{}", entrys.size());
        }

        List<TableData> tableDataList = new ArrayList<>(entrys.size());
        String dbName;
        String tableName;
        CanalEntry.RowChange rowChange;
        for (CanalEntry.Entry entry : entrys) {
            // 事物数据不处理
            if (entry.getEntryType() != CanalEntry.EntryType.ROWDATA) {
                continue;
            }
            //获取表名
            tableName = entry.getHeader().getTableName();
            dbName = entry.getHeader().getSchemaName();
            if (!canalConfig.getListenerDb().contains(dbName)) {
                continue;
            }

            //仅处理部分表接口数据
            try {
                rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
                // RowData --具体insert/update/delete的变更数据，可为多条，1个binlog event事件可对应多条变更，比如批处理
                // insert 只有after数据，delete只有 before数据，update会有after和before数据

                CanalEntry.EventType eventType = rowChange.getEventType();
                if (eventType == CanalEntry.EventType.QUERY || rowChange.getIsDdl()) {
                    continue;
                }

                for (CanalEntry.RowData rowData : rowChange.getRowDatasList()) {
                    switch (rowChange.getEventType()) {
                        //当前仅处理Insert和更新数据
                        case INSERT:
                        case UPDATE:
                            //todo 业务处理逻辑
//                            checkShouldContinueLogic(rowData, dbName, tableName, tableDataList);
//                            log.info("rowData:{},dbName:{},tableName:{},tableDataList:{}", rowData, dbName, tableName, tableDataList);
                            tableDataList.add(new TableData().setTableName(tableName));
                            break;
                        case DELETE:
                            break;
                        default:
                            break;
                    }
                }
            } catch (Exception e) {
                log.error("当前行数据解析错误：", e);
            }
        }
        if (CollectionUtil.isNotEmpty(tableDataList)) {
//            List<TableData> tableDataDistinctList = tableDataList.stream().distinct().collect(Collectors.toList());
//            log.info("平台disruptor数据：【{}】", tableDataDistinctList);
            //发送数据到异步队列框架
            disruptorProducer.send(tableDataList);
        }
        if (log.isDebugEnabled()) {
            log.debug("接收到Mysql更新数据，总数量：{}", tableDataList.size());
        }
    }
//
//
//    /***
//     *
//     * 验证逻辑是否还要继续执行下去
//     * @param rowData   待解析数据
//     * @param dbName 数据库名称
//     * @param tableName 表名
//     * @param tableDataList 列表
//     * @return
//     */
//    private void checkShouldContinueLogic(CanalEntry.RowData rowData, String dbName, String tableName, List<TableData> tableDataList) {
//
//        //变更前数据
//        List<CanalEntry.Column> beforeColumns = rowData.getBeforeColumnsList();
//        //变更后数据
//        List<CanalEntry.Column> afterColumns = rowData.getAfterColumnsList();
//
//        //初始化
//        TableData tableData = new TableData();
//        tableData.setDbName(dbName)
//                .setTableName(tableName)
//                .setId(parseKey(afterColumns));
//
//        //订单推送逻辑
//        if (PURCHASE_SUB_ORDER.equals(tableName)) {
//            handlePayedOrder(tableData, tableDataList, beforeColumns, afterColumns);
//        }
//
//        //订单状态变更逻辑
//        if (SUPPLIERS_SKU.equals(tableName)) {
//            handlePayedNoStorageOrder(tableData, tableDataList, beforeColumns, afterColumns);
//        }
//
//        // 商品es数据同步逻辑
//        if (Arrays.asList(TABLE_NAME_FOR_ITEM_ES).contains(tableName)) {
//            TableData map = BeanMapperUtil.map(tableData, TableData.class);
//            map.setHandler(CanalDisruptorHandlerEnum.ITEM_ES_SAVE_UPDATE.name());
//            tableDataList.add(map);
//        }
//
//        // 订单es数据同步逻辑
//        if (PURCHASE_SUB_ORDER_REFUND.equals(tableName)) {
//            handleRefund(tableData, tableDataList, beforeColumns, afterColumns);
//        }
//
//
//    }
//
//
//    /**
//     * 解析主键KEY
//     * CanalEntry.Column字段：
//     * sqlType     [jdbc type]
//     * name        [字段名]
//     * isKey       [是否为主键]
//     * updated     [是否发生过变更]
//     * isNull      [值是否为null]
//     * value       [具体的内容，注意为string文本]
//     *
//     * @param columns
//     */
//    private String parseKey(List<CanalEntry.Column> columns) {
//        // 获取操作后完整的整条记录
//        String line = columns.stream().map(column -> column.getName() + "=" + column.getValue()).collect(Collectors.joining(","));
//        if (log.isDebugEnabled()) {
//            log.debug("完整的记录：{}", line);
//        }
//
//        //只保留主键Key的数据
//        Optional<CanalEntry.Column> first = columns.stream().filter(CanalEntry.Column::getIsKey).findFirst();
//        return first.get().getValue();
//
//    }

    /**
     * 关闭
     */
    private void close() {
        try {
            if (start) {
                connector.disconnect();
            }
        } catch (CanalClientException e) {
            log.error("关闭Canal连接错误：", e);
        } finally {
            start = false;
        }

    }

//    /**
//     * 订单由未支付变为已支付状态
//     *
//     * @param tableData
//     * @param tableDataList
//     * @param beforeColumns
//     * @param afterColumns
//     */
//    private void handlePayedOrder(TableData tableData, List<TableData> tableDataList, List<CanalEntry.Column> beforeColumns, List<CanalEntry.Column> afterColumns) {
//        String beforeStatus = "";
//        String afterStatus = "";
//        Integer type = -1;
//        String subOrderNum = "";
//        for (CanalEntry.Column column : beforeColumns) {
//            if ("status".equals(column.getName())) {
//                beforeStatus = column.getValue();
//                break;
//            }
//        }
//        for (CanalEntry.Column column : afterColumns) {
//            if ("status".equals(column.getName())) {
//                afterStatus = column.getValue();
//            } else if ("type".equals(column.getName())) {
//                type = Integer.valueOf(column.getValue());
//            } else if ("purchase_sub_order_num".equals(column.getName())) {
//                subOrderNum = column.getValue();
//            }
//        }
//
//        log.info("收到DB关于子单的变更信息subOrderNum: {} ,beforeStatus: {} ,afterStatus:{} ,type: {}", subOrderNum, beforeStatus, afterStatus, type);
//
//        if (PurchaseOrderTypeEnum.COMMON.getValue().equals(type)) {
//            if (!PurchaseSubOrderStatusEnum.UNPAYED.name().equals(beforeStatus) && !PurchaseSubOrderStatusEnum.PAYED.name().equals(beforeStatus) && !PurchaseSubOrderStatusEnum.PAYEDNOSTORAGE.name().equals(beforeStatus)) {
//                return;
//            }
//
//        } else if (PurchaseOrderTypeEnum.RESERVE.getValue().equals(type) || PurchaseOrderTypeEnum.RESERVE_CLOUD.getValue().equals(type)) {
//            if (!PurchaseSubOrderStatusEnum.WAITTAILPAY.name().equals(beforeStatus) && !PurchaseSubOrderStatusEnum.PAYED.name().equals(beforeStatus) && !PurchaseSubOrderStatusEnum.DEPOSITPAYED.name().equals(beforeStatus)) {
//                return;
//            }
//
//        } else if (PurchaseOrderTypeEnum.MANUAL_BILL.getValue().equals(type) || PurchaseOrderTypeEnum.MANUAL_BILL_RESERVE.getValue().equals(type) || PurchaseOrderTypeEnum.MANUAL_BILL_NORMAL.getValue().equals(type)) {
//            if (!PurchaseSubOrderStatusEnum.UNPAYED.name().equals(beforeStatus) && !PurchaseSubOrderStatusEnum.DEPOSITPAYED.name().equals(beforeStatus)) {
//                return;
//            }
//        } else {
//            log.error("没有对应的订单类型，subOrderNum：【{}】", subOrderNum);
//            return;
//        }
//
//        if (PurchaseSubOrderStatusEnum.WAITDELIVERY.name().equals(afterStatus)) {
//            log.info("创建并发送rabbitmq的订单subOrderNum: {}", subOrderNum);
//            TableData map = BeanMapperUtil.map(tableData, TableData.class);
//            map.setHandler(CanalDisruptorHandlerEnum.ORDER_MQ.name());
//            tableDataList.add(map);
//        }
//
//        if (PurchaseSubOrderStatusEnum.PAYED.name().equals(afterStatus) || PurchaseSubOrderStatusEnum.WAITDELIVERY.name().equals(afterStatus)) {
//            log.info("订单支付完成，推送ES处理，subOrderNum: {}", subOrderNum);
//            TableData map1 = BeanMapperUtil.map(tableData, TableData.class);
//            map1.setHandler(CanalDisruptorHandlerEnum.ORDER_ES_SAVE_UPDATE.name());
//            tableDataList.add(map1);
//        }
//
//    }
//
//    /**
//     * 订单已付款库存不足变更为已付款
//     *
//     * @param tableData
//     * @param tableDataList
//     * @param beforeColumns
//     * @param afterColumns
//     */
//    private void handlePayedNoStorageOrder(TableData tableData, List<TableData> tableDataList, List<CanalEntry.Column> beforeColumns, List<CanalEntry.Column> afterColumns) {
//        int beforeStorage = 0;
//        int afterStorage = 0;
//        int beforeRStorage = 0;
//        int afterRStorage = 0;
//
//        for (CanalEntry.Column column : beforeColumns) {
//            if ("storage".equals(column.getName())) {
//                beforeStorage = Integer.valueOf(column.getValue());
//            } else if ("erp_reserve_storage".equals(column.getName())) {
//                beforeRStorage = Integer.valueOf(column.getValue());
//            }
//        }
//        for (CanalEntry.Column column : afterColumns) {
//            if ("storage".equals(column.getName())) {
//                afterStorage = Integer.valueOf(column.getValue());
//            } else if ("erp_reserve_storage".equals(column.getName())) {
//                afterRStorage = Integer.valueOf(column.getValue());
//            }
//        }
//
//        if (afterStorage > beforeStorage || afterRStorage > beforeRStorage) {
//            TableData map = BeanMapperUtil.map(tableData, TableData.class);
//            map.setHandler(CanalDisruptorHandlerEnum.ORDER_STATUS.name());
//            tableDataList.add(map);
//        }
//    }
//
//    /**
//     * 售后由售后中变为售后完成
//     *
//     * @param tableData
//     * @param tableDataList
//     * @param beforeColumns
//     * @param afterColumns
//     */
//    private void handleRefund(TableData tableData, List<TableData> tableDataList, List<CanalEntry.Column> beforeColumns, List<CanalEntry.Column> afterColumns) {
//        String before = "";
//        String after = "";
//        for (CanalEntry.Column column : beforeColumns) {
//            if ("status".equals(column.getName())) {
//                before = column.getValue();
//                break;
//            }
//        }
//        for (CanalEntry.Column column : afterColumns) {
//            if ("status".equals(column.getName())) {
//                after = column.getValue();
//                break;
//            }
//        }
//        if (!RefundStatusEnum.REFUNDING.name().equals(before) && !RefundStatusEnum.REFUNDED.name().equals(before)) {
//            return;
//        }
//        if (!RefundStatusEnum.REFUNDED.name().equals(after)) {
//            return;
//        }
//        TableData map = BeanMapperUtil.map(tableData, TableData.class);
//        map.setHandler(CanalDisruptorHandlerEnum.ORDER_ES_SAVE_UPDATE.name());
//        tableDataList.add(map);
//    }
}
