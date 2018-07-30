package fragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.vendorprovider.R;
import com.vendorprovider.ServiceCreationActivity;
import com.vendorprovider.ViewServicesActivity;

import java.util.Calendar;


public class AdditionalFragment extends Fragment {
    public static AdditionalFragment newInstance() {
        AdditionalFragment fragment = new AdditionalFragment();
        return fragment;
    }

    static final int TIME_DIALOG_ID = 1111;
    static final int TIME_DIALOG2_ID = 2222;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Button btnNext;
    Button btnBack;
    RadioButton radioBtnOneTimePayment, radioBtnRecursivePayment;
    RadioButton radioBtnEveryMonth, radioBtnSixMonth, radioBtnYearly;
    RelativeLayout paymentOptionLayout;
    TextInputEditText txtOpeningHours, txtClosingHours;
    TextInputEditText txtSortDescription, txtMrpPrice, txtSalePrice;
    String sortDes, mrpPrice, salePrice, opningHours, closingHours;
    String paymentOption = "", paymentType = "";
    private int hr;
    private int min;
    String checkEditMode;
    int position;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.additional_details_fragment, container, false);

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        checkEditMode = preferences.getString("EditService", "");
        position = preferences.getInt("Position", 0);
        editor = preferences.edit();
        radioBtnOneTimePayment = (RadioButton) view.findViewById(R.id.radioBtnOneTimePayment);
        radioBtnRecursivePayment = (RadioButton) view.findViewById(R.id.radioBtnRecursivePayment);
        radioBtnEveryMonth = (RadioButton) view.findViewById(R.id.radioBtnEveryMonth);
        radioBtnSixMonth = (RadioButton) view.findViewById(R.id.radioBtnSixMonth);
        radioBtnYearly = (RadioButton) view.findViewById(R.id.radioBtnYearly);
        paymentOptionLayout = (RelativeLayout) view.findViewById(R.id.paymentOptionLayout);
        txtOpeningHours = (TextInputEditText) view.findViewById(R.id.txtOpeningHours);
        txtClosingHours = (TextInputEditText) view.findViewById(R.id.txtClosingHours);
        txtSortDescription = (TextInputEditText) view.findViewById(R.id.txtSortDescription);
        txtMrpPrice = (TextInputEditText) view.findViewById(R.id.txtMrpPrice);
        txtSalePrice = (TextInputEditText) view.findViewById(R.id.txtSalePrice);
        btnNext = (Button) view.findViewById(R.id.btnNext);
        btnBack = (Button) view.findViewById(R.id.btnBack);

        if (checkEditMode.equals("Edit")) {
            txtSortDescription.setText(ViewServicesActivity.arrayList.get(position).getSortDescription());
            txtOpeningHours.setText(ViewServicesActivity.arrayList.get(position).getOpeningHour());
            txtClosingHours.setText(ViewServicesActivity.arrayList.get(position).getClosingHour());
            txtMrpPrice.setText(ViewServicesActivity.arrayList.get(position).getMaximumRetailPrice());
            txtSalePrice.setText(ViewServicesActivity.arrayList.get(position).getSalePrice());
            if (ViewServicesActivity.arrayList.get(position).getPaymentOptions().equals("One Time Payment")) {
                radioBtnOneTimePayment.setChecked(true);
            } else if (ViewServicesActivity.arrayList.get(position).getPaymentOptions().equals("Recursive Payment")) {
                radioBtnRecursivePayment.setChecked(true);
                paymentOptionLayout.setVisibility(View.VISIBLE);
                if (ViewServicesActivity.arrayList.get(position).getRecursivePayment().equals("Every Months")) {
                    radioBtnEveryMonth.setChecked(true);
                } else if (ViewServicesActivity.arrayList.get(position).getRecursivePayment().equals("Six Month")) {
                    radioBtnSixMonth.setChecked(true);
                } else if (ViewServicesActivity.arrayList.get(position).getRecursivePayment().equals("Yearly")) {
                    radioBtnYearly.setChecked(true);
                }
            }
        }

        final Calendar c = Calendar.getInstance();
        hr = 8;
        min = 0;
        updateTime(hr, min, txtOpeningHours);
        hr = 20;
        updateTime(hr, min, txtClosingHours);

        txtOpeningHours.setInputType(InputType.TYPE_NULL);

        txtOpeningHours.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    hr = 8;
                    min = 0;
                    createdDialog(1111).show();
                }
            }
        });
        txtOpeningHours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hr = 8;
                min = 0;
                createdDialog(1111).show();
                //setTimeData();
            }
        });

        txtClosingHours.setInputType(InputType.TYPE_NULL);
        txtClosingHours.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    hr = 20;
                    min = 0;
                    createdDialog(2222).show();
                }
            }
        });
        txtClosingHours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hr = 20;
                min = 0;
                createdDialog(2222).show();
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sortDes = txtSortDescription.getText().toString();
                mrpPrice = txtMrpPrice.getText().toString();
                salePrice = txtSalePrice.getText().toString();
                opningHours = txtOpeningHours.getText().toString();
                closingHours = txtClosingHours.getText().toString();

                if (TextUtils.isEmpty(sortDes)) {
                    txtSortDescription.setError("Please enter description!");
                    txtSortDescription.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(opningHours)) {
                    txtOpeningHours.setError("Please enter opening hours!");
                    txtOpeningHours.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(closingHours)) {
                    txtClosingHours.setError("Please enter closing hours!");
                    txtClosingHours.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(mrpPrice)) {
                    txtMrpPrice.setError("Please enter MRP!");
                    txtMrpPrice.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(salePrice)) {
                    txtSalePrice.setError("Please enter sale price!");
                    txtSalePrice.requestFocus();
                    return;
                }

                if(Integer.valueOf(mrpPrice) < Integer.valueOf(salePrice)){
                    txtSalePrice.setError("Price must be lower then MRP!");
                    txtSalePrice.requestFocus();
                    return;
                }

                if (paymentOption.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "Please select payment option!", Toast.LENGTH_SHORT).show();
                } else if (paymentOption.equalsIgnoreCase("Recursive Payment") && TextUtils.isEmpty(paymentType)) {
                    Toast.makeText(getContext(), "Please select payment condition!", Toast.LENGTH_SHORT).show();
                } else {
                    editor.putString("sort_description", sortDes);
                    editor.putString("opening_hours", opningHours);
                    editor.putString("closing_hours", closingHours);
                    editor.putString("mrp_price", mrpPrice);
                    editor.putString("sale_price", salePrice);
                    editor.putString("payment_option", paymentOption);
                    editor.putString("payment_type", paymentType);
                    editor.commit();
                    ServiceCreationActivity.mViewPager.setCurrentItem(3, true);
                }
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServiceCreationActivity.mViewPager.setCurrentItem(1, true);
            }
        });
        radioBtnOneTimePayment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (radioBtnOneTimePayment.isChecked()) {
                    paymentOption = radioBtnOneTimePayment.getText().toString();
                    paymentType = "";
                } else {
                }
            }
        });
        radioBtnRecursivePayment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (radioBtnRecursivePayment.isChecked()) {
                    paymentOptionLayout.setVisibility(View.VISIBLE);
                    paymentOption = radioBtnRecursivePayment.getText().toString();
                } else {
                    paymentOptionLayout.setVisibility(View.GONE);
                }
            }
        });
        radioBtnEveryMonth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (radioBtnEveryMonth.isChecked()) {
                    paymentType = radioBtnEveryMonth.getText().toString();
                } else {
                }
            }
        });
        radioBtnSixMonth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (radioBtnSixMonth.isChecked()) {
                    paymentType = radioBtnSixMonth.getText().toString();
                } else {
                }
            }
        });
        radioBtnYearly.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (radioBtnYearly.isChecked()) {
                    paymentType = radioBtnYearly.getText().toString();
                } else {
                }
            }
        });

        return view;
    }


    protected Dialog createdDialog(int id) {
        switch (id) {
            case TIME_DIALOG_ID:
                return new TimePickerDialog(getContext(), timePickerListener, hr, min, false);
            case TIME_DIALOG2_ID:
                return new TimePickerDialog(getContext(), timePickerListener2, hr, min, false);
        }
        return null;
    }

    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
// TODO Auto-generated method stub
            hr = hourOfDay;
            min = minutes;
            updateTime(hr, min, txtOpeningHours);
        }
    };

    private TimePickerDialog.OnTimeSetListener timePickerListener2 = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
// TODO Auto-generated method stub
            hr = hourOfDay;
            min = minutes;
            updateTime(hr, min, txtClosingHours);
        }
    };

    private static String utilTime(int value) {
        if (value < 10)
            return "0" + String.valueOf(value);
        else
            return String.valueOf(value);
    }

    private void updateTime(int hours, int mins, TextInputEditText txtInput) {
        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";
        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);
        String aTime = new StringBuilder().append(hours).append(':').append(minutes).append(" ").append(timeSet).toString();
        txtInput.setText(aTime);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_more).setVisible(true);
        menu.findItem(R.id.action_logout).setVisible(false);
    }
}