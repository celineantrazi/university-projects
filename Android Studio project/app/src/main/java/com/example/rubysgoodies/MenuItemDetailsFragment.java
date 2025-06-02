package com.example.rubysgoodies;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


public class MenuItemDetailsFragment extends Fragment {

    private TextView tvDate;
    public MenuItemDetailsFragment() {
        // Required empty public constructor
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_item_details, container, false);
        TextView itemName = view.findViewById(R.id.item_name);
        TextView itemDesc = view.findViewById(R.id.item_desc);
        ImageView itemImage = view.findViewById(R.id.item_image);
        tvDate = view.findViewById(R.id.txtDate);
        Bundle args = getArguments();
        if (args != null) {
            itemName.setText(args.getString("itemName"));
            itemDesc.setText(args.getString("itemDesc"));
            itemImage.setImageResource(args.getInt("itemImage"));
        }
        getData();
        return view;
    }
    private void getData() {
        String url = "https://aae.acodeo.com/getDate.php";
        RequestQueue mRequestQueue = Volley.newRequestQueue(getContext());
        StringRequest mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    tvDate.setText(response.trim());
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getActivity(), "Error fetching date", Toast.LENGTH_SHORT).show();
            }
        });
        mRequestQueue.add(mStringRequest);
    }
}
