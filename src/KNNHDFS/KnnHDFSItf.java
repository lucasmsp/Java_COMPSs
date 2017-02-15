package KNNHDFS;


import integratedtoolkit.types.annotations.Parameter;
import integratedtoolkit.types.annotations.task.Method;
import integration.Block;

import java.util.ArrayList;


public interface KnnHDFSItf {


    @Method(declaringClass = "KNNHDFS.KnnHDFS")
    void ClassifierFromHDFSblock(
            @Parameter(type = Parameter.Type.OBJECT, direction = Parameter.Direction.IN)
                    Block blk,
            @Parameter(type = Parameter.Type.OBJECT, direction = Parameter.Direction.IN)
                    ArrayList<Block> FILE_TRAIN_SPLITS,
            @Parameter(type = Parameter.Type.OBJECT, direction = Parameter.Direction.INOUT)
                    int[] correct,
            @Parameter(direction = Parameter.Direction.IN)
            int K
    );

/*

    @Method(declaringClass = "KNNHDFS.KnnHDFS")
    ArrayList<Sample> readblockfromHDFS(
            @Parameter(type = Parameter.Type.OBJECT, direction = Parameter.Direction.IN) Block blk
    );


    @Method(declaringClass = "KNNHDFS.KnnHDFS")
    ArrayList<Integer> getResult(
            @Parameter(type = Parameter.Type.OBJECT, direction = Parameter.Direction.IN)
                    ArrayList<Sample> validationSet,
            @Parameter(type = Parameter.Type.OBJECT, direction = Parameter.Direction.IN)
                    ArrayList<Sample> trainingSet
    );



    @Method(declaringClass = "KNNHDFS.KnnHDFS")
    int[] verify(
            @Parameter(type = Parameter.Type.OBJECT, direction = Parameter.Direction.IN)
                    ArrayList<Sample> part,
            @Parameter(type = Parameter.Type.OBJECT, direction = Parameter.Direction.IN)
                    int[] partial
    );
*/





}
