package chierra.hof_reporter;

/**
 * Created by Unknown on 3/26/2018.
 */

public class UserProfile {
    private String mDeviceId, mName, mAddress, mPhone, mPicture;

    public UserProfile(String mDeviceId, String mName, String mAddress, String mPhone) {
        this.mDeviceId = mDeviceId;
        this.mName = mName;
        this.mAddress = mAddress;
        this.mPhone = mPhone;
    }

    public UserProfile(String mDeviceId, String mName, String mAddress, String mPhone, String mPicture) {
        this.mDeviceId = mDeviceId;
        this.mName = mName;
        this.mAddress = mAddress;
        this.mPhone = mPhone;
        this.mPicture = mPicture;
    }

    public UserProfile() {
    }

    public void setmDeviceId(String mDeviceId) {
        this.mDeviceId = mDeviceId;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public void setmAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    public void setmPhone(String mPhone) {
        this.mPhone = mPhone;
    }

    public String getmDeviceId() {
        return mDeviceId;
    }

    public String getmName() {
        return mName;
    }

    public String getmAddress() {
        return mAddress;
    }

    public String getmPhone() {
        return mPhone;
    }

    public String getmPicture() {
        return mPicture;
    }
}
