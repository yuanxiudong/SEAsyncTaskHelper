package com.seagle.android.task.helper;

import android.os.AsyncTask;

/**
 * <h1>异步任务协助类</h1>
 * <p>包装了AsyncTask,提供一套任务跟踪和取消机制</P>
 *
 * @author : xiudong.yuan@midea.com.cn
 * @date : 2016/5/26
 */
public final class AsyncTaskHelper {

    /**
     * 是否使用AsyncTask的默认线程池执行任务。
     * 默认线程池是单线程的。
     */
    private final boolean mSerialExecutor;

    public AsyncTaskHelper(boolean serialExecutor) {
        mSerialExecutor = serialExecutor;
    }

    /**
     * 提交一个异步任务
     *
     * @param task       异步任务
     * @param callback   任务回调
     * @param <Progress> 进度类型
     * @param <Result>   结果类型
     * @return 请求Session
     */
    @SuppressWarnings("unused")
    public <Progress, Result> AsyncTaskSession<Progress, Result> submitTask(final CallableTask<Progress, Result> task,
                                                                            final AsyncTaskCallback<Progress, Result> callback) {
        final AsyncTaskSession<Progress, Result> taskSession = new AsyncTaskSession<>();
        AsyncTask<Object, Progress, AsyncTaskResult<Result>> asyncTask = new AsyncTask<Object, Progress, AsyncTaskResult<Result>>() {
            @Override
            protected AsyncTaskResult<Result> doInBackground(Object... params) {
                try {
                    task.taskCallback = new CallableTask.TaskInternalCallback<Progress>() {
                        @SuppressWarnings("unchecked")
                        @Override
                        public void onProgressUpdate(Progress progress) {
                            publishProgress(progress);
                        }
                    };
                    return task.call();
                } catch (Exception e) {
                    if (!isCancelled()) {
                        AsyncTaskResult<Result> taskResult = new AsyncTaskResult<>();
                        taskResult.setCode(AsyncTaskResult.CODE_FAILURE);
                        taskResult.setMessage(e.getMessage());
                        return taskResult;
                    }
                }
                return null;
            }


            @Override
            protected void onPostExecute(AsyncTaskResult<Result> result) {
                if (result != null) {
                    taskSession.callOnTaskComplete(result);
                }
            }

            @SafeVarargs
            @Override
            protected final void onProgressUpdate(Progress... values) {
                if (values != null && values.length > 0) {
                    taskSession.callOnProgressUpdate(values[0]);
                }
            }

            @Override
            protected void onCancelled() {
                task.onCancel();
                taskSession.callOnCancelled();
            }

        };
        taskSession.setAsyncTask(asyncTask);
        taskSession.setTaskCallback(callback);
        if (mSerialExecutor) {
            asyncTask.execute();
        } else {
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        return taskSession;
    }
}
