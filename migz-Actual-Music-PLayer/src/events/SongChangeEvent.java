package events;

import java.util.EventObject;
import model.Song;

public class SongChangeEvent extends EventObject {
    public enum Type { TRACK_CHANGED, PLAYING, PAUSED, STOPPED }

    private final Song song;
    private final Type type;

    public SongChangeEvent(Object source, Song song, Type type) {
        super(source);
        this.song = song;
        this.type = type;
    }

    public Song getSong() { return song; }
    public Type getType() { return type; }
}