package model;

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
        "service_status",
        "service_id",
        "title",
        "tag_line",
        "parent_cat_id",
        "parent_cat_name",
        "parent_product_count",
        "parent_image",
        "parent_image_small",
        "parent_image_medium",
        "sub_cat_id",
        "sub_cat_name",
        "sub_product_count",
        "sub_image",
        "sub_image_small",
        "sub_image_medium",
        "image",
        "image_small",
        "image_medium",
        "gallery_images",
        "sort_description",
        "opening_hour",
        "closing_hour",
        "maximum_retail_price",
        "sale_price",
        "payment_options",
        "recursive_payment",
        "features",
        "contact_name",
        "appartment",
        "latitude",
        "longitude",
        "mobile_no",
        "email",
        "address",
        "state",
        "city",
        "pincode",
        "tags",
        "review_rattings",
        "total_reviews",
        "total_rattings",
        "que_ans_history",
        "address_image",
        "address_small",
        "address_medium"
})
public class ServicesData implements Serializable
{

    @JsonProperty("service_status")
    private String serviceStatus;
    @JsonProperty("service_id")
    private Integer serviceId;
    @JsonProperty("title")
    private String title;
    @JsonProperty("tag_line")
    private String tagLine;
    @JsonProperty("parent_cat_id")
    private Integer parentCatId;
    @JsonProperty("parent_cat_name")
    private String parentCatName;
    @JsonProperty("parent_product_count")
    private Integer parentProductCount;
    @JsonProperty("parent_image")
    private String parentImage;
    @JsonProperty("parent_image_small")
    private String parentImageSmall;
    @JsonProperty("parent_image_medium")
    private String parentImageMedium;
    @JsonProperty("sub_cat_id")
    private Integer subCatId;
    @JsonProperty("sub_cat_name")
    private String subCatName;
    @JsonProperty("sub_product_count")
    private Integer subProductCount;
    @JsonProperty("sub_image")
    private String subImage;
    @JsonProperty("sub_image_small")
    private String subImageSmall;
    @JsonProperty("sub_image_medium")
    private String subImageMedium;
    @JsonProperty("image")
    private String image;
    @JsonProperty("image_small")
    private String imageSmall;
    @JsonProperty("image_medium")
    private String imageMedium;
    @JsonProperty("gallery_images")
    private List<GalleryImage> galleryImages = null;
    @JsonProperty("sort_description")
    private String sortDescription;
    @JsonProperty("opening_hour")
    private String openingHour;
    @JsonProperty("closing_hour")
    private String closingHour;
    @JsonProperty("maximum_retail_price")
    private String maximumRetailPrice;
    @JsonProperty("sale_price")
    private String salePrice;
    @JsonProperty("payment_options")
    private String paymentOptions;
    @JsonProperty("recursive_payment")
    private String recursivePayment;
    @JsonProperty("features")
    private List<Feature> features = null;
    @JsonProperty("contact_name")
    private String contactName;
    @JsonProperty("appartment")
    private String appartment;
    @JsonProperty("latitude")
    private String latitude;
    @JsonProperty("longitude")
    private String longitude;
    @JsonProperty("mobile_no")
    private String mobileNo;
    @JsonProperty("email")
    private String email;
    @JsonProperty("address")
    private String address;
    @JsonProperty("state")
    private String state;
    @JsonProperty("city")
    private String city;
    @JsonProperty("pincode")
    private String pincode;
    @JsonProperty("tags")
    private String tags;
    @JsonProperty("review_rattings")
    private List<ReviewRatting> reviewRattings = null;
    @JsonProperty("total_reviews")
    private Integer totalReviews;
    @JsonProperty("total_rattings")
    private String totalRattings;
    @JsonProperty("address_image")
    private String addressImage;
    @JsonProperty("address_small")
    private String addressSmall;
    @JsonProperty("address_medium")
    private String addressMedium;
    @JsonProperty("que_ans_history")
    private List<QueAnsHistory> queAnsHistory = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private final static long serialVersionUID = -557723790757291366L;

    @JsonProperty("service_status")
    public String getServiceStatus() {
        return serviceStatus;
    }

    @JsonProperty("service_status")
    public void setServiceStatus(String serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    @JsonProperty("service_id")
    public Integer getServiceId() {
        return serviceId;
    }

    @JsonProperty("service_id")
    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("tag_line")
    public String getTagLine() {
        return tagLine;
    }

    @JsonProperty("tag_line")
    public void setTagLine(String tagLine) {
        this.tagLine = tagLine;
    }

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

    @JsonProperty("parent_product_count")
    public Integer getParentProductCount() {
        return parentProductCount;
    }

    @JsonProperty("parent_product_count")
    public void setParentProductCount(Integer parentProductCount) {
        this.parentProductCount = parentProductCount;
    }

    @JsonProperty("parent_image")
    public String getParentImage() {
        return parentImage;
    }

    @JsonProperty("parent_image")
    public void setParentImage(String parentImage) {
        this.parentImage = parentImage;
    }

    @JsonProperty("parent_image_small")
    public String getParentImageSmall() {
        return parentImageSmall;
    }

    @JsonProperty("parent_image_small")
    public void setParentImageSmall(String parentImageSmall) {
        this.parentImageSmall = parentImageSmall;
    }

    @JsonProperty("parent_image_medium")
    public String getParentImageMedium() {
        return parentImageMedium;
    }

    @JsonProperty("parent_image_medium")
    public void setParentImageMedium(String parentImageMedium) {
        this.parentImageMedium = parentImageMedium;
    }

    @JsonProperty("sub_cat_id")
    public Integer getSubCatId() {
        return subCatId;
    }

    @JsonProperty("sub_cat_id")
    public void setSubCatId(Integer subCatId) {
        this.subCatId = subCatId;
    }

    @JsonProperty("sub_cat_name")
    public String getSubCatName() {
        return subCatName;
    }

    @JsonProperty("sub_cat_name")
    public void setSubCatName(String subCatName) {
        this.subCatName = subCatName;
    }

    @JsonProperty("sub_product_count")
    public Integer getSubProductCount() {
        return subProductCount;
    }

    @JsonProperty("sub_product_count")
    public void setSubProductCount(Integer subProductCount) {
        this.subProductCount = subProductCount;
    }

    @JsonProperty("sub_image")
    public String getSubImage() {
        return subImage;
    }

    @JsonProperty("sub_image")
    public void setSubImage(String subImage) {
        this.subImage = subImage;
    }

    @JsonProperty("sub_image_small")
    public String getSubImageSmall() {
        return subImageSmall;
    }

    @JsonProperty("sub_image_small")
    public void setSubImageSmall(String subImageSmall) {
        this.subImageSmall = subImageSmall;
    }

    @JsonProperty("sub_image_medium")
    public String getSubImageMedium() {
        return subImageMedium;
    }

    @JsonProperty("sub_image_medium")
    public void setSubImageMedium(String subImageMedium) {
        this.subImageMedium = subImageMedium;
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

    @JsonProperty("gallery_images")
    public List<GalleryImage> getGalleryImages() {
        return galleryImages;
    }

    @JsonProperty("gallery_images")
    public void setGalleryImages(List<GalleryImage> galleryImages) {
        this.galleryImages = galleryImages;
    }

    @JsonProperty("sort_description")
    public String getSortDescription() {
        return sortDescription;
    }

    @JsonProperty("sort_description")
    public void setSortDescription(String sortDescription) {
        this.sortDescription = sortDescription;
    }

    @JsonProperty("opening_hour")
    public String getOpeningHour() {
        return openingHour;
    }

    @JsonProperty("opening_hour")
    public void setOpeningHour(String openingHour) {
        this.openingHour = openingHour;
    }

    @JsonProperty("closing_hour")
    public String getClosingHour() {
        return closingHour;
    }

    @JsonProperty("closing_hour")
    public void setClosingHour(String closingHour) {
        this.closingHour = closingHour;
    }

    @JsonProperty("maximum_retail_price")
    public String getMaximumRetailPrice() {
        return maximumRetailPrice;
    }

    @JsonProperty("maximum_retail_price")
    public void setMaximumRetailPrice(String maximumRetailPrice) {
        this.maximumRetailPrice = maximumRetailPrice;
    }

    @JsonProperty("sale_price")
    public String getSalePrice() {
        return salePrice;
    }

    @JsonProperty("sale_price")
    public void setSalePrice(String salePrice) {
        this.salePrice = salePrice;
    }

    @JsonProperty("payment_options")
    public String getPaymentOptions() {
        return paymentOptions;
    }

    @JsonProperty("payment_options")
    public void setPaymentOptions(String paymentOptions) {
        this.paymentOptions = paymentOptions;
    }

    @JsonProperty("recursive_payment")
    public String getRecursivePayment() {
        return recursivePayment;
    }

    @JsonProperty("recursive_payment")
    public void setRecursivePayment(String recursivePayment) {
        this.recursivePayment = recursivePayment;
    }

    @JsonProperty("features")
    public List<Feature> getFeatures() {
        return features;
    }

    @JsonProperty("features")
    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    @JsonProperty("contact_name")
    public String getContactName() {
        return contactName;
    }

    @JsonProperty("contact_name")
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    @JsonProperty("appartment")
    public String getAppartment() {
        return appartment;
    }

    @JsonProperty("appartment")
    public void setAppartment(String appartment) {
        this.appartment = appartment;
    }

    @JsonProperty("latitude")
    public String getLatitude() {
        return latitude;
    }

    @JsonProperty("latitude")
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    @JsonProperty("longitude")
    public String getLongitude() {
        return longitude;
    }

    @JsonProperty("longitude")
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @JsonProperty("mobile_no")
    public String getMobileNo() {
        return mobileNo;
    }

    @JsonProperty("mobile_no")
    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    @JsonProperty("email")
    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty("address")
    public String getAddress() {
        return address;
    }

    @JsonProperty("address")
    public void setAddress(String address) {
        this.address = address;
    }

    @JsonProperty("state")
    public String getState() {
        return state;
    }

    @JsonProperty("state")
    public void setState(String state) {
        this.state = state;
    }

    @JsonProperty("city")
    public String getCity() {
        return city;
    }

    @JsonProperty("city")
    public void setCity(String city) {
        this.city = city;
    }

    @JsonProperty("pincode")
    public String getPincode() {
        return pincode;
    }

    @JsonProperty("pincode")
    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    @JsonProperty("tags")
    public String getTags() {
        return tags;
    }

    @JsonProperty("tags")
    public void setTags(String tags) {
        this.tags = tags;
    }

    @JsonProperty("review_rattings")
    public List<ReviewRatting> getReviewRattings() {
        return reviewRattings;
    }

    @JsonProperty("review_rattings")
    public void setReviewRattings(List<ReviewRatting> reviewRattings) {
        this.reviewRattings = reviewRattings;
    }

    @JsonProperty("total_reviews")
    public Integer getTotalReviews() {
        return totalReviews;
    }

    @JsonProperty("total_reviews")
    public void setTotalReviews(Integer totalReviews) {
        this.totalReviews = totalReviews;
    }

    @JsonProperty("total_rattings")
    public String getTotalRattings() {
        return totalRattings;
    }

    @JsonProperty("total_rattings")
    public void setTotalRattings(String totalRattings) {
        this.totalRattings = totalRattings;
    }

    @JsonProperty("que_ans_history")
    public List<QueAnsHistory> getQueAnsHistory() {
        return queAnsHistory;
    }

    @JsonProperty("que_ans_history")
    public void setQueAnsHistory(List<QueAnsHistory> queAnsHistory) {
        this.queAnsHistory = queAnsHistory;
    }

    @JsonProperty("address_image")
    public String getAddressImage() {
        return addressImage;
    }

    @JsonProperty("address_image")
    public void setAddressImage(String addressImage) {
        this.addressImage = addressImage;
    }

    @JsonProperty("address_small")
    public String getAddressSmall() {
        return addressSmall;
    }

    @JsonProperty("address_small")
    public void setAddressSmall(String addressSmall) {
        this.addressSmall = addressSmall;
    }

    @JsonProperty("address_medium")
    public String getAddressMedium() {
        return addressMedium;
    }

    @JsonProperty("address_medium")
    public void setAddressMedium(String addressMedium) {
        this.addressMedium = addressMedium;
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


