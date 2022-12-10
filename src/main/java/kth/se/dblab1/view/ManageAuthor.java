package kth.se.dblab1.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.ArrayList;
import java.util.List;

public class ManageAuthor extends Dialog {

    ObservableList<HBoxCell> myObservableList;

    public static class HBoxCell extends HBox {
        Label label = new Label();
        Button button = new Button();

        HBoxCell(String labelText, String buttonText) {
            super();

            label.setText(labelText);
            label.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(label, Priority.ALWAYS);

            button.setText(buttonText);

            this.getChildren().addAll(label, button);
        }
    }
    public ManageAuthor(){
        super();
        this.setTitle("Manage Authors");
        this.setHeaderText("Look, a Custom Login Dialog");

        // Set the button types.
        ButtonType loginButtonType = new ButtonType("Apply", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL,loginButtonType);

        //create root grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        //list view of author to add to the book
        List<HBoxCell> list = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            list.add(new HBoxCell("Item " + i, "remove "));
        }
        ListView<HBoxCell> authorToAddListView = new ListView<HBoxCell>();
        authorToAddListView.setPrefHeight(5 * 24 + 2);
        myObservableList = FXCollections.observableList(list);
        authorToAddListView.setItems(myObservableList);

        grid.add(new Label("Author To Add:"), 0, 0);
        grid.add(authorToAddListView, 1, 0);

        // author detail inputs
        TextField authorNameTf = new TextField();
        authorNameTf.setPromptText("Author Name");
        TextField authorIDTf = new TextField();
        authorIDTf.setPromptText("Author ID");
        TextField authorTeleTf = new TextField();
        authorTeleTf.setPromptText("author Tele");
        Button createAuthorBtn = new Button("Create Author");

        HBox authorInputsVbox = new HBox();
        authorInputsVbox.getChildren().addAll(authorNameTf,authorIDTf,authorTeleTf,createAuthorBtn);

        grid.add(new Label("Author Input:"), 0, 1);
        grid.add(authorInputsVbox, 1, 1);

        // existing authors
        List<HBoxCell> existingAuthors = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            existingAuthors.add(new HBoxCell("Item " + i, "remove "));
        }
        ListView<HBoxCell> existingAuthorsListView = new ListView<HBoxCell>();
        existingAuthorsListView.setPrefHeight(5 * 24 + 2);
        myObservableList = FXCollections.observableList(existingAuthors);
        existingAuthorsListView.setItems(myObservableList);

        grid.add(new Label("Existing author:"), 0, 2);
        grid.add(existingAuthorsListView, 1, 2);

        this.getDialogPane().setContent(grid);

    }
}