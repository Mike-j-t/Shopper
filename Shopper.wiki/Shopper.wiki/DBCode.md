# Database Code the ShopperDBHelper.java file

All database related code is held in the one file **ShopperDBHelper.java**

There are two primary sections, class definitions for DB objects and the primary **DBHelper** code.

## An Overview of the Classes (DBColumn, DBTable and DBDatabase)

The class definitions define three classes that allow a pseudo DB structure/schema to be built, namely **DBColumn**, **DBTAble** and **DBDatabase**. The schema is then used to build or amend the Database via methods that generate appropriate **SQL** after comparison with the existing database (_new database created = no existing tables so all tables defined in the pseudo schema are added (of course includes the columns)_). If columns or tables don't exist but others do then the non-existent tables/columns will be added. Note that only additions are made i.e. no attempts are made to delete apparently redundant tables or columns.

## Creating and Amending the Database Tables
Note that the methodology used differs from the frequently used method of coding the table creation and amendment in the DBHelper's **onCreate** and **onUpgrade** methods. 

### Initial table creation via the DBHelper's onCreate method
Method `onCreate` is still used to create the tables however methods `generateDBSchema` and `actionDBBuildSQL` are used to create the tables. Method `generateDBSchema` creates the pseudo schema to ensure that it is usable (via the `isDBDatabaseUsable` method). Method `actionDBBuildSQL` actually builds the tables according to the pseudo schema.
_Note all methods mentioned are methods of the DBDatabase Class._

### Amendments via the introduced onExpand method
Method `onUpgrade` and it's version management methodology is not used, rather the `onExpand` method (_added to the DBHelper_) is used; `onExpand` being called whenever the app is started. The `onExpand` method replicates the `onCreate` generation of the pseudo schema and build. The build would typically do nothing as the SQL for the table creation includes `IF NOT EXISTS`. If a new table were added then it would be created (note that SQL is only built for new tables anyway, as the pseudo schema is compared against the current Database).

***

## Class DBColumn

### Class DBColumn - Properties

| Property | Type | Use |
|:---------|:-----|:----|
| usable| Boolean | Flag to indicate if the object is in a usable state |
| name | String | Column name |
| type | String | SQLite Type e.g. INT, REAL (note converted to TEXT, INTEGER, REAL or NUMERIC by the DBCOLumn\'s simplifyColumnType method)|
| primary\_index| Boolean | Flag primary index or not |
| default\_value | String | The default value of the column  as a string, if empty then no default value is set|
| order | Integer | unused at present but could facilitate ordering of columns accordingly |
| problem\_msg| String | For holding error messages |

### Class DBColumn - Constructors

| Constructor | Notes |
|:---|:---|
| DBColumn() | Should not be used unless setting properties. _Use without setting properties would result in an unusable Database._ Message would be `WDBC0003 - Uninstantiated - Use at least setDBColumnType AND setDBColumnName methods. Caller=(DBColumn (Default))`|
| DBColumn(String column_name) | Should not be used with caution as type is set to TEXT.|
| DBColumn(String column_name, int sortorder) | Same as `DBColumn(String column_name)` above, as sort order is curently redundant. |
| DBColumn(String column_name, String column_type, boolean primary_index, String default_value) | Recommended |
| DBColumn(String column_name,String column_type, boolean primary_index, String  default_value, int sortorder) | Same as `DBColumn(String column_name, String column_type, boolean primary_index, String default_value)` but with superfluous sort order parameter. |

### Class DBColumn - Setter methods

| Method | Notes |
|:---|:---|
| setDBColumnName(String column_name) | Sets the name (column name). |
| setDBColumnType(String column_type) | Sets the type (column type). |
| setDefault_value(String default_value) | Sets the default\_value (column's default value) |

_**Note** Although all SQLite column types are accepted (an exception being BLOB's), they are converted to the basic/raw (affinities )types as per [Datatypes In SQLite Version 3 - 3.2. Affinity Name Examples](https://www.sqlite.org/datatype3.html). This is undertaken by method `simplifyColumnType`_

### Class DBColumn - Getter Methods

| Method | Notes |
|:---|:---|
| String getDBColumnName() | returns the name (Column Name) |
| String getDBColumnType() | returns the type (Column type) |
| boolean getDBColumnIsUsable() | returns true is the column is usable |
| boolean isDBColumnUsable() | alias/same as `getDBColumnIsUsable()` |
| boolean getDBColumnIsPrimaryIndex() | returns true id the column is a Primary Index column |
| boolean isDBColumnPrimaryIndex() | alias/same as `getDBColumnIsPrimaryIndex()` |
| String getDBColumnDefaultValue() | returns the default value (empty string if none specified) |
| String getDBColumnProblemMsg() | returns the message associated with the column not being usable. If usable empty string is returned. |
| String getUnusableMsg() | alias/same as `getDBColumnProblemMsg()` |
| int getSortorder() | returns the sortorder of the column |

### Class DBColumn - Miscellaneous methods

| Method | Notes |
|:---|:---|
| boolean checkDBColumnIsUsable(String caller) | Checks that column is usable and if not sets appropriate message(s) for the insatnce. String Caller is included in the message. |
| String simplifyColumnType(String type) | Converts SQLite TYPES to the type's underlying affinity as per [Datatypes In SQLite Version 3 - 3.2. Affinity Name Examples](https://www.sqlite.org/datatype3.html) e.g. INT, BIG INT etc returns INTEGER |								

### Class DBColumn example usage

_example usage for the columns in the Aisles table (note uses predefined variables e.g. AISLES_COLUMN_ID ):-_

    // Aisles Table and columns
    ArrayList<DBColumn> aislescolumns = new ArrayList<DBColumn>();					//arraylist to hold all columns for the aisles
    aislescolumns.add(new DBColumn(AISLES_COLUMN_ID,"INTEGER",true,""));			// _id column, INTEGER type, is primary index column with no default value		
    aislescolumns.add(new DBColumn(AISLES_COLUMN_NAME,"TEXT",false,""));			// name column, TEXT type, isn't primary, no default value
    aislescolumns.add(new DBColumn(AISLES_COLUMN_ORDER,"INTEGER",false,"100"));		// order columh, INTEGER type, isn't PI, defaults to 100
    aislescolumns.add(new DBColumn(AISLES_COLUMN_SHOP,"INTEGER",false,""));         // Followed by (see DBTable classs below)
    DBTable aisles = new DBTable(AISLES_TABLE_NAME,aislescolumns);					// creates a DBTable instance called aisles which uses the ailses
    AISLES_COLUMN_COUNT = aisles.numberOfColumnsInTable();							// sets variable to number of columns in the table

***
## Class DBTable
### Class DBTable - Properties

| Property | Type | Use |
|:---|:---|:---|
| usable| Boolean | Flag to indicate if the object is in a usable state |
| table\_name | String | The DB table name |
| table_columns | DBColumn Arraylist| List columns as DBColumn objects |
| problem\_msg | String | Holds error messages in the table is unusable |

### Class DBTable - Constructors

| Constructor | Notes |
|:---|:---|
| DBTable() | Will instantiate unusable instance.  Problem message would be set to `WDBT0004 - Uninstantiated - Use addDBColumnToDBTable to add at least 1 usable DBColumn or Use addDBColumnsToDBTable to add at least 1 usable DBColumn or Use AddMultipleColumnstoDBTable to add at least 1 usable DBColumn. Note any unusable DBcolumn will render table unusable. Also use setDBTableTableName to set the Table Name. Caller=DBTable (Default Constructor)` |
| DBTable(String table_name) | Will instantiate unusable instance. Problem Message would be set to `WDBT0005 - Partially Instantiated - Use addDBColumnToDBTable to add at least 1 usable DBColumns or Use addDBColumnsToDBTable to add at least 1 usable DBColumn or Use AddMultipleColumnstoDBTable to add at least 1 usable DBColumn. Note any unusable DBColumn will render table unusable. Caller=DBTable (Table Name only Constructor)`. Can also issue Message `WDTB0006 -  Invalid Table Name - Must be at least 1 character in length. Caller=(DBTable (table_name))`. This in addition to the `WDBT0005` Message.|
| DBTable(String table_name, DBColumn table_column) | Only caters for 1 column. Problem Message will end with `DBTable (table_name, table_column (singular))`. See `checkDBTableIsUsable` for potential messages. |
| DBTable(String table_name, ArrayList<DBColumn> table_columns) | Recommended Constructor. Problem Message will end with `DBTable Full Constructor`. See `checkDBTableIsUsable` for potential messages. |

### Class DBTable - Setter Methods

| Method | Notes |
|:---|:---|
| AddDBColumnToDBTable(DBColumn dbcolumn) | Sets table_columns to a single column instance. Limited use. |
| AddDBColumnstoDBTable(ArrayList<DBColumn> dbcolumns)| Sets table_columns to an arraylist of DBColumn objects. |
| AddMultipleColumnstoDBTable(ArrayList<DBColumn> dbcolumns)| alias/same as `AddDBColumnstoDBTable` |
| setDBTableName(String table_name) | Sets the table_name |

### Class DBTable - Getter Methods

| Method | Notes |
|:---|:---|
| String getDBTableName() | Returns table_name as String. |
| boolean isDBTableUsable() | Returns true if the DBTable is usable. False if not. _Note an unusable underlying volumn will render the table unusable._ |
| int numberOfColumnsInTable() | Returns the number of columns in the table as an integer. |
| ArrayList<DBColumn> getTableDBColumns() | Returns an arraylist of DBColumn objects according to the defined columns. |
| String getDBTableProblemMsg() | Gets the Problem Message for the table, if any, else empty string. |
| String getAllDBTableProblemMsgs() | Gets the table's Problem Message and any underlying Problem Messages for the underlying columns. |
| boolean checkDBTableIsUsable(String caller) | Returns the usable state of the table, also checking the  underlying volumns via `anyEmptyDBColumnsInDBTable` _as below_ |
| boolean anyEmptyDBColumnsInDBTable(String caller) | Returns false if there are no underlying table columns, or false if there are underlying columns and they are unusable. Else returns true. |

### Class DBTable - Miscellaneous/Specialised Methods

| Method | Notes |
|:---|:---|
| String getSQLCreateString(SQLiteDatabase db) | Returns the SQL to create the table. _Note SQL starts with `CREATE  TABLE IF NOT EXISTS`, so tables will only be created if they don't actually exist._ __Note! this method checks for the existence of the real DB table and returns empty string if it exists.__|
| ArrayList<String> getSQLAlterToAddNewColumns(SQLiteDatabase db) | Returns the SQL to alter the table and add a column or coulmns if the real DB table exists. Returns an empty string if the table doesn't exist. Furthermore if a column already exists in the real DB, then the SQL for that column will not be generated and returned. |


***

## Class DBDatabase
### Class DBDatabase - Properties

| Property | Type | Use |
|:---|:---|:---|
| usable | boolean| Flag to indicate if the object is in a usable state |
| database_name | String | Name of the Database |
| database_tables | DBTable type ArratList | The underlying tables of the database (and columns from the tables) |

###Class DBDatabase - Constructors

| Constructor | Notes |
|:---|:---|
| DBDatabase() | Will instantiate unusable instance.  Problem message would be set to `WDBD0100 - Uninstantiated - Use setDBDatabaseName to set the Database Name. Use addDBTableToDBDatabase to add at least 1 Table or Use addDBTablesToDBDatabase to add at least 1 Table or Use addMultipleTablesToDBDatabase to add at least 1 Table` |
| DBDatabase(String database_name) | Instantiate unusable instance (only database name set).   Problem message would be set to `WDBD0101 - Partially Instantiated - Use addDBTableToDBDatabase to add at least 1 Table or Use addDBTablesToDBDatabase to add at least 1 Table or Use addMultipleTablesToDBDatabase to add at least 1 Table` |
| DBDatabase(String database_name, DBTable database_table) | Instantiate single table database. Usability would depend on underlying table which is checked via `checkDBDatabaseIsUsable`. |
| DBDatabase(String database_name, ArrayList<DBTable> database_tables | Instantiate usable instance with 1 or more tables (note only usable if all tables and columns are usable)|

### Class DBDatbase - Setter Methods

| Method | Notes |
|:---|:---|
| setDBDatabaseName(String database_name) | Set the Database name |
| addDBTableToDBDatabase(DBTable database_table) | Add a single table to the database |
| addDBTablesToDBDatabase(ArrayList<DBTable> database_tables) | Add 1 or more tables to the database via an ArrayList of DBTable objects |

### Class DBDatabase - Getter Methods

| Method | Notes |
|:---|:---|
| boolean isDBDatabaseUsable() | true if the Databse is flagged as usable, otherwise false. |
| long numberOfTablesinDBDatabase() | The number of Tables in the database |
| String getDBDatabaseName() | The Database name |
| String getDBDatabaseProblemMsg() | The Database's Problem message, empty if none |
| String getAllDBDatabaseProblemMsgs() | All Problem messages including any for tables and columns |
| boolean checkDBDatabaseIsUsable(String caller) | true if database is usable, else false. Note that this performs checks rather than just returning the usable value. |
| boolean anyEmptyDBTablesInDBDatabase(String caller) | true if all underlying tables cnd columns are usable, fasle if not and also false if there are no tables defined |

### Class DBDatabase - Miscellaneous Methods

| Method | Notes |
|:---|:---|
| ArrayList<String> (SQLiteDatabase db) | Create an ArrayList of strings containing the SQL to build the database |
| actionDBBuildSQL(SQLiteDatabase db) | Builds the Database after invoking `generateDBBuildSQL` |
| ArrayList<String> generateDBAlterSQL(SQLiteDatabase db) | Create and ArrayList of strings containing the SQL to Alter and existing database |
| actionDBAlterSQL(SQLiteDatabase db) | Alters the database after invoking `generateDBAlterSQL` |

### DBDatabase - Example Code

#### DBDatabase - Example Code - Build the Database Schema in preparation for the it being used to cerate or amend the Real Database.

    // Database
    ArrayList<DBTable> databasetables = new ArrayList<DBTable>();
    databasetables.add(shops);
    databasetables.add(aisles);
    databasetables.add(products);
    databasetables.add(productusage);
    databasetables.add(rules);
    databasetables.add(shoplist);
    databasetables.add(values);
    return  new DBDatabase(DATABASE_NAME,databasetables);
 
*Note see previous example for how the databasetables are generated. *
*Note this is part of a method called generateDBSChema; hence the return statement.*

#### DBDatabase - Example Code - Actually creating the database 

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Generate the schema that will be used aas the basis for
        DBDatabase shopper = generateDBSchema(db);
        if (!shopper.isDBDatabaseUsable()) {
            Log.e(Constants.LOG, "Unable to Create Tables -" +
                    "The design(schema)has been marked as unusable. " +
                    "- This is a Development Issue. Please contact the Developer.");
            return;
        }

        ArrayList<String> SQLBuildStatements = new ArrayList<String>(shopper.generateDBBuildSQL(db));

        // Build the Tables according to the generated schema
        shopper.actionDBBuildSQL(db);

        Log.i(Constants.LOG, "Shopper Database Tables created.");
    }


_Note! The initial part of the code checks that the Pseudo Schema is usable. The call to build the ArrayList SQLBuildStatements is for debugging purposes and is not necessary. So without error checking and debugging code the above could be :-_


    DBDatabase shopper = generateDBSchema(db);
    shopper.actionDBBuildSQL(db);