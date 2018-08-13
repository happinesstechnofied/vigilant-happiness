package adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vendorprovider.R;

import java.util.ArrayList;
import java.util.List;

import model.Answer;
import model.QueAnsHistory;

/**
 * Created by rajgandhi on 13/08/18.
 */

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.MyViewHolder> {

    private List<QueAnsHistory> saveList;
    Context context;


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView txtQuestion, txtAnswerDate;
        public ImageView imgQuestionStatus;

        public MyViewHolder(View view) {
            super(view);
            txtQuestion = (TextView) view.findViewById(R.id.txtQuestion);
            txtAnswerDate = (TextView) view.findViewById(R.id.txtAnswerDate);
            imgQuestionStatus = (ImageView) view.findViewById(R.id.imgQuestionStatus);
        }
    }

    public ReplyAdapter(Context context, List<QueAnsHistory> sList) {
        this.saveList = sList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_reply_fragment, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        QueAnsHistory nData = saveList.get(position);

        if(nData.getAnswers() != null && nData.getAnswers().size() > 0){
            holder.txtQuestion.setText(nData.getQuestion());
            String qDate = nData.getDate();
            String[] qSeparated = qDate.split(" ");
            holder.txtAnswerDate.setText(qSeparated[0]);
            holder.imgQuestionStatus.setBackgroundResource(R.drawable.ic_read_question);
        }else{
            holder.txtQuestion.setText(nData.getQuestion());
            String qDate = nData.getDate();
            String[] qSeparated = qDate.split(" ");
            holder.txtAnswerDate.setText(qSeparated[0]);
            holder.imgQuestionStatus.setBackgroundResource(R.drawable.ic_unread_question);
        }
    }

    @Override
    public int getItemCount() {
        return saveList.size();
    }
}
