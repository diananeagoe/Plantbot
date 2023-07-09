package com.example.plantbot;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BottomSheetDialogDeletePlant extends BottomSheetDialogFragment {


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
            ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.bottom_sheet_layout_delete_plant,
                container, false);



        Button cancelButton= v.findViewById(R.id.cancel_delete_button);
        Button continueButton= v.findViewById(R.id.continue_delete_button);



        SharedPreferences sharedPreferences = getContext().getSharedPreferences("PLANTS", MODE_PRIVATE);
        // Creating an Editor object to edit(write to the file)
        SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismiss();

            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int position = sharedPreferences.getInt("query delete", -1);
                deleteCard( getContext(),  position);
                dismiss();
            }
        });

        return v;
    }




    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{

        } catch (RuntimeException e)
        {
            throw  new RuntimeException(context.toString()+" Must implement method");
        }

    }


    private void deleteCard(Context context, int position){

        SharedPreferences sharedPreferences = context.getSharedPreferences("PLANTS", MODE_PRIVATE);
        SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();


        int number = sharedPreferences.getInt( "PLANT NUMBER", 0 );

        if(position > 0){

            for(int pos=position; pos<number; pos++){

                int sharedposition = pos+1;
                String nickname1 = sharedPreferences.getString("plant nickname"+sharedposition, "");
                preferencesEditor.putString( "plant nickname" + pos, nickname1 );

                String name1 = sharedPreferences.getString("plant name"+sharedposition, "");
                preferencesEditor.putString( "plant name" + pos, name1 );

                String image1 = sharedPreferences.getString("plant image"+sharedposition, "");
                preferencesEditor.putString( "plant image" + pos, image1 );



                String light1 = sharedPreferences.getString("plant light"+sharedposition, "");
                preferencesEditor.putString( "plant light" + pos, light1 );

                String water1 = sharedPreferences.getString("plant water"+sharedposition, "");
                preferencesEditor.putString( "plant water" + pos, water1 );

                String lastWatered1 = sharedPreferences.getString("plant lastWatered"+sharedposition, "");
                preferencesEditor.putString( "plant lastWatered" + pos, lastWatered1 );


            }


            //se sterge ultimu
            int last = number;

            preferencesEditor.remove("plant nickname"+last);
            preferencesEditor.remove("plant name"+last);
            preferencesEditor.remove("plant image"+last);

            preferencesEditor.remove("plant light"+last);
            preferencesEditor.remove("plant water"+last);
            preferencesEditor.remove("plant lastWatered"+last);



            number--;
            preferencesEditor.putInt( "PLANT NUMBER", number );



            preferencesEditor.commit();

        }


    }

}