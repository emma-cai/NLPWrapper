//package main.java.org.data.preprocess;
//
//
// public class test {
//
// private static final Set<String> particles = ImmutableSet.of(
// "abroad", "across", "after", "ahead", "along", "aside", "away", "around",
// "back", "down", "forward", "in", "off", "on", "over", "out",
// "round", "together", "through", "up"
// );
//
// private static void count(int counter, Treebank trainTreeBank) {
// counter++;
// if (counter % 1000 == 0) {
// System.out.println("Processing " + counter + " of " + trainTreeBank.size());
// }
// }
//
// private static String phrasalVerb(Morphology morpha, String word, String tag) {
// // must be a verb and contain an underscore
// assert (word != null);
// assert (tag != null);
// if (!tag.startsWith("VB") || !word.contains("_")) return null;
//
// // check whether the last part is a particle
// String[] verb = word.split("_");
// if (verb.length != 2) return null;
// String particle = verb[1];
// if (particles.contains(particle)) {
// String base = verb[0];
// String lemma = morpha.lemma(base, tag);
// return lemma + '_' + particle;
// }
//
// return null;
// }
//
// public static void tagLemma(List<CoreLabel> tokens) {
// // Not sure if this can be static.
// Morphology morpha = new Morphology();
//
// for (CoreLabel token : tokens) {
// String lemma;
// if (token.tag().length() > 0) {
// String phrasalVerb = phrasalVerb(morpha, token.word(), token.tag());
// if (phrasalVerb == null) {
// lemma = morpha.lemma(token.word(), token.tag());
// } else {
// lemma = phrasalVerb;
// }
// } else {
// lemma = morpha.stem(token.word());
// }
//
// // LGLibEn.convertUnI only accept cap I.
// if (lemma.equals("i")) {
// lemma = "I";
// }
//
// token.setLemma(lemma);
// }
// }
//
// public static String getCoNLLXString(Collection<TypedDependency> deps, List<CoreLabel> tokens) {
// StringBuilder bf = new StringBuilder();
//
// Map<Integer, TypedDependency> indexedDeps = new HashMap<>(deps.size());
// for (TypedDependency dep : deps) {
// indexedDeps.put(dep.dep().index(), dep);
// }
//
// int idx = 1;
//
// if (tokens.get(0).lemma() == null) {
// tagLemma(tokens);
// }
//
// for (CoreLabel token : tokens) {
// String word = token.word();
// String pos = token.tag();
// String cPos = (token.get(CoreAnnotations.CoarseTagAnnotation.class) != null) ?
// token.get(CoreAnnotations.CoarseTagAnnotation.class) : LangTools.getCPOSTag(pos);
// String lemma = token.lemma();
// Integer gov = indexedDeps.containsKey(idx) ? indexedDeps.get(idx).gov().index() : 0;
// String reln = indexedDeps.containsKey(idx) ? indexedDeps.get(idx).reln().toString() : "erased";
// String out = String.format("%d\t%s\t%s\t%s\t%s\t_\t%d\t%s\t_\t_\n", idx, word, lemma, cPos, pos, gov, reln);
// bf.append(out);
// idx++;
// }
// bf.append("\n");
// return bf.toString();
// }
//
// public static void convertTreebankToCoNLLX(String trainDirPath, FileFilter trainTreeBankFilter, boolean makeCopulaVerbHead, String outputFileName) {
// int counter = 0;
// DiskTreebank trainTreeBank = new DiskTreebank();
// trainTreeBank.loadPath(trainDirPath, trainTreeBankFilter);
//
// SemanticHeadFinder headFinder = new SemanticHeadFinder(!makeCopulaVerbHead); // keep copula verbs as head
//
// try {
// FileWriter fw = new FileWriter(outputFileName);
//
// trainTreeBank.parallelStream().forEach(tree -> {
// count(counter, trainTreeBank);
// Collection<TypedDependency> tdep = new EnglishGrammaticalStructure(tree, string -> true, headFinder, true).typedDependencies();
// String conllxString = getCoNLLXString(tdep, tree.taggedLabeledYield());
// try {
// fw.write(conllxString);
// } catch (IOException e) {
// e.printStackTrace();
// }
// });
//
// fw.flush();
// fw.close();
// } catch (IOException e) {
// e.printStackTrace();
// }
// }
//
//
// public static void main(String[] args) {
// FileFilter filter = new ExtensionFileFilter(".parse", true);
// convertTreebankToCoNLLX("/Users/Maochen/Desktop/data.mrg",filter,true,"/Users/Maochen/Desktop/conllx.txt");
//
// }
//
//
//
// }
