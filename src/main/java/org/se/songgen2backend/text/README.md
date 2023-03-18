<!--
  * @author Val Richter
 -->

# Text

This package deals with reading the text in the input file, extracting it's most common terms and using those and a general structure of the song, to write the lyrics for the song. In the following, each subpackage is shortly described. For more thorough explanations of each pacakge, see the respective READMEs in each directory.

## Analysis

After receiving the input text, it is analyzed to extract its most common terms. In our case, terms are either nouns or verbs. Using techniques from Natural Language Processing, we extract all terms from the text and analyze their grammatical form (e.g. its case, gender, numerus). For extracted nouns, we also offer the possibility of changing the term's grammatical form, if the lyrics require it.

## Metrics

We analyze certain metrics from the input text. These metrics then have an effect on the generated music and lyrics, making the song more closely related with the input text. To be specific, we analyze the tempo of the text, which changes the music's tempo accordingly, and we analyze the general mood of the text, which effects the templates used for the lyrics.

## Generation

To generate the lyrics for the song, we need both the terms extracted from the input-text, as well as a general structure of the song itself. Said structure will be the basis for creating the music to the song as well as the lyrics. To generate the lyrics we use templates. The templates are then filled with the most common terms from the input file and turned into their correct grammatical forms.
