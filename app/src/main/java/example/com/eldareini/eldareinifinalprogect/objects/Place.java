package example.com.eldareini.eldareinifinalprogect.objects;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Eldar on 9/17/2017.
 */
//an object that contain data on a Place
public class Place implements Parcelable {

    private String id, name, address, picturesURL;
    private double lat, lon;
    private int image;

    protected Place(Parcel in) {
        id = in.readString();
        name = in.readString();
        address = in.readString();
        picturesURL = in.readString();
        lat = in.readDouble();
        lon = in.readDouble();
        image = in.readInt();
    }

    public static final Creator<Place> CREATOR = new Creator<Place>() {
        @Override
        public Place createFromParcel(Parcel in) {
            return new Place(in);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPicturesURL() {
        return picturesURL;
    }

    public void setPicturesURL(String picturesURL) {
        this.picturesURL = picturesURL;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public Place(String id, String name, String address, double lat, double lon, int image, String picturesURL) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.picturesURL = picturesURL;
        this.lat = lat;
        this.lon = lon;
        this.image = image;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(picturesURL);
        dest.writeDouble(lat);
        dest.writeDouble(lon);
        dest.writeInt(image);
    }
}