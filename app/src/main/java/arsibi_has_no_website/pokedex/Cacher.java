package arsibi_has_no_website.pokedex;

import android.os.AsyncTask;
import android.speech.tts.Voice;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by hp on 28-06-2017.
 */

public class Cacher extends AsyncTask<String,Void,Void> {
    File imagefile;
    public Cacher(File f2) {
        imagefile=f2;
    }
    @Override
    protected Void doInBackground(String... params) {
        String resource=params[0];
        try {
            if (!imagefile.exists())
                imagefile.createNewFile();
            else
                return null;
            FileOutputStream fileOutputStream = new FileOutputStream(imagefile);
            URL url = new URL(resource);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            InputStream inputStream=connection.getInputStream();
            int b;
            while((b=inputStream.read())!=-1){
                fileOutputStream.write(b);
            }
            fileOutputStream.close();
            connection.disconnect();
            inputStream.close();
        }catch (IOException e){}
        return null;
    }
}
