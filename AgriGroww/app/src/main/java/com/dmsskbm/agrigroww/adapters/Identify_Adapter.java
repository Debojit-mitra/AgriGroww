package com.dmsskbm.agrigroww.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.dmsskbm.agrigroww.ChangeLanguageActivity;
import com.dmsskbm.agrigroww.R;
import com.dmsskbm.agrigroww.models.ModelCacheIdentify;
import com.dmsskbm.agrigroww.models.ModelPlantDataset;
import com.dmsskbm.agrigroww.models.Model_Identify;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;

import ozaydin.serkan.com.image_zoom_view.ImageViewZoom;

public class Identify_Adapter extends RecyclerView.Adapter<Identify_Adapter.Holder> {

    ArrayList<Model_Identify> identifyArrayList;
    Context context;
    Thread thread;
    BottomSheetDialog bottomSheetDialog;
    ModelPlantDataset modelPlantDataset;
    String otherLanguagesAvailable;
    String sScore, totalScore;

    public Identify_Adapter(ArrayList<Model_Identify> identifyArrayList, Context context) {
        this.identifyArrayList = identifyArrayList;
        this.context = context;
    }

    public Identify_Adapter() {

    }

    @NonNull
    @Override
    public Identify_Adapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.identify_recyclerview_layout, parent, false);

        SharedPreferences OtherLanguagesAvailable = context.getSharedPreferences("OtherLanguages", Context.MODE_PRIVATE);
        otherLanguagesAvailable = OtherLanguagesAvailable.getString("otherLanguagesAvailable", "NO");

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Identify_Adapter.Holder holder, int position) {

        //holder.recyclerView_relative_btn.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.recyclerviewscroll));
        Model_Identify model_identify = identifyArrayList.get(position);

        holder.cardView_identify_layout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.item_animation_fall_down));

        String floatScore = model_identify.getScore();
        float newfloteScore = Float.parseFloat(floatScore);
        DecimalFormat decimalFormat = new DecimalFormat("##.###");
        decimalFormat.setMaximumFractionDigits(4);  //4
        float score = Float.parseFloat(decimalFormat.format(newfloteScore));
        totalScore = String.valueOf(score * 100);

        SharedPreferences options = context.getSharedPreferences("LanguagePreference", Context.MODE_PRIVATE);
        String savedLanguage = options.getString("SelectedLanguage", "English");
        if (savedLanguage.equals("English")) {
            sScore = totalScore + "% Matches";
            holder.plant_score.setText(sScore);

        } else if (savedLanguage.equals("অসমীয়া")) {

            if(totalScore.contains("0")){
                totalScore = totalScore.replace("0","০");
            }if(totalScore.contains("1")){
                totalScore = totalScore.replace("1","১");
            }if(totalScore.contains("2")){
                totalScore = totalScore.replace("2","২");
            }if(totalScore.contains("3")){
                totalScore = totalScore.replace("3","৩");
            }if(totalScore.contains("4")){
                totalScore = totalScore.replace("4","৪");
            }if(totalScore.contains("5")){
                totalScore = totalScore.replace("5","৫");
            }if(totalScore.contains("6")){
                totalScore = totalScore.replace("6","৬");
            }if(totalScore.contains("7")){
                totalScore = totalScore.replace("7","৭");
            }if(totalScore.contains("8")){
                totalScore = totalScore.replace("8","৮");
            }if(totalScore.contains("9")){
                totalScore = totalScore.replace("9","৯");
            }
            sScore = totalScore + "% মিল আছে";
            holder.plant_score.setText(sScore);

        } else if (savedLanguage.equals("हिंदी")) {

            if(totalScore.contains("0")){
                totalScore = totalScore.replace("0","०");
            }if(totalScore.contains("1")){
                totalScore = totalScore.replace("1","१");
            }if(totalScore.contains("2")){
                totalScore = totalScore.replace("2","२");
            }if(totalScore.contains("3")){
                totalScore = totalScore.replace("3","३");
            }if(totalScore.contains("4")){
                totalScore = totalScore.replace("4","४");
            }if(totalScore.contains("5")){
                totalScore = totalScore.replace("5","५");
            }if(totalScore.contains("6")){
                totalScore = totalScore.replace("6","६");
            }if(totalScore.contains("7")){
                totalScore = totalScore.replace("7","७");
            }if(totalScore.contains("8")){
                totalScore = totalScore.replace("8","८");
            }if(totalScore.contains("9")){
                totalScore = totalScore.replace("9","९");
            }

            sScore = totalScore + "% मेल खाता है";
            holder.plant_score.setText(sScore);
        }

        Glide.with(holder.plant_imageView.getContext()).load(model_identify.getO())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(holder.plant_imageView);
        holder.scientific_name.setText(model_identify.getScientificNameWithoutAuthor());
        holder.common_name.setText(model_identify.getCommonNames());
        holder.detected_organ.setText(model_identify.getOrgan());


        holder.recyclerView_relative_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline()) {
                    /*try catch to make app uncrashable when clicked multiple times or any other issue */
                    try {
                        holder.recyclerView_relative_btn.setEnabled(false);
                        SharedPreferences options = context.getSharedPreferences("optionsPreference", Context.MODE_PRIVATE);
                        String savedValue = options.getString("Server", "NotSelected");

                        holder.progressBarBs.setVisibility(View.VISIBLE);

                        bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetTheme);
                        View bsView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_dialog, v.findViewById(R.id.bottom_sheet_scroll));

                        //ImageView identify_image;
                        ImageViewZoom identify_image;
                        TextView plant_score, scientific_name, common_name, family_name, detected_organ, plant_description, common_diseases;
                        identify_image = bsView.findViewById(R.id.identify_image);
                        plant_score = bsView.findViewById(R.id.plant_score);
                        scientific_name = bsView.findViewById(R.id.scientific_name);
                        common_name = bsView.findViewById(R.id.common_name);
                        family_name = bsView.findViewById(R.id.family_name);
                        detected_organ = bsView.findViewById(R.id.detected_organ);
                        plant_description = bsView.findViewById(R.id.plant_description);
                        common_diseases = bsView.findViewById(R.id.common_diseases);

                        if (savedValue.equals("NotSelected")) {

                            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("plantDataset");
                            referenceProfile.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.hasChild(model_identify.getRealNameLanguageSupport())) {

                                        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("plantDataset").child(model_identify.getRealNameLanguageSupport());
                                        referenceProfile.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                modelPlantDataset = snapshot.getValue(ModelPlantDataset.class);
                                                Glide.with(identify_image.getContext()).load(model_identify.getO())
                                                        .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                                        .error(R.drawable.unable_toload_image)
                                                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).into(identify_image);
                                                plant_score.setText(holder.plant_score.getText());
                                                scientific_name.setText(model_identify.getScientificNameWithoutAuthor());
                                                common_name.setText(model_identify.getCommonNames());
                                                family_name.setText(model_identify.getFamilyName());
                                                detected_organ.setText(model_identify.getOrgan());

                                                if (savedLanguage.equals("English") && otherLanguagesAvailable.equals("YES") || otherLanguagesAvailable.equals("NO")) {

                                                    plant_description.setText(modelPlantDataset.getDescription());
                                                    common_diseases.setText(modelPlantDataset.getCommon_disease());
                                                    holder.progressBarBs.setVisibility(View.GONE);
                                                    holder.recyclerView_relative_btn.setEnabled(true);
                                                    bottomSheetDialog.setContentView(bsView);
                                                    bottomSheetDialog.show();

                                                } else if (savedLanguage.equals("অসমীয়া") && otherLanguagesAvailable.equals("YES")) {

                                                    thread = new Thread(new Runnable() {
                                                        @Override
                                                        public void run() {

                                                            try {
                                                                HttpResponse<String> responseAssamese = Unirest.post("https://translo.p.rapidapi.com/api/v3/translate")
                                                                        .header("content-type", "application/x-www-form-urlencoded")
                                                                        .header("X-RapidAPI-Key", "10ca243c80msh5ca8d0421f8400cp1d2c4djsna46678028df6")
                                                                        .header("X-RapidAPI-Host", "translo.p.rapidapi.com")
                                                                        .body("from=en&to=as&text=" + modelPlantDataset.getDescription() + " -/- " + modelPlantDataset.getCommon_disease() + " /. ")
                                                                        .asString();

                                                                // Log.e("FAQFRAGMENT:Assamese", responseAssamese.getBody());

                                                                JSONObject jsonObject = new JSONObject(responseAssamese.getBody());
                                                                String translatedText = jsonObject.getString("translated_text");

                                                                String descriptionTranslated = translatedText.substring(0, translatedText.indexOf("-/-")).trim();
                                                                String commonDiseaseTranslated = translatedText.substring(translatedText.indexOf("-/-") + 3, translatedText.indexOf("/.")).trim();

                                                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        plant_description.setText(descriptionTranslated);
                                                                        common_diseases.setText(commonDiseaseTranslated);
                                                                        holder.progressBarBs.setVisibility(View.GONE);
                                                                        holder.recyclerView_relative_btn.setEnabled(true);
                                                                        bottomSheetDialog.setContentView(bsView);
                                                                        bottomSheetDialog.show();
                                                                    }
                                                                });

                                                            } catch (Exception e) {
                                                                Log.e("Identify_Adapter", e.getMessage());
                                                            }

                                                        }
                                                    });
                                                    thread.start();

                                                } else if (savedLanguage.equals("हिंदी")  && otherLanguagesAvailable.equals("YES")) {

                                                    thread = new Thread(new Runnable() {
                                                        @Override
                                                        public void run() {

                                                            try {
                                                                HttpResponse<String> responseHindi = Unirest.post("https://translo.p.rapidapi.com/api/v3/translate")
                                                                        .header("content-type", "application/x-www-form-urlencoded")
                                                                        .header("X-RapidAPI-Key", "10ca243c80msh5ca8d0421f8400cp1d2c4djsna46678028df6")
                                                                        .header("X-RapidAPI-Host", "translo.p.rapidapi.com")
                                                                        .body("from=en&to=hi&text=" + modelPlantDataset.getDescription() + " ?@ " + modelPlantDataset.getCommon_disease() + " @? ")
                                                                        .asString();

                                                                // Log.e("FAQFRAGMENT:Assamese", responseAssamese.getBody());

                                                                JSONObject jsonObject = new JSONObject(responseHindi.getBody());
                                                                String translatedText = jsonObject.getString("translated_text");

                                                                String descriptionTranslated = translatedText.substring(0, translatedText.indexOf("?@")).trim();
                                                                String commonDiseaseTranslated = translatedText.substring(translatedText.indexOf("?@") + 2, translatedText.indexOf("@?")).trim();

                                                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        plant_description.setText(descriptionTranslated);
                                                                        common_diseases.setText(commonDiseaseTranslated);
                                                                        holder.progressBarBs.setVisibility(View.GONE);
                                                                        holder.recyclerView_relative_btn.setEnabled(true);
                                                                        bottomSheetDialog.setContentView(bsView);
                                                                        bottomSheetDialog.show();
                                                                    }
                                                                });

                                                            } catch (Exception e) {
                                                                Log.e("Identify_Adapter", e.getMessage());
                                                            }

                                                        }
                                                    });
                                                    thread.start();

                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                holder.progressBarBs.setVisibility(View.GONE);
                                                holder.recyclerView_relative_btn.setEnabled(true);
                                                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });


                                    } else {

                                        Glide.with(identify_image.getContext()).load(model_identify.getO())
                                                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                                .error(R.drawable.unable_toload_image)
                                                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).into(identify_image);
                                        plant_score.setText(holder.plant_score.getText());
                                        scientific_name.setText(model_identify.getScientificNameWithoutAuthor());
                                        common_name.setText(model_identify.getCommonNames());
                                        family_name.setText(model_identify.getFamilyName());
                                        detected_organ.setText(model_identify.getOrgan());

                                        thread = new Thread(new Runnable() {
                                            @Override
                                            public void run() {

                                                try {
                                                    HttpResponse<String> response = Unirest.post("https://chatgpt-gpt-3-5.p.rapidapi.com/ask")
                                                            .header("content-type", "application/json")
                                                            .header("X-RapidAPI-Key", "10ca243c80msh5ca8d0421f8400cp1d2c4djsna46678028df6")
                                                            .header("X-RapidAPI-Host", "chatgpt-gpt-3-5.p.rapidapi.com")
                                                            .body("{\r\"query\": \"describe about " + model_identify.getRealNameLanguageSupport() + "\"\r}")
                                                            .asString();

                                                    HttpResponse<String> response2 = Unirest.post("https://chatgpt-gpt-3-5.p.rapidapi.com/ask")
                                                            .header("content-type", "application/json")
                                                            .header("X-RapidAPI-Key", "10ca243c80msh5ca8d0421f8400cp1d2c4djsna46678028df6")
                                                            .header("X-RapidAPI-Host", "chatgpt-gpt-3-5.p.rapidapi.com")
                                                            .body("{\r\"query\": \"describe briefly all the common diseases of " + model_identify.getRealNameLanguageSupport() + "\"\r}")
                                                            .asString();



                                                    JSONObject jsonObjectPlant_desc = new JSONObject(response.getBody());
                                                    String plant_desc = jsonObjectPlant_desc.getString("response");
                                                    JSONObject jsonObjectDisease_desc = new JSONObject(response2.getBody());
                                                    String disease_desc = jsonObjectDisease_desc.getString("response");



                                                    if (response.getStatus() == 200 && response2.getStatus() == 200) {

                                                        if (savedLanguage.equals("English") && otherLanguagesAvailable.equals("YES") || otherLanguagesAvailable.equals("NO")) {

                                                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                                @Override
                                                                public void run() {

                                                                    plant_description.setText(plant_desc);
                                                                    common_diseases.setText(disease_desc);
                                                                    holder.progressBarBs.setVisibility(View.GONE);
                                                                    holder.recyclerView_relative_btn.setEnabled(true);
                                                                    bottomSheetDialog.setContentView(bsView);
                                                                    bottomSheetDialog.show();

                                                                    //uploading to dataset if plant doesnt exists
                                                                    String description, common_disease;
                                                                    description = response.getBody();
                                                                    common_disease = response2.getBody();
                                                                    modelPlantDataset = new ModelPlantDataset(common_disease, description);
                                                                    DatabaseReference referenceProfile1 = FirebaseDatabase.getInstance().getReference("plantDataset");
                                                                    referenceProfile1.child(model_identify.getRealNameLanguageSupport()).setValue(modelPlantDataset).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {

                                                                            } else {

                                                                            }
                                                                        }
                                                                    });

                                                                }
                                                            });

                                                        } else if (savedLanguage.equals("অসমীয়া")  && otherLanguagesAvailable.equals("YES")) {

                                                            HttpResponse<String> responseAssamese = Unirest.post("https://translo.p.rapidapi.com/api/v3/translate")
                                                                    .header("content-type", "application/x-www-form-urlencoded")
                                                                    .header("X-RapidAPI-Key", "10ca243c80msh5ca8d0421f8400cp1d2c4djsna46678028df6")
                                                                    .header("X-RapidAPI-Host", "translo.p.rapidapi.com")
                                                                    .body("from=en&to=as&text=" + plant_desc + " -/- " + disease_desc + " /. ")
                                                                    .asString();

                                                            // Log.e("FAQFRAGMENT:Assamese", responseAssamese.getBody());

                                                            JSONObject jsonObject = new JSONObject(responseAssamese.getBody());
                                                            String translatedText = jsonObject.getString("translated_text");

                                                            String descriptionTranslated = translatedText.substring(0, translatedText.indexOf("-/-")).trim();
                                                            String commonDiseaseTranslated = translatedText.substring(translatedText.indexOf("-/-") + 3, translatedText.indexOf("/.")).trim();


                                                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                                @Override
                                                                public void run() {

                                                                    plant_description.setText(descriptionTranslated);
                                                                    common_diseases.setText(commonDiseaseTranslated);
                                                                    holder.progressBarBs.setVisibility(View.GONE);
                                                                    holder.recyclerView_relative_btn.setEnabled(true);
                                                                    bottomSheetDialog.setContentView(bsView);
                                                                    bottomSheetDialog.show();

                                                                    //uploading to dataset if plant doesnt exists
                                                                    String description, common_disease;
                                                                    description = response.getBody();
                                                                    common_disease = response2.getBody();
                                                                    modelPlantDataset = new ModelPlantDataset(common_disease, description);
                                                                    DatabaseReference referenceProfile1 = FirebaseDatabase.getInstance().getReference("plantDataset");
                                                                    referenceProfile1.child(model_identify.getRealNameLanguageSupport()).setValue(modelPlantDataset).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {

                                                                            } else {

                                                                            }
                                                                        }
                                                                    });

                                                                }
                                                            });

                                                        } else if (savedLanguage.equals("हिंदी")  && otherLanguagesAvailable.equals("YES")) {

                                                    }

                                                    } else {
                                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                holder.progressBarBs.setVisibility(View.GONE);
                                                                Toast.makeText(context, "Error fetching description and common disease!", Toast.LENGTH_LONG).show();
                                                                bottomSheetDialog.setContentView(bsView);
                                                                bottomSheetDialog.show();
                                                            }
                                                        });
                                                    }

                                                } catch (Exception e) {
                                                    Log.e("UnirestException1", e.getMessage());
                                                }

                                            }
                                        });
                                        thread.start();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else if (savedValue.equals("Selected")) {


                            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("plantDataset");
                            referenceProfile.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.hasChild(model_identify.getRealNameLanguageSupport())) {

                                        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("plantDataset").child(model_identify.getRealNameLanguageSupport());
                                        referenceProfile.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                modelPlantDataset = snapshot.getValue(ModelPlantDataset.class);
                                                Glide.with(identify_image.getContext()).load(model_identify.getO())
                                                        .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                                        .error(R.drawable.unable_toload_image)
                                                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).into(identify_image);
                                                plant_score.setText(sScore);
                                                scientific_name.setText(model_identify.getScientificNameWithoutAuthor());
                                                common_name.setText(model_identify.getCommonNames());
                                                family_name.setText(model_identify.getFamilyName());
                                                detected_organ.setText(model_identify.getOrgan());
                                                plant_description.setText(modelPlantDataset.getDescription());
                                                common_diseases.setText(modelPlantDataset.getCommon_disease());
                                                holder.progressBarBs.setVisibility(View.GONE);
                                                holder.recyclerView_relative_btn.setEnabled(true);
                                                bottomSheetDialog.setContentView(bsView);
                                                bottomSheetDialog.show();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });


                                    } else {

                                        Glide.with(identify_image.getContext()).load(model_identify.getO())
                                                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                                .error(R.drawable.unable_toload_image)
                                                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).into(identify_image);
                                        plant_score.setText(sScore);
                                        scientific_name.setText(model_identify.getScientificNameWithoutAuthor());
                                        common_name.setText(model_identify.getCommonNames());
                                        family_name.setText(model_identify.getFamilyName());
                                        detected_organ.setText(model_identify.getOrgan());

                                 /*   modelCacheIdentify = new ModelCacheIdentify(sScore, model_identify.getScientificNameWithoutAuthor(), model_identify.getCommonNames(), model_identify.getOrgan(), model_identify.getO(), "empty", "empty");
                                    modelCacheIdentifyArrayList.add(modelCacheIdentify);*/

                                        thread = new Thread(new Runnable() {
                                            @Override
                                            public void run() {

                                                try {
                                                    HttpResponse<String> response = Unirest.get("https://api.lolhuman.xyz/api/openai?apikey=51e4f9a0f9c4471e41391a13&text={maintext}&user=agrigroww")
                                                            .routeParam("maintext", "describe about " + model_identify.getRealNameLanguageSupport())
                                                            .asString();

                                                    JSONObject jsonObject = new JSONObject(response.getBody());
                                                    String status = jsonObject.getString("status");

                                                    HttpResponse<String> response2 = Unirest.get("https://api.lolhuman.xyz/api/openai?apikey=51e4f9a0f9c4471e41391a13&text={maintext}&user=agrigroww")
                                                            .routeParam("maintext", "describe briefly all the common diseases of " + model_identify.getRealNameLanguageSupport())
                                                            .asString();

                                                    JSONObject jsonObject1 = new JSONObject(response2.getBody());
                                                    String status1 = jsonObject1.getString("status");

                                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                        @Override
                                                        public void run() {

                                                            if (status.equals("200") && status1.equals("200")) {
                                                                try {
                                                                    String resultDesc = jsonObject.getString("result");
                                                                    String resultDisease = jsonObject1.getString("result");

                                                                    plant_description.setText(resultDesc);
                                                                    common_diseases.setText(resultDisease);
                                                                    holder.progressBarBs.setVisibility(View.GONE);
                                                                    holder.recyclerView_relative_btn.setEnabled(true);
                                                                    bottomSheetDialog.setContentView(bsView);
                                                                    bottomSheetDialog.show();

                                                                    modelPlantDataset = new ModelPlantDataset(resultDisease, resultDesc);
                                                                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("plantDataset");
                                                                    referenceProfile.child(model_identify.getScientificNameWithoutAuthor()).setValue(modelPlantDataset).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {

                                                                            } else {

                                                                            }
                                                                        }
                                                                    });
                                                                } catch (Exception e) {
                                                                    holder.progressBarBs.setVisibility(View.GONE);
                                                                    holder.recyclerView_relative_btn.setEnabled(true);
                                                                    Toast.makeText(context, "API ERROR! STATUS NOT 200!", Toast.LENGTH_LONG).show();
                                                                    Log.e("UnirestException2_IN", e.getMessage());
                                                                }
                                                            } else {
                                                                holder.progressBarBs.setVisibility(View.GONE);
                                                                holder.recyclerView_relative_btn.setEnabled(true);
                                                                Toast.makeText(context, "API ERROR! STATUS NOT 200!", Toast.LENGTH_LONG).show();
                                                                Log.e("ApiError", "API ERROR!");
                                                            }

                                                        }
                                                    });

                                                } catch (Exception e) {
                                                    holder.progressBarBs.setVisibility(View.GONE);
                                                    holder.recyclerView_relative_btn.setEnabled(true);
                                                    Toast.makeText(context, "API ERROR! STATUS NOT 200!", Toast.LENGTH_LONG).show();
                                                    Log.e("UnirestException2", e.getMessage());
                                                }

                                            }
                                        });
                                        thread.start();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    holder.progressBarBs.setVisibility(View.GONE);
                                    holder.recyclerView_relative_btn.setEnabled(true);
                                    Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } catch (Exception e) {
                        holder.progressBarBs.setVisibility(View.GONE);
                        holder.recyclerView_relative_btn.setEnabled(true);
                        Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(context, "Check Your Internet Connection!", Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return identifyArrayList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        ImageView plant_imageView;
        TextView plant_score, scientific_name, common_name, detected_organ;
        RelativeLayout recyclerView_relative_btn;
        CardView cardView_identify_layout;
        ProgressBar progressBar, progressBarBs;

        public Holder(@NonNull View itemView) {
            super(itemView);

            plant_imageView = itemView.findViewById(R.id.plant_imageView);
            plant_score = itemView.findViewById(R.id.plant_score);
            scientific_name = itemView.findViewById(R.id.scientific_name);
            common_name = itemView.findViewById(R.id.common_name);
            detected_organ = itemView.findViewById(R.id.detected_organ);
            recyclerView_relative_btn = itemView.findViewById(R.id.recyclerView_relative_btn);
            progressBar = itemView.findViewById(R.id.progressBar);
            progressBarBs = itemView.findViewById(R.id.progressBarBs);
            cardView_identify_layout = itemView.findViewById(R.id.cardView_identify_layout);

        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
