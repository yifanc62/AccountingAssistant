package com.cirnoteam.accountingassistant.json;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yifan on 2017/7/23.
 */
public class BookReqEntity {
    private String token;
    private String uuid;
    private String type;
    private List<SyncBook> books;

    public BookReqEntity() {
        this.books = new ArrayList<>();
    }

    public String getToken() {
        return token;
    }

    public BookReqEntity setToken(String token) {
        this.token = token;
        return this;
    }

    public String getUuid() {
        return uuid;
    }

    public BookReqEntity setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getType() {
        return type;
    }

    public BookReqEntity setType(String type) {
        this.type = type;
        return this;
    }

    public List<SyncBook> getBooks() {
        return books;
    }

    public BookReqEntity setBooks(List<SyncBook> books) {
        this.books = books;
        return this;
    }

    public BookReqEntity addBook(SyncBook book) {
        books.add(book);
        return this;
    }
}
