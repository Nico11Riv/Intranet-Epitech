package com.nico_11_riv.intranetepitech;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nico_11_riv.intranetepitech.api.APIErrorHandler;
import com.nico_11_riv.intranetepitech.api.IntrAPI;
import com.nico_11_riv.intranetepitech.database.Mark;
import com.nico_11_riv.intranetepitech.database.Userinfos;
import com.nico_11_riv.intranetepitech.database.setters.marks.PMarks;
import com.nico_11_riv.intranetepitech.database.setters.user.GUser;
import com.nico_11_riv.intranetepitech.database.setters.user.GUserInfos;
import com.nico_11_riv.intranetepitech.database.setters.user.PUserInfos;
import com.nico_11_riv.intranetepitech.toolbox.CircleTransform;
import com.nico_11_riv.intranetepitech.toolbox.IsConnected;
import com.nico_11_riv.intranetepitech.ui.adapters.RVMarksAdapter;
import com.nico_11_riv.intranetepitech.ui.contents.MarkContent;
import com.orm.query.Condition;
import com.orm.query.Select;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.rest.RestService;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by nicol on 15/03/2016.
 *
 */

@EFragment(R.layout.fragment_marks)
public class MarksActivityFragment extends Fragment {

    private GUser gUser = new GUser();
    private GUserInfos user_info = new GUserInfos();
    private RVMarksAdapter adapter;
    private IsConnected ic;
    private static int def_nb = 8;
    private static int def_semester = 11;
    public static final String ARG_PAGE = "ARG_PAGE";

    @FragmentArg
    String login = gUser.getLogin();
    @RestService
    IntrAPI api;
    @Bean
    APIErrorHandler ErrorHandler;
    @ViewById
    RecyclerView rv;
    @ViewById
    ProgressBar marks_progress;


    @AfterInject
    void afterInject() {
        api.setRestErrorHandler(ErrorHandler);
    }

    @UiThread
    void setadpt(ArrayList<MarkContent> items) {
        adapter = new RVMarksAdapter(items, getContext(), login);
        rv.setAdapter(adapter);
    }

    void fillmarksui() {
        ArrayList<MarkContent> items = new ArrayList<>();
        List<Mark> marks = Select.from(Mark.class).where(Condition.prop("login").eq(login)).list();

        for (int i = marks.size() - 1; i > 0; i--) {
            Mark info = marks.get(i);
            items.add(new MarkContent(info.getFinalnote(), info.getCorrecteur(), info.getTitle(), info.getTitlemodule()));
        }
        setadpt(items);
    }

    @UiThread
    void fillnewmarksui() {
        if (def_nb != 8) {
            if (def_semester != 11)
                adapter.filter((def_nb + 1) * 5, "B" + Integer.toString(def_semester) + "%");
            else
                adapter.filter((def_nb + 1) * 5, "All");
        } else {
            if (def_semester != 11)
                adapter.filter(0, "B" + Integer.toString(def_semester) + "%");
            else
                adapter.filter(0, "All");
        }
    }

    void setMarks() {
        fillmarksui();
        if (ic.connected()) {
            String m;
            api.setCookie("PHPSESSID", gUser.getToken());
            try {
                m = api.getmarksandmodules(login);
                PMarks marks = new PMarks(login, getActivity());
                marks.init(m);
            } catch (HttpClientErrorException e) {
                Log.d("Response", e.getResponseBodyAsString());
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (NullPointerException e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            fillnewmarksui();
        }
    }

    @UiThread
    void setProgressBar() {
        marks_progress.setVisibility(View.GONE);
    }

    @Background
    void profile_marks() {
        setMarks();
        setProgressBar();
    }

    @AfterViews
    void init() {
        ic = new IsConnected(getActivity().getApplicationContext());
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        profile_marks();
    }

    public void filter(int size, String semester, int def_nb_act, int def_semester_act) {
        if (adapter != null) {
            def_nb = def_nb_act;
            def_semester = def_semester_act;
            adapter.filter(size, semester);
        }
        else
            Toast.makeText(getActivity().getApplicationContext(), "Attendez le chargement de la page", Toast.LENGTH_SHORT).show();
    }

    public void search(String text) {
        if (adapter != null)
            adapter.search(text);
        else
            Toast.makeText(getActivity().getApplicationContext(), "Attendez le chargement de la page", Toast.LENGTH_SHORT).show();
    }
}
