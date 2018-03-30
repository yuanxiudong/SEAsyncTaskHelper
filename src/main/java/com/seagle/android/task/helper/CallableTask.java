package com.seagle.android.task.helper;


import android.os.Bundle;
import android.support.annotation.CallSuper;

import java.util.concurrent.Callable;

/**
 * <h1>任务</h1>
 * 用户任务的生命周期：开始，更新，结束。
 * 无论是正常结束，异常结束，用户取消，任务都将处于结束状态。
 *
 * @author : xiudong.yuan@midea.com.cn
 * @date : 2016/5/26
 */
public abstract class CallableTask<Progress, Result> implements Callable<AsyncTaskResult<Result>> {

    /**
     * 任务进度更新回调
     */
    volatile TaskInternalCallback<Progress> taskCallback;

    /**
     * 任务取消
     */
    protected volatile boolean mCancelled;

    /**
     * 通知任务进度跟新
     *
     * @param progress 进度
     */
    @SuppressWarnings("unused")
    protected final void notifyProgressUpdate(Progress progress) {
        if (taskCallback != null) {
            taskCallback.onProgressUpdate(progress);
        }
    }

    /**
     * 取消任务
     */
    @CallSuper
    protected void onCancel() {
        mCancelled = true;
    }

    /**
     * 创建一个任务成功执行的结果
     *
     * @param result    用户结果
     * @param extraData 附带数据
     * @return 任务结果
     */
    @SuppressWarnings("unused")
    protected final AsyncTaskResult<Result> getSuccessTaskResult(Result result, Bundle extraData) {
        final AsyncTaskResult<Result> taskResult = new AsyncTaskResult<>();
        taskResult.setCode(AsyncTaskResult.CODE_SUCCESS);
        taskResult.setMessage("Task Success");
        taskResult.setResult(result);
        taskResult.setExtraData(extraData);
        return taskResult;
    }

    /**
     * 创建一个任务执行失败的任务结果
     *
     * @param errCode   错误码
     * @param errMsg    错误消息
     * @param extraData 附带数据
     * @return 执行结果
     */
    @SuppressWarnings("unused")
    protected final AsyncTaskResult<Result> getFailureTaskResult(int errCode, String errMsg, Bundle extraData) {
        final AsyncTaskResult<Result> taskResult = new AsyncTaskResult<>();
        taskResult.setCode(errCode);
        taskResult.setMessage(errMsg);
        taskResult.setExtraData(extraData);
        return taskResult;
    }

    @Override
    public abstract AsyncTaskResult<Result> call();

    /**
     * 任务回调
     *
     * @param <Progress> 进度类型
     * @hide
     */
    interface TaskInternalCallback<Progress> {

        /**
         * 进度更新
         *
         * @param progress 进度
         */
        void onProgressUpdate(Progress progress);
    }
}
