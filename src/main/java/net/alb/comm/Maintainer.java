package net.alb.comm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by mymon_000 on 14-1-5.
 */
public class Maintainer extends Thread {
    private Socket conn=null;
    private String remoteIp=null;
    private int remotePort;

    private static Logger logger= LoggerFactory.getLogger(Maintainer.class);

    public Maintainer(String remoteIp,int port,int timeout) throws IOException {
        this.remoteIp=remoteIp;
        this.remotePort=port;
        conn=new Socket();
        SocketAddress address=new InetSocketAddress(remoteIp,remotePort);
        conn.connect(address,timeout);
    }

    //TODO Not finished yet!!!
    @Override
    public void run() {
        try{
            PrintWriter printWriter=new PrintWriter(conn.getOutputStream());
        }catch (IOException  e) {
            logger.warn("GetOutputStream from connection with {} error:",remoteIp);
        }


    }

}
