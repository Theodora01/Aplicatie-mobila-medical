package com.example.aplicatiemedicala;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ArticolAdapter extends RecyclerView.Adapter<ArticolAdapter.ArticleViewHolder>{
    private Context context;
    private List<Articol> articleList;

    public ArticolAdapter(Context context, List<Articol> articleList) {
        this.context = context;
        this.articleList = articleList;
    }
    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_article, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        Articol article = articleList.get(position);
        holder.tvNameDocor.setText(article.getDoctorName());
        holder.tvSpecialization.setText(article.getSpecialization());
        holder.tvArticlesLink.setText(article.getArticleLink());

        Glide.with(context)
                .load(article.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(holder.imgLink);
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    public class ArticleViewHolder extends RecyclerView.ViewHolder {

        TextView tvNameDocor, tvSpecialization, tvArticlesLink;
        ImageView imgLink;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNameDocor = itemView.findViewById(R.id.tvNameDocor);
            tvSpecialization = itemView.findViewById(R.id.tvSpecialization);
            tvArticlesLink = itemView.findViewById(R.id.tvArticlesLink);
            imgLink = itemView.findViewById(R.id.imgLink);
        }
    }

}
