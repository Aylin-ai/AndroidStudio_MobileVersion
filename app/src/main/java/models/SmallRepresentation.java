package models;


import com.fasterxml.jackson.annotation.JsonProperty;

public class SmallRepresentation{

    private long id;
    @JsonProperty("id")
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    private String name;
    @JsonProperty("name")
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    private String russian;
    @JsonProperty("russian")
    public String getRussian() { return russian; }
    public void setRussian(String russian) { this.russian = russian; }

    private Image image;
    @JsonProperty("image")
    public Image getImage() { return image; }
    public void setImage(Image image) { this.image = image; }

    private String url;
    @JsonProperty("url")
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}
