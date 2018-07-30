package fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.vendorprovider.R;
import com.vendorprovider.ServiceCreationActivity;
import com.vendorprovider.ViewServicesActivity;

/**
 * Created by rajgandhi on 24/07/18.
 */

public class SearchingKeywordFragment extends Fragment {

    Button btnNext, btnBack;
    TextInputEditText txtGenericKeyWord;
    SharedPreferences preferences;
    String checkEditMode;
    int position;

    public static SearchingKeywordFragment newInstance() {
        SearchingKeywordFragment fragment = new SearchingKeywordFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_keyword_fragment, container, false);

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        checkEditMode = preferences.getString("EditService", "");
        position = preferences.getInt("Position", 0);

        btnNext = (Button) view.findViewById(R.id.btnNext);
        btnBack = (Button) view.findViewById(R.id.btnBack);
        txtGenericKeyWord = (TextInputEditText) view.findViewById(R.id.txtGenericKeyWord);

        if (checkEditMode.equals("Edit")) {

        }

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String keyword = txtGenericKeyWord.getText().toString();
                if(TextUtils.isEmpty(keyword)){
                    txtGenericKeyWord.setError("Please enter keyword!");
                    txtGenericKeyWord.requestFocus();
                    return;
                }else{
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("generic_keyword", keyword);
                    editor.commit();
                    ServiceCreationActivity.mViewPager.setCurrentItem(5, true);
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServiceCreationActivity.mViewPager.setCurrentItem(3, true);
            }
        });

        return view;
    }

    }
