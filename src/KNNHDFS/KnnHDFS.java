package KNNHDFS;

/**
 * Created by lucasmsp on 07/02/17.
 */

import java.util.ArrayList;


import integration.Block;
import integration.HDFS;



public class KnnHDFS {

    /*
    public static ArrayList<Sample> readFilefromHDFS(HDFS dfs,String inputname) {
        ArrayList<Sample> samples = new ArrayList<Sample>();
        String[] rows = dfs.readALLFiletoString(inputname).split("\n");
        System.out.println("Acabou1");
        for(String line : rows){
            String[] tokens = line.split(",");
            Sample sample = new Sample();
            sample.label = Math.round(Float.parseFloat(tokens[0]) );
            sample.cols = new double[tokens.length - 1];
            for(int i = 1; i < tokens.length; i++) {
                sample.cols[i-1] = Double.parseDouble(tokens[i]);
            }
            System.out.println("Acabou2");
            samples.add(sample);
        }

        return samples;
    }
    */

    public static void ClassifierFromHDFSblock(Block blk, ArrayList<Block> FILE_TRAIN_SPLITS, int[] correct,int K){
        ArrayList<Sample> trainingSet = new ArrayList<Sample>();
        String line_tmp = null;
        //String line = null;
        String lines[] = null;
        Sample sample = new Sample();
        String[] tokens;
        int l =0;
        try{
            for(Block b1 : FILE_TRAIN_SPLITS) { //Usar outro metodo
                lines = b1.getRecords();
                //System.out.println("Pegou os Records");
                //for (String line : lines) {
                for ( l = 0; l<lines.length;l++) {
                    line_tmp = lines[l];
                    if (!line_tmp.equals("")) {
                        tokens = line_tmp.split(",");

                        sample.setLabel(Math.round(Float.parseFloat(tokens[0])));
                        sample.setCols(new double[tokens.length - 1]);
                        for (int i = 1; i < tokens.length; i++)
                            sample.cols[i - 1] = Double.parseDouble(tokens[i]);
                        trainingSet.add(sample);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error: ClassifierFromHDFSblock\n Error - Line "+l+": "+ line_tmp +"\n" );
            System.out.println("File1: "+ line_tmp);
            e.printStackTrace();
        }

        System.out.println("Training file loaded");
        try{
                lines = blk.getRecords();
                for ( l = 0; l<lines.length;l++) {
                    line_tmp = lines[l];

                    if ( (l % 1000) == 0)
                        System.out.println("Index current:" + l);

                    if (!line_tmp.equals("")) {
                        tokens = line_tmp.split(",");

                        sample.setLabel(Math.round(Float.parseFloat(tokens[0])));
                        sample.setCols(new double[tokens.length - 1]);
                        for (int i = 1; i < tokens.length; i++)
                            sample.cols[i - 1] = Double.parseDouble(tokens[i]);

                        int label = classify(trainingSet, sample.getCols(),K);

                        //System.out.println(label + " "+sample.getLabel());

                        if (label == sample.getLabel())
                            correct[0]++;
                        correct[1]++;
                    }
                }

            /*while (blk.HasRecords()) {
                    String[] tokens = blk.getRecord().split(",");
                    Sample sample = new Sample();
                    sample.setLabel(Integer.parseInt(tokens[0]));
                    sample.setCols(new double[tokens.length - 1]);

                    for (int i = 1; i < tokens.length; i++)
                        sample.cols[i-1] = Double.parseDouble(tokens[i]);

                    int label = classify(trainingSet, sample.getCols());
                    if (label == sample.getLabel())
                        correct[0]++;
                    correct[1]++;
            }*/
        } catch (Exception e) {
            System.out.println("File2: "+ line_tmp);
            System.out.println("Error: ClassifierFromHDFSblock\n Error - Line "+l+": "+ line_tmp +"\n" );
            e.printStackTrace();
        }

        System.out.println("Finish task 1");
    }



/*
    //Create
    public static ArrayList<Sample> readblockfromHDFS(Block blk)  {

        ArrayList<Sample> samples = new ArrayList<Sample>();


        while (blk.HasRecords()) {
                String line = blk.getRecord();
                String[] tokens = line.split(",");
                Sample sample = new Sample();
                sample.label = Math.round(Float.parseFloat(tokens[0]) );
                sample.cols = new double[tokens.length - 1];
                for(int i = 1; i < tokens.length; i++)
                    sample.cols[i-1] = Double.parseDouble(tokens[i]);
                samples.add(sample);

        }

        System.out.println("Leu uma parte");

        return samples;
    }


    public static ArrayList<Integer> getResult(ArrayList<Sample> validationSet, ArrayList<Sample> trainingSet){
        ArrayList<Integer> partial_result = new ArrayList<Integer>();

        for(Sample sample:validationSet) {
            int label = classify(trainingSet, sample.cols);
            partial_result.add(label);
        }
        return partial_result;
    }

    public static int[] verify(ArrayList<Sample>part, ArrayList<Integer> partial){
        int correct =0;
        int[] numCorrect = new int[2];
        for (int i = 0; i<part.size();i++)
            if (partial.get(i) == part.get(i).getLabel())
                correct++;

        numCorrect[0]=correct;
        numCorrect[1]=part.size();
        return numCorrect;
    }
    */

    public static double getPopularElement(double[] a){
        int count = 1, tempCount;
        double popular = a[0];
        double temp = 0;
        for (int i = 0; i < (a.length - 2); i++){
            temp = a[i];
            tempCount = 0;
            for (int j = 1; j < (a.length -1); j++){
                if (temp == a[j])
                    tempCount++;
            }
            if (tempCount > count){
                popular = temp;
                count = tempCount;
            }
        }
        return popular;
    }

    public static int distance(double[] a, double[] b) {
        int sum = 0;
        for(int i = 0; i < a.length; i++) {
            sum += (a[i] - b[i]) * (a[i] - b[i]);
        }
        return (int)Math.sqrt(sum); // euclidian distance would be sqrt(sum)...
    }

    public static int  classify(ArrayList<Sample> trainingSet, double[] cols, int K) {
        int label = -1;
        double [][] bestPoints = new double[2][K+1];

        try {
            double tmp_dist = 0;
            double tmp_label = 0;
            //Setting up
            for (int d=0; d<K;d++){
                bestPoints[0][d] = -1.0;
                bestPoints[1][d] = Double.MAX_VALUE;
            }
            for (Sample sample : trainingSet) {
                bestPoints[1][K-1] = distance(sample.cols, cols);
                bestPoints[0][K-1] = sample.label;

                for (int j=K; j>0;j--){ //Insert Sort
                    if(bestPoints[1][j] < bestPoints[1][j-1]){
                        tmp_label = bestPoints[0][j];
                        bestPoints[0][j]    = bestPoints[0][j-1];
                        bestPoints[0][j-1]  = tmp_label;

                        tmp_dist = bestPoints[1][j];
                        bestPoints[1][j]    = bestPoints[1][j-1];
                        bestPoints[1][j-1]  = tmp_dist;
                    }
                }

            }

            label = (int) getPopularElement(bestPoints[0]);

            /*
                for (Sample sample : trainingSet) {
                    int dist = distance(sample.cols, cols);
                    if (dist < bestDistance) {
                        bestDistance = dist;
                        label = sample.label;
                    }
                }
            */
        }catch (Exception e) {
            e.printStackTrace();
        }

        return label;
    }




    public static void main(String[] args) {


        int K = 1;
        String defaultFS = System.getenv("MASTER_HADOOP_URL");
        String trainingSet_name     =  "/user/pdm2/higgs-train-0.1m.csv";
        String validationSet_name   =  "/user/pdm2/higgs-test-0.1m.csv";

        int argIndex = 0;
        // Get and parse arguments
        while (argIndex < args.length) {
            String arg = args[argIndex++];
            if (arg.equals("-t")) {
                trainingSet_name  = args[argIndex++];
            } else if (arg.equals("-v")) {
                validationSet_name  = args[argIndex++];
            } else if (arg.equals("-K")) {
                K = Integer.parseInt(args[argIndex++]);
            }
        }


        HDFS dfs =  new HDFS(defaultFS);
        ArrayList<Block> FILE_TRAIN_SPLITS = dfs.findALLBlocks(trainingSet_name);
        ArrayList<Block> FILE_TEST_SPLITS  = dfs.findALLBlocks(validationSet_name);

        int numFrags = FILE_TEST_SPLITS.size();
        int[][] numcorrect = new int[numFrags][2];

        System.out.println("Running with the following parameters:");
        System.out.println("- Clusters: " + K);
        System.out.println("- Training set: " + trainingSet_name);
        System.out.println("- Test set: " + validationSet_name);
        System.out.println("- Frag Number:" + numFrags);

        //Read the Training Set
       // ArrayList<Sample> trainingSet = new ArrayList<Sample>();
        // for(Block b1 : FILE_TRAIN_SPLITS) {
        //    trainingSet.addAll(readblockfromHDFS(b1));
        //}
        //System.out.println("TrainingSet loaded:" + trainingSet.get(1).getLabel());

        int i = 0;
        //Read the test set and classifier
        for(Block b2 : FILE_TEST_SPLITS){
            //ArrayList<Sample> test = readblockfromHDFS(b2);
            ClassifierFromHDFSblock(b2,FILE_TRAIN_SPLITS, numcorrect[i],K); // getResult(test,trainingSet);
           // numcorrect[i] = verify(test,partial_result);
            i++;

        }

        //Accumulate
        int numCorrect=0;
        int validationSize=0;
        for(int f =0; f<numFrags;f++){
            numCorrect +=numcorrect[f][0];
            validationSize+=numcorrect[f][1];
        }

        //Evaluate
        System.out.println("Accuracy: " + (double) numCorrect / validationSize * 100 + "%");

    }
}