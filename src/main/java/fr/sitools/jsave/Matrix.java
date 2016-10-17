package fr.sitools.jsave;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

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

    public static class JSave {

        private final File file;
        private RandomAccessFile raf;
        private HashMap<String, Object> variables;
        private HashMap<String, Object> metadata;

        private static final HashMap<Integer, String> RECTYPE_DICT = new HashMap<Integer, String>() {{
            put(0, "START_MARKER");
            put(1, "COMMON_VARIABLE");
            put(2, "VARIABLE");
            put(3, "SYSTEM_VARIABLE");
            put(6, "END_MARKER");
            put(10, "TIMESTAMP");
            put(12, "COMPILED");
            put(13, "IDENTIFICATION");
            put(14, "VERSION");
            put(15, "HEAP_HEADER");
            put(16, "HEAP_DATA");
            put(17, "PROMOTE64");
            put(19, "NOTICE");
            put(20, "DESCRIPTION");
        }};

        public JSave(File file) throws FileNotFoundException {
            this.file = file;
            this.raf = new RandomAccessFile(file, "r");
            this.variables = new HashMap<String, Object>();
            this.metadata = new HashMap<String, Object>();
        }

        public void read() throws IOException, Exception {
            String signature = readSignature();
            checkSignature(signature);
            isCompressed();
            while (true) {
                HashMap<String, Object> record = readRecord();
                if (record.containsKey("data")) { // Variables
                    this.getVariables().put(record.get("varname").toString().toLowerCase(), record.get("data"));
                } else { // metadata
                    this.getMetadata().putAll(record);
                }
                if (record.containsKey("end") && Boolean.parseBoolean((String) record.get("end")) == true) {
                    break;
                }
            }
            this.raf.close();
        }

        private String readSignature() throws IOException {
            byte[] data = new byte[2];
            this.raf.read(data);
            String str = new String(data, StandardCharsets.UTF_8);
            return str;
        }

        private void checkSignature(String signature) throws Exception {
            if (!"SR".equals("SR")) {
                throw new Exception("Invalid SIGNATURE: " + signature);
            }
        }

        private boolean isCompressed() throws IOException {
            byte[] data = new byte[2];
            this.raf.read(data);
            return true;
        }

        /**
         * Read the file and casting data depending on the type
         *
         * @return
         * @throws IOException
         * @throws Exception
         */
        private HashMap<String, Object> readRecord() throws Exception {
            HashMap record = new HashMap<>();
            record.put("rectype", Utils.read_long(this.raf));
            int nextrec = Utils.read_uint32(this.raf);
            nextrec += Utils.read_uint32(this.raf) * 2 * Math.pow(2, 32);
            Utils.skip_bytes(this.raf, 4);
            if (!RECTYPE_DICT.containsKey(record.get("rectype"))) {
                throw new Exception("Unknown RECTYPE: " + record.get("rectype"));
            }

            record.replace("rectype", RECTYPE_DICT.get(record.get("rectype")));
            String rectype = (String) record.get("rectype");
            switch (rectype) {
                case "VARIABLE":
                    record.put("varname", Utils.read_string(this.raf));
                    HashMap<String, Object> rectypedesc = Utils.read_typedesc(raf);
                    if ((int) rectypedesc.get("typecode") == 0) {
                        if (nextrec == this.raf.getFilePointer()) {
                            record.put("data", null);
                        } else {
                            throw new Exception("Unexpected type code: 0");
                        }
                    } else {
                        int varstart = Utils.read_long(raf);
                        if (varstart != 7) {
                            throw new Exception("VARSTART is not 7");
                        }
                        if ((boolean) rectypedesc.get("structure")) {
                            record.put("data", Utils.read_structure(raf, (HashMap) rectypedesc.get("array_desc"), (HashMap) rectypedesc.get("struct_desc")));
                        } else if ((boolean) rectypedesc.get("array")) {
                            Matrix data = Utils.read_array(raf, (int) rectypedesc.get("typecode"), (HashMap) rectypedesc.get("array_desc"));
                            record.put("data", data);
                        } else {
                            int dtype = (int) rectypedesc.get("typecode");
                            record.put("data", Utils.read_data(raf, dtype));
                        }
                    }
                    break;
                case "HEAP_DATA":
                    record.put("heap_index", Utils.read_long(this.raf));
                    Utils.skip_bytes(this.raf, 4);
                    rectypedesc = Utils.read_typedesc(raf);
                    if ((int) rectypedesc.get("typecode") == 0) {
                        if (nextrec == this.raf.getFilePointer()) {
                            record.put("data", null);
                        } else {
                            throw new Exception("Unexpected type code: 0");
                        }
                    } else {
                        int varstart = Utils.read_long(raf);
                        if (varstart != 7) {
                            throw new Exception("VARSTART is not 7");
                        }
                        if ((boolean) rectypedesc.get("structure")) {
                            record.put("data", Utils.read_structure(raf, (HashMap) rectypedesc.get("array_desc"), (HashMap) rectypedesc.get("struct_desc")));
                        } else if ((boolean) rectypedesc.get("array")) {
                            record.put("data", Utils.read_array(raf, (int) rectypedesc.get("typecode"), (HashMap) rectypedesc.get("array_desc")));
                        } else {
                            int dtype = (int) rectypedesc.get("typecode");
                            record.put("data", Utils.read_data(raf, dtype));
                        }
                    }

                    break;
                case "TIMESTAMP":
                    Utils.skip_bytes(this.raf, 4 * 256);
                    record.put("date", Utils.read_string(this.raf));
                    record.put("user", Utils.read_string(this.raf));
                    record.put("host", Utils.read_string(this.raf));
                    break;
                case "VERSION":
                    record.put("format", Utils.read_long(this.raf));
                    record.put("arch", Utils.read_string(this.raf));
                    record.put("os", Utils.read_string(this.raf));
                    record.put("release", Utils.read_string(this.raf));
                    break;
                case "IDENTIFICATON":
                    record.put("author", Utils.read_string(this.raf));
                    record.put("title", Utils.read_string(this.raf));
                    record.put("idcode", Utils.read_string(this.raf));
                    break;
                case "NOTICE":
                    record.put("notice", Utils.read_string(this.raf));
                    break;
                case "DESCRIPTION":
                    record.put("description", Utils.read_string_data(this.raf));
                    break;
                case "HEAP_HEADER":
                    record.put("nvalues", Utils.read_long(this.raf));
                    int length = (int) record.get("nvalues");
                    int[] indices = new int[length];
                    for (int i = 0; i < length; i++) {
                        indices[i] = Utils.read_long(this.raf);
                    }
                    record.put("indices", indices);
                    break;
                case "COMMONBLOCK":
                    record.put("nvars", Utils.read_long(this.raf));
                    record.put("name", Utils.read_string(this.raf));
                    length = (int) record.get("nvars");
                    String[] varnames = new String[length];
                    for (int i = 0; i < length; i++) {
                        varnames[i] = Utils.read_string(this.raf);
                    }
                    record.put("varnames", varnames);
                    break;
                case "END_MARKER":
                    record.put("end", "True");
                    break;
                case "UNKNOWN":
                    // warnings.warn("Skipping UNKNOWN record")
                    break;
                case "SYSTEM_VARIABLE":
                    // warnings.warn("Skipping SYSTEM_VARIABLE record")
                    break;
                default:
                    throw new Exception("record['rectype']=" + rectype + " not implemented");

            }
    //        System.out.println(nextrec);

            this.raf.seek(nextrec);
            return record;
        }

        public void displayFileMetadata() {
            int cut = 0;
            System.out.println("----------- METADATA -----------");
            for (Map.Entry<String, Object> entry : this.getMetadata().entrySet()) {
                if (entry.getKey().equals("rectype"))
                    continue;

                System.out.print(entry.getKey() + "--> ");
                System.out.println(entry.getValue());
            }
        }

        public void displayAvailableVariables() {
            int cut = 0;
            System.out.println("----------- Available variables -----------");
            for (Map.Entry<String, Object> entry : this.getVariables().entrySet()) {
                if (entry.getKey().equals("rectype"))
                    continue;

                System.out.print("- " + entry.getKey());
                System.out.println("[<type '" + entry.getValue().getClass() + ">]");
            }
        }

        /* Getters & Setters */

        public HashMap<String, Object> getVariables() {
            return variables;
        }

        public void setVariables(HashMap<String, Object> variables) {
            this.variables = variables;
        }

        public HashMap<String, Object> getMetadata() {
            return metadata;
        }

        public void setMetadata(HashMap<String, Object> metadata) {
            this.metadata = metadata;
        }

        /* MAIN */

        public static void main(String[] args) throws FileNotFoundException, IOException, Exception {

            String savFilePath = new File(".").getCanonicalPath() + "\\data\\";
            JSave sav = new JSave(new File(savFilePath + "cube8439_2.sav"));
            sav.read();

            sav.displayFileMetadata();
            sav.displayAvailableVariables();

        }

    }
}
