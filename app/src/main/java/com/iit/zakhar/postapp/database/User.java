package com.iit.zakhar.postapp.database;



import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "users")
public class User  {

    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_NICKNAME = "nickName";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_WEBSITE = "webSite";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_CITY = "city";

    @DatabaseField(generatedId = true)
    private Long id;
    @DatabaseField(canBeNull = false, dataType = DataType.STRING,  columnName =COLUMN_NAME)
    public String name;
    @DatabaseField(canBeNull = false, dataType = DataType.STRING, columnName =COLUMN_NICKNAME)
    public String nickName;
    @DatabaseField (canBeNull = false, dataType = DataType.STRING, columnName =COLUMN_EMAIL)
    public String email;
    @DatabaseField (canBeNull = false, dataType = DataType.STRING, columnName =COLUMN_WEBSITE)
    public String webSite;
    @DatabaseField (canBeNull = false, dataType = DataType.STRING, columnName =COLUMN_PHONE)
    public String phone;
    @DatabaseField (canBeNull = false, dataType = DataType.STRING, columnName =COLUMN_CITY)
    public String city;

    public User(){
    }

    public User(String name, String nickName, String email, String webSite, String phone, String city) {
        super();
        this.name = name;
        this.nickName = nickName;
        this.email = email;
        this.webSite = webSite;
        this.phone = phone;
        this.city = city;
    }
}

