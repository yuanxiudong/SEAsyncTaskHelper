# SEAsyncTaskHelper

是一套简单的任务提交工具。支持同步和异步任务返回。

### 思路
本组件一个四个核心的类：
- AsyncTaskHelper：任务提交执行组件，用于提交任务。
- CallableTask: 任务类，用于封装用户的任务。
- AsyncTaskSession：任务执行周期，用于通过返回任务结果以及取消任务。
- AsyncTaskResult：任务执行结果。

### 如何使用

1. new 一个AsyncTaskHelper
2. new一个CallableTask
3. 调用AsyncTaskHelper#submit方法提交这个task并返回AsyncTaskSession。提交的时候可选参数
    - SETaskCallback  异步执行回调
4. 取消任务调用AsyncTaskSession#cancel方法
6. 调用AsyncTaskSession#getTaskResult得到任务同步返回结果。
7. 用户任务如果包含进度更新，可以调用CallableTask#notifyProgressUpdate实现执行进度上报。

使用案例参照单元测试用例。