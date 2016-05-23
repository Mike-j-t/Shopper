# Shopper
Android Shopping list app
Written using Android Studio
Utilises SQLite (7 Tables, Shops, Aisles, Products, ProductUsage, Rules, ShopList & Appvalues).

**Features**
Ordered shopping list (ordered by Shop/Aisle/Product).
Automated and prompted addition to the shopping list according to rules.
Free from Ads and in-app purchases.

**Notes**
Shops referred to as Stores (attempt to avoid cofusion between verb Shop and object Shop).

**DB Structure**

**Shops**

| Column        | Type          | Description                                                                           |
|---------------|---------------|---------------------------------------------------------------------------------------|
| \_id          | Integer       | unique identifier (thus possible to have same shop details)                           |
| shopname      | Text          | name of the shop (store)                                                              |
| shoporder     | Integer       | order of the shop in the shopping list (lower #'s appear first)                       |
| shopstreet    | Text          | name of the street (reference/convenience/distinguising chain stores)                 |
| shopcity      | Text          | name of the city/area/suburb etc(reference/convenience/distinguising chain stores)    |
| shopstate     | Text          | name of the state/county etc (reference/convenience)                                  |
| shopphone     | Text          | phone number (reference/convenience)                                                  |
| shopnotes     | Text          | notes (reference/convenience)                                                         | 
 
_Note **id** and **shopname** only required. 
**shoporder** required but defaults to **100**.
**shopcity** and **shopstreet** are often displayed to allow stores in the same chain to be distinguished, so best given._
 
  
**Aisles**

| Column        | Type          | Description                                                                           |
|---------------|---------------|---------------------------------------------------------------------------------------|
| \_id          | Integer       | unique identifier (thus possible to have same aisle details)                          |
| aislename     | Text          | name of the aisle                                                                     |
| aisleorder    | Integer       | order of the aisle within the shop in the shopping list (lower #'s appear first)      |
| aisleshopref  | Text          | the _id of the shop that the aisle is in                                              |

 _Note all fields required, **shopref** is critical for referential integrity. **aisleorder** will default to 100_
  
**Products**

| Column | Type | Description |
|--------|------|-------------|
| \_id | Integer | Unique identifier |
| productname | Text | Name of the product |
| productaisleref | Integer | _Redundant (link table productusage links products to aisles)_ |
| productuses | Integer | _Redundant_ |
| product notes| Text | _Redundant_|
  
**ProductUsage**

| Column | Type | Description |
|--------|------|-------------|
| productaisleref | Integer | Aisle that this product has been assigned to **1st** part of **Primary Key** |
| productprodcutref | Integer | Product assigned to the aisle **2nd** and final part of **Primary Key** |
| productcost | Real | The cost of this product in this specific product/aisle combination |
| productbuycount | Integer | Number of times this product has been purchased (would be used for suggested rules feature )|
| productfirstbuydate | Integer | Date that this product was first purchased |
| productlastbuydate | Integer | The lastest Date that this product was purchased |
| mincost | Real | Unused |
| orderinaisle | Integer | order of the product within the aisle |

_Note that product, within the productusage table, refers to the unqiue product/aisle combination. 
That is a product (as per the product table) may be assigned to a number of aisles.
Hence the rather limited columns in the product table but a relatively high number of columns in the productusage table._

  
**Rules**

| Column | Type | Description |
|--------|------|-------------|
|\_id | Integer | Unique identifier |
| rulename | Text | The name of the Rule |
| ruletype | Integer | Unused |
| rulepromptflag | Integer | flag as to whether or not application of the rule should be prompted |
| ruleperiod | Integer | The base period/frequency used by the rule DAYS(0), WEEKS(1), FORTNIGHTS(2), MONTHS(3), QUARTERS(4) or YEARS(5) |
| rulemultiplier | Integer | period * multiplier determines the rules occurence frequency |
| ruleactiveon | Integer | The Date that the rule becomes active (_if that date or past when shopping list is opened then rule is applied and the product added to the list_)|
| ruleproductref | Integer | The \_id of the underlying product and thus also 2nd part of productusage Primary Key |
| ruleaisleref | Integer | The \_id of the aisle i.e. 1st part of the productusage Primary Key |
| ruleuses | Integer | The number of times that this rule has been applied (used to add a shopping list entry) |
| mincost | Real | unused |
| maxcost | Real | unused |
| rulesnumbertoget | Integer | The quantity of products to purchase |
  
**ShopList**

| Column | Type | Description |
|--------|------|-------------|
| \_id | Integer | Unique Shoppinbg List Entry Identifier |
| slproductid | Integer| \_id of the product |
| sldateadded | Integer | Date the entry was added |
| slnumbertoget | Integer | The number to purchase |
| sldone | Integer |  Flag to denote when the entry has been completed |
| productusageref | Integer | Unused, would replicate slproductid |
| aisleref | Integer | \_id of the aisle |

_Note An entry has three states incomplete, fully purchased (ie slnumbertoget is 0 )_
  
**AppValues**

| Column | Type | Description |
|--------|------|-------------|
| \_id | Integer | unique APPVALUE identifier |
| valuename | Text| Name of the value (not need not be unique e.g. for arrays) |
| valuetype| Text | The type of the value stored e.g. INTEGER, TEXT or REAL |
| valueint | Integer | The stored value, if it is of type INTEGER |
| valuereal | Real | The stored value, if it is of type REAL |
| valuetext | Text | The stored value, if it is of type TEXT |
| valueincludeinsettings | Integer| UNUSED but potential to use in automatically genereating user settings |
| valuesettingsinfo | Text | UNUSED but would be used in conjunction with valueincludeinsettings as text to display |

  _NOTE caters for storage of underlying values e.g. the list of periods used by rules_
  
**Database Code the ShopperDnHelper.java file**

All database related code is held in the one file ShopperDBHelper.java

This file consists of two prime sections class definitions for DB objects and the primary DBHelper code.
The class definitions define three classes that allow the DB structure to be built as objects namely DBColumn, DBTAble and DBDatabase.
Methods of these objects then build the SQL for the creation and or update of the actual tables via the creation of a psuedo DB schema which is compared against
what actually exists.  This comparison against what cuurently exists being undertaken at two points. In the mandatory DBHelper onCreate method and in
an added method onUpgrade, invoked everytime the app is run. This bypasses the standard SQLite version control methodology used.
Basically it caters for relatively simple database modifications.

**Class DBColumn - Properties**

| Property | Type | Use |
|-|-|-|
| usable| Boolean | Flag to indicate if the object is in a usable state |
| name | String | Column name |
| type | String | SQLite Type e.g. INT, REAL .... (note converted to TEXT, INTEGER, REAL or NUMERIC by the DBCOLumn's `simplifyColumnType` method)|
| primary\_index| Boolean | Wether or not this column is part of the primary index |
| default\_value | String | The default value of the column  as a string, if empty then no defualt value is set|
| order | Integer | unused at present but could facilitate ordering of columns accordingly |
| problem\_msg| String | For holding error messages |



This includes Classes :-
DBColumn						A class used for the define of table columns; an instance cconsists of a single column :-
									usable		- boolean flag indicating wheter or not this column is deemed usable
									name 		- string, that contains the name of the column
									type 		- string,(can be any of the types allowable by SQLite, however converted to stored type by simplifyColumnType(String type))
									primary_index - (boolean, defaults to false, true if the column is a primary index or part of a primary index)
									default_value - string, that is empty (not null) if no default value otherwise contains the default value for the column
									order		- integer, the order of the column NOTE ordering isn't implemented
									problem_msg - string, for messages regarding errors encountered
									
								5 constructors :-
									DBColumn()
									DBColumn(String column_name)
									DBColumn(String column_name, int sortorder)
									DBColumn(String column_name, String column_type, boolean primary_index, String default_value)
									DBColumn(String column_name,String column_type, boolean primary_index, String  default_value, int sortorder)
									
								3 Methods for setting properties :-
									setDBColumnName(String column_name)
									setDBColumnType(String column_type)
									setDefault_value(String default_value)
									
								10 methods for retrieving properties :-
									String getDBColumnName()
									String getDBColumnType()
									boolean getDBColumnIsUsable()
									boolean isDBColumnUsable() NOTE same as getDBColumnIsUsable()
									boolean getDBColumnIsPrimaryIndex()
									String getDBColumnDefaultValue()
									boolean isDBColumnPrimaryIndex() Note same as getDBColumnisPrimaryIndex()
									String getDBColumnProblemMsg()
									String getUnusableMsg() Note same as getDBColumnProblemMsg()
									int getSortorder()
									
								2 internal methods used during construction
									boolean checkDBColumnIsUsable(String caller) - Checks that column is usable and if not sets appropriate message(s)
									String simplifyColumnType(String type) (converts valuetype to core type e.g. INT, BIG INT etc returns INTEGER)
									
		example usage for the columns in the Aisles table (note uses predefined varialbes e.g. AISLES_COLUMN_ID ):-

					// Aisles Table and columns
					ArrayList<DBColumn> aislescolumns = new ArrayList<DBColumn>();					//arraylist to hold all columns for the aisles
					aislescolumns.add(new DBColumn(AISLES_COLUMN_ID,"INTEGER",true,""));			// _id column, INTEGER type, is primary index column with no default value		
					aislescolumns.add(new DBColumn(AISLES_COLUMN_NAME,"TEXT",false,""));			// name column, TEXT type, isn't primary, no default value
					aislescolumns.add(new DBColumn(AISLES_COLUMN_ORDER,"INTEGER",false,"100"));		// order columh, INTEGER type, isn't PI, defaults to 100
					aislescolumns.add(new DBColumn(AISLES_COLUMN_SHOP,"INTEGER",false,""));
					// Followed by (see DBTable classs below)
					DBTable aisles = new DBTable(AISLES_TABLE_NAME,aislescolumns);					// creates a DBTable instance called aisles which uses the ailses
					AISLES_COLUMN_COUNT = aisles.numberOfColumnsInTable();							// sets variable to number of columns in the table
					
					
DBTable							A class used for the definition of DB tables; and instance consists of the table name and an array of DBColumn instances
DB
  
  

