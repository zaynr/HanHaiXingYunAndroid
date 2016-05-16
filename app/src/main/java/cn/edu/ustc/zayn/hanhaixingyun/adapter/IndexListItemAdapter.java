package cn.edu.ustc.zayn.hanhaixingyun.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import cn.edu.ustc.zayn.hanhaixingyun.Explorer.ExploreMainPage;
import cn.edu.ustc.zayn.hanhaixingyun.R;

/**
 * Created by zaynr on 2016/5/14.
 */
public class IndexListItemAdapter extends BaseAdapter{
    private Context context;
    private ArrayList<ExploreMainPage.ListItem> listItems;

    private class ViewHolder{
        public TextView title, date, replyNum, author;
    }

    public IndexListItemAdapter(Context context, ArrayList<ExploreMainPage.ListItem> listItems){
        super();
        this.context = context;
        this.listItems = listItems;
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int position) {
        return listItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.main_page_index_item, null);
            viewHolder.author = (TextView) convertView.findViewById(R.id.author_id);
            viewHolder.date = (TextView) convertView.findViewById(R.id.article_date);
            viewHolder.title = (TextView) convertView.findViewById(R.id.article_title);
            viewHolder.replyNum = (TextView) convertView.findViewById(R.id.article_replys);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ExploreMainPage.ListItem item = listItems.get(position);
        viewHolder.title.setText(item.title);
        viewHolder.date.setText(item.date);
        viewHolder.author.setText(item.author);
        viewHolder.replyNum.setText(item.reply);

        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return listItems.isEmpty();
    }

}
