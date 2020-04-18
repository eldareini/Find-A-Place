package example.com.eldareini.eldareinifinalprogect.objects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Eldar on 10/8/2017.
 */
//an object that contain All the data on a Place
public class PlaceDetails extends Place implements Parcelable {
    private String website, openingHours, phone;
    private float rating;
    private boolean openNow;

    public PlaceDetails(String id, String name, String address, double lat, double lon, int image, String picturesURL, String website, String openingHours, String phone, float rating, boolean openNow) {
        super(id, name, address, lat, lon, image, picturesURL);
        this.website = website;
        this.openingHours = openingHours;
        this.phone = phone;
        this.rating = rating;
        this.openNow = openNow;
    }

    protected PlaceDetails(Parcel in) {
        super(in);
        website = in.readString();
        openingHours = in.readString();
        phone = in.readString();
        rating = in.readFloat();
        openNow = in.readByte() != 0;
    }

    public String getWebsite() {
        return website;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public String getPhone() {
        return phone;
    }

    public float getRating() {
        return rating;
    }

    public boolean isOpenNow() {
        return openNow;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(website);
        dest.writeString(openingHours);
        dest.writeString(phone);
        dest.writeFloat(rating);
        dest.writeByte((byte) (openNow? 1 : 0));
    }
}
