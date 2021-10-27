package com.drizzle.reward;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class MainActivity extends AppCompatActivity {
    private RewardedAd mRewardedAd;
    private final String TAG = "--->AdMob";

    private Button btn;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = findViewById(R.id.button);
        textView = findViewById(R.id.textView);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                loadRewardedAd();
            }
        });

        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showRewardedAd();
            }
        });
    }

    private void loadRewardedAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917",
                adRequest, new RewardedAdLoadCallback(){
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                Log.d(TAG, loadAdError.getMessage());
                mRewardedAd = null;
                Log.d(TAG, "onAdFailedToLoad");
            }

            @Override
            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                mRewardedAd = rewardedAd;
                Log.d(TAG, "광고가 로드 되었습니다.");

                mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                    @Override
                    public void onAdShowedFullScreenContent() {
                        Log.d(TAG, "광고가 표시되었습니다.");
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        Log.d(TAG, "광고 표시에 실패했습니다..");
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        Log.d(TAG, "광고가 닫혔습니다.");
                        // 광고를 닫으면 다시 광고를 로드합니다.
                        loadRewardedAd();
                    }
                });
            }
        });
    }
    private void showRewardedAd(){
        if(mRewardedAd != null){
            mRewardedAd.show(this, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) { // 사용자에게 보상을 제공
                    Log.d(TAG,"사용자가 보상을 받았습니다.");
                    int rewardAmount = rewardItem.getAmount();
                    String rewardType = rewardItem.getType();

                    int reward = Integer.parseInt(textView.getText().toString().trim());

                    textView.setText(String.valueOf(reward + rewardAmount));
                }
            });
        }else{
            Log.d(TAG, "보상형 광고가 아직 준비되지 않았습니다.");
        }
    }
}