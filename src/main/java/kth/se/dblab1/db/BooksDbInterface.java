package kth.se.dblab1.db;

import kth.se.dblab1.model.Author;
import kth.se.dblab1.model.Book;
import kth.se.dblab1.model.Review;

import java.sql.SQLException;
import java.util.List;

/**
 * This interface declares methods for querying a Books database.
 * Different implementations of this interface handles the connection and
 * queries to a specific DBMS and database, for example a MySQL or a MongoDB
 * database.
 *
 * NB! The methods in the implementation must catch the SQL/MongoDBExceptions thrown
 * by the underlying driver, wrap in a BooksDbException and then re-throw the latter
 * exception. This way the interface is the same for both implementations, because the
 * exception type in the method signatures is the same. More info in BooksDbException.java.
 * 
 * @author anderslm@kth.se
 */
public interface BooksDbInterface {
    
    /**
     * Connect to the database.
     * @param database
     * @return true on successful connection.
     */
    public void connect(String database) throws BooksDbException;
    public void disconnect() throws BooksDbException, SQLException;
    
    public List<Book> searchBooksByTitle(String title, int limit) throws BooksDbException;
    public List<Book> searchBooksByIsbn(String isbn, int limit) throws BooksDbException;
    public List<Book> searchBooksByAuthorName(String authorName, int limit) throws BooksDbException;
    public List<Book> searchBooksByRate(int rate, int limit) throws BooksDbException;
    public List<Book> searchBooksByGenre(String genre, int limit) throws BooksDbException;
    public List<Author> getAuthorByPersonId(String personId, int limit) throws BooksDbException;
    public List<Author> getBookAuthors(Book book, int limit) throws BooksDbException;
    public List<String> getBookGenres(Book book, int limit) throws BooksDbException;
    public List<Author> getAuthorByName(String name, int limit) throws BooksDbException;
    public List<Review> getBookReview(Book book) throws BooksDbException;

    public void insertBookFullDetail(Book book, List<Author> authors, List<String> genre) throws BooksDbException, SQLException;
    public int insertAuthor(Author author) throws BooksDbException;
    public int insertReview(Book book, int rate, String review) throws BooksDbException;

    public int updateBookInfo(Book book) throws BooksDbException;
    public void updateBookFullDetail(Book book, List<Author> authors , List<String> genre) throws BooksDbException, SQLException;

    public int deleteBookByIsbn(String isbn) throws BooksDbException;
}
