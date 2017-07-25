package com.cirnoteam.accountingassistant.json;

import java.util.Date;

/**
 * Created by Yifan on 2017/7/23.
 */
public class SyncRecord {
    private Long id;
    private Long remoteId;
    private Long remoteAccountId;
    private Boolean expense;
    private Float amount;
    private Integer type;
    private String remark;
    private Date time;
    private Date rTime;

    public Date getrTime() {
        return rTime;
    }

    public SyncRecord setrTime(Date rTime) {
        this.rTime = rTime;
        return this;
    }

    public Long getId() {
        return id;
    }

    public SyncRecord setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getRemoteId() {
        return remoteId;
    }

    public SyncRecord setRemoteId(Long remoteId) {
        this.remoteId = remoteId;
        return this;
    }

    public Long getRemoteAccountId() {
        return remoteAccountId;
    }

    public SyncRecord setRemoteAccountId(Long remoteAccountId) {
        this.remoteAccountId = remoteAccountId;
        return this;
    }

    public Boolean getExpense() {
        return expense;
    }

    public SyncRecord setExpense(Boolean expense) {
        this.expense = expense;
        return this;
    }

    public Float getAmount() {
        return amount;
    }

    public SyncRecord setAmount(Float amount) {
        this.amount = amount;
        return this;
    }

    public Integer getType() {
        return type;
    }

    public SyncRecord setType(Integer type) {
        this.type = type;
        return this;
    }

    public String getRemark() {
        return remark;
    }

    public SyncRecord setRemark(String remark) {
        this.remark = remark;
        return this;
    }

    public Date getTime() {
        return time;
    }

    public SyncRecord setTime(Date time) {
        this.time = time;
        return this;
    }
}
