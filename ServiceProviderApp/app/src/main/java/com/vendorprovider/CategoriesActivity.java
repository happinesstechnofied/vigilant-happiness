package com.vendorprovider;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.connection.ConnectionDetector;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;

import adapter.CategoryAdapter;
import fragment.CategoryFragment;
import kprogresshud.KProgressHUD;
import model.Categories;
import util.AppConstants;
import util.RecyclerItemClickListener;

public class CategoriesActivity extends AppCompatActivity {

    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    private KProgressHUD hud;
    private RelativeLayout layoutLostConnection;
    RelativeLayout layoutNoCategory;
    RecyclerView recyclerView;
    CategoryAdapter adapter;
    ArrayList<Categories> categoriesList = new ArrayList<>();
    ArrayList<Categories> subCategoriesList = new ArrayList<>();
    String category;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    Dialog dialog;
    TextInputEditText txtCategory;
    Button btnSubmit;
    ImageButton btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categories_activity);
        initView();

        cd = new ConnectionDetector(CategoriesActivity.this);
        isInternetPresent = cd.isConnectingToInternet();

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = preferences.edit();

        Intent intent = getIntent();
        category = intent.getStringExtra("Category");

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Select Main Category");

        if (isInternetPresent) {
            hud = KProgressHUD.create(CategoriesActivity.this).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Please wait");
            hud.show();
            GetMainCategory();
        } else {
            layoutLostConnection.setVisibility(View.VISIBLE);
        }

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO Handle item click

                        editor.putString("Category_ID", categoriesList.get(position).getParentCatId().toString());
                        editor.putString("Category_Title", categoriesList.get(position).getParentCatName());
                        String cateCheck = preferences.getString("Category", "");
                        if (cateCheck.equals("Main")) {

                            editor.putInt("Category_Position", position);
                            editor.putString("main_category", categoriesList.get(position).getParentCatId().toString());
                            editor.putString("main_category_name", categoriesList.get(position).getParentCatName());

                            if (categoriesList.size() - 1 == position) {
                                editor.putString("Category_ID", "0");

                                editor.commit();
                                customDialog();
                            } else {
                                CategoryFragment.txtCategoryName.setText(categoriesList.get(position).getParentCatName());
                                CategoryFragment.txtSubCategoryName.setHint("Select Sub-Category");
                                CategoryFragment.txtSubCategoryName.setHintTextColor(Color.BLACK);
                                CategoryFragment.txtSubCategoryName.setText("");
                                editor.commit();
                                onBackPressed();
                            }
                        } else {
                            editor.putString("sub_category", categoriesList.get(position).getParentCatId().toString());
                            editor.putString("sub_category_name", categoriesList.get(position).getParentCatName());
                            if (categoriesList.size() - 1 == position) {
                                customDialog();
                            } else {
                                CategoryFragment.txtSubCategoryName.setText(categoriesList.get(position).getParentCatName());
                                editor.commit();
                                onBackPressed();
                            }
                        }
                    }
                })
        );
    }

    private void GetMainCategory() {

        try {
            JSONArray jsonArray = CategoryFragment.finalData;
            TypeReference<ArrayList<Categories>> typeRef = new TypeReference<ArrayList<Categories>>() {
            };

            categoriesList = AppConstants.getMapper().readValue(jsonArray.toString(), typeRef);

            Categories other_category = new Categories();

            other_category.setParentCatId(0);
            other_category.setParentCatName("Other");
            other_category.setImage("");
            other_category.setImageMedium("");
            other_category.setImageSmall("");

            categoriesList.add(other_category);
            if (category.equalsIgnoreCase("Main")) {
                getSupportActionBar().setTitle("Select Category");
                adapter = new CategoryAdapter(CategoriesActivity.this, categoriesList);
            } else {

                getSupportActionBar().setTitle("Select Sub-Category");
                int position = preferences.getInt("Category_Position", 0);
                String tempData = AppConstants.getMapper().writeValueAsString(categoriesList.get(position).getSubcategory());
                categoriesList = new ArrayList<>();
                if (tempData != null && !tempData.equalsIgnoreCase("null"))
                    categoriesList = AppConstants.getMapper().readValue(tempData, typeRef);

                other_category = new Categories();

                other_category.setParentCatId(0);
                other_category.setParentCatName("Other");
                other_category.setImage("");
                other_category.setImageMedium("");
                other_category.setImageSmall("");
                categoriesList.add(other_category);

                if (categoriesList.size() == 0) {
                    layoutNoCategory.setVisibility(View.VISIBLE);
                } else {
                    adapter = new CategoryAdapter(CategoriesActivity.this, categoriesList);
                }
            }
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapter);
            hud.dismiss();

        } catch (JsonParseException e) {

        } catch (JsonMappingException e) {

        } catch (IOException e) {

        }
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutLostConnection = (RelativeLayout) findViewById(R.id.layoutLostConnection);
        layoutNoCategory = (RelativeLayout) findViewById(R.id.layoutNoCategory);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void customDialog() {
        dialog = new Dialog(CategoriesActivity.this);
        dialog.setContentView(R.layout.your_custom_dialog);

        txtCategory = (TextInputEditText) dialog.findViewById(R.id.txtCategory);
        btnSubmit = (Button) dialog.findViewById(R.id.btnSubmit);
        btnClose = (ImageButton) dialog.findViewById(R.id.btnClose);


        dialog.show();
        dialog.setCancelable(false);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int displayWidth = displayMetrics.widthPixels;
        int displayHeight = displayMetrics.heightPixels;
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        int dialogWindowWidth = (int) (displayWidth * 0.9f);
        layoutParams.width = dialogWindowWidth;
        dialog.getWindow().setAttributes(layoutParams);


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cateCheck = preferences.getString("Category", "");
                if (cateCheck.equals("Main")) {
                    editor.putString("main_category", "0");
                    editor.putString("main_category_name", txtCategory.getText().toString());
                    CategoryFragment.txtCategoryName.setText(txtCategory.getText().toString());
                    CategoryFragment.txtSubCategoryName.setHint("Select Sub-Category");
                    CategoryFragment.txtSubCategoryName.setHintTextColor(Color.BLACK);
                    CategoryFragment.txtSubCategoryName.setText("");
                    dialog.dismiss();
                    onBackPressed();
                } else {
                    editor.putString("sub_category", "0");
                    editor.putString("sub_category_name", txtCategory.getText().toString());
                    CategoryFragment.txtSubCategoryName.setText(txtCategory.getText().toString());
                    onBackPressed();

                }
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}