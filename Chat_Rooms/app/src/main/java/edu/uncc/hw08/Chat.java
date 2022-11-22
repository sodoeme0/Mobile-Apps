package edu.uncc.hw08;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

public class Chat implements Serializable {
            String started_by_id, started_by_name, recieved_by_id, recieved_by_name, chat_id;
               ArrayList<Message> messages;

    @Override
    public String toString() {
        return "Chat{" +
                "started_by_id='" + started_by_id + '\'' +
                ", started_by_name='" + started_by_name + '\'' +
                ", recieved_by_id='" + recieved_by_id + '\'' +
                ", recieved_by_name='" + recieved_by_name + '\'' +
                ", messages=" + messages +
                '}';
    }

    public Chat() {
    }

    public String getChat_id() {
        return chat_id;
    }

    public void setChat_id(String chat_id) {
        this.chat_id = chat_id;
    }

    public Chat(String started_by_id, String started_by_name, String recieved_by_id, String recieved_by_name, ArrayList<Message> messages) {
        this.started_by_id = started_by_id;
        this.started_by_name = started_by_name;
        this.recieved_by_id = recieved_by_id;
        this.recieved_by_name = recieved_by_name;
        this.messages = messages;
    }
    public void sortMessages(){
        messages.sort(new Comparator<Message>() {
            @Override
            public int compare(Message message1, Message message2) {
                return message1.timeSTamp.compareTo(message2.timeSTamp);
            }
        });
    }
    public String getStarted_by_id() {
        return started_by_id;
    }

    public void setStarted_by_id(String started_by_id) {
        this.started_by_id = started_by_id;
    }

    public String getStarted_by_name() {
        return started_by_name;
    }

    public void setStarted_by_name(String started_by_name) {
        this.started_by_name = started_by_name;
    }

    public String getRecieved_by_id() {
        return recieved_by_id;
    }

    public void setRecieved_by_id(String recieved_by_id) {
        this.recieved_by_id = recieved_by_id;
    }

    public String getRecieved_by_name() {
        return recieved_by_name;
    }

    public void setRecieved_by_name(String recieved_by_name) {
        this.recieved_by_name = recieved_by_name;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }
}
