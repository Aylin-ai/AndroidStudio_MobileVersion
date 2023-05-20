package models;

import android.net.Uri;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Video {

    private long id;
    @JsonProperty("id")
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    private Uri url;
    @JsonProperty("url")
    public Uri getUrl() { return url; }
    public void setUrl(Uri url) { this.url = url; }

    private Uri imageUrl;
    @JsonProperty("image_url")
    public Uri getImageUrl() { return imageUrl; }
    public void setImageUrl(Uri imageUrl) { this.imageUrl = imageUrl; }

    private Uri playerUrl;
    @JsonProperty("player_url")
    public Uri getPlayerUrl() { return playerUrl; }
    public void setPlayerUrl(Uri playerUrl) { this.playerUrl = playerUrl; }

    private String name;
    @JsonProperty("name")
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    private String kind;
    @JsonProperty("kind")
    public String getKind() { return kind; }
    public void setKind(String kind) { this.kind = kind; }

    private String hosting;
    @JsonProperty("hosting")
    public String getHosting() { return hosting; }
    public void setHosting(String hosting) { this.hosting = hosting; }

}
