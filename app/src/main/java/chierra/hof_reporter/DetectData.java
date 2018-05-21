package chierra.hof_reporter;

/**
 * Created by Unknown on 3/26/2018.
 */

public class DetectData {
    private String mStatus, mImage, mTime;


    public DetectData(String mStatus, String mImage, String mTime) {
        this.mStatus = mStatus;
        this.mImage = mImage;
        this.mTime = mTime;
    }

    public String getmImage() {
        return mImage;
    }

    public void setmImage(String mImage) {
        this.mImage = mImage;
    }

    public String getmTime() {
        return mTime;
    }

    public void setmTime(String mTime) {
        this.mTime = mTime;
    }

    public String getmStatus() {
        return mStatus;
    }

    public void setmStatus(String mStatus) {
        this.mStatus = mStatus;
    }


}
