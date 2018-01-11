package com.sonal.meettheteam.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import com.sonal.meettheteam.Adapter.PeopleListAdapter;
import com.sonal.meettheteam.Base.BaseActivity;
import com.sonal.meettheteam.Commons.Constant;
import com.sonal.meettheteam.Commons.ReqConst;
import com.sonal.meettheteam.Model.PeopleModel;
import com.sonal.meettheteam.Preference.PrefConst;
import com.sonal.meettheteam.Preference.Preference;
import com.sonal.meettheteam.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import in.srain.cube.views.GridViewWithHeaderAndFooter;

public class PeopleListActivity extends BaseActivity {
    GridViewWithHeaderAndFooter grvPeopleList;
    PeopleListAdapter peopleListAdapter;
    ArrayList<PeopleModel> peopleList = new ArrayList<>();
    View footerView;
    ImageView imvAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_list);

        Constant.P_LIST_ACTIVITY = this;

        loadLayout();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Constant.P_LIST_ACTIVITY = null;
    }

    private void loadLayout() {

        peopleListAdapter = new PeopleListAdapter(this, R.layout.grid_people, peopleList);
        grvPeopleList = (GridViewWithHeaderAndFooter) findViewById(R.id.grv_people_list);
        footerView = ((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.footer_blank, null, false);
        grvPeopleList.addFooterView(footerView);
        grvPeopleList.setAdapter(peopleListAdapter);
        grvPeopleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (position == peopleList.size()) {
                    return;
                }

                gotoQuickView(position);
            }
        });

        imvAdd = (ImageView) findViewById(R.id.imv_add);
        imvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               gotoDetails();
            }
        });

        String launchStatus = Preference.getInstance().getString(this, PrefConst.PREF_PARAM_FIRST_RUN);
        if (launchStatus.length() == 0 || launchStatus.equals(PrefConst.PREF_CONS_FIRST)) {
            loadJsonFile(this);
        } else {
            peopleList = Preference.getInstance().getPeopleList(this, PrefConst.PREF_PARAM_PEOPLE_LIST);
            peopleListAdapter.setData(peopleList);
        }
    }

    private void gotoDetails() {
        Intent intent = new Intent(PeopleListActivity.this, DetailsActivity.class);

        startActivity(intent);
    }

    private void gotoQuickView(int itemIndex) {
        Intent intent = new Intent(PeopleListActivity.this, QuickViewActivity.class);
        intent.putExtra(Constant.PEOPLE, peopleList.get(itemIndex));

        startActivity(intent);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public String loadJsonFile(Context context) {
        String json = null;

        try {
            InputStream stream = context.getAssets().open("team.json");

            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();

            json = new String(buffer, "UTF-8");
            parseData(json);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private void parseData(String json) {

        try {
            JSONArray jsonData = new JSONArray(json);

            if (jsonData != null) {
                for (int i = 0; i < jsonData.length(); i++) {
                    JSONObject jsonPeople = jsonData.getJSONObject(i);
                    PeopleModel people = new PeopleModel();

                    people.setId(jsonPeople.getString(ReqConst.REQ_ID));
                    people.setFirstName(jsonPeople.getString(ReqConst.REQ_F_NAME));
                    people.setLastName(jsonPeople.getString(ReqConst.REQ_L_NAME));
                    people.setImageUrl(jsonPeople.getString(ReqConst.REQ_AVATAR));
                    people.setBio(jsonPeople.getString(ReqConst.REQ_BIO));
                    people.setBirthday(jsonPeople.getString(ReqConst.REQ_DOB));
                    people.setTitle(jsonPeople.getString(ReqConst.REQ_TITLE));
                    people.setPhone(jsonPeople.getString(ReqConst.REQ_PHONE));

                    peopleList.add(people);
                }

                putPeopleListToLocalStorage(peopleList);
            } else {
                onError();
            }
        } catch (JSONException e) {
            onError();
        }
    }

    private void onError() {

        showToast("Error");
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    public void putPeopleListToLocalStorage(ArrayList<PeopleModel> peoples) {
        peopleList = peoples;

        Preference.getInstance().putPeopleList(this, PrefConst.PREF_PARAM_PEOPLE_LIST, peoples);
        Preference.getInstance().putString(this, PrefConst.PREF_PARAM_FIRST_RUN, PrefConst.PREF_CONS_PASS);

        peopleListAdapter.setData(peopleList);
    }

}


