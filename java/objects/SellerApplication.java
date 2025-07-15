package com.uilover.project2252.objects;

public class SellerApplication {
    private String id;
    private String email;
    private String username;
    private String status;

    public SellerApplication() {}

    public SellerApplication(String id, String email, String username) {
        this.id = id;
        this.email = email;
        this.username = username;

        this.status = "pending";
    }


    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }


}