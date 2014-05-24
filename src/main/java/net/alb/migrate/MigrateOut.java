package net.alb.migrate;

/**
 * Created by mymon_000 on 14-1-16.
 */
public abstract class MigrateOut extends Thread {

    public MigrateOut(String threadName) {
        super(threadName);
    }

    public abstract void out();

    @Override
    public void run() {
        out();
    }
}
