package com.sanskar.Code.Library.Backend.testutil;

import com.sanskar.Code.Library.Backend.model.Snippet;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Slf4j
public class CsvLoader {
    public static List<Snippet> loadSnippets(String filePath){
        List<Snippet> snippets = new ArrayList<>();
        // try-with-resources
        // no need to close resource explicitly in finally block
        // it will be closed automatically
        try(Scanner scanner = new Scanner(new File(filePath))){
            // Skipping header
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("\\|");
                // in regex, pipe is special character, so we need to escape it
                // in java, \ is special, so we again need to escape it

                Snippet snippet = Snippet.builder()
                        .id(parts[0].trim())
                        .authorName(parts[1].trim())
                        .publicVisibility(Boolean.parseBoolean(parts[2].trim()))
                        .deleted(Boolean.parseBoolean(parts[3].trim()))
                        .title(parts[4].trim())
                        .build();

                // tags
                if(!parts[4].trim().isEmpty()){
                    List<String> tags = Arrays.asList(parts[4].trim().split(","));
                    snippet.setTags(tags);
                }

                // pendingRequests
                if (!parts[5].trim().isEmpty()) {
                    List<String> pendingRequests = Arrays.asList(parts[5].trim().split(","));
                    snippet.setPendingPushRequestIds(pendingRequests);
                }

                // collaborators
                if (!parts[6].trim().isEmpty()) {
                    Map<String, String> collaborators = new HashMap<>();
                    String[] collabParts = parts[6].trim().split(",");
                    for (String collab : collabParts) {
                        String[] keyValue = collab.split(":");
                        if (keyValue.length == 2) {
                            collaborators.put(keyValue[0].trim(), keyValue[1].trim());
                        }
                    }
                    snippet.setCollaborators(collaborators);
                }

                snippet.setVersion(0);

                snippets.add(snippet);
            }
        }
        catch (IOException e) {
            log.error("Error reading CSV file at: {}", filePath);
        }
        return snippets;
    }
}