package fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.vendorprovider.R;
import com.vendorprovider.ServiceCreationActivity;
import com.vendorprovider.ViewServicesActivity;

public class FeatureFragment extends Fragment {
    public static FeatureFragment newInstance() {
        FeatureFragment fragment = new FeatureFragment();
        return fragment;
    }

    SharedPreferences preferences;
    Button btnNext;
    Button btnBack;
    Button btnAddFeature, btnRemoveFeature;
    public static LinearLayout parent_linear_layout;
    EditText editFeature;
    String featurePost;
    String checkEditMode;
    int position;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feature_details_fragment, container, false);

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        checkEditMode = preferences.getString("EditService", "");
        position = preferences.getInt("Position", 0);

        btnNext = (Button) view.findViewById(R.id.btnNext);
        btnBack = (Button) view.findViewById(R.id.btnBack);
        btnRemoveFeature = (Button) view.findViewById(R.id.btnRemoveFeature);
        btnAddFeature = (Button) view.findViewById(R.id.btnAddFeature);
        editFeature = (EditText) view.findViewById(R.id.editFeature);
        parent_linear_layout = (LinearLayout) view.findViewById(R.id.parent_linear_layout);

        if (checkEditMode.equals("Edit")) {
            int i = 0;
            do{
                i++;
                if(ViewServicesActivity.features.size()>i){
                    btnAddFeature.performClick();
                }

            }while(ViewServicesActivity.features.size()>i);
            final int childCount = parent_linear_layout.getChildCount();
            for ( i = 0; i < childCount; i++) {

                    View v = parent_linear_layout.getChildAt(i);
                    EditText child2 = v.findViewById(R.id.editFeature);
                    child2.setText(ViewServicesActivity.features.get(i).getFeature());

            }
        }

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int flag = 0;
                SharedPreferences.Editor editor = preferences.edit();
                final int childCount = parent_linear_layout.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View v = parent_linear_layout.getChildAt(i);
                    EditText child2 = v.findViewById(R.id.editFeature);
                    editor.putString("feature_" + i, child2.getText().toString());
                    flag = 1;
                }
                editor.commit();
                ServiceCreationActivity.mViewPager.setCurrentItem(4, true);
                if(flag == 0){
                    Toast.makeText(getContext(), "Please enter feature post!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServiceCreationActivity.mViewPager.setCurrentItem(2, true);
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_more).setVisible(true);
        menu.findItem(R.id.action_logout).setVisible(false);
    }
}