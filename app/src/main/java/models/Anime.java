package models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Anime extends AnimeMangaBase {

    private long episodes;
    @JsonProperty("episodes")
    public long getEpisodes() { return episodes; }
    public void setEpisodes(long episodes) { this.episodes = episodes; }

    private long episodesAired;
    @JsonProperty("episodes_aired")
    public long getEpisodesAired() { return episodesAired; }
    public void setEpisodesAired(long episodesAired) { this.episodesAired = episodesAired; }

}

