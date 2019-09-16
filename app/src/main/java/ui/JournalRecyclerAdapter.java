package ui;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailymotivation.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import model.JournalModel;

public class JournalRecyclerAdapter extends RecyclerView.Adapter<JournalRecyclerAdapter.ViewHolder>
{
    private Context context;
    private List<JournalModel> journalModelList;

    public JournalRecyclerAdapter(Context context, List<JournalModel> journalModelList)
    {
        this.context = context;
        this.journalModelList = journalModelList;
    }

    @NonNull
    @Override
    public JournalRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_row_layout,
                parent, false);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull JournalRecyclerAdapter.ViewHolder holder, int position)
    {
        String imageUrl;
        JournalModel journalModel = journalModelList.get(position);

        holder.journal_title_list.setText(journalModel.getTitle());
        holder.journal_thought_list.setText(journalModel.getThought());
        holder.journal_row_username.setText(journalModel.getUserName());

        imageUrl = journalModel.getImageUrl();

        //Converting timeAdded to time ago.
        String timeAgo = (String) DateUtils.getRelativeTimeSpanString(journalModel.getTimeAdded().getSeconds() *1000);

        holder.journal_timestamp_list.setText(timeAgo);

        //Picasso to load images and showing in app.
        Picasso.get().
                load(imageUrl)
                .placeholder(R.drawable.back2)
                .fit()
                .into(holder.journal_image_list);



    }

    @Override
    public int getItemCount()
    {
        return journalModelList.size();
    }

     class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView journal_row_username,
        journal_title_list,
        journal_thought_list,
        journal_timestamp_list;

        ImageButton journal_row_share_button;
        ImageView journal_image_list;



        public ViewHolder(@NonNull View itemView, Context ctx)
        {
            super(itemView);
            context = ctx;

            journal_row_username = itemView.findViewById(R.id.journal_row_username);
            journal_title_list = itemView.findViewById(R.id.journal_title_list);
            journal_thought_list = itemView.findViewById(R.id.journal_thought_list);
            journal_timestamp_list = itemView.findViewById(R.id.journal_timestamp_list);

            journal_row_share_button = itemView.findViewById(R.id.journal_row_share_button);

            journal_image_list = itemView.findViewById(R.id.journal_image_list);

            journal_row_share_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, "This message is from implicit intent");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "This is working!");
                    context.startActivity(intent);
                }
            });

        }
    }
}
