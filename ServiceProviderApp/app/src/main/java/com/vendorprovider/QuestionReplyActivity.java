package com.vendorprovider;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class QuestionReplyActivity extends AppCompatActivity {

    TextView txtQuestion, txtAnswer;
    TextInputEditText txtAnswerReply;
    CheckBox checkPolicy;
    Button txtSubmitNow;
    String setAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_reply_activity);
        initView();

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Reply");

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
                        txtAnswerReply.setText("");
                        txtAnswer.setText(setAnswer);
                    }
                }else{
                    Toast.makeText(QuestionReplyActivity.this,"Please Accept Policy!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void initView() {
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