package com.vendorprovider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import model.QueAnsHistory;
import services.Services;

public class QuestionReplyActivity extends AppCompatActivity {

    TextView txtQuestion, txtAnswer;
    TextView txtAns;
    TextView txtQueDate, txtAnsDate;
    TextInputEditText txtAnswerReply;
    CheckBox checkPolicy;
    Button txtSubmitNow;
    String setAnswer;
    SharedPreferences preferences;
    int position;
    RequestQueue requestQueue;
    String serviceID, questionID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_reply_activity);
        preferences = PreferenceManager.getDefaultSharedPreferences(QuestionReplyActivity.this);
        position = preferences.getInt("Position",0);
        initView();

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Reply");

        QueAnsHistory nData = ReplyFragment.arrayList.get(position);
        if(nData.getAnswers() != null && nData.getAnswers().size() > 0){
            txtQuestion.setText(nData.getQuestion());
            txtAnswer.setText(nData.getAnswers().get(0).getAnswer());
            String qDate = nData.getDate();
            String[] qSeparated = qDate.split(" ");
            txtQueDate.setText(qSeparated[0]);

            String aDate = nData.getAnswers().get(0).getDate();
            String[] aSeparated = aDate.split(" ");
            txtAnsDate.setText(aSeparated[0]);

            serviceID = nData.getServiceId();
            questionID = nData.getQuestionId();

            txtSubmitNow.setEnabled(false);
            txtAnswerReply.setEnabled(false);
            checkPolicy.setChecked(true);
            checkPolicy.setEnabled(false);

        }else{

            serviceID = nData.getServiceId();
            questionID = nData.getQuestionId();

            txtQuestion.setText(nData.getQuestion());
            String qDate = nData.getDate();
            String[] qSeparated = qDate.split(" ");
            txtQueDate.setText(qSeparated[0]);
            txtAns.setVisibility(View.GONE);
            txtAnsDate.setVisibility(View.GONE);

        }
        txtSubmitNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAnswer = txtAnswerReply.getText().toString();
                if(checkPolicy.isChecked()==true){
                    if(TextUtils.isEmpty(setAnswer)){
                        txtAnswerReply.setError("Please Enter Your Answer!");
                        txtAnswerReply.requestFocus();
                    }
                    else{
                        SetUserAnswer(serviceID, questionID, setAnswer);
                    }
                }else{
                    Toast.makeText(QuestionReplyActivity.this,"Please Accept Policy!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void SetUserAnswer(final String serviceID, final String questionID, final String setAnswer) {

        StringRequest postRequest = new StringRequest(Request.Method.POST, Services.ANS_GIVE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jObj = new JSONObject(response.toString());
                            String status = jObj.getString("status");
                            if (status.matches("success")) {
                                txtAnswerReply.setText("");
                                txtAns.setVisibility(View.VISIBLE);
                                txtAnsDate.setVisibility(View.VISIBLE);
                                txtAnswer.setText(QuestionReplyActivity.this.setAnswer);
                                txtSubmitNow.setEnabled(false);
                                txtAnswerReply.setEnabled(false);
                                checkPolicy.setChecked(true);
                                checkPolicy.setEnabled(false);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(QuestionReplyActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Service", "submit_answer");
                if (preferences.getString("user_data", "") != null && !preferences.getString("user_data", "").equalsIgnoreCase("")) {
                    try {
                        JSONObject jObj = new JSONObject(preferences.getString("user_data", ""));
                        params.put("user_id", jObj.getString("user_id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Intent intent = new Intent(QuestionReplyActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                params.put("service_id", serviceID);
                params.put("question_id", questionID);
                params.put("answer", setAnswer);
                return params;
            }
        };
        requestQueue = Volley.newRequestQueue(QuestionReplyActivity.this);
        postRequest.setShouldCache(false);
        requestQueue.add(postRequest);

    }

    private void initView() {
        txtAnsDate = (TextView) findViewById(R.id.txtAnsDate);
        txtQueDate = (TextView) findViewById(R.id.txtQueDate);
        txtAns = (TextView) findViewById(R.id.txtAns);
        txtQuestion = (TextView) findViewById(R.id.txtQuestion);
        txtAnswer = (TextView) findViewById(R.id.txtAnswer);
        txtAnswerReply = (TextInputEditText)findViewById(R.id.txtAnswerReply);
        checkPolicy = (CheckBox) findViewById(R.id.checkPolicy);
        txtSubmitNow = (Button) findViewById(R.id.txtSubmitNow);
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
}