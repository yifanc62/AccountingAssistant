package com.cirnoteam.accountingassistant.json;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yifan on 2017/7/23.
 */
public class AccountReqEntity {
    private String token;
    private String uuid;
    private String type;
    private List<SyncAccount> accounts;

    public AccountReqEntity() {
        this.accounts = new ArrayList<>();
    }

    public String getToken() {
        return token;
    }

    public AccountReqEntity setToken(String token) {
        this.token = token;
        return this;
    }

    public String getUuid() {
        return uuid;
    }

    public AccountReqEntity setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getType() {
        return type;
    }

    public AccountReqEntity setType(String type) {
        this.type = type;
        return this;
    }

    public List<SyncAccount> getAccounts() {
        return accounts;
    }

    public AccountReqEntity setAccounts(List<SyncAccount> accounts) {
        this.accounts = accounts;
        return this;
    }

    public AccountReqEntity addAccount(SyncAccount account) {
        accounts.add(account);
        return this;
    }
}
