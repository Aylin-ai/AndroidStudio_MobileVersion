package models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Publisher {

    private long id;
    @JsonProperty("id")
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    private String name;
    @JsonProperty("name")
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

}
