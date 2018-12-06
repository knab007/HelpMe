package com.software.uottawa.helpme;

import static org.junit.Assert.*;
import org.junit.Test;

public class ServiceTest {

    @Test

    public void CheckServiceTitle() {
        Service aService = new Service("11", "plombier", "1", "Novak", "debouche les toilettes", "15");

        assertEquals("check Service Title","plombier", aService.getTitle());
    }

    @Test
    public void CheckServiceId() {
        Service aService = new Service("11", "plombier", "1", "Novak", "debouche les toilettes", "15");

        assertEquals("check Service Id","11", aService.getId());
    }

    @Test
    public void CheckServiceCreatorName() {
        Service aService = new Service("11", "plombier", "1", "Novak", "debouche les toilettes", "15");

        assertEquals("check Service Creator Name","Novak", aService.getCreatorName());
    }

    @Test
    public void CheckServiceDescription() {
        Service aService = new Service("11", "plombier", "1", "Novak", "debouche les toilettes", "15");

        assertEquals("check Service Description","debouche les toilettes", aService.getDescription());
    }

    @Test
    public void CheckServiceHourlyRate() {
        Service aService = new Service("11", "plombier", "1", "Novak", "debouche les toilettes", "15");

        assertEquals("check Service HourlyRate","15", aService.getRate());
    }


}




