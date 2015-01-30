package com.jakewharton.telecine;

import android.content.SharedPreferences;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import dagger.Module;
import dagger.Provides;
import java.util.Map;
import javax.inject.Singleton;
import timber.log.Timber;

import static android.content.Context.MODE_PRIVATE;

@Module(injects = {
    TelecineActivity.class,
    TelecineService.class,
    TelecineShortcutConfigureActivity.class,
    TelecineShortcutLaunchActivity.class,
})
final class TelecineModule {
  private static final String PREFERENCES_NAME = "telecine";
  private static final boolean DEFAULT_SHOW_COUNTDOWN = true;
  private static final boolean DEFAULT_HIDE_FROM_RECENTS = false;
  private static final boolean DEFAULT_RECORD_WITH_AUDIO = false;
  private static final int DEFAULT_VIDEO_SIZE_PERCENTAGE = 100;

  private final TelecineApplication app;

  TelecineModule(TelecineApplication app) {
    this.app = app;
  }

  @Provides @Singleton Analytics provideAnalytics() {
    if (BuildConfig.DEBUG) {
      return new Analytics() {
        @Override public void send(Map<String, String> params) {
          Timber.tag("Analytics").d(String.valueOf(params));
        }
      };
    }

    GoogleAnalytics googleAnalytics = GoogleAnalytics.getInstance(app);
    Tracker tracker = googleAnalytics.newTracker(BuildConfig.ANALYTICS_KEY);
    tracker.setSessionTimeout(300); // ms? s? better be s.
    return new Analytics.GoogleAnalytics(tracker);
  }

  @Provides @Singleton SharedPreferences provideSharedPreferences() {
    return app.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
  }

  @Provides @Singleton @ShowCountdown BooleanPreference provideShowCountdownPreference(
      SharedPreferences prefs) {
    return new BooleanPreference(prefs, "show-countdown", DEFAULT_SHOW_COUNTDOWN);
  }

  @Provides @ShowCountdown Boolean provideShowCountdown(@ShowCountdown BooleanPreference pref) {
    return pref.get();
  }

  @Provides @Singleton @HideFromRecents BooleanPreference provideHideFromRecentsPreference(
      SharedPreferences prefs) {
    return new BooleanPreference(prefs, "hide-from-recents", DEFAULT_HIDE_FROM_RECENTS);
  }

  @Provides @Singleton @RecordWithAudio BooleanPreference provideRecordWithAudioPreference(
      SharedPreferences prefs) {
    return new BooleanPreference(prefs, "record-with-audio", DEFAULT_RECORD_WITH_AUDIO);
  }

  @Provides @RecordWithAudio Boolean provideRecordWithAudio(@RecordWithAudio BooleanPreference pref) {
    return pref.get();
  }
    
  @Provides @Singleton @VideoSizePercentage IntPreference provideVideoSizePercentagePreference(
      SharedPreferences prefs) {
    return new IntPreference(prefs, "video-size", DEFAULT_VIDEO_SIZE_PERCENTAGE);
  }

  @Provides @VideoSizePercentage Integer provideVideoSizePercentage(
      @VideoSizePercentage IntPreference pref) {
    return pref.get();
  }
}
