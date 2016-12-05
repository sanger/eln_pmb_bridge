package uk.ac.sanger.eln_pmb_bridge;

import java.io.File;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

/**
 * @author hc6
 */
public class WatchDirectoryTest {

    public void testEventForDirectory() throws Exception {
        WatchService watchService = FileSystems.getDefault().newWatchService();

        FileManager fileManager = new FileManager();
        fileManager.setPMBProperties();

        Path pollPath = Paths.get("/Users/hc6/Desktop/eln_pmb_folder/poll_folder_test");
        WatchKey basePatchWatchKey = pollPath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

        File file1 = new File("/Users/hc6/Desktop/eln_pmb_folder/poll_folder_test/test1.txt");
        File file2 = new File("/Users/hc6/Desktop/eln_pmb_folder/poll_folder_test/test2.txt");
        File file3 = new File("/Users/hc6/Desktop/eln_pmb_folder/poll_folder_test/test3.txt");

        WatchKey watchKey = watchService.poll(10000, TimeUnit.MILLISECONDS);
        watchKey.cancel();
        assertNotNull(watchKey);
        assertEquals(watchKey, basePatchWatchKey);
        List<WatchEvent<?>> eventList = watchKey.pollEvents();
        assertEquals(eventList.size(), 3);
        for (WatchEvent event : eventList) {
            assertEquals(event.kind(), StandardWatchEventKinds.ENTRY_CREATE);
            assertEquals(event.count(),1);
        }
        watchKey.cancel();
        Path eventPath = (Path) eventList.get(0).context();
        assertEquals(eventPath, Paths.get("test1.txt"));
        Path watchedPath = (Path) watchKey.watchable();
        assertEquals(watchedPath, pollPath);
        watchService.close();
    }

}
