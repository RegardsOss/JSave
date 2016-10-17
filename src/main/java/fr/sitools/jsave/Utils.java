package fr.sitools.jsave;


import org.apache.commons.math3.complex.Complex;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class Utils {

    public static final HashMap STRUCT_DICT = new HashMap();

    private static final HashMap<Integer, String> DTYPE_DICT = new HashMap<Integer, String>() {
        {
            put(1, ">u1"); //1-byte unsigned integer, "U1 0"
            put(2, ">i2"); //2-byte signed integer, "I2 99",  "I2 15 -7 99"
            put(3, ">i4"); //4-byte integer signed, "I4 -5"
            put(4, ">f4"); //4-byte floating point, "F4 1.0"
            put(5, ">f8"); //8-byte floating point, "F8 6.02e23", "F8 0.1"
            put(6, ">c8");
            put(7, "|O");
            put(8, "|O");
            put(9, ">c16"); //128-bit complex floating-point number
            put(10, "|O");
            put(11, "|O");
            put(12, ">u2"); //2-byte unsigned integer, "U2 512"
            put(13, ">u4"); //4-byte unsigned integer, "U2 979"
            put(14, ">i8"); //8-byte signed integer, use hex notation for the value, "I8 0x123456789abcdf01"
            put(15, ">u8"); //8-byte unsigned integer, use hex notation for the value, "U8 0x7fffffffffffffff"
        }
    };

    public static int read_long(final RandomAccessFile raf) throws IOException {
        byte[] data = new byte[4];
        raf.read(data);
        return toInt(data);
    }

    // TODO To be verified
    public static int read_uint16(final RandomAccessFile raf) throws IOException {
        byte[] data = new byte[4];
        raf.read(data);
        return toShort(data);
    }

    public static int read_uint32(final RandomAccessFile raf) throws IOException {
        byte[] data = new byte[4];
        raf.read(data);
        return toInt(data);
    }

    private static long read_uint64(final RandomAccessFile raf) throws IOException {
        byte[] data = new byte[4];
        raf.read(data);
        return toLong(data);
    }

    public static String read_string(final RandomAccessFile raf) throws IOException {
        int length = read_long(raf);
        String result;
        if (length > 0) {
            byte[] data = new byte[length];
            raf.read(data);
            align_32(raf);
            result = new String(data, StandardCharsets.UTF_8);
        } else {
            result = "";
        }
        return result;
    }

    public static String read_string_data(final RandomAccessFile raf) throws IOException {
        int length = read_long(raf);
        String result;
        if (length > 0) {
            length = read_long(raf);
            byte[] data = new byte[length];
            raf.read(data);
            align_32(raf);
            result = new String(data, StandardCharsets.UTF_8);
        } else {
            result = "";
        }
        return result;
    }

    public static HashMap<String, Object> read_typedesc(final RandomAccessFile raf) throws IOException, Exception {
        HashMap<String, Object> typedesc = new HashMap<>();
        typedesc.put("typecode", read_long(raf));
        typedesc.put("varflags", read_long(raf));
        if (2 == ((int) typedesc.get("varflags") & 2)) {
            throw new Exception("System variables not implemented");
        }
        typedesc.put("array", ((int) typedesc.get("varflags") & 4) == 4);
        typedesc.put("structure", ((int) typedesc.get("varflags") & 32) == 32);
        if ((boolean) typedesc.get("structure")) {
            typedesc.put("array_desc", read_arraydesc(raf));
            typedesc.put("struct_desc", read_structdesc(raf));
        } else if ((boolean) typedesc.get("array")) {
            typedesc.put("array_desc", read_arraydesc(raf));
        }
        return typedesc;
    }

    public static HashMap<String, Object> read_arraydesc(final RandomAccessFile raf) throws IOException, Exception {
        HashMap<String, Object> arraydesc = new HashMap<>();
        arraydesc.put("arrstart", read_long(raf));
        int arrStart = (int) arraydesc.get("arrstart");
        switch (arrStart) {
            case 8:
                skip_bytes(raf, 4);
                arraydesc.put("nbytes", read_long(raf));
                arraydesc.put("nelements", read_long(raf));
                arraydesc.put("ndims", read_long(raf));
                skip_bytes(raf, 8);
                arraydesc.put("nmax", read_long(raf));
                int length = (int) arraydesc.get("nmax");
                int[] dims = new int[length];
                for (int i = 0; i < length; i++) {
                    dims[i] = read_long(raf);
                }
                arraydesc.put("dims", dims);
                break;
            case 18:
                //warnings.warn("Using experimental 64-bit array read")
                //_skip_bytes(f, 8)

                //arraydesc['nbytes'] = _read_uint64(f)
                //arraydesc['nelements'] = _read_uint64(f)
                //arraydesc['ndims'] = _read_long(f)
                //_skip_bytes(f, 8)
                //arraydesc['nmax'] = 8
                //arraydesc['dims'] = []
                //for d in range(arraydesc['nmax']):
                //    v = _read_long(f)
                //    if v != 0:
                //        raise Exception("Expected a zero in ARRAY_DESC")
                //    arraydesc['dims'].append(_read_long(f))                
                break;
            default:
                throw new Exception("Unknown ARRSTART: " + arraydesc.get("arrstart"));
        }

        return arraydesc;
    }

    public static HashMap<String, Object> read_structdesc(final RandomAccessFile raf) throws IOException, Exception {
        HashMap<String, Object> structdesc = new HashMap<>();
        int structstart = read_long(raf);
        if (structstart != 9) {
            throw new Exception("STRUCTSTART should be 9");
        }
        structdesc.put("name", read_string(raf));
        int predef = read_long(raf);
        structdesc.put("ntags", read_long(raf));
        structdesc.put("nbytes", read_long(raf));
        structdesc.put("predef", predef & 1);
        structdesc.put("inherits", predef & 2);
        structdesc.put("is_super", predef & 4);
        if ((int) structdesc.get("predef") != 1) {
            int length = (int) structdesc.get("ntags");
            HashMap[] tagtable = new HashMap[length];
            String[] name = new String[length];
            for (int i = 0; i < length; i++) {
                tagtable[i] = read_tagdesc(raf);
            }
            structdesc.put("tagtable", tagtable);
            for (HashMap tag : (HashMap[]) structdesc.get("tagtable")) {
                tag.put("name", read_string(raf));
            }
            HashMap<String, Object> arrtable = new HashMap<>();
            for (HashMap tag : (HashMap[]) structdesc.get("tagtable")) {
                if ((int) tag.get("array") == 1) {
                    arrtable.put((String) tag.get("name"), read_arraydesc(raf));
                }
            }
            structdesc.put("arrtable", arrtable);

            HashMap<String, Object> structtable = new HashMap<>();
            for (HashMap tag : (HashMap[]) structdesc.get("tagtable")) {
                if ((int) tag.get("structure") == 1) {
                    structtable.put((String) tag.get("name"), read_structdesc(raf));
                }
            }
            structdesc.put("structtable", structtable);

            if ((int) structdesc.get("inherits") == 1 || (int) structdesc.get("is_super") == 1) {
                structdesc.put("classname", read_string(raf));
                structdesc.put("nsupclasses", read_long(raf));
                int lengthSupclassnames = (int) structdesc.get("nsupclasses");
                String[] supclassnames = new String[lengthSupclassnames];
                for (int i = 0; i < lengthSupclassnames; i++) {
                    supclassnames[i] = read_string(raf);
                }
                structdesc.put("supclassnames", supclassnames);

                HashMap[] supclasstable = new HashMap[((HashMap[]) structdesc.get("nsupclasses")).length];
                for (int i = 0; i < ((HashMap[]) structdesc.get("nsupclasses")).length; i++) {
                    supclasstable[i] = read_structdesc(raf);
                }
                structdesc.put("supclasstable", supclasstable);
            }
            STRUCT_DICT.put(structdesc.get("name"), structdesc);

        } else {
            if (!STRUCT_DICT.containsKey(structdesc.get("name"))) {
                throw new Exception("PREDEF=1 but can't find definition");
            }

            structdesc = (HashMap<String, Object>) STRUCT_DICT.get(structdesc.get("name"));
        }
        return structdesc;
    }

    private static HashMap<String, Object> read_tagdesc(RandomAccessFile raf) throws IOException {
        HashMap<String, Object> tagdesc = new HashMap<>();
        tagdesc.put("offset", read_long(raf));
        if ((int) tagdesc.get("offset") == -1) {
            tagdesc.put("offset", read_uint64(raf));
        }
        tagdesc.put("typecode", read_long(raf));
        int tagflags = read_long(raf);
        tagdesc.put("array", (tagflags & 4) == 4);
        tagdesc.put("structure", (tagflags & 32) == 32);
        tagdesc.put("scalar", DTYPE_DICT.containsKey(tagdesc.get("typecode")));
        // Assume '10'x is scalar
        return tagdesc;
    }

    public static Object read_structure(RandomAccessFile raf, HashMap array_desc, HashMap struct_desc) {
        HashMap[] nrows = (HashMap[]) array_desc.get("nelements");
        HashMap[] columns = (HashMap[]) struct_desc.get("tagtable");
        for (HashMap col : columns) {
            if ((boolean) col.get("structure") || (boolean) col.get("array")) {

            }
            //dtype.append(((col['name'].lower(), col['name']), np.object_))

        }
        return null;
    }

    static Matrix read_array(RandomAccessFile raf, int typecode, HashMap array_desc) throws Exception {

        //TODO : To be refactored to take into account typecode
//        if (typecode == 1 || typecode == 3 || typecode == 4 ||
//                typecode == 5 || typecode == 6 || typecode == 9 || typecode == 13 ||
//                typecode == 14 || typecode == 15) {
//            if (typecode == 1) {
//                int nbBytes = read_int32(raf);
//                if (nbBytes != (int) array_desc.get("nbytes")) {
//                    throw new Exception("Error occurred while reading byte array");
//                }
//            }
//            // read as numpy array
//            byte[] data = new byte[(int) array_desc.get("nbytes")];
//            raf.read(data);
//            Array ok =  fromString(data, DTYPE_DICT.get(typecode));
//
//        } else if (typecode == 2 || typecode == 12) {
//            // JC old version
//            byte[] data = new byte[(int) array_desc.get("nbytes")];
//            raf.read(data);
//            ByteArrayInputStream bas = new ByteArrayInputStream(data);
//            DataInputStream ds = new DataInputStream(bas);
//            float[] fArr = new float[data.length / 4];  // 4 bytes per float
//            for (int i = 0; i < fArr.length; i++) {
//                fArr[i] = ds.readFloat();
//            }
//        }

        byte[] data = new byte[(int) array_desc.get("nbytes")];
        raf.read(data);
        ByteArrayInputStream bas = new ByteArrayInputStream(data);
        DataInputStream ds = new DataInputStream(bas);
        float[] fArr = new float[data.length / 4];  // 4 bytes per float
        for (int i = 0; i < fArr.length; i++) {
            fArr[i] = ds.readFloat();
        }

        double[] doubleArray = new double[fArr.length];

        // Convert floatArray to doubleArray
        for (int i = 0; i < fArr.length; i++) {
            doubleArray[i] = (double) fArr[i];
        }

        Matrix cube;
        int nbDims = (int) array_desc.get("ndims");

        int[] dims = new int[nbDims];
        int[] tmpDims = (int[]) array_desc.get("dims");

        // Filtering useless '1' value in dimension tab
        int j = 0;
        for (int i = 0; i < tmpDims.length; i++) {
            if (tmpDims[i] != 1) {
                dims[j] = tmpDims[i];
                j++;
            }
        }
        reverseArray(dims);

        int xDim = (dims.length >= 1) ? dims[0] : 1;
        int yDim = (dims.length >= 2) ? dims[1] : 1;
        int zDim = (dims.length == 3) ? dims[2] : 1;
        cube = new Matrix(xDim, yDim, zDim); // 480, 120, 69 ==> 3974400

        int indfArr = 0;
        for (int d2 = 0; d2 < zDim; d2++) { // 69 plans
            for (int d0 = 0; d0 < xDim; d0++) { // 480 lignes
                for (int d1 = 0; d1 < yDim; d1++) { // 120 colonnes
                    cube.setCubeValue(d0, d1, d2, fArr[indfArr]);
                    indfArr++;
                }
            }
        }

        align_32(raf);

        return cube;
    }

    /**
     * Reverse data order in passed array
     *
     * @param dims the dimension
     */
    private static void reverseArray(int[] dims) {
        // Reversing Array
        if (dims != null) {
            int i = 0;
            for (int x = dims.length - 1; x > i; ++i) {
                int tmp = dims[x];
                dims[x] = dims[i];
                dims[i] = tmp;
                --x;
            }
        }
    }

    /**
     * Convert an array of bytes to an array of double
     *
     * @param byteArray array of bytes
     * @return
     */
    public static double[] toDoubleArray(byte[] byteArray) {
        int times = 4 / Byte.SIZE;
        double[] doubles = new double[byteArray.length / times];
        for (int i = 0; i < doubles.length; i++) {
            doubles[i] = ByteBuffer.wrap(byteArray, i * times, times).getDouble();
        }
        return doubles;
    }

    static Object read_data(RandomAccessFile raf, int dtype) throws Exception {

        if (dtype == 1) {
            if (read_int32(raf) != -1) {
                throw new Exception("Error occurred while reading byte variable");
            }
            return read_byte(raf);
        } else if (dtype == 2) {
            return read_int16(raf);
        } else if (dtype == 3) {
            return read_int32(raf);
        } else if (dtype == 4) {
            return read_float32(raf);
        } else if (dtype == 5) {
            return read_float64(raf);
        } else if (dtype == 6) {
            float real = read_float32(raf);
            float img = read_float32(raf);
            return new Complex(real, img);
//            return complex64(real + img * 1j); // 1j ???
        } else if (dtype == 7) {
            return read_string_data(raf);
        } else if (dtype == 8) {
            throw new Exception("Should not be here - please report this");
        } else if (dtype == 9) {
            float real = read_float32(raf);
            float img = read_float32(raf);
            return new Complex(real, img);
//            return complex128(real + img * 1j); // 1j ???
        } else if (dtype == 10) {
//            return Pointer(read_int32(raf));
        } else if (dtype == 11) {
//            return ObjectPointer(read_int32(raf));
        } else if (dtype == 12) {
            return read_uint16(raf);
        } else if (dtype == 13) {
            return read_uint32(raf);
        } else if (dtype == 14 || dtype == 15) {
            return read_uint64(raf);
        } else {
            throw new UnsupportedOperationException("Unknow IDL type" + dtype + " - please report this");
        }
        return null;

//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static byte read_byte(RandomAccessFile raf) {
        byte[] data = new byte[4];
        byte byteData = -1;
        try {
            raf.read(data);
            byteData = ByteBuffer.wrap(data).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return byteData;
    }

    private static short read_int16(RandomAccessFile raf) {
        byte[] data = new byte[4];
        short shortData = -1;
        try {
            raf.read(data);
            shortData = ByteBuffer.wrap(data).getShort();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return shortData;
    }

    private static int read_int32(RandomAccessFile raf) {
        byte[] data = new byte[4];
        int intData = -1;
        try {
            raf.read(data);
            intData = ByteBuffer.wrap(data).getInt();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return intData;
    }

    private static float read_float32(RandomAccessFile raf) {
        byte[] data = new byte[4];
        float floatData = -1;
        try {
            raf.read(data);
            floatData = ByteBuffer.wrap(data).getFloat();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return floatData;
    }

    private static float read_float64(RandomAccessFile raf) {
        byte[] data = new byte[4];
        float floatData = -1;
        try {
            raf.read(data);
            floatData = ByteBuffer.wrap(data).getLong();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return floatData;
    }

    public static final void align_32(RandomAccessFile raf) throws IOException {
        long pos = raf.getFilePointer();
        if (pos % 4 != 0) {
            raf.seek(pos + 4 - pos % 4);
        }
    }

    public static void skip_bytes(final RandomAccessFile raf, int length) throws IOException {
        byte[] data = new byte[length];
        raf.read(data);
    }

    // TODO To be verified
    public static int toShort(byte[] b) {
        ByteBuffer bb = ByteBuffer.allocate(b.length);
        bb.put(b);
        return bb.getShort(0);
    }

    public static int toInt(byte[] b) {
        ByteBuffer bb = ByteBuffer.allocate(b.length);
        bb.put(b);
        return bb.getInt(0);
    }

    public static long toLong(byte[] b) {
        ByteBuffer bb = ByteBuffer.allocate(b.length);
        bb.put(b);
        return bb.getLong(0);
    }

}
