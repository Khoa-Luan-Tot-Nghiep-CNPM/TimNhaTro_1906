package com.doan.timnhatro.model;

public class PicturePicker {
    private String pathPicture;
    private boolean isChecked;

    public PicturePicker(String pathPicture) {
        this.pathPicture = pathPicture;
    }

    public String getPathPicture() {
        return pathPicture;
    }

    public void setPathPicture(String pathPicture) {
        this.pathPicture = pathPicture;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
