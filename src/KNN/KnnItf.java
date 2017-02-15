package KNN;


import integratedtoolkit.types.annotations.Parameter;
import integratedtoolkit.types.annotations.task.Method;


import java.util.ArrayList;
import java.util.List;


public interface KnnItf {

    /*
    @Method(declaringClass = "KNN.Knn")
    public int classify(
            @Parameter (type = Parameter.Type.OBJECT, direction = Parameter.Direction.IN)
                    ArrayList<Sample> trainingSet,
            @Parameter (direction = Parameter.Direction.IN)
                    double[] cols
    );
*/

    @Method(declaringClass = "KNN.Knn")
    void getResult(
            @Parameter (direction = Parameter.Direction.IN)
                    ArrayList<Sample> validationSet,
            @Parameter (direction = Parameter.Direction.IN)
                    ArrayList<Sample> trainingSet,
            @Parameter (direction = Parameter.Direction.INOUT)
                    int[] partial,
            @Parameter (direction = Parameter.Direction.IN)
                    int K
    );



    @Method(declaringClass = "KNN.Knn")
    void verify(
            @Parameter (direction = Parameter.Direction.IN)
                    ArrayList<Sample>part,
            @Parameter (direction = Parameter.Direction.IN)
                    int[] partial,
            @Parameter (direction = Parameter.Direction.INOUT)
                    int[] correct
    );






}
