package com.seagle.android.task.helper;

import android.os.Bundle;

/**
 * <h1>异步任务回调</h1>
 *
 * @author : xiudong.yuan@midea.com.cn
 * @date : 2016/5/26
 */
public abstract class AsyncTaskCallback<Progress, Result> {

    /**
     * 完成。任务正常结束
     *
     * @param result 结果
     */
    protected abstract void onComplete(Result result);

    /**
     * 失败。任务异常结束
     *
     * @param errCode   错误码
     * @param errMsg    错误消息
     * @param extraData 附带数据
     */
    @SuppressWarnings("unused")
    protected void onError(final int errCode, final String errMsg, final Bundle extraData) {
    }

    /**
     * 进度更新
     *
     * @param progress 进度
     */
    @SuppressWarnings("unused")
    protected void onProgressUpdate(Progress progress) {
    }

    /**
     * 任务取消。任务因为用户取消结束
     */
    protected void onTaskCancelled() {
    }
}
