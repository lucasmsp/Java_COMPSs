package kmeansHDFS;

import integratedtoolkit.types.annotations.task.Method;
import integratedtoolkit.types.annotations.Parameter;
import integratedtoolkit.types.annotations.Parameter.Direction;
import integratedtoolkit.types.annotations.Parameter.Type;
import integration.Block;

/**
 * Created by lucasmsp on 03/02/17.
 */
public interface KMeansDataSetItf {








        @Method(declaringClass = "kmeansHDFS.DataSetIft")
        public float[][] mergeResults(
                @Parameter float[] point,
                @Parameter float[][] points,
                @Parameter int frag

        );

        @Method(declaringClass = "kmeansHDFS.DataSetIft")
        public float[] read(
                @Parameter(type = Type.OBJECT, direction = Direction.IN) Block blk,
                @Parameter  int numDimensions
        );
    }
