package kth.se.dblab1.model;

import java.util.List;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class BookDbMySqlImpl implements BooksDbInterface{
    private Connection conn = null;

    @Override
    public boolean connect(String database) throws BooksDbException {
        String user = System.getenv("env_user"); // username
        String pwd = System.getenv("env_pwd"); // password
        String endpoint = System.getenv("env_endpoint"); //link to database, database endpoint
        String server = "jdbc:mysql://" + endpoint + ":3306/" + database + "?UseClientEnc=UTF8";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(server, user, pwd);
            System.out.println("Connected!");
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void disconnect() throws BooksDbException {
        try {
            if (conn == null)
                throw new BooksDbException("No connection");
            conn.close();
            System.out.println("Connection closed.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new BooksDbException("someting gone wtrong");
        }
    }

    @Override
    public List<Book> searchBooksByTitle(String title) throws BooksDbException {
        return null;
    }

    public void executeQuery(String query) throws
            SQLException {
        try (Statement stmt = conn.createStatement()) {
            // Execute the SQL statement
            ResultSet rs = stmt.executeQuery(query);
            // Get the attribute names
            ResultSetMetaData metaData = rs.getMetaData();
            int count = metaData.getColumnCount();
            for (int c = 1; c <= count; c++) {
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
                for (int c = 1; c <= count; c++) {
                    System.out.print(rs.getObject(c) + "\t");
                }
                System.out.println();
            }
        }
    }
}
