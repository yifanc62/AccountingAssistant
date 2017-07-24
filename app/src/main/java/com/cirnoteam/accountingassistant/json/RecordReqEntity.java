package com.cirnoteam.accountingassistant.json;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yifan on 2017/7/23.
 */
public class RecordReqEntity {
    private String token;
    private String uuid;
    private String type;
    private List<SyncRecord> records;

    public RecordReqEntity() {
        this.records = new ArrayList<>();
    }

    public String getToken() {
        return token;
    }

    public RecordReqEntity setToken(String token) {
        this.token = token;
        return this;
    }

    public String getUuid() {
        return uuid;
    }

    public RecordReqEntity setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getType() {
        return type;
    }

    public RecordReqEntity setType(String type) {
        this.type = type;
        return this;
    }

    public List<SyncRecord> getRecords() {
        return records;
    }

    public RecordReqEntity setRecords(List<SyncRecord> records) {
        this.records = records;
        return this;
    }

    public RecordReqEntity addRecord(SyncRecord record) {
        records.add(record);
        return this;
    }
}
