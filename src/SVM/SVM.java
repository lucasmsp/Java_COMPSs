package SVM;

import java.io.*;
import java.util.StringTokenizer;

public class SVM {




    public static double lambda;
    public static double lr = 0.001;
    public static double threshold = 0.001;


    public SVM (double paramLambda){
        lambda = paramLambda;
    }

    public static void  calc_cost(double[] yp, double[][] X, double[] y, double[] w,double [] COST, int numDim ){
        //double [] COST = new double[1];

        for(int m=0;m<X.length;m++){
            //yp[m]=0;
            for(int d=0;d<numDim;d++)
                yp[m] += X[m][d] * w[d];

            if(y[m]*yp[m]-1<0)
                COST[0] += (1-y[m]*yp[m]);

        }
    }

    public static double accumulate(double[][] COST,  int numFrag,int numDim, double[] w){
        double cost = 0;
        for (int f=1;f<numFrag;f++)
            cost+= COST[f][0];

        //Serial
        for(int d=0;d<numDim;d++)
            cost += 0.5*lambda*w[d]*w[d];

        return cost; // to prevent any aliasing with the task parameters
    }

    public double CostAndGrad(double[][][] X, double[][] y,double[] w, double[] grad,
                              double[][]  yp, int numDim, int sizeTrainPerFrag,int numFrag){

        double cost =0;
        double [][] COST = new double[numFrag][1];

        //Parallel
        for (int f=0;f<numFrag;f++) {
            calc_cost(yp[f], X[f], y[f], w, COST[f], numDim);
        }

        //Syncronization
        cost =  accumulate(COST,numFrag,numDim,w);


        //Paralelizavel
        for(int d=0;d<numDim;d++){
            grad[d] = Math.abs(lambda*w[d]);
            for (int f=0;f<numFrag;f++)
                for(int m=0;m<sizeTrainPerFrag;m++)
                    if(y[f][m]*yp[f][m]-1<0)
                        grad[d]-= y[f][m]*X[f][m][d];
        }



        return cost;
    }

    public void update(double[] w,double[] grad,int numDim){
        for(int d=0;d<numDim;d++){
            w[d] -= lr*grad[d];
        }
    }

    public void Train(double[][][] X,double[][] y,int numDim, int sizeTrain,int sizeTrainPerFrag, int numFrag, int maxIters){

        if(sizeTrain <=0){
            System.out.println("num of example <=0!");
            return;
        }

        double[] grad = new double[numDim];
        double[][] yp = new double[numFrag][sizeTrainPerFrag];
        double[]    w = new double[numDim];
        double   cost = 0;

        for(int iter=0;iter<maxIters;iter++){
            cost = CostAndGrad(X,y,w,grad,yp,numDim, sizeTrainPerFrag,numFrag);
            System.out.println("cost:"+cost);
            if(cost< threshold){
                break;
            }
            update(w,grad,numDim);
        }
    }
    private double predict(double[] x,double[] w){
        double pre=0;
        for(int j=0;j<x.length;j++){
            pre+=x[j]*w[j];
        }
        if(pre >=0)
            return  1.0;
        else return 0.0;
    }

    public void Test(double[][] testX,double[] testY,double[] w){
        int error=0;

        //Paralelizavel
        for(int i=0;i<testX.length;i++)	{
            if(predict(testX[i],w) != testY[i]){
                error++;
            }
        }
        System.out.println("total:"+testX.length);
        System.out.println("error:"+error);
        System.out.println("error rate:"+((double)error/testX.length));
        System.out.println("acc rate:"+((double)(testX.length-error)/testX.length));
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

    public static void loadfile_and_split(double[][][] features, double[][] labels, String trainFile,int numFrag,int sizeTrainPerFrag) {

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
        int sizeTest  = 10000;
        int numFrag = 4;

        int sizeTrainPerFrag = (int) Math.floor((float)sizeTrain/numFrag);

        double[][] train_labels = new double[numFrag][sizeTrainPerFrag];
        double[][][] train_features = new double[numFrag][sizeTrainPerFrag][numDim];
        String trainFile = "/media/lucasmsp/Dados/TEMP/Dataset/higgs-train-0.001m.csv";

        loadfile_and_split(train_features,train_labels,trainFile, numFrag, sizeTrainPerFrag);


        SVM svm = new SVM(0.0001);

        svm.Train(train_features,train_labels,numDim,sizeTrain,sizeTrainPerFrag,numFrag,3);
 /*
        double[] test_y = new double[sizeTest];
        double[][] test_X = new double[sizeTest][numDim];
        String testFile = "/media/lucasmsp/Dados/TEMP/Dataset/higgs-train-0.01m.csv";
        loadfile(test_X,test_y,testFile);

        svm.Test(test_X, test_y);
*/
    }

}
