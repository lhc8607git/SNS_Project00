package com.example.sns_project00.listener;

import com.example.sns_project00.PostInfo;

public interface OnPostListener {
    void onDelete(PostInfo postInfo);
    void onModify();

}
