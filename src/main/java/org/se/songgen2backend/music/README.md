<!--
  * @author Benjamin Frahm
  * @reviewer Malte Richert
 -->

# Music

This package deals with generating music, adding the text that was generated in the text component at the correct positions and persisting it in a midi file.
The generation is divided into 3 steps:

## 1. Template and text selection

In the first step a structure-template is selected from the genre's structure-templates (stored in yml-file).
The structure is then passed to the TextGeneration component, where fitting texts are selected.
Thereafter, some set-up work is done, like determining the number of midi tracks needed, calculating the length of the whole song, setting the tempo, etc.

## 2. Part-filling

After the structure and the texts have been selected, the actual music generation starts.
This is done by iterating over the parts of the structure (intro, chorus, verse, etc.) and generating the notes that are to be played by the instruments specified in the part.
The different instruments are treated as follows:

-   Chords: play the chord progression of the part in the right hand, play root note of the current chord in the left hand.
-   Bass: play a bass line that contains transitions between the chords of the part's progression
-   Drum: play a drumbeat selected from the drumBeat-templates
-   Melody: Every part has a 4- or 12-bar theme, which is then varied and applied in slightly different versions

Since some parts of the music are being reused (equal parts) or varied (theme variations), the music is not directly written as midi, but abstracted and stored as instances of the MidiPlayable class.

The specific midi instrument number to be associated with the abstract instruments (like chords, bass, etc.) is specified in a config file (instrument_mapping.yml).

## 3. Midi-File creation

After all MidiPlayables have been created, the conversion to actual midi begins, by iterating over the parts of the song in order, as specified in structure template, and writing the MidiPlayables' content into one Midi-Sequence-Object.
After this is done, the text is added at the required positions (indicated by "vocals" instrument in the structure).
