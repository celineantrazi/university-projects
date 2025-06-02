package com.example.rubysgoodies;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class MenuActivity extends AppCompatActivity {

    private String[] menuItems = {"Heaven on Earth", "Doughnuts", "Strawberry Cake", "Cookies", "Chocolate Buns", "Rainbow Cake",
            "Strawberry Cookies", "Tarboush", "Star Bread", "Cookie Cake", "Cinnamon Rolls", "Strawberry Tart", "Toast Bread",
            "Baguette", "Chocolate Cake", "Fatayir"};
    private String[] menuItemsDesc = {
            "Light, creamy dessert made with layers of cake, pudding, whipped cream, and fruit",
            "Sweet and fluffy doughnut glazed with chocolate",
            "Moist cake with a soft, fruity flavor, topped with fresh strawberries and creamy frosting",
            "Soft and gooey cookie packed with m&ms",
            "Delicious soft buns filled with rich, crunchy chocolate",
            "Colorful layers of cake, each with a vibrant hue, stacked with sweet frosting between them",
            "Fruity cookies with a hint of strawberry flavor, packed with strawberries",
            "Very sweet and extremely popular chocolate glazed marshmallow-like filling",
            "Soft, fluffy bread shaped like a star, with a light, buttery taste",
            "Round cookie cake, decorated with nutella and chocolate chips",
            "Moist rolls swarmed with sweet sauce",
            "Crunchy biscuit filled with cream and strawberries",
            "Fluffy bread with a hint of butter",
            "Crunchy on the outside, but soft on the inside",
            "Cloud-like consistency cake with drooping chocolate",
            "Every Lebanese' house's essentials"
    };
    private int[] menuItemImages = {
            R.drawable.heaven_on_earth2, R.drawable.doughnuts, R.drawable.strawberry_cake, R.drawable.cookies, R.drawable.chocolate_buns,
            R.drawable.rainbow_cake, R.drawable.strawberry_cookies, R.drawable.tarboush, R.drawable.star_bread, R.drawable.cookie_cake,
            R.drawable.cinnamon_rolls, R.drawable.strawberry_tart, R.drawable.toast_bread, R.drawable.baguette, R.drawable.chocolate_cake,
            R.drawable.fatayir
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ListView listView = findViewById(R.id.list_view_menu);
        View fragmentContainer2 = findViewById(R.id.fragment_container2);
        // i got this method from chatgpt to be able to keep switching between menu items and menu item details
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                    listView.setVisibility(View.VISIBLE);
                    fragmentContainer2.setVisibility(View.GONE);
                } else {
                    Intent i = new Intent(MenuActivity.this, MainActivity.class);
                    startActivity(i);
                }
            }
        });
        CustomAdapter adapter = new CustomAdapter(getApplicationContext(), menuItems, menuItemImages);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((adapterView, view, position, id) -> {
            Fragment menuItemDetailsFragment = new MenuItemDetailsFragment();
            Bundle args = new Bundle();
            args.putString("itemName", menuItems[position]);
            args.putString("itemDesc", menuItemsDesc[position]);
            args.putInt("itemImage", menuItemImages[position]);
            menuItemDetailsFragment.setArguments(args);
            listView.setVisibility(View.GONE);
            fragmentContainer2.setVisibility(View.VISIBLE);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container2, menuItemDetailsFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }
}
