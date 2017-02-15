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
package kmeansHDFS;

import integration.Block;
import integration.HDFS;

import java.util.ArrayList;


/**
 * A class to encapsulate an input set of 
 * Points for use in the kmeansHDFS program and the
 * reading/writing of a set of points to data files.
 */
public class KMeansDataSet {

    private static int nFrag;
    public final int numPoints;
    public final int numDimensions;
    public final float[][] points;
    public final float[] currentCluster;


    
    public KMeansDataSet(int np, int nd, float[][] pts, float[] cluster) {
        numPoints = np;
        numDimensions = nd;
        points = pts;
        currentCluster = cluster;
        nFrag = 1;
    }

    public int getFrag(){
        return nFrag;
    }

    public static void setFrag(int numFrag){
        nFrag = numFrag;
    }


    public static KMeansDataSet readPointsFromHDFS(String fileName, int numPoints, int numDimensions, int K) {

        String defaultFS = System.getenv("MASTER_HADOOP_URL");  // local of the HDFS's master node -> you need to set
                                                                // a env variable with the path of master node

        float[][] points = null;
        float[] cluster = new float[K * numDimensions];

        HDFS dfs =  new HDFS(defaultFS);

        ArrayList<Block> HDFS_SPLITS_LIST = dfs.findALLBlocks(fileName);
        int numFrags = HDFS_SPLITS_LIST.size();
        setFrag(numFrags);

        points = new float[numFrags][numPoints * numDimensions];

        int frag = 0;
        for(Block b : HDFS_SPLITS_LIST) {
           float[] point = read(b,numDimensions);
           points = mergeResults(point, points,frag++);
        }

        return new KMeansDataSet(numPoints, numDimensions, points, cluster);

    }

    public static float[] read(Block blk,int numDimensions){

        ArrayList<float[]> tmp= new ArrayList<float[]>();

        while (blk.HasRecords()) {
            float[] row = new float[numDimensions];
            String[] col = blk.getRecord().split(",");
            for (int j = 1; j < numDimensions; ++j) { // 1 to 27
                row[j - 1] = Float.parseFloat(col[j]);
            }
            tmp.add(row);
        }

        float[] points = new float[tmp.size()*numDimensions];
        for (int j = 0; j<tmp.size();j++) {
            float [] f = tmp.get(j);
            for (int i=0; i<f.length;i++)
                points[j*numDimensions + i] = f[i];
        }
        return points;
    }

    public static float[][] mergeResults(float[] point,float[][] points,int frag){
        points[frag] = point.clone();
        return points;
    }
    
    public final int getPointOffset(int point) {
        return point*numDimensions;
    }


}
