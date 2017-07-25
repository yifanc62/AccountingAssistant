package com.cirnoteam.accountingassistant.json;

/**
 * Response
 * Servlet返回对象
 *
 * @author Yifan
 * @version 0.5
 */
public class Response {
    private int code;
    private String message;
    private Object entity;

    public Response() {

    }

    public Response(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Response(int code) {
        this.code = code;
        switch (code) {
            case 200:
                message = "OK";
                break;
            case 400:
                message = "Bad Request";
                break;
            case 401:
                message = "Unauthorized";
                break;
            case 402:
                message = "Payment Required";
                break;
            case 403:
                message = "Forbidden";
                break;
            case 404:
                message = "Not Found";
                break;
            case 405:
                message = "Method Not Allowed";
                break;
            case 406:
                message = "Not Acceptable";
                break;
            case 408:
                message = "Request Timeout";
                break;
            case 415:
                message = "Unsupported Media Type";
                break;
            case 500:
                message = "Internal Server Error";
                break;
            default:
                message = "Error";
                break;
        }
    }

    public int getCode() {
        return code;
    }

    public Response setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Response setMessage(String message) {
        this.message = message;
        return this;
    }

    public Object getEntity() {
        return entity;
    }

    public Response setEntity(Object entity) {
        this.entity = entity;
        return this;
    }
}
