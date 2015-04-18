package com.josedlpozo.bluetootharduino;

/**
 * Created by josedlpozo on 16/4/15.
 */
import android.graphics.drawable.Drawable;

public class Bluetooth {

    private Drawable imageSrc;
    private String name;

    public Bluetooth(Drawable imageSrc, String name) {
        this.imageSrc = imageSrc;
        this.name = name;
    }

    public Drawable getImageSrc() {
        return imageSrc;
    }

    public void setImageSrc(Drawable imageSrc) {
        this.imageSrc = imageSrc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}