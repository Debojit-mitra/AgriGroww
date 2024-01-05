package com.dmsskbm.agrigroww.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dmsskbm.agrigroww.ErrorActivity;
import com.dmsskbm.agrigroww.MainActivity;
import com.dmsskbm.agrigroww.R;
import com.dmsskbm.agrigroww.adapters.Identify_Adapter;
import com.dmsskbm.agrigroww.models.Model_Identify;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.UUID;

import ozaydin.serkan.com.image_zoom_view.ImageViewZoom;

public class FAQFragment extends Fragment {

    String api; //= "https://my-api.plantnet.org/v2/identify/all?images=https://i.natgeofe.com/k/9fc2bb76-3097-44b3-b672-52835d3e0115/a-8-carnivorous-plants-venus-flytrap.jpg&include-related-images=true&lang=en&api-key=2a10dhqKV1csqtYS4gUnTxZ";

    ArrayList<Model_Identify> model_identifyArrayList = new ArrayList<>();
    RecyclerView recyclerView;
    ShimmerFrameLayout shimmer_view;
    String commonName;
    ImageViewZoom identify_image;
    RelativeLayout relative_identify;
    Uri uriImage, oldUri;
    private static final int PICK_IMAGE_REQUEST = 1;
    String TagImage = "oldImage";
    FirebaseAuth authProfile;
    FirebaseUser firebaseUser;
    FirebaseStorage firebaseStorage;
    ProgressBar progressBar;
    LottieAnimationView custom_animationView, faq_animationView1, faq_animationView2;
    LinearLayout linear1, linear2;
    CardView profile_image_card;
    String imageUrihalf;
    Thread thread;
    String savedLanguage, otherLanguagesAvailable;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_f_a_q, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        shimmer_view = view.findViewById(R.id.shimmer_view);
        identify_image = view.findViewById(R.id.identify_image);
        relative_identify = view.findViewById(R.id.relative_identify);
        progressBar = view.findViewById(R.id.progressBar);
        linear1 = view.findViewById(R.id.linear1);
        linear2 = view.findViewById(R.id.linear2);
        profile_image_card = view.findViewById(R.id.profile_image_card);
        faq_animationView1 = view.findViewById(R.id.faq_animationView1);
        faq_animationView2 = view.findViewById(R.id.faq_animationView2);

        SharedPreferences OtherLanguagesAvailable = requireContext().getSharedPreferences("OtherLanguages", Context.MODE_PRIVATE);
        otherLanguagesAvailable = OtherLanguagesAvailable.getString("otherLanguagesAvailable", "NO");


        authProfile = FirebaseAuth.getInstance();
        //get instance of the current user
        firebaseUser = authProfile.getCurrentUser();

        linear1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChoser();
            }
        });
        linear2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline()) {
                    uploadImage();

                } else {
                    Intent intent = new Intent(requireActivity(), ErrorActivity.class);
                    intent.putExtra("ExtraText", "No Internet Connection!");
                    startActivity(intent);
                    requireActivity().finish();
                }
            }
        });
        //to make ui more smooth
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                faq_animationView1.playAnimation();
                faq_animationView2.playAnimation();
            }
        }, 800);

        //translate part
        SharedPreferences options = requireActivity().getSharedPreferences("LanguagePreference", Context.MODE_PRIVATE);
        savedLanguage = options.getString("SelectedLanguage", "English");


        return view;
    }

    private void uploadImage() {

        if (TagImage.equals("newImage") && (uriImage != null) && oldUri != uriImage) {
            try {

                AlertDialog.Builder loading = new AlertDialog.Builder(requireContext(), R.style.RoundedCornersDialog);
                View view = LayoutInflater.from(requireContext()).inflate(R.layout.custom_alert_dialog, null);
                custom_animationView = view.findViewById(R.id.custom_animationView);
                switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
                    case Configuration.UI_MODE_NIGHT_YES:
                        custom_animationView.setAnimation("Loading_white.json");
                        break;
                    case Configuration.UI_MODE_NIGHT_NO:
                        custom_animationView.setAnimation("Loading_black.json");
                        break;
                }
                custom_animationView.setVisibility(View.VISIBLE);
                custom_animationView.setRepeatCount(LottieDrawable.INFINITE);
                int width = 300;
                int height = 300;
                RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(width, height);
                parms.addRule(RelativeLayout.CENTER_HORIZONTAL);
                custom_animationView.setLayoutParams(parms);
                loading.setView(view);
                AlertDialog alertDialog = loading.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setCancelable(false);
                alertDialog.show();
                alertDialog.getWindow().setLayout(400, 400);


                recyclerView.setVisibility(View.GONE);
                shimmer_view.startShimmer();
                shimmer_view.setVisibility(View.VISIBLE);
                //  progressBar.setVisibility(View.VISIBLE);
                linear1.setEnabled(false);
                linear2.setEnabled(false);
                relative_identify.setEnabled(false);
                model_identifyArrayList.clear();
                TagImage = "oldImage";

                firebaseStorage = FirebaseStorage.getInstance();
                StorageReference storageReference = firebaseStorage.getReference("IdentifiedPics");

                StorageReference fileReference = storageReference.child(authProfile.getCurrentUser().getUid()).child(UUID.randomUUID() + ".jpeg");
                fileReference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                //customLottieDialog.setLoadingText("Matching your image in our database!");

                                imageUrihalf = String.valueOf(uri);
                                Log.e("got url", imageUrihalf);
                                String finalImageUri = imageUrihalf.substring(0, imageUrihalf.indexOf('&'));
                                Log.e("finalImageUri", finalImageUri);

                                try {
                                    String encodedUrl = URLEncoder.encode(finalImageUri, "utf-8");
                                    api = "https://my-api.plantnet.org/v2/identify/all?images=" + encodedUrl + "&organs=auto&include-related-images=true&no-reject=false&lang=en&api-key=2b10nkeCUeNeUzImQGhWnqpg7";
                                    Log.e("encodedurl", encodedUrl);
                                } catch (UnsupportedEncodingException e) {
                                    Log.e("UnsupportedEncodingException", e.getMessage());
                                }


                                RequestQueue queue = Volley.newRequestQueue(requireContext());

                                StringRequest stringRequest = new StringRequest(Request.Method.GET, api, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        //Log.e("volley",response);

                                        try {
                                            Log.e("api", api);
                                            JSONObject jsonObject = new JSONObject(response);
                                            JSONArray jsonArrayResults = jsonObject.getJSONArray("results");
                                            for (int i = 0; i < 12; i++) { //jsonArrayResults.length()
                                                JSONObject jsonObjectInfinite = jsonArrayResults.getJSONObject(i);
                                                String score = jsonObjectInfinite.getString("score");

                                                JSONObject jsonObjectScientificName = jsonObjectInfinite.getJSONObject("species");
                                                String ScientificNameResult = jsonObjectScientificName.getString("scientificNameWithoutAuthor").trim();
                                                String ScientificName = ScientificNameResult.replaceAll("\\.", "").trim();


                                                JSONObject jsonObjectFamilyName = jsonObjectScientificName.getJSONObject("family");
                                                String familyName = jsonObjectFamilyName.getString("scientificNameWithoutAuthor");

                                                try {
                                                    JSONArray jsonArrayCommonNames = jsonObjectScientificName.getJSONArray("commonNames");
                                                    commonName = jsonArrayCommonNames.getString(0).trim();
                                                } catch (Exception e) {
                                                    commonName = "n/a";
                                                }

                                                JSONArray jsonArrayImages = jsonObjectInfinite.getJSONArray("images");
                                                JSONObject jsonObjectImages = jsonArrayImages.getJSONObject(0);
                                                String organ = jsonObjectImages.getString("organ").trim();
                                                JSONObject jsonObjectImagesUrl = jsonObjectImages.getJSONObject("url");
                                                String imageUrl = jsonObjectImagesUrl.getString("o");

                                                if (savedLanguage.equals("English") && otherLanguagesAvailable.equals("YES") || otherLanguagesAvailable.equals("NO")) {
                                                    Model_Identify model_identify = new Model_Identify(score, ScientificName, commonName, organ, imageUrl, familyName, ScientificName);
                                                    model_identifyArrayList.add(model_identify);
                                                    Identify_Adapter identify_adapter = new Identify_Adapter(model_identifyArrayList, getActivity());
                                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                                                    recyclerView.setLayoutManager(linearLayoutManager);

                                                    SharedPreferences scrollChoice = requireActivity().getSharedPreferences("optionsPreference", Context.MODE_PRIVATE);
                                                    String savedValueAnimation = scrollChoice.getString("ScrollAnimation", "NotSelected");
                                                    if (savedValueAnimation.equals("NotSelected")) {
                                                        RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                                        recyclerView.setLayoutParams(parms);
                                                        recyclerView.setNestedScrollingEnabled(false);
                                                        recyclerView.setAdapter(identify_adapter);
                                                    } else if (savedValueAnimation.equals("Selected")) {
                                                        recyclerView.setAdapter(identify_adapter);
                                                    }

                                                    shimmer_view.stopShimmer();
                                                    alertDialog.dismiss();
                                                    shimmer_view.setVisibility(View.GONE);
                                                    recyclerView.setVisibility(View.VISIBLE);

                                                } else if (savedLanguage.equals("অসমীয়া") && otherLanguagesAvailable.equals("YES")) {

                                                    thread = new Thread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            try {
                                                                String all = ScientificName + " ./ " + commonName + " /. " + organ + " -/ " + familyName + " /- ";
                                                                HttpResponse<String> responseAssamese = Unirest.post("https://translo.p.rapidapi.com/api/v3/translate")
                                                                        .header("content-type", "application/x-www-form-urlencoded")
                                                                        .header("X-RapidAPI-Key", "10ca243c80msh5ca8d0421f8400cp1d2c4djsna46678028df6")
                                                                        .header("X-RapidAPI-Host", "translo.p.rapidapi.com")
                                                                        .body("from=en&to=as&text=" + all)
                                                                        .asString();


                                                                Log.e("FAQFRAGMENT:AssameseHere", responseAssamese.getBody());

                                                                JSONObject jsonObject = new JSONObject(responseAssamese.getBody());
                                                                String translatedText = jsonObject.getString("translated_text");

                                                                // String asScore = translatedText.substring(0, translatedText.indexOf("/)")+1).trim();
                                                                String asScientificName = translatedText.substring(0, translatedText.indexOf("./")).trim();
                                                                String asCommonName = translatedText.substring(translatedText.indexOf("./") + 2, translatedText.indexOf("/.")).trim();
                                                                String asOrganName = translatedText.substring(translatedText.indexOf("/.") + 2, translatedText.indexOf("-/")).trim();
                                                                String asFamilyName = translatedText.substring(translatedText.indexOf("-/") + 2, translatedText.indexOf("/-")).trim();

                                                                // Log.e("FAQFRAGMENT:Score", asFamilyName);

                                                                if (responseAssamese.getStatus() == 200) {
                                                                    Model_Identify model_identify = new Model_Identify(score, asScientificName, asCommonName, asOrganName, imageUrl, asFamilyName, ScientificName);
                                                                    model_identifyArrayList.add(model_identify);
                                                                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            model_identifyArrayList.sort(new Comparator<Model_Identify>() {
                                                                                @Override
                                                                                public int compare(Model_Identify model_identify, Model_Identify t1) {
                                                                                    return model_identify.getScore().compareToIgnoreCase(t1.getScore());
                                                                                }
                                                                            });

                                                                            Collections.reverse(model_identifyArrayList);

                                                                            Identify_Adapter identify_adapter = new Identify_Adapter(model_identifyArrayList, getActivity());
                                                                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                                                                            recyclerView.setLayoutManager(linearLayoutManager);

                                                                            SharedPreferences scrollChoice = requireActivity().getSharedPreferences("optionsPreference", Context.MODE_PRIVATE);
                                                                            String savedValueAnimation = scrollChoice.getString("ScrollAnimation", "NotSelected");
                                                                            if (savedValueAnimation.equals("NotSelected")) {
                                                                                RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                                                                recyclerView.setLayoutParams(parms);
                                                                                recyclerView.setNestedScrollingEnabled(false);
                                                                                recyclerView.setAdapter(identify_adapter);
                                                                                shimmer_view.stopShimmer();
                                                                                alertDialog.dismiss();
                                                                                shimmer_view.setVisibility(View.GONE);
                                                                                recyclerView.setVisibility(View.VISIBLE);
                                                                            } else if (savedValueAnimation.equals("Selected")) {
                                                                                recyclerView.setAdapter(identify_adapter);
                                                                                shimmer_view.stopShimmer();
                                                                                alertDialog.dismiss();
                                                                                shimmer_view.setVisibility(View.GONE);
                                                                                recyclerView.setVisibility(View.VISIBLE);
                                                                            }
                                                                        }
                                                                    }, 5000);
                                                                } else {
                                                                    Model_Identify model_identify = new Model_Identify(score, ScientificName, commonName, organ, imageUrl, familyName, ScientificName);
                                                                    model_identifyArrayList.add(model_identify);
                                                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            Identify_Adapter identify_adapter = new Identify_Adapter(model_identifyArrayList, getActivity());
                                                                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                                                                            recyclerView.setLayoutManager(linearLayoutManager);

                                                                            SharedPreferences scrollChoice = requireActivity().getSharedPreferences("optionsPreference", Context.MODE_PRIVATE);
                                                                            String savedValueAnimation = scrollChoice.getString("ScrollAnimation", "NotSelected");
                                                                            if (savedValueAnimation.equals("NotSelected")) {
                                                                                RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                                                                recyclerView.setLayoutParams(parms);
                                                                                recyclerView.setNestedScrollingEnabled(false);
                                                                                recyclerView.setAdapter(identify_adapter);
                                                                            } else if (savedValueAnimation.equals("Selected")) {
                                                                                recyclerView.setAdapter(identify_adapter);
                                                                            }

                                                                            shimmer_view.stopShimmer();
                                                                            alertDialog.dismiss();
                                                                            shimmer_view.setVisibility(View.GONE);
                                                                            recyclerView.setVisibility(View.VISIBLE);
                                                                        }
                                                                    });
                                                                }

                                                            } catch (Exception e) {
                                                                Log.e("FAQFRAGMENT", e.getMessage());
                                                            }
                                                        }
                                                    });
                                                    thread.start();


                                                } else if (savedLanguage.equals("हिंदी") && otherLanguagesAvailable.equals("YES")) {

                                                    thread = new Thread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            try {
                                                                String all = ScientificName + " ?= " + commonName + " =? " + organ + " ?@ " + familyName + " @? ";
                                                                HttpResponse<String> responseHindi = Unirest.post("https://translo.p.rapidapi.com/api/v3/translate")
                                                                        .header("content-type", "application/x-www-form-urlencoded")
                                                                        .header("X-RapidAPI-Key", "10ca243c80msh5ca8d0421f8400cp1d2c4djsna46678028df6")
                                                                        .header("X-RapidAPI-Host", "translo.p.rapidapi.com")
                                                                        .body("from=en&to=hi&text=" + all)
                                                                        .asString();


                                                                Log.e("FAQFRAGMENT:HindiHere", responseHindi.getBody());

                                                                JSONObject jsonObject = new JSONObject(responseHindi.getBody());
                                                                String translatedText = jsonObject.getString("translated_text");

                                                                // String asScore = translatedText.substring(0, translatedText.indexOf("/)")+1).trim();
                                                                String hiScientificName = translatedText.substring(0, translatedText.indexOf("?=")).trim();
                                                                String hiCommonName = translatedText.substring(translatedText.indexOf("?=") + 2, translatedText.indexOf("=?")).trim();
                                                                String hiOrganName = translatedText.substring(translatedText.indexOf("=?") + 2, translatedText.indexOf("?@")).trim();
                                                                String hiFamilyName = translatedText.substring(translatedText.indexOf("?@") + 2, translatedText.indexOf("@?")).trim();

                                                                // Log.e("FAQFRAGMENT:Score", asFamilyName);

                                                                if (responseHindi.getStatus() == 200) {
                                                                    Model_Identify model_identify = new Model_Identify(score, hiScientificName, hiCommonName, hiOrganName, imageUrl, hiFamilyName, ScientificName);
                                                                    model_identifyArrayList.add(model_identify);
                                                                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                                                        @Override
                                                                        public void run() {

                                                                            model_identifyArrayList.sort(new Comparator<Model_Identify>() {
                                                                                @Override
                                                                                public int compare(Model_Identify model_identify, Model_Identify t1) {
                                                                                    return model_identify.getScore().compareToIgnoreCase(t1.getScore());
                                                                                }
                                                                            });

                                                                            Collections.reverse(model_identifyArrayList);

                                                                            Identify_Adapter identify_adapter = new Identify_Adapter(model_identifyArrayList, getActivity());
                                                                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                                                                            recyclerView.setLayoutManager(linearLayoutManager);

                                                                            SharedPreferences scrollChoice = requireActivity().getSharedPreferences("optionsPreference", Context.MODE_PRIVATE);
                                                                            String savedValueAnimation = scrollChoice.getString("ScrollAnimation", "NotSelected");
                                                                            if (savedValueAnimation.equals("NotSelected")) {
                                                                                RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                                                                recyclerView.setLayoutParams(parms);
                                                                                recyclerView.setNestedScrollingEnabled(false);
                                                                                recyclerView.setAdapter(identify_adapter);
                                                                                shimmer_view.stopShimmer();
                                                                                alertDialog.dismiss();
                                                                                shimmer_view.setVisibility(View.GONE);
                                                                                recyclerView.setVisibility(View.VISIBLE);
                                                                            } else if (savedValueAnimation.equals("Selected")) {
                                                                                recyclerView.setAdapter(identify_adapter);
                                                                                shimmer_view.stopShimmer();
                                                                                alertDialog.dismiss();
                                                                                shimmer_view.setVisibility(View.GONE);
                                                                                recyclerView.setVisibility(View.VISIBLE);
                                                                            }
                                                                        }
                                                                    }, 5000);
                                                                } else {
                                                                    Model_Identify model_identify = new Model_Identify(score, ScientificName, commonName, organ, imageUrl, familyName, ScientificName);
                                                                    model_identifyArrayList.add(model_identify);
                                                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            Identify_Adapter identify_adapter = new Identify_Adapter(model_identifyArrayList, getActivity());
                                                                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                                                                            recyclerView.setLayoutManager(linearLayoutManager);

                                                                            SharedPreferences scrollChoice = requireActivity().getSharedPreferences("optionsPreference", Context.MODE_PRIVATE);
                                                                            String savedValueAnimation = scrollChoice.getString("ScrollAnimation", "NotSelected");
                                                                            if (savedValueAnimation.equals("NotSelected")) {
                                                                                RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                                                                recyclerView.setLayoutParams(parms);
                                                                                recyclerView.setNestedScrollingEnabled(false);
                                                                                recyclerView.setAdapter(identify_adapter);
                                                                            } else if (savedValueAnimation.equals("Selected")) {
                                                                                recyclerView.setAdapter(identify_adapter);
                                                                            }

                                                                            shimmer_view.stopShimmer();
                                                                            alertDialog.dismiss();
                                                                            shimmer_view.setVisibility(View.GONE);
                                                                            recyclerView.setVisibility(View.VISIBLE);
                                                                        }
                                                                    });
                                                                }

                                                            } catch (Exception e) {
                                                                Log.e("FAQFRAGMENT", e.getMessage());
                                                            }
                                                        }
                                                    });
                                                    thread.start();

                                                }

                                            }


                                            //  progressBar.setVisibility(View.GONE);
                                            linear1.setEnabled(true);
                                            linear2.setEnabled(true);
                                            relative_identify.setEnabled(true);
                                            oldUri = uriImage;
                                            deleteRecentIdentifyPic();


                                        } catch (JSONException e) {
                                            shimmer_view.stopShimmer();
                                            alertDialog.dismiss();
                                            shimmer_view.setVisibility(View.GONE);
                                            //  progressBar.setVisibility(View.GONE);
                                            linear1.setEnabled(true);
                                            linear2.setEnabled(true);
                                            relative_identify.setEnabled(true);
                                            Log.e("volleyerror", e.getMessage());
                                            if (e.getMessage().equals("Index 0 out of range [0..0)")) {
                                                Toast.makeText(requireActivity(), "No similar image found! Upload a proper image!", Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(requireActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                            deleteRecentIdentifyPic();
                                        }


                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        shimmer_view.stopShimmer();
                                        alertDialog.dismiss();
                                        shimmer_view.setVisibility(View.GONE);
                                        //   progressBar.setVisibility(View.GONE);
                                        relative_identify.setEnabled(true);
                                        if (error.toString().equals("com.android.volley.TimeoutError")) {
                                            profile_image_card.setVisibility(View.GONE);
                                            linear1.setEnabled(true);
                                            linear2.setEnabled(true);
                                            Toast.makeText(requireActivity(), "Timeout Error! Please try again!", Toast.LENGTH_LONG).show();
                                            deleteRecentIdentifyPic();
                                        } else if (error.toString().contains("com.android.volley.ClientError")) {
                                            profile_image_card.setVisibility(View.GONE);
                                            linear1.setEnabled(true);
                                            linear2.setEnabled(true);
                                            Toast.makeText(requireActivity(), "Server Error! Please try after Sometime!", Toast.LENGTH_LONG).show();
                                            deleteRecentIdentifyPic();
                                        } else {
                                            profile_image_card.setVisibility(View.GONE);
                                            linear1.setEnabled(true);
                                            linear2.setEnabled(true);
                                            Toast.makeText(requireActivity(), "Do not upload unnecessary images!", Toast.LENGTH_LONG).show();
                                            deleteRecentIdentifyPic();
                                        }
                                        Log.e("volleyerrorResponse", error.toString());
                                    }
                                });
                                stringRequest.setRetryPolicy(new DefaultRetryPolicy(15 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                queue.add(stringRequest);

                            }
                        });


                    }
                });

            } catch (Exception e) {
                Toast.makeText(requireActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(requireActivity(), "Select Image First!", Toast.LENGTH_SHORT).show();
        }

    }

    private void deleteRecentIdentifyPic() {
        try {
            firebaseStorage = FirebaseStorage.getInstance();
            StorageReference storageReference = firebaseStorage.getReferenceFromUrl(imageUrihalf);
            storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.d("oldIdentifiedImages", "onSuccess: file deleted!");
                }
            });
        } catch (Exception e) {
            Log.e("FirebaseExceptionOldIdentifiedImages", e.getMessage());
        }
    }

    private void openImageChoser() {
        ImagePicker.with(this)
                .crop()
                .maxResultSize(1080, 1080)
                .start(PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && data != null && data.getData() != null) {
            uriImage = data.getData();
            TagImage = "newImage";
            profile_image_card.setVisibility(View.VISIBLE);
            identify_image.setImageURI(uriImage);
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

}