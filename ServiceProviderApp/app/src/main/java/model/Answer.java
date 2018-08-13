package model;

/**
 * Created by rajgandhi on 13/08/18.
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
        "answer_id",
        "service_id",
        "answer",
        "date"
})
public class Answer implements Serializable
{

    @JsonProperty("answer_id")
    private String answerId;
    @JsonProperty("service_id")
    private String serviceId;
    @JsonProperty("answer")
    private String answer;
    @JsonProperty("date")
    private String date;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private final static long serialVersionUID = 314684878820902352L;

    @JsonProperty("answer_id")
    public String getAnswerId() {
        return answerId;
    }

    @JsonProperty("answer_id")
    public void setAnswerId(String answerId) {
        this.answerId = answerId;
    }

    @JsonProperty("service_id")
    public String getServiceId() {
        return serviceId;
    }

    @JsonProperty("service_id")
    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @JsonProperty("answer")
    public String getAnswer() {
        return answer;
    }

    @JsonProperty("answer")
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @JsonProperty("date")
    public String getDate() {
        return date;
    }

    @JsonProperty("date")
    public void setDate(String date) {
        this.date = date;
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
