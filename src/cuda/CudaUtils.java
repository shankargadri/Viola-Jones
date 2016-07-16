package cuda;

import jcuda.driver.CUcontext;
import jcuda.driver.CUdevice;
import jcuda.driver.CUmodule;
import jcuda.driver.JCudaDriver;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static jcuda.driver.JCudaDriver.*;

public class CudaUtils {

    public final static String PATH_TO_SRC = "cuda/src/";
    public final static String PATH_TO_PTX = "cuda/ptx/";

    // Return the name of ptx file compiled
    // Arg cudaFilename should have no extension
    public static String compileCuda(String fileName) throws IOException {
        String ptxFileName = PATH_TO_PTX + fileName + ".ptx";
        String cudaFileName = PATH_TO_SRC + fileName + ".cu";
        File ptxFile = new File(ptxFileName);
        File cudaFile = new File(cudaFileName);

        if (!cudaFile.exists())
            throw new IOException("Input file not found: " + cudaFileName);

        // No need to compile cuda, it has already been done
        if (ptxFile.exists() && cudaFile.lastModified() <= ptxFile.lastModified())
            return ptxFileName;
        else {
            File ptxDir = new File(PATH_TO_PTX);
            if (!ptxDir.exists()) {
                System.out.println("Creating output directory :" + PATH_TO_PTX);
                ptxDir.mkdirs();
            }
        }

        String modelString = "-m" + System.getProperty("sun.arch.data.model");
        String command = "nvcc " + modelString + " -ptx " + cudaFileName + " -o " + ptxFileName;

        System.out.println("Executing : '" + command + "'");
        Process process = Runtime.getRuntime().exec(command);

        String errorMessage = new String(toByteArray(process.getErrorStream()));
        String outputMessage = new String(toByteArray(process.getInputStream()));
        int exitValue = 0;

        try {
            exitValue = process.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while waiting for nvcc output", e);
        }

        if (exitValue != 0) {
            System.err.println("nvcc process exitValue : " + exitValue);
            System.err.println("errorMessage : " + System.lineSeparator() + errorMessage);
            System.err.println("outputMessage : " + System.lineSeparator() + outputMessage);
            throw new IOException("Could not create .ptx file: " + errorMessage);
        }

        System.out.println("Finished creating PTX file");
        return ptxFileName;
    }

    // To read the execution of a the Cuda compiler
    private static byte[] toByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte buffer[] = new byte[8192];
        while (true) {
            int read = inputStream.read(buffer);
            if (read == -1)
                break;
            baos.write(buffer, 0, read);
        }
        return baos.toByteArray();
    }

    public static CUmodule initCuda(String cudaFilename) {

        JCudaDriver.setExceptionsEnabled(true);

        // Create the PTX file by calling the NVCC
        String ptxFileName;
        try {
            ptxFileName = CudaUtils.compileCuda(cudaFilename);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        // Initialize the driver and create a context for the first device.
        cuInit(0);
        CUcontext pctx = new CUcontext();
        CUdevice dev = new CUdevice();
        cuDeviceGet(dev, 0);
        cuCtxCreate(pctx, 0, dev);

        // Load the file containing the kernels
        CUmodule module = new CUmodule();
        cuModuleLoad(module, ptxFileName);

        return module;
    }

}
