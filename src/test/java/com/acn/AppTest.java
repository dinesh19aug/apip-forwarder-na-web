package com.acn;


import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.apip.forwarder.dao.ApipDaoImpl;
import com.apip.forwarder.utility.ForwarderUtilities;

public class AppTest {

    
    @Test
    public void testLastFour() {
        try {
            assertTrue(ForwarderUtilities.getLastFour(null) == null);
            assertTrue(ForwarderUtilities.getLastFour("") == null);
            assertTrue(ForwarderUtilities.getLastFour("123").equals("123"));
            assertTrue(ForwarderUtilities.getLastFour("1234").equals("1234"));
            assertTrue(ForwarderUtilities.getLastFour("A1234").equals("1234"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Shouldn't reach here!");
        }
    }
    
    @Test
    public void testMapNullCast() {
        try {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("test", null);
            String test = (String) map.get("test");
            assertTrue(test == null);
            Integer test2 = (Integer) map.get("test");
            assertTrue(test2 == null);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Shouldn't reach here!");
        }
    }
    
    @Test
    public void testBigDecimalDB() {
        try {
            Map<String, Object> map = new HashMap<String, Object>();
            BigDecimal result = null;
            map.put("test", "$3.49");
            result = ApipDaoImpl.getBigDecimalParameterForDB(map, "test");
            assertTrue(result.doubleValue() == 3.49d);
            map.put("test", "3.49");
            result = ApipDaoImpl.getBigDecimalParameterForDB(map, "test");
            assertTrue(result.doubleValue() == 3.49d);
            map.put("test", 3);
            result = ApipDaoImpl.getBigDecimalParameterForDB(map, "test");
            assertTrue(result.intValue() == 3);
            map.put("test", 3l);
            result = ApipDaoImpl.getBigDecimalParameterForDB(map, "test");
            assertTrue(result.longValue() == 3l);
            map.put("test", 3.49d);
            result = ApipDaoImpl.getBigDecimalParameterForDB(map, "test");
            assertTrue(result.doubleValue() == 3.49d);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Shouldn't reach here!");
        }
    }
    
}
