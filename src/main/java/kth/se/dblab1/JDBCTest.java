package kth.se.dblab1;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
public class JDBCTest {
    public static void main(String[] args) throws Exception {
        String user = System.getenv("env_user"); // username
        String pwd = System.getenv("env_pwd"); // password
        String endpoint = System.getenv("env_endpoint");
        String database = "test"; // the name of the specific database
        String server = "jdbc:mysql://"+endpoint+":3306/"+ database + "?UseClientEnc=UTF8";
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(server, user, pwd);
            System.out.println("Connected!");
            executeQuery(con, "SELECT * FROM class;");
        } finally {
            try {
                if (con != null) {
                    con.close();
                    System.out.println("Connection closed.");
                }
            } catch (SQLException e) {
            }
        }
    }
    public static void executeQuery(Connection con, String query) throws
            SQLException {
        try (Statement stmt = con.createStatement()) {
            // Execute the SQL statement
            ResultSet rs = stmt.executeQuery(query);
            // Get the attribute names
            ResultSetMetaData metaData = rs.getMetaData();
            int ccount = metaData.getColumnCount();
            for (int c = 1; c <= ccount; c++) {
                System.out.print(metaData.getColumnName(c) + "\t");
            }
            System.out.println();
            // Get the attribute values
            while (rs.next()) {
                // NB! This is an example, -not- the preferred way to retrieve data.
                // You should use methods that return a specific data type, like
                // rs.getInt(), rs.getString() or such.
                // It's also advisable to store each tuple (row) in an object of
                // custom type (e.g. Employee).
                for (int c = 1; c <= ccount; c++) {
                    System.out.print(rs.getObject(c) + "\t");
                }
                System.out.println();
            }
        }
    }
}