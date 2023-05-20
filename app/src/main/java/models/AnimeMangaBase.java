package models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AnimeMangaBase extends SmallRepresentation {

    private String kind;
    @JsonProperty("kind")
    public String getKind() { return kind; }
    public void setKind(String kind) { this.kind = kind; }

    private String score;
    @JsonProperty("score")
    public String getScore() { return score; }
    public void setScore(String score) { this.score = score; }

    private String status;
    @JsonProperty("status")
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    private String airedOn;
    @JsonProperty("aired_on")
    public String getAiredOn() { return airedOn; }
    public void setAiredOn(String airedOn) { this.airedOn = airedOn; }

    private String releasedOn;
    @JsonProperty("released_on")
    public String getReleasedOn() { return releasedOn; }
    public void setReleasedOn(String releasedOn) { this.releasedOn = releasedOn; }
}

