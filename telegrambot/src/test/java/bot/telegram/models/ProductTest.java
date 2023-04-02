package bot.telegram.models;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    static Product product;

    @BeforeAll
    static void prepareData() {
        product = new Product();
    }
    @Test
    void testToString() {
        product.setName("1");
        product.setCategory("2");
        product.setPrice(3.0);
        product.setUrl("4.com");
        String temp = """
                name: 1
                category: 2
                price: 3.0
                4.com""";
        assertEquals(temp, product.toString());
    }

    @Test
    void getId() {
        product.setId(24);
        assertEquals(24, product.getId());
    }

    @Test
    void getName() {
        product.setName("123");
        assertEquals("123", product.getName());
    }

    @Test
    void getCategory() {
        product.setCategory("CAT");
        assertEquals("CAT", product.getCategory());
    }

    @Test
    void getDescription() {
        product.setDescription("long description");
        assertEquals("long description", product.getDescription());
    }

    @Test
    void getPrice() {
        product.setPrice(123.5);
        assertEquals(123.5, product.getPrice());
    }

    @Test
    void getUrl() {
        product.setUrl("123.com");
        assertEquals("123.com", product.getUrl());
    }

    @Test
    void getLast_updated() {
        long temp = new Date().getTime();
        product.setLast_updated(temp);
        assertEquals(temp, product.getLast_updated());
    }

    @Test
    void getPicture_url() {
        product.setPicture_url("111.com");
        assertEquals("111.com", product.getPicture_url());
    }

    @Test
    void setId() {
        product.setId(21);
        assertEquals(21, product.getId());
    }

    @Test
    void setName() {
        product.setName("123");
        assertEquals("123", product.getName());
    }

    @Test
    void setCategory() {
        product.setCategory("CAT");
        assertEquals("CAT", product.getCategory());
    }

    @Test
    void setDescription() {
        product.setDescription("long description");
        assertEquals("long description", product.getDescription());
    }

    @Test
    void setPrice() {
        product.setPrice(123.5);
        assertEquals(123.5, product.getPrice());
    }

    @Test
    void setUrl() {
        product.setUrl("123.com");
        assertEquals("123.com", product.getUrl());
    }

    @Test
    void setLast_updated() {
        long temp = new Date().getTime();
        product.setLast_updated(temp);
        assertEquals(temp, product.getLast_updated());
    }

    @Test
    void setPicture_url() {
        product.setPicture_url("111.com");
        assertEquals("111.com", product.getPicture_url());
    }
}