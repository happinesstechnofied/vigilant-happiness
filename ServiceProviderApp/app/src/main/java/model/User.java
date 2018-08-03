package model;

/**
 * Created by Piyush on 10/25/2017.
 */

public class User {

    private String userID , firstName , lastName , emailID;
    private String socialLogin;

    public User(String userID, String emailID ,String firstName, String lastName, String socialLogin) {
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailID = emailID;
        this.socialLogin = socialLogin;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailID() {
        return emailID;
    }

    public void setEmailID(String emailID) {
        this.emailID = emailID;
    }
    public String getSocialLogin() {
        return socialLogin;
    }

    public void setSocialLogin(String socialLogin) {
        this.socialLogin = socialLogin;
    }
}

