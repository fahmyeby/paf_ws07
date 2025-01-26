package com.example.paf_ws7.model;

import java.util.Date;
import java.util.List;

public class Review {
    private String id;
    private String user;
    private Integer rating;
    private String comment;
    private String gameId;
    private Date posted;
    private String name;
    private List<Edit> edited;

    public Review() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public Date getPosted() {
        return posted;
    }

    public void setPosted(Date posted) {
        this.posted = posted;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Edit> getEdited() {
        return edited;
    }

    public void setEdited(List<Edit> edited) {
        this.edited = edited;
    }

    public Review(String id, String user, Integer rating, String comment, String gameId, Date posted, String name,
            List<Edit> edited) {
        this.id = id;
        this.user = user;
        this.rating = rating;
        this.comment = comment;
        this.gameId = gameId;
        this.posted = posted;
        this.name = name;
        this.edited = edited;
    }

    public static class Edit {
        private String comment;
        private Integer rating;
        private Date posted;

        public Edit(String comment, Integer rating, Date posted) {
            this.comment = comment;
            this.rating = rating;
            this.posted = posted;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public Integer getRating() {
            return rating;
        }

        public void setRating(Integer rating) {
            this.rating = rating;
        }

        public Date getPosted() {
            return posted;
        }

        public void setPosted(Date posted) {
            this.posted = posted;
        }

    }
}
