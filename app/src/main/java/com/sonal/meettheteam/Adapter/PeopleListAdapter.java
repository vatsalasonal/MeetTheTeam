package com.sonal.meettheteam.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.sonal.meettheteam.Commons.Constant;
import com.sonal.meettheteam.Model.PeopleModel;
import com.sonal.meettheteam.R;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class PeopleListAdapter extends ArrayAdapter<PeopleModel> {
    Context context;
    ArrayList peopleList;

    public PeopleListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<PeopleModel> peoples) {
        super(context, resource, peoples);

        this.context = context;
        peopleList = new ArrayList<PeopleModel>(peoples);
    }

    @Override
    public int getCount() {
        return peopleList.size();
    }

    @Nullable
    @Override
    public PeopleModel getItem(int position) {
        return (PeopleModel) peopleList.get(position);
    }

    public void setData(ArrayList<PeopleModel> peoples) {
        peopleList.clear();
        peopleList.addAll(peoples);
        this.notifyDataSetChanged();
    }

    public class ViewHolder {
        TextView txvName;
        ImageView imvAvatar;
        View view1, view2;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder vHolder;

        if (convertView == null) {
            vHolder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.grid_people, parent, false);

            vHolder.txvName = (TextView) convertView.findViewById(R.id.txv_name);
            vHolder.imvAvatar = (ImageView) convertView.findViewById(R.id.imv_avatar);
            vHolder.view1 = (View) convertView.findViewById(R.id.view_1);
            vHolder.view2 = (View) convertView.findViewById(R.id.view_2);

            convertView.setTag(vHolder);
        } else {
            vHolder = (ViewHolder) convertView.getTag();
        }

        final PeopleModel people = getItem(position);

        vHolder.txvName.setText(people.getFirstName() + " " + people.getLastName());

        if (people.getImageUrl().substring(0, 4).equals(Constant.WEB_URL_HEAD)) {
            Picasso.with(context).load(people.getImageUrl())
                .placeholder(R.mipmap.p0).error(R.mipmap.p0).into(vHolder.imvAvatar);
        } else {
            Bitmap bitmap = BitmapFactory.decodeFile(people.getImageUrl());
            vHolder.imvAvatar.setImageBitmap(bitmap);
        }

        if (position % 3 != 1) {
            vHolder.view1.setVisibility(View.GONE);
            vHolder.view2.setVisibility(View.VISIBLE);
        } else {
            vHolder.view1.setVisibility(View.VISIBLE);
            vHolder.view2.setVisibility(View.GONE);
        }
        return convertView;
    }

}
