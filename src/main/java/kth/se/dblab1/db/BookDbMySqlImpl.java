package kth.se.dblab1.db;

import kth.se.dblab1.model.Book;

import java.sql.*;
import java.util.List;

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
        try (Statement stmt = conn.createStatement()) {
            // Execute the SQL statement
            String queryString = "Select * from book where title = '" + title+"';";
            PreparedStatement pstmt = conn.prepareStatement(queryString);
            ResultSet rs = pstmt.executeQuery();
            // Get the attribute names
            return Book.map(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Book> searchBooksByIsbn(String isbn) throws BooksDbException {
        try (Statement stmt = conn.createStatement()) {
            // Execute the SQL statement
            String queryString = "Select * from book where isbn = '" + isbn+"';";
            PreparedStatement pstmt = conn.prepareStatement(queryString);
            ResultSet rs = pstmt.executeQuery();
            // Get the attribute names
            return Book.map(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Book> searchBooksByAuthorName(String authorName) throws BooksDbException {
        try (Statement stmt = conn.createStatement()) {
            // Execute the SQL statement
            String queryString = "select * from book where id in (select book_id from writen_by where author_id = (select id from author where name = '"+authorName +"'));";
            PreparedStatement pstmt = conn.prepareStatement(queryString);
            ResultSet rs = pstmt.executeQuery();
            // Get the attribute names
            return Book.map(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Book> searchBooksByRate(int rate) throws BooksDbException {
        try (Statement stmt = conn.createStatement()) {
            // Execute the SQL statement
            String queryString = "select * from book where id in (select book_id from rate where rate = " + rate +");";
            PreparedStatement pstmt = conn.prepareStatement(queryString);
            ResultSet rs = pstmt.executeQuery();
            // Get the attribute names
            return Book.map(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Book> searchBooksByGenre(String genre) throws BooksDbException {
        try (Statement stmt = conn.createStatement()) {
            // Execute the SQL statement
            String queryString = "select * from book where id in (select book_id from genre where genre_title = '" + genre +"');";
            PreparedStatement pstmt = conn.prepareStatement(queryString);
            ResultSet rs = pstmt.executeQuery();
            // Get the attribute names
            return Book.map(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
