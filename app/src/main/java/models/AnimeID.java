package models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AnimeID extends AnimeMangaIdBase {

    private long episodes;
    @JsonProperty("episodes")
    public long getEpisodes() { return episodes; }
    public void setEpisodes(long episodes) { this.episodes = episodes; }

    private long episodesAired;
    @JsonProperty("episodes_aired")
    public long getEpisodesAired() { return episodesAired; }
    public void setEpisodesAired(long episodesAired) { this.episodesAired = episodesAired; }

    private String rating;
    @JsonProperty("rating")
    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }

    private long duration;
    @JsonProperty("duration")
    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }

    private String updatedAt;
    @JsonProperty("updated_at")
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    private String nextEpisodeAt;
    @JsonProperty("next_episode_at")
    public String getNextEpisodeAt() { return nextEpisodeAt; }
    public void setNextEpisodeAt(String nextEpisodeAt) { this.nextEpisodeAt = nextEpisodeAt; }

    private Studio[] studios;
    @JsonProperty("studios")
    public Studio[] getStudios() { return studios; }
    public void setStudios(Studio[] studios) { this.studios = studios; }

    private Video[] videos;
    @JsonProperty("videos")
    public Video[] getVideos() { return videos; }
    public void setVideos(Video[] videos) { this.videos = videos; }

    private Screenshots[] screens;
    @JsonProperty("screenshots")
    public Screenshots[] getScreens() { return screens; }
    public void setScreens(Screenshots[] screens) { this.screens = screens; }

}

