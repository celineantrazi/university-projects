package com.example.rubysgoodies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomAdapter extends BaseAdapter {
    private Context context;
    private String[] menuItems;
    private int[] menuItemImages;
    private LayoutInflater inflater;
    public CustomAdapter(Context context, String[] menuItems, int[] menuItemImages) {
        this.context = context;
        this.menuItems = menuItems;
        this.menuItemImages = menuItemImages;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return menuItems.length;
    }

    @Override
    public Object getItem(int position) {
        return menuItems[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.menu_item_list, parent, false);
        }
        ImageView itemImage = convertView.findViewById(R.id.item_image);
        TextView itemName = convertView.findViewById(R.id.item_name);
        itemImage.setImageResource(menuItemImages[position]);
        itemName.setText(menuItems[position]);
        return convertView;
    }
}
