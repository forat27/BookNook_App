package com.example.myapplicationfinal;

public class book_card {
    private String location;
    private String Title;
    private String Price;
    private String Name;
    private String Img;
    private String userId;



    public book_card(String title, String name, String price, String img,String userId,String location) {
        this.Title = title;
        this.Price = price;
        this.Name = name;
        this.Img = img;
        this.userId = userId;
        this.location = location;
    }

    public String getUserId() {return userId;}
    public String getImg() {return Img;}
    public String getTitle() {return Title;}
    public String getName() {return Name;}
    public String getPrice() {return Price+"₪";}
    public String getLocation() {
        return location;
    }




    public void setImg(String img) {Img = img;}
    public void setTitle(String title){Title=title;}
    public void setName(String id){Name=Name;}
    public void setPrice(String price){Price=price;}
    public void setLocation(String location){this.location = location;}
}
