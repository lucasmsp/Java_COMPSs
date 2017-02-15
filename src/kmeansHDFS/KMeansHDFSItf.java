
package kmeansHDFS;



import integratedtoolkit.types.annotations.Parameter;
import integratedtoolkit.types.annotations.Parameter.Direction;
import integratedtoolkit.types.annotations.task.Method;


public interface KMeansHDFSItf {

	@Method(declaringClass = "kmeansHDFS.KMeansHDFS")
	void computeNewLocalClusters(
			@Parameter
					int myK,
			@Parameter
					int numDimensions,
			@Parameter
					float[] points,
			@Parameter
					float[] clusterPoints,
			@Parameter(direction = Direction.INOUT)
					float[] newClusterPoints,
			@Parameter(direction = Direction.INOUT)
					int[] clusterCounts
	);

	@Method(declaringClass = "kmeansHDFS.KMeansHDFS")
	void accumulate(
			@Parameter(direction = Direction.INOUT)
					float[] onePoints,
			@Parameter
					float[] otherPoints,
			@Parameter(direction = Direction.INOUT)
					int[] oneCounts,
			@Parameter
					int[] otherCounts
	);

	@Method(declaringClass = "kmeansHDFS.KMeansHDFS")
    float[] initPointsFrag(
			@Parameter
					int oneCounts,
			@Parameter
					int otherCounts
	);

}
