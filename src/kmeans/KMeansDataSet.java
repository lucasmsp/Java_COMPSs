/*
 *  Copyright 2002-2015 Barcelona Supercomputing Center (www.bsc.es)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package kmeans;


import java.io.*;
import java.util.Random;


/**
 * A class to encapsulate an input set of 
 * Points for use in the kmeansHDFS program and the
 * reading/writing of a set of points to data files.
 */
public class KMeansDataSet {
    private static final int cookie = 0x2badfdc0;
    private static final int version = 1;
    
    public final int numPoints;
    public final int numDimensions;
    public final float[][] points;
    public final float[] currentCluster;
    
    
    public KMeansDataSet(int np, int nd, float[][] pts, float[] cluster) {
        numPoints = np;
        numDimensions = nd;
        points = pts;
        currentCluster = cluster;
    }
    
    /*public final float getFloat(int point, int dim) {
        return points[point*numDimensions + dim];
    }*/
    
    public final int getPointOffset(int point) {
        return point*numDimensions;
    }
    
    
    /**
     * Create numPoints random points each of dimension numDimensions.
     */
    public static KMeansDataSet generateRandomPoints(int numPoints, int numDimensions, int numFrags, int K) {
        float[][] points = new float[numFrags][];
        float[] cluster = new float[K * numDimensions];
        
        return new KMeansDataSet(numPoints, numDimensions, points, cluster);
    } 
    
    /**
     * Generate a set of random points and write them to a data file
     * @param fileName the name of the file to create
     * @param numPoints the number of points to write to the file
     * @param numDimensions the number of dimensions each point should have
     * @param seed a random number seed to generate the points.
     * @return <code>true</code> on success, <code>false</code> on failure
     */
    public static boolean generateRandomPointsToFile(String fileName, int numPoints, int numDimensions, int seed) {
        try {
            Random rand = new Random(seed);
            File outputFile = new File(fileName);
            DataOutputStream out = new DataOutputStream(new FileOutputStream(outputFile));
            out.writeInt(cookie);
            out.writeInt(version);
            out.writeInt(numPoints);
            out.writeInt(numDimensions);
            int numFloats = numPoints * numDimensions;
            for (int i=0; i<numFloats; i++) {
                out.writeFloat(rand.nextFloat());
            }
            out.close();
        } catch (FileNotFoundException e) {
            System.out.println("Unable to open file for writing "+fileName);
            return false;
        } catch (IOException e) {
            System.out.println("Error writing data to "+fileName);
            e.printStackTrace();
            return false;
        }
                
        return true;
    }
    
    
    /**
     * Write a set of points to a data file
     * @param fileName the name of the file to create
     * @param data the points to write
     * @return <code>true</code> on success, <code>false</code> on failure
     */
/*    public static boolean writePointsToFile(String fileName, KMeansDataSet data) {
        int numPoints = data.numPoints;
        if (numPoints == 0) return false;
        int numDimensions = data.numPoints;
        try {
            File outputFile = new File(fileName);
            DataOutputStream out = new DataOutputStream(new FileOutputStream(outputFile));
            out.writeInt(cookie);
            out.writeInt(version);
            out.writeInt(numPoints);
            out.writeInt(numDimensions);
            for (int i=0; i<numPoints*numDimensions; i++) {
                out.writeFloat(data.points[i]);
            }
        
        } catch (FileNotFoundException e) {
            System.out.println("Unable to open file for writing "+fileName);
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
                
        return true;
    }
*/    
    /**
     * Create numPoints random points each of dimension numDimensions.
     * @param fileName the name of the data file containing the points
     */
    public static KMeansDataSet readPointsFromFile(String fileName,int numPoints,int numDimensions,int numFrags, int K) {
        int i = 0;
        int j = 0;
        int f = 0;

        float[][] points = null;
        float[] cluster = new float[K * numDimensions];

        try {
            BufferedReader lines = new BufferedReader(new FileReader(fileName));
            points = new float[numFrags][numPoints * numDimensions];

            System.out.printf("Reading %d %d-dimensional points from %s\n", numPoints, numDimensions, fileName);
            int pointsPerFragment = numPoints / numFrags;

            for (i = 0; i < numPoints; i++) {
                if ((i != 0) && ((i % pointsPerFragment) == 0))
                    f++;

                String line = lines.readLine();
                String[] tok = line.split(",");
                //System.out.println(line);
                for (j = 1; j < numDimensions; ++j) { // 1 to 27
                   // System.out.println(Float.parseFloat(tok[j]));
                    points[f][i * numDimensions + j - 1] = Float.parseFloat(tok[j]);
                }

                //System.out.println(points[f][i * numDimensions]);


            }
        } catch (FileNotFoundException e) {
            System.err.println("Unable to open file " + fileName);
        } catch (IOException e) {
            System.err.printf("File did not contain enough data for %d %d-dimenstional points\n", numPoints, numDimensions);
            System.err.printf("Only found %d floats; expected to find %d\n", i * numDimensions + j, numPoints * numDimensions);
            e.printStackTrace();
        }

        System.out.println("Terminou de ler o arquivo.");

        return new KMeansDataSet(numPoints, numDimensions, points, cluster);

    }
}
