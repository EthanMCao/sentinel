package dev.ethancao.antixray.logging;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

/**
 * Handles writing suspicious mining events to JSON log files.
 * Creates JSONL format (one JSON object per line) for easy SIEM ingestion.
 */
public class JsonLogger {
    private final File logFile;
    private final Gson gson;
    private final Logger pluginLogger;
    private final DateTimeFormatter isoFormatter;
    
    public JsonLogger(File dataFolder, Logger logger) {
        this.pluginLogger = logger;
        
        // Create logs directory structure
        File logsDir = new File(dataFolder, "logs");
        if (!logsDir.exists()) {
            if (logsDir.mkdirs()) {
                pluginLogger.info("Created logs directory: " + logsDir.getAbsolutePath());
            } else {
                pluginLogger.warning("Failed to create logs directory!");
            }
        }
        
        // Initialize log file
        this.logFile = new File(logsDir, "antixray.jsonl");
        
        // Create Gson instance for JSON serialization (single-line for JSONL)
        this.gson = new GsonBuilder()
                .disableHtmlEscaping()
                .create();
        
        // ISO 8601 formatter for timestamps
        this.isoFormatter = DateTimeFormatter.ISO_INSTANT;
        
        // Log initialization
        pluginLogger.info("JSON logger initialized: " + logFile.getAbsolutePath());
    }
    
    /**
     * Log a suspicious xray event to the JSON file.
     */
    public void logEvent(XrayEvent event) {
        try (FileWriter fw = new FileWriter(logFile, true);
             PrintWriter pw = new PrintWriter(fw)) {
            
            // Convert event to JSON (single line)
            String json = gson.toJson(event);
            
            // Write to file (JSONL format - one JSON per line)
            pw.println(json);
            
        } catch (IOException e) {
            pluginLogger.severe("Failed to write to log file: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Get current timestamp in ISO 8601 format.
     */
    public String getCurrentTimestamp() {
        return isoFormatter.format(Instant.now().atOffset(ZoneOffset.UTC));
    }
    
    /**
     * Get the log file path for reference.
     */
    public String getLogFilePath() {
        return logFile.getAbsolutePath();
    }
    
    /**
     * Check if log file exists and is writable.
     */
    public boolean isReady() {
        return logFile.getParentFile().exists() && logFile.getParentFile().canWrite();
    }
}

