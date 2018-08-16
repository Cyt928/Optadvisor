package utf8.optadvisor.util;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import utf8.optadvisor.R;

public class RightAdapter extends RecyclerView.Adapter<RightAdapter.ViewHolder> {
    private List<String[]> put_option_info;
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView right1;
        TextView right2;
        TextView right3;
        TextView right4;
        TextView right5;
        TextView right6;
        public ViewHolder(View view){
            super(view);
            right1=(TextView) view.findViewById(R.id.right1);
            right2=(TextView) view.findViewById(R.id.right2);
            right3=(TextView) view.findViewById(R.id.right3);
            right4=(TextView) view.findViewById(R.id.right4);
            right5=(TextView) view.findViewById(R.id.right5);
            right6=(TextView) view.findViewById(R.id.right6);
        }
    }
    public RightAdapter(List<String[]> put_option_info){
        this.put_option_info=put_option_info;
    }
    @Override
    public RightAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.put_option_layout,parent,false);
        RightAdapter.ViewHolder holder=new RightAdapter.ViewHolder(view);
        return holder;
    }
    @Override
    public void onBindViewHolder(RightAdapter.ViewHolder holder, int position){
        String[] info=put_option_info.get(position);
        holder.right1.setText(info[37]);
        holder.right2.setText(info[6]);
        holder.right3.setText(info[40]);
        holder.right4.setText(info[39]);
        holder.right5.setText(info[3]);
        holder.right6.setText(info[1]);
    }
    @Override
    public int getItemCount(){
        return put_option_info.size();
    }
}
