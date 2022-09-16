package com.tmax.hyperauth.rest;

public class UserProfileModel {

    private int age;

    private String sex;

    private String phoneNumber;

    private String job;

    public int getAge() { return age;}
    public void setAge(int age) {this.age = age;}

    public String getSex() { return sex;}
    public void setSex(String sex) { this.sex = sex;}

    public String getPhoneNumber() { return phoneNumber;}
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber;}

    public String getJob() {return job;}
    public void setJob(String job) {this.job = job;}

    @Override
    public String toString() {
        return "UserProfileModel{" +
                "age=" + age +
                ", sex='" + sex + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", job='" + job + '\'' +
                '}';
    }
}
