package edu.uncc.hw08;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {

    String senderName, senderId, recieverName, recieverId,  message;
    Date timeSTamp;
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a");

    @Override
    public String toString() {
        return "Message{" +
                "senderName='" + senderName + '\'' +
                ", senderId='" + senderId + '\'' +
                ", recieverName='" + recieverName + '\'' +
                ", recieverId='" + recieverId + '\'' +
                ", timeSTamp='" + timeSTamp + '\'' +
                ", message='" + message + '\'' +
                ", sdf=" + sdf +
                '}';
    }

    public Message(String senderName, String senderId, String recieverName, String recieverId, String message) {
        this.senderName = senderName;
        this.senderId = senderId;
        this.recieverName = recieverName;
        this.recieverId = recieverId;
        this.timeSTamp = (new Date());
        this.message = message;
    }

    public Message() {
    }

    public String getDateFormat(){
        return sdf.format(timeSTamp);
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getRecieverName() {
        return recieverName;
    }

    public void setRecieverName(String recieverName) {
        this.recieverName = recieverName;
    }

    public String getRecieverId() {
        return recieverId;
    }

    public void setRecieverId(String recieverId) {
        this.recieverId = recieverId;
    }

    public Date getTimeSTamp() {
        return timeSTamp;
    }

    public void setTimeSTamp(Date timeSTamp) {
        this.timeSTamp = timeSTamp;
    }
}
