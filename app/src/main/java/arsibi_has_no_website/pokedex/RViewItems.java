package arsibi_has_no_website.pokedex;

import android.support.annotation.Nullable;

import java.io.File;
import java.io.Serializable;


public class RViewItems implements Serializable{
    String name;
    String imageurl;
    File f;
    String type;

    protected RViewItems(String name, String imageurl, @Nullable File f,String type){
        this.name=name.substring(0,1)+name.substring(1);
        this.f=f;
        this.imageurl=imageurl;
        this.type=type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
