package com.software.uottawa.helpme;
import static org.junit.Assert.*;
import org.junit.Test;


public class UserTest {

    @Test
    public void checkUserFirstName() {

        User aUser = new User("1","Samir","Tchakounte","Sam@gmail.com","homeOwner");
        assertEquals("Check User firstName", "Samir",aUser.getFirstName());

    }

    @Test
    public void checkUserLastName() {

        User aUser = new User("1","Samir","Tchakounte","Sam@gmail.com","homeOwner");
        assertEquals("Check User LastName", "Tchakounte",aUser.getLastName());

    }

    @Test
    public void checkUserEmail() {

        User aUser = new User("1","Samir","Tchakounte","Sam@gmail.com","homeOwner");
        assertEquals("Check User Email", "Sam@gmail.com",aUser.getEmail());

    }

    @Test
    public void checkUserType() {

        User aUser = new User("1","Samir","Tchakounte","Sam@gmail.com","homeOwner");
        assertEquals("Check User Type", "homeOwner",aUser.getTypeOfUser());

    }

    @Test
    public void checkUserId() {

        User aUser = new User("1","Samir","Tchakounte","Sam@gmail.com","homeOwner");
        assertEquals("Check User Id", "1",aUser.getId());

    }
}
