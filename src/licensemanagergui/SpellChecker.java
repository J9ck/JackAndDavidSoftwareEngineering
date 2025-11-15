package licensemanagergui;

import org.apache.commons.text.similarity.LevenshteinDistance;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for fuzzy string matching of software names.
 * Helps correct common misspellings in license name input.
 */
public class SpellChecker {
    private static final LevenshteinDistance levenshtein = new LevenshteinDistance(3); // Max distance of 3
    private static final double SIMILARITY_THRESHOLD = 0.6; // 60% similarity required
    
    /**
     * Find the best match for a given input string from a list of valid options.
     * 
     * @param input The user's input (possibly misspelled)
     * @param validOptions List of valid software names
     * @return The best matching software name, or null if no good match found
     */
    public static String findBestMatch(String input, List<String> validOptions) {
        if (input == null || input.isEmpty() || validOptions == null || validOptions.isEmpty()) {
            return null;
        }
        
        String normalizedInput = input.toLowerCase().trim();
        String bestMatch = null;
        double bestScore = 0.0;
        
        for (String option : validOptions) {
            String normalizedOption = option.toLowerCase().trim();
            
            // Exact match
            if (normalizedInput.equals(normalizedOption)) {
                return option;
            }
            
            // Calculate similarity
            double similarity = calculateSimilarity(normalizedInput, normalizedOption);
            
            if (similarity > bestScore && similarity >= SIMILARITY_THRESHOLD) {
                bestScore = similarity;
                bestMatch = option;
            }
        }
        
        return bestMatch;
    }
    
    /**
     * Get all suggestions above the similarity threshold.
     * 
     * @param input The user's input
     * @param validOptions List of valid software names
     * @return List of suggested corrections
     */
    public static List<String> getSuggestions(String input, List<String> validOptions) {
        List<String> suggestions = new ArrayList<>();
        
        if (input == null || input.isEmpty() || validOptions == null || validOptions.isEmpty()) {
            return suggestions;
        }
        
        String normalizedInput = input.toLowerCase().trim();
        
        for (String option : validOptions) {
            String normalizedOption = option.toLowerCase().trim();
            double similarity = calculateSimilarity(normalizedInput, normalizedOption);
            
            if (similarity >= SIMILARITY_THRESHOLD) {
                suggestions.add(option);
            }
        }
        
        return suggestions;
    }
    
    /**
     * Calculate similarity score between two strings.
     * 
     * @param s1 First string
     * @param s2 Second string
     * @return Similarity score between 0.0 and 1.0
     */
    private static double calculateSimilarity(String s1, String s2) {
        if (s1.equals(s2)) {
            return 1.0;
        }
        
        // Use Levenshtein distance
        Integer distance = levenshtein.apply(s1, s2);
        
        // If distance is null, strings are too different (beyond threshold)
        if (distance == null || distance == -1) {
            return 0.0;
        }
        
        // Convert distance to similarity score
        int maxLen = Math.max(s1.length(), s2.length());
        if (maxLen == 0) {
            return 1.0;
        }
        
        return 1.0 - ((double) distance / maxLen);
    }
}
