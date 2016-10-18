package jsave;

/**
 * Find on Stack Overflow
 * http://stackoverflow.com/questions/15992076/java-3d-array-storing-cube-retreiving-slice-of-cube
 * author : -> Aquillo http://stackoverflow.com/users/1336310/aquillo
 */
public class Matrix {

    /**
     * <pre>
     * y or height
     * ^
     * |
     * |  / z or deep
     * | /
     * |/
     * ------------>  x or width
     * </pre>
     */
    private final int width, height, deep;

    /**
     * Stores the 3D matrix in one dimension array.
     */
    private final double[] data;

    /**
     * Stores the statistics in one dimension array. For each deep, [min,max]
     * are stored.
     */
    private final double[] stats;

    /**
     * Constructs a 3D Matrix.
     *
     * @param w the number of pixels along x axis
     * @param h the number of pixels along y axis
     * @param d the number of pixels along z axis
     */
    public Matrix(int w, int h, int d) {
        this.width = w;
        this.height = h;
        this.deep = d;
        this.data = new double[w * h * d];
        this.stats = computeStats();
    }

    /**
     * Computes the statistics (min,max) for each deep.
     *
     * @return the one dimension matrix that contains (min,max) for each deep
     */
    private double[] computeStats() {
        double[] statistics = new double[2 * this.deep];
        for (int i = 0; i < this.deep; i++) {
            statistics[2 * i] = Double.POSITIVE_INFINITY; // min
            statistics[2 * i + 1] = Double.NEGATIVE_INFINITY; //max
        }
        return statistics;
    }

    /**
     * Retrieves a value of the data cube
     *
     * @param x pixel number along x axis (starts with 0)
     * @param y pixel number along y axis (starts with 0)
     * @param z pixel number along z axis (starts with 0)
     * @return the value of (x,y,z)
     */
    public double getCubeValue(int x, int y, int z) {
        return this.data[x * this.height * this.deep + y * this.deep + z];
    }

    /**
     * Sets the value in a cube and computes statistics. While the value is set
     * to the cube, the statistics is computes. The main goal of the statistics
     * is to compute the min/max for each deep.
     *
     * @param x pixel number along x axis (starts with 0)
     * @param y pixel number along y axis (starts with 0)
     * @param z pixel number along z axis (starts with 0)
     * @param value the value to set in (x,y,z)
     */
    void setCubeValue(int x, int y, int z, double value) {
        this.data[x * this.height * this.deep + y * this.deep + z] = value;
        if (this.stats[2 * z] > value) {
            this.stats[2 * z] = value;
        }
        if (this.stats[2 * z + 1] < value) {
            this.stats[2 * z + 1] = value;
        }
    }

    /**
     * Returns the statistics for the deep d.
     *
     * @param d the deep for which the statistics are retrieved
     * @return the statistics (min/max) for the deep d
     */
    public double[] getStats(int d) {
        return new double[]{this.stats[2 * d], this.stats[2 * d + 1]};
    }

    /**
     * Returns all statistics as a one dimension array.
     *
     * @return one dimension array
     */
    public double[] getStats() {
        return this.stats;
    }

    /**
     * Returns a vector along x axis crossing the coordinate (y,z) for each x
     * value.
     *
     * Gives the coordinate (y,z) and returns the vector along x axis
     * <pre>
     * y
     * ^
     * |   z (y,z)
     * |  / *------->
     * | /
     * |/
     * ------------>  x
     * </pre>
     *
     * @param y the y coordinate
     * @param z the z coordinate
     * @return a vector along x axis crossing the coordinate (y,z) for each x
     * value
     */
    public double[] getVectorAlongX(int y, int z) {
        double[] slice = new double[1 * this.width];
        for (int x = 0; x < this.width; x++) {
            slice[x] = getCubeValue(x, y, z);
        }
        return slice;
    }

    /**
     * Returns a vector along y axis crossing the point (x,z).
     *
     * Gives the coordinate (x,z) and returns the vector along y axis.
     * <pre>
     * y
     * ^        ^
     * |   z    |
     * |  /     |
     * | /      |
     * |/       *(x,z)
     * ------------>  x
     * </pre>
     *
     * @param x the y coordinate (starts with 0)
     * @param z the z coordinate (starts with 0)
     * @return a vector along z axis crossing all plans (X,Y)
     */
    public double[] getVectorAlongY(int x, int z) {
        double[] slice = new double[this.height];
        for (int y = 0; y < this.height; y++) {
            slice[y] = getCubeValue(x, y, z);
        }
        return slice;
    }

    /**
     * Returns a vector along z axis crossing the point (x,y).
     *
     * Gives the coordinate (x,y) and returns the vector along z axis.
     * <pre>
     * y
     * ^
     * |   z       ^
     * |  /       /
     * | /       /
     * |/       *(x,y)
     * ------------>  x
     * </pre>
     *
     * @param x the y coordinate (starts with 0)
     * @param y the z coordinate (starts with 0)
     * @return a vector along z axis crossing all plans (X,Y)
     */
    public double[] getVectorAlongZ(int x, int y) {
        double[] slice = new double[this.deep];
        for (int z = 0; z < this.deep; z++) {
            slice[z] = getCubeValue(x, y, z);
        }
        return slice;
    }

    /**
     * Get a matrix along x and z axis for a given y.
     * <pre>
     * y
     * ^        _______
     * |   z   /       /
     * |  /   /       /
     * | /   /_______/
     * |/
     * ------------>  x
     * </pre>
     *
     * @param y the y coordinate (starts with 0)
     * @return the vector column along x coordinate
     * @see #getValueFromPlaneXZ(double[], int, int) to get a value from the
     * array
     */
    public double[] getPlaneXZ(int y) {
        double[] slice = new double[this.width * this.deep];
        for (int x = 0; x < this.width; x++) {
            for (int z = 0; z < this.deep; z++) {
                slice[z * this.width + x] = getCubeValue(x, y, z);
            }
        }
        return slice;
    }

    /**
     * Get a matrix along y and z axis for a given x.
     * <pre>
     * y          /|
     * ^         / |
     * |   z    /  |
     * |  /    |  /
     * | /     | /
     * |/      |/
     * ------------>  x
     * </pre>
     *
     * @param x the x coordinate (starts with 0)
     * @return the vector column along x coordinate
     * @see #getValueFromPlaneYZ(double[], int, int) to get a value from the
     * array
     */
    public double[] getPlaneYZ(int x) {
        double[] slice = new double[this.height * this.deep];
        for (int y = 0; y < this.height; y++) {
            for (int z = 0; z < this.deep; z++) {
                slice[y * this.deep + z] = getCubeValue(x, y, z);
            }
        }
        return slice;
    }

    /**
     * Returns the matrix x,y to the given deep z.
     * <pre>
     * y  ________
     * ^ |        |
     * | | z      |
     * | |/       |
     * | /________|
     * |/
     * ------------>  x
     * </pre>
     *
     * @param z the deep (starts with 0)
     * @return the matrix XY
     * @see #getValueFromPlaneXY(double[], int, int) to get a value from the
     * array
     */
    public double[] getPlane(int z) {
        double[] slice = new double[this.width * this.height];
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                slice[x * height + y] = getCubeValue(x, y, z);
            }
        }
        return slice;
    }

    /**
     * Returns the dimension of the matrix.
     *
     * @return the dimension of the matrix.
     */
    public String shape() {
        return this.width + " x " + this.height + " x " + this.deep;
    }

    /**
     * Return the number of elements in the matrix
     *
     * @return the number of values in the matrix
     */
    public int size() {
        return this.width * this.height * this.deep;
    }

    /**
     * Returns the (y,z) for a x deep.
     *
     * @param slice the deep {@link #getPlaneYZ(int) getDeep}
     * @param y the y coordinate
     * @param z the z coordinate
     * @return the (y,z) for a x deep
     */
    public double getValueFromPlaneYZ(double[] slice, int y, int z) {
        return slice[y * this.deep + z];
    }

    /**
     * Returns the value (x,z) coordinate for a y deep.
     *
     * @param slice the deep {@link #getPlaneXZ(int) getDeep}
     * @param x the x coordinate
     * @param z the z coordinate
     * @return the value (x,z) coordinate for a y deep
     */
    public double getValueFromPlaneXZ(double[] slice, int x, int z) {
        return slice[z * this.width + x];
    }

    /**
     * Returns the value of (x,y) coordinate for a z deep.
     *
     * @param slice the deep {@link #getPlane(int) getDeep}
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the value of (x,y)
     */
    public double getValueFromPlaneXY(double[] slice, int x, int y) {
        return slice[x * this.height + y];
    }

    /**
     * Returns the matrix as one dimension array.
     *
     * @return the matrix
     */
    public double[] getData() {
        return data;
    }

    /**
     * Returns the number of pixels along x axis.
     *
     * @return the number of pixels along x axis
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the number of pixels along y axis.
     *
     * @return the number of pixels along y axis
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns the number of pixels along z axis.
     *
     * @return the number of pixels along z axis
     */
    public int getDeep() {
        return deep;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder("\narray(");
        printArrayZ(output);
        output = output.append(")\n");
        return output.toString();
    }
    
    /**
     * Displays the deep part.
     * @param output output
     */
    private void printArrayZ(StringBuilder output) {
        if (this.getDeep() > 1) {
            output = output.append("[");
        }         
        for (int i = 0; i < Matrix.this.getDeep(); i++) {
            printArrayY(output, i);
            if(i==getDeep()-1) {
                output = output.append("\n").append("       "); 
            } else {
                output = output.append(",\n").append("       ");    
            }            
        }
        if (this.getDeep() > 1) {
            output = output.append("]");
        }         
    }

    /**
     * Displays the height
     * @param output output
     * @param i the deep index
     */
    private void printArrayY(StringBuilder output, int i) {
        boolean tooLongHeight = false;
        if (this.getHeight() > 1) {
            output = output.append("[");
        }        
        for (int j = 0; j < getHeight(); j++) {
            if (j <= 3 || j >= getHeight() - 3) {
                printArrayX(output, i, j);               
                if(j == getHeight()-1) {
                    output = output.append("\n").append("       ");    
                } else {
                    output = output.append(",\n").append("       ");        
                }                            
            } else if (!tooLongHeight) {
                output = output.append("...,\n").append("       ");
                tooLongHeight = true;
            }
        }
        if (this.getHeight() > 1) {
            output = output.append("]");
        }        
    }

    /**
     * Displays the width.
     * @param output output
     * @param i deep index
     * @param j height index
     */
    private void printArrayX(StringBuilder output, int i, int j) {
        boolean tooLongWidth = false;
        if (this.getWidth() > 1) {
            output = output.append("[");
        }
        for (int k = 0; k < getWidth(); k++) {
            if (k <= 2 || k >= (getWidth() - 2 - 1)) {
                output = output.append(" ").append(String.format("%.8f", getCubeValue(k, j, i)));
                if (k == (getWidth() - 1)) {

                } else {
                    output = output.append(",");
                }
                if ((k + 1) % 6 == 0 && k != 0 && k + 1 != getWidth()) {
                    output = output.append("\n        ");
                }
            } else if (!tooLongWidth) {
                output = output.append(" ...,");
                tooLongWidth = true;
            }
        }
        if (this.getWidth() > 1) {
            output = output.append("]");
        }
    }
}
