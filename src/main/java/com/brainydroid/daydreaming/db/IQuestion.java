package com.brainydroid.daydreaming.db;

import android.location.Location;

public interface IQuestion {

    public String getName();
    public void setName(String name);

    public String getCategory();
    public void setCategory(String category);

    public String getSubCategory();
    public void setSubCategory(String subCategory);

    public String getClassName();
    public void setClassName(String className);

    public String getDetailsAsJson();
    public void setDetailsFromJson(String jsonDetails);

    public IQuestionDetails getDetails();
    public void setDetails(IQuestionDetails details);

    public String getStatus();
    public void setStatus(String status);

    public String getAnswerAsJson();
    public void setAnswerFromJson(String jsonAnswer);

    public IAnswer getAnswer();
    public void setAnswer(IAnswer answer);

    public String getLocationAsJson();
    public void setLocationFromJson(String jsonLocation);

    public Location getLocation();
    public void setLocation(Location location);

    public long getTimestamp();
    public void setTimestamp(long timestamp);

}
