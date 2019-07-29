package com.example.sns_project00;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PostInfo implements Serializable {  //implements Serializable   -> 커스텀한 객체를 받을려면 이거 사용 해야 한다.!!
    private String title;
    private ArrayList<String> contents;   //내용
    private String publisher;
    private Date createdAt; //생성일
    private String id;

    public PostInfo(String title, ArrayList<String> contents, String publisher, Date createdAt,String id) {
        this.title = title;
        this.contents = contents;
        this.publisher = publisher;
        this.createdAt =createdAt;
        this.id=id;
    }

    public PostInfo(String title, ArrayList<String> contents, String publisher, Date createdAt) {
        this.title = title;
        this.contents = contents;
        this.publisher = publisher;
        this.createdAt =createdAt;
    }

    public Map<String, Object> getPostInfo(){
        Map<String,Object> docData = new HashMap<>();
        docData.put("title",title);
        docData.put("contents",contents);
        docData.put("publisher",publisher);
        docData.put("createdAt",createdAt);
        return docData;

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
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
}
