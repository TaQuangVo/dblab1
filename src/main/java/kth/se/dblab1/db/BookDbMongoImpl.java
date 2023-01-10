package kth.se.dblab1.db;


import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.QueryBuilder;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import kth.se.dblab1.model.Author;
import kth.se.dblab1.model.Book;
import kth.se.dblab1.model.Review;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.regex;

public class BookDbMongoImpl implements BooksDbInterface {
    private MongoClient client;
    private MongoDatabase db;
    @Override

    public void connect(String dbName) throws BooksDbException {
        try {
            this.client = MongoClients.create("mongodb://root:1234598765@ac-ihzdebw-shard-00-00.srmuvwk.mongodb.net:27017,ac-ihzdebw-shard-00-01.srmuvwk.mongodb.net:27017,ac-ihzdebw-shard-00-02.srmuvwk.mongodb.net:27017/?ssl=true&replicaSet=atlas-v8zayh-shard-0&authSource=admin&retryWrites=true&w=majority");//auto connect on

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
        try{
            MongoCollection<Document> collection = this.db.getCollection("books");

            BasicDBObject regexQuery = new BasicDBObject();
            regexQuery.put("title", new BasicDBObject("$regex", title + ".*").append("$options", "i"));
            FindIterable<Document> iterable = collection.find(regexQuery);
            List<Book> books = Book.mapFromFind(iterable);
            return books;
        } catch (Exception e) {
            throw new BooksDbException(e.getMessage());
        }
    }

    @Override
    public List<Book> searchBooksByIsbn(String isbn, int limit) throws BooksDbException {
        try{
            MongoCollection<Document> collection = this.db.getCollection("books");
            FindIterable find = collection.find(eq("_id",isbn));
            List<Book> bs = Book.mapFromFind(find);
            return bs;
        } catch (Exception e) {
            throw new BooksDbException(e.getMessage());
        }
    }

    @Override
    public List<Book> searchBooksByAuthorName(String authorName, int limit) throws BooksDbException {
        try{

            MongoCollection<Document> collection = this.db.getCollection("authors");
            FindIterable find = collection.find(eq("name",authorName));
            List<Author> authors = Author.mapFromFind(find);

            List<String> authors_id = new ArrayList<>();
            for(Author a: authors)
                authors_id.add(a.getPersonId());

            MongoCollection<Document> bookCol = this.db.getCollection("books");
            FindIterable findb = bookCol.find(Filters.in("authors", authors_id));
            List<Book> books = Book.mapFromFind(findb);

            return books;
        } catch (Exception e) {
            throw new BooksDbException(e.getMessage());
        }
    }

    @Override
    public List<Book> searchBooksByRate(int rate, int limit) throws BooksDbException {
        return null;
    }

    @Override
    public List<Book> searchBooksByGenre(String genre, int limit) throws BooksDbException {
        try{
            MongoCollection<Document> collection = this.db.getCollection("books");
            FindIterable find = collection.find(eq("genre",genre));

            return Book.mapFromFind(find);
        } catch (Exception e) {
            throw new BooksDbException(e.getMessage());
        }
    }

    @Override
    public List<Author> getAuthorByPersonId(String personId, int limit) throws BooksDbException {
        try{
            MongoCollection<Document> collection = this.db.getCollection("authors");
            FindIterable find = collection.find(eq("_id",personId));

            return Author.mapFromFind(find);
        } catch (Exception e) {
            throw new BooksDbException(e.getMessage());
        }
    }

    @Override
    public List<Author> getBookAuthors(Book book, int limit) throws BooksDbException {
        try{
            MongoCollection<Document> collection = this.db.getCollection("authors");
            FindIterable find = collection.find(Filters.in("_id", book.getAuthors()));

            return Author.mapFromFind(find);
        } catch (Exception e) {
            throw new BooksDbException(e.getMessage());
        }
    }

    @Override
    public List<String> getBookGenres(Book book, int limit) throws BooksDbException {
        return book.getGenre();
    }

    @Override
    public List<Author> getAuthorByName(String name, int limit) throws BooksDbException {
        try{
            MongoCollection<Document> collection = this.db.getCollection("authors");

            BasicDBObject regexQuery = new BasicDBObject();
            regexQuery.put("name", new BasicDBObject("$regex", name + ".*").append("$options", "i"));
            FindIterable<Document> iterable = collection.find(regexQuery);
            List<Author> authors = Author.mapFromFind(iterable);

            return authors;
        } catch (Exception e) {
            throw new BooksDbException(e.getMessage());
        }
    }

    @Override
    public List<Review> getBookReview(Book book) throws BooksDbException {
        return new ArrayList<>();
    }

    @Override
    public void insertBookFullDetail(Book books, List<Author> authors, List<String> genre) throws BooksDbException, SQLException {
        MongoCollection<Document> bookCol = this.db.getCollection("books");
        MongoCollection<Document> authorCol = this.db.getCollection("authors");
        try {
            //insert author if not exist
            for (Author author : authors){
                List<Author> a = getAuthorByPersonId(author.getPersonId(),1);
                if(a.isEmpty()){
                    insertAuthor(a.get(0));
                }
            }

            //insert books
            this.insertBook(books, authors, genre);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void insertBook(Book book, List<Author> authors,  List<String> genre)throws BooksDbException{
        try{
            List<String> auList = new ArrayList<>();
            for (Author author : authors)
                auList.add(author.getPersonId());

            MongoCollection<Document> collection = this.db.getCollection("books");
            Document document = new Document("_id",book.getIsbn())
                    .append("title", book.getTitle())
                    .append("published", book.getPublished())
                    .append("storyLine", book.getStoryLine())
                    .append("authors", auList)
                    .append("genre", genre);
            collection.insertOne(document);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int insertAuthor(Author author) throws BooksDbException {
        try{
            MongoCollection<Document> collection = this.db.getCollection("authors");
            Document document = new Document("_id",author.getPersonId())
                    .append("teleNo", author.getTelefonNo())
                    .append("name",author.getName());
            collection.insertOne(document);

            return 1;
        } catch (Exception e) {
            throw new BooksDbException(e.getMessage());
        }

    }

    @Override
    public int insertReview(Book book, int rate, String review) throws BooksDbException {
        return 0;
    }

    @Override
    public int updateBookInfo(Book book) throws BooksDbException {
        try{
            MongoCollection<Document> collection = this.db.getCollection("books");

            Bson updates = Updates.combine(
                    Updates.set("title", book.getTitle()),
                    Updates.set("published", book.getPublished()),
                    Updates.set("storyLine", book.getStoryLine())
            );

            Document query = new Document("_id", book.getIsbn());

            collection.updateOne(query, updates);
            return 0;
        } catch (Exception e) {
            throw new BooksDbException(e.getMessage());
        }
    }

    @Override
    public void updateBookFullDetail(Book book, List<Author> authors, List<String> genre) throws BooksDbException, SQLException {
        try{
            updateBookInfo(book);

            MongoCollection<Document> collection = this.db.getCollection("books");

            List<String> str = new ArrayList<>();

            for (Author a: authors)
                str.add(a.getPersonId());


            Bson updates = Updates.combine(
                    Updates.set("genre", genre),
                    Updates.set("authors", str)
            );
            Document query = new Document("_id", book.getIsbn());
            collection.updateOne(query, updates);
        } catch (BooksDbException e) {
            throw new BooksDbException(e.getMessage());
        }
    }

    @Override
    public int deleteBookByIsbn(String isbn) throws BooksDbException {
        try{
            MongoCollection<Document> collection = this.db.getCollection("books");
            collection.deleteOne(eq("_id",isbn));
            return 1;
        } catch (Exception e) {
            throw new BooksDbException(e.getMessage());
        }
    }
}
