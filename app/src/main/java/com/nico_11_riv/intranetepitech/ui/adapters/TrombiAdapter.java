package com.nico_11_riv.intranetepitech.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nico_11_riv.intranetepitech.R;
import com.nico_11_riv.intranetepitech.TrombiUserActivity_;
import com.nico_11_riv.intranetepitech.database.Trombi;
import com.nico_11_riv.intranetepitech.toolbox.ToHTML;
import com.nico_11_riv.intranetepitech.ui.contents.Trombi_content;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by victor on 17/03/2016.
 */
public class TrombiAdapter extends RecyclerView.Adapter<TrombiAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Trombi_content> itemsArrayList;

    public TrombiAdapter(Context context, ArrayList<Trombi_content> itemsArrayList) {

        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public int getItemCount() {
        return itemsArrayList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_trombi, viewGroup, false);
        return new ViewHolder(v, context);
    }

    @Override
    public void onBindViewHolder(ViewHolder ViewHolder, int i) {
        ViewHolder.login.setText(itemsArrayList.get(i).getLogin());
        ViewHolder.name.setText(itemsArrayList.get(i).getName());
        if (!Objects.equals(itemsArrayList.get(i).getPicture(), "null")) {
            Picasso.with(context).load(itemsArrayList.get(i).getPicture()).into(ViewHolder.image);
        } else {
            Picasso.with(context).load(R.drawable.login_x).into(ViewHolder.image);

        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView image;
        private TextView login;
        private TextView name;
        private Context context;
        private ToHTML to = new ToHTML();

        ViewHolder(View itemView, Context context) {
            super(itemView);
            itemView.setOnClickListener(this);

            this.context = context;

            image = (ImageView) itemView.findViewById(R.id.image);
            login = (TextView) itemView.findViewById(R.id.login);
            name = (TextView) itemView.findViewById(R.id.name);
        }

        @Override
        public void onClick(View view) {
            login = (TextView) itemView.findViewById(R.id.login);
            Intent intent = new Intent(context , TrombiUserActivity_.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("login", login.getText().toString());
            context.startActivity(intent);
        }
    }

    public void print(String query, String ville, String annee, String tek) {
        itemsArrayList.clear();

        List<Trombi> trombi;
        if (Objects.equals(query, "nosearch")) {
            trombi = Trombi.findWithQuery(Trombi.class, "SELECT * FROM Trombi WHERE location = ? AND years = ? AND tek = ? ORDER BY login", ville, annee, tek);
        } else {
            trombi = Trombi.findWithQuery(Trombi.class, "SELECT * FROM Trombi WHERE login LIKE ?", "%" + query + "%");
            if (trombi.size() == 0) {
                trombi = Trombi.findWithQuery(Trombi.class, "SELECT * FROM Trombi WHERE nom LIKE ?", "%" + query + "%");
                if (trombi.size() == 0) {
                    trombi = Trombi.findWithQuery(Trombi.class, "SELECT * FROM Trombi WHERE prenom LIKE ?", "%" + query + "%");
                }
            }
        }
        for (int i = trombi.size() - 1; i > 0; i--) {
            Trombi info = trombi.get(i);
            itemsArrayList.add(new Trombi_content(info.getLogin(), info.getTitle(), info.getPicture()));
        }
        notifyDataSetChanged();
    }
}
