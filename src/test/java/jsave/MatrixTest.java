 /*******************************************************************************
 * Copyright 2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 *
 * This file is part of JSave.
 *
 * JSave is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JSave is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSave.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package jsave;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author malapert
 */
public class MatrixTest {
    
    /**
     *   plan 1     Plan 2
     *    111       187
     *    125       731       
     *    732       123 
     *    412       012
     */
    Matrix instance = new Matrix(3,4,2);    
    public MatrixTest() {
        // plan 1
        instance.setCubeValue(0, 0, 0, 1);
        instance.setCubeValue(1, 0, 0, 1);
        instance.setCubeValue(2, 0, 0, 1);
        
        instance.setCubeValue(0, 1, 0, 1);
        instance.setCubeValue(1, 1, 0, 2);
        instance.setCubeValue(2, 1, 0, 5);        
        
        instance.setCubeValue(0, 2, 0, 7);
        instance.setCubeValue(1, 2, 0, 3);
        instance.setCubeValue(2, 2, 0, 2);         
        
        instance.setCubeValue(0, 3, 0, 4);
        instance.setCubeValue(1, 3, 0, 1);
        instance.setCubeValue(2, 3, 0, 2);          
        
        //plan 2
        instance.setCubeValue(0, 0, 1, 1);
        instance.setCubeValue(1, 0, 1, 8);
        instance.setCubeValue(2, 0, 1, 7);
        
        instance.setCubeValue(0, 1, 1, 7);
        instance.setCubeValue(1, 1, 1, 3);
        instance.setCubeValue(2, 1, 1, 1);        
        
        instance.setCubeValue(0, 2, 1, 1);
        instance.setCubeValue(1, 2, 1, 2);
        instance.setCubeValue(2, 2, 1, 3);         
        
        instance.setCubeValue(0, 3, 1, 0);
        instance.setCubeValue(1, 3, 1, 1);
        instance.setCubeValue(2, 3, 1, 2);            
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getCubeValue method, of class Matrix.
     */
    @Test
    public void testGetCubeValue() {
        System.out.println("getCubeValue");
        int x = 1;
        int y = 2;
        int z = 1;
        double expResult = 2;
        double result = instance.getCubeValue(x, y, z);
        assertEquals(expResult, result, 1e-10);

    }
    
    /**
     * Test of getCubeValue method, of class Matrix.
     */
    @Test
    public void testGetSubMatrix() {
        System.out.println("getSubMatrix");
        Matrix subMatrix = instance.getSubMatrix("0:2", "1:2", "0");
        assertEquals(1, subMatrix.getCubeValue(0, 0, 0), 1e-10);
        assertEquals(2, subMatrix.getCubeValue(1, 0, 0), 1e-10);
        assertEquals(5, subMatrix.getCubeValue(2, 0, 0), 1e-10);
        assertEquals(7, subMatrix.getCubeValue(0, 1, 0), 1e-10);
        assertEquals(3, subMatrix.getCubeValue(1, 1, 0), 1e-10);
        assertEquals(2, subMatrix.getCubeValue(2, 1, 0), 1e-10);
    }    

    /**
     * Test of getStats method, of class Matrix.
     */
    @Test
    public void testGetStats_int() {
        System.out.println("getStats");
        int d = 0;
        double[] expResult = new double[]{1,7};
        double[] result = instance.getStats(d);
        assertArrayEquals(expResult, result, 1e-10);
    }

    /**
     * Test of getVectorAlongX method, of class Matrix.
     */
    @Test
    public void testGetVectorAlongX() {
        System.out.println("getVectorAlongX");
        int y = 0;
        int z = 0;
        double[] expResult = new double[]{1,1,1};
        double[] result = instance.getVectorAlongX(y, z);
        assertArrayEquals(expResult, result, 1e-10);
    }
    
    /**
     * Test of getVectorAlongY method, of class Matrix.
     */
    @Test
    public void testGetVectorAlongY() {
        System.out.println("getVectorAlongY");
        int x = 2;
        int z = 0;
        double[] expResult = new double[]{1,5,2,2};
        double[] result = instance.getVectorAlongY(x, z);
        assertArrayEquals(expResult, result,1e-10);
    }    

    /**
     * Test of getVectorAlongZ method, of class Matrix.
     */
    @Test
    public void testGetVectorAlongZ() {
        System.out.println("getVectorAlongZ");
        int x = 2;
        int y = 0;
        double[] expResult = new double[]{1,7};
        double[] result = instance.getVectorAlongZ(x, y);
        assertArrayEquals(expResult, result,1e-10);
    }

    /**
     * Test of getMaxtrixXZ method, of class Matrix.
     */
    @Test
    public void testGetPlaneXZ() {
        System.out.println("getPlaneXZ");
        int y = 0;
        double[] expResult = new double[]{1,1,1,1,8,7};
        double[] result = instance.getPlaneXZ(y);
        assertEquals(expResult[0], instance.getValueFromPlaneXZ(result, 0, 0),1e-10);
        assertEquals(expResult[1], instance.getValueFromPlaneXZ(result, 1, 0),1e-10);
        assertEquals(expResult[2], instance.getValueFromPlaneXZ(result, 2, 0),1e-10);
        assertEquals(expResult[3], instance.getValueFromPlaneXZ(result, 0, 1),1e-10);
        assertEquals(expResult[4], instance.getValueFromPlaneXZ(result, 1, 1),1e-10);
        assertEquals(expResult[5], instance.getValueFromPlaneXZ(result, 2, 1),1e-10);        
        assertArrayEquals(expResult, result,1e-10);
    }

    /**
     * Test of getPlaneYZ method, of class Matrix.
     */
    @Test
    public void testGetPlaneYZ() {
        System.out.println("getPlaneYZ");
        int x = 0;
        double[] expResult = new double[]{1,1,7,4,1,7,1,0};
        double[] result = instance.getPlaneYZ(x);
        assertEquals(expResult[0], instance.getValueFromPlaneYZ(result, 0, 0),1e-10);
        assertEquals(expResult[1], instance.getValueFromPlaneYZ(result, 1, 0),1e-10);
        assertEquals(expResult[2], instance.getValueFromPlaneYZ(result, 2, 0),1e-10);
        assertEquals(expResult[3], instance.getValueFromPlaneYZ(result, 3, 0),1e-10);
        assertEquals(expResult[4], instance.getValueFromPlaneYZ(result, 0, 1),1e-10);
        assertEquals(expResult[5], instance.getValueFromPlaneYZ(result, 1, 1),1e-10);
        assertEquals(expResult[6], instance.getValueFromPlaneYZ(result, 2, 1),1e-10);
        assertEquals(expResult[7], instance.getValueFromPlaneYZ(result, 3, 1),1e-10);        
    }

    /**
     * Test of getDeep method, of class Matrix.
     */
    @Test
    public void testGetPlane_int() {
        System.out.println("getPlane");
        int z = 0;
        double[] expResult = new double[]{1,1,1,1,2,5,7,3,2,4,1,2};
        double[] result = instance.getPlane(z);        
        assertEquals(expResult[0], instance.getValueFromPlaneXY(result, 0, 0),1e-10);
        assertEquals(expResult[1], instance.getValueFromPlaneXY(result, 1, 0),1e-10);
        assertEquals(expResult[2], instance.getValueFromPlaneXY(result, 2, 0),1e-10);
        assertEquals(expResult[3], instance.getValueFromPlaneXY(result, 0, 1),1e-10);
        assertEquals(expResult[4], instance.getValueFromPlaneXY(result, 1, 1),1e-10);
        assertEquals(expResult[5], instance.getValueFromPlaneXY(result, 2, 1),1e-10);
        assertEquals(expResult[6], instance.getValueFromPlaneXY(result, 0, 2),1e-10);
        assertEquals(expResult[7], instance.getValueFromPlaneXY(result, 1, 2),1e-10);
        assertEquals(expResult[8], instance.getValueFromPlaneXY(result, 2, 2),1e-10);
        assertEquals(expResult[9], instance.getValueFromPlaneXY(result, 0, 3),1e-10);
        assertEquals(expResult[10], instance.getValueFromPlaneXY(result, 1, 3),1e-10);
        assertEquals(expResult[11], instance.getValueFromPlaneXY(result, 2, 3),1e-10);
    }

    /**
     * Test of shape method, of class Matrix.
     */
    @Test
    public void testShape() {
        System.out.println("shape");
        String expResult = "3 x 4 x 2";
        String result = instance.shape();
        assertEquals(expResult, result);
    }

    /**
     * Test of size method, of class Matrix.
     */
    @Test
    public void testSize() {
        System.out.println("size");
        int expResult = 24;
        int result = instance.size();
        assertEquals(expResult, result);
    }

    /**
     * Test of getWidth method, of class Matrix.
     */
    @Test
    public void testGetWidth() {
        System.out.println("getWidth");
        int expResult = 3;
        int result = instance.getWidth();
        assertEquals(expResult, result);
    }

    /**
     * Test of getHeight method, of class Matrix.
     */
    @Test
    public void testGetHeight() {
        System.out.println("getHeight");
        int expResult = 4;
        int result = instance.getHeight();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDeep method, of class Matrix.
     */
    @Test
    public void testGetPlan_0args() {
        System.out.println("getPlan");
        int expResult = 2;
        int result = instance.getDeep();
        assertEquals(expResult, result);
    }   
}
