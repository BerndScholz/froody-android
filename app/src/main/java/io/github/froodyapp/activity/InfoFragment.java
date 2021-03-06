package io.github.froodyapp.activity;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.froodyapp.R;
import io.github.froodyapp.ui.BaseFragment;
import io.github.froodyapp.util.Helpers;
import io.github.gsantner.opoc.util.HelpersA;
import io.github.gsantner.opoc.util.SimpleMarkdownParser;

/**
 * Activity for information about the app
 */
public class InfoFragment extends BaseFragment {
    //########################
    //## Static
    //########################
    public static final String FRAGMENT_TAG = "InfoFragment";

    public static InfoFragment newInstance() {
        return new InfoFragment();
    }

    //####################
    //##  Ui Binding
    //####################
    @BindView(R.id.info__fragment__text_app_version)
    TextView textAppVersion;

    @BindView(R.id.info__fragment__text_maintainers)
    TextView textMaintainers;

    @BindView(R.id.info__fragment__text_contributors)
    TextView textContributors;


    //####################
    //##  Methods
    //####################
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.info__fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        Context context = getContext();
        textMaintainers.setText(new SpannableString(Html.fromHtml(
                Helpers.get().loadMarkdownForTextViewFromRaw(R.raw.maintainers, ""))));
        textMaintainers.setMovementMethod(LinkMovementMethod.getInstance());

        textContributors.setText(new SpannableString(Html.fromHtml(
                Helpers.get().loadMarkdownForTextViewFromRaw(R.raw.contributors, "* ")
        )));
        textContributors.setMovementMethod(LinkMovementMethod.getInstance());

        // App version
        try {
            PackageManager manager = getActivity().getPackageManager();
            PackageInfo info = manager.getPackageInfo(getActivity().getPackageName(), 0);
            textAppVersion.setText(getString(R.string.app_version_v, info.versionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.info__fragment__text_app_version, R.id.info__fragment__button_third_party_licenses, R.id.info__fragment__button_show_app_license})
    public void onButtonClicked(View v) {
        Context context = v.getContext();
        switch (v.getId()) {
            case R.id.info__fragment__text_app_version: {
                Helpers.get().openWebpageInExternalBrowser(getString(R.string.project_github_page));
                break;
            }
            case R.id.info__fragment__button_show_app_license: {
                HelpersA.get(getActivity()).showDialogWithHtmlTextView(R.string.license, Helpers.get().loadMarkdownForTextViewFromRaw(R.raw.license, ""));
                break;
            }
            case R.id.info__fragment__button_third_party_licenses: {
                try {
                    HelpersA.get(getActivity()).showDialogWithHtmlTextView(R.string.license, new SimpleMarkdownParser().parse(
                            getResources().openRawResource(R.raw.licenses_3rd_party),
                            SimpleMarkdownParser.FILTER_ANDROID_TEXTVIEW, "").getHtml());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    @Override
    public String getFragmentTag() {
        return FRAGMENT_TAG;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity activity = (MainActivity) getActivity();
        activity.setTitle(R.string.about_);
        activity.selectTab(2, true);
    }

    @Override
    public boolean onBackPressed() {
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        return true;
    }
}
