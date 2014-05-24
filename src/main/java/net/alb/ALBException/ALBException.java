package net.alb.ALBException;

/**
 * Created by mymon_000 on 13-12-27.
 */
public class ALBException extends Exception {
    public ALBException() {
        super();
    }

    public ALBException(String errMsg) {
        super(errMsg);
    }

    public ALBException(String errMsg, Throwable cause) {
        super(errMsg, cause);
    }

    public ALBException(Throwable cause) {
        super(cause);
    }
}
