package models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Screenshots {

    private String original;
    @JsonProperty("original")
    public String getOriginal() { return original; }
    public void setOriginal(String original) { this.original = original; }

    private String preview;
    @JsonProperty("preview")
    public String getPreview() { return preview; }
    public void setPreview(String preview) { this.preview = preview; }

}
