package com.dmsskbm.agrigroww.admin;

public class ReadWriteAdminDetails {

    public String fullName, dob, gender, profileImage, phone, dateJoined, userId, isAdmin;

    public ReadWriteAdminDetails(){};

    public ReadWriteAdminDetails(String textFullName, String textDoB, String textGender, String textProfileImage,
                                 String textPhone, String textDateJoined, String textUserId, String isAdmin) {
        this.fullName = textFullName;
        this.dob = textDoB;
        this.gender = textGender;
        this.profileImage = textProfileImage;
        this.phone = textPhone;
        this.dateJoined = textDateJoined;
        this.userId = textUserId;
        this.isAdmin = isAdmin;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDateJoined() {
        return dateJoined;
    }

    public void setDateJoined(String dateJoined) {
        this.dateJoined = dateJoined;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(String isAdmin) {
        this.isAdmin = isAdmin;
    }
}
