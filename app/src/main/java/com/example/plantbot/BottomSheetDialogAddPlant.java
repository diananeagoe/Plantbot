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

public class BottomSheetDialogAddPlant extends BottomSheetDialogFragment {


    public Bitmap plantImageBitmap;
    public Uri chestie;
    boolean selectedPicture = false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
            ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.bottom_sheet_layout_add_plant,
                container, false);


        EditText nicknameButton= v.findViewById(R.id.editTextNickname);
        EditText nameButton= v.findViewById(R.id.editTextName);
        EditText lightButton = v.findViewById(R.id.editTextLight);
        EditText waterButton = v.findViewById(R.id.editTextWater);
        ImageView plantImage = v.findViewById(R.id.imageViewPlant);


        Button addPhotoButton= v.findViewById(R.id.add_photo_button);
        Button continueButton= v.findViewById(R.id.continue_button);

        if(!selectedPicture){
            plantImage.setVisibility(View.GONE);
        }

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("PLANTS", MODE_PRIVATE);
        // Creating an Editor object to edit(write to the file)
        SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();


        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);

                launchSomeActivity.launch(i);


                plantImage.setImageURI(chestie);
                plantImage.setVisibility(View.VISIBLE);

                selectedPicture = true;
                //plantImage.setImageBitmap(plantImageBitmap);
                //plantImage.setImageURI(chestie);

                //plantImage.setImageBitmap(plantImageBitmap);


            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String plantNickname=nicknameButton.getText().toString();
                String plantName=nameButton.getText().toString();
                String plantLight=lightButton.getText().toString();
                String plantWater=waterButton.getText().toString();

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM");
                LocalDateTime now = LocalDateTime.now();
                String dateToday = dtf.format(now);

                int plantNumber = sharedPreferences.getInt("PLANT NUMBER", 0);
                plantNumber++;
                preferencesEditor.putInt("PLANT NUMBER", plantNumber);

                preferencesEditor.putString("plant nickname"+plantNumber, plantNickname);
                preferencesEditor.putString("plant name"+plantNumber, plantName);
                preferencesEditor.putString("plant light"+plantNumber, plantLight);
                preferencesEditor.putString("plant water"+plantNumber, plantWater);
                preferencesEditor.putString("plant lastWatered"+plantNumber, dateToday);

                preferencesEditor.commit();


                dismiss();
            }
        });

        return v;
    }


    ActivityResultLauncher<Intent> launchSomeActivity
            = registerForActivityResult(
            new ActivityResultContracts
                    .StartActivityForResult(),
            result -> {
                if (result.getResultCode()
                        == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    // do your operation from here....
                    if (data != null
                            && data.getData() != null) {
                        Uri selectedImageUri = data.getData();
                        chestie = selectedImageUri;
                        Bitmap selectedImageBitmap = null;
                        try {
                            selectedImageBitmap
                                    = MediaStore.Images.Media.getBitmap(
                                    getContext().getContentResolver(),
                                    selectedImageUri);

                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        //Bitmap scaledAccountImage= Bitmap.createScaledBitmap(selectedImageBitmap, (int) 100, (int) 100, false);
                        Bitmap scaledAccountImage = ThumbnailUtils.extractThumbnail(selectedImageBitmap, 80, 80);
                        scaledAccountImage.compress(Bitmap.CompressFormat.PNG, 2, baos); //bm is the bitmap object
                        /**
                         * accountImage.setImageBitmap(scaledAccountImage);
                         */

                        plantImageBitmap = scaledAccountImage;

                        byte[] b = baos.toByteArray();
                        String encoded = Base64.encodeToString(b, Base64.DEFAULT);


                        SharedPreferences sharedPreferences = getContext().getSharedPreferences("PLANTS", MODE_PRIVATE);
                        // Creating an Editor object to edit(write to the file)
                        SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();

                        int position = sharedPreferences.getInt("PLANT NUMBER", 0);
                        position++;

                        preferencesEditor.putString("plant image"+position, encoded);
                        preferencesEditor.commit();

                    }
                }
            });


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{

        } catch (RuntimeException e)
        {
            throw  new RuntimeException(context.toString()+" Must implement method");
        }

    }
}