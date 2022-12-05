//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package util;

import cn.treeh.ToNX.util.PropsUtil;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.*;

public class DataBaseUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataBaseUtil.class);
    private static final String DRIVER;
    private static final String URL;
    private static final String USERNAME;
    private static final String PASSWORD;
    private static final BasicDataSource DATA_SOURCE;
    private static final QueryRunner QUERY_RUNNER = new QueryRunner();
    private static final ThreadLocal<Connection> CONNECTION_HOLDER = new ThreadLocal();

    public DataBaseUtil() {
    }

    public static void beginTransaction() {
        Connection connection = getConnection();
        if (connection != null) {
            try {
                connection.setAutoCommit(false);
            } catch (SQLException var5) {
                LOGGER.error("begin transaction failure", var5);
                throw new RuntimeException(var5);
            } finally {
                CONNECTION_HOLDER.set(connection);
            }
        }

    }

    public static void commitTransaciton() {
        Connection connection = getConnection();
        if (connection != null) {
            try {
                connection.commit();
                connection.close();
            } catch (SQLException var5) {
                LOGGER.error("commit transaction failure", var5);
                throw new RuntimeException(var5);
            } finally {
                CONNECTION_HOLDER.remove();
            }
        }

    }

    public static void rollbackTransaction() {
        Connection connection = getConnection();
        if (connection != null) {
            try {
                connection.rollback();
                connection.close();
            } catch (SQLException var5) {
                LOGGER.error("rollback transaction failure", var5);
                throw new RuntimeException(var5);
            } finally {
                CONNECTION_HOLDER.remove();
            }
        }

    }

    public static Connection getConnection() {
        Connection connection = (Connection)CONNECTION_HOLDER.get();
        if (connection == null) {
            try {
                connection = DATA_SOURCE.getConnection();
            } catch (SQLException var5) {
                LOGGER.error("get connection failure", var5);
                throw new RuntimeException(var5);
            } finally {
                CONNECTION_HOLDER.set(connection);
            }
        }

        return connection;
    }

    public static <T> List<T> queryEntityList(Class<T> entityClass, String sql, Object... params) {
        try {
            Connection connection = getConnection();
            List<T> entityList = (List)QUERY_RUNNER.query(connection, sql, new BeanListHandler(entityClass), params);
            return entityList;
        } catch (SQLException var5) {
            LOGGER.error("query entity list failure", var5);
            throw new RuntimeException(var5);
        }
    }

    public static <T> T queryEntity(Class<T> entityClass, String sql, Object... params) {
        try {
            Connection connection = getConnection();
            T entity = (T) QUERY_RUNNER.query(connection, sql, new BeanHandler(entityClass), params);
            return entity;
        } catch (SQLException var5) {
            LOGGER.error("query entity failure", var5);
            throw new RuntimeException(var5);
        }
    }

    public static List<Map<String, Object>> executeQuery(String sql, Object... params) {
        try {
            Connection connection = getConnection();
            List<Map<String, Object>> result = (List)QUERY_RUNNER.query(connection, sql, new MapListHandler(), params);
            return result;
        } catch (SQLException var4) {
            LOGGER.error("query entity map failure", var4);
            throw new RuntimeException(var4);
        }
    }

    public static int executeUpdate(String sql, Object... params) {
        try {
            Connection connection = getConnection();
            int rows = QUERY_RUNNER.update(connection, sql, params);
            return rows;
        } catch (SQLException var4) {
            LOGGER.error("execute update failure", var4);
            throw new RuntimeException(var4);
        }
    }

    public static <T> boolean insertEntity(Class<T> entityClass, Map<String, Object> fieldMap) {
        if (fieldMap.isEmpty()) {
            LOGGER.error("can't insert entity: fieldMap is empty");
            return false;
        } else {
            String sql = "INSERT INTO " + getTableName(entityClass);
            StringBuilder colums = new StringBuilder("(");
            StringBuilder values = new StringBuilder("(");
            Iterator var5 = fieldMap.keySet().iterator();

            while(var5.hasNext()) {
                String fieldName = (String)var5.next();
                colums.append(fieldName).append(", ");
                values.append("?, ");
            }

            colums.replace(colums.lastIndexOf(", "), colums.length(), ")");
            values.replace(values.lastIndexOf(", "), values.length(), ")");
            sql = sql + colums + "VALUES" + values;
            Object[] params = fieldMap.values().toArray();
            return executeUpdate(sql, params) == 1;
        }
    }

    public static boolean insertEntity(Object entity) {
        HashMap<String, Object> fieldMap = new HashMap();
        Field[] fields = entity.getClass().getFields();
        Field[] var4 = fields;
        int var5 = fields.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            Field field = var4[var6];
            if (field.isAnnotationPresent(DBField.class)) {
                DBField annotation = (DBField)field.getAnnotation(DBField.class);

                try {
                    if (annotation.field().equals("")) {
                        fieldMap.put(field.getName(), field.get(entity));
                    } else {
                        fieldMap.put(annotation.field(), field.get(entity));
                    }
                } catch (IllegalAccessException var9) {
                    JOptionPane.showConfirmDialog((Component)null, "奇怪的事情发生了1", "成功", -1);
                    throw new RuntimeException("Field should be public");
                }
            }
        }

        return insertEntity(entity.getClass(), fieldMap);
    }

    public static boolean updateEntity(Object entity, long id) {
        HashMap<String, Object> fieldMap = new HashMap();
        Field[] fields = entity.getClass().getFields();
        Field[] var6 = fields;
        int var7 = fields.length;

        for(int var8 = 0; var8 < var7; ++var8) {
            Field field = var6[var8];
            if (field.isAnnotationPresent(DBField.class)) {
                DBField annotation = (DBField)field.getAnnotation(DBField.class);

                try {
                    if (annotation.field().equals("")) {
                        fieldMap.put(field.getName(), field.get(entity));
                    } else {
                        fieldMap.put(annotation.field(), field.get(entity));
                    }
                } catch (IllegalAccessException var11) {
                    throw new RuntimeException("Field should be public");
                }
            }
        }

        return updateEntity(entity.getClass(), id, fieldMap);
    }

    public static <T> boolean updateEntity(Class<T> entityClass, long id, Map<String, Object> fieldMap) {
        if (fieldMap.isEmpty()) {
            LOGGER.error("can't update entity: fieldMap is empty");
            return false;
        } else {
            String sql = "UPDATE " + getTableName(entityClass) + " SET ";
            StringBuilder columns = new StringBuilder();
            Iterator var6 = fieldMap.keySet().iterator();

            while(var6.hasNext()) {
                String fieldName = (String)var6.next();
                columns.append(fieldName).append("=?, ");
            }

            sql = sql + columns.substring(0, columns.lastIndexOf(", ")) + " WHERE id=?";
            List<Object> paramList = new ArrayList();
            paramList.addAll(fieldMap.values());
            paramList.add(id);
            Object[] params = paramList.toArray();
            return executeUpdate(sql, params) == 1;
        }
    }

    public static <T> boolean deleteEntity(Class<T> entityClass, long id) {
        String var10000 = getTableName(entityClass);
        String sql = "DELETE FROM " + var10000 + " WHERE id=?";
        return executeUpdate(sql, id) == 1;
    }

    private static String getTableName(Class<?> entityClass) {
        return entityClass.getSimpleName();
    }

    public static void executeSqlFile(String filePath) {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            String sql;
            while((sql = reader.readLine()) != null) {
                executeUpdate(sql);
            }

        } catch (Exception var5) {
            LOGGER.error("execute sql file failure", var5);
            throw new RuntimeException(var5);
        }
    }

    static {
        Properties conf = PropsUtil.loadProps("config.properties");
        DRIVER = conf.getProperty("jdbc.driver");
        URL = conf.getProperty("jdbc.url");
        USERNAME = conf.getProperty("jdbc.username");
        PASSWORD = conf.getProperty("jdbc.password");

        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException var2) {
            LOGGER.error("can not load jdbc driver", var2);
        }

        DATA_SOURCE = new BasicDataSource();
        DATA_SOURCE.setDriverClassName(DRIVER);
        DATA_SOURCE.setUrl(URL);
        DATA_SOURCE.setUsername(USERNAME);
        DATA_SOURCE.setPassword(PASSWORD);
    }
}
