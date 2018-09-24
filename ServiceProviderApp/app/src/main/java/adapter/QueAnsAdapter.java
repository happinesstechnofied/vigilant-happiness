package adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.apt360.vendor.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import model.Answer;
import model.QueAnsHistory;
import model.ReviewRatting;

/**
 * Created by rajgandhi on 13/08/18.
 */

public class QueAnsAdapter extends RecyclerView.Adapter<QueAnsAdapter.MyViewHolder> {

    private List<QueAnsHistory> saveList;
    private ArrayList<Answer> answersList = new ArrayList<>();
    Context context;


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView txtQuestion, txtQuestionDisplay, txtQuestionDate;
        public TextView txtAnswer, txtAnswerDisplay, txtAnswerDate;


        public MyViewHolder(View view) {
            super(view);
            txtQuestionDisplay = (TextView) view.findViewById(R.id.txtQuestionDisplay);
            txtQuestionDate = (TextView) view.findViewById(R.id.txtQuestionDate);
            txtAnswerDisplay = (TextView) view.findViewById(R.id.txtAnswerDisplay);
            txtAnswerDate = (TextView) view.findViewById(R.id.txtAnswerDate);
            txtQuestion = (TextView) view.findViewById(R.id.txtQuestion);
            txtAnswer = (TextView) view.findViewById(R.id.txtAnswer);

        }
    }

    public QueAnsAdapter(Context context, List<QueAnsHistory> sList) {
        this.saveList = sList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_queans_adapter, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        QueAnsHistory nData = saveList.get(position);
        /*answersList = (ArrayList<Answer>) nData.getAnswers();
        Answer nAns = answersList.get(position);*/


        if (nData.getAnswers() != null && nData.getAnswers().size() > 0) {

            holder.txtQuestionDisplay.setText(nData.getQuestion());
            String qDate = nData.getDate();
            String[] qSeparated = qDate.split(" ");
            holder.txtQuestionDate.setText(qSeparated[0]);
            holder.txtAnswerDisplay.setText(nData.getAnswers().get(0).getAnswer());
            String aDate = nData.getAnswers().get(0).getDate();
            String[] aSeparated = aDate.split(" ");
            holder.txtAnswerDate.setText(aSeparated[0]);
        } else {
            holder.txtQuestionDisplay.setVisibility(View.GONE);
            holder.txtQuestionDate.setVisibility(View.GONE);
            holder.txtAnswerDisplay.setVisibility(View.GONE);
            holder.txtAnswerDate.setVisibility(View.GONE);
            holder.txtQuestion.setVisibility(View.GONE);
            holder.txtAnswer.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return saveList.size();
    }
}
