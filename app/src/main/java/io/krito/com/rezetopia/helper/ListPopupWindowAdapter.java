package io.krito.com.rezetopia.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import io.krito.com.rezetopia.R;

public class ListPopupWindowAdapter extends BaseAdapter {

    LayoutInflater mLayoutInflater;
    List<MenuCustomItem> mItemList;
    int mLayout;

    public ListPopupWindowAdapter(Context context, List<MenuCustomItem> itemList, int layout) {
        mLayoutInflater = LayoutInflater.from(context);
        mItemList = itemList;
        mLayout = layout;
    }

    @Override
    public int getCount() {
        return mItemList.size();
    }

    @Override
    public MenuCustomItem getItem(int i) {
        return mItemList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(mLayout, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvTitle.setText(getItem(position).getTitle());
        holder.ivImage.setImageResource(getItem(position).getImageRes());

        return convertView;
    }

    static class ViewHolder {
        TextView tvTitle;
        ImageView ivImage;

        ViewHolder(View view) {
            tvTitle = view.findViewById(R.id.menuTextCustom);
            ivImage = view.findViewById(R.id.menuImageCustom);
        }
    }
}

