package fr.sitools.jsave;

/**
 * Find on Stack Overflow
 * http://stackoverflow.com/questions/15992076/java-3d-array-storing-cube-retreiving-slice-of-cube
 * author : -> Aquillo http://stackoverflow.com/users/1336310/aquillo
 */
public class Matrix {

    private int width, height, plan;
    private double[] data; // 1D

    public Matrix(int w, int h, int d) {
        this.width = w;
        this.height = h;
        this.plan = d;
        this.data = new double[w * h * d];

//        System.out.println("data: width" + w + ", height" + h + ", plan " + d + " = " + (w * h * d));
    }

    public double getCubeValue(int x, int y, int z) {
        return this.data[x * this.height * this.plan + y * this.plan + z];
    }

    void setCubeValue(int x, int y, int z, double value) {
//        System.out.println("value " + (x * this.height * this.plan + y * this.plan + z) + ": x" + x + ", y" + y + ", z" + z + " = " + value);
        this.data[x * this.height * this.plan + y * this.plan + z] = value;
    }

    /**
     * Get a vector column depending on x
     *
     * @param x the width
     * @return
     */
    public double[] getVectorX(int x) {
        double[] slice = new double[this.width * this.plan];
        for (int y = 0; y < this.width; y++) {
            for (int z = 0; z < this.plan; z++) {
                slice[z * this.width + y] = getCubeValue(x, y, z);
            }
        }
        return slice;
    }

    // TODO Define what to extract exactly
    public double[] getVectorY(int x) {
        double[] slice = new double[this.height * this.plan];
        for (int y = 0; y < this.height; y++) {
            for (int z = 0; z < this.plan; z++) {
                slice[y * this.plan + z] = getCubeValue(x, y, z);
            }
        }
        return slice;
    }

    /**
     * Get a column vector from only one plan and height
     *
     * @param y the height
     * @param z the plan
     * @return tab with the vector
     */
    public double[] getVectorYZ(int y, int z) {
        double[] slice = new double[1 * this.width];
        for (int x = 0; x < this.width; x++) {
            slice[x] = getCubeValue(x, y, z);
        }
        return slice;
    }

    /**
     * Get a vector from width and height for all plan
     *
     * @param x the width
     * @param y the height
     * @return
     */
    public double[] getVectorXY(int x, int y) {
        double[] slice = new double[this.plan];
        for (int z = 0; z < this.plan; z++) {
            slice[z] = getCubeValue(x, y, z);
        }
        return slice;
    }

    /**
     * Get the matrice to the given plan (z)
     *
     * @param z the plan
     * @return
     */
    public double[] getPlan(int z) {
        double[] slice = new double[this.width * this.height];
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                slice[x * height + y] = getCubeValue(x, y, z);
            }
        }
        return slice;
    }

    /**
     * an array containing dimension
     *
     * @return
     */
    public int[] shape() {
        return new int[]{this.width, this.height, this.plan};
    }

    /**
     * Return the numbers of elements in the matrix
     *
     * @return
     */
    public int size() {
        return this.width * this.height * this.plan;
    }

    public double xSliceValue(double[] slice, int y, int z) {
        return slice[y * this.plan + z];
    }

    public double ySliceValue(double[] slice, int x, int z) {
        return slice[z * this.width + x];
    }

    public double zSliceValue(double[] slice, int x, int y) {
        return slice[x * this.height + y];
    }

    /* Getters & Setters */

    public double[] getData() {
        return data;
    }

    public void setData(double[] data) {
        this.data = data;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getPlan() {
        return plan;
    }

    public void setPlan(int plan) {
        this.plan = plan;
    }

}
