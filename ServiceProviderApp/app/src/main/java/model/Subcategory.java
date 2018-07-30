package model;

/**
 * Created by vish on 14-10-2017.
 */

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "parent_cat_id",
        "parent_cat_name",
        "product_count",
        "image",
        "image_small",
        "image_medium",
        "subcategory"
})
public class Subcategory implements Serializable
{

    @JsonProperty("parent_cat_id")
    private Integer parentCatId;
    @JsonProperty("parent_cat_name")
    private String parentCatName;
    @JsonProperty("product_count")
    private Integer productCount;
    @JsonProperty("image")
    private String image;
    @JsonProperty("image_small")
    private String imageSmall;
    @JsonProperty("image_medium")
    private String imageMedium;
    @JsonProperty("subcategory")
    private List<Subcategory> subcategory = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private final static long serialVersionUID = 2342542654004833464L;

    @JsonProperty("parent_cat_id")
    public Integer getParentCatId() {
        return parentCatId;
    }

    @JsonProperty("parent_cat_id")
    public void setParentCatId(Integer parentCatId) {
        this.parentCatId = parentCatId;
    }

    @JsonProperty("parent_cat_name")
    public String getParentCatName() {
        return parentCatName;
    }

    @JsonProperty("parent_cat_name")
    public void setParentCatName(String parentCatName) {
        this.parentCatName = parentCatName;
    }

    @JsonProperty("product_count")
    public Integer getProductCount() {
        return productCount;
    }

    @JsonProperty("product_count")
    public void setProductCount(Integer productCount) {
        this.productCount = productCount;
    }

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

    @JsonProperty("subcategory")
    public List<Subcategory> getSubcategory() {
        return subcategory;
    }

    @JsonProperty("subcategory")
    public void setSubcategory(List<Subcategory> subcategory) {
        this.subcategory = subcategory;
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