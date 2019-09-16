package model;

import com.google.firebase.Timestamp;

public class JournalModel
{
    private String title;
    private String thought;
    private String userName;
    private Timestamp timeAdded;
    private String imageUrl;
    private String userId;

    public JournalModel()
    {
        //empty constructor is reqd for firestore to work.
    }

    public JournalModel(String title, String thought, String userName, Timestamp timeAdded, String imageUrl, String userId)
    {
        this.title = title;
        this.thought = thought;
        this.userName = userName;
        this.timeAdded = timeAdded;
        this.imageUrl = imageUrl;
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThought() {
        return thought;
    }

    public void setThought(String thought) {
        this.thought = thought;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Timestamp getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(Timestamp timeAdded) {
        this.timeAdded = timeAdded;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
