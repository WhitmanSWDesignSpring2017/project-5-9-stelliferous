/*
 * Provided for use only in CS 370 at Whitman College.
 * DO NOT DISTRIBUTE.
 */
package controller;

import javafx.scene.paint.Color;
import javax.sound.midi.ShortMessage;

/**
 * Stores the enumerated type for instruments and associated functions to get them.
 * @author Janet Davis
 * @author Tyler Maule
 * @author Jingyuan Wang
 * @author Kaylin Jarriel
 */
public enum Instrument {
    
    //the enum that stores the program, channel, names, and color for each instrument
    PIANO         (1,  0, "Piano",        Color.MEDIUMSEAGREEN),
    HARPSICHORD   (7,  1, "Harpsichord",  Color.DARKSEAGREEN),
    MARIMBA       (13, 2, "Marimba",      Color.CADETBLUE),
    CHURCH_ORGAN  (20, 3, "Church Organ", Color.SKYBLUE),
    ACCORDION     (22, 4, "Accordion",    Color.BLUEVIOLET),
    GUITAR        (25, 5, "Guitar",       Color.MEDIUMPURPLE),
    VIOLIN        (41, 6, "Violin",       Color.MAROON), 
    FRENCH_HORN   (61, 7, "French Horn",  Color.CORAL),
    WOOD_BLOCK    (115, 8, "Wood Block",  Color.CHOCOLATE),
    PAN_FLUTE     (75, 10, "Pan Flute",   Color.VIOLET),
    BOTTLE        (78, 11, "Bottle",      Color.NAVY),
    CHOIR         (52, 12, "Choir",       Color.GOLDENROD);
    
    //the program which indicates the sound of the chosen instrument
    private final int midiProgram;
    
    //the channel on which the instrument sound is played
    private final int channel;    
    
    //the name that is displayed on the list of radio buttons
    private final String displayName;
    
    //the color which fills rectangles of the associated instrument
    private final Color displayColor;

    /**
     * Initializes an instrument.
     * @param midiProgram the program associated with the sound of the instrument
     * @param channel the channel which the instrument sound is played on
     * @param displayName the name of the instrument
     * @param displayColor the color associated with the instrument
     */
    Instrument(int midiProgram, int channel, 
               String displayName, Color displayColor) {
        this.midiProgram = midiProgram;
        this.channel = channel;
        this.displayName = displayName;
        this.displayColor = displayColor;
    }
    
    /**
     * Gets the midi program of the instrument.
     * @return the integer number of the program
     */
    public int getMidiProgram() {
        return midiProgram;
    }
    
    /**
     * Gets the channel of the instrument.
     * @return the integer number of the channel
     */
    public int getChannel() {
        return channel;
    }
    
    /**
     * Gets the display name of the instrument.
     * @return a string of the instrument name
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Gets the display color of the instrument.
     * @return the color of the instrument
     */
    public Color getDisplayColor() {
        return displayColor;
    }
    
    /**
     * Adds all the instruments to the midi player.
     * @param player the midi player that the instruments are added to
     */
    public static void addAll(MidiPlayer player) {
        for (Instrument inst : Instrument.values()) {
            player.addMidiEvent(ShortMessage.PROGRAM_CHANGE + inst.getChannel(), 
                                inst.getMidiProgram()-1, 
                                0, 0, 0);
        }
    }
}