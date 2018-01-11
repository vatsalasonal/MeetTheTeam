package com.sonal.meettheteam.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sonal.meettheteam.Base.BaseActivity;
import com.sonal.meettheteam.Commons.Constant;
import com.sonal.meettheteam.Model.PeopleModel;
import com.sonal.meettheteam.R;
import com.squareup.picasso.Picasso;

public class QuickViewActivity extends BaseActivity {
    ImageView imvAvatar, imvBack;
    TextView txvName, txvId, txvTitle;
    LinearLayout lytIntro;

    PeopleModel selectedPeople;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_view);

        Intent intent = getIntent();
        selectedPeople = (PeopleModel) intent.getSerializableExtra(Constant.PEOPLE);

        loadLayout();
    }

    private void loadLayout() {

        imvAvatar = (ImageView) findViewById(R.id.imv_avatar);
        if (selectedPeople.getImageUrl().substring(0, 4).equals(Constant.WEB_URL_HEAD)) {
            Picasso.with(this).load(selectedPeople.getImageUrl())
                    .placeholder(R.mipmap.p0).error(R.mipmap.p0).into(imvAvatar);
        } else {
            Bitmap bitmap = BitmapFactory.decodeFile(selectedPeople.getImageUrl());
            imvAvatar.setImageBitmap(bitmap);
        }

        txvName = (TextView) findViewById(R.id.txv_name);
        txvName.setText(selectedPeople.getFirstName() + " " + selectedPeople.getLastName());

        txvId = (TextView) findViewById(R.id.txv_id);
        txvId.setText("ID: " + selectedPeople.getId());

        txvTitle = (TextView) findViewById(R.id.txv_title);
        txvTitle.setText(selectedPeople.getTitle());

        imvBack = (ImageView) findViewById(R.id.imv_back);
        imvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        lytIntro = (LinearLayout) findViewById(R.id.lyt_intro);
        lytIntro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoDetails();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void gotoDetails() {
        Intent intent = new Intent(QuickViewActivity.this, DetailsActivity.class);
        intent.putExtra(Constant.PEOPLE, selectedPeople);

        startActivity(intent);
        finish();
    }
}
