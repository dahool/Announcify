
package com.announcify.tts;

import java.util.HashMap;
import java.util.Locale;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import com.announcify.api.tts.Speech;
import com.announcify.api.util.AnnouncifySettings;

public class Speaker extends TextToSpeech {
    private final HashMap<String, String> params;

    public Speaker(final Context context, final OnInitListener listener) {
        super(context, listener);

        params = new HashMap<String, String>();
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "com.announcify.UTTERANCE_ID");
        params.put(TextToSpeech.Engine.KEY_PARAM_STREAM,
                String.valueOf(new AnnouncifySettings(context).getStream()));
    }

    @Override
    public int speak(final String text, final int queueMode, final HashMap<String, String> params) {
        if (super.speak(text, queueMode, params) == TextToSpeech.SUCCESS) {
            return TextToSpeech.SUCCESS;
        } else {
            // TODO: send log to server
            return TextToSpeech.ERROR;
        }
    }

    public void speak(String text) {
        if (text == null) {
            text = "";
        }
        super.speak(text, TextToSpeech.QUEUE_ADD, params);
    }

    public void applyLanguage(final Speech speech) {
        super.setLanguage(speech.getLanguage());
        super.setPitch(speech.getPitch());
        super.setSpeechRate(speech.getSpeechRate());
    }

    public void revertLanguage() {
        super.setLanguage(Locale.getDefault());
    }

    synchronized public void interrupt() {
        super.stop();
    }

    @Override
    synchronized public void shutdown() {
        interrupt();
        super.shutdown();
    }
}
