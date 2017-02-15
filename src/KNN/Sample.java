package KNN;

import java.io.Serializable;

/**
 * Created by lucasmsp on 07/02/17.
 */
public class Sample implements Serializable {
    int label;
    double [] cols;

    public Sample(double[] cols, int label) {
        this.cols = cols;
        this.label = label;
    }

    public Sample( ) {

    }

    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = label;
    }

    public double[] getCols() {
        return cols;
    }

    public void setCols(double[] cols) {
        this.cols = cols;
    }
}