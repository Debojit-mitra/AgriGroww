package com.dmsskbm.agrigroww.models;

public class Model_Identify {


    String score, scientificNameWithoutAuthor, commonNames, organ, o, familyName, realNameLanguageSupport;

    public Model_Identify(String score, String scientificNameWithoutAuthor, String commonNames, String organ, String o, String familyName, String realNameLanguageSupport) {
        this.score = score;
        this.scientificNameWithoutAuthor = scientificNameWithoutAuthor;
        this.commonNames = commonNames;
        this.organ = organ;
        this.o = o;
        this.familyName = familyName;
        this.realNameLanguageSupport = realNameLanguageSupport;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getScientificNameWithoutAuthor() {
        return scientificNameWithoutAuthor;
    }

    public void setScientificNameWithoutAuthor(String scientificNameWithoutAuthor) {
        this.scientificNameWithoutAuthor = scientificNameWithoutAuthor;
    }

    public String getCommonNames() {
        return commonNames;
    }

    public void setCommonNames(String commonNames) {
        this.commonNames = commonNames;
    }

    public String getOrgan() {
        return organ;
    }

    public void setOrgan(String organ) {
        this.organ = organ;
    }

    public String getO() {
        return o;
    }

    public void setO(String o) {
        this.o = o;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getRealNameLanguageSupport() {
        return realNameLanguageSupport;
    }

    public void setRealNameLanguageSupport(String realNameLanguageSupport) {
        this.realNameLanguageSupport = realNameLanguageSupport;
    }
}
