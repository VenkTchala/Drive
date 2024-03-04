package com.example.driveclient;

import com.example.driveclient.dto.File;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.time.Instant;
import java.util.ResourceBundle;

public class FileManagerController implements Initializable {

    @FXML
    private TableColumn<File, Instant> modifiedAt;

    @FXML
    private TableView<File> table;

    @FXML
    private TableColumn<File, Long> name;

    @FXML
    private TableColumn<File, String> owner;

    @FXML
    private BorderPane pane;

    @FXML
    private TableColumn<File, Long> size;

    private ObservableList<File> intailData(){
        File f1 = File.builder()
                .id(1l)
                .modifiedAt(Instant.now())
                .name("Apollo")
                .owner("siva")
                .isDirectory(true)
                .size(1200l)
                .build();

        File f2 = File.builder()
                .id(2l)
                .modifiedAt(Instant.now())
                .name("file")
                .owner("siva")
                .isDirectory(false)
                .size(1200l)
                .build();

        var fx = FXCollections.observableArrayList(f1);
        for(int i = 0 ; i < 10 ; i++)
            if(i % 2 != 0)
                fx.add(f1);
            else
            fx.add(f2);

        return fx;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        name.setResizable(false);
        size.setResizable(false);
        owner.setResizable(false);
        modifiedAt.setResizable(false);


        name.setCellValueFactory(new PropertyValueFactory<>("id"));
        size.setCellValueFactory(new PropertyValueFactory<>("size"));
        owner.setCellValueFactory(new PropertyValueFactory<>("owner"));
        modifiedAt.setCellValueFactory(new PropertyValueFactory<>("modifiedAt"));
        table.setItems(intailData());

        table.setFixedCellSize(40);
        table.prefHeightProperty().bind(Bindings.size(table.getItems()).multiply(table.getFixedCellSize()).add(33));

        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        table.setRowFactory(tv -> {
            TableRow<File> row = new TableRow<>();
            row.selectedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    row.setStyle("-fx-background-color: #81c483;");
                } else {
                    row.setStyle("");
                }
            });

            return row;});

        name.setCellFactory(col -> {
            TableCell<File, Long> cell = new TableCell<>();
            cell.itemProperty().addListener((observableValue, o, newValue) -> {
                if (newValue != null) {
                    Node graphic = createFileGraphic(newValue);
                    cell.graphicProperty().bind(Bindings.when(cell.emptyProperty()).then((Node) null).otherwise(graphic));
                }
            });
            return cell;
        });

    }

    private Node createFileGraphic(Long FileId){
        File file =
        intailData().stream()
                .parallel()
                .filter(i -> i.getId() == FileId)
                .findFirst().orElseThrow(IllegalArgumentException::new);

        HBox graphicContainer = new HBox();
        graphicContainer.setAlignment(Pos.CENTER_LEFT);
        FontIcon icon;

        if(file.isDirectory())
            icon = new FontIcon("fas-folder");
        else
            icon = new FontIcon("fas-file");

        icon.setIconSize(20);
        icon.setIconColor(Paint.valueOf("#05988f"));

        Text text = new Text(file.getName());
        graphicContainer.getChildren().add(icon);
        graphicContainer.getChildren().add(text);
        graphicContainer.setSpacing(10);
        return graphicContainer;
    }

}

