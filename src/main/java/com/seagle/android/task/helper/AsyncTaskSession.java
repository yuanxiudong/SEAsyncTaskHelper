package com.seagle.android.task.helper;

import android.os.AsyncTask;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * <h1>请求回话</h1>
 * <p>这个类提供一套任务的跟踪，同步，取消机制</P>
 *
 * @author : yuanxiudong66@sina.com
 */
public final class AsyncTaskSession<Progress, Result> {

    /**
     * 任务是否完成
     */
    private volatile boolean mCompleted;

    /**
     * 任务
     */
    private volatile AsyncTask<Object, Progress, AsyncTaskResult<Result>> mAsyncTask;

    /**
     * 任务回调
     */
    private volatile AsyncTaskCallback<Progress, Result> mTaskCallback;

    /**
     * final的Class和包访问权限的构造方法，保证了这个类不能够被外部修改。
     */
    AsyncTaskSession() {
        mCompleted = false;
    }

    /**
     * 获取任务结果。
     * 在任务完成之前，调用这个接口会阻塞
     *
     * @return 任务结果
     */
    @SuppressWarnings("unused")
    public final AsyncTaskResult<Result> getTaskResult() {
        if (mAsyncTask == null || mAsyncTask.isCancelled()) {
            return null;
        }
        try {
            AsyncTaskResult<Result> taskResult = mAsyncTask.get();
            mCompleted = true;
            return taskResult;
        } catch (InterruptedException | ExecutionException e) {
            AsyncTaskResult<Result> taskResult = new AsyncTaskResult<>();
            taskResult.setCode(AsyncTaskResult.CODE_FAILURE);
            taskResult.setMessage(e.getMessage());
            mCompleted = true;
            return taskResult;
        }
    }

    /**
     * 获取任务结果。
     * 在任务完成之前，调用这个接口会阻塞。
     * 如果获取结果超时不取消任务，还可以继续调用这个接口，回调还能正常收到回调。
     *
     * @param timeout    超时时间
     * @param unit       超时时间单位
     * @param cancelTask 获取任务结果超时是否取消任务
     * @return 任务结果
     */
    @SuppressWarnings("unused")
    public final AsyncTaskResult<Result> getTaskResult(int timeout, TimeUnit unit, boolean cancelTask) {
        if (mAsyncTask == null || mAsyncTask.isCancelled()) {
            return null;
        }
        try {
            AsyncTaskResult<Result> taskResult = mAsyncTask.get(timeout, unit);
            mCompleted = true;
            return taskResult;
        } catch (InterruptedException | ExecutionException e) {
            AsyncTaskResult<Result> taskResult = new AsyncTaskResult<>();
            taskResult.setCode(AsyncTaskResult.CODE_FAILURE);
            taskResult.setMessage(e.getMessage());
            mCompleted = true;
            return taskResult;
        } catch (TimeoutException e) {
            AsyncTaskResult<Result> taskResult = new AsyncTaskResult<>();
            taskResult.setCode(AsyncTaskResult.CODE_TIMEOUT);
            taskResult.setMessage(e.getMessage());
            if (cancelTask) {
                mCompleted = true;
                mAsyncTask.cancel(true);
            }
            return taskResult;
        }
    }

    /**
     * 取消任务
     */
    @SuppressWarnings("unused")
    public final void cancelTask() {
        if (mAsyncTask != null && !mAsyncTask.isCancelled() && !mCompleted) {
            mAsyncTask.cancel(true);
        }
    }

    /**
     * 任务是否完成
     *
     * @return true or false
     */
    @SuppressWarnings("unused")
    public final boolean isCompleted() {
        return mCompleted;
    }

    /**
     * 判断任务是否取消
     *
     * @return true or false
     */
    public final boolean isCancelled() {
        return (mAsyncTask == null || mAsyncTask.isCancelled());
    }

    /**
     * 设置任务回调
     *
     * @param callback 回调
     */
    protected final void setTaskCallback(AsyncTaskCallback<Progress, Result> callback) {
        if (!mCompleted && !isCancelled()) {
            mTaskCallback = callback;
        }
    }

    /**
     * 设置异步任务
     *
     * @param asyncTask 异步任务
     */
    protected final void setAsyncTask(AsyncTask<Object, Progress, AsyncTaskResult<Result>> asyncTask) {
        mAsyncTask = asyncTask;
    }

    /**
     * 通知进度更新。
     * 运行在主线程
     *
     * @param progress 进度
     */
    protected final void callOnProgressUpdate(Progress progress) {
        if (!mCompleted && mTaskCallback != null) {
            mTaskCallback.onProgressUpdate(progress);
        }
    }

    /**
     * 通知任务执行完毕
     * 运行在主线程
     *
     * @param taskResult 任务结果
     */
    protected final void callOnTaskComplete(AsyncTaskResult<Result> taskResult) {
        if (!mCompleted && mAsyncTask != null && !mAsyncTask.isCancelled()) {
            mCompleted = true;
            if (mTaskCallback != null) {
                if (AsyncTaskResult.CODE_SUCCESS == taskResult.getCode()) {
                    mTaskCallback.onComplete(taskResult.getResult());
                } else {
                    mTaskCallback.onError(taskResult.getCode(), taskResult.getMessage(), taskResult.getExtraData());
                }
                mTaskCallback = null;
            }
        }
    }

    /**
     * 通知任务取消。
     * 运行在主线程
     */
    protected final void callOnCancelled() {
        if (!mCompleted) {
            mCompleted = true;
            if (mTaskCallback != null) {
                mTaskCallback.onTaskCancelled();
                mTaskCallback = null;
            }
        }
    }
}
