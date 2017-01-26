package com.sun.action;

/**
 *
 * @author Sunil
 */
public class LoginAction {

    private String userName;
    private String password;
    private String message;

    public String execute() {
        if (userName != null && !userName.isEmpty()) {
            message = "You have logged in successfully.";
            return "success";
        } else {
            message = "Login failed.";
            return "login";
        }

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
}
