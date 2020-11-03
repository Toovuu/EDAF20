package krusty;

public class DatabaseTest {

    public static void main(String[] args) {
        Database db = new Database();
        db.connect();
        System.out.println(db.reset(null,null));
        /*
        System.out.println(db.createPallettest("nono"));
        System.out.println(db.createPallettest("Amneris"));
        System.out.println(db.createPallettest("Tango"));
        System.out.println(db.createPallettest("Amneris"));
        System.out.println(db.createPallettest("Nut ring"));
        System.out.println(db.createPallettest("Nut cookie"));
        System.out.println(db.getCustomers(null,null));
        System.out.println(db.getCookies(null,null));
        System.out.println(db.getRawMaterials(null,null));
        System.out.println(db.getRecipes(null,null));
        System.out.println(db.getPalletstest(null,null, null,null));
        System.out.println(db.getPalletstest("2020-03-24",null, null,null));
        System.out.println(db.getPalletstest("2020-03-22",null, null,null));
        System.out.println(db.getPalletstest("2020-03-26",null, null,null));
        System.out.println(db.getPalletstest(null,"2020-03-24", null,null));
        System.out.println(db.getPalletstest(null,"2020-03-26", null,null));
        System.out.println(db.getPalletstest(null,"2020-03-22", null,null));
        System.out.println(db.getPalletstest(null,null, "nono",null));
        System.out.println(db.getPalletstest(null,null, "Amneris",null));
        System.out.println(db.getPalletstest(null,null, null,"no"));
        System.out.println(db.getPalletstest(null,null, null,"yes"));
        */
        db.closeConnection();
    }
}
