package org.example.model;

public class User {
    private long id;
    private String name;
    private String email;

    public User() {}
    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }


    // getters e setters
    public int getId() { return (int) id; }
    public void setId(long id) {
        this.id = id;
    }    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}

