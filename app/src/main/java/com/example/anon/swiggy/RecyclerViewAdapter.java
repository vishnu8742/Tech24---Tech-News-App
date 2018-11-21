package com.example.anon.swiggy;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.uncopt.android.widget.text.justify.JustifiedTextView;

import java.util.ArrayList;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> mImages = new ArrayList<>();
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mDesk = new ArrayList<>();
    private Context mcontext;

    public RecyclerViewAdapter( ArrayList<String> mImages, ArrayList<String> mNames, ArrayList<String> mDesk, Context mcontext) {
        this.mImages = mImages;
        this.mNames = mNames;
        this.mDesk = mDesk;
        this.mcontext = mcontext;
    }

    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, final int position) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: Images Set");

        Glide.with(mcontext)
                .asBitmap()
                .load(mImages.get(position))
                .into(holder.img);

        holder.txt.setText(mNames.get(position));

        holder.title.setText(mDesk.get(position));

        Typeface typeface = ResourcesCompat.getFont(mcontext, R.font.merriweatherregular);
        holder.title.setTypeface(typeface);

        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Clicked");
//                Toast.makeText(mcontext, mNames.get(position), Toast.LENGTH_SHORT).show();
                PopupMenu popup = null;
                popup = new PopupMenu(mcontext,  holder.anchor, Gravity.RIGHT);

                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(mcontext,"You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });

                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mImages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View anchor;
        ImageView img;
        TextView txt;
        TextView title;
        RelativeLayout parent_layout;


        public ViewHolder( View itemView) {
            super(itemView);
            anchor = itemView.findViewById(R.id.anchor);
            img = itemView.findViewById(R.id.image);
            txt = itemView.findViewById(R.id.text);
            title = itemView.findViewById(R.id.title);
            parent_layout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
