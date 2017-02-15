package SVM;

import integratedtoolkit.types.annotations.Parameter;
import integratedtoolkit.types.annotations.task.Method;




/**
 * Created by lucasmsp on 13/02/17.
 */
public interface SVMItf {

    @Method(declaringClass = "SVM.SVM", isModifier=true, priority=true)
    double[] calc_cost(
            @Parameter(type = Parameter.Type.OBJECT,  direction = Parameter.Direction.IN)
                    double[][] X,
            @Parameter(type = Parameter.Type.OBJECT, direction = Parameter.Direction.IN)
                    double[] y,
            @Parameter(direction = Parameter.Direction.IN)
                    double[] w,
            @Parameter(direction = Parameter.Direction.IN)
                    int numDim
    );

    @Method(declaringClass = "SVM.SVM")
    double[] predict_chunck(
            @Parameter(type = Parameter.Type.OBJECT,  direction = Parameter.Direction.IN) double[][] testX,
            @Parameter(type = Parameter.Type.OBJECT,  direction = Parameter.Direction.IN) double[] testY,
            @Parameter(type = Parameter.Type.OBJECT,  direction = Parameter.Direction.IN) double[] w,
            @Parameter(direction = Parameter.Direction.IN) int sizeTestPerFrag
    );


/*


       @Method(declaringClass = "SVM.SVM")
    double predict(
            @Parameter(type = Parameter.Type.OBJECT, direction = Parameter.Direction.IN) double[] x,
            @Parameter(type = Parameter.Type.OBJECT, direction = Parameter.Direction.IN) double[] w

    );

    @Method(declaringClass = "SVM.SVM")
    double calc_cost2(
            @Parameter(direction = Parameter.Direction.IN) int numFrag,
            @Parameter(direction = Parameter.Direction.IN) int sizeTrainPerFrag,
            @Parameter(direction = Parameter.Direction.IN) int numDim,
            @Parameter(direction = Parameter.Direction.IN) double[] w,
            @Parameter(direction = Parameter.Direction.IN) double[][] label,
            @Parameter(direction = Parameter.Direction.IN) double [][] yp,
            @Parameter(direction = Parameter.Direction.IN) double lambda
    );



    @Method(declaringClass = "SVM.SVM")
    void calc_grad(
            @Parameter(direction = Parameter.Direction.IN) int sizeTrainPerFrag,
            @Parameter(direction = Parameter.Direction.IN) int numDim,
            @Parameter(direction = Parameter.Direction.IN) double lambda,
            @Parameter(direction = Parameter.Direction.IN) double numFrag,
            @Parameter(direction = Parameter.Direction.IN) double[] w,
            @Parameter(direction = Parameter.Direction.IN) double [][] yp,
            @Parameter(direction = Parameter.Direction.INOUT) double[] grad,
            @Parameter(direction = Parameter.Direction.IN) double[][][] X,
            @Parameter(direction = Parameter.Direction.IN) double[][] y
    );









/*

    @Method(declaringClass = "SVM.SVM")
    void loadfile_and_split(
            @Parameter(direction = Parameter.Direction.INOUT)
                double[][][] features,
            @Parameter(direction = Parameter.Direction.INOUT)
                double[][] labels,
            @Parameter(direction = Parameter.Direction.IN)
                String trainFile,
            @Parameter(direction = Parameter.Direction.IN)
                int numFrag,
            @Parameter(direction = Parameter.Direction.IN)
                int sizeTrainPerFrag
    );
*/





}
