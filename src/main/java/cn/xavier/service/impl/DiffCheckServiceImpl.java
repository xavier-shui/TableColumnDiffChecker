package cn.xavier.service.impl;

import cn.xavier.service.IDiffCheckService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.*;
import java.util.*;

/**
 * 省略了DAO层
 * @author Zheng-Wei Shui
 * @date 12/5/2021
 */
@Service
public class DiffCheckServiceImpl implements IDiffCheckService {

    //连接数据库的四大金刚
    @Value("${mysql.driver-class-name}")
    private String driverClassName;
    @Value("${mysql.jdbcUrl}")
    private String url;
    @Value("${mysql.username}")
    private String username;
    @Value("${mysql.password}")
    private String password;
    private String dbName;

    @PostConstruct
    private void init() {
        dbName = url.substring(url.lastIndexOf("/") + 1); // url中不含编码
    }

    private DatabaseMetaData dbmd;


    /**
     * 获取指定表中差异信息
     * @return
     */
    @Override
    public Map<String, String> diffCheck(Map<String, String> params){
        String table1 = params.get("table1");
        String table2 = params.get("table2");
        Map<String,String> compareResult = new LinkedHashMap<String,String>();
        PreparedStatement ps;
        Connection conn = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement(
                    "SELECT column_name FROM information_schema.Columns where table_name=? and table_schema=? AND column_name NOT IN (" +
                        "SELECT column_name FROM information_schema.Columns where table_name=? and table_schema=?)");
            ps.setString(1, table1);
            ps.setString(2, dbName);
            ps.setString(3, table2);
            ps.setString(4, dbName);
            ResultSet rs = ps.executeQuery();
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append(rs.getString(1)).append(", ");
            }
            if (sb.length() > 0) {
                compareResult.put("uniqueInLeft", sb.toString().substring(0, sb.length() - 2)); // 去除最后一个逗号
            }
            ps = conn.prepareStatement(
                    "SELECT column_name FROM information_schema.Columns where table_name=? and table_schema=? AND column_name NOT IN (" +
                        "SELECT column_name FROM information_schema.Columns where table_name=? and table_schema=?)");
            ps.setString(1, table2);
            ps.setString(2, dbName);
            ps.setString(3, table1);
            ps.setString(4, dbName);
            rs = ps.executeQuery();
            sb = new StringBuilder();
            while (rs.next()) {
                sb.append(rs.getString(1)).append(", ");
            }
            if (sb.length() > 0) {
                compareResult.put("uniqueInRight", sb.toString().substring(0, sb.length() - 2));
            }

            ps = conn.prepareStatement(
                    "SELECT column_name FROM information_schema.Columns where table_name=? and table_schema=? AND column_name IN (" +
                        "SELECT column_name FROM information_schema.Columns where table_name=? and table_schema=?)");
            ps.setString(1, table1);
            ps.setString(2, dbName);
            ps.setString(3, table2);
            ps.setString(4, dbName);
            rs = ps.executeQuery();
            sb = new StringBuilder();
            while (rs.next()) {
                sb.append(rs.getString(1)).append(", ");
            }
            if (sb.length() > 0) {
                compareResult.put("common", sb.toString().substring(0, sb.length() - 2));
            }
        } catch (SQLException e1) {
            System.out.println("语句对象创建异常!");
            e1.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return compareResult;
    }

    /**
     * 获取该数据库中所有的表组成的集合，保存的是数据库表的名称
     * @return
     */
    @Override
    public List<String> getAllTables(){
        List<String> tables = new ArrayList<String>();
        Connection conn = null;
        try {
            conn = getConnection();
            dbmd = conn.getMetaData();
            ResultSet rs = dbmd.getTables(null, "%","%",new String[]{"TABLE"});
            while(rs.next()){
                tables.add(rs.getString("TABLE_NAME"));
            }
        } catch (SQLException e) {
            System.out.println("获取表元数据异常!");
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return tables;
    }


    /**
     * 获取连接
     * @return
     */
    private Connection getConnection(){
        Connection conn = null;
        try {
            Class.forName(driverClassName);
            conn = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            System.out.println("驱动加载异常：是否忘记导入驱动包？ 驱动名写错？");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("获取连接异常：用户名和密码写错？ mysql服务尚未打开？ url格式错误？");
            e.printStackTrace();
        }
        return conn;
    }
}
