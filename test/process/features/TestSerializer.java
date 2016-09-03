package process.features;

import org.junit.Test;
import process.Conf;
import process.DecisionStump;
import utils.Serializer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static process.features.FeatureExtractor.computeImageFeatures;
import static utils.Serializer.*;


public class TestSerializer {

    @Test
    public void writeReadArray() {
        String filePath = Conf.TEST_DIR + "/writeReadArray.data";
        ArrayList<Integer> r1 = new ArrayList<>();
        r1.add(1);
        r1.add(2);
        r1.add(3);
        r1.add(5);
        r1.add(7);
        appendArrayToDisk(filePath, r1);
        ArrayList<Integer> r2 = new ArrayList<>();
        r2.add(8);
        r2.add(9);
        r2.add(1);
        r2.add(6);
        r2.add(10);
        appendArrayToDisk(filePath, r2);
        ArrayList<Integer> result = readArrayFromDisk(filePath);
        assertEquals(result.get(0), new Integer(1));
        assertEquals(result.get(1), new Integer(2));
        assertEquals(result.get(2), new Integer(3));
        assertEquals(result.get(3), new Integer(5));
        assertEquals(result.get(4), new Integer(7));
        assertEquals(result.get(5), new Integer(8));
        assertEquals(result.get(6), new Integer(9));
        assertEquals(result.get(7), new Integer(1));
        assertEquals(result.get(8), new Integer(6));
        assertEquals(result.get(9), new Integer(10));
        assertEquals(readIntFromDisk(filePath, 8), 6);

        try {
            Files.delete(Paths.get(filePath));
        } catch (IOException e) {
            assertEquals(false, true);
        }

        writeArrayToDisk(filePath, result);
        result = readArrayFromDisk(filePath);
        assertEquals(result.get(0), new Integer(1));
        assertEquals(result.get(1), new Integer(2));
        assertEquals(result.get(2), new Integer(3));
        assertEquals(result.get(3), new Integer(5));
        assertEquals(result.get(4), new Integer(7));
        assertEquals(result.get(5), new Integer(8));
        assertEquals(result.get(6), new Integer(9));
        assertEquals(result.get(7), new Integer(1));
        assertEquals(result.get(8), new Integer(6));
        assertEquals(result.get(9), new Integer(10));
        assertEquals(readIntFromDisk(filePath, 8), 6);

        result = readArrayFromDisk(filePath, 3, 6);
        assertEquals(result.size(), 3);
        assertEquals(result.get(0), new Integer(5));
        assertEquals(result.get(1), new Integer(7));
        assertEquals(result.get(2), new Integer(8));
        //assertEquals(result.get(3), new Integer(9));

        try {
            Files.delete(Paths.get(filePath));
        } catch (IOException e) {
            assertEquals(false, true);
        }
    }

    @Test
    public void computeWriteAndRead() {
        if (Conf.USE_CUDA)
            Conf.haarExtractor.setUp(19, 19);
        String img = "data/trainset/faces/face00001.png";
        String haar = img + Conf.FEATURE_EXTENSION;

        if (Files.exists(Paths.get(haar))) {
            try {
                Files.delete(Paths.get(haar));
            } catch (IOException e) {
                e.printStackTrace();
                assertEquals(false, true);
            }
        }

        ArrayList<Integer> correctValues = computeImageFeatures(img, true);
        ArrayList<Integer> writtenValues = readArrayFromDisk(haar);

        assertEquals(writtenValues.size(), correctValues.size());

        for (int i = 0; i < correctValues.size(); i++)
            assertEquals(writtenValues.get(i), correctValues.get(i));
    }

    @Test
    public void printRuleTest() {
        String tmp_file = "tmp/test/featuresValues.data";

        ArrayList<DecisionStump> committee = new ArrayList<>();

        DecisionStump decisionStump = new DecisionStump(1, 1, 1, 1, 1);
        committee.add(decisionStump);

        Serializer.writeRule(committee, true, tmp_file);

        DecisionStump decisionStump2 = new DecisionStump(2, 2, 2, 2, -1);
        committee.add(decisionStump2);

        Serializer.writeRule(committee, false, tmp_file);

        ArrayList<DecisionStump> read = Serializer.readRule(tmp_file);

        assertEquals(committee.size() + 1, read.size());

        assertEquals(committee.get(0).error, read.get(0).error);
        assertEquals(committee.get(0).featureIndex, read.get(0).featureIndex);
        assertEquals(committee.get(0).threshold, read.get(0).threshold);
        assertEquals(committee.get(0).toggle, read.get(0).toggle);

        assertEquals(committee.get(0).error, read.get(1).error);
        assertEquals(committee.get(0).featureIndex, read.get(1).featureIndex);
        assertEquals(committee.get(0).threshold, read.get(1).threshold);
        assertEquals(committee.get(0).toggle, read.get(1).toggle);

        assertEquals(committee.get(1).error, read.get(2).error);
        assertEquals(committee.get(1).featureIndex, read.get(2).featureIndex);
        assertEquals(committee.get(1).threshold, read.get(2).threshold);
        assertEquals(committee.get(1).toggle, read.get(2).toggle);
    }
}
