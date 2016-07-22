package process.features;

import GUI.ImageHandler;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;

public class TestFeatureExtractor {
    @Test
    public void featuresChecker() {
        int[][] tmp = {{0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}};
        ImageHandler image = new ImageHandler(tmp, 4, 4);

        assertEquals(40, FeatureExtractor.listAllTypeA(image).size()); // 2*1 -> 40
        assertEquals(20, FeatureExtractor.listAllTypeB(image).size()); // 3*1 -> 20
        assertEquals(40, FeatureExtractor.listAllTypeC(image).size()); // 1*2 -> 40
        assertEquals(20, FeatureExtractor.listAllTypeD(image).size()); // 1*3 -> 20
        assertEquals(16, FeatureExtractor.listAllTypeE(image).size()); // 2*2 -> 16

        ArrayList<Feature> tmp_lf = new ArrayList<>();

        FeatureExtractor.streamAllTypeA(image).iterator().forEachRemaining(tmp_lf::add);
        assertEquals(40, tmp_lf.size()); // 2*1 -> 40

        tmp_lf.clear();

        FeatureExtractor.streamAllTypeB(image).iterator().forEachRemaining(tmp_lf::add);
        assertEquals(20, tmp_lf.size()); // 3*1 -> 20

        tmp_lf.clear();

        FeatureExtractor.streamAllTypeC(image).iterator().forEachRemaining(tmp_lf::add);
        assertEquals(40, tmp_lf.size()); // 1*2 -> 40

        tmp_lf.clear();

        FeatureExtractor.streamAllTypeD(image).iterator().forEachRemaining(tmp_lf::add);
        assertEquals(20, tmp_lf.size()); // 1*3 -> 20

        tmp_lf.clear();

        FeatureExtractor.streamAllTypeE(image).iterator().forEachRemaining(tmp_lf::add);
        assertEquals(16, tmp_lf.size()); // 2*2 -> 16
    }

    @Test
    public void countFeaturesTest() {
        // Manually count: http://stackoverflow.com/a/1711158/3157230

        assertEquals(136L, FeatureExtractor.countAllFeatures(4, 4));
        assertEquals(162336L, FeatureExtractor.countAllFeatures(24, 24));
        assertEquals(29979041500L, FeatureExtractor.countAllFeatures(500, 500));
        assertEquals(8+6+4+2 + 9+3+6+2+3+1 + 4+3+2+1 + 6+4+2 + 6+2, FeatureExtractor.countAllFeatures(4, 3));
    }
}