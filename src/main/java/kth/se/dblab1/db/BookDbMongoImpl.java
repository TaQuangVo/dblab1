package kth.se.dblab1.db;


import com.mongodb.Block;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import kth.se.dblab1.model.Author;
import kth.se.dblab1.model.Book;
import kth.se.dblab1.model.Review;
import org.bson.Document;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BookDbMongoImpl implements BooksDbInterface {
    private MongoClient client;
    private MongoDatabase db;
    @Override

    public void connect(String dbName) throws BooksDbException {
        try {
            this.client = MongoClients.create("mongodb://root:1234598765@ac-ihzdebw-shard-00-00.srmuvwk.mongodb.net:27017,ac-ihzdebw-shard-00-01.srmuvwk.mongodb.net:27017,ac-ihzdebw-shard-00-02.srmuvwk.mongodb.net:27017/?ssl=true&replicaSet=atlas-v8zayh-shard-0&authSource=admin&retryWrites=true&w=majority");//auto connect on
            //this.db = client.getDatabase("sample_airbnb");
            //MongoCollection<Document> collection = this.db.getCollection("listingsAndReviews");
            //collection.find().forEach((Block<? super Document>) doc -> System.out.println(doc.toJson()));

            this.db = client.getDatabase("bookdb");
            System.out.println("connected to db");
        } catch (Exception e) {
            throw new BooksDbException("Error connecting to the database: " + e.getMessage(), e);
        }
    }




    @Override
    public void disconnect() throws BooksDbException, SQLException {
        this.client.close();
    }

    @Override
    public List<Book> searchBooksByTitle(String title, int limit) throws BooksDbException {
        return null;
    }

    @Override
    public List<Book> searchBooksByIsbn(String isbn, int limit) throws BooksDbException {
        return null;
    }

    @Override
    public List<Book> searchBooksByAuthorName(String authorName, int limit) throws BooksDbException {
        return null;
    }

    @Override
    public List<Book> searchBooksByRate(int rate, int limit) throws BooksDbException {
        return null;
    }

    @Override
    public List<Book> searchBooksByGenre(String genre, int limit) throws BooksDbException {
        return null;
    }

    @Override
    public List<Author> getAuthorByPersonId(String personId, int limit) throws BooksDbException {
        return null;
    }

    @Override
    public List<Author> getBookAuthors(Book book, int limit) throws BooksDbException {
        return null;
    }

    @Override
    public List<String> getBookGenres(Book book, int limit) throws BooksDbException {
        return null;
    }

    @Override
    public List<Author> getAuthorByName(String name, int limit) throws BooksDbException {
        return null;
    }

    @Override
    public List<Review> getBookReview(Book book) throws BooksDbException {
        return null;
    }

    @Override
    public void insertBookFullDetail(Book book, List<Author> authors, List<String> genre) throws BooksDbException, SQLException {

    }

    @Override
    public int insertAuthor(Author author) throws BooksDbException {
        return 0;
    }

    @Override
    public int insertReview(Book book, int rate, String review) throws BooksDbException {
        return 0;
    }

    @Override
    public int updateBookInfo(Book book) throws BooksDbException {
        return 0;
    }

    @Override
    public void updateBookFullDetail(Book book, List<Author> authors, List<String> genre) throws BooksDbException, SQLException {

    }

    @Override
    public int deleteBookByIsbn(String isbn) throws BooksDbException {
        return 0;
    }
}
