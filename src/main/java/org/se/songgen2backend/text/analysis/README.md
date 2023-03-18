<!--
  * @author Val Richter
 -->

# Text-Analysis

This sub-component of the Text-component takes care of analyzing the input-text. From the outside, this package is a black box, taking a text as input and outputting a collection of most common terms in the input. We will first take a look at the logic used in the Analysis and then provide an overview over the file structure here, to allow for better code-navigation.

## Logic

Internally, we achieve this via a three-step process. First, we read and preprocess the text. Then we tag each word and lastly we transform each word that was tagged as a Noun or Verb into a Term.

Reading and preprocessing of the text are both done outside of the Analyzer class, as their outputs are also used elsewhere.

For tagging words, we currently use two methods. Firstly, if the word is capitalized and isn't the first word in the sentence, we know that it's a noun. Secondly, we try all combinations of splittin suffixes and prefixes from the word, to see if the word's stem/radix (used synonymously here) are found in our dictionary of nouns or verbs. If so, then we know what type of word it is.

We also store all the grammatical data associated with the suffixes, prefixes and word's stem that we found, because when we build the terms, we do the exact same process again. If we already stored said data, we avoid doing the same procedure again of course.

We collect all noun-terms and verb-terms in respective lists and store the amount of times that they appeared with them. However, if we find two words in different grammatical forms, but with the same radix, then we store them together in a so-called TermVariation. When the SongTextGenerator then needs a specific form of a word, we first check for each term if we already saw it in said grammatical form. If not, we use certain rules stored in the dictionary, to create said grammatical form. Often the result is correct, but due to german's complex rules and its many edge-cases, it's inevitable, that we won't always be able to create the exact correct form of the word.

## File Structure

The `FileReader` and `Preprocessor` are both called first from outside. Their output serves as an input to the `Analyzer`, who takes care of tagging the words and building the terms.

The `TermCollection` class is used as the Analyzer's output and internally stores lists of `TermVariation`s for the verbs and nouns, that were found. The `TermVariaton` in turn, stores a map of terms, where each term is associated with a key. Said key reflects the grammatical form of the Term, allowing us to quickly search for a specific variation and to check if one such variation already exists.

When building the terms, we create a new `Term` object, where the `Term` is the parent class of the `VerbTerm` and `NounTerm` class. Semantically it makes sense to have both as children of the `Term`-class and since both share some attributes, it also reduces the code-duplication.

Lastly, in the top directory here, we only have the `Tag` class, which primarily stores the type of its word (i.e. Noun, Verb, Other), the `TermComp` class, which is simply used to sort terms to have the most common ones at the top of the list, and the `Util` class, which offers some utility functionality, that is used in some classes here.

### Model

The classes in the Model directory are only used to model certain data without associated functionality. Many of the classes in there are also Enums.

### Dict

The dict-class stores all classes related to the dictionary and its functionality. These are of no importance to the outside, but vital for recognizing a word's grammatical form.

The `Dict` class itself is the dictionary. It reads in data from the resources and uses said data for finding a word's grammatical form and type. All data, that the dictionary reads, is stored in CSV-files. The parsing of these files is done via the `Parser`-class, which offers some convenience methods, like immediately adding each row to a list. The `Parser` also offers methods to parse a string into the correct type, which often is simply some Enum. For non-Enums, the Parser includes one method, that uses reflections, to recognize the type of each attribute, so it can parse each column accordingly.

If no such parsing should be done, the row is simply stored as a `WordWithData` object, which is a wrapper around a Map from a String to a String.

The `WordList` class is also a wrapper, here it's around a List of `WordWithData` objects. The WordList also guarantees to be lexicographically sorted at all times and thus offer quick searching. It is used for many of the lists of data read in by the dictionary.

However, the `WordList` specifically sorts after one value in the `WordWithData` object (usually called "radix"). When said radix can be duplicate in the list of data, we instead use a simple List and traverse it linearly. Since none of the lists with this caveat are very big, it shouldn't really be a performance overhead.

Lastly, the `WordStemmer` class offers a way to model a word split into its specific suffixes and prefixes. It's used as an intermediate model, before turning the words into `Term` objects.
