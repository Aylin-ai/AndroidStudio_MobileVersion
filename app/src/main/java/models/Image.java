package models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Image {

    private String original;
    @JsonProperty("original")
    public String getOriginal() { return original; }
    public void setOriginal(String original) { this.original = original; }

    private String preview;
    @JsonProperty("preview")
    public String getPreview() { return preview; }
    public void setPreview(String preview) { this.preview = preview; }

    private String x96;
    @JsonProperty("x96")
    public String getX96() { return x96; }
    public void setX96(String x96) { this.x96 = x96; }

    private String x48;
    @JsonProperty("x48")
    public String getX48() { return x48; }
    public void setX48(String x48) { this.x48 = x48; }
}

