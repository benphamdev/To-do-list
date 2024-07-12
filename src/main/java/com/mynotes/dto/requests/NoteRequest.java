package com.mynotes.dto.requests;

public class NoteRequest {
    private String title;
    private String content;

    /**
     * -----------------------------------------------
     * GETTER AND SETTER METHODS:
     * -----------------------------------------------
     */
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
// END OF NOTE REQUEST CLASS.
