package models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MangaID extends AnimeMangaIdBase {

    private long volumes;
    @JsonProperty("volumes")
    public long getVolumes() { return volumes; }
    public void setVolumes(long volumes) { this.volumes = volumes; }

    private long chapters;
    @JsonProperty("chapters")
    public long getChapters() { return chapters; }
    public void setChapters(long chapters) { this.chapters = chapters; }

    private Publisher[] publishers;
    @JsonProperty("publishers")
    public Publisher[] getPublishers() { return publishers; }
    public void setPublishers(Publisher[] publishers) { this.publishers = publishers; }
}

