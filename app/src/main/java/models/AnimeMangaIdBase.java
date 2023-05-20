package models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AnimeMangaIdBase extends AnimeMangaBase {

    private String[] english;
    @JsonProperty("english")
    public String[] getEnglish() { return english; }
    public void setEnglish(String[] english) { this.english = english; }

    private String[] japanese;
    @JsonProperty("japanese")
    public String[] getJapanese() { return japanese; }
    public void setJapanese(String[] japanese) { this.japanese = japanese; }

    private String[] synonyms;
    @JsonProperty("synonyms")
    public String[] getSynonyms() { return synonyms; }
    public void setSynonyms(String[] synonyms) { this.synonyms = synonyms; }

    private String licenseNameRu;
    @JsonProperty("license_name_ru")
    public String getLicenseNameRu() { return licenseNameRu; }
    public void setLicenseNameRu(String licenseNameRu) { this.licenseNameRu = licenseNameRu; }

    private String description;
    @JsonProperty("description")
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    private String descriptionHtml;
    @JsonProperty("description_html")
    public String getDescriptionHtml() { return descriptionHtml; }
    public void setDescriptionHtml(String descriptionHtml) { this.descriptionHtml = descriptionHtml; }

    private String descriptionSource;
    @JsonProperty("description_source")
    public String getDescriptionSource() { return descriptionSource; }
    public void setDescriptionSource(String descriptionSource) { this.descriptionSource = descriptionSource; }

    private String franchise;
    @JsonProperty("franchise")
    public String getFranchise() { return franchise; }
    public void setFranchise(String franchise) { this.franchise = franchise; }

    private boolean favoured;
    @JsonProperty("favoured")
    public boolean isFavoured() { return favoured; }
    public void setFavoured(boolean favoured) { this.favoured = favoured; }

    private boolean anons;
    @JsonProperty("anons")
    public boolean isAnons() { return anons; }
    public void setAnons(boolean anons) { this.anons = anons; }

    private boolean ongoing;
    @JsonProperty("ongoing")
    public boolean isOngoing() { return ongoing; }
    public void setOngoing(boolean ongoing) { this.ongoing = ongoing; }

    private Genre[] genres;
    @JsonProperty("genres")
    public Genre[] getGenres() { return genres; }
    public void setGenres(Genre[] genres) { this.genres = genres; }
}

