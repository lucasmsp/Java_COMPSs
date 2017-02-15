package SVM;

import integratedtoolkit.types.annotations.Parameter;
import integratedtoolkit.types.annotations.task.Method;




/**
 * Created by lucasmsp on 13/02/17.
 */
public interface SVMItf {

    @Method(declaringClass = "SVM.SVM")
    void calc_cost(
            @Parameter(direction = Parameter.Direction.INOUT)
                    double[] yp,
            @Parameter(direction = Parameter.Direction.IN)
                    double[][] X,
            @Parameter(direction = Parameter.Direction.IN)
                    double[] y,
            @Parameter(direction = Parameter.Direction.IN)
                    double[] w,
            @Parameter(direction = Parameter.Direction.INOUT)
                    double[] COST,
            @Parameter(direction = Parameter.Direction.IN)
                    int numDim
    );


    @Method(declaringClass = "SVM.SVM")
    double accumulate(
            @Parameter(direction = Parameter.Direction.IN)
                    double[][] COST,
            @Parameter(direction = Parameter.Direction.IN)
                    int numFrag,
            @Parameter(direction = Parameter.Direction.IN)
                    int numDim,
            @Parameter(direction = Parameter.Direction.IN)
                    double[] w
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
