package com.example.sns_project00;

import java.util.ArrayList;
import java.util.Date;

public class WriteInfo {
    private String title;
    private ArrayList<String> contents;   //내용
    private String publisher;
    private Date createdAt; //생성일

    public WriteInfo(String title, ArrayList<String> contents, String publisher, Date createdAt) {
        this.title = title;
        this.contents = contents;
        this.publisher = publisher;
        this.createdAt =createdAt;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public ArrayList<String> getContents() {
        return contents;
    }
    public void setContents(ArrayList<String> contents) {
        this.contents = contents;
    }
    public String getPublisher() {
        return publisher;
    }
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
    public Date getCreatedAt() {return createdAt;}
    public void setCreatedAt(Date createdAt) {this.createdAt = createdAt;}
}
