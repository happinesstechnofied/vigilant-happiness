package model;

/**
 * Created by rajgandhi on 10/08/18.
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
        "user_id",
        "user_name",
        "user_avatar",
        "review",
        "rattings",
        "date",
        "status"
})
public class ReviewRatting implements Serializable {

    @JsonProperty("user_id")
    private Integer userId;
    @JsonProperty("user_name")
    private String userName;
    @JsonProperty("user_avatar")
    private String userAvatar;
    @JsonProperty("review")
    private String review;
    @JsonProperty("rattings")
    private String rattings;
    @JsonProperty("date")
    private String date;
    @JsonProperty("status")
    private Boolean status;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private final static long serialVersionUID = 7284683295225952212L;

    @JsonProperty("user_id")
    public Integer getUserId() {
        return userId;
    }

    @JsonProperty("user_id")
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @JsonProperty("user_name")
    public String getUserName() {
        return userName;
    }

    @JsonProperty("user_name")
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @JsonProperty("user_avatar")
    public String getUserAvatar() {
        return userAvatar;
    }

    @JsonProperty("user_avatar")
    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    @JsonProperty("review")
    public String getReview() {
        return review;
    }

    @JsonProperty("review")
    public void setReview(String review) {
        this.review = review;
    }

    @JsonProperty("rattings")
    public String getRattings() {
        return rattings;
    }

    @JsonProperty("rattings")
    public void setRattings(String rattings) {
        this.rattings = rattings;
    }

    @JsonProperty("date")
    public String getDate() {
        return date;
    }

    @JsonProperty("date")
    public void setDate(String date) {
        this.date = date;
    }

    @JsonProperty("status")
    public Boolean getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(Boolean status) {
        this.status = status;
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