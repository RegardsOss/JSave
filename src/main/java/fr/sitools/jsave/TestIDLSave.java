package fr.sitools.jsave;

public final class TestIDLSave {

    private TestIDLSave() {
    }

    public static void main(String[] args) {

        Matrix cube = new Matrix(2, 2, 3);

        float[] fArr = new float[12];

        for (int i = 0; i < 12; i++) {
            fArr[i] = i;
        }

        int indfArr = 0;
        for (int d2 = 0; d2 < 3; d2++) { // 69 plans
            for (int d0 = 0; d0 < 2; d0++) { // 480 lignes
                for (int d1 = 0; d1 < 2; d1++) { // 120 colonnes
                    cube.setCubeValue(d0, d1, d2, fArr[indfArr]);
                    indfArr++;
                }
            }
        }

        double[] zslice2 = cube.getPlan(2);
        double[] yslice2 = cube.getVectorY(1);

    }


}