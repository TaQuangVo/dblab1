package kth.se.dblab1;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import kth.se.dblab1.model.BookDbMySqlImpl;
import kth.se.dblab1.model.BooksDbException;
import kth.se.dblab1.model.BooksDbInterface;
import kth.se.dblab1.model.BooksDbMockImpl;
import kth.se.dblab1.view.BooksPane;

import java.sql.SQLException;

/**
 * Application start up.
 *hej tquang
 * @author anderslm@kth.se
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {

        BooksDbMockImpl booksDb = new BooksDbMockImpl(); // model
        // Don't forget to connect to the db, somewhere...

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
        BooksDbInterface db = new BookDbMySqlImpl();
        try{
            Boolean connected = db.connect("test");

            if(connected)
                db.disconnect();
        } catch (BooksDbException e) {
                e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        launch(args);
    }
}
