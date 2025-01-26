package com.example.paf_ws6.model;

public class Game {
    private String _id;
    private Integer gid;
    private String name;
    private Integer year;
    private Integer ranking;

    private Integer usersRated;
    private String url;
    private String image;

    public Game() {
        this.gid = 0;
        this.ranking = 0;
        this.year = 0;
        this.usersRated = 0;
    }

    public Game(String _id, Integer gid, String name, Integer year,
            Integer ranking, Integer usersRated, String url, String image) {
        validateFields(gid, name, year);
        this._id = _id;
        this.gid = gid;
        this.name = name;
        this.year = year;
        this.ranking = ranking;
        this.usersRated = usersRated;
        this.url = url;
        this.image = image;
    }

    private void validateFields(Integer gid, String name, Integer year) {
        if (gid == null || gid < 0) {
            throw new IllegalArgumentException("Game ID must be non-negative");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (year == null || year < 1900) {
            throw new IllegalArgumentException("Year must be 1900 or later");
        }
    }

    // Getters
    public String get_id() {
        return _id;
    }

    public Integer getGid() {
        return gid != null ? gid : 0;
    }

    public String getName() {
        return name;
    }

    public Integer getYear() {
        return year != null ? year : 0;
    }

    public Integer getRanking() {
        return ranking != null ? ranking : 0;
    }

    public Integer getUsersRated() {
        return usersRated != null ? usersRated : 0;
    }

    public String getUrl() {
        return url != null ? url : "";
    }

    public String getImage() {
        return image != null ? image : "";
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setGid(Integer gid) {
        this.gid = gid != null ? gid : 0;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setYear(Integer year) {
        this.year = year != null ? year : 0;
    }

    public void setRanking(Integer ranking) {
        this.ranking = ranking != null ? ranking : 0;
    }

    public void setUsersRated(Integer usersRated) {
        this.usersRated = usersRated != null ? usersRated : 0;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setImage(String image) {
        this.image = image;
    }
}