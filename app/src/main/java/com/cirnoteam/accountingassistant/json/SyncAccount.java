package com.cirnoteam.accountingassistant.json;

import java.util.Date;

/**
 * Created by Yifan on 2017/7/23.
 */
public class SyncAccount {
    private Long id;
    private Long remoteId;
    private Long remoteBookId;
    private Float balance;
    private Integer type;
    private String name;
    private Date time;

    public Long getId() {
        return id;
    }

    public SyncAccount setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getRemoteId() {
        return remoteId;
    }

    public SyncAccount setRemoteId(Long remoteId) {
        this.remoteId = remoteId;
        return this;
    }

    public Long getRemoteBookId() {
        return remoteBookId;
    }

    public SyncAccount setRemoteBookId(Long remoteBookId) {
        this.remoteBookId = remoteBookId;
        return this;
    }

    public Float getBalance() {
        return balance;
    }

    public SyncAccount setBalance(Float balance) {
        this.balance = balance;
        return this;
    }

    public Integer getType() {
        return type;
    }

    public SyncAccount setType(Integer type) {
        this.type = type;
        return this;
    }

    public String getName() {
        return name;
    }

    public SyncAccount setName(String name) {
        this.name = name;
        return this;
    }

    public Date getTime() {
        return time;
    }

    public SyncAccount setTime(Date time) {
        this.time = time;
        return this;
    }
}
