package com.dmsskbm.agrigroww.admin.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.dmsskbm.agrigroww.admin.R;
import com.dmsskbm.agrigroww.admin.ReadWriteUserDetails;
import com.dmsskbm.agrigroww.admin.SentEmailActivity;
import com.dmsskbm.agrigroww.admin.WebViewActivity;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import co.nedim.maildroidx.MaildroidX;
import co.nedim.maildroidx.MaildroidXType;
import ozaydin.serkan.com.image_zoom_view.ImageViewZoom;

public class ShowUsersAdapter extends RecyclerView.Adapter<ShowUsersAdapter.Holder> {

    ArrayList<ReadWriteUserDetails> showUsersAdapterArrayList;
    Context context;
    TextView button_textview;
    BottomSheetDialog bottomSheetDialog;
    FirebaseAuth authProfile;

    public ShowUsersAdapter(ArrayList<ReadWriteUserDetails> showUsersAdapterArrayList, Context context) {
        this.context = context;
        this.showUsersAdapterArrayList = showUsersAdapterArrayList;
    }


    @NonNull
    @Override
    public ShowUsersAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.users_expert_layout, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowUsersAdapter.Holder holder, int position) {

        authProfile = FirebaseAuth.getInstance();

        ReadWriteUserDetails readWriteUserDetails = showUsersAdapterArrayList.get(position);
        holder.show_name.setText(readWriteUserDetails.getFullName());
        String reqNo = String.valueOf(readWriteUserDetails.getTotalRequests());
        holder.show_totalRequests.setText(reqNo);
        Glide.with(holder.person_imageView.getContext()).load(readWriteUserDetails.getProfileImage()).addListener(new RequestListener<Drawable>() {
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
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .error(R.drawable.ic_user)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(holder.person_imageView);

        //getting user uid
        //uID = readWriteUserDetails.getUserId();

        holder.recyclerView_relative_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetTheme);
                View bsView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_dialog, view.findViewById(R.id.bottom_sheet_scroll));

                KenBurnsView animatedImageBackground = bsView.findViewById(R.id.animatedImageBackground);
                ImageViewZoom identify_image = bsView.findViewById(R.id.identify_image);
                TextView users_name, users_dateJoined, users_userId, users_email, users_dob, users_gender, users_bannedStatus;
                users_name = bsView.findViewById(R.id.users_name);
                users_dateJoined = bsView.findViewById(R.id.users_dateJoined);
                users_userId = bsView.findViewById(R.id.users_userId);
                users_email = bsView.findViewById(R.id.users_email);
                users_dob = bsView.findViewById(R.id.users_dob);
                users_gender = bsView.findViewById(R.id.users_gender);
                users_bannedStatus = bsView.findViewById(R.id.users_bannedStatus);

                Glide.with(animatedImageBackground.getContext()).load(readWriteUserDetails.getProfileImage())
                        .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(animatedImageBackground);

                Glide.with(identify_image.getContext()).load(readWriteUserDetails.getProfileImage())
                        .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .error(R.drawable.ic_user)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(identify_image);

                users_name.setText(readWriteUserDetails.getFullName());
                users_userId.setText(readWriteUserDetails.getUserId());
                users_dateJoined.setText(readWriteUserDetails.getDateJoined());
                users_dob.setText(readWriteUserDetails.getDob());
                users_email.setText(readWriteUserDetails.getEmail());
                users_gender.setText(readWriteUserDetails.getGender());

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("userDetails").child(readWriteUserDetails.getUserId());
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild("isBanned")){
                            users_bannedStatus.setText(R.string.yes);
                        }else {
                            users_bannedStatus.setText(R.string.no);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

                bottomSheetDialog.setContentView(bsView);
                bottomSheetDialog.show();
            }
        });


        holder.three_dot_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Context wrapper = new ContextThemeWrapper(context, R.style.CustomPopupTheme);
                PopupMenu popup = new PopupMenu(wrapper, holder.three_dot_menu);
                popup.inflate(R.menu.user_experts_popup_menu);

                Menu menu = popup.getMenu();

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("userDetails");

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        if (menuItem.getItemId() == R.id.three_dot_email) {

                            if (readWriteUserDetails.getEmail().equals("n/a")) {
                                Toast.makeText(context, "User has no registered mail!", Toast.LENGTH_LONG).show();
                            } else {

                                Intent intent = new Intent(context, SentEmailActivity.class);
                                intent.putExtra("name", readWriteUserDetails.getFullName());
                                intent.putExtra("email", readWriteUserDetails.getEmail());
                                intent.putExtra("uid", readWriteUserDetails.getUserId());
                                context.startActivity(intent);

                            }


                        } else if (menuItem.getItemId() == R.id.three_dot_ban) {

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.RoundedCornersDialog);
                            View view = LayoutInflater.from(context).inflate(R.layout.custom_alert_dialog, null);
                            Button ok_button = view.findViewById(R.id.ok_button);
                            Button cancel_button = view.findViewById(R.id.cancel_button);
                            CheckBox custom_checkbox = view.findViewById(R.id.custom_checkbox);
                            cancel_button.setVisibility(View.VISIBLE);
                            ok_button.setVisibility(View.VISIBLE);
                            custom_checkbox.setVisibility(View.VISIBLE);
                            TextInputLayout custom_textview_for_editext = view.findViewById(R.id.custom_textview_for_editext);
                            EditText custom_edittext = view.findViewById(R.id.custom_edittext);

                            LottieAnimationView custom_animationView = view.findViewById(R.id.custom_animationView);
                            custom_animationView.setAnimation("red_warning.json");
                            custom_animationView.setVisibility(View.VISIBLE);
                            custom_animationView.setRepeatCount(LottieDrawable.INFINITE);

                            if (readWriteUserDetails.getEmail().equals("n/a")) {
                                custom_checkbox.setText(R.string.send_Email_to_user_user_has_no_email);
                                custom_checkbox.setEnabled(false);
                            } else {
                                custom_checkbox.setText(R.string.send_email_to_user);
                            }

                            int width = 800;
                            int height = 600;
                            RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(width, height);
                            parms.addRule(RelativeLayout.CENTER_HORIZONTAL);
                            custom_animationView.setLayoutParams(parms);

                            TextView custom_textview;
                            custom_textview = view.findViewById(R.id.custom_textview);
                            button_textview = view.findViewById(R.id.button_textview);
                            button_textview.setText("Email Preview");
                            String custom_text = "Are you sure, you want to ban " + readWriteUserDetails.getFullName() + "?"; //context.getString(R.string.logging_out);
                            custom_textview.setText(custom_text);
                            custom_textview.setVisibility(View.VISIBLE);

                            alertDialogBuilder.setView(view);
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.setCanceledOnTouchOutside(false);
                            ok_button.setTextColor(context.getResources().getColor(R.color.red));
                            ok_button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    databaseReference.child(readWriteUserDetails.getUserId()).child("isBanned").setValue("YES").addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            alertDialog.dismiss();
                                            Toast.makeText(context, readWriteUserDetails.getFullName() + " has been banned!", Toast.LENGTH_SHORT).show();
                                            if (custom_checkbox.isChecked()) {
                                                if (!custom_edittext.getText().toString().isEmpty()) {
                                                    custom_edittext.clearFocus();
                                                    banUserEmail(readWriteUserDetails, custom_edittext.getText().toString().trim());
                                                } else {
                                                    Toast.makeText(context, "Please enter ban reason!", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        }
                                    });

                                }
                            });
                            cancel_button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alertDialog.dismiss();
                                }
                            });
                            custom_checkbox.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (custom_checkbox.isChecked()) {
                                        custom_textview_for_editext.setVisibility(View.VISIBLE);
                                        button_textview.setVisibility(View.VISIBLE);
                                    } else {
                                        custom_textview_for_editext.setVisibility(View.GONE);
                                        button_textview.setVisibility(View.GONE);
                                    }
                                }
                            });

                            button_textview.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(context, WebViewActivity.class);
                                    intent.putExtra("whereFrom", "ShowUsersAdapter:BanUser");
                                    if (!custom_edittext.getText().toString().isEmpty()) {
                                        intent.putExtra("custom_reason", custom_edittext.getText().toString().trim());
                                    }
                                    intent.putExtra("name", readWriteUserDetails.getFullName());
                                    intent.putExtra("email", readWriteUserDetails.getEmail());
                                    context.startActivity(intent);
                                }
                            });


                            alertDialog.show();


                        } else if (menuItem.getItemId() == R.id.three_dot_Unban) {

                            AlertDialog.Builder logout = new AlertDialog.Builder(context, R.style.RoundedCornersDialog);
                            View view = LayoutInflater.from(context).inflate(R.layout.custom_alert_dialog, null);
                            Button ok_button = view.findViewById(R.id.ok_button);
                            Button cancel_button = view.findViewById(R.id.cancel_button);
                            CheckBox custom_checkbox = view.findViewById(R.id.custom_checkbox);
                            cancel_button.setVisibility(View.VISIBLE);
                            ok_button.setVisibility(View.VISIBLE);
                            custom_checkbox.setVisibility(View.VISIBLE);
                            custom_checkbox.setChecked(true);

                            LottieAnimationView custom_animationView = view.findViewById(R.id.custom_animationView);
                            custom_animationView.setAnimation("green_tick.json");
                            custom_animationView.setVisibility(View.VISIBLE);

                            if (readWriteUserDetails.getEmail().equals("n/a")) {
                                custom_checkbox.setText(R.string.send_Email_to_user_user_has_no_email);
                                custom_checkbox.setEnabled(false);
                            } else {
                                custom_checkbox.setText(R.string.send_email_to_user);
                            }

                            int width = 800;
                            int height = 600;
                            RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(width, height);
                            parms.addRule(RelativeLayout.CENTER_HORIZONTAL);
                            custom_animationView.setLayoutParams(parms);

                            TextView custom_textview, button_textview;
                            custom_textview = view.findViewById(R.id.custom_textview);
                            button_textview = view.findViewById(R.id.button_textview);
                            String custom_text = "Are you sure, you want to Unban " + readWriteUserDetails.getFullName() + "?"; //context.getString(R.string.logging_out);
                            custom_textview.setText(custom_text);
                            custom_textview.setVisibility(View.VISIBLE);
                            button_textview.setVisibility(View.VISIBLE);
                            button_textview.setText("Email Preview");

                            logout.setView(view);
                            AlertDialog alertDialog = logout.create();
                            alertDialog.setCanceledOnTouchOutside(false);
                            ok_button.setTextColor(context.getResources().getColor(R.color.main_color));
                            ok_button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    databaseReference.child(readWriteUserDetails.getUserId()).child("isBanned").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            alertDialog.dismiss();
                                            Toast.makeText(context, readWriteUserDetails.getFullName() + " has been Unbanned!", Toast.LENGTH_SHORT).show();
                                            if (custom_checkbox.isChecked()) {
                                                UnbanUserEmail(readWriteUserDetails);
                                            }
                                        }
                                    });

                                }
                            });

                            cancel_button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alertDialog.dismiss();
                                }
                            });

                            if (custom_checkbox.isChecked()) {
                                button_textview.setVisibility(View.VISIBLE);
                            } else {
                                button_textview.setVisibility(View.GONE);
                            }

                            button_textview.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(context, WebViewActivity.class);
                                    intent.putExtra("whereFrom", "ShowUsersAdapter:UnBanUser");
                                    intent.putExtra("name", readWriteUserDetails.getFullName());
                                    intent.putExtra("email", readWriteUserDetails.getEmail());
                                    context.startActivity(intent);
                                }
                            });

                            custom_checkbox.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (custom_checkbox.isChecked()) {
                                        button_textview.setVisibility(View.VISIBLE);
                                    } else {
                                        button_textview.setVisibility(View.GONE);
                                    }
                                }
                            });


                            alertDialog.show();
                        }

                        return false;
                    }
                });
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    popup.setForceShowIcon(true);
                }

                databaseReference.child(readWriteUserDetails.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild("isBanned")) {
                            menu.getItem(1).setEnabled(false);
                            menu.getItem(2).setEnabled(true);
                        } else {
                            menu.getItem(1).setEnabled(true);
                            menu.getItem(2).setEnabled(false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                popup.show();
            }
        });


    }

    private void UnbanUserEmail(ReadWriteUserDetails readWriteUserDetails) {

        String name = readWriteUserDetails.getFullName();
        String email = readWriteUserDetails.getEmail();

        MaildroidX.Builder maildroidX = new MaildroidX.Builder();
        maildroidX.smtp("smtp-relay.sendinblue.com");
        maildroidX.smtpUsername("debojit16mitra@gmail.com");
        maildroidX.smtpPassword("19x7bQYZaUzyTNqX");
        maildroidX.port("465");
        maildroidX.type(MaildroidXType.HTML);
        maildroidX.to(email);
        maildroidX.from("agrigroww@gmail.com");
        maildroidX.subject(name + ", You have been Unbanned!");

        maildroidX.body("<!DOCTYPE html>\n" +
                "<html>\n" +
                "\n" +
                "<head>\n" +
                "  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "  <title>You have been Unbanned!</title>\n" +
                "</head>\n" +
                "\n" +
                "<body style=\"font-family: Helvetica, Arial, sans-serif; margin: 0px; padding: 0px; background-color: #ffffff;\">\n" +
                "  <table role=\"presentation\"\n" +
                "    <style=\"width: 100%; border-collapse: collapse; border: 0px; border-spacing: 0px; font-family: Arial, Helvetica, sans-serif; background-color: rgb(239, 239, 239);\">\n" +
                "    <tbody>\n" +
                "      <tr>\n" +
                "        <td align=\"center\" style=\"padding: 1rem 2rem; vertical-align: top; width: 100%;\">\n" +
                "          <table role=\"presentation\" style=\"max-width: 600px; border-collapse: collapse; border: 0px; border-spacing: 0px; text-align: left;\">\n" +
                "            <tbody>\n" +
                "              <tr>\n" +
                "                <td style=\"padding: 40px 0px 0px;\">\n" +
                "                  <div style=\"text-align: left;\">\n" +
                "                    <div style=\"margin-bottom: -100px; text-align: center; background-color:white;background-color: rgb(255, 255, 255);\"><img src=\"https://i.ibb.co/0qf8xMS/splashlogo.png\" alt=\"AgriGroww\" style=\"width: 256px;\"></div>\n" +
                "                  </div>\n" +
                "                  <div style=\"padding: 20px; background-color: rgb(255, 255, 255);\">\n" +
                "                    <div style=\"color: rgb(0, 0, 0); text-align: left;\">\n" +
                "                      <h1 style=\"margin: 1rem 0; padding-bottom: 16px\">You have been unbanned!</h1>\n" +
                "                      <p style=\"padding-bottom: 16px\"><strong style=\"font-size: 110%\">We reviewed you and did`nt find any reason to ban you, sorry for the inconvenience cause.</strong></p>\n" +
                "                      <p style=\"padding-bottom: 16px\">Thanks,<br>The AgriGroww team</p>\n" +
                "                    </div>\n" +
                "                  </div>\n" +
                "                  <div style=\"padding-top: 20px; color: rgb(153, 153, 153); text-align: center;\">\n" +
                "                    <p style=\"padding-bottom: 16px\">Made with \uD83D\uDC96 in India</p>\n" +
                "                  </div>\n" +
                "                </td>\n" +
                "              </tr>\n" +
                "            </tbody>\n" +
                "          </table>\n" +
                "        </td>\n" +
                "      </tr>\n" +
                "    </tbody>\n" +
                "  </table>\n" +
                "</body>\n" +
                "\n" +
                "</html>");
        maildroidX.isJavascriptDisabled(false);
        maildroidX.onCompleteCallback(new MaildroidX.onCompleteCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(context, "Email successfully delivered to " + name + ".", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFail(@NonNull String s) {
                Toast.makeText(context, "Failed to deliver email!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public long getTimeout() {
                return 0;
            }
        });
        maildroidX.mail();

    }

    private void banUserEmail(ReadWriteUserDetails readWriteUserDetails, String custom_reason) {

        String name = readWriteUserDetails.getFullName();
        String email = readWriteUserDetails.getEmail();

        if (custom_reason.isEmpty()) {
            custom_reason = "You have violated our rules!";
        }

        MaildroidX.Builder maildroidX = new MaildroidX.Builder();
        maildroidX.smtp("smtp-relay.sendinblue.com");
        maildroidX.smtpUsername("debojit16mitra@gmail.com");
        maildroidX.smtpPassword("19x7bQYZaUzyTNqX");
        maildroidX.port("465");
        maildroidX.type(MaildroidXType.HTML);
        maildroidX.to(email);
        maildroidX.from("agrigroww@gmail.com");
        maildroidX.subject(name + ", You have been banned!");

        maildroidX.body("<!DOCTYPE html>\n" +
                "<html>\n" +
                "\n" +
                "<head>\n" +
                "  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "  <title>You have been banned!</title>\n" +
                "</head>\n" +
                "\n" +
                "<body style=\"font-family: Helvetica, Arial, sans-serif; margin: 0px; padding: 0px; background-color: #ffffff;\">\n" +
                "  <table role=\"presentation\"\n" +
                "    <style=\"width: 100%; border-collapse: collapse; border: 0px; border-spacing: 0px; font-family: Arial, Helvetica, sans-serif; background-color: rgb(239, 239, 239);\">\n" +
                "    <tbody>\n" +
                "      <tr>\n" +
                "        <td align=\"center\" style=\"padding: 1rem 2rem; vertical-align: top; width: 100%;\">\n" +
                "          <table role=\"presentation\" style=\"max-width: 600px; border-collapse: collapse; border: 0px; border-spacing: 0px; text-align: left;\">\n" +
                "            <tbody>\n" +
                "              <tr>\n" +
                "                <td style=\"padding: 40px 0px 0px;\">\n" +
                "                  <div style=\"text-align: left;\">\n" +
                "                    <div style=\"margin-bottom: -100px; text-align: center; background-color:white;background-color: rgb(255, 255, 255);\"><img src=\"https://i.ibb.co/0qf8xMS/splashlogo.png\" alt=\"AgriGroww\" style=\"width: 256px;\"></div>\n" +
                "                  </div>\n" +
                "                  <div style=\"padding: 20px; background-color: rgb(255, 255, 255);\">\n" +
                "                    <div style=\"color: rgb(0, 0, 0); text-align: left;\">\n" +
                "                      <h1 style=\"margin: 1rem 0; padding-bottom: 16px\">" + name + ", you have been banned!</h1>\n" +
                "                      <p style=\"padding-bottom: 4px\"><strong style=\"font-size: 130%\">Reason:</strong></p>\n" +
                "                       <p style=\"padding-bottom: 16px; color:black;\">" + custom_reason + "</p>" +
                "                      <p style=\"padding-bottom: 16px\">If you think it was a mistake, please submit a unban request!</p>\n" +
                "                      <p style=\"padding-bottom: 16px\">Thanks,<br>The AgriGroww team</p>\n" +
                "                    </div>\n" +
                "                  </div>\n" +
                "                  <div style=\"padding-top: 20px; color: rgb(153, 153, 153); text-align: center;\">\n" +
                "                    <p style=\"padding-bottom: 16px\">Made with \uD83D\uDC96 in India</p>\n" +
                "                  </div>\n" +
                "                </td>\n" +
                "              </tr>\n" +
                "            </tbody>\n" +
                "          </table>\n" +
                "        </td>\n" +
                "      </tr>\n" +
                "    </tbody>\n" +
                "  </table>\n" +
                "</body>\n" +
                "\n" +
                "</html>");
        maildroidX.isJavascriptDisabled(false);
        maildroidX.onCompleteCallback(new MaildroidX.onCompleteCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(context, "Email successfully delivered to " + name + ".", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFail(@NonNull String s) {
                Toast.makeText(context, "Failed to deliver email!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public long getTimeout() {
                return 0;
            }
        });
        maildroidX.mail();


    }

    @Override
    public int getItemCount() {
        return showUsersAdapterArrayList.size();
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            ArrayList<ReadWriteUserDetails> filteredSearchList = new ArrayList<>();
            if (charSequence.toString().trim().isEmpty()) {
                filteredSearchList.addAll(showUsersAdapterArrayList);
            } else {
                for (ReadWriteUserDetails readWriteUserDetails : showUsersAdapterArrayList) {
                    if (readWriteUserDetails.fullName.toLowerCase().contains(charSequence.toString().toLowerCase().trim())) {
                        filteredSearchList.add(readWriteUserDetails);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredSearchList;
            //results.count = filteredSearchList;
            return filterResults;


        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            showUsersAdapterArrayList.clear();
            showUsersAdapterArrayList.addAll((ArrayList<ReadWriteUserDetails>) filterResults.values);
            notifyDataSetChanged();

        }
    };

    public static class Holder extends RecyclerView.ViewHolder {

        TextView show_name, show_totalRequests;
        ImageView person_imageView;
        ProgressBar progressBar;
        ImageButton three_dot_menu;
        RelativeLayout recyclerView_relative_btn;

        public Holder(@NonNull View itemView) {
            super(itemView);

            progressBar = itemView.findViewById(R.id.progressBar);
            show_name = itemView.findViewById(R.id.show_name);
            show_totalRequests = itemView.findViewById(R.id.show_totalRequests);
            person_imageView = itemView.findViewById(R.id.person_imageView);
            three_dot_menu = itemView.findViewById(R.id.three_dot_menu);
            recyclerView_relative_btn = itemView.findViewById(R.id.recyclerView_relative_btn);
        }
    }
}
