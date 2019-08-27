package nc.tool.zhongfa.mysql;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import nc.vo.pub.BusinessException;

/**
 * JDBC工具类
 * */
public class JDBCUtils {

	public static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";

	public String    URL = null;
	public String   PORT = null;
	public String DBNAME = null;
	public String   USER = null;
	public String    PWD = null;
	
	// 前缀变量
	public static final String Prefix_CHANGTAN = "changtan";	// 长滩
	public static final String Prefix_MANILA   = "manila";		// 马尼拉
	
	String Prefix = null;
	
	public JDBCUtils(String prefix) throws BusinessException {
		try {
			this.Prefix = prefix;
			getDBConfig(prefix);
		} catch (IOException e) {
			throw new BusinessException("读取配置文件出错:" + e.toString());
		}
	}

	public Connection getConn(String systype) throws BusinessException {
		Connection conn = null;
		try {
			Class.forName(MYSQL_DRIVER);
			conn = DriverManager.getConnection(
				"jdbc:mysql://" +
				URL+":"+PORT+"/"+DBNAME+"?characterEncoding=GBK",
				USER,
				PWD);
		} catch (ClassNotFoundException e) {
			throw new BusinessException(e.toString());
		} catch (SQLException e) {
			throw new BusinessException(e.toString());
		}
		return conn;
	}

	public void getDBConfig(String prefix) throws IOException {
		InputStream inStream = JDBCUtils.class
				.getClassLoader()
				.getResourceAsStream(
						"nc/tool/zhongfa/mysql/dbconfiginfo.properties");
		Properties p = new Properties();
		p.load(inStream);

		URL  = p.getProperty(prefix + "_url");
		PORT = p.getProperty(prefix + "_port");
		DBNAME = p.getProperty(prefix + "_dbname");
		USER = p.getProperty(prefix + "_user");
		PWD  = p.getProperty(prefix + "_pwd");
	}

	public void closeConn(Connection conn) throws BusinessException {
		if (conn != null) {
			try {
				if(!conn.isClosed()){
					conn.close();
				}
			} catch (SQLException e) {
				throw new BusinessException(e.toString());
			}
		}
	}

	public void closeStat(Statement stat) throws BusinessException {
		if (null != stat) {
			try {
				stat.close();
			} catch (SQLException e) {
				throw new BusinessException(e.toString());
			}
		}
	}

	public void rollBack(Connection conn) throws BusinessException {
		if (conn != null) {
			try {
				conn.rollback();
			} catch (SQLException e) {
				throw new BusinessException(e.toString());
			}
		}
	}
}
