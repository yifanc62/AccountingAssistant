package com.cirnoteam.accountingassistant.json;

import java.util.Date;

/**
 * Created by Yifan on 2017/7/23.
 */
public class SyncBook {
    private Long id;
    private Long remoteId;
    private String username;
    private String name;
    private Date time;

    public Long getId() {
        return id;
    }

    public SyncBook setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getRemoteId() {
        return remoteId;
    }

    public SyncBook setRemoteId(Long remoteId) {
        this.remoteId = remoteId;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public SyncBook setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getName() {
        return name;
    }

    public SyncBook setName(String name) {
        this.name = name;
        return this;
    }

    public Date getTime() {
        return time;
    }

    public SyncBook setTime(Date time) {
        this.time = time;
        return this;
    }
}
