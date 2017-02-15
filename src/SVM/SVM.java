package SVM;

import java.io.*;
import java.util.StringTokenizer;

public class SVM {


    public double[] Train(double[][][] X, double[][] y,int numDim, int sizeTrain,int sizeTrainPerFrag,
                                 int numFrag,  double lambda, double threshold, double lr, int maxIters){

        if(sizeTrain <=0){
            System.out.println("num of example <=0!");
            return null;
        }

        double[] grad = new double[numDim];
        double   cost = 0;
        double[] w =  new double[numDim];

        for(int iter=0;iter<maxIters;iter++){
            double[][] yp = new double[numFrag][sizeTrainPerFrag];

            for (int f=0;f<numFrag;f++)
                yp[f] = calc_cost( X[f], y[f], w, numDim);


            calc_grad(sizeTrainPerFrag,  numDim,  lambda,  numFrag, w, yp, grad, X, y);
            cost = calc_cost2( numFrag, sizeTrainPerFrag, numDim,  w,  y,  yp, lambda);

            System.out.println("[INFO] - Current cost: "+cost);
            if(cost< threshold){
                break;
            }
            update(w,grad,lr, numDim);
        }

        return w;
    }

    public static void calc_grad(int sizeTrainPerFrag, int numDim, double lambda, double numFrag,
                                   double[] w, double [][] yp, double[] grad,double[][][] X,double[][] y){

        for(int d=0;d<numDim;d++){
            grad[d] = Math.abs(lambda*w[d]);
            for (int f=0;f<numFrag;f++)
                for(int m=0;m<sizeTrainPerFrag;m++)
                    if(y[f][m]*yp[f][m]-1<0)
                        grad[d]-= y[f][m]*X[f][m][d];
        }
    }



    public static double[]  calc_cost( double[][] X, double[] label, double[] wp, int numDim ){
        //double [] COST = new double[1];
        double[] ypp = new double[X.length];

        for(int m=0;m<X.length;m++){
            //yp[m]=0;
            for(int d=0;d<numDim;d++)
                ypp[m] += X[m][d] * wp[d];
        }
        return ypp;
    }

    public static double calc_cost2(int numFrag, int sizeTrainPerFrag, int numDim, double[] w,
                                    double[][] label, double [][] yp, double lambda){
        double cost = 0;

        for (int f=0;f<numFrag;f++)
            for(int m=0;m<sizeTrainPerFrag;m++) {
                if (label[f][m] * yp[f][m] - 1 < 0)
                    cost += (1 - label[f][m] * yp[f][m]);
            }

        //Serial
        for(int d=0;d<numDim;d++)
            cost += 0.5*lambda*w[d]*w[d];

        return cost;

    }

    public static void update(double[] w, double[] grad, double lr,int numDim){
        for(int d=0;d<numDim;d++){
            w[d] -= lr*grad[d];
        }
    }



    public static void Test(int sizeTestPerFrag,int numDim,int numFrag,
                            int sizeTest, double[][][] testX, double[][] testY, double[] w){
        int error=0;
        double[][] labels_result = new double[numFrag][sizeTest];

        //Parallel
        for (int f =0; f<numFrag;f++)
            labels_result[f] = predict_chunck(testX[f], testY[f], w, sizeTestPerFrag);

        //Evaluate
        for (int f =0; f<numFrag;f++)
            for(int i=0;i<sizeTestPerFrag;i++)	{
                if(labels_result[f][i]!=testY[f][i])
                    error++;
            }

        System.out.println("Total Length:"+testX.length);
        System.out.println("Error:"+error);
        System.out.println("Error rate:"+((double)error/testX.length));
        System.out.println("Acc rate:"+((double)(testX.length-error)/testX.length));
    }

    public static double[] predict_chunck(double[][] testX, double[] testY, double[] w,  int sizeTestPerFrag){
        double[] label_result = new  double[sizeTestPerFrag];
        for(int i=0;i<sizeTestPerFrag;i++)	{
            label_result[i] = predict(testX[i], w);
        }

        return label_result;
    }

    public static double predict(double[] x, double[] w){
        double pre=0;
        for(int j=0;j<x.length;j++){
            pre+=x[j]*w[j];
        }
        if(pre >=0)
            return  1.0;
        else return 0.0;
    }

    /*
    public static void loadfile(double[][] features, double[] labels, String trainFile) throws IOException {

        File file = new File(trainFile);
        RandomAccessFile raf = new RandomAccessFile(file,"r");
        StringTokenizer tokenizer;

        int index=0;
        while(true){
            String line = raf.readLine();

            if(line == null)
                break;

            tokenizer = new StringTokenizer(line,",");
            labels[index] = Double.parseDouble(tokenizer.nextToken());
            //System.out.println(y[index]);
            int index2=0;
            while(tokenizer.hasMoreTokens()){
                features[index][index2] = Double.parseDouble(tokenizer.nextToken());
                index2++;
            }
            //features[index][0] =1;
            index++;
        }
    }
*/

    public static void loadfile_and_split(double[][][] features, double[][] labels, String trainFile,int numFrag, int sizeTrainPerFrag) {

        //labels = new double[numFrag][sizeTrainPerFrag];
        //features = new double[numFrag][sizeTrainPerFrag][28];//HARD
        File file = new File(trainFile);
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(file,"r");

            StringTokenizer tokenizer;

            int index=0;
            int readed = 0;
            int f = -1;
            while(true){
                String line = raf.readLine();

                if(line == null)
                    break;

                if ((index % sizeTrainPerFrag) == 0){
                    f++;
                    index=0;
                }

                tokenizer = new StringTokenizer(line,",");
                labels[f][index] = Double.parseDouble(tokenizer.nextToken());
                //System.out.println(y[index]);
                int index2=0;
                while(tokenizer.hasMoreTokens()){
                    features[f][index][index2] = Double.parseDouble(tokenizer.nextToken());
                    index2++;
                }
                //features[index][0] =1;
                index++;
                readed++;


            }

           // while (readed<sizeTrain){

                //PREENCHER O RESTANTE COM 1
              //  readed++;
           // }





        } catch (FileNotFoundException e) {
            System.out.println("ERROR - SVM.loadfile_and_split");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("ERROR - SVM.loadfile_and_split");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException
    {

        int numDim = 28;
        int sizeTrain = 1000;
        int sizeTest  = 1000;
        int numFrag = 4;

        int sizeTrainPerFrag = (int) Math.floor((float)sizeTrain/numFrag);

        double[][] train_labels = new double[numFrag][sizeTrainPerFrag];
        double[][][] train_features = new double[numFrag][sizeTrainPerFrag][numDim];
        String trainFile = "/media/lucasmsp/Dados/TEMP/Dataset/higgs-train-0.001m.csv";

        loadfile_and_split(train_features,train_labels,trainFile, numFrag, sizeTrainPerFrag);


        SVM svm = new SVM();

        double lambda = 0.0001;
        double lr = 0.001;
        double threshold = 0.001;
        double[]    w = svm.Train(train_features,train_labels,numDim,sizeTrain,
                                  sizeTrainPerFrag,numFrag,lambda,threshold,lr,3);


        int sizeTestPerFrag = (int) Math.floor((float)sizeTest/numFrag);
        double[][] test_labels = new double[sizeTest][sizeTestPerFrag];
        double[][][] test_features = new double[sizeTest][sizeTestPerFrag][numDim];
        String testFile = "/media/lucasmsp/Dados/TEMP/Dataset/higgs-train-0.001m.csv";

        loadfile_and_split(test_features,test_labels,testFile,numFrag,sizeTestPerFrag);

        svm.Test(sizeTestPerFrag,numDim,numFrag,sizeTest,test_features, test_labels,w);

    }

}
