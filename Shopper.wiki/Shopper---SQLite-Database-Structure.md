# Shopper - SQLite DataBase Structure

## An Overview of the Database Tables used by Shopper
The Shopper database consists of 7 tables. **Shops**, **Aisles**, **Products**, **ProductUsage**, **Rules**, **ShopList** & **AppValues**.

There can be any number of shops (referred to as stores to avoid confusion with the verb (to shop) and the noun (a shop).
A **Shop** can have an **Aisle** or more.
There can be any number of **Products**.
An **Aisle** can contain a number of **Products**, the same **Product** can be in multiple **Aisles**. It is the **ProductUsage** table that holds instances of **Products** that are in **Aisles** (and thus in **Shops**).
**ShopList** contains **Products** that are assigned to **Aisles** but only those selected by the user (manually or via **Rules**).
There can be any number of **Rules**. However, a **Rule** can only be made for an existing **ProductUsage**. **Rules** cater for automated and semi-automated (prompted) regular additions to the **ShopList**.
**AppValues** contains data that is used internally e.g. The list of periods that can be used for the frequency that **Rules** add to the **ShopList**.

## Shopper's Tables and Columns

### Shops Table

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
 
  
### Aisles Table

| Column        | Type          | Description                                                                           |
|---------------|---------------|---------------------------------------------------------------------------------------|
| \_id          | Integer       | unique identifier (thus possible to have same aisle details)                          |
| aislename     | Text          | name of the aisle                                                                     |
| aisleorder    | Integer       | order of the aisle within the shop in the shopping list (lower #'s appear first)      |
| aisleshopref  | Text          | the _id of the shop that the aisle is in                                              |

 _Note all fields required, **shopref** is critical for referential integrity. **aisleorder** will default to 100_
  
### Products Table

| Column | Type | Description |
|--------|------|-------------|
| \_id | Integer | Unique identifier |
| productname | Text | Name of the product |
| productaisleref | Integer | _Redundant (link table productusage links products to aisles)_ |
| productuses | Integer | _Redundant_ |
| product notes| Text | _Redundant_|
  
### ProductUsage Table

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

### Rules Table

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
  
### ShopList Table

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
  
### AppValues Table

| Column | Type | Description |
|--------|------|-------------|
| \_id | Integer | unique APPVALUE identifier |
| valuename | Text| Name of the value (not need not be unique e.g. for arrays) |
| valuetype| Text | The type of the value stored e.g. INTEGER, TEXT or REAL |
| valueint | Integer | The stored value, if it is of type INTEGER |
| valuereal | Real | The stored value, if it is of type REAL |
| valuetext | Text | The stored value, if it is of type TEXT |
| valueincludeinsettings | Integer| UNUSED but potential to use in automatically generating user settings |
| valuesettingsinfo | Text | UNUSED but would be used in conjunction with valueincludeinsettings as text to display |

  _NOTE caters for storage of underlying values e.g. the list of periods used by rules_
 
***
***