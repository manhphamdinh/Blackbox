package com.example.blackbox;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.SoundPool;

import java.util.EnumMap;
import java.util.Map;

public class AudioManager {
    private static final int SOUND_POOL_MAX_STREAMS = 5;
    private static final int BGM_MAIN = R.raw.bgm_73bpm;    // Fuck magic values

    private static MediaPlayer bgm;
    private static SoundPool soundPool;

    private enum SFX {
        LEVEL_SELECT(R.raw.level_select),
        BOX_COMPLETE(R.raw.box_complete),
        PUZZLE_COMPLETE(R.raw.puzzle_complete);

        private final int resourceId;

        SFX(int resourceId) {
            this.resourceId = resourceId;
        }
    }

    private static class Sound {
        int soundId;
        boolean loaded;

        Sound(int soundId) {
            this.soundId = soundId;
            this.loaded = false;
        }
    }

    // ENUM MAP
    private static final Map<SFX, Sound> sounds = new EnumMap<>(SFX.class);

    public static void init(Context context) {
        if (soundPool != null) {
            return;
        }

        soundPool = new SoundPool.Builder().setMaxStreams(SOUND_POOL_MAX_STREAMS).build();

        for (SFX sfx : SFX.values()) {
            load(context, sfx);
        }

        soundPool.setOnLoadCompleteListener((sp, sampleId, status) -> {
            for (Sound sound : sounds.values()) {
                if (sound.soundId == sampleId) {
                    sound.loaded = true;
                    break;
                }
            }
        });
    }

    // HELPERS
    private static void load(Context context, SFX sfx) {
        int soundId = soundPool.load(context, sfx.resourceId, 1);
        sounds.put(sfx, new Sound(soundId));
    }

    private static void playSFX(SFX sfx) {
        if (soundPool == null) return;

        Sound sound = sounds.get(sfx);
        if (sound == null) return;

        if (sound.loaded) {
            soundPool.play(sound.soundId, 1, 1, 1, 0, 1);
        }
    }

    // SFX METHODS
    public static void playLevelSelectSFX() {
        playSFX(SFX.LEVEL_SELECT);
    }

    public static void playBoxCompleteSFX() {
        playSFX(SFX.BOX_COMPLETE);
    }

    public static void playPuzzleCompleteSFX() {
        playSFX(SFX.PUZZLE_COMPLETE);
    }

    // BGM METHODS
    public static void startBgm(Context context) {
        if (bgm == null) {
            bgm = MediaPlayer.create(context.getApplicationContext(), BGM_MAIN);
            bgm.setLooping(true);
        }

        if (!bgm.isPlaying()) {
            bgm.start();
        }
    }

    public static void pauseBgm() {
        if (bgm != null && bgm.isPlaying()) {
            bgm.pause();
        }
    }

    // RELEASE
    public static void release() {
        if (bgm != null) {
            bgm.release();
            bgm = null;
        }

        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }

        sounds.clear();
    }
}