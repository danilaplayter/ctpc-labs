package by.iba.connection;

import by.iba.config.AppConfig;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConnectionPool {

    private static final ReentrantLock LOCK = new ReentrantLock();
    private static volatile ConnectionPool connectionPool;

    private final ArrayBlockingQueue<Connection> freeConnections;
    private final ArrayBlockingQueue<Connection> releaseConnections;

    public static ConnectionPool getInstance() {
        LOCK.lock();
        try {
            if (connectionPool == null) {
                connectionPool = new ConnectionPool();
            }
        } catch (Exception e) {
            log.error("Can not get instance", e);
            throw new RuntimeException("Can not get instance", e);
        } finally {
            LOCK.unlock();
        }
        return connectionPool;
    }

    private ConnectionPool() {
        AppConfig config = AppConfig.getInstance();
        this.freeConnections = new ArrayBlockingQueue<>(config.getPoolSize());
        this.releaseConnections = new ArrayBlockingQueue<>(config.getPoolSize());
        try {
            DriverManager.registerDriver(
                    (Driver)
                            Class.forName(config.getDriver())
                                    .getDeclaredConstructor()
                                    .newInstance());
            init(config);
        } catch (Exception e) {
            throw new RuntimeException("Pool can not initialize", e);
        }
    }

    private void init(AppConfig config) throws SQLException {
        for (int i = 0; i < config.getPoolSize(); i++) {
            Connection connection =
                    DriverManager.getConnection(
                            config.getUrl(), config.getUser(), config.getPassword());
            freeConnections.add(connection);
        }
    }

    public Connection getConnection() {
        try {
            Connection connection = freeConnections.take();
            releaseConnections.offer(connection);
            log.info("Connection was taken, free connections left: {}", freeConnections.size());
            return connection;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Can not get connection", e);
        }
    }

    public void releaseConnection(Connection connection) {
        releaseConnections.remove(connection);
        freeConnections.offer(connection);
        log.info("Connection was released, free connections: {}", freeConnections.size());
    }

    public void destroy() {
        for (int i = 0; i < freeConnections.size(); i++) {
            try {
                Connection connection = freeConnections.take();
                connection.close();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Connection close interrupted", e);
            } catch (SQLException e) {
                log.error("Database is not closed", e);
            }
        }
        try {
            Enumeration<Driver> drivers = DriverManager.getDrivers();
            while (drivers.hasMoreElements()) {
                DriverManager.deregisterDriver(drivers.nextElement());
            }
        } catch (SQLException e) {
            log.error("Drivers were not deregistered", e);
        }
    }
}
