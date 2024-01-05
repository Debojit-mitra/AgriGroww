package com.dmsskbm.agrigroww.expert.models;

public class ModelLoadRequests {

    private String groupName, members, solved;
    private long requestCreated;

    public ModelLoadRequests() {
    }

    public ModelLoadRequests(String groupName, String members, long requestCreated, String solved) {
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
