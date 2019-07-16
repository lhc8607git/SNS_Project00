package com.example.sns_project00;

public class WriteInfo {
    private String title;
    private String contents;   //내용
    private String publisher;

    public WriteInfo(String title, String contents, String publisher) {
        this.title = title;
        this.contents = contents;
        this.publisher = publisher;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getContents() {
        return contents;
    }
    public void setContents(String contents) {
        this.contents = contents;
    }
    public String getPublisher() {
        return publisher;
    }
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
