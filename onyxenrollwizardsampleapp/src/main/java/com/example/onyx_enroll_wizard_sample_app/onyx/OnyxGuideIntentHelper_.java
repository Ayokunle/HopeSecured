package com.example.onyx_enroll_wizard_sample_app.onyx;

import android.content.Context;
import android.content.Intent;
import com.dft.onyx.guide.OnyxGuideActivity;

public class OnyxGuideIntentHelper_ {
    public OnyxGuideIntentHelper_() {
    }

    public Intent getOnyxGuideIntent(Context context, boolean ignorePrefs) {
        Intent onyxGuideIntent = (new Intent(context, OnyxGuideActivity_.class))
                .putExtra("ignore_guide_prefs", ignorePrefs);
        return onyxGuideIntent;
    }
}