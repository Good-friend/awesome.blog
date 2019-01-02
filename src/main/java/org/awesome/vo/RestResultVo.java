package org.awesome.vo;

public class RestResultVo {
    private int code;
    private String message;
    private Object data;

    public RestResultVo(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public static class RestResultCode {
        public static final int SUCCESS = 1;
        public static final int FAILED = 0;
        public static final int EXCEPTION = -1;

    }
}
