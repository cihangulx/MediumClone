package com.cihangul.mediumclone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import com.cihangul.mediumclone.R;
import com.cihangul.mediumclone.interfaces.ItemClickListener;
import com.parse.ParseObject;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> implements Filterable  {

    private Context context;
    private List<ParseObject> postList,originalPostList;
    private ItemClickListener<ParseObject> postItemClickListener;

    public PostAdapter(Context context, List<ParseObject> postList, ItemClickListener<ParseObject> postItemClickListener) {
        this.context = context;
        this.postList = postList;
        this.originalPostList = postList;
        this.postItemClickListener = postItemClickListener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostViewHolder(LayoutInflater.from(context).inflate(R.layout.post_item_view,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        ParseObject parseObject = postList.get(position);
        holder.setTitleText(parseObject.get("title").toString());
        if (parseObject.getParseFile("image") != null)
            holder.setImageUrl(parseObject.getParseFile("image").getUrl());
        holder.setCategoryText(parseObject.get("category").toString());
        holder.setDateText(new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(parseObject.getCreatedAt()));
        holder.setUserNameText(( parseObject.get("userName")).toString());
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String query = (String) charSequence;
                if (query.isEmpty()) {
                    postList = originalPostList;
                } else {
                    List<ParseObject> resultList = new ArrayList<>();
                    for ( ParseObject parseObject : originalPostList) {
                        if (parseObject.get("title").toString().toLowerCase().contains(query.toLowerCase())) {
                            resultList.add(parseObject);
                        }
                    }
                    postList = resultList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = postList;
                notifyDataSetChanged();
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            }
        };
    }

    class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView title,category,userName,date;
        private ImageView image;

        PostViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            title = itemView.findViewById(R.id.title_text);
            category = itemView.findViewById(R.id.category_text);
            userName = itemView.findViewById(R.id.user_name_text);
            date = itemView.findViewById(R.id.date_text);
            image = itemView.findViewById(R.id.image);
        }

        @Override
        public void onClick(View view) {
            postItemClickListener.onItemClick(postList.get(getAdapterPosition()));
        }

        public void setTitleText(String titleText) {
            this.title.setText(titleText);
        }

        public void setCategoryText(String categoryText) {
            this.category.setText(categoryText);
        }

        public void setUserNameText(String userNameText) {
            this.userName.setText(userNameText);
        }

        public void setDateText(String dateText) {
            this.date.setText(dateText);
        }

        public void setImageUrl(String imageUrl){
            Picasso.get().load(imageUrl).into(image);
        }

    }
}
