package io.github.alexbashchuk.base;

import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.global.opencv_core;
//import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
//import static org.bytedeco.opencv.global.opencv_core.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class ImageToTemplateMatcher {

    static {
        Loader.load(opencv_core.class); // loads native OpenCV (Bytedeco)
    }

    private final Path templatePath;
    private final Path screenshotPath;
    private final int thresholdPercent;

    public ImageToTemplateMatcher(String templatePath, String screenshotPath, int thresholdPercent) {
        this.templatePath = Path.of(Objects.requireNonNull(templatePath));
        this.screenshotPath = Path.of(Objects.requireNonNull(screenshotPath));
        if (thresholdPercent < 0 || thresholdPercent > 100) {
            throw new IllegalArgumentException("thresholdPercent must be in range 0..100");
        }
        this.thresholdPercent = thresholdPercent;
    }

    private static void validateFile(Path path, String argName) {
        if (!Files.exists(path)) throw new IllegalArgumentException(argName + " does not exist: " + path);
        if (!Files.isRegularFile(path)) throw new IllegalArgumentException(argName + " is not a file: " + path);
    }

    public boolean matches() {
        validateFile(templatePath, "templatePath");
        validateFile(screenshotPath, "screenshotPath");

        Mat screenshot = opencv_imgcodecs.imread(screenshotPath.toString(), opencv_imgcodecs.IMREAD_COLOR);
        Mat template = opencv_imgcodecs.imread(templatePath.toString(), opencv_imgcodecs.IMREAD_COLOR);

        if (screenshot.empty()) throw new IllegalStateException("Failed to read screenshot image: " + screenshotPath);
        if (template.empty()) throw new IllegalStateException("Failed to read template image: " + templatePath);

        if (template.cols() > screenshot.cols() || template.rows() > screenshot.rows()) return false;

        Mat screenshotGray = new Mat();
        Mat templateGray = new Mat();
        opencv_imgproc.cvtColor(screenshot, screenshotGray, opencv_imgproc.COLOR_BGR2GRAY);
        opencv_imgproc.cvtColor(template, templateGray, opencv_imgproc.COLOR_BGR2GRAY);

        int resultCols = screenshotGray.cols() - templateGray.cols() + 1;
        int resultRows = screenshotGray.rows() - templateGray.rows() + 1;
        Mat result = new Mat(resultRows, resultCols, opencv_core.CV_32FC1);

        opencv_imgproc.matchTemplate(
                screenshotGray,
                templateGray,
                result,
                opencv_imgproc.TM_CCOEFF_NORMED
        );

        DoublePointer minVal = new DoublePointer(1);
        DoublePointer maxVal = new DoublePointer(1);
        org.bytedeco.opencv.opencv_core.Point minLoc = new org.bytedeco.opencv.opencv_core.Point();
        org.bytedeco.opencv.opencv_core.Point maxLoc = new org.bytedeco.opencv.opencv_core.Point();
        opencv_core.minMaxLoc(result, minVal, maxVal, minLoc, maxLoc, null);
        double bestScore = maxVal.get();
        return bestScore >= (thresholdPercent / 100.0);
    }
}