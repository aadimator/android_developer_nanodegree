package com.aadimator.bakingapp.fragments;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.text.TextUtilsCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aadimator.bakingapp.R;
import com.aadimator.bakingapp.models.Step;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Aadam on 21/06/2017.
 * <p>
 * StepFragment, Initializes the player as well
 */

public class StepFragment extends Fragment implements ExoPlayer.EventListener {

    private final String TAG = StepFragment.class.getSimpleName();
    private static final String BUNDLE_STEP_DATA = "com.aadimator.bakingapp.fragments.step_data";
    private static final String PLAYER_STATE = "player_playback_state";

    private Step mStep;
    private SimpleExoPlayer mExoPlayer;
    private MediaSessionCompat mMediaSession;
    private PlaybackStateCompat mPlaybackStateCompat;
    private PlaybackStateCompat.Builder mStateBuilder;

    @BindView(R.id.tv_directions)
    TextView tvStepFragmentDirections;
    @BindView(R.id.exo_player_view)
    SimpleExoPlayerView exoStepFragmentPlayerView;
    @BindView(R.id.iv_no_video_placeholder)
    ImageView ivStepFragmentNoVideoPlaceholder;

    public static StepFragment newInstance(Step step) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_STEP_DATA, step);
        StepFragment fragment = new StepFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if ((arguments != null) && (arguments.containsKey(BUNDLE_STEP_DATA))) {
            mStep = arguments.getParcelable(BUNDLE_STEP_DATA);
        }

        mPlaybackStateCompat = null;
        if (savedInstanceState != null) {
            mPlaybackStateCompat = savedInstanceState.getParcelable(PLAYER_STATE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_step, container, false);
        ButterKnife.bind(this, view);

        if (!getResources().getBoolean(R.bool.isTablet)) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        }

        if (!TextUtils.isEmpty(mStep.getVideoURL())) {
            initializeMediaSession();
            initializePlayer(Uri.parse(mStep.getVideoURL()));
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE &&
                    !getResources().getBoolean(R.bool.isTablet)) {
                hideSystemUI();
                tvStepFragmentDirections.setVisibility(View.GONE);
                exoStepFragmentPlayerView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                exoStepFragmentPlayerView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            } else {
                tvStepFragmentDirections.setText(mStep.getDescription());
            }
        } else {
            exoStepFragmentPlayerView.setVisibility(View.GONE);
            tvStepFragmentDirections.setText(mStep.getDescription());
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(mStep.getVideoURL())) {
            initializePlayer(Uri.parse(mStep.getVideoURL()));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // If correctly initialized....
        if (mStateBuilder != null && mExoPlayer != null) {
            // ... update the current player info
            updatePlaybackState(mExoPlayer.getPlayWhenReady(), mExoPlayer.getPlaybackState());
        }
        releasePlayer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
        if (mMediaSession != null) {
            mMediaSession.setActive(false);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(PLAYER_STATE, mPlaybackStateCompat);
    }

    private void hideSystemUI() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        getActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
    }

    private void initializeMediaSession() {
        mMediaSession = new MediaSessionCompat(getContext(), TAG);
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        mMediaSession.setMediaButtonReceiver(null);
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS);

        if (mPlaybackStateCompat == null) {
            mPlaybackStateCompat = mStateBuilder.build();
        }
        mMediaSession.setPlaybackState(mPlaybackStateCompat);
        mMediaSession.setCallback(new MySessionCallback());
        mMediaSession.setActive(true);
    }

    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            exoStepFragmentPlayerView.setPlayer(mExoPlayer);
            mExoPlayer.addListener(this);
            String userAgent = Util.getUserAgent(getContext(), "RecipeStepVideo");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.seekTo(mPlaybackStateCompat.getPosition());
            mExoPlayer.setPlayWhenReady(mPlaybackStateCompat.getState() == PlaybackStateCompat.STATE_PLAYING);
        }
    }

    private void releasePlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (this.isVisible()) {
            if (!isVisibleToUser) {
                if (mExoPlayer != null) {
                    mExoPlayer.setPlayWhenReady(false);
                }
            }
        }
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        updatePlaybackState(playWhenReady, playbackState);
    }

    private void updatePlaybackState(boolean playWhenReady, int playbackState) {
        if ((playbackState == ExoPlayer.STATE_READY) && playWhenReady) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if ((playbackState == ExoPlayer.STATE_READY)) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        mPlaybackStateCompat = mStateBuilder.build();
        mMediaSession.setPlaybackState(mPlaybackStateCompat);
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
    }

    @Override
    public void onPositionDiscontinuity() {
    }

    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }

}