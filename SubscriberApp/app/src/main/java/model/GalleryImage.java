package model;

/**
 * Created by rajgandhi on 14/07/18.
 */

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "image",
        "image_small",
        "image_medium"
})
public class GalleryImage implements Serializable {

    @JsonProperty("image")
    private String image;
    @JsonProperty("image_small")
    private String imageSmall;
    @JsonProperty("image_medium")
    private String imageMedium;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private final static long serialVersionUID = -6084995459739781032L;

    @JsonProperty("image")
    public String getImage() {
        return image;
    }

    @JsonProperty("image")
    public void setImage(String image) {
        this.image = image;
    }

    @JsonProperty("image_small")
    public String getImageSmall() {
        return imageSmall;
    }

    @JsonProperty("image_small")
    public void setImageSmall(String imageSmall) {
        this.imageSmall = imageSmall;
    }

    @JsonProperty("image_medium")
    public String getImageMedium() {
        return imageMedium;
    }

    @JsonProperty("image_medium")
    public void setImageMedium(String imageMedium) {
        this.imageMedium = imageMedium;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
