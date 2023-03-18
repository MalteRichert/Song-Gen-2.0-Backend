<!--
  * @author Jakob Kautz
 -->

# Metric

This package deals with analyzing the text from the input File according to pace and mood in order to influence toe final song output.
All parts follow the KISS principle (Keep It Stupid Simple), thus at times simplifying a method and loosing accuracy where it is not needed.

## Analyze Metrics

The metrics Analyzer influences the music component of the song.
We analyze the metrics of the song according to the average sentence length and number of syllabes. The metrics Analyzer returns a value for bpm.

## MoodAnalyzer

The mood Analyzer influences the text component of the song.
The text is analyzed for mood keywords to define if the mood is happy, sad, angry or horny. The chosen text templates for the song are suitable for the mood.

## WordCounter

The word Counter returns the amount of sentences and the amount of words from the input text so that the metrics Analyzer can calculate the average sentence length.

## Hyphenizer

The Hyphenizer counts the amounts of Syllabes in a word and returns the total amount of Syllabes in the input text so that the metricsAnalyzer can calculate the average amount of Syllabes per word.
