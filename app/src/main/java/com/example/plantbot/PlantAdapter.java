package com.example.plantbot;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.example.plantbot.databinding.PlantCardModelBinding;
import com.google.android.material.shape.CornerFamily;


public class PlantAdapter extends RecyclerView.Adapter<PlantAdapter.ViewHolder> {

    //List<String> ticketList = new ArrayList<>();
    private List<String> plantNicknameList = new ArrayList<>();
    private List<String> plantNameList = new ArrayList<>();
    private List<String> imageList = new ArrayList<>();
    private List<String> lightPreferenceList = new ArrayList<>();
    private List<String> waterPreferenceList = new ArrayList<>();
    private List<String> lastWateredList = new ArrayList<>();


    public void updateList(List<String> plantNicknameList, List<String> plantNameList, List <String> imageList,
                           List<String> lightPreferenceList, List<String> waterPreferenceList,
                           List<String> lastWateredList) {

        this.plantNicknameList = plantNicknameList;
        this.plantNameList = plantNameList;
        this.imageList = imageList;

        this.lightPreferenceList = lightPreferenceList;
        this.waterPreferenceList = waterPreferenceList;
        this.lastWateredList = lastWateredList;

        this.notifyDataSetChanged();

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        PlantCardModelBinding plantCardModelBinding= PlantCardModelBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(plantCardModelBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String lp =  "Preferinta mea de lumina: " + lightPreferenceList.get(position);
        String wp =  "Interval de udat:  " + waterPreferenceList.get(position);
        String lw =  "Ultima data udat: " + lastWateredList.get(position);

        holder.bindView(
                plantNicknameList.get(position),
                plantNameList.get(position),
                imageList.get(position),

                lp,
                wp,
                lw);


        //holder.itemView.setVisibility( View.GONE );


    }

    @Override
    public int getItemCount() {
        //Log.d(TAG, "getItemCount: " + stringList.size());
        return plantNicknameList.size();
    }


    public void filterSearch (boolean doClear, String textInit,

                              List<String> entireListNickname,
                              List<String> entireListName,
                              List<String> entireListImage,
                              List<String> entireListLight,
                              List<String> entireListWater,
                              List<String> entireListLastWatered,

                              List<String> listNickname,
                              List<String> listName,
                              List<String> listImage,
                              List<String> listLight,
                              List<String> listWater,
                              List<String> listLastWatered

                              ) {

        if(doClear){
            listNickname.clear();
            listName.clear();
            listImage.clear();
            listLight.clear();
            listWater.clear();
            listLastWatered.clear();

            Log.d("dupa clear", "");

        }




        if( textInit.equals("STERGERE") ){


            Log.d("dupa stergere", "");

        } else {

            String normalized = Normalizer.normalize(textInit, Normalizer.Form.NFD)
                    .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

            String text = normalized.toLowerCase(Locale.ROOT);

            for(int i=0; i<entireListNickname.size(); i++){

                String nrm = Normalizer.normalize( entireListNickname.get(i), Normalizer.Form.NFD)
                        .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

                String item = nrm.toLowerCase(Locale.ROOT);

                if( item.contains(text) ){

                    listNickname.add( entireListNickname.get(i) );
                    listName.add(entireListName.get(i));
                    listImage.add(entireListImage.get(i));
                    listLight.add(entireListLight.get(i));
                    listWater.add(entireListWater.get(i));
                    listLastWatered.add(entireListLastWatered.get(i));

                    Log.d( "added ", entireListNickname.get(i) );

                }

            }

        }

        this.updateList( listNickname, listName,  listImage, listLight, listWater, listLastWatered );

        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        //        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//        }
        PlantCardModelBinding plantCardModelBinding;


        public ViewHolder (@NonNull PlantCardModelBinding plantCardModelBinding) {
            super(plantCardModelBinding.getRoot());
            this.plantCardModelBinding = plantCardModelBinding;


            plantCardModelBinding.waterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM");
                    LocalDateTime now = LocalDateTime.now();
                    String dateToday = dtf.format(now);

                    plantCardModelBinding.plantLastWatered.setText("Ultima data udat: " + dateToday);

                    Context context = plantCardModelBinding.getRoot().getContext();

                    SharedPreferences sharedPreferences = context.getSharedPreferences("PLANTS", MODE_PRIVATE);
                    SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
                    int position = getAbsoluteAdapterPosition();
                    position++;
                    preferencesEditor.putString("plant lastWatered"+position, dateToday);
                    preferencesEditor.commit();

                }
            });

            plantCardModelBinding.plantCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    /**
                     * TODO: meniu de editare planta + BUTON DE DELETE
                     */

                    Context context = plantCardModelBinding.getRoot().getContext();

                    FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();

                    SharedPreferences sharedPreferences = context.getSharedPreferences("PLANTS", MODE_PRIVATE);
                    SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
                    int position = getAbsoluteAdapterPosition();
                    position++;
                    preferencesEditor.putInt("query", position);
                    preferencesEditor.commit();

                    BottomSheetDialogEditPlant bottomSheet = new BottomSheetDialogEditPlant();
                    bottomSheet.show(fragmentManager,
                            "ModalBottomSheet");



                }
            });

            plantCardModelBinding.plantCard.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    int position = getAbsoluteAdapterPosition();

                    Context context = plantCardModelBinding.getRoot().getContext();
                    FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();

                    SharedPreferences sharedPreferences = context.getSharedPreferences("PLANTS", MODE_PRIVATE);
                    SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();

                    preferencesEditor.putInt("query delete", position+1);

                    preferencesEditor.commit();


                    BottomSheetDialogDeletePlant bottomSheet = new BottomSheetDialogDeletePlant();
                    bottomSheet.show(fragmentManager,
                            "ModalBottomSheet");



                    REFRESH(context);



                    return false;
                }
            });


        }

        public void bindView(String nickname, String name, String image, String light, String water, String lastWatered) {

            plantCardModelBinding.plantNickname.setText(nickname);
            plantCardModelBinding.plantScientificName.setText(name);


            byte[] imageAsBytes = Base64.decode(image.getBytes(), Base64.DEFAULT);
            plantCardModelBinding.plantPicture.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
            //.setImageResource(image);

            plantCardModelBinding.plantLightPreference.setText(light);
            plantCardModelBinding.plantWaterPreference.setText(water);
            plantCardModelBinding.plantLastWatered.setText(lastWatered);

        }


        public void deleteItem(int position) {

            plantNicknameList.remove(position);
            plantNameList.remove(position);
            imageList.remove(position);
            lightPreferenceList.remove(position);
            waterPreferenceList.remove(position);
            lastWateredList.remove(position);

            notifyItemRemoved(position);
            notifyItemRangeChanged(position, plantNicknameList.size());
            notifyItemRangeChanged(position, plantNameList.size());
            notifyItemRangeChanged(position, imageList.size());
            notifyItemRangeChanged(position, lightPreferenceList.size());
            notifyItemRangeChanged(position, waterPreferenceList.size());
            notifyItemRangeChanged(position, lastWateredList.size());



            //holder.itemView.setVisibility(View.GONE);
        }

        private void deleteCard(Context context){

            SharedPreferences sharedPreferences = context.getSharedPreferences("PLANTS", MODE_PRIVATE);
            SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();


            int number = sharedPreferences.getInt( "PLANT NUMBER", 0 );
            number--;
            preferencesEditor.putInt( "PLANT NUMBER", number );

            for(int pos=0; pos<plantNicknameList.size(); pos++){

                int sharedposition = pos+1;
                preferencesEditor.putString( "plant nickname" + sharedposition, plantNicknameList.get(pos) );
                preferencesEditor.putString( "plant name" + sharedposition, plantNameList.get(pos) );
                preferencesEditor.putString( "plant image" + sharedposition, imageList.get(pos) );

                preferencesEditor.putString( "plant light" + sharedposition, lightPreferenceList.get(pos) );
                preferencesEditor.putString( "plant water" + sharedposition, waterPreferenceList.get(pos) );
                preferencesEditor.putString( "plant lastWatered" + sharedposition, lastWateredList.get(pos) );


            }

            /*
            //se sterge ultimu
            int last = plantNicknameList.size();
            last++;
            preferencesEditor.remove("plant nickname"+last);
            preferencesEditor.remove("plant name"+last);
            preferencesEditor.remove("plant image"+last);

            preferencesEditor.remove("plant light"+last);
            preferencesEditor.remove("plant water"+last);
            preferencesEditor.remove("plant lastWatered"+last);

             */



            preferencesEditor.commit();

        }

        public void REFRESH(Context context){

            SharedPreferences sharedPreferences = context.getSharedPreferences("PLANTS", MODE_PRIVATE);
            SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();

            plantNicknameList.clear();
            plantNameList.clear();
            imageList.clear();

            lightPreferenceList.clear();
            waterPreferenceList.clear();
            lastWateredList.clear();

            int plantNumber = sharedPreferences.getInt("PLANT NUMBER", 0);

            for (int position = 1; position <= plantNumber; position++) {


                String nickname = sharedPreferences.getString("plant nickname" + position, "0");
                String name = sharedPreferences.getString("plant name" + position, "0");
                String image = sharedPreferences.getString("plant image" + position, "");


                String light = sharedPreferences.getString("plant light" + position, "0");
                String water = sharedPreferences.getString("plant water" + position, "0");
                String lastWatered = sharedPreferences.getString("plant lastWatered" + position, "0");



                plantNicknameList.add(nickname);
                plantNameList.add(name);
                imageList.add(image);

                lightPreferenceList.add(light);
                waterPreferenceList.add(water);
                lastWateredList.add(lastWatered);



            }


            notifyDataSetChanged();

        }


    }

}