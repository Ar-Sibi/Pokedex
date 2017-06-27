package arsibi_has_no_website.pokedex;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by hp on 27-06-2017.
 */

public class StatsAdapter extends ArrayAdapter<StatPair>{
    public StatsAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<StatPair> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View v=inflater.inflate(R.layout.statitem,parent,false);
        ((TextView)v.findViewById(R.id.idtext)).setText(getItem(position).id);
        ((TextView)v.findViewById(R.id.valuetext)).setText(getItem(position).value);
        return v;
    }
}
