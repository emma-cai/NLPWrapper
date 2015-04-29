package main.java.org.data.preprocess;

import java.io.*;
import java.util.ArrayList;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by qingqingcai on 4/27/15.
 */
public class OntoTreeToStandardTree {

    /** ******************************************************
     * Convert ontotree to standard tree: (1) remove "(TOP ";
     * (2) remove "-***" in "NP-***" tags; (3) replace "-NONE-"
     * with "NN" if the tag of "-NONE-" is *noun-phrase*;
     * otherwise, replace "-NONE-"; (4) remove the last ")";
     * with "X";
     */
    public static String convertOntoTreeToStandardTree(String ontotree) {

        String standardtree = "";
        // (1) remove the beginning tag "(TOP"
        standardtree = ontotree.replaceAll("^\\(TOP ", "");

        Pattern p = Pattern.compile("\\(XX ");
        Matcher m = p.matcher(standardtree);
        if (m.find()) return "";

        // remove "-***" in "NP-***" tags
        standardtree = standardtree.replaceAll("\\(((NP )|(NP-[A-Z]*[-]{0,1}[1-9]* ))\\(-NONE- ", "\\(NP \\(NN ");

        //
        standardtree = standardtree.replaceAll("\\(-NONE- ", "\\(X ");
        // remove "-***" in tags

        standardtree = standardtree.replaceAll("-(([A-Z]*)|([1-9]*)|([A-Z]*-[1-9]*))[^ ] \\(", " \\(");

        // (4) remove the ending tag ")", which is balanced with "(TOP"
        standardtree = standardtree.replaceAll("\\)$", "");
        return standardtree;
    }

    /** ******************************************************
     * Collect all ontotrees from input file, convert them to
     * StandardTrees, and save to a new file
     */
    public static void readData(String filepath, ArrayList<String> standardtreeList) {

        try {
            int index = 0;
            BufferedReader reader = new BufferedReader(new FileReader(filepath));
            StringBuilder stringbuilder = new StringBuilder();
            String stringseparator = System.getProperty("line.separator");
            String line = null;
            while ((line = reader.readLine()) != null) {
                // continue reading until an empty line is hit
                if (!line.isEmpty()) {
                    stringbuilder.append(line);
                    stringbuilder.append(stringseparator);
                } else {
                    String ontotree = stringbuilder.toString().trim();
                    String standardtree = convertOntoTreeToStandardTree(ontotree);
                    if (!standardtree.isEmpty() && checkParentheses(standardtree)==true)
                        standardtreeList.add(standardtree);

                    if (checkParentheses(standardtree) == false) {
                        System.out.println(index);
                        System.out.println("-------ontotree-------\n" + ontotree);
                        System.out.println("-------standardtree-------\n" + standardtree);
                    }

                    // reconstruct string builder
                    stringbuilder = new StringBuilder();
                    index++;
                }
            }
            String ontotree = stringbuilder.toString().trim();
            if (!ontotree.isEmpty()) {
                String standardtree = convertOntoTreeToStandardTree(ontotree);
                if (!standardtree.isEmpty() && checkParentheses(standardtree)==true)
                    standardtreeList.add(standardtree);

                if (checkParentheses(standardtree) == false) {
                    System.out.println(index);
                    System.out.println("-------ontotree-------\n" + ontotree);
                    System.out.println("-------standardtree-------\n" + standardtree);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** ******************************************************
     * Write standard tree to additional files
     */
    public static void writeData(String filepath, ArrayList<String> standardtreeList) {

        String directory = filepath.substring(0, filepath.lastIndexOf("/"));

        try {
            File theDir = new File(directory);
            if (!theDir.exists()) {
                theDir.mkdirs();
            }
            File file = new File(filepath);
            if (!file.exists())
                file.createNewFile();

            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            for (String standardtree : standardtreeList) {
                bw.write(standardtree);
                bw.newLine();
                bw.newLine();
            }
            bw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    public static boolean checkParentheses(String tree) {

        Stack<Character> stack = new Stack<Character>();
        char ch;
        for (int i = 0; i < tree.length(); i++) {
            ch = tree.charAt(i);
            if (ch == '(') {
                stack.push(ch);
            } else if (ch == ')') {
                if (stack.empty())
                    return false;
                else if (stack.peek() == '(')
                    stack.pop();
                else
                    return false;
            }
        }
        return stack.empty();
    }

    public static void test() {

        String input = "(TOP (FOO))";
        String expected = "(FOO)";
        String actual = convertOntoTreeToStandardTree(input);
        System.out.println("\n-------\ninput = " + input
                + "\nexpected = " + expected
                + "\nactual = " + actual
                + "\nmatched_parenthese = " + checkParentheses(actual) + "\n-------");

        input = "(TOP (TOP (FOO)))";
        expected = "(TOP (FOO))";
        actual = convertOntoTreeToStandardTree(input);
        System.out.println("\n-------\ninput = " + input
                + "\nexpected = " + expected
                + "\nactual = " + actual
                + "\nmatched_parenthese = " + checkParentheses(actual) + "\n-------");

        input = "(TOP (SBAR (-NONE- *EXP*-2)))";
        expected = "(SBAR (X *EXP*-2))";
        actual = convertOntoTreeToStandardTree(input);
        System.out.println("\n-------\ninput = " + input
                + "\nexpected = " + expected
                + "\nactual = " + actual
                + "\nmatched_parenthese = " + checkParentheses(actual) + "\n-------");

        input = "(TOP (NP (-NONE- *T*-1)))";
        expected = "(NP (NN *T*-1))";
        actual = convertOntoTreeToStandardTree(input);
        System.out.println("\n-------\ninput = " + input
                + "\nexpected = " + expected
                + "\nactual = " + actual
                + "\nmatched_parenthese = " + checkParentheses(actual) + "\n-------");

        input = "(TOP (NP-SBJ (-NONE- *-2)))";
        expected = "(NP (NN *-2))";
        actual = convertOntoTreeToStandardTree(input);
        System.out.println("\n-------\ninput = " + input
                + "\nexpected = " + expected
                + "\nactual = " + actual
                + "\nmatched_parenthese = " + checkParentheses(actual) + "\n-------");

        input = "(TOP (NP-SBJ-2 (-NONE- *T*-1)))";
        expected = "(NP (NN *T*-1))";
        actual = convertOntoTreeToStandardTree(input);
        System.out.println("\n-------\ninput = " + input
                + "\nexpected = " + expected
                + "\nactual = " + actual
                + "\nmatched_parenthese = " + checkParentheses(actual) + "\n-------");

        input = "(TOP (S-ADV (NP (NN *PRO*-2))))";
        expected = "(S (NP (NN *PRO*-2)))";
        actual = convertOntoTreeToStandardTree(input);
        System.out.println("\n-------\ninput = " + input
                + "\nexpected = " + expected
                + "\nactual = " + actual
                + "\nmatched_parenthese = " + checkParentheses(actual) + "\n-------");

        input = "(TOP (ADJP-PRD (-NONE- *?*)))";
        expected = "(ADJP (X *?*))";
        actual = convertOntoTreeToStandardTree(input);
        System.out.println("\n-------\ninput = " + input
                + "\nexpected = " + expected
                + "\nactual = " + actual
                + "\nmatched_parenthese = " + checkParentheses(actual) + "\n-------");

        input = "(TOP (SINV (VP-TPC-1 (VBG Standing))))";
        expected = "(SINV (VP (VBG Standing)))";
        actual = convertOntoTreeToStandardTree(input);
        System.out.println("\n-------\ninput = " + input
                + "\nexpected = " + expected
                + "\nactual = " + actual
                + "\nmatched_parenthese = " + checkParentheses(actual) + "\n-------");

        input = "(TOP (XX As)\n" +
                "     (XX the))";
        expected = "";
        actual = convertOntoTreeToStandardTree(input);
        System.out.println("\n-------\ninput = " + input
                + "\nexpected = " + expected
                + "\nactual = " + actual
                + "\nmatched_parenthese = " + checkParentheses(actual) + "\n-------");

        input = "(TOP (NP (S (NP-SBJ (-NONE- *PRO*-1)))))";
        expected = "(NP (S (NP (NN *PRO*-1))))";
        actual = convertOntoTreeToStandardTree(input);
        System.out.println("\n-------\ninput = " + input
                + "\nexpected = " + expected
                + "\nactual = " + actual
                + "\nmatched_parenthese = " + checkParentheses(actual) + "\n-------");

        //
        input = "(TOP (S (NP-SBJ-2 (NP (-NONE- *T*-1)))))";
        expected = "(S (NP (NP (NN *T*-1))))";
        actual = convertOntoTreeToStandardTree(input);
        System.out.println("\n-------\ninput = " + input
                + "\nexpected = " + expected
                + "\nactual = " + actual
                + "\nmatched_parenthese = " + checkParentheses(actual) + "\n-------");

        input = "(TOP (S (CC But)\n" +
                "        (NP-SBJ (PRP it))\n" +
                "        (VP (VBD did)\n" +
                "            (VP (VB provide)\n" +
                "                (NP (PRP us))\n" +
                "                (PP-CLR (IN with)\n" +
                "                        (NP (NP (DT a)\n" +
                "                                (VBN written)\n" +
                "                                (NN statement))\n" +
                "                            (VP (VBG saying)\n" +
                "                                (S-SEZ (INTJ (UH quote))\n" +
                "                                       (PP-MNR (IN like)\n" +
                "                                               (NP (DT any)\n" +
                "                                                   (NN company)))\n" +
                "                                       (NP-SBJ-1 (PRP we))\n" +
                "                                       (VP (VBP want)\n" +
                "                                           (S (NP-SBJ (-NONE- *PRO*-1))\n" +
                "                                              (VP (TO to)\n" +
                "                                                  (VP (VB make)\n" +
                "                                                      (S (NP-SBJ (NP (-NONE- *PRO*))\n" +
                "                                                                 (SBAR (-NONE- *EXP*-2)))\n" +
                "                                                         (ADJP-PRD (JJ sure))\n" +
                "                                                         (SBAR-2 (-NONE- 0)\n" +
                "                                                                 (S (NP-SBJ (PRP$ our)\n" +
                "                                                                            (NML (NML (NNS associates))\n" +
                "                                                                                 (NML (NNS customers))\n" +
                "                                                                                 (CC and)\n" +
                "                                                                                 (NML (JJ local)\n" +
                "                                                                                      (NN community))))\n" +
                "                                                                    (VP (VBP feel)\n" +
                "                                                                        (ADJP-PRD (JJ good)\n" +
                "                                                                                  (PP (IN about)\n" +
                "                                                                                      (NP (PRP us))))))))))))))))))\n" +
                "        (. /.)))";
        expected = "???";
        actual = convertOntoTreeToStandardTree(input);
        System.out.println("\n-------\ninput = " + input
                + "\nexpected = " + expected
                + "\nactual = " + actual
                + "\nmatched_parenthese = " + checkParentheses(actual) + "\n-------");

    }

    public static void readFile(String inputDirectory, String outputDirectory) {

        File theDir = new File(inputDirectory);
        File[] fList = theDir.listFiles();
        for (File file : fList) {
            String absolutePath = file.getAbsolutePath();
            if (file.isFile()) {
                String path = absolutePath.substring(absolutePath.indexOf("/data/english") + 13);
                String outputPath = outputDirectory + File.separator + path;

                if (file.getName().endsWith(".parse")) {
                    System.out.println("absolutepath = " + absolutePath);
                    System.out.println("path = " + path);
                    System.out.println("outputpath = " + outputPath);
                    System.out.println();
                    ArrayList<String> standardtreeList = new ArrayList<String>();
                    readData(absolutePath, standardtreeList);
                    writeData(outputPath, standardtreeList);
                }
            } else if (file.isDirectory()) {
                readFile(absolutePath, outputDirectory);
            }
        }
    }

    public static void main(String[] args) {

//        test();

//        ArrayList<String> standardlist = new ArrayList<String>();
//        readData("/Users/qingqingcai/Documents/ontonotes-release-5.0/data/files/data/english/annotations/bc/cnn/00/cnn_0001.parse", standardlist);

        String inputDirectory = "/Users/qingqingcai/Documents/ontonotes-release-5.0/data/files/data/english";
        String outputDirectory = "/Users/qingqingcai/Documents/IntellijWorkspace/NLPWrapper/data/files/data/english";

        readFile(inputDirectory, outputDirectory);
    }
}
