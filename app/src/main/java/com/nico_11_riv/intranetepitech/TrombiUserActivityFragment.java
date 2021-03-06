package com.nico_11_riv.intranetepitech;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nico_11_riv.intranetepitech.api.APIErrorHandler;
import com.nico_11_riv.intranetepitech.api.IntrAPI;
import com.nico_11_riv.intranetepitech.database.Userinfos;
import com.nico_11_riv.intranetepitech.database.setters.user.GUser;
import com.nico_11_riv.intranetepitech.database.setters.user.PUserInfos;
import com.nico_11_riv.intranetepitech.toolbox.CircleTransform;
import com.nico_11_riv.intranetepitech.toolbox.IsConnected;
import com.nico_11_riv.intranetepitech.toolbox.Tools;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.rest.RestService;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Objects;

@EFragment(R.layout.fragment_trombi_user)
public class TrombiUserActivityFragment extends Fragment {

    private GUser gUser = new GUser();
    private Userinfos user ;
    private IsConnected ic;
    private Tools tools;

    @ViewById
    TextView text;
    @RestService
    IntrAPI api;
    @Bean
    APIErrorHandler ErrorHandler;
    @ViewById
    ImageView student_img;
    @ViewById
    TextView login;
    @ViewById
    TextView email;
    @ViewById
    TextView gpa_content;
    @ViewById
    TextView promo_content;
    @ViewById
    TextView semester_content;
    @ViewById
    ImageView background;
    @ViewById
    LinearLayout CardContainer;
    @ViewById
    CardView card_mail;
    @ViewById
    CardView card_add_contact;
    @ViewById
    CardView card_call;
    @ViewById
    TextView action_card;
    @ViewById
    ImageView ic_card;
    @ViewById
    TextView credits_content;
    @FragmentArg
    String logintoget = gUser.getLogin();

    public TrombiUserActivityFragment() {
        this.tools = new Tools(getActivity());
    }

    View.OnClickListener mailListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (Objects.equals(user.getEmail(), "")) {
                maketoast("Pas d'adresse mail");
            } else {
                final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

                emailIntent.setType("plain/text");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{user.getEmail()});
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject");
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Text");
                startActivity(Intent.createChooser(emailIntent, "Choisissez l'application"));
            }
        }
    };
    View.OnClickListener contactListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (Objects.equals(user.getTitle(), "")) {
                maketoast("Pas d'informations suffisantes");
            } else {
                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setType(ContactsContract.Contacts.CONTENT_TYPE);

                intent.putExtra(ContactsContract.Intents.Insert.NAME, user.getTitle());
                intent.putExtra(ContactsContract.Intents.Insert.COMPANY, "Epitech");
                if (!Objects.equals(user.getPhone(), ""))
                    intent.putExtra(ContactsContract.Intents.Insert.PHONE, user.getPhone());
                if (!Objects.equals(user.getEmail(), ""))
                    intent.putExtra(ContactsContract.Intents.Insert.EMAIL, user.getEmail());
                startActivity(intent);
            }

        }
    };
    View.OnClickListener phoneListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (user.getPhone().length() < 9) {
                maketoast("Aucun numéro disponible");
            } else {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + user.getPhone()));
                startActivity(intent);
            }
        }
    };

    @UiThread
    void filluserinfosui() {

        Picasso.with(getContext()).load(user.getPicture()).transform(new CircleTransform()).into(student_img);
        login.setText(user.getLogin());
        email.setText(user.getEmail());
        gpa_content.setText(user.getGpa());
        promo_content.setText(user.getPromo());
        semester_content.setText(user.getSemester());
        credits_content.setText(user.getCredits());
        CardContainer.setVisibility(View.VISIBLE);
        CardView mail = (CardView) CardContainer.findViewById(R.id.card_mail);
        CardView contact = (CardView) CardContainer.findViewById(R.id.card_add_contact);
        CardView phone = (CardView) CardContainer.findViewById(R.id.card_call);


        card_mail.setVisibility(View.VISIBLE);
        card_mail.setOnClickListener(mailListener);
        ((ImageView) mail.findViewById(R.id.ic_card)).setImageResource(R.drawable.ic_send_mail);
        ((TextView) mail.findViewById(R.id.action_card)).setText(Html.fromHtml("<a href=\"mailto:" + user.getEmail() + "\">Envoyer un email</a>"));
       /* ic_card.setImageResource(R.drawable.ic_send_mail);
        action_card.setText(Html.fromHtml("<a href=\"mailto:" + userinfos.getEmail() + "\">Envoyer un email </a>"));*/

        card_add_contact.setVisibility(View.VISIBLE);
        contact.setOnClickListener(contactListener);
        ((ImageView) contact.findViewById(R.id.ic_card)).setImageResource(R.drawable.ic_add_contact);
        ((TextView) contact.findViewById(R.id.action_card)).setText(Html.fromHtml("<a href=\"\">Ajouter aux contacts</a>"));

        card_call.setVisibility(View.VISIBLE);
        phone.setOnClickListener(phoneListener);
        ((ImageView) phone.findViewById(R.id.ic_card)).setImageResource(R.drawable.ic_phone);
        ((TextView) phone.findViewById(R.id.action_card)).setText(Html.fromHtml("<a href=\"\">Appeller le contact</a>"));
       /* ic_card.setImageResource(R.drawable.ic_add_contact);
        action_card.setText("ee");*/

        card_call.setVisibility(View.VISIBLE);
    }

    @UiThread
    public void maketoast(String text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }

    @UiThread
    void fillmenu() {
        TextView tv = (TextView) getActivity().findViewById(R.id.menu_login);
        tv.setText(tools.getgUserInfos().getLogin());
        tv = (TextView) getActivity().findViewById(R.id.menu_email);
        tv.setText(tools.getgUserInfos().getEmail());

        ImageView iv = (ImageView) getActivity().findViewById(R.id.menu_img);
        Picasso.with(getContext()).load(tools.getgUserInfos().getPicture()).transform(new CircleTransform()).into(iv);
    }

    void setUserInfos() {
        List<Userinfos> uInfos = Userinfos.findWithQuery(Userinfos.class, "SELECT * FROM Userinfos WHERE login = ?", logintoget);
        if (uInfos.size() > 0){
            user = uInfos.get(0);
            filluserinfosui();
        }
        if (ic.connected()) {
            api.setCookie("PHPSESSID", tools.getgUser().getToken());
            try {
                PUserInfos infos = new PUserInfos(logintoget);
                infos.init(api.getuserinfo(logintoget));
                uInfos = Userinfos.findWithQuery(Userinfos.class, "SELECT * FROM Userinfos WHERE login = ?", logintoget);
            } catch (HttpClientErrorException e) {
                Log.d("Response", e.getResponseBodyAsString());
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (NullPointerException e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            if (uInfos.size() > 0) {
                user = uInfos.get(0);
                filluserinfosui();
            }
        }
    }

    @Background
    void profile_messages() {
        fillmenu();
        setUserInfos();
    }

    @AfterViews
    void init() {
        ic = new IsConnected(getActivity().getApplicationContext());
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        profile_messages();
    }

}