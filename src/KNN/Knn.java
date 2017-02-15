package KNN;

/**
 * Created by lucasmsp on 07/02/17.
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Knn {

    //Create
    public static ArrayList<Sample> readFile(String file) throws IOException {
        ArrayList<Sample> samples = new ArrayList<Sample>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        try {
            String line = null; //reader.readLine(); // ignore header
            while((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                Sample sample = new Sample();
                sample.label = Math.round(Float.parseFloat(tokens[0]) );
                sample.cols = new double[tokens.length - 1];
                for(int i = 1; i < tokens.length; i++) {
                    sample.cols[i-1] = Double.parseDouble(tokens[i]);
                }
                samples.add(sample);
            }
        } finally { reader.close(); }



        return samples;
    }

    public static int distance(double[] a, double[] b) {
        int sum = 0;
        for(int i = 0; i < a.length; i++) {
            sum += (a[i] - b[i]) * (a[i] - b[i]);
        }
        return (int)Math.sqrt(sum); // euclidian distance would be sqrt(sum)...
    }

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

    public static int  classify(ArrayList<Sample> trainingSet, double[] cols, int K) {
        int label = -1;
        double [][] bestPoints = new double[2][K+1];
        //Setting up
        for (int d=0; d<K;d++){
            bestPoints[0][d] = -1.0;
            bestPoints[1][d] = Double.MAX_VALUE;
        }
        try {
            double tmp_dist = 0;
            double tmp_label = 0;
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

        }catch (Exception e) {
            e.printStackTrace();
        }

        return label;
    }


    public static void getResult(ArrayList<Sample> validationSet, ArrayList<Sample> trainingSet, int[] partial, int K){
        int i=0;
        for(Sample sample:validationSet) {
            int label = classify(trainingSet, sample.cols,K);
            partial[i] = label;
            i++;
        }

    }

    public static  void verify( ArrayList<Sample>part,int[] partial, int[] correct){

        for (int i = 0; i<part.size();i++) {
            //System.out.println(partial[i] + " "+part.get(i).getLabel());
            if (partial[i] == part.get(i).getLabel())
                correct[i] = 1;

        }


    }

    public static void main(String[] args) throws IOException {


        int frag = 4;
        int K = 1;
        String trainingName = "/media/lucasmsp/Dados/TEMP/Dataset/higgs-train-0.001m.csv";
        String testName = "/media/lucasmsp/Dados/TEMP/Dataset/higgs-train-0.0001m.csv";

        // Get and parse arguments
        int argIndex = 0;
        while (argIndex < args.length) {
            String arg = args[argIndex++];
            if (arg.equals("-K")) {
                K = Integer.parseInt(args[argIndex++]);
            } else if (arg.equals("-f")) {
                frag = Integer.parseInt(args[argIndex++]);
            }
        }

        System.out.println("Running with the following parameters:");
        System.out.println("- Clusters: " + K);
        System.out.println("- Nodes: " + frag);
        System.out.println("- Training set: " + trainingName);
        System.out.println("- Test set: " + testName);

        ArrayList<Sample> trainingSet   = readFile(trainingName);
        ArrayList<Sample> validationSet = readFile(testName);


        int start = 0;
        int end = validationSet.size()/frag;
        int[][] result = new int[frag][validationSet.size()/frag];

        //Split
        ArrayList<ArrayList<Sample>> data_frag = new ArrayList<ArrayList<Sample>>();
        for (int f = 0 ; f<frag;f++) {
            ArrayList<Sample> part = new ArrayList<Sample>(validationSet.subList(start, end));
            start = start + validationSet.size()/frag;
            end = end + validationSet.size()/frag;
            data_frag.add(part);
        }


        //Classifier
        int[][] numcorret = new int[frag][validationSet.size()/frag];
        int i =0;
        for (ArrayList<Sample> part : data_frag){
            getResult(part,trainingSet,result[i],K);
            verify(part,result[i],numcorret[i]);
            i++;
        }

        //Evaluate
        int numCorrect = 0;
        for (int j = 0; j <frag; j++)
            for (int k = 0; k <numcorret.length; k++)
                numCorrect+=numcorret[j][k];

        System.out.println("Accuracy: " + (double) numCorrect / validationSet.size() * 100 + "%");
    }
}