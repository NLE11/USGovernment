package com.hle.knowyourgovernment;

import java.io.Serializable;
import java.util.Objects;

public class Official implements Serializable, Comparable<Official> {

    private String name;
    private String position;
    private String party;
    private String address;
    private String phones;
    private String urls;
    private String emails;
    private String photoUrl;
    private String FacebookID;
    private String TwitterID;
    private String YouTubeID;
    private int index;

    public Official(String name,
            String position,
            String party,
            String address,
            String phones,
            String urls,
            String emails,
            String photoUrl,
            String FacebookID,
            String TwitterID,
            String YouTubeID,
            int index) {
        this.name = name;
        this.position = position;
        this.party = party;
        this.address = address;
        this.phones = phones;
        this.urls = urls;
        this.emails = emails;
        this.photoUrl = photoUrl;
        this.FacebookID = FacebookID;
        this.TwitterID = TwitterID;
        this.YouTubeID = YouTubeID;
        this.index = index;
    }

    public String getName() { return name; }
    public String getPosition() { return position; }
    public String getParty() { return party; }
    public String getAddress() { return address; }
    public String getPhones() { return phones; }
    public String getUrls() { return urls; }
    public String getEmails() { return emails; }
    public String getPhotoUrl() { return photoUrl; }
    public String getFacebookID() { return FacebookID; }
    public String getTwitterID() { return TwitterID; }
    public String getYouTubeID() { return YouTubeID; }
    public int getIndex() { return index; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Official official = (Official) o;
        return name.equals(official.name) &&
                position.equals(official.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, position);
    }

    @Override
    public int compareTo(Official official) {
        return String.valueOf(index).compareTo(String.valueOf(official.getIndex()));
    }
}
