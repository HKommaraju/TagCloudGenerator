import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;

/**
 *
 * @author Libby Mannix
 * @author Harshitha Kommaraju
 *
 */
public final class TagCloudGenerator {

    /**
     * Default constructor--private to prevent instantiation.
     */
    private TagCloudGenerator() {
        // no code needed here
    }

    /**
     * includes all characters and character combinations that are considered
     * separators.
     */
    private static String separator = " [] {} \" \t , ; -- ._`~\n\0!:&#*+=<>()/\\?    '";

    /**
     * Compare {@code Map.Pair}s in decreasing order of value.
     */
    private static class MapPairValue
            implements Comparator<Map.Entry<String, Integer>> {
        @Override
        public int compare(java.util.Map.Entry<String, Integer> m1,
                java.util.Map.Entry<String, Integer> m2) {
            //toLowerCase is used so words with capitalized first letters
            //are not sorted differently than the same word with a lowercase first letter
            int result = m2.getValue().compareTo(m1.getValue());
            if (result == 0) {
                result = m1.getKey().compareTo(m2.getKey());
            }
            return result;

            //note: they are still recognized as separate words, but are
            //placed next to each other in the ordering

        }
    }

    /**
     * Compare {@code Map.Pair}s in lexicographic order.
     */
    private static class MapPairKey
            implements Comparator<Map.Entry<String, Integer>> {
        @Override
        public int compare(java.util.Map.Entry<String, Integer> m1,
                java.util.Map.Entry<String, Integer> m2) {

            int result = m1.getKey().compareTo(m2.getKey());
            if (result == 0) {
                result = m1.getValue().compareTo(m2.getValue());
            }
            //toLowerCase is used so words with capitalized first letters
            //are not sorted differently than the same word with a lowercase first letter
            return result;

            //note: they are still recognized as separate words, but are
            //placed next to each other in the ordering

        }
    }

    /**
     * creates a map with each word as the key and number of times it is written
     * in the text as the value.
     *
     * @ensures a map that has with each different in the text file as the key
     *          and the amount of times each is found as the value.
     * @param file
     *            input file
     * @return map (unordered) words and their count values
     */
    public static Map<String, Integer> mapCreator(BufferedReader file) {

        Map<String, Integer> wordsMap = new HashMap<String, Integer>();

        //runs until the end of the file

        //resets position and moves to the next line in the file
        int pos = 0;
        String line;
        try {
            line = file.readLine();
            while (line != null) {
                while (pos < line.length()) {
                    String word = nextWordOrSeparator(line, pos);
                    //Additional Activity #2 - counts capitalized and lower case words
                    //as the same
                    word = word.toLowerCase();
                    //does not add any values in sepatatorSet to the wordMap
                    if ((separator.indexOf(word.charAt(0)) < 0)) {
                        //if the word is not in the map, it adds the word to the
                        //map as a pair
                        if (!wordsMap.containsKey(word)) {
                            wordsMap.put(word, 1);
                            //if word is already there, it adds 1 to the count
                            //for that word
                        } else {
                            int count = wordsMap.remove(word);
                            wordsMap.put(word, count + 1);
                        }
                    }
                    //moves the position to the end of the word or separator found
                    pos = pos + word.length();

                }
                pos = 0;
                line = file.readLine();
            }

        } catch (IOException e) {
            System.out.println("Error reading from file or empty file");
            System.exit(0);
        }

        return wordsMap;
    }

    /**
     * Prints the title and headers to the outfile in HTML format.
     *
     * @ensures HTML page with titles and headers.
     *
     * @param out
     *            writes to the output file
     * @param url
     *            link to text file
     * @param fileName
     *            name of the file used for titles on the output page
     * @param num
     *            number of words in TagCloud
     */
    private static void htmlFormat(BufferedWriter out, String fileName,
            String url, int num) {
        //formats title and headers
        try {
            out.write("<html>\n");
            out.write("<head>\n");
            //links to formatting page
            out.write("<title>Top " + num + " words in " + fileName
                    + "</title><link href=\"http://web.cse.ohio-state.edu"
                    + "/software/2231/web-sw2/assignments/projects/tag-cloud-generator"
                    + "/data/tagcloud.css\" "
                    + "rel=\"stylesheet\" type=\"text/css\">\n");
            out.write("</head>\n");

            out.write("<body>\n");
            out.write("<h2>Top " + num + " words in " + fileName + "</h2><hr>");
            out.write("<div class=\"cdiv\">\n");
            out.write("<p class=\"cbox\">\n");

        } catch (IOException e) {
            System.err.print("Error writing to file");
        }
    }

    /**
     * Adds a pair (word and it's count) to the map.
     *
     * @ensures a word and it's corresponding count is added to the HTML file
     *
     * @param out
     *            Writes to output file
     * @param m1
     *            pair of word and times counted
     * @param lowest
     *            minimum count of word
     * @param highest
     *            maximum count of word
     */
    private static void printWord(java.util.Map.Entry<String, Integer> m1,
            BufferedWriter out, int lowest, int highest) { //throws IOException

        int font = calculateFontSize(lowest, highest, m1.getValue());

        try {
            out.write("<span style=\"cursor:default\" class=\"f" + font
                    + "\" title=\"count: " + m1.getValue() + "\">" + m1.getKey()
                    + "</span>\n");
        } catch (IOException e) {
            System.err.println("Error writing to file");
        }

    }

    /**
     * Calculates and returns a font size corresponding to the given count.
     *
     * @ensures a word and it's corresponding count is added to the HTML file
     *
     * @param minCount
     *            minimum times a word is counted
     * @param maxCount
     *            max word and times counted
     * @param wordCount
     *            number of times the word appears in the text
     * @return font size to be displayed
     */
    private static int calculateFontSize(int minCount, int maxCount,
            int wordCount) {
        final int minFont = 11;
        final int maxFont = 48;
        int font = 11;
        if (!(minCount == maxCount)) {
            font = (((maxFont - minFont) * (wordCount - minCount))
                    / (maxCount - minCount)) + minFont;
        }

        return font;

    }

    /**
     * Returns the first "word" (maximal length string of characters not in
     * {@code separators}) or "separator string" (maximal length string of
     * characters in {@code separators}) in the given text starting at the given
     * position.
     *
     * @param text
     *            the string from which to get the word or separator string
     * @param position
     *            the starting index
     * @param separators
     *            the {@code Set} of separator characters
     * @return the first word or separator string found in {@code text} starting
     *         at index {@code position}
     * @requires 0 <= position < |text|
     * @ensures <pre>
     * nextWordOrSeparator =
     *   text[position, position + |nextWordOrSeparator|)  and
     * if entries(text[position, position + 1)) intersection separators = {}
     * then
     *   entries(nextWordOrSeparator) intersection separators = {}  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      intersection separators /= {})
     * else
     *   entries(nextWordOrSeparator) is subset of separators  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      is not subset of separators)
     * </pre>
     */
    private static String nextWordOrSeparator(String text, int position) {
        assert text != null : "Violation of: text is not null";
        assert 0 <= position : "Violation of: 0 <= position";
        assert position < text.length() : "Violation of: position < |text|";

        StringBuilder next = new StringBuilder();
        int i = position;

        if (separator.indexOf(text.charAt(i)) >= 0) {
            while ((i < text.length()
                    && separator.indexOf(text.charAt(i)) >= 0)) {
                next.append(text.charAt(i));
                i++;
            }
        } else {

            //using short circuit evaluation
            while ((i < text.length()
                    && separator.indexOf(text.charAt(i)) < 0)) {
                next.append(text.charAt(i));
                i++;
            }
        }

        return next.toString();
    }

    /**
     * This method was made and given to us by professor Heym through piazza as
     * a reply to our posted question.
     *
     * Given an entrySet view of a Map, returns a non-view List containing
     * Map.Entry pairs, each with its own reference that is valid for further
     * processing.
     *
     * @param <K>
     *            the key-type of the Map backing the entrySet view
     *
     * @param <V>
     *            the value-type of the Map backing the entrySet view
     *
     * @param s
     *            entrySet view of a Map
     *
     * @return a non-view List of the entries in the given entrySet
     * @ensures entries(nonViewListOfValidMapEntries) = s and [while the key and
     *          value references within the Map.Entry<K, V> pairs of
     *          nonViewListOfValidMapEntries may be aliased within some Map and
     *          one or more of its views, the Map.Entry<K, V> pairs themselves
     *          are not backed by any other object (not even by any Map) and are
     *          independent and valid for further processing]
     */
    private static <K, V> java.util.List<java.util.Map.Entry<K, V>> nonViewListOfValidMapEntries(
            java.util.Set<java.util.Map.Entry<K, V>> s) {
        java.util.List<java.util.Map.Entry<K, V>> list;
        list = new java.util.LinkedList<>();
        for (java.util.Map.Entry<K, V> p : s) {
            list.add(new java.util.AbstractMap.SimpleEntry<>(p));
        }
        return new java.util.ArrayList<>(list);
    }

    /**
     * Adds the top num amount of words with the highest word counts to a new
     * Priority Queue.
     *
     * @param num
     *            number of words to include in tagCloud
     *
     * @param countOrder
     *            a priorityQueue ordered by word count
     * @param alph
     *            PriorityQueue ordered alphabetically
     * @param outFile
     *            File to output to
     *
     */
    private static void createAlphOrderedQueue(int num,
            PriorityQueue<Map.Entry<String, Integer>> countOrder,
            PriorityQueue<Map.Entry<String, Integer>> alph,
            BufferedWriter outFile) {
        int highest = 0;
        int lowest = 0;
        //gets highest and lowest counts to be used in font size calculation
        if (num <= countOrder.size()) {
            for (int i = 0; i < num; i++) {
                Map.Entry<String, Integer> removed = countOrder.remove();
                if (i == 0) {
                    highest = removed.getValue();
                }

                if (i == num - 1) {
                    lowest = removed.getValue();
                }
                //adds top num words to PriorityQueue
                alph.add(removed);

            }
        } else {

            System.err.println(
                    "number of words requested more than words in file");
        }

        while (alph.size() > 0) {
            //removes words alphabetically and prints
            Map.Entry<String, Integer> current = alph.remove();
            printWord(current, outFile, lowest, highest);
        }

    }

    /**
     * Puts all entries from WordMap into a PriorityQueue.
     *
     * @param wordMap
     *            All the words in the file and their counts
     *
     * @param counts
     *            a priorityQueue ordered by word count
     *
     */
    private static void createCountOrderedQueue(Map<String, Integer> wordMap,
            PriorityQueue<Map.Entry<String, Integer>> counts) {
        //adds values from wordMap to the PriorityQueue
        List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>();
        list = nonViewListOfValidMapEntries(wordMap.entrySet());

        while (list.size() > 0) {
            counts.add(list.remove(0));
        }

    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments; unused here
     */
    public static void main(String[] args) {

        //Reads input from the console
        Scanner in = new Scanner(System.in);

        System.out.println("Name of input file: ");
        String fileName = in.nextLine();

        System.out.println("Name of an output file: ");
        String url = in.nextLine();

        System.out.println("Number of words to be included in tag cloud: ");
        int num = Integer.parseInt(in.nextLine());

        //BufferedWriter for the output (html) file
        BufferedWriter outFile = null;
        try {
            outFile = new BufferedWriter(new FileWriter(url));

        } catch (IOException e) {
            System.err.println("Error writing to file");
            in.close();
            return;
        }

        htmlFormat(outFile, fileName, url, num);

        //Reads the file the user inputs
        BufferedReader fileReader = null;

        try {
            fileReader = new BufferedReader(new FileReader(fileName));
        } catch (IOException e) {
            System.err.println("Error opening file to read from");

        }
        //make sure no method is called unless stream is open
        if (fileReader != null) {
            Map<String, Integer> wordMap = mapCreator(fileReader);

            Comparator<Map.Entry<String, Integer>> order = new MapPairValue();
            
            if(wordMap.size()>0)
            {
            PriorityQueue<Map.Entry<String, Integer>> highestCount = new PriorityQueue<Map.Entry<String, Integer>>(
                    wordMap.size(), order);
            createCountOrderedQueue(wordMap, highestCount);

            Comparator<Map.Entry<String, Integer>> order2 = new MapPairKey();
            PriorityQueue<Map.Entry<String, Integer>> alphabetized = new PriorityQueue<Map.Entry<String, Integer>>(
                    num, order2);
            createAlphOrderedQueue(num, highestCount, alphabetized, outFile);

            //Completes the HTML formatting for the output file
            try {
                outFile.write("</p>\n");
                outFile.write("</div>\n");
                outFile.write("</body>\n");
                outFile.write("</html>\n");

            } catch (IOException e) {
                System.err.print("Error writing to file");
            }

            //Close all BufferedReaders and BufferedWriters
            in.close();
            try {
                outFile.close();
            } catch (IOException e) {
                System.err.print("Error closing file");
            }

            try {
                fileReader.close();
            } catch (IOException e) {
                System.err.print("Error closing file");
            }
            }
        }
    }
}
