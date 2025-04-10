package org.datastructures.string;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.toSet;

public class Anagram {
    private final String text;

    public Anagram(final String text) {
        this.text = text;
    }

    public Map<String, Set<String>> groupAnagrams() {
        List<String> words = Arrays.asList(text.split(" "));

        return words.stream()
                .collect(Collectors.groupingBy(this::hashAnagram, toSet()));

    }

    public Map<String, Set<String>> groupAnagramsStreamless() {
        List<String> words = Arrays.asList(text.split(" "));
        Map<String, Set<String>> anagramMap = new HashMap<>();

        for (String word : words) {
            final String anagramHash = hashAnagram(word);
            Set<String> anagrams = anagramMap.getOrDefault(anagramHash, new HashSet<>());
            anagrams.add(word);
            anagramMap.put(anagramHash, anagrams);
        }
        return anagramMap;
    }

    public Map<String, Long> anagramCount() {
        return Arrays.stream(text.split(" "))
                .collect(Collectors.groupingBy(this::hashAnagram, counting()));
    }

    public Set<String> mostPopularAnagrams() {
        Map<String, Set<String>> anagramMap = groupAnagrams();

        String baseAnagramWithMaxFrequency = anagramCount().entrySet()
                .stream()
                .max(Comparator.comparingLong(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElse("");

        return anagramMap.get(baseAnagramWithMaxFrequency);
    }

    public Set<String> mostPopularAnagramsStreams() {
        return groupAnagrams()
                .entrySet()
                .stream()
                .max(comparingByValue(new AnagramComparator()))
                .map(Map.Entry::getValue)
                .orElse(Collections.emptySet());
    }

    private String hashAnagram(final String input) {

        char[] charArray = input.toCharArray();
        Arrays.sort(charArray);

        return new String(charArray);
    }

    private String hashAnagramWithoutSort(final String input) {
        
        int[] frequencyArray = new int[52];
        Arrays.fill(frequencyArray, 0);

        for (char character : input.toCharArray()) {
            frequencyArray[character - 'A'] += 1;
        }


        StringBuilder hashCodeBuilder = new StringBuilder();
        for (int frequency : frequencyArray) {
            hashCodeBuilder.append("#");
            hashCodeBuilder.append(frequency);
        }

        return hashCodeBuilder.toString();
    }

    private AnagramFrequencyPair mostPopularAnagramWithFrequency() {
        return anagramCount()
                .entrySet()
                .stream()
                .max(comparingByValue())
                .map(entry -> new AnagramFrequencyPair(entry.getKey(), entry.getValue()))
                .orElse(null);
    }

    private String getAnagramHashUsingStream(final String input) {

        return input.chars()
                .sorted()
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }


    private static class AnagramComparator implements Comparator<Set<String>> {
        @Override
        public int compare(Set<String> o1, Set<String> o2) {
            return Integer.compare(o1.size(), o2.size());
        }
    }

    private static record AnagramFrequencyPair(String anagram, long frequency) {
    }
}
