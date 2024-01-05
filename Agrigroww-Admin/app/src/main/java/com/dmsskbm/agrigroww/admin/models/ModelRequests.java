package com.dmsskbm.agrigroww.admin.models;

public class ModelRequests {

    private String groupName, members, solved;
    private long requestCreated;

    public ModelRequests() {
    }

    public ModelRequests(String groupName, String members, long requestCreated, String solved) {
        this.groupName = groupName;
        this.members = members;
        this.requestCreated = requestCreated;
        this.solved = solved;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getMembers() {
        return members;
    }

    public void setMembers(String members) {
        this.members = members;
    }

    public long getRequestCreated() {
        return requestCreated;
    }

    public void setRequestCreated(long requestCreated) {
        this.requestCreated = requestCreated;
    }

    public String getSolved() {
        return solved;
    }

    public void setSolved(String solved) {
        this.solved = solved;
    }
}
