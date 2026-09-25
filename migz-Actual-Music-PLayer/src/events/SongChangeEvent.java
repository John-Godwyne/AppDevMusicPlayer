package events;

import java.util.EventObject;
import model.Song;

public class SongChangeEvent extends EventObject {
    private final Song song;

    public SongChangeEvent(Object source, Song song) {
        super(source);
        this.song = song;
    }

    public Song getSong() {
        return song;
    }
}