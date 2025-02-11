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
    @SerializedName("can_edit")
    private boolean canEdit = true;

    @Expose
    @SerializedName("length")
    private int length = 25;

    @Expose
    @SerializedName("width")
    private float width = 0.008f;

    @Expose
    @SerializedName("third_person_length")
    private float third_person_length = 2f;

    @Expose
    @SerializedName("third_person_width")
    private float third_person_width = 0.008f;

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

    public boolean canEdit() {
        return canEdit;
    }

    public int getLength() {
        return length;
    }

    public float getWidth() {
        return width;
    }

    public float getLengthThird() {
        return third_person_length;
    }

    public float getWidthThird() {
        return third_person_width;
    }
}
