package arsibi_has_no_website.pokedex;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by hp on 28-06-2017.
 */

public class RViewAdapter extends RecyclerView.Adapter<RViewAdapter.RViewHolder>{
    LayoutInflater inflater;
    Context context;

    public RViewAdapter(Context context) {
        this.context=context;
        inflater=LayoutInflater.from(context);
    }

    @Override
    public RViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v=inflater.inflate(R.layout.customrecycleditem,parent,false);
        RViewHolder holder = new RViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(RViewHolder holder, int position) {
        RViewItems items=History.historylist.get(position);
        Log.d("MOO1",items.f.getAbsolutePath());
        holder.name.setText(items.name);
        if(items.f!=null){
            if(items.f.exists())
                holder.icon.setImageURI(Uri.fromFile(items.f));
            else{
                Picasso.with(context).load(items.imageurl).into(holder.icon);
            }
        }
        else{
            Picasso.with(context).load(items.imageurl).into(holder.icon);
        }
    }

    @Override
    public int getItemCount() {
        return History.historylist.size();
    }
    class RViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView icon;
        public RViewHolder(View itemView) {
            super(itemView);
            name=(TextView)itemView.findViewById(R.id.recycledText);
            icon=(ImageView)itemView.findViewById(R.id.recycledimage);
        }
    }
}
