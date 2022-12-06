package kth.se.dblab1;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import kth.se.dblab1.db.BookDbMySqlImpl;
import kth.se.dblab1.db.BooksDbException;
import kth.se.dblab1.db.BooksDbInterface;
import kth.se.dblab1.model.Author;
import kth.se.dblab1.model.Book;
import kth.se.dblab1.view.BooksPane;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

/**
 * Application start up.
 *hej tquang
 * @author anderslm@kth.se
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws BooksDbException, SQLException {

        BooksDbInterface booksDb = new BookDbMySqlImpl(); // model
        booksDb.connect("book2");
        // Don't forget to connect to the db, somewhere...

        //Book book = new Book("11101911", "hej på dig 10", new Date(2000,12,12));
        //Author au = new Author("milad ta vo", "111999304", "2000421343");
        //Author au1 = new Author("milad vo ta", "110999304", "2100125343");
        //booksDb.insertBookFullDetail(book, List.of(au, au1), List.of("skräkt", "omamy", "humor"));
        //System.out.println(booksDb.deleteByIsbn("11101911"));

        Book book_update = new Book("11101911", "hej på dig 3", new Date(2002,11,12));
        book_update.setStoryLine("this is the story line");
        System.out.println(booksDb.updateBookInfo(book_update));



        BooksPane root = new BooksPane(booksDb);
        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("Books Database Client");
        // add an exit handler to the stage (X) ?
        primaryStage.setOnCloseRequest(event -> {
            try {
                booksDb.disconnect();
            } catch (Exception e) {}
        });
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        //test code
        /*
        BooksDbInterface db = new BookDbMySqlImpl();
        try{
            Boolean connected = db.connect("books");
            db.searchBooksByGenre("genre 1");
            //if(connected)
                //db.disconnect();
        } catch (BooksDbException e) {
                e.printStackTrace();
        }
        */

        launch(args);
    }
}
