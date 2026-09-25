package events;

import java.util.EventListener;

public interface SongChangeListener extends EventListener {
    void songChanged(SongChangeEvent event);
}