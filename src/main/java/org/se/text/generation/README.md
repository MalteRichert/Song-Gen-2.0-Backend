
<!--  
  * @author Olivier Stenzel
-->  

# Text-Generation
This package deals with the generation of a song text based on a `structure`, a `MoodType`, a `TermCollaction` and several templates. The structure is generated in the music-package and the MoodType, as well as the TermCollaction, based on the text given by the user.

## How the generation works

### Get an overview of the requirements 
First, the order, the naming of the parts (intro, chorus, verse, etc.) and the number of bars of the song are saved on the basis of the passed structure.
Based on these, one part after the other is created and added to the song text.

### Create a strophe based on the part-requirements
A random strophe-template that has not yet been used is loaded from a collection of template, written in several .yml-files.
This strophe-template contains placeholder with requirements, which are analyzed and replaced by a matching word, stored in the TermCollaction.

### Passing the song text
For further processing, the song text is split and provided with additional information.
At the end the Text-Generator returns a list for each part, which assigns the generated song text and its number of syllables to each bar of this part.
