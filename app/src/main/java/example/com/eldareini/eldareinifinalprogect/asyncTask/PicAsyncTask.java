package example.com.eldareini.eldareinifinalprogect.asyncTask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Eldar on 10/1/2017.
 */

public class PicAsyncTask extends AsyncTask<String, Void, Bitmap> {

    private OnWebResultListener listener;

    public PicAsyncTask(OnWebResultListener listener) {
        this.listener = listener;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        HttpsURLConnection connection = null;
        Bitmap image;

        try {
            URL url = new URL(params[0]);
            connection = (HttpsURLConnection) url.openConnection();

            image = BitmapFactory.decodeStream(connection.getInputStream());

            return image;

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        
        if (bitmap != null) {
            listener.showImage(bitmap);
        }
    }

    public interface OnWebResultListener {
        void showImage(Bitmap image);
    }
}
