package sample;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import common.MessagePacket;

import java.io.IOException;

public class MainController {
    private Thread writerThread;
    private static Stage connectStage;
    @FXML
    private Label connectionStatus;
    @FXML
    private TextField messageField;
    @FXML
    private ListView roomsListView;
    @FXML
    private TabPane tabPane;
    @FXML
    private TextArea waitingRoomTextArea;

    @FXML
    public void menuConnect() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("ConnectScreen.fxml"));
        connectStage = new Stage();
        connectStage.setTitle("Connect");
        connectStage.setScene(new Scene(root, 400, 80));
        connectStage.setAlwaysOnTop(true);
        connectStage.setResizable(false);
        connectStage.show();

        //tutaj przekazanie kontrolera, chociaż mogłoby być w jakiejś metodzie typu initialize, w momencie tworzenia głónego okna, ale wysyłało null
        Main.setMainController(this);
    }

    @FXML
    public void menuDisconnect() throws IOException {
        Main.getConnection().disconnect();
    }

    @FXML
    public void menuExit() throws IOException {
        Main.getConnection().disconnect();
        System.exit(0);
    }

    @FXML
    public void menuAbout() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("AboutScreen.fxml"));
        Stage aboutStage = new Stage();
        aboutStage.setTitle("About");
        aboutStage.setScene(new Scene(root, 300, 150));
        aboutStage.setAlwaysOnTop(true);
        aboutStage.setResizable(false);
        aboutStage.show();
    }

    @FXML
    public void setStatus(boolean isConnected) {
        if (isConnected) {
            connectionStatus.setTextFill(Color.GREEN);
            connectionStatus.setText("connected");
        } else {
            connectionStatus.setTextFill(Color.RED);
            connectionStatus.setText("disconnected");
        }
    }

    @FXML
    public void fillRoomsList() {
        roomsListView.setItems(FXCollections.observableList(Main.getChatRoomsList()));
    }

    @FXML
    public void clearRoomsList() {
        roomsListView.getItems().clear();
        roomsListView.refresh();
    }

    @FXML
    public void onEnter(KeyEvent e) {
        if (e.getCode().toString().equals("ENTER") && !messageField.getText().isEmpty()) {
            sendClicked();
        }
    }

    @FXML
    public void sendClicked() {
        if (Main.getConnection().isConnected()) {
            String roomID = tabPane.getSelectionModel().getSelectedItem().getText();
            String message = messageField.getText();
            MessagePacket msgPacket = new MessagePacket();
            msgPacket.setMessage(message);
            msgPacket.setRoom(roomID);
            Runnable writerJob = new WriterThread(msgPacket);
            writerThread = new Thread(writerJob);
            writerThread.setName("Writer Thread");
            writerThread.start();
            messageField.clear();
        } else {
            System.out.println("Nie można wysłać wiadomośći, nie jesteś połączony!");
            messageField.clear();
        }

    }

    public void roomClick(MouseEvent click) {
        if (click.getClickCount() == 2) {
            String currentItemSelected = (String) roomsListView.getSelectionModel().getSelectedItem();
            if ((currentItemSelected != null) && !Main.getJoinedChatRoomsTabs().containsKey(currentItemSelected)) {
                joinRoom(currentItemSelected);
            }
        }
    }

    public void joinRoom(String joinRoom) {
        Tab newTab = new Tab();
        newTab.setText(joinRoom);
        tabPane.getTabs().add(newTab);
        tabPane.getSelectionModel().select(newTab);

        TextArea newTextArea = new TextArea();
        newTextArea.setEditable(false);
        newTextArea.setWrapText(true);
        newTab.setContent(newTextArea);

        Main.getJoinedChatRoomsTabs().put(joinRoom, newTextArea);
    }

    public void viewMessage(MessagePacket message) {
        if (message.getRoom().equalsIgnoreCase("Waiting room")) { //view message in chat room tab
            waitingRoomTextArea.appendText(message.getMessage() + "\n");

        } else { //view message in other tabs
            TextArea roomTab = Main.getJoinedChatRoomsTabs().get(message.getRoom());
            roomTab.appendText(message.getMessage() + "\n");
        }

    }

    public void closeTabs() {
        int tabPaneSize = tabPane.getTabs().size();
        tabPane.getTabs().remove(1, tabPaneSize);
    }

    public static Stage getConnectStage() {
        return connectStage;
    }
}
