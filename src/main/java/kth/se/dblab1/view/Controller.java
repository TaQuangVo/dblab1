package kth.se.dblab1.view;

import javafx.application.Platform;
import kth.se.dblab1.db.BooksDbException;
import kth.se.dblab1.model.Author;
import kth.se.dblab1.model.Book;
import kth.se.dblab1.db.BooksDbInterface;
import kth.se.dblab1.model.SearchMode;

import java.util.ArrayList;
import java.util.List;

import static javafx.scene.control.Alert.AlertType.*;

/**
 * The controller is responsible for handling user requests and update the view
 * (and in some cases the model).
 *
 * @author anderslm@kth.se
 */

interface DbSearchBook { // SAM(single abstract method) datatype
    List<Book> search(String searchStr) throws BooksDbException;
}

public class Controller {

    private final BooksPane booksView; // view
    private final BooksDbInterface booksDb; // model

    public Controller(BooksDbInterface booksDb, BooksPane booksView) {
        this.booksDb = booksDb;
        this.booksView = booksView;
    }

    private void dbSearchBookAndUpdateAsync(String searchStr, DbSearchBook searchFunc){
        new Thread(() -> {
            final List<Book> bookResult;
            try{
                bookResult = searchFunc.search(searchStr);
                Platform.runLater(() -> {
                    if (bookResult == null || bookResult.isEmpty()){
                        booksView.showAlertAndWait("No results found.", INFORMATION);
                    }else{
                        booksView.displayBooks(bookResult);
                    }
                });
            }catch (BooksDbException e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    booksView.showAlertAndWait("Something went wrong!!!", ERROR);
                });
            }
        }).start();
    }


    protected void onSearchSelected(String searchFor, SearchMode mode) {

        try {
            if (searchFor != null && searchFor.length() > 1) {
                List<Book> result = null;
                switch (mode) {
                    case Title:
                        dbSearchBookAndUpdateAsync(searchFor, booksDb::searchBooksByTitle);
                        break;
                    case ISBN:
                        dbSearchBookAndUpdateAsync(searchFor, booksDb::searchBooksByIsbn);
                        break;
                    case Author:
                        dbSearchBookAndUpdateAsync(searchFor, booksDb::searchBooksByAuthorName);
                        break;
                    default:
                        booksView.displayBooks(new ArrayList<>());
                }
            } else {
                booksView.showAlertAndWait("Enter a search string!", WARNING);
            }
        } catch (Exception e) {
            e.printStackTrace();
            booksView.showAlertAndWait("Database error.",ERROR);
        }
    }

    // TODO:
    // Add methods for all types of user interaction (e.g. via  menus).
}
