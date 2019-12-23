package com.example.akshiban.everything.locker;

class LockerItem {
    private String nameOfItem;
    private String urlOfImage;
    private String typeOfFile;

    public String getTypeOfFile() {
        return typeOfFile;
    }

    public void setTypeOfFile(String typeOfFile) {
        this.typeOfFile = typeOfFile;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;
    private long dateCreated;


    public LockerItem(String nameOfItem, String urlOfImage, long dateCreated) {
        this.nameOfItem = nameOfItem;
        this.urlOfImage = urlOfImage;
        this.dateCreated = dateCreated;
    }

    public LockerItem() {
    }


    public String getNameOfItem() {
        return nameOfItem;
    }

    public void setNameOfItem(String nameOfItem) {
        this.nameOfItem = nameOfItem;
    }

    public String getUrlOfImage() {
        return urlOfImage;
    }

    public void setUrlOfImage(String urlOfImage) {
        this.urlOfImage = urlOfImage;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }
}
