package utils;

import GUI.ImageHandler;
import process.Conf;
import utils.yield.Yielderable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Utils {
    public static ArrayList<String> scanDir(String dir) { // Already recursive
        ArrayList<String> results = new ArrayList<>();
        try {
            Files.walk(Paths.get(dir)).forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    results.add(filePath.toString());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }

    public static Yielderable<String> streamFiles(String dir, String ext) {
        return yield -> {
            for (String p : scanDir(dir)) {
                if (ext == null || p.endsWith(ext))
                    if (new File(p).isFile())
                        yield.returning(p);
            }
        };
    }

    public static Yielderable<BufferedImage> streamImages(String dir, String ext) {
        return yield -> {
            for (String p : streamFiles(dir, ext)) {
                try {
                    yield.returning(ImageIO.read(new File(p)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public static ArrayList<BufferedImage> listImages(String dir, String ext) {
        ArrayList<BufferedImage> images = new ArrayList<>();
        for (BufferedImage r : streamImages(dir, ext))
            images.add(r);
        return images;
    }

    public static Yielderable<ImageHandler> streamImageHandler(String dir, String ext) {
        return yield -> {
            for (String p : scanDir(dir)) {
                if (ext == null || p.endsWith(ext))
                    if (new File(p).isFile())
                        yield.returning(new ImageHandler(p));
            }
        };
    }

    public static int countFiles(String dir, String ext) {
        int count = 0;
        for (String p : scanDir(dir))
            if (ext == null || p.endsWith(ext))
                if (new File(p).isFile())
                    count++;
        return count;
    }

    public static String[] computeIndexes(String testsetDir) {
        // Compute the list of files to give them an index
        File directoryPos = new File(testsetDir + "faces");
        File directoryNeg = new File(testsetDir + "non-faces");

        String[] fileListPos = directoryPos.list();
        String[] fileListNeg = directoryNeg.list();
        ArrayList<String> fileList = new ArrayList<>();
        for (String aFileListNeg : fileListNeg) {
            if (!aFileListNeg.endsWith(Conf.FEATURE_EXTENSION)) {
                fileList.add("non-faces/" + aFileListNeg);
            }
        }
        for (String fileListPo : fileListPos) {
            if (!fileListPo.endsWith(Conf.FEATURE_EXTENSION)) {
                fileList.add("faces/" + fileListPo);
            }
        }
        String[] filelistIndex = new String[fileList.size()];
        fileList.toArray(filelistIndex);
        return filelistIndex;
    }
}