package net.alb.comm;

import net.alb.ALBException.ALBException;
import net.alb.config.Configuration;
import net.alb.sysmonitor.SystemStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by mymon_000 on 14-1-5.
 */
public class CommManager extends Thread {
    private Map<String, Boolean> neighborLife = new HashMap<String, Boolean>();
    //Save the socketchannel with established connection with neighbor server
    private List<SocketChannel> socketChannelAlive = new ArrayList<SocketChannel>();
    //Neighbor SystemStatus,Make sure it's thread safety
    private Map<String, SystemStatus> neighborStatusMap = new HashMap<String, SystemStatus>();
    private ReadWriteLock neighborStatusMapRWLocker = new ReentrantReadWriteLock();
    //Local System's current system status,make sure its' thread safety
    private SystemStatus currentStatus = new SystemStatus();
    private ReadWriteLock currentStatusRWLocker = new ReentrantReadWriteLock();
    //Send current system status to neighbor server's timespan
    private int commTimespan;

    private static Logger logger = LoggerFactory.getLogger(CommManager.class);

    public CommManager() {
        logger.debug("Communicator is initializing...");
        try {
            Configuration configuration = Configuration.getConfiguration();
            List<String> ipTotal = configuration.getIps();
            commTimespan = configuration.getCommTimespan();
            for (String ip : ipTotal) {
                neighborLife.put(ip, false);
            }
            Timer senderTimer = new Timer();
            senderTimer.schedule(new SenderTimer(), commTimespan, commTimespan);
        } catch (ALBException e) {
            logger.error("Load configuration error,{}.", e.getMessage());
            System.exit(-1);
        }
        logger.debug("Communicator initialization complete.");
    }

    public CommManager(String threadName) {
        super(threadName);
        logger.debug("Communicator is initializing...");
        try {
            Configuration configuration = Configuration.getConfiguration();
            List<String> ipTotal = configuration.getIps();
            commTimespan = configuration.getCommTimespan();
            for (String ip : ipTotal) {
                neighborLife.put(ip, false);
            }
            Timer senderTimer = new Timer();
            senderTimer.schedule(new SenderTimer(), commTimespan*1000, commTimespan*1000);
        } catch (ALBException e) {
            logger.error("Load configuration error,{}.", e.getMessage());
            System.exit(-1);
        }
        logger.debug("Communicator initialization complete.");
    }

    //TODO Checking the connection with other servers and maintain communication
    @Override
    public void run() {
        Selector selector;
        ServerSocketChannel serverSocketChannel;

        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(new InetSocketAddress(9999), 20);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            for (String neighborIp : neighborLife.keySet()) {
                SocketChannel socketChannel = SocketChannel.open();
                socketChannel.configureBlocking(false);
                socketChannel.register(selector, SelectionKey.OP_CONNECT,neighborIp);
                socketChannel.connect(new InetSocketAddress(neighborIp, 9999));
            }

            while (true) {
                int nkeys = selector.select(500);
                if (nkeys > 0) {
                    Set<SelectionKey> keys = selector.selectedKeys();
                    Iterator<SelectionKey> it = keys.iterator();
                    while (it.hasNext()) {
                        SelectionKey key = it.next();
                        if (key.isAcceptable()) {
                            handleAccept(key);
                        } else if (key.isConnectable()) {
                            handleConnect(key);
                        } else if (key.isReadable()) {
                            handleRead(key);
                        }
                        it.remove();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());       //This error may cause more big errors,we should exit the application immediately
            System.exit(-1);
        }
    }

    class SenderTimer extends TimerTask {
        @Override
        public void run() {
            for (SocketChannel socketChannel : socketChannelAlive) {
                SystemStatus statusNow = getCurrentStatus();
                try {
                    ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                    objectOutputStream.writeObject(statusNow);
                    socketChannel.write(ByteBuffer.wrap(byteArrayOutputStream.toByteArray()));
                } catch (IOException e) {
                    logger.warn(e.getMessage());
                }
            }
        }
    }

    private void handleAccept(SelectionKey key) {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        try {
            SocketChannel socketChannel = serverSocketChannel.accept();
            socketChannel.configureBlocking(false);
            String remoteAddress = socketChannel.getRemoteAddress().toString();
            String remoteIp = remoteAddress.substring(1, remoteAddress.indexOf(':'));
            if (neighborLife.keySet().contains(remoteIp)) {
                neighborLife.put(remoteIp, true);
                logger.info("Server:{} connected!", remoteIp);
                socketChannel.register(key.selector(), SelectionKey.OP_READ);
                socketChannelAlive.add(socketChannel);
                logger.debug("SocketChannelAlive Number:{},connection with {}..", socketChannelAlive.size(), remoteIp);
            } else {
                logger.warn("Host:{} connected,but not found in known server,reject it.");
                socketChannel.close();
            }
        } catch (IOException e) {
            logger.warn(e.getMessage());
        }
    }

    private void handleConnect(SelectionKey key) {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        String neighborIp = key.attachment().toString();
        try {
            if (socketChannel.isConnectionPending())
                socketChannel.finishConnect();
            socketChannelAlive.add(socketChannel);
            logger.debug("SocketChannelAlive Number:{},connection with {}.", socketChannelAlive.size(), neighborIp);
            socketChannel.register(key.selector(), SelectionKey.OP_READ);
        } catch (IOException e) {
            logger.debug("Connect to {} error.{}", neighborIp,e.getMessage());
        }
    }

    //FIXME Receive data from neighbor server and translate it to SystemStatus
    private void handleRead(SelectionKey key) {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        try {
            int readBytes = socketChannel.read(byteBuffer);
            if (readBytes > 0) {
                byteBuffer.flip();
                ObjectInputStream objectInputStream=new ObjectInputStream(new ByteArrayInputStream(byteBuffer.array()));
                SystemStatus neighborStatus=(SystemStatus)objectInputStream.readObject();
                String neighborAddress=socketChannel.getRemoteAddress().toString();
                String neighborIp=neighborAddress.substring(1,neighborAddress.indexOf(':'));
                updateNeighborStatusMap(neighborIp, neighborStatus);
            } else {
                socketChannelAlive.remove(socketChannel);
                socketChannel.close();
                logger.debug("Close connection with server {}.", socketChannel.getRemoteAddress());
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public Map<String, SystemStatus> getNeighborStatusMap() {
        neighborStatusMapRWLocker.readLock().lock();
        Map<String, SystemStatus> temp = new HashMap<String, SystemStatus>(neighborStatusMap);
        neighborStatusMapRWLocker.readLock().unlock();
        return temp;
    }

    //FIXME
    public void updateNeighborStatusMap(String neighborIp,SystemStatus neighborStatus) {
        neighborStatusMapRWLocker.writeLock().lock();
        neighborStatusMap.put(neighborIp,neighborStatus);
        neighborStatusMapRWLocker.writeLock().unlock();
    }

    public SystemStatus getCurrentStatus() {
        currentStatusRWLocker.readLock().lock();
        SystemStatus temp = new SystemStatus(currentStatus);
        currentStatusRWLocker.readLock().unlock();
        return temp;
    }

    public void setCurrentStatus(SystemStatus currentStatus) {
        currentStatusRWLocker.writeLock().lock();
        this.currentStatus = currentStatus;
        currentStatusRWLocker.writeLock().unlock();
    }
}
