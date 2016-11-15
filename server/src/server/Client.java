package server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {

    private Socket socket;
    private String room;
    private String nickName;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;


    Client(){

    }

    Client(String nick, ObjectInputStream inputStream, ObjectOutputStream outputStream){
        this.nickName = nick;
        this.outputStream = outputStream;
        this.inputStream = inputStream;
        this.room = "Waiting room";
    }

    public Socket getSocket() {
        return socket;
    }

    public String getRoom() {
        return room;
    }

    public String getNickName() {
        return nickName;
    }

    public ObjectOutputStream getOutputStream() {
        return outputStream;
    }

    public ObjectInputStream getInputStream() {
        return inputStream;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
