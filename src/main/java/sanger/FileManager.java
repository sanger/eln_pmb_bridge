package sanger;

import java.io.*;
import java.util.*;

/**
 * @author hc6
 */
public class FileManager {

    public PrintRequest readFile(String filename) throws IOException {
        File file = findFile(filename);
        if (file==null) {
            throw new FileNotFoundException("No file found with file name: " + filename);
        }

        PrintRequest request = null;
        Scanner scanner = new Scanner(file);
        while (scanner.hasNext()) {
            String s = scanner.next();
            String[] values = s.split("[|]");

            Map<String, String> fieldMap = new HashMap<>();
            fieldMap.put("cell_line", values[0]);
            fieldMap.put("barcode", values[1]);

            List<PrintRequest.Label> labels = new ArrayList<>();
            PrintRequest.Label label = new PrintRequest.Label(fieldMap);
            labels.add(label);

            request = new PrintRequest("d340bc", labels);
            System.out.println(request);
        }
        return request;

//        FileReader reader = new FileReader(f);
//        BufferedReader br = new BufferedReader(reader);
//        System.out.println(br.readLine());
    }

    private File findFile(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("Filename is null");
        }
        File f = new File(System.getProperty("user.home") + File.separator + filename);
        if (f.isFile()) {
            return f;
        }
        return null;
    }

}
