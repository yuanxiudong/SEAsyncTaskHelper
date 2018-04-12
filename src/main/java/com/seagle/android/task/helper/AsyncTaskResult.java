package com.seagle.android.task.helper;

import android.os.Bundle;

/**
 * <h1>任务执行结果</h1>
 *
 * @author : xiudong.yuan@midea.com.cn
 */
public final class AsyncTaskResult<Result> {

    /**
     * 请求成功
     */
    public static final int CODE_SUCCESS = 0;

    /**
     * 请求失败通用错误码
     */
    public static final int CODE_FAILURE = -1;

    /**
     * 请求超时
     */
    public static final int CODE_TIMEOUT = -2;

    /**
     * 结果码
     */
    private int mCode;

    /**
     * 消息
     */
    private String mMessage;

    /**
     * 用户结果。
     */
    private Result mResult;

    /**
     * 任务结果附带数据
     */
    private Bundle mExtraData;

    public int getCode() {
        return mCode;
    }

    public void setCode(int code) {
        mCode = code;
    }

    public Bundle getExtraData() {
        return mExtraData;
    }

    public void setExtraData(Bundle extraData) {
        mExtraData = extraData;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public Result getResult() {
        return mResult;
    }

    public void setResult(Result result) {
        mResult = result;
    }
}
