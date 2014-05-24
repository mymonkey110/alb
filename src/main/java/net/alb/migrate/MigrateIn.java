package net.alb.migrate;

/**
 * Created by mymon_000 on 14-2-12.
 */
public abstract class MigrateIn extends Thread {
    public MigrateIn(String threadName) {
        super(threadName);
    }

    public abstract void in() ;

    @Override
    public void run() {
        in();
    }
}
