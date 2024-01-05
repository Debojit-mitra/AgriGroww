package com.dmsskbm.agrigroww.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.dmsskbm.agrigroww.R;
import com.dmsskbm.agrigroww.VideoPlayerActivity;
import com.dmsskbm.agrigroww.models.ModelMessage;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ozaydin.serkan.com.image_zoom_view.ImageViewZoom;
public class MessagesAdapter extends RecyclerView.Adapter{

    Context context;
    ArrayList<ModelMessage> messageArrayList;

    final int ITEM_SENT = 1;
    final int ITEM_RECEIVE = 2;

    public MessagesAdapter(Context context, ArrayList<ModelMessage> messageArrayList) {
        this.context = context;
        this.messageArrayList = messageArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == ITEM_SENT){
            View view = LayoutInflater.from(context).inflate(R.layout.sender_chat_layout, parent, false);
            return new SentViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.receiver_chat_layout, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        ModelMessage message = messageArrayList.get(position);
        //checking if user is the sender to sent message in the activity
        if(FirebaseAuth.getInstance().getUid().equals(message.getSenderId())){
            return ITEM_SENT;
        } else {

            return ITEM_RECEIVE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ModelMessage message = messageArrayList.get(position);
        //to stop messages from duplicating
        holder.setIsRecyclable(false);

        if(holder.getClass() == SentViewHolder.class){
            SentViewHolder viewHolder = (SentViewHolder) holder;
            //setting sender message
            viewHolder.textView_senderMessage.setText(messageArrayList.get(holder.getLayoutPosition()).getMessage());
            //converting timestamp to date
            Date d = new Date(message.getTimestamp());
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-yy ");
            String date  = dateFormat.format(d);
            viewHolder.textView_senderMessage_time.setText(date);

            try{
                if (message.getMessageType().equals("photo")) {
                    viewHolder.textView_senderMessage.setVisibility(View.GONE);
                    viewHolder.cardView_data.setVisibility(View.VISIBLE);
                    viewHolder.imageView_senderImage.setVisibility(View.VISIBLE);

                    RequestBuilder<Drawable> requestBuilder = Glide.with(viewHolder.imageView_senderImage.getContext()).asDrawable().sizeMultiplier(0.1f);
                    Glide.with(context).load(messageArrayList.get(holder.getLayoutPosition()).getDataUrl()).thumbnail(requestBuilder).addListener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    viewHolder.progressBar.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    viewHolder.progressBar.setVisibility(View.GONE);
                                    return false;
                                }
                            }).diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.placeholder)
                            .into(viewHolder.imageView_senderImage);

                }

                if(message.getMessageType().equals("audio")){
                    viewHolder.textView_senderMessage.setVisibility(View.GONE);
                    viewHolder.cardView_data.setVisibility(View.VISIBLE);
                    viewHolder.audioPlayer.setVisibility(View.VISIBLE);
                    viewHolder.progressBar.setVisibility(View.GONE);

                    String url = message.getDataUrl();
                    ExoPlayer exoPlayer;
                    viewHolder.videoPlayer.setShowFastForwardButton(false);
                    viewHolder.videoPlayer.setShowRewindButton(false);
                    viewHolder.videoPlayer.setShowNextButton(false);
                    viewHolder.videoPlayer.setShowPreviousButton(false);
                    viewHolder.audioPlayer.setControllerHideOnTouch(false);
                    exoPlayer = new ExoPlayer.Builder(context).build();
                    viewHolder.audioPlayer.setPlayer(exoPlayer);
                    com.google.android.exoplayer2.upstream.DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, "app"));
                    MediaSource audioSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(url)));
                    exoPlayer.prepare(audioSource);
                    exoPlayer.setPlayWhenReady(false);
                }

                if(message.getMessageType().equals("video")){
                    viewHolder.textView_senderMessage.setVisibility(View.GONE);
                    viewHolder.cardView_data.setVisibility(View.VISIBLE);
                    viewHolder.videoPlayer.setVisibility(View.VISIBLE);
                    viewHolder.progressBar.setVisibility(View.GONE);

                    String url = message.getDataUrl();
                    ExoPlayer exoPlayer;
                    viewHolder.audioPlayer.setControllerShowTimeoutMs(0);
                    viewHolder.videoPlayer.setShowFastForwardButton(false);
                    viewHolder.videoPlayer.setShowRewindButton(false);
                    viewHolder.videoPlayer.setShowNextButton(false);
                    viewHolder.videoPlayer.setShowPreviousButton(false);
                    viewHolder.videoPlayer.setControllerHideOnTouch(true);
                    exoPlayer = new ExoPlayer.Builder(context).build();
                    viewHolder.videoPlayer.setPlayer(exoPlayer);
                    com.google.android.exoplayer2.upstream.DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, "app"));
                    MediaSource audioSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(url)));
                    exoPlayer.prepare(audioSource);
                    exoPlayer.setPlayWhenReady(false);
                    viewHolder.videoPlayer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            exoPlayer.pause();
                            Intent intent = new Intent(context, VideoPlayerActivity.class);
                            intent.putExtra("videoURL", url);
                            context.startActivity(intent);
                        }
                    });

                }


            }catch (Exception e){
                e.printStackTrace();
            }

        } else {
            ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;
            //setting receiver message
            viewHolder.textView_receiverMessage.setText(message.getMessage());
            //converting timestamp to date
            Date d = new Date(message.getTimestamp());
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-yy ");
            String date  = dateFormat.format(d);
            viewHolder.textView_receiverMessage_time.setText(date);

            try{
                if (message.getMessageType().equals("photo")) {
                    viewHolder.textView_receiverMessage.setVisibility(View.GONE);
                    viewHolder.cardView_data.setVisibility(View.VISIBLE);
                    viewHolder.imageView_receiverImage.setVisibility(View.VISIBLE);


                    Glide.with(context).load(message.getDataUrl()).addListener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    viewHolder.progressBar.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    viewHolder.progressBar.setVisibility(View.GONE);
                                    return false;
                                }
                            }).diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.placeholder)
                            .diskCacheStrategy(DiskCacheStrategy.ALL).into(viewHolder.imageView_receiverImage);

                }

                if(message.getMessageType().equals("audio")){
                    viewHolder.textView_receiverMessage.setVisibility(View.GONE);
                    viewHolder.cardView_data.setVisibility(View.VISIBLE);
                    viewHolder.audioPlayer.setVisibility(View.VISIBLE);
                    viewHolder.progressBar.setVisibility(View.GONE);

                    String url = message.getDataUrl();
                    ExoPlayer exoPlayer;
                    viewHolder.audioPlayer.setControllerShowTimeoutMs(0);
                    viewHolder.audioPlayer.setCameraDistance(30);
                    viewHolder.audioPlayer.setControllerHideOnTouch(false);
                    exoPlayer = new ExoPlayer.Builder(context).build();
                    viewHolder.audioPlayer.setPlayer(exoPlayer);
                    com.google.android.exoplayer2.upstream.DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, "app"));
                    MediaSource audioSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(url)));
                    exoPlayer.prepare(audioSource);
                    exoPlayer.setPlayWhenReady(false);
                }

                if(message.getMessageType().equals("video")){
                    viewHolder.textView_receiverMessage.setVisibility(View.GONE);
                    viewHolder.cardView_data.setVisibility(View.VISIBLE);
                    viewHolder.videoPlayer.setVisibility(View.VISIBLE);
                    viewHolder.progressBar.setVisibility(View.GONE);

                    String url = message.getDataUrl();
                    ExoPlayer exoPlayer;
                    viewHolder.audioPlayer.setControllerShowTimeoutMs(0);
                    viewHolder.videoPlayer.setShowFastForwardButton(false);
                    viewHolder.videoPlayer.setShowRewindButton(false);
                    viewHolder.videoPlayer.setShowNextButton(false);
                    viewHolder.videoPlayer.setShowPreviousButton(false);
                    viewHolder.videoPlayer.setControllerHideOnTouch(true);
                    exoPlayer = new ExoPlayer.Builder(context).build();
                    viewHolder.videoPlayer.setPlayer(exoPlayer);
                    com.google.android.exoplayer2.upstream.DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, "app"));
                    MediaSource audioSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(url)));
                    exoPlayer.prepare(audioSource);
                    exoPlayer.setPlayWhenReady(false);
                    viewHolder.videoPlayer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            exoPlayer.pause();
                            Intent intent = new Intent(context, VideoPlayerActivity.class);
                            intent.putExtra("videoURL", url);
                            context.startActivity(intent);
                        }
                    });

                }

            } catch (Exception e){
                e.printStackTrace();
            }


            //setting receiver role and name
            String isAdmin = message.getIsAdmin();
            String isExpert = message.getIsExpert();

            if(isAdmin.equals("YES")){
                String receiverRoleAndName = "@Admin : "+message.getName();
                viewHolder.textView_receiverRoleAndName.setText(receiverRoleAndName);
            } else if (isExpert.equals("YES")) {
                String receiverRoleAndName = "@Expert : "+message.getName();
                viewHolder.textView_receiverRoleAndName.setText(receiverRoleAndName);
            }

        }

    }

    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }

    public class SentViewHolder extends RecyclerView.ViewHolder {

        TextView textView_senderMessage, textView_senderMessage_time;
        CardView cardView_data;
        ImageViewZoom imageView_senderImage;
        ProgressBar progressBar;
        StyledPlayerView audioPlayer;
        StyledPlayerView videoPlayer;
        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            textView_senderMessage = itemView.findViewById(R.id.textView_senderMessage);
            textView_senderMessage_time = itemView.findViewById(R.id.textView_senderMessage_time);
            cardView_data = itemView.findViewById(R.id.cardView_data);
            imageView_senderImage = itemView.findViewById(R.id.imageView_senderImage);
            progressBar = itemView.findViewById(R.id.progressBar);
            audioPlayer = itemView.findViewById(R.id.audioPlayer);
            videoPlayer = itemView.findViewById(R.id.videoPlayer);
        }
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder {

        TextView textView_receiverMessage, textView_receiverMessage_time, textView_receiverRoleAndName;
        CardView cardView_data;
        ImageViewZoom imageView_receiverImage;
        ProgressBar progressBar;
        StyledPlayerView audioPlayer;
        StyledPlayerView videoPlayer;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            textView_receiverMessage = itemView.findViewById(R.id.textView_receiverMessage);
            textView_receiverMessage_time = itemView.findViewById(R.id.textView_receiverMessage_time);
            textView_receiverRoleAndName = itemView.findViewById(R.id.textView_receiverRoleAndName);
            cardView_data = itemView.findViewById(R.id.cardView_data);
            imageView_receiverImage = itemView.findViewById(R.id.imageView_receiverImage);
            progressBar = itemView.findViewById(R.id.progressBar);
            audioPlayer = itemView.findViewById(R.id.audioPlayer);
            videoPlayer = itemView.findViewById(R.id.videoPlayer);
        }
    }

}
