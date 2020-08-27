package com.nala.sharding.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 客户跟进记录
 * </p>
 *
 * @author wangj01@lizi.com
 * @since 2019-11-20
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class CustomerVisitRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 销售id
     */
    private String salesId;

    /**
     * 销售代表名称
     */
    private String salesName;

    /**
     * 客户id（客户阶段）
     */
    private String userId;

    /**
     * 线索id（线索阶段）
     */
    private String cluesId;

    /**
     * 跟进内容
     */
    private String visitContent;

    /**
     * 图片
     */
    private String imgUrl;

    /**
     * 线索等级
     */
    private String cluesLevel;

    /**
     * 拜访类型（线索结算  客户阶段）
     */
    private String phases;

    /**
     * 跟进类型 1 主动跟进 2 微信跟进 3 电话跟进
     */
    private Integer visitType;

    /**
     * 联系人姓名
     */
    private String contactName;

    private String friendsWxId;

    private String salesWxId;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;
    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
