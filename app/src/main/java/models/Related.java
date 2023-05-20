package models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Related {

    private String relation;
    @JsonProperty("relation")
    public String getRelation() { return relation; }
    public void setRelation(String relation) { this.relation = relation; }

    private String relationRussian;
    @JsonProperty("relation_russian")
    public String getRelationRussian() { return relationRussian; }
    public void setRelationRussian(String relationRussian) { this.relationRussian = relationRussian; }

    private Anime anime;
    @JsonProperty("anime")
    public Anime getAnime() { return anime; }
    public void setAnime(Anime anime) { this.anime = anime; }

    private Manga manga;
    @JsonProperty("manga")
    public Manga getManga() { return manga; }
    public void setManga(Manga manga) { this.manga = manga; }

}
