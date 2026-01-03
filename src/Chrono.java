import java.time.Duration;
import java.time.LocalTime;
import javax.swing.JLabel;

public class Chrono implements Runnable {

    public LocalTime startTime;
    private final JLabel textChrono;

    private final Object lock = new Object();
    public volatile boolean paused = false;
    public volatile boolean running = true;
    public Duration duration;
    private Duration lastDuration;

    public Chrono(JLabel textChrono, LocalTime now) {
        this.startTime = now;
        this.textChrono = textChrono;
    }

    @Override
    public void run() {
        while (running) {

            synchronized (lock) {
                while (paused) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }

            this.duration = Duration.between(startTime, LocalTime.now());
            if(this.lastDuration !=null){
                this.duration = this.duration.plus(this.lastDuration);
            }
            long h = duration.toHours();
            long m = duration.toMinutes() % 60;
            long s = duration.toSeconds() % 60;
            long ms = duration.toMillis() % 1000;

            this.textChrono.setText(h+":"+m+":"+s+":"+ms);

            try {
                Thread.sleep(50); 
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    public void pause() {
        paused = true;
        if(this.lastDuration==null){
            this.lastDuration = this.duration;
        }
        else{
            this.lastDuration = this.lastDuration.plus(duration);
        }
    }

    public void resume() {
        synchronized (lock) {
            paused = false;
            lock.notifyAll();
        }
    }

    public void stop() {
        running = false;
        resume();
        
    }
}
