package models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Genre {

    public Genre(long id, String name, String russian, String kind){
        this.id = id;
        this.name = name;
        this.russian = russian;
        this.kind = kind;
    }

    public Genre(){}

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

    private String kind;
    @JsonProperty("kind")
    public String getKind() { return kind; }
    public void setKind(String kind) { this.kind = kind; }

}
