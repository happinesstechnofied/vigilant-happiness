package fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.apt360.vendor.MainActivity;
import com.apt360.vendor.R;

public class SuccessfullySubmitFragment extends Fragment {
    public static SuccessfullySubmitFragment newInstance() {
        SuccessfullySubmitFragment fragment = new SuccessfullySubmitFragment();
        return fragment;
    }

    Button btnBack;
    SharedPreferences preferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.successfully_submit_fragment, container, false);

        btnBack = (Button) view.findViewById(R.id.btnBack);

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int image_counter = 0;
                do {
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove("feature_" + image_counter);
            editor.commit();
                    image_counter++;
                } while (!preferences.getString("feature_" + image_counter, "").equalsIgnoreCase(""));


                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(intent);
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