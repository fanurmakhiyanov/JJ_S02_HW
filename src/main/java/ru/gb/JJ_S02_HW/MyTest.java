package ru.gb.JJ_S02_HW;

public class MyTest {

    @BeforeEach(-1)
    public void initTestLow() {
        System.out.println("initTest запущен priority:-1");
    }

    @Test(1)
    private void firstTest() {
        System.out.println("firstTest запущен priority:1");
    }

    @Test(2)
    void secondTest() {
        System.out.println("secondTest запущен priority:2");
    }

    @BeforeEach(1)
    private void initTestMid() {
        System.out.println("initTest запущен priority:1");
    }

    @Skip
    @AfterEach(3)
    void endTestLow() {
        System.out.println("endTest запущен priority:3");}

    @AfterEach(5)
    public void endTestHigh() {
        System.out.println("endTest запущен priority:5");}
}
