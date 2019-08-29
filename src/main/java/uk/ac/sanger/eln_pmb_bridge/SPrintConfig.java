package uk.ac.sanger.eln_pmb_bridge;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * SPrintConfig reader, based on code from sccp-lims.
 * @author dr6
 */
public class SPrintConfig {
    private static final String TEMPLATE_KEY_START = "#", TEMPLATE_KEY_END = "#";

    private static SPrintConfig instance;

    private Map<LabelType, StringTemplate> templates;
    private String host;

    public SPrintConfig(String host, Map<LabelType, StringTemplate> templates) {
        this.host = host;
        this.templates = templates;
    }

    public String getHost() {
        return this.host;
    }

    public StringTemplate getTemplate(LabelType labelType) {
        StringTemplate template = templates.get(labelType);
        if (template==null) {
            throw new IllegalArgumentException(ErrorType.MISSING_TEMPLATE.getMessage()+labelType);
        }
        return template;
    }

    public static SPrintConfig getInstance() {
        return instance;
    }

    private static SPrintConfig load(String path) throws IOException {
        Properties properties = PropertiesFileReader.loadFile(path);
        return load(properties);
    }

    public static void initialise(String path) throws IOException {
        instance = load(path);
    }

    private static SPrintConfig load(Properties properties) throws IOException {
        String host = getProp(properties, "host");

        if (host==null) {
            throw new IOException(ErrorType.MISSING_SPRINT_URL.getMessage());
        }

        Map<LabelType, StringTemplate> templates = loadTemplates(properties);
        return new SPrintConfig(host, templates);
    }

    /**
     * Gets a value from properties.
     * If it's not null, trim it.
     * If the trimmed value is empty, return null.
     * @param properties the properties
     * @param key the key to look up
     * @return the value read, or null
     */
    private static String getProp(Properties properties, String key) {
        String value = properties.getProperty(key, "").trim();
        return (value.isEmpty() ? null : value);
    }

    /**
     * Reads template file locations from the given map, and reads the template files from the given locations.
     * Not every label type needs to have a template.
     * @param properties properties read from the sprint properties file
     * @return a map of label types to templates
     * @exception IOException there was a problem reading a template
     */
    private static Map<LabelType, StringTemplate> loadTemplates(Properties properties) throws IOException {
        Map<LabelType, StringTemplate> templates = new EnumMap<>(LabelType.class);
        String templateDir = getProp(properties, "template_dir");
        for (LabelType lt : LabelType.values()) {
            String key = lt.name().toLowerCase();
            String templateFilename = getProp(properties, key);
            if (templateFilename==null) {
                continue;
            }
            String templateString = readTemplate(templateDir, templateFilename);
            StringTemplate template = new StringTemplate(templateString, TEMPLATE_KEY_START, TEMPLATE_KEY_END);
            templates.put(lt, template);
        }
        return templates;
    }

    /**
     * Reads a template from the specified filename (optionally with the specified directory).
     * The file path is made up of the template filename, preceded by the directory path (if it is not null).
     * The file path should be absolute.
     * @param templateDir (optional) the directory containing templates
     * @param templateFilename the filename of the template
     * @return the content of the template file read
     * @exception IOException the file could not be read
     */
    private static String readTemplate(String templateDir, String templateFilename) throws IOException {
        Path templatePath;
        if (templateDir == null) {
            templatePath = Paths.get(templateFilename);
        } else {
            templatePath = Paths.get(templateDir, templateFilename);
        }
        return Files.lines(templatePath).collect(Collectors.joining("\n"));
    }

}
