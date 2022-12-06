<!--
 * @author Val Richter (for this README and for CSV-Files)
 -->

All CSV-Files in this directory are written in the same format. Rows are separated by newlines, columns are separated by commas and lists of values are seperated with dashes in the column. The use dashes avoids using quotation to specify a list of values inside a column.

In the `declination.csv` file, the dollar-sign is used as a flag to show, that vowels in the word's stem might have to be changed to their umlaut-versions. The flag is specific to that ending and not all endings in the list. For this conversion, refer to the rules outlined in the `umlautChanges.csv`. If the ending doesn't change from the stem, then the cell is either left out, or if it's only one of many options to leave it empty, the other options are appended with a `-`. The empty ending can also be annotated with the `$` flag.

The `umlautChanges.csv` file contains a table showing the same sounds with and without Umlaute. The order matters, as it determines which sounds will be chosen first. That also explains the inclusion of `eu`, which doesn't have a version with Umlaute. However, `eu` is listed before `u`, meaning that the diphtong `eu` won't be changed to `eü`.