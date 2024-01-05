package com.dmsskbm.agrigroww.models;

public class ModelCacheIdentify {

    String score;
    String scientific_name, common_name, detected_organ, image_url, description, common_disease;

    public ModelCacheIdentify(String score, String scientific_name, String common_name, String detected_organ, String image_url, String description, String common_disease) {
        this.score = score;
        this.scientific_name = scientific_name;
        this.common_name = common_name;
        this.detected_organ = detected_organ;
        this.image_url = image_url;
        this.description = description;
        this.common_disease = common_disease;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getScientific_name() {
        return scientific_name;
    }

    public void setScientific_name(String scientific_name) {
        this.scientific_name = scientific_name;
    }

    public String getCommon_name() {
        return common_name;
    }

    public void setCommon_name(String common_name) {
        this.common_name = common_name;
    }

    public String getDetected_organ() {
        return detected_organ;
    }

    public void setDetected_organ(String detected_organ) {
        this.detected_organ = detected_organ;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCommon_disease() {
        return common_disease;
    }

    public void setCommon_disease(String common_disease) {
        this.common_disease = common_disease;
    }
}
