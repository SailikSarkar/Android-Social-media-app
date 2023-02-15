package com.sailiksarkar.freshchatapp;

public class Request
{
    public String fullname,status,profileimage;

    public  Request()
    {

    }


    public Request(String fullname, String status, String profileimage) {
        this.fullname = fullname;
        this.status = status;
        this.profileimage = profileimage;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }
}
