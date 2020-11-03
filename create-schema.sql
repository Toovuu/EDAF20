USE Krusty;

DROP TABLE IF EXISTS Customers;
DROP TABLE IF EXISTS Products;
DROP TABLE IF EXISTS Ingredients;
DROP TABLE IF EXISTS Recipes;
DROP TABLE IF EXISTS Orders;
DROP TABLE IF EXISTS Pallets;
DROP TRIGGER IF EXISTS palletIngredients;

CREATE TABLE Customers(
Customer_Name varchar(200),
Address varchar(200) not null,
primary key(Customer_Name)
);

CREATE TABLE Products(
Product_Name varchar(200),
primary key(Product_Name)
);

CREATE TABLE Ingredients(
Ingredient_Name varchar(200),
Amount Integer,
Unit varchar(50),
primary key(Ingredient_Name)
);

CREATE TABLE Recipes(
Product_Name varchar(200),
Ingredient_Name varchar(200),
Amount Integer not null,
foreign key (Product_Name) references Products(Product_Name),
foreign key (Ingredient_Name) references Ingredients(Ingredient_Name)
);

CREATE TABLE Orders(
Order_ID Integer AUTO_INCREMENT,
Customer_Name varchar(200) not null,
Order_Date Date not null,
Delivery_Date Date not null,
primary key(Order_ID),
foreign key(Customer_Name) references Customers(Customer_Name)
);

CREATE TABLE Pallets(
Pallet_ID Integer AUTO_INCREMENT,
Product_Name varchar(200) not null,
Order_ID Integer DEFAULT NULL,
Production_Date Date DEFAULT (CURRENT_DATE),
Blocked BOOLEAN DEFAULT FALSE,
primary key(Pallet_ID),
foreign key(Product_Name) references Products(Product_Name),
foreign key(Order_ID) references Orders(Order_ID)
);

create trigger palletIngredients
after insert on Pallets
for each row
update recipes join ingredients
on recipes.ingredient_name=ingredients.ingredient_name
set ingredients.amount = ingredients.amount-recipes.amount*54
where product_name = new.product_name;







