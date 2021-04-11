package com.bphc.dbs_project.models;

import com.google.gson.annotations.SerializedName;

public class ServerResponse {

    @SerializedName("error")
    public boolean error;

    @SerializedName("message")
    public String message;

    @SerializedName("result")
    public Result result;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}
