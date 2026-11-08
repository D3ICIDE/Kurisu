package voice;

import javax.sound.sampled.SourceDataLine;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

    public final class SpeakerPlayer implements AutoCloseable {

        private final SourceDataLine line;
        private final BlockingQueue<byte[]> queue = new LinkedBlockingQueue<>();
        private final Thread worker;
        private volatile boolean running = true;

        public SpeakerPlayer(SourceDataLine line) {
            this.line = line;
            this.worker = Thread.ofPlatform().name("speaker-player").daemon().start(this::drainLoop);
        }

        /** Queue a chunk of PCM audio for playback. */
        public void play(byte[] pcm) {
            if (running && pcm != null && pcm.length > 0) {
                queue.offer(pcm);
            }
        }

        /** User started talking over the model: drop everything queued and cut what is buffered. */
        public void interrupt() {
            queue.clear();
            line.flush();
        }

        private void drainLoop() {
            try {
                while (running) {
                    byte[] chunk = queue.take();
                    line.write(chunk, 0, chunk.length);
                }
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }

        @Override
        public void close() {
            running = false;
            worker.interrupt();
            queue.clear();
            line.stop();
            line.close();
        }
    }

