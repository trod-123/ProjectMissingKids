package com.thirdarm.projectmissingkids.data;

import java.util.Date;

/**
 * Created by sobelman on 4/6/2018.
 */
public class ChildData {
    // data from search
    private String caseNumber;
    private String orgPrefix;
    private String orgName;
    private boolean isChild;
    private int seqNumber;
    private String langId;
    private String firstName;
    private String lastName;
    private String middleName;
    private String missingCity;
    private String missingCounty;
    private String missingState;
    private String missingCountry;
    private Date missingDate;
    private int age;
    private boolean inMonth;
    private boolean inDay;
    private String approxAge;
    private boolean hasThumbnail;
    private boolean hasPoster;
    private String thumbnailURL;
    private String caseType;
    private String posterTitle;
    private String race;

    // detail data
    private boolean hasAgedPhoto;
    private boolean hasExtraPhoto;
    private String possibleLocation;
    private String sex;
    private Date birthDate;
    private int height;
    private boolean heightInInch;
    private int weight;
    private boolean weightInPound;
    private String eyeColor;
    private String hairColor;
    private boolean hasPhoto;
    private String missingProvince;
    private String circumstance;
    private String profileNarrative;
    private String orgContactInfo;
    private String orgLogo;
    private boolean isClearinghouse;
    private String repSightURL;
    private String altContact;
    private String photoMap;

    public ChildData(String caseNumber, String orgPrefix, String orgName, boolean isChild,
                     int seqNumber, String langId, String firstName, String lastName,
                     String middleName, String missingCity, String missingCounty,
                     String missingState, String missingCountry, Date missingDate, int age,
                     boolean inMonth, boolean inDay, String approxAge, boolean hasThumbnail,
                     boolean hasPoster, String thumbnailURL, String caseType, String posterTitle,
                     String race) {
        this.caseNumber = caseNumber;
        this.orgPrefix = orgPrefix;
        this.orgName = orgName;
        this.isChild = isChild;
        this.seqNumber = seqNumber;
        this.langId = langId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.missingCity = missingCity;
        this.missingCounty = missingCounty;
        this.missingState = missingState;
        this.missingCountry = missingCountry;
        this.missingDate = missingDate;
        this.age = age;
        this.inMonth = inMonth;
        this.inDay = inDay;
        this.approxAge = approxAge;
        this.hasThumbnail = hasThumbnail;
        this.hasPoster = hasPoster;
        this.thumbnailURL = thumbnailURL;
        this.caseType = caseType;
        this.posterTitle = posterTitle;
        this.race = race;
    }

    public void addDetailData(boolean hasAgedPhoto, boolean hasExtraPhoto, String possibleLocation,
                              String sex, Date birthDate, int height, boolean heightInInch,
                              int weight, boolean weightInPound, String eyeColor, String hairColor,
                              boolean hasPhoto, String missingProvince, String circumstance,
                              String profileNarrative, String orgContactInfo, String orgLogo,
                              boolean isClearinghouse, String repSightURL, String altContact,
                              String photoMap) {
        this.hasAgedPhoto = hasAgedPhoto;
        this.hasExtraPhoto = hasExtraPhoto;
        this.possibleLocation = possibleLocation;
        this.sex = sex;
        this.birthDate = birthDate;
        this.height = height;
        this.heightInInch = heightInInch;
        this.weight = weight;
        this.weightInPound = weightInPound;
        this.eyeColor = eyeColor;
        this.hairColor = hairColor;
        this.hasPhoto = hasPhoto;
        this.missingProvince = missingProvince;
        this.circumstance = circumstance;
        this.profileNarrative = profileNarrative;
        this.orgContactInfo = orgContactInfo;
        this.orgLogo = orgLogo;
        this.isClearinghouse = isClearinghouse;
        this.repSightURL = repSightURL;
        this.altContact = altContact;
        this.photoMap = photoMap;
    }

    public String getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber(String caseNumber) {
        this.caseNumber = caseNumber;
    }

    public String getOrgPrefix() {
        return orgPrefix;
    }

    public void setOrgPrefix(String orgPrefix) {
        this.orgPrefix = orgPrefix;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public boolean isChild() {
        return isChild;
    }

    public void setChild(boolean child) {
        isChild = child;
    }

    public int getSeqNumber() {
        return seqNumber;
    }

    public void setSeqNumber(int seqNumber) {
        this.seqNumber = seqNumber;
    }

    public String getLangId() {
        return langId;
    }

    public void setLangId(String langId) {
        this.langId = langId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getMissingCity() {
        return missingCity;
    }

    public void setMissingCity(String missingCity) {
        this.missingCity = missingCity;
    }

    public String getMissingCounty() {
        return missingCounty;
    }

    public void setMissingCounty(String missingCounty) {
        this.missingCounty = missingCounty;
    }

    public String getMissingState() {
        return missingState;
    }

    public void setMissingState(String missingState) {
        this.missingState = missingState;
    }

    public String getMissingCountry() {
        return missingCountry;
    }

    public void setMissingCountry(String missingCountry) {
        this.missingCountry = missingCountry;
    }

    public Date getMissingDate() {
        return missingDate;
    }

    public void setMissingDate(Date missingDate) {
        this.missingDate = missingDate;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isInMonth() {
        return inMonth;
    }

    public void setInMonth(boolean inMonth) {
        this.inMonth = inMonth;
    }

    public boolean isInDay() {
        return inDay;
    }

    public void setInDay(boolean inDay) {
        this.inDay = inDay;
    }

    public String getApproxAge() {
        return approxAge;
    }

    public void setApproxAge(String approxAge) {
        this.approxAge = approxAge;
    }

    public boolean isHasThumbnail() {
        return hasThumbnail;
    }

    public void setHasThumbnail(boolean hasThumbnail) {
        this.hasThumbnail = hasThumbnail;
    }

    public boolean isHasPoster() {
        return hasPoster;
    }

    public void setHasPoster(boolean hasPoster) {
        this.hasPoster = hasPoster;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public String getCaseType() {
        return caseType;
    }

    public void setCaseType(String caseType) {
        this.caseType = caseType;
    }

    public String getPosterTitle() {
        return posterTitle;
    }

    public void setPosterTitle(String posterTitle) {
        this.posterTitle = posterTitle;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public boolean isHasAgedPhoto() {
        return hasAgedPhoto;
    }

    public void setHasAgedPhoto(boolean hasAgedPhoto) {
        this.hasAgedPhoto = hasAgedPhoto;
    }

    public boolean isHasExtraPhoto() {
        return hasExtraPhoto;
    }

    public void setHasExtraPhoto(boolean hasExtraPhoto) {
        this.hasExtraPhoto = hasExtraPhoto;
    }

    public String getPossibleLocation() {
        return possibleLocation;
    }

    public void setPossibleLocation(String possibleLocation) {
        this.possibleLocation = possibleLocation;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isHeightInInch() {
        return heightInInch;
    }

    public void setHeightInInch(boolean heightInInch) {
        this.heightInInch = heightInInch;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public boolean isWeightInPound() {
        return weightInPound;
    }

    public void setWeightInPound(boolean weightInPound) {
        this.weightInPound = weightInPound;
    }

    public String getEyeColor() {
        return eyeColor;
    }

    public void setEyeColor(String eyeColor) {
        this.eyeColor = eyeColor;
    }

    public String getHairColor() {
        return hairColor;
    }

    public void setHairColor(String hairColor) {
        this.hairColor = hairColor;
    }

    public boolean isHasPhoto() {
        return hasPhoto;
    }

    public void setHasPhoto(boolean hasPhoto) {
        this.hasPhoto = hasPhoto;
    }

    public String getMissingProvince() {
        return missingProvince;
    }

    public void setMissingProvince(String missingProvince) {
        this.missingProvince = missingProvince;
    }

    public String getCircumstance() {
        return circumstance;
    }

    public void setCircumstance(String circumstance) {
        this.circumstance = circumstance;
    }

    public String getProfileNarrative() {
        return profileNarrative;
    }

    public void setProfileNarrative(String profileNarrative) {
        this.profileNarrative = profileNarrative;
    }

    public String getOrgContactInfo() {
        return orgContactInfo;
    }

    public void setOrgContactInfo(String orgContactInfo) {
        this.orgContactInfo = orgContactInfo;
    }

    public String getOrgLogo() {
        return orgLogo;
    }

    public void setOrgLogo(String orgLogo) {
        this.orgLogo = orgLogo;
    }

    public boolean isClearinghouse() {
        return isClearinghouse;
    }

    public void setClearinghouse(boolean clearinghouse) {
        isClearinghouse = clearinghouse;
    }

    public String getRepSightURL() {
        return repSightURL;
    }

    public void setRepSightURL(String repSightURL) {
        this.repSightURL = repSightURL;
    }

    public String getAltContact() {
        return altContact;
    }

    public void setAltContact(String altContact) {
        this.altContact = altContact;
    }

    public String getPhotoMap() {
        return photoMap;
    }

    public void setPhotoMap(String photoMap) {
        this.photoMap = photoMap;
    }
}
