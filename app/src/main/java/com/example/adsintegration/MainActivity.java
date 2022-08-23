package com.example.adsintegration;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.mediation.ads.MaxRewardedAd;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements MaxAdListener, MaxRewardedAdListener {

    private MaxAdView adView;
    private MaxInterstitialAd interstitialAd;
    private MaxRewardedAd rewardedAd;
    private int retryAttempt;
    AppCompatButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button=findViewById(R.id.show_iv_ads);

        //applovin initial
        AppLovinSdk.getInstance(MainActivity.this).setMediationProvider("max");
        AppLovinSdk.initializeSdk(MainActivity.this, new AppLovinSdk.SdkInitializationListener() {
            @Override
            public void onSdkInitialized(final AppLovinSdkConfiguration configuration) {

            }
        });

        adView = findViewById(R.id.adView);
        adView.loadAd();
        LoadInterstitialAd();
        LoadMaxRewardedAdAd();


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rewardedAd.isReady()) {
                    rewardedAd.showAd();

                    // code here
                    LoadMaxRewardedAdAd();
                }
            }
        });
    }

    private void LoadInterstitialAd() {

        interstitialAd = new MaxInterstitialAd( "YOUR_AD_UNIT_ID", this );
        interstitialAd.setListener( this );
        interstitialAd.loadAd();
    }

    private void LoadMaxRewardedAdAd() {

        rewardedAd = MaxRewardedAd.getInstance("YOUR_AD_UNIT_ID", this);
        rewardedAd.setListener(this);
        rewardedAd.loadAd();
    }

    @Override
    public void onAdLoaded(final MaxAd maxAd)
    {

        retryAttempt = 0;
    }

    @Override
    public void onAdLoadFailed(final String adUnitId, final MaxError error)
    {
        retryAttempt++;
        long delayMillis = TimeUnit.SECONDS.toMillis( (long) Math.pow( 2, Math.min( 6, retryAttempt ) ) );

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                rewardedAd.loadAd();
            }
        }, delayMillis );
    }

    @Override
    public void onAdDisplayFailed(final MaxAd maxAd, final MaxError error)
    {

        rewardedAd.loadAd();
        interstitialAd.loadAd();
    }

    @Override
    public void onAdDisplayed(final MaxAd maxAd) {}

    @Override
    public void onAdClicked(final MaxAd maxAd) {}

    @Override
    public void onAdHidden(final MaxAd maxAd)
    {
        rewardedAd.loadAd();
        interstitialAd.loadAd();
    }

    @Override
    public void onRewardedVideoStarted(final MaxAd maxAd) {}

    @Override
    public void onRewardedVideoCompleted(final MaxAd maxAd) {
        //users offer
        Toast.makeText(this, "Thanks", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUserRewarded(final MaxAd maxAd, final MaxReward maxReward)
    {

    }
}