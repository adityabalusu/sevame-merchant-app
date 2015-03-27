package in.geekvalet.sevame;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by gautam on 24/6/14.
 */
public class TaskManager {
    private static final int KEEP_ALIVE_TIME = 60;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
    private static final int MAXIMUM_POOL_SIZE = 3;
    private static final int MIN_POOL_SIZE = 1;
    private static final int TASK_COMPLETE = 1;
    private static final int TASK_FAILED = 2;
    private final UIHandler handler;

    public static abstract class Task<T> {

        private Thread thread;
        private boolean running;
        private Throwable throwable;
        private T result;
        private Handler handler;

        public Task() {
            this.running = false;
            this.thread = null;
        }

        protected void runWrapper() {
            this.thread = Thread.currentThread();

            ensureNotRunning();

            try {
                result = run();
                Message msg = handler.obtainMessage(TASK_COMPLETE, this);
                msg.sendToTarget();
            } catch (Throwable t) {
                throwable = t;
                Message msg = handler.obtainMessage(TASK_FAILED, this);
                msg.sendToTarget();
            } finally {
                this.thread = null;
            }
        }

        private void ensureNotRunning() {
            synchronized (this) {
                if(this.running) {
                    throw new RuntimeException("Trying to use the same task on two threads at the same time? Bad Boy!");
                }

                this.running = true;
            }
        }

        protected void onFailWrapper() {
            try {
                onFail(throwable);
            } finally {
                running = false;
            }
        }

        protected void onSuccessWrapper() {
            try {
                onSuccess(result);
            } finally {
                running = false;
            }
        }

        public void tryCancel() {
            this.thread.interrupt();
        }

        public boolean isRunning() {
            return running;
        }

        public void execute(TaskManager taskManager) {
            taskManager.run(this);
        }

        public abstract T run();

        public abstract void onSuccess(T result);

        public abstract void onFail(Throwable e);

        public void setHandler(Handler handler) {
            this.handler = handler;
        }
    }

    private class UIHandler extends android.os.Handler {
        public UIHandler() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            Task task = (Task) msg.obj;

            switch (msg.what) {
                case TASK_COMPLETE:
                    task.onSuccessWrapper();
                    break;

                case TASK_FAILED:
                    task.onFailWrapper();
                    break;

                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    private final BlockingQueue<Runnable> workQueue;
    private final ThreadPoolExecutor threadPool;

    TaskManager() {
        workQueue = new LinkedBlockingQueue<Runnable>();
        threadPool = new ThreadPoolExecutor(
                MIN_POOL_SIZE,       // Initial pool size
                MAXIMUM_POOL_SIZE,       // Max pool size
                KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT,
                workQueue);
        handler = new UIHandler();
    }

    public void run(final Task task) {
        task.setHandler(handler);
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                task.runWrapper();
            }
        });
    }
}