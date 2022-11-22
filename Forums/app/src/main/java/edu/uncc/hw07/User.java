package edu.uncc.hw07;

public class User {
    String name;
    public User(){}
    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }



    String email;




    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
