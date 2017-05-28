
        package com.glitchcog.fontificator.emoji.loader.twitch;


public class TwitchBadgesNew {

    private String image_url_1x;
    private String image_url_2x;
    private String image_url_4x;
    private String description;
    private String title;

    /**
     * No args constructor for use in serialization
     *
     */
    public TwitchBadgesNew() {
    }

    /**
     *
     * @param image_url_2x
     * @param title
     * @param description
     * @param image_url_4x
     * @param image_url_1x
     */
    public TwitchBadgesNew(String image_url_1x, String image_url_2x, String image_url_4x, String description, String title) {
        super();
        this.image_url_1x = image_url_1x;
        this.image_url_2x = image_url_2x;
        this.image_url_4x = image_url_4x;
        this.description = description;
        this.title = title;
    }

    public String getImage_url_1x() {
        return image_url_1x;
    }

    public void setImage_url_1x(String image_url_1x) {
        this.image_url_1x = image_url_1x;
    }

    public String getImage_url_2x() {
        return image_url_2x;
    }

    public void setImage_url_2x(String image_url_2x) {
        this.image_url_2x = image_url_2x;
    }

    public String getImage_url_4x() {
        return image_url_4x;
    }

    public void setImage_url_4x(String image_url_4x) {
        this.image_url_4x = image_url_4x;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
