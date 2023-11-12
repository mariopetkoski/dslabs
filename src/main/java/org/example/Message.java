package org.example;

import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {

    static final long serialVersionUID = 42L;
    String sender;      //sender name
    String receiver;    // receiver name
    String messageText;     //message text
    ArrayList<String> fileContents;     //if a file is sent, the file should be contained here

    public Message() {
    }

    public Message(String sender, String receiver, String messageText, ArrayList<String> fileContents) {
        this.sender = sender;
        this.receiver = receiver;
        this.messageText = messageText;
        this.fileContents = fileContents;
    }

    @Override
    public String toString() {
        return "Message{" +
                "sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", messageText='" + messageText + '\'' +
                ", fileContents=" + fileContents +
                '}';
    }
}