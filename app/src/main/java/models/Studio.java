package models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Studio {

    private long id;
    @JsonProperty("id")
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    private String name;
    @JsonProperty("name")
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    private String filteredName;
    @JsonProperty("filtered_name")
    public String getFilteredName() { return filteredName; }
    public void setFilteredName(String filteredName) { this.filteredName = filteredName; }

    private Boolean real;
    @JsonProperty("real")
    public Boolean getReal() { return real; }
    public void setReal(Boolean real) { this.real = real; }

    private String image;
    @JsonProperty("image")
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

}
