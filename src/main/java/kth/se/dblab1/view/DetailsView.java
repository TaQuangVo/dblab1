package kth.se.dblab1.view;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import kth.se.dblab1.db.BooksDbException;
import kth.se.dblab1.db.BooksDbInterface;
import kth.se.dblab1.model.Author;
import kth.se.dblab1.model.Book;
import kth.se.dblab1.model.Review;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DetailsView extends Dialog {
    private ObservableList<HBox> reviewObList;
    HBox createHBox(Review review) {
        Label label = new Label();
        label.setText(review.getRate()+", "+ review.getReview());
        label.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(label, Priority.ALWAYS);

        HBox hb = new HBox();
        hb.getChildren().addAll(label);
        return hb;
    };
    public DetailsView(Book book, BooksDbInterface db){
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        this.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
        this.reviewObList = FXCollections.observableList(new ArrayList<HBox>());

        Text isbn = new Text(book.getIsbn());
        Text title = new Text(book.getTitle());
        Text published = new Text(book.getPublished().toString());
        Text storyLine = new Text(book.getStoryLine());
        Text authors = new Text();
        Text genre = new Text();
        Text rate = new Text();
        ListView<HBox> review = new ListView();
        review.setPrefHeight(5 * 24 + 2);
        review.setItems(reviewObList);

        grid.add(new Label("Isbn:"), 0, 0);
        grid.add(isbn, 1, 0);
        grid.add(new Label("Title:"), 0, 1);
        grid.add(title, 1, 1);
        grid.add(new Label("Published:"), 0, 2);
        grid.add(published, 1, 2);
        grid.add(new Label("Storyline:"), 0, 3);
        grid.add(storyLine, 1, 3);
        grid.add(new Label("Genres:"), 0, 4);
        grid.add(genre, 1, 4);
        grid.add(new Label("Authors:"), 0, 5);
        grid.add(authors, 1, 5);
        grid.add(new Label("Rate:"), 0, 6);
        grid.add(rate, 1, 6);
        grid.add(new Label("Review:"), 0, 7);
        grid.add(review, 1, 7);

        TextField rateF = new TextField();
        rateF.setPromptText("rate");
        TextField reviewF = new TextField();
        reviewF.setPromptText("review");
        Button btn = new Button("post");
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                new Thread(()->{
                    try {
                        if(rate.getText() != ""){
                            db.insertReview(book,Integer.parseInt(rateF.getText()) , reviewF.getText());
                            rate.setText("");
                            reviewF.setText("");

                            List<Review> reviews = db.getBookReview(book);
                            if(reviews.size() == 0){
                                rate.setText("no rate jet");
                            }else{
                                float avgRate = 0;
                                List<HBox> t = new ArrayList<>();
                                for(Review r : reviews){
                                    avgRate += r.getRate();
                                    t.add(createHBox(r));
                                }
                                avgRate = avgRate/reviews.size();
                                rate.setText(Float. toString(avgRate));

                                Platform.runLater(()->{
                                    updateReviewList(t);
                                });
                            }
                        }else{
                            Platform.runLater(()->{
                                showAlertAndWait("Fill in required filed", Alert.AlertType.WARNING);
                            });
                        }

                    } catch (Exception e) {
                        Platform.runLater(()->{
                            showAlertAndWait(e.getMessage(), Alert.AlertType.ERROR);
                        });
                    }
                }).start();
            }
        });
        HBox hbox = new HBox();
        hbox.getChildren().addAll(rateF, reviewF, btn);
        grid.add(new Label("post:"), 0, 8);
        grid.add(hbox, 1, 8);

        new Thread(()->{
            try {
                String authorsStr = "";
                List<Author> authorList = db.getBookAuthors(book, 100);
                for(Author a : authorList)
                    authorsStr += a.getName() + ", ";
                authors.setText(authorsStr);

                String genreStr = "";
                List<String> genreList = db.getBookGenres(book,100);
                for(String g : genreList)
                    genreStr += g + ", ";
                genre.setText(genreStr);

                List<Review> reviews = db.getBookReview(book);
                float avgRate = 0;
                List<HBox> t = new ArrayList<>();
                for(Review r : reviews){
                    avgRate += r.getRate();
                    t.add(createHBox(r));
                }
                if(reviews.size() == 0){
                    rate.setText("No rate jet");
                }else{
                    avgRate = avgRate/reviews.size();
                    rate.setText(Float.toString(avgRate));
                    Platform.runLater(()->{
                        updateReviewList(t);
                    });
                }

            } catch (BooksDbException e) {
                Platform.runLater(()->{
                    showAlertAndWait(e.getMessage(), Alert.AlertType.ERROR);
                });
            }
        }).start();

        this.getDialogPane().setContent(grid);

        this.setResultConverter(button -> {
            return null;
        });
    }

    private void updateReviewList(List<HBox> t){
        this.reviewObList.clear();
        this.reviewObList.addAll(t);
    }

    protected void showAlertAndWait(String msg, Alert.AlertType type) {
        // types: INFORMATION, WARNING et c.
        Alert alert = new Alert(type, msg);
        alert.showAndWait();
    }
}
