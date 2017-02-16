package extras;

import android.media.MediaPlayer;

/**
 * Created by Dylan on 10/25/2015.
 */

    public class BlastMediaPlayer {
        public MediaPlayer mp = new MediaPlayer();
        private static volatile BlastMediaPlayer instance = null;
        private BlastMediaPlayer() { }

        public static BlastMediaPlayer getInstance() {
            if (instance == null) {
                synchronized (BlastMediaPlayer.class) {
                    if (instance == null) {
                        instance = new BlastMediaPlayer();

                    }
                }
            }

            return instance;
        }
    }

