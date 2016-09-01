package com.example.onyx_enroll_wizard_sample_app;

import android.util.Log;

import com.dft.onyx.FingerprintTemplate;
import com.dft.onyx.FingerprintTemplateVector;
import com.dft.onyx.MatchResult;
import com.dft.onyx.core;
import com.dft.onyx.verify.IdentifyFingerprintTask;

public class IdentifyFingerprint {
    private static final String TAG = "IdentifyFingerprint";

    public int identifyFingerprint(FingerprintTemplateVector gallery, FingerprintTemplate ft) {
        int galleryIndex = 0;
        try {
            MatchResult matchResult = core.identify(gallery, ft);
            Log.e(TAG, "Score: " + matchResult.getScore() );
            if (matchResult.getScore() > IdentifyFingerprintTask.MATCH_SCORE) {
                galleryIndex = matchResult.getIndex();
                return galleryIndex;
            } else {
                galleryIndex = IdentifyFingerprintTask.MATCH_FAIL;
                return galleryIndex;
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception identifying templates.", e);
        }
        galleryIndex = IdentifyFingerprintTask.MATCH_ERROR;
        return galleryIndex;
    }
}