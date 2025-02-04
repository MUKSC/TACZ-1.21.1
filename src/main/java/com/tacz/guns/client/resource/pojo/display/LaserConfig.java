package com.tacz.guns.client.resource.pojo.display;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.awt.*;

public class LaserConfig {
    private Integer defaultColor;

    @Expose
    @SerializedName("default_color")
    private String color = "#FF0000";

    @Expose
    @SerializedName("length")
    private int length = 25;

    @Expose
    @SerializedName("can_edit")
    private boolean canEdit = true;

    public int getDefaultColor() {
        if (defaultColor == null) {
            try {
                defaultColor = Color.decode(color).getRGB();
            } catch (NumberFormatException e) {
                defaultColor = Color.WHITE.getRGB();
            }
        }
        return defaultColor;
    }

    public int getLength() {
        return length;
    }

    public boolean canEdit() {
        return canEdit;
    }
}
