package com.dmsskbm.agrigroww.models;

public class ModelPlantDataset {

    String common_disease, description;

    public ModelPlantDataset() {
    }

    public ModelPlantDataset(String common_disease, String description) {
        this.common_disease = common_disease;
        this.description = description;
    }

    public String getCommon_disease() {
        return common_disease;
    }

    public void setCommon_disease(String common_disease) {
        this.common_disease = common_disease;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
