package com.example.plantbot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.plantbot.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    SwipeRefreshLayout swipeRefreshLayout;

    PlantAdapter plantAdapter;
    RecyclerView plantRecyclerView;
    Button addPlantButton;

    SearchView plantSearch;

    public List<String> entireListNickname = new ArrayList<>();
    public List<String> entireListName = new ArrayList<>();
    public List<String> entireListImage = new ArrayList<>();
    public List<String> entireListLight = new ArrayList<>();
    public List<String> entireListWater = new ArrayList<>();
    public List<String> entireListLastWatered = new ArrayList<>();


    private List<String> nicknameList = new ArrayList<>();
    private List<String> nameList = new ArrayList<>();
    private List<String> imageList = new ArrayList<>();
    private List<String> lightList = new ArrayList<>();
    private List<String> waterList = new ArrayList<>();
    private List<String> lastWateredList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        SharedPreferences sharedPreferences = this.getSharedPreferences("PLANTS", MODE_PRIVATE);
        // Creating an Editor object to edit(write to the file)
        SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();


        plantRecyclerView = binding.plantRecyclerView;
        plantAdapter = new PlantAdapter();
        plantSearch = binding.plantSearch;
        addPlantButton = binding.addPlantButton;

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        /*
        entireListNickname.add("Gigel");
        entireListNickname.add("Petronel");

        entireListName.add("CACTUS");
        entireListName.add("Opuntia Rubescens");

        entireListImage.add(R.drawable.cactus_dansator);
        entireListImage.add(R.drawable.petronel);

        entireListLight.add("multa");
        entireListLight.add("semi");

        entireListWater.add("2 saptamani");
        entireListWater.add("5 zile");

        entireListLastWatered.add("15/12");
        entireListLastWatered.add("21/12");

         */



        EditText txtSearch = ((EditText)plantSearch.findViewById(androidx.appcompat.R.id.search_src_text));
        //txtSearch.setHint(getResources().getString(R.string.search_hint));
        txtSearch.setHintTextColor(Color.BLACK);
        txtSearch.setTextColor(Color.BLACK);
        txtSearch.setHighlightColor(Color.BLACK);

        ImageView searchIcon = plantSearch.findViewById(androidx.appcompat.R.id.search_button);
        searchIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_search_24));

        ImageView closeIcon = plantSearch.findViewById(androidx.appcompat.R.id.search_close_btn);
        closeIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_close_24));


        if( nicknameList.size()==0 ) {

            if( sharedPreferences.contains("PLANT NUMBER") ){

                int plantNumber = sharedPreferences.getInt("PLANT NUMBER", 0);

                for (int position = 1; position <= plantNumber; position++) {


                    String nickname = sharedPreferences.getString("plant nickname" + position, "0");
                    String name = sharedPreferences.getString("plant name" + position, "0");
                    String image = sharedPreferences.getString("plant image" + position, "");

                    String light = sharedPreferences.getString("plant light" + position, "0");
                    String water = sharedPreferences.getString("plant water" + position, "0");
                    String lastWatered = sharedPreferences.getString("plant lastWatered" + position, "0");



                    nicknameList.add(nickname);
                    nameList.add(name);
                    imageList.add(image);

                    lightList.add(light);
                    waterList.add(water);
                    lastWateredList.add(lastWatered);



                    entireListNickname.add(nickname);
                    entireListName.add(name);
                    entireListImage.add(image);

                    entireListLight.add(light);
                    entireListWater.add(water);
                    entireListLastWatered.add(lastWatered);

                }

            } else {



            }

        }

        plantAdapter.updateList( entireListNickname, entireListName, entireListImage, entireListLight, entireListWater, entireListLastWatered );


        //preferencesEditor.putInt("PLANT NUMBER", 0);


        plantSearch.setImeOptions(EditorInfo.IME_ACTION_DONE);

        plantSearch.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                plantAdapter.filterSearch( true, query,

                        entireListNickname,
                        entireListName,
                        entireListImage,
                        entireListLight,
                        entireListWater,
                        entireListLastWatered,

                        nicknameList,
                        nameList,
                        imageList,
                        lightList,
                        waterList,
                        lastWateredList
                        );

                plantAdapter.updateList( nicknameList,
                        nameList,
                        imageList,
                        lightList,
                        waterList,
                        lastWateredList );

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText ) {
                plantAdapter.filterSearch( true, newText,

                        entireListNickname,
                        entireListName,
                        entireListImage,
                        entireListLight,
                        entireListWater,
                        entireListLastWatered,

                        nicknameList,
                        nameList,
                        imageList,
                        lightList,
                        waterList,
                        lastWateredList
                );

                plantAdapter.updateList( nicknameList,
                        nameList,
                        imageList,
                        lightList,
                        waterList,
                        lastWateredList );

                return false;
            }

        });

        addPlantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BottomSheetDialogAddPlant bottomSheet = new BottomSheetDialogAddPlant();
                bottomSheet.show(getSupportFragmentManager(),
                        "ModalBottomSheet");

                REFRESH();

            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);

                REFRESH();

            }
        });



        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        plantRecyclerView.setLayoutManager( linearLayoutManager );

        plantRecyclerView.setAdapter( plantAdapter );



    }

    void REFRESH(){

        SharedPreferences sharedPreferences = this.getSharedPreferences("PLANTS", MODE_PRIVATE);
        // Creating an Editor object to edit(write to the file)
        SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();

        nicknameList.clear();
        nameList.clear();
        imageList.clear();

        lightList.clear();
        waterList.clear();
        lastWateredList.clear();



        entireListNickname.clear();
        entireListName.clear();
        entireListImage.clear();

        entireListLight.clear();
        entireListWater.clear();
        entireListLastWatered.clear();

        int plantNumber = sharedPreferences.getInt("PLANT NUMBER", 0);

        for (int position = 1; position <= plantNumber; position++) {


            String nickname = sharedPreferences.getString("plant nickname" + position, "0");
            String name = sharedPreferences.getString("plant name" + position, "0");

            String image = sharedPreferences.getString("plant image" + position, "");
            byte[] imageAsBytes = Base64.decode(image.getBytes(), Base64.DEFAULT);
            //accountImage.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));

            String light = sharedPreferences.getString("plant light" + position, "0");
            String water = sharedPreferences.getString("plant water" + position, "0");
            String lastWatered = sharedPreferences.getString("plant lastWatered" + position, "0");



            nicknameList.add(nickname);
            nameList.add(name);
            imageList.add(image);

            lightList.add(light);
            waterList.add(water);
            lastWateredList.add(lastWatered);



            entireListNickname.add(nickname);
            entireListName.add(name);
            entireListImage.add(image);

            entireListLight.add(light);
            entireListWater.add(water);
            entireListLastWatered.add(lastWatered);

        }

        plantAdapter.updateList( nicknameList,
                nameList,
                imageList,
                lightList,
                waterList,
                lastWateredList );

        plantAdapter.notifyDataSetChanged();

    }
}