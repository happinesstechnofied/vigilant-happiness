package model;

/**
 * Created by rajgandhi on 13/08/18.
 */

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "question_id",
        "service_id",
        "question",
        "date",
        "answers"
})
public class QueAnsHistory implements Serializable
{

    @JsonProperty("question_id")
    private String questionId;
    @JsonProperty("service_id")
    private String serviceId;
    @JsonProperty("question")
    private String question;
    @JsonProperty("date")
    private String date;
    @JsonProperty("answers")
    private List<Answer> answers = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private final static long serialVersionUID = 3154589388582258476L;

    @JsonProperty("question_id")
    public String getQuestionId() {
        return questionId;
    }

    @JsonProperty("question_id")
    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    @JsonProperty("service_id")
    public String getServiceId() {
        return serviceId;
    }

    @JsonProperty("service_id")
    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @JsonProperty("question")
    public String getQuestion() {
        return question;
    }

    @JsonProperty("question")
    public void setQuestion(String question) {
        this.question = question;
    }

    @JsonProperty("date")
    public String getDate() {
        return date;
    }

    @JsonProperty("date")
    public void setDate(String date) {
        this.date = date;
    }

    @JsonProperty("answers")
    public List<Answer> getAnswers() {
        return answers;
    }

    @JsonProperty("answers")
    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
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