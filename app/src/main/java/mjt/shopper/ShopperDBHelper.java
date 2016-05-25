package mjt.shopper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.*;

/**
 * Created by Mike092015 on 28/01/2016.
 *
 * ShopperDBHelper - SQLite3 Database Helper Extended/Cutomised
 *
 * OOTAD out_of-the-actual-database classes/methods/schema
 * AKA relatively simple database changes
 * =================================================================================================
 * Also includes complemetary Database classes DBDatabase, DBTable & DBColumn and methods
 * This allowing an out-of-the-actual-database (OOTAD) schema that can be used for relatively simple table
 * and column definition, thier use in the SQLite onCreate, onUpgrade and onDowngrade if overidden.
 * methods actionDBAlterSQL and actionDBBuildSQL can be used to implment and alter actual schema
 * based upon the OOTAD.
 *
 * More specifically actionDBBuilSQL generates and actions CREATE ??? IF NOT EXISTS ... SQL
 * so if a new table is added to the respective OOTAB (DBColumn objects fed into a DBTable object)
 * that table will automatically be created.
 * Similarily actionDBAlterSQL generates ALTER SQL to ADD columns. However, the underlying process
 * compares the actual schema with the proposed new schema (no changes = nothing done)
 *
 * In brief create a String Arraylist of DBColumn objects eg :-
 *
 * ArrayList<DBColumn> <my_table_columns> = new ArrayList<DBColumn>();
 *
 * <my_table_columns>.add(DBColumn(<my_column_name>,     // String
 *      <my_column_type>,                                // String according to SQLite Datatypes  #1
 *      <primary_index_boolean>,                         // true = this will be primary index col #2
 *      <my_default_vale_if_one_else_empty_string>););   // String ("" to not specify default)
 *  ........
 *  ArrayList<DBTable> <my_list_of_tables> = new ArrayList<DBTable>();
 *
 * <my_list_of_tables>.add(DBTable(<my_table_name>,<my_table_columns>);
 * ........
 * DBDatabase <my_database_schema> = new DBDatabase(<my_database_name>,<>)                        #3
 *
 * <my_database_schema>.actionDBBuildSQL(<real_database>); // Build or add new tables
 * <my_database_schema>.actionDBALterSQL(<real_database>); // Add non existing columns to existing
 *      would have actionDBBuildSQL in onCreate or it's equivalent
 *      would have both in onUpgrade or it's equivalent
 *
 *      NOTE!!! there is a flag that is propogated  and from DBColumn through DBTable to DBDatabase
 *      this should be checked as it can indicate potential failures such as specifying an invalid
 *      DATATYPE, no table name. (note will be set to false throught the levels)
 *      DBDatabase method isDBDatabaseUsable() returns boolean true if useable
 *      e.g if(<mydatabase>.isDBDatabaseUsable()) {<mydatabase>.actionDBBuildSQL(<real_database>); }
 *
 *      Reason(s) for the unusable state can be obtained from problemmsg property via
 *      get<class>ProblemMessage() for the message(s) stored at that class/object level or via
 *      get<class>AllProblemMessages() for traversal of the underlying objects within the DBDatabase
 *       DBTable, DBColumn hierarchy (no ALL version for DBColumn class as it's the lowest level).
 *
 *
 * #1 All types should be catered for. However, they are then converted to thier more primitive
 *    types INTEGER, TEXT, REAL or NUMERIC.
 * #2 Supports multiple primary index columns
 * #3 At present the database name is not used externally as a comparison with the actual, so it
 *    need not be the same.
 *
 *    Both actionDBBuildSQL and actionDBAlterSQL have respective underlying generate...SQLStatements
 *    methods. These can be used when devloping to then grab and check the generated SQL they can
 *    then be copied and pasted to check/confirm that the results are as expected (eg copy and paste
 *    them into SQLiteManger for Firefox from Debug).
 *
 *    WARNING!!! if you were to add a table and then use actionDBAlterSQL that was not preceeded
 *    by actionDBBuildSQLyou would get an exception as the ALTER statements for the new table would
 *    generate even though the table did not exist
 *    NOTE!!! ALTER statements should now not be generated if the table to which they relate does
 *    not exist in the actual database
 */

class DBDatabase {
    private boolean usable;
    private String database_name;
    private ArrayList<DBTable> database_tables;
    private String problem_msg;

    public DBDatabase() {
        this.usable = false;
        this.database_name = "";
        this.database_tables = new ArrayList<DBTable>();
        this.problem_msg = "WDBD0100 - Uninstantiated - " +
                "Use setDBDatabaseName to set the Database Name. " +
                "Use addDBTableToDBDatabase to add at least 1 Table or " +
                "Use addDBTablesToDBDatabase to add at least 1 Table or " +
                "Use addMultipleTablesToDBDatabase to add at least 1 Table";
    }

    public DBDatabase(String database_name) {
        this();
        this.database_name = database_name;
        this.problem_msg = "WDBD0101 - Partially Instantiated -  " +
                "Use addDBTableToDBDatabase to add at least 1 Table or " +
                "Use addDBTablesToDBDatabase to add at least 1 Table or " +
                "Use addMultipleTablesToDBDatabase to add at least 1 Table";
    }

    public DBDatabase(String database_name, DBTable database_table) {
        this();
        this.database_name = database_name;
        this.problem_msg = "";
        this.database_tables.add(database_table);
        this.checkDBDatabaseIsUsable("DBDatabase (database_name, database_table (singular))");
    }

    public DBDatabase(String database_name, ArrayList<DBTable> database_tables) {
        this();
        this.database_name = database_name;
        this.problem_msg = "";
        this.database_tables.addAll(database_tables);
        this.checkDBDatabaseIsUsable("DBDatabse Full Contructor");
    }
    public boolean isDBDatabaseUsable() { return this.usable; }
    public long numberOfTablesinDBDatabase() { return this.database_tables.size(); }

    public void setDBDatabaseName(String database_name) {
        this.database_name = database_name;
    }

    public String getDBDatabaseName() { return this.database_name; }
    public String getDBDatabaseProblemMsg() { return this.problem_msg; }
    public String getAllDBDatabaseProblemMsgs() {
        String problem_messages = this.getDBDatabaseProblemMsg();
        for(DBTable dt : this.database_tables) {
            problem_messages = problem_messages + dt.getAllDBTableProblemMsgs();
        }
        return  problem_messages;
    }

    public void addDBTableToDBDatabase(DBTable database_table) {
        this.database_tables.add(database_table);
        this.problem_msg = "";
        this.checkDBDatabaseIsUsable("AddDBTableToDBDatabase");
    }
    public void addDBTablesToDBDatabase(ArrayList<DBTable> database_tables) {
        this.database_tables.addAll(database_tables);
        this.problem_msg = "";
        this.checkDBDatabaseIsUsable("AddDBTablesToDBDatabase");
    }

    public boolean checkDBDatabaseIsUsable(String caller) {
        this.usable = false;
        if(this.anyEmptyDBTablesInDBDatabase(caller)) {
            this.usable = true;
            if (this.database_name.length() < 1) {
                this.problem_msg = this.problem_msg + " EDBT0105 - Inavlid Table Name - " +
                        "Must be at least 1 character in length. " +
                        "Caller=(" + caller + ")";
                this.usable = false;
            }
        }
        return this.usable;
    }
    public boolean anyEmptyDBTablesInDBDatabase(String caller) {
        boolean rc = true;
        if(this.database_tables.isEmpty()) {
            this.problem_msg = this.problem_msg + "EDBD0103 - No Tables - " +
                    "Must have at least 1 Table in the Database. " +
                    "Caller=(" + caller + ")";
            this.usable = false;
            return false;
        }
        for(DBTable dt : this.database_tables) {
            if(!dt.isDBTableUsable()) {
                this.problem_msg = this.problem_msg + "EDBT0104 - Table " + dt.getDBTableName() +
                        " is unusable. Must be usable. " +
                        "Caller=(" + caller + ")";
                rc = false;
            }

        }
        return rc;
    }
    // Generate all of the SQL to build the defined database
    public ArrayList<String> generateDBBuildSQL(SQLiteDatabase db) {
        ArrayList<String> generatedSQLStatements = new ArrayList<String>();
        for(DBTable dbt : this.database_tables) {
            String current_create_string = dbt.getSQLCreateString(db);
            if(current_create_string.length() > 0) {
                generatedSQLStatements.add(current_create_string);
            }
            //generatedSQLStatements.add(dbt.getSQLCreateString(db));
        }
        return generatedSQLStatements;
    }
    //==============================================================================================
    // Actually perform the Table create statments (note CREATE IF NOT EXISTS) so will
    // only create tables that don't exist.
    // As such there is no need to compare what actually exists with the coded schema/design
    public void actionDBBuildSQL(SQLiteDatabase db) {
        ArrayList<String> actionsql = new ArrayList<String >();
        actionsql.addAll(generateDBBuildSQL(db));
        for(String currentsql : actionsql) {
            db.execSQL(currentsql);
        }
    }
    //==============================================================================================
    // Generate all of the SQL ALter statements, these determined by comparing the DBDatabase
    // schema against the actual database (data extracted via PRAGMA).
    // NOTE!!! this is limited to basically just comparing the databse column names
    // NOTE!!! This only considers adding columns that don't exist.
    public ArrayList<String> generateDBAlterSQL(SQLiteDatabase db) {
        ArrayList<String> generatedSQLStatements = new ArrayList<String>();
        for(DBTable dbt: this.database_tables) {
            generatedSQLStatements.addAll(dbt.getSQLAlterToAddNewColumns(db));
        }
        return generatedSQLStatements;
    }

    //==============================================================================================
    // Actually perform the automatically generated SQL ALTERs (Add columns)
    // See generateDBAlterSQL
    public void actionDBAlterSQL(SQLiteDatabase db) {
        ArrayList<String> actionsql = new ArrayList<String>();
        actionsql.addAll(generateDBAlterSQL(db));
        for(String currentsql : actionsql) {
            db.execSQL(currentsql);
        }
    }
}
/*==================================================================================================
//==================================================================================================
// DBTable class - Database Table Class has table name and holds list of the columns in the table
// Note!! Table name is converted to lowercase
//
// If default constructor used then the table will be flagged as unusable. It will require:-
//        1) A table name via the setDBTable
//        2) One or more columns via methods :-
//            AddDBColumnToDBTable (adds a single column via a DBColumn object)
//            AddDBColumnsToDBTable (adds multiple columns (1 or more) via an ArrayList of DBCOlumn
//                Objects.
//            AddMultipleColumnstoDBTable just a psuedonym for AddColumnsToDBTable, that is easier
//                to differentiate between AddDBColumnToDBTAble and AddColumnsToDBTable (plural)
//
// If Intermediate Constructor used (sets the table name but not any columns). The table will be
// flagged as unusable. However, the table name would be set (unless "" given as the table name).
// Assuming the name is not "" then a column or columns will have to be added as per 2) above.
// If "" given as name then 1) above would have to be applied.
//
// If HigherIntermediate Constructor (sets the table name and 1 column) then the table would be
// usable. However 2) above would be required if further columns were needed.
//
// If Full constructor used then (sets table name and multiple columns as per and ArrayList of
// DBColumn objects) table should be usable.
// NOTE!!! In all cases the assumption is that all DBColumns applied are themselves usable. If any
//         are not then the table will be flagged as unusable. Refer to DBColumns for their
//         usability criteria.
//==================================================================================================
//================================================================================================*/
class DBTable {
    private boolean usable;                         //* Flag to denote if this object can be used
    private String table_name;                      // The table name
    private ArrayList<DBColumn> table_columns;      // The list of columns in the table
    private String problem_msg;                     //* Holds error/warning message

                                                    //* signifies internally managed property

    //==============================================================================================
    // Default DBTable Object Constructor
    public DBTable() {
        this.usable = false;
        this.table_name = "";
        this.table_columns = new ArrayList<DBColumn>();
        this.problem_msg = "WDBT0004 - Uninstantiated - " +
                "Use addDBColumnToDBTable to add at least 1 usable DBColumn or " +
                "Use addDBColumnsToDBTable to add at least 1 usable DBColumn or " +
                "Use AddMultipleColumnstoDBTable to add at least 1 usable DBColumn. " +
                "Note any unusable DBcolumn will render table unusable. " +
                "Also use setDBTableTableName to set the Table Name. " +
                "Caller=DBTable (Default Constructor)";
    }

    //==============================================================================================
    // Intermediate DBTable Object Constructor - Just the table name
    public DBTable(String table_name) {
        this.usable = false;
        this.table_name = table_name.toLowerCase();
        this.table_columns = new ArrayList<DBColumn>();
        this.problem_msg = "WDBT0005 - Partially Instantiated - " +
                "Use addDBColumnToDBTable to add at least 1 usable DBColumns or " +
                "Use addDBColumnsToDBTable to add at least 1 usable DBColumn or " +
                "Use AddMultipleColumnstoDBTable to add at least 1 usable DBColumn. " +
                "Note any unusable DBColumn will render table unusable. " +
                "Caller=DBTable (Table Name only Constructor)";
        if(table_name.length() < 1) {
            this.problem_msg = this.problem_msg + "WDTB0006 - " +
                    "Invalid Table Name - Must be at least 1 character in length. " +
                    "Caller=(DBTable (table_name))";
        }
    }
    //==============================================================================================
    // HigherIntermediate DBTable Object Constructor - Table with 1 column
    public DBTable(String table_name, DBColumn table_column) {
        this();
        this.table_name = table_name;
        this.table_columns.add(table_column);
        this.problem_msg = "";
        this.checkDBTableIsUsable("DBTable (table_name, table_column (singular))");
    }
    //==============================================================================================
    // Full DBTable Object Constructor
    public DBTable(String table_name, ArrayList<DBColumn> table_columns) {
        this();
        this.problem_msg = "";
        this.table_name = table_name;
        this.table_columns = table_columns;
        this.checkDBTableIsUsable("DBTable Full Constructor");
    }
    //==============================================================================================
    // Add a DBCOlumn Object to the table
    public void AddDBColumnToDBTable(DBColumn dbcolumn) {
        this.table_columns.add(dbcolumn);
        this.problem_msg = "";
        this.checkDBTableIsUsable("AddDBColumnToDBTable");
    }
    //==============================================================================================
    // Add a list of DBCOlumn Objects, as held in a DBColumn ArrayList, to the table
    public void AddDBColumnstoDBTable(ArrayList<DBColumn> dbcolumns) {
        this.table_columns.addAll(dbcolumns);
        this.problem_msg = "";
        this.checkDBTableIsUsable("AddDBColumnsToDBtable");
    }
    //==============================================================================================
    // psuedonym for AddDBColumnsToDBTable (easier to differentiate from AddDBColumnToDBTable)
    public void AddMultipleColumnstoDBTable(ArrayList<DBColumn> dbcolumns) {
        this.AddDBColumnstoDBTable(dbcolumns);
    }
    //==============================================================================================
    // Set the name of the table.
    // Note table name will be converetd to lowercase.
    // The table usability will be rechecked.
    public void setDBTableName(String table_name) {
        this.table_name = table_name.toLowerCase();
        this.problem_msg = "";
        this.checkDBTableIsUsable("setDBTableName");
    }
    // Retrieve the DBTable's name
    //==============================================================================================
    public String getDBTableName() { return this.table_name; }
    //==============================================================================================
    public boolean isDBTableUsable() { return this.usable; }
    public int numberOfColumnsInTable() { return this.table_columns.size(); }
    //Retrieve the DBTable's DBColumn list as and ArrayList of DBCOlumn objects
    //==============================================================================================
    public ArrayList<DBColumn> getTableDBColumns() { return this.table_columns; }
    // Retrieve the DBTable's Problem Message
    //==============================================================================================
    public String getDBTableProblemMsg() { return this.problem_msg; }
    //==============================================================================================
    // Retrieve the DBTable's Problem Message along with all the Problem Messages for the
    // DBColumns in the DBTable
    public String getAllDBTableProblemMsgs() {
        String problem_messages = this.getDBTableProblemMsg();
        for(DBColumn tc : this.table_columns) {
            problem_messages = problem_messages + tc.getDBColumnProblemMsg();
        }
        return  problem_messages;
    }
    //==============================================================================================
    // Check if the DBTable is usable, setting usability state. Includes underlying DBColumns
    public boolean checkDBTableIsUsable(String caller) {
        this.usable = false;
        if(this.anyEmptyDBColumnsInDBTable(caller)) {
            this.usable = true;
            if (this.table_name.length() < 1) {
                this.problem_msg = this.problem_msg + " EDBT0009 - Inavlid Table Name - " +
                        "Must be at least 1 character in length. " +
                        "Caller=(" + caller + ")";
                this.usable = false;
            }
        }
        return this.usable;
    }
    //==============================================================================================
    // Check to see if the DBtable has 0 DBColumns. If not then checks the DBColumns for usability.
    // Note!! does not reset DBColumn usability, rather this is the DBTable's view of the DBColumns
    // usability.
    public boolean anyEmptyDBColumnsInDBTable(String caller) {
        boolean rc = true;
        if(this.table_columns.isEmpty()) {
            this.problem_msg = this.problem_msg + "EDBT0007 - No Columns - " +
                    "Must have at least 1 Column in the Table. " +
                    "Caller=(" + caller + ")";
            this.usable = false;
            return false;
        }
        for(DBColumn tc : this.table_columns) {
            if(!tc.isDBColumnUsable()) {
                this.problem_msg = this.problem_msg + "EDBT0008 - Column " + tc.getDBColumnName() +
                        " is unusable. Must be usable. " +
                        "Caller=(" + caller + ")";
                rc = false;
            }
        }
        return rc;
    }
    //==============================================================================================
    public String getSQLCreateString(SQLiteDatabase db) {

        // Check to see if this table exists, if not then skip CREATE
        String sqlstr_mstr = "SELECT name FROM sqlite_master WHERE type = 'table' AND name!='android_metadata' ORDER by name;";
        Cursor csr_mstr = db.rawQuery(sqlstr_mstr,null);
        boolean table_exists = false;

        while(csr_mstr.moveToNext()) {
            if(this.table_name.equals(csr_mstr.getString(0))) { table_exists = true; }
        }
        // Finished with master
        csr_mstr.close();
        if(table_exists) {
            return "";
        }

        // Extract Columns that are flagged as PRIMARY INDEXES so we have a count
        // More than one has to be handled differently
        ArrayList<String> indexes = new ArrayList<String>();
        for(DBColumn dc : this.table_columns) {
            if(dc.getDBColumnIsPrimaryIndex()) {
                indexes.add(dc.getDBColumnName());
            }
        }
        // Build the CREATE SQL
        String part1 = " CREATE  TABLE IF NOT EXISTS " + this.table_name + " (";
        int dccount = 0;
        // Main Loop through the columns
        for(DBColumn dc : this.table_columns) {
            part1 = part1 + dc.getDBColumnName() + " " + dc.getDBColumnType() + " ";
            // Apply the default value if required
            if(dc.getDBColumnDefaultValue().length() > 0 ) {
                part1 = part1 + " DEFAULT " + dc.getDBColumnDefaultValue() + " ";
            }
            // if only 1 PRIMARY INDEX and this is it then add it
            if(dc.getDBColumnIsPrimaryIndex() & indexes.size() == 1) {
                part1 = part1 + " PRIMARY KEY ";
            }
            // If more to do then include comma separator
            dccount++;
            if (dccount < this.table_columns.size()) {
                part1 = part1 + ", ";
            }
        }
        // Handle multiple PRIMARY INDEXES ie add PRIMARY KEY (<col>, <col> .....)
        int ixcount = 1;
        if(indexes.size() > 1 ) {
            part1 = part1 + ", PRIMARY KEY (";
            for(String ix : indexes) {
                part1 = part1 + ix;
                if(ixcount < (indexes.size() ) ) {
                    part1 = part1 + ", ";
                }
                ixcount++;
            }
            part1 = part1 + ")";
        }
        part1 = part1 + ") ;";
        return part1;
    }
    //==============================================================================================
    public ArrayList<String> getSQLAlterToAddNewColumns(SQLiteDatabase db) {
        // Have to return an array (arraylist) as ALTER statements can only Add 1 column at a time.
        ArrayList<String> result = new ArrayList<>();

        // Prepare to get the current database table information PRAGMA
        String sqlstr = " PRAGMA table_info (" + this.table_name + ")";
        Cursor csr = db.rawQuery(sqlstr, null);

        // Check to see if this table exists, if not then cannot ALTER anything
        // Should never happen if the method actionDBAlterSQL (method that invokes this method)
        // is preceeded by actionDBCreateSQL, as that should create any tables that don't exist.
        String sqlstr_mstr = "SELECT name FROM sqlite_master WHERE type = 'table' AND name!='android_metadata' ORDER by name;";
        Cursor csr_mstr = db.rawQuery(sqlstr_mstr,null);
        boolean table_exists = false;

        while(csr_mstr.moveToNext()) {
            String cmix0 = csr_mstr.getString(0);
            if(this.table_name.equals(csr_mstr.getString(0))) { table_exists = true; }
        }
        csr_mstr.close();
        if(!table_exists) {
            csr.close();
            return result;
        }

        // Loop through all the columns of the potentially new columns
        for(DBColumn dc : this.table_columns) {
            String columntofind = dc.getDBColumnName();
            boolean columnmatch = false;

            csr.moveToPosition(-1);
            while(csr.moveToNext()) {
                String testx = csr.getString(1);
                String test2 = csr.getString(2);
                if(csr.getString(1).equals(columntofind)) {
                    columnmatch = true;
                }
            }

            if(!columnmatch) {
                String altersql = " ALTER TABLE " + this.table_name + " ADD COLUMN " +
                        dc.getDBColumnName() + " " +
                        dc.getDBColumnType() + " ";
                if(dc.isDBColumnPrimaryIndex()) {
                    altersql = altersql + " PRIMARY INDEX ";
                }
                if(dc.getDBColumnDefaultValue().length() > 0 ) {
                    altersql = altersql + " DEFAULT " + dc.getDBColumnDefaultValue() + " ";
                }
                altersql = altersql + " ; ";
                result.add(altersql);
            }
        }
        csr.close();
        return result;
    }
}
/*==================================================================================================
//==================================================================================================
// DBTColumn class - Database Column Class has Column Name, Column Datatype, primary index flag,
// and default value "" = no default.
// Also has a usability flag and a Problem Message (both of these are managed internally)
// Note!! Column name is converted to lowercase
//
// If default constructor used then the table will be flagged as unusable. It will require:-
//        1) A table name via the setDBTable
//        2) One or more columns via methods :-
//            AddDBColumnToDBTable (adds a single column via a DBColumn object)
//            AddDBColumnsToDBTable (adds multiple columns (1 or more) via an ArrayList of DBCOlumn
//                Objects.
//            AddMultipleColumnstoDBTable just a psuedonym for AddColumnsToDBTable, that is easier
//                to differentiate between AddDBColumnToDBTAble and AddColumnsToDBTable (plural)
//
// If Intermediate Constructor used (sets the table name but not any columns). The table will be
// flagged as unusable. However, the table name would be set (unless "" given as the table name).
// Assuming the name is not "" then a column or columns will have to be added as per 2) above.
// If "" given as name then 1) above would have to be applied.
//
// If HigherIntermediate Constructor (sets the table name and 1 column) then the table would be
// usable. However 2) above would be required if further columns were needed.
//
// If Full constructor used then (sets table name and multiple columns as per and ArrayList of
// DBColumn objects) table should be usable.
// NOTE!!! In all cases the assumption is that all DBColumns applied are themselves usable. If any
//         are not then the table will be flagged as unusable. Refer to DBColumns for their
//         usability criteria.
//==================================================================================================
//================================================================================================*/
class DBColumn {
    private boolean usable;
    private String column_name;
    private String column_type;
    private boolean primary_index;
    private String default_value;
    private String problem_msg;
    private int order;

    //==============================================================================================
    // Default Constructor
    public DBColumn() {
        this.usable = false;
        this.column_name = "";
        this.column_type = "";
        this.primary_index = false;
        this.default_value = "";
        this.order = 0;
        this.problem_msg = "WDBC0003 - Uninstantiated - " +
                "Use at least setDBColumnType AND setDBColumnName methods. " +
                "Caller=(DBColumn (Default))";
    }
    //==============================================================================================
    // Intermediate Constructor - Just give the column name - defaults to TEXT and not a primary index
    // Note!!! always assumed to be usable.
    public DBColumn(String column_name) {
        this.usable = false;
        this.column_name = column_name.toLowerCase();
        this.column_type = "TEXT";
        this.default_value = "";
        this.primary_index = false;
        this.problem_msg = "";
        this.order = 0;
        this.checkDBColumnIsUsable("DBCOlumn (Quick Constructor)");
    }
    //==============================================================================================
    // Intermediate with sort order
    public DBColumn(String column_name, int sortorder) {
        this(column_name);
        this.order = sortorder;
    }
    //==============================================================================================
    // Full constructor
    public DBColumn(String column_name, String column_type, boolean primary_index, String default_value) {
        column_type = column_type.toUpperCase();
        column_name = column_name.toLowerCase();

        // Lots of potential values for the column type; so validate
        boolean column_ok = false;
        this.problem_msg = "";
        this.column_type = simplifyColumnType(column_type);
        this.column_name = column_name;
        this.primary_index = primary_index;
        this.default_value = default_value;
        this.order = 0;
        this.checkDBColumnIsUsable("DBColumn (Full)");
    }
    //==============================================================================================
    // Full with sort order
    public DBColumn(String column_name,String column_type, boolean primary_index, String  default_value, int sortorder) {
        this(column_name, column_type, primary_index, default_value);
        this.order = sortorder;
    }
    //==============================================================================================
    public void setDBColumnName(String column_name) {
        this.column_name = column_name;
        this.checkDBColumnIsUsable("setDBColumnName");
    }
    //==============================================================================================
    public void setDBColumnType(String column_type) {
        this.column_type = simplifyColumnType(column_type);
        this.checkDBColumnIsUsable("setDBColumnType");
    }
    //==============================================================================================
    public void setDefault_value(String default_value) {
        this.default_value = default_value;
    }
    //==============================================================================================
    public String getDBColumnName() {
        return this.column_name;
    }
    public String getDBColumnType() {
        return this.column_type;
    }
    public boolean getDBColumnIsUsable() {
        return this.usable;
    }
    public boolean isDBColumnUsable() {
        return this.usable;
    }
    public boolean getDBColumnIsPrimaryIndex() {
        return this.primary_index;
    }
    public String getDBColumnDefaultValue() { return this.default_value; }
    public boolean isDBColumnPrimaryIndex() { return this.primary_index; }
    public String getDBColumnProblemMsg() { return this.problem_msg; }
    public String getUnusableMsg() { return this.problem_msg; }
    public int getSortorder() { return this.order; }
    //==============================================================================================
    private boolean checkDBColumnIsUsable(String caller) {
        this.usable = false;
        if(this.column_name.length() > 0 & this.column_type.length() > 0) {
            this.usable = true;
            this.problem_msg = "";
        } else {
            if(this.column_name.length() < 1) {
                this.problem_msg=this.problem_msg +
                        "EDBC001 - Invalid Column Name - Must be at least 1 character in length. " +
                        "Caller=(" + caller + ")";
            }
            if(this.column_type.length() < 1) {
                this.problem_msg=this.problem_msg +
                        "EDBC002 - Invalid Column Type - Must be a valid SQLite DATATYPE. " +
                        "Caller=(" + caller + ")";
            }
        }
        return this.usable;
    }
    //==============================================================================================
    private String simplifyColumnType(String type) {
        type = type.toUpperCase();

        if (type.contains("CHAR")) { return "TEXT"; }
        if (type.contains("DECIMAL")) { return "NUMERIC"; }

        switch (type) {
            case "INT": { return "INTEGER"; }
            case "TINYINT": { return "INTEGER"; }
            case "SMALLINT": { return "INTEGER"; }
            case "MEDIUMINT": { return "INTEGER"; }
            case "BIGINT": { return "INTEGER"; }
            case "UNSIGNED BIG INT": { return "INTEGER"; }
            case "INT2": { return "INTEGER"; }
            case "INT8": { return "INTEGER"; }
            case "INTEGER":  { return "INTEGER"; }
            case "LONG": { return  "INTEGER"; }
            case "BOOLEAN": { return "INTEGER" ;}

            case "CLOB": { return "TEXT"; }
            case "TEXT": { return "TEXT"; }

            case "REAL": { return "REAL"; }
            case "DOUBLE": { return "REAL"; }
            case "DOUBLE PRECISION": { return "REAL"; }
            case "FLOAT": { return "REAL"; }

            case "NUMERIC": { return "NUMERIC" ;}

            case "DATE": { return "NUMERIC" ;}
            case "DATETIME": { return "NUMERIC" ;}
            default: { return ""; }
        }
    }
}
//==================================================================================================
/**
 * ShopperDbHelper Itself
 * ======================
 */
//==================================================================================================
public class ShopperDBHelper extends SQLiteOpenHelper {

    // Shopper database
    public static final String DATABASE_NAME = "Shopper";
    public static final String PRIMARY_KEY_NAME = "_id";

    // Table shops
    public static final String SHOPS_TABLE_NAME = "shops";
    public static final String SHOPS_COLUMN_ID = PRIMARY_KEY_NAME;
    public static final int SHOPS_COLUMNN_ID_INDEX = 0;
    public static final String SHOPS_COLUMN_NAME = "shopname";
    public static final int SHOPS_COLUMN_NAME_INDEX = 1;
    public static final String SHOPS_COLUMN_ORDER = "shoporder";
    public static final int SHOPS_COLUMN_ORDER_INDEX = 2;
    public static final String SHOPS_COLUMN_STREET = "shopstreet";
    public static final int SHOPS_COLUMN_STREET_INDEX = 3;
    public static final String SHOPS_COLUMN_CITY = "shopcity";
    public static final int SHOPS_COLUMN_CITY_INDEX = 4;
    public static final String SHOPS_COLUMN_STATE = "shopstate";
    public static final int SHOPS_COLUMN_STATE_INDEX = 5;
    public static final String SHOPS_COLUMN_PHONE = "shopphone";
    public static final int SHOPS_COULMN_PHONE_INDEX = 6;
    public static final String SHOPS_COLUMN_NOTES = "shopnotes";
    public static final int SHOPS_COULMN_NOTES_INDEX = 7;
    public static int SHOPS_COLUMN_COUNT = -1;

    // Table Aisles
    public static final String AISLES_TABLE_NAME = "aisles";
    public static final String AISLES_COLUMN_ID = PRIMARY_KEY_NAME;
    public static final int AISLES_COLUMN_ID_INDEX = 0;
    public static final String AISLES_COLUMN_NAME = "aislename";
    public static final int AISLES_COLUMN_NAME_INDEX = 1;
    public static final String AISLES_COLUMN_ORDER = "aisleorder";
    public static final int AISLES_COLUMN_ORDER_INDEX = 2;
    public static final String AISLES_COLUMN_SHOP = "aisleshopref";
    public static final int AISLES_COLUMN_SHOP_INDEX = 3;
    public static int AISLES_COLUMN_COUNT = -1;

    // Table Products
    public static final String PRODUCTS_TABLE_NAME = "products";
    public static final String PRODUCTS_COLUMN_ID = PRIMARY_KEY_NAME;
    public static final int PRODUCTS_COLUMN_ID_INDEX = 0;
    public static final String PRODUCTS_COLUMN_NAME = "productname";
    public static final int PRODUCTS_COLUMN_NAME_INDEX = 1;
    public static final String PRODUCTS_COLUMN_ORDER = "productorder"; //redundant
    public static final int PRODUCTS_COLUMN_ORDER_INDEX = 2;
    public static final String PRODUCTS_COLUMN_AISLE = "productaisleref"; //redundant
    public static final int PRODUCTS_COLUMN_AISLE_INDEX = 3;
    public static final String PRODUCTS_COLUMN_USES = "productuses"; //redundant
    public static final int PRODUCTS_COLUMN_USES_INDEX = 4;
    public static final String PRODUCTS_COLUMN_NOTES = "productnotes";
    public static final int PRODUCTS_COLUMN_NOTES_INDEX = 5;
    public static int PRODUCTS_COLUMN_COUNT = -1;

    // Table ProductUsage
    public static final String PRODUCTUSAGE_TABLE_NAME = "productusage";
    public static final String PRODUCTUSAGE_COLUMN_AISLEREF = "productailseref";
    public static final int PRODUCTUSAGE_COLUMN_AISLEREF_INDEX = 0;
    public static final String PRODUCTUSAGE_COLUMN_PRODUCTREF = "productproductref";
    public static final int PRODUCTUSAGE_COLUMN_PRODUCTREF_INDEX = 1;
    public static final String PRODUCTUSAGE_COLUMN_COST = "productcost";
    public static final int PRODUCTUSAGE_COLUMN_COST_INDEX = 2;
    public static final String PRODUCTUSAGE_COLUMN_BUYCOUNT = "productbuycount";
    public static final int PRODUCTUSAGE_COLUMN_BUYCOUNT_INDEX = 3;
    public static final String PRODUCTUSAGE_COLUMN_FIRSTBUYDATE = "productfirstbuydate";
    public static final int PRODUCTUSAGE_COLUMN_FIRSTBUYDATE_INDEX = 4;
    public static final String PRODUCTUSAGE_COLUMN_LATESTBUYDATE = "productlatestbuydate";
    public static final int PRODUCTUSAGE_COLUMN_LASTBUYDATE_INDEX = 5;
    public static final String PRODUCTUSAGE_COLUMN_MINCOST = "mincost";
    public static final int PRODUCTUSAGE_COLUMN_MINCOST_INDEX = 6;
    public static final String PRODUCUSAGE_COLUMN_MINCOST_TYPE = "REAL";
    public static final String PRODUCTUSAGE_COLUMN_ORDER = "orderinaisle";
    public static final int PRODUCTUSAGE_COLUMN_ORDER_INDEX = 7;
    public static final String PRODUCTUSAGE_COLUMN_ORDER_TYPE = "INTEGER";
    public static int PRODUCTUSAGE_COLUMN_COUNT = -1;


    // Rules
    public static final String RULES_TABLE_NAME = "rules";
    public static final String RULES_COLUMN_ID = PRIMARY_KEY_NAME;
    public static final int    RULES_COLUMN_ID_INDEX = 0;
    public static final String RULES_COLUMN_ID_TYPE = "INTEGER";
    public static final String RULES_COLUMN_NAME = "rulename";
    public static final int    RULES_COLUMN_NAME_INDEX = 1;
    public static final String RULES_COULMN_NAME_TYPE = "TEXT";
    public static final String RULES_COLUMN_TYPE = "ruletype";
    public static final int    RULES_COLUMN_TYPE_INDEX = 2;
    public static final String RULES_COLUMN_TYPE_TYPE = "INTEGER";
    public static final String RULES_COLUMN_PROMPTFLAG = "rulepromptflag";
    public static final int    RULES_COLUMN_PROMPTFLAG_INDEX = 3;
    public static final String RULES_COLUMN_PROMPTFLAG_TYPE = "INTEGER";
    public static final String RULES_COLUMN_PERIOD = "ruleperiod";
    public static final int    RULES_COLUMN_PERIOD_INDEX = 4;
    public static final String RULES_COLUMN_PERIOD_TYPE = "INTEGER";
    public static final String RULES_COLUMN_MULTIPLIER = "rulemultiplier";
    public static final int    RULES_COLUMN_MULTIPLIER_INDEX = 5;
    public static final String RULES_COLUMN_MULTIPLIER_TYPE = "INTEGER";
    public static final String RULES_COLUMN_ACTIVEON = "ruleactiveon";
    public static final int    RULES_COLUMN_ACTIVEON_INDEX = 6;
    public static final String RULES_COLUMN_ACTIVEON_TYPE = "INTEGER";
    public static final String RULES_COLUMN_PRODUCTREF = "ruleproductref";
    public static final int    RULES_COLUMN_PRODUCTREF_INDEX = 7;
    public static final String RULES_COLUMN_PRODUCTREF_TYPE = "INTEGER";
    public static final String RULES_COLUMN_AISLEREF = "ruleaisleref";
    public static final int    RULES_COLUMN_AISLEREF_INDEX = 8;
    public static final String RULES_COLUMN_AISLREF_TYPE = "INTEGER";
    public static final String RULES_COLUMN_USES = "ruleuses";
    public static final int    RULES_COLUMN_USES_INDEX = 9;
    public static final String RULES_COLUMN_USES_TYPE = "INTEGER";
    public static final String RULES_COLUMN_MINCOST = "mincost";
    public static final int    RULES_COLUMN_MINCOST_INDEX = 10;
    public static final String RULES_COLUMN_MINCOST_TYPE = "REAL";
    public static final String RULES_COLUMN_MAXCOST = "maxcost";
    public static final int    RULES_COLUMN_MAXCOST_INDEX = 11;
    public static final String RULES_COLUMN_MAXCOST_TYPE = "REAL";
    public static final String RULES_COLUMN_NUMBERTOGET = "rulesnumbettoget";
    public static final int    RULES_COLUMN_NUMBERTOGET_INDEX = 12;
    public static final String RULES_COLUMN_NUMBERTOGET_TYPE = "INTEGER";
    public static int RULES_COLUMN_COUNT = -1;

    // Shoplist
    public static final String SHOPLIST_TABLE_NAME = "shoplist";
    public static final String SHOPLIST_COLUMN_ID = PRIMARY_KEY_NAME;
    public static final int    SHOPLIST_COLUMN_ID_INDEX = 0;
    public static final String SHOPLIST_COLUMN_ID_TYPE = "INTEGER";
    public static final String SHOPLIST_COLUMN_PRODUCTREF = "slproductid";
    public static final int    SHOPLIST_COLUMN_PRODUCTREF_INDEX = 1;
    public static final String SHOPLIST_COLUMN_PRODUCTREF_TYPE = "INTEGER";
    public static final String SHOPLIST_COLUMN_DATEADDED = "sldateadded";
    public static final int    SHOPLIST_COLUMN_DATEADDED_INDEX = 2;
    public static final String SHOPLIST_COLUMN_DATEADDED_TYPE = "INTEGER";
    public static final String SHOPLIST_COLUMN_NUMBERTOGET = "slnumbertoget";
    public static final int    SHOPLIST_COLUMN_NUMBERTOGET_INDEX = 3;
    public static final String SHOPLIST_COLUMN_NUMBERTOGET_TYPE = "INTEGER";
    public static final String SHOPLIST_COLUMN_DONE = "sldone";
    public static final int    SHOPLIST_COLUMN_DONE_INDEX = 4;
    public static final String SHOPLIST_COLUMN_DONE_TYPE = "INTEGER";
    public static final String SHOPLIST_COLUMN_DATEGOT = "sldategot";
    public static final int    SHOPLIST_COLUMN_DATEGOT_INDEX = 5;
    public static final String SHOPLIST_COLUMN_DATEGOT_TYPE = "INTEGER";
    public static final String SHOPLIST_COLUMN_COST = "slcost";
    public static final int    SHOPLIST_COLUMN_COST_INDEX = 6;
    public static final String SHOPLIST_COLUMN_COST_TYPE = "REAL";
    public static final String SHOPLIST_COLUMN_PRODUCTUSAGEREF = "productusageref";
    public static final int    SHOPLIST_COLUMN_PRODUCTUSAGEREF_INDEX = 7;
    public static final String SHOPLIST_COLUMN_PRODUCTUSAGE_TYPE = "INTEGER";
    public static final String SHOPLIST_COLUMN_AISLEREF = "aisleref";
    public static final int    SHOPLIST_COLUMN_AISLEREF_INDEX = 8;
    public static final String SHOPLIST_COLUMN_AISLEREF_TYPE = "INTEGER";
    public static int SHOPLIST_COLUMN_COUNT = -1;


    //APPValues
    public static final String VALUES_TABLE_NAME = "appvalues";
    public static final String VALUES_COLUMN_ID = PRIMARY_KEY_NAME;
    public static final int    VALUES_COLUMN_ID_INDEX = 0;
    public static final String VALUES_COLUMN_ID_TYPE = "INTEGER";
    public static final String VALUES_COLUMN_VALUENAME = "valuename";
    public static final int    VALUES_COLUMN_VALUENAME_INDEX = 1;
    public static final String VALUES_COLUMN_VALUENAME_TYPE = "TEXT";
    public static final String VALUES_COLUMN_VALUETYPE = "valuetype";
    public static final int    VALUES_COLUMN_VALUETYPE_INDDEX = 2;
    public static final String VALUES_COLUMN_VALUETYPE_TYPE = "TEXT";
    public static final String VALUES_COLUMN_VALUEINT = "valueint";
    public static final int    VALUES_COLUMN_VALUEINT_INDEX = 3;
    public static final String VALUES_COLUMN_VALUEINT_TYPE = "INTEGER";
    public static final String VALUES_COLUMN_VALUEREAL = "valuereal";
    public static final int    VALUES_COLUMN_VALUEREAL_INDEX = 4;
    public static final String VALUES_COLUMN_VALUEREAL_TYPE = "REAL";
    public static final String VALUES_COLUMN_VALUETEXT = "valuetext";
    public static final int    VALUES_COLUMN_VALUETEXT_INDEX = 5;
    public static final String VALUES_COLUMN_VALUETEXT_TYPE = "TEXT";
    public static final String VALUES_COLUMN_VALUEINCLUDEINSETTINGS = "valueincludeinsettings";
    public static final int    VALUES_COLUMN_VALUEINCLUDEINSETTINGS_INDEX = 6;
    public static final String VALUES_COLUMN_VALUEINCLUDEINSETTINGS_TYPE = "INTEGER";
    public static final String VALUES_COLUMN_VALUESETTINGSINFO = "valuesettingsinfo";
    public static final int    VALUES_COLUMN_VALUESETTINGSINFO_INDEX = 7;
    public static final String VALUES_COLUMN_VALUESETTINGSINFO_TYPE = "TEXT";

    public ShopperDBHelper(Context ctxt, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(ctxt, DATABASE_NAME, factory, 1); }

    // Method to build the database scehma object (an instance of the DBDatabase class).
    // The schema DBDAtabase object returned has an ArrayList of the DBTable objects.
    // Each DBTable object conatins an ArrayList of DBColumn objects.
    // This routine builds those objects. Some of the important methods then available are:-
    //IMPORTANT!!! This is the database design

    // To add a Table
    //      1) Create the appropriate variable definitions as above (not required)
    //          public static final String <UCTABLENAME>_TABLE_NAME = "<tablename>"; 1 for table
    //          public static final String <UCTABELNAME>_COLUMN_<UCCOLUMNNAME> = "<columnname>" 1 per col
    //          public static final int    <UCTABLENAME>_COLUMN_<UCCOLUMNNAME>_INDEX = nn (sequential order within this list starts with 0) 1 per col
    //          public static final String <UCTABLENAME>_COLUMN_<UCCOLUMNNAME>_TYPE = "??" (?? = valid SQLite DATATYPE) 1 per col
    //              repeat previous three lines for each column.
    //              UCTABLENAME is the table name in Uppercase. TABLENAME is the table name as required case wise.
    //              Likewwise, with columns.
    //      2) Create an empty Arraylist of type DBColumn suggested name is <tablename>
    //      3) For each column use the ArrayList's add method to add a new DBColumn parms are :-
    //              1) ColumnName as String. Pull in appropriate variable from 1
    //              2) Valid SQL DATATYPE as string. Again pull from appropriate variable defined in 1
    //              3) Primary index flag as boolean (ie true or false)
    //              4) Default value as string. "" for no default value.
    //      4) Create a new DBTable object parameters are
    //              1) Table Name as String (pull appropriate variable defined in 1)
    //              2) The DBColumn Arraylist as create in 2-3
    //      5) Add DBTable object to existing, if adding a table to an existing database,
    //          or Add to new DBTable Arraylist
    //      6) Add DBTable Arraylist to existing (might already be done) or new DBDatabase object
    //          as 2nd paramter (1st is the Database name).
    //      Plenty of examples below.
    public DBDatabase generateDBSchema(SQLiteDatabase db) {

        // Shops Table and columns
        ArrayList<DBColumn> shopscolumns = new ArrayList<DBColumn>();
        shopscolumns.add(new DBColumn(SHOPS_COLUMN_ID,"INTEGER",true,""));
        shopscolumns.add(new DBColumn(SHOPS_COLUMN_NAME,"TEXT",false,""));
        shopscolumns.add(new DBColumn(SHOPS_COLUMN_ORDER,"INTEGER",false,"100"));
        shopscolumns.add(new DBColumn(SHOPS_COLUMN_STREET,"TEXT",false,""));
        shopscolumns.add(new DBColumn(SHOPS_COLUMN_CITY,"TEXT",false,""));
        shopscolumns.add(new DBColumn(SHOPS_COLUMN_STATE,"TEXT",false,""));
        shopscolumns.add(new DBColumn(SHOPS_COLUMN_PHONE,"TEXT",false,""));
        shopscolumns.add(new DBColumn(SHOPS_COLUMN_NOTES,"TEXT",false,""));
        DBTable shops = new DBTable(SHOPS_TABLE_NAME,shopscolumns);
        SHOPS_COLUMN_COUNT = shops.numberOfColumnsInTable();

        // Aisles Table and columns
        ArrayList<DBColumn> aislescolumns = new ArrayList<DBColumn>();
        aislescolumns.add(new DBColumn(AISLES_COLUMN_ID,"INTEGER",true,""));
        aislescolumns.add(new DBColumn(AISLES_COLUMN_NAME,"TEXT",false,""));
        aislescolumns.add(new DBColumn(AISLES_COLUMN_ORDER,"INTEGER",false,"100"));
        aislescolumns.add(new DBColumn(AISLES_COLUMN_SHOP,"INTEGER",false,""));
        DBTable aisles = new DBTable(AISLES_TABLE_NAME,aislescolumns);
        AISLES_COLUMN_COUNT = aisles.numberOfColumnsInTable();

        // Products Table and columns
        ArrayList<DBColumn> productscolumns = new ArrayList<DBColumn>();
        productscolumns.add(new DBColumn(PRODUCTS_COLUMN_ID,"INTEGER",true,""));
        productscolumns.add(new DBColumn(PRODUCTS_COLUMN_NAME,"TEXT",false,""));
        productscolumns.add(new DBColumn(PRODUCTS_COLUMN_ORDER,"INTEGER",false,"100"));
        productscolumns.add(new DBColumn(PRODUCTS_COLUMN_AISLE,"INTEGER",false,""));
        productscolumns.add(new DBColumn(PRODUCTS_COLUMN_USES,"INTEGER",false,"0"));
        productscolumns.add(new DBColumn(PRODUCTS_COLUMN_NOTES,"TEXT",false,""));
        DBTable products = new DBTable(PRODUCTS_TABLE_NAME,productscolumns);
        PRODUCTS_COLUMN_COUNT = products.numberOfColumnsInTable();


        // Product Usage Table (link table between Product and Aisle.
        // Includes columns for ailse/product specfic data
        ArrayList<DBColumn> productusagecolumns = new ArrayList<DBColumn>();
        productusagecolumns.add(new DBColumn(PRODUCTUSAGE_COLUMN_AISLEREF,"INTEGER",true,""));
        productusagecolumns.add(new DBColumn(PRODUCTUSAGE_COLUMN_PRODUCTREF,"INTEGER",true,""));
        productusagecolumns.add(new DBColumn(PRODUCTUSAGE_COLUMN_COST,"REAL",false,"1.00"));
        productusagecolumns.add(new DBColumn(PRODUCTUSAGE_COLUMN_BUYCOUNT,"INTEGER",false,"0"));
        productusagecolumns.add(new DBColumn(PRODUCTUSAGE_COLUMN_FIRSTBUYDATE,"INTEGER",false,"0"));
        productusagecolumns.add(new DBColumn(PRODUCTUSAGE_COLUMN_LATESTBUYDATE,"INTEGER",false,"0"));
        productusagecolumns.add(new DBColumn(PRODUCTUSAGE_COLUMN_MINCOST,PRODUCUSAGE_COLUMN_MINCOST_TYPE,false,""));
        productusagecolumns.add(new DBColumn(PRODUCTUSAGE_COLUMN_ORDER,PRODUCTUSAGE_COLUMN_ORDER_TYPE,false,"100"));
        DBTable productusage = new DBTable(PRODUCTUSAGE_TABLE_NAME,productusagecolumns);
        PRODUCTUSAGE_COLUMN_COUNT = productusage.numberOfColumnsInTable();

        // Rules Table (Rules allow auto picking of regularly purchased items)
        ArrayList<DBColumn> rulecolumns = new ArrayList<DBColumn>();
        rulecolumns.add(new DBColumn(RULES_COLUMN_ID,RULES_COLUMN_ID_TYPE,true,""));
        rulecolumns.add(new DBColumn(RULES_COLUMN_NAME,RULES_COULMN_NAME_TYPE,false,""));
        rulecolumns.add(new DBColumn(RULES_COLUMN_TYPE,RULES_COLUMN_TYPE_TYPE,false,""));
        rulecolumns.add(new DBColumn(RULES_COLUMN_PROMPTFLAG,RULES_COLUMN_PROMPTFLAG_TYPE,false,""));
        rulecolumns.add(new DBColumn(RULES_COLUMN_PERIOD,RULES_COLUMN_PERIOD_TYPE,false,""));
        rulecolumns.add(new DBColumn(RULES_COLUMN_MULTIPLIER,RULES_COLUMN_MULTIPLIER_TYPE,false,""));
        rulecolumns.add(new DBColumn(RULES_COLUMN_ACTIVEON,RULES_COLUMN_ACTIVEON_TYPE,false,""));
        rulecolumns.add(new DBColumn(RULES_COLUMN_PRODUCTREF, RULES_COLUMN_PRODUCTREF_TYPE, false, ""));
        rulecolumns.add(new DBColumn(RULES_COLUMN_AISLEREF,RULES_COLUMN_AISLREF_TYPE,false,""));
        rulecolumns.add(new DBColumn(RULES_COLUMN_USES,RULES_COLUMN_USES_TYPE,false,"0"));
        rulecolumns.add(new DBColumn(RULES_COLUMN_MINCOST,RULES_COLUMN_MINCOST_TYPE,false,"0"));
        rulecolumns.add(new DBColumn(RULES_COLUMN_MAXCOST,RULES_COLUMN_MAXCOST_TYPE,false,"0"));
        rulecolumns.add(new DBColumn(RULES_COLUMN_NUMBERTOGET,RULES_COLUMN_NUMBERTOGET_TYPE,false,"1"));
        DBTable rules = new DBTable(RULES_TABLE_NAME,rulecolumns);
        RULES_COLUMN_COUNT = rules.numberOfColumnsInTable();



        // Shoplist Table (The actual shopiing list - TODO still being designed)
        ArrayList<DBColumn> shoplistcolumns = new ArrayList<DBColumn>();
        shoplistcolumns.add(new DBColumn(SHOPLIST_COLUMN_ID,SHOPLIST_COLUMN_ID_TYPE,true,""));
        shoplistcolumns.add(new DBColumn(SHOPLIST_COLUMN_PRODUCTREF,SHOPLIST_COLUMN_PRODUCTREF_TYPE,false,""));
        shoplistcolumns.add(new DBColumn(SHOPLIST_COLUMN_DATEADDED,SHOPLIST_COLUMN_DATEADDED_TYPE,false,"0"));
        shoplistcolumns.add(new DBColumn(SHOPLIST_COLUMN_NUMBERTOGET,SHOPLIST_COLUMN_NUMBERTOGET_TYPE,false,"1"));
        shoplistcolumns.add(new DBColumn(SHOPLIST_COLUMN_DONE,SHOPLIST_COLUMN_DONE_TYPE,false,"0"));
        shoplistcolumns.add(new DBColumn(SHOPLIST_COLUMN_DATEGOT,SHOPLIST_COLUMN_DATEGOT_TYPE,false,""));
        shoplistcolumns.add(new DBColumn(SHOPLIST_COLUMN_COST,SHOPLIST_COLUMN_COST_TYPE,false,"0"));
        shoplistcolumns.add(new DBColumn(SHOPLIST_COLUMN_PRODUCTUSAGEREF,SHOPLIST_COLUMN_PRODUCTUSAGE_TYPE,false,""));
        shoplistcolumns.add(new DBColumn(SHOPLIST_COLUMN_AISLEREF,SHOPLIST_COLUMN_AISLEREF_TYPE,false,""));
        DBTable shoplist = new DBTable(SHOPLIST_TABLE_NAME,shoplistcolumns);
        SHOPLIST_COLUMN_COUNT = shoplist.numberOfColumnsInTable();

        //Values Table (Values akin to global variables)
        ArrayList<DBColumn> valuescolumns = new ArrayList<DBColumn>();
        valuescolumns.add(new DBColumn(VALUES_COLUMN_ID,VALUES_COLUMN_ID_TYPE,true,""));
        valuescolumns.add(new DBColumn(VALUES_COLUMN_VALUENAME,VALUES_COLUMN_VALUENAME_TYPE,false,""));
        valuescolumns.add(new DBColumn(VALUES_COLUMN_VALUETYPE,VALUES_COLUMN_VALUETYPE_TYPE,false,""));
        valuescolumns.add(new DBColumn(VALUES_COLUMN_VALUEINT,VALUES_COLUMN_VALUEINT_TYPE,false,"0"));
        valuescolumns.add(new DBColumn(VALUES_COLUMN_VALUEREAL,VALUES_COLUMN_VALUEREAL_TYPE,false,"'0.0'"));
        valuescolumns.add(new DBColumn(VALUES_COLUMN_VALUETEXT,VALUES_COLUMN_VALUETEXT_TYPE,false,""));
        valuescolumns.add(new DBColumn(VALUES_COLUMN_VALUEINCLUDEINSETTINGS,VALUES_COLUMN_VALUEINCLUDEINSETTINGS_TYPE,false,"0"));
        valuescolumns.add(new DBColumn(VALUES_COLUMN_VALUESETTINGSINFO,VALUES_COLUMN_VALUESETTINGSINFO_TYPE,false,""));
        DBTable values = new DBTable(VALUES_TABLE_NAME,valuescolumns);

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
    }

    //==============================================================================================
    // Shops DB - Instantiate
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

    //==============================================================================================
    // Overide for onUpgrade DO NOTHING AS VERSIONING ISN't IN USE rather onEXpand is used
    @Override
    public  void onUpgrade(SQLiteDatabase db, int oldversion, int newversion) {
        //onUpgrade/versioning bypassed onExpand (below), which is invoked from main ALWAYS runs
    }

    //==============================================================================================
    // Expand the database if the  OOTAD schema has changed
    // (note only caters for additional tables and columns hence onExpand)
    public void onExpand() {
        Log.i(Constants.LOG," onExpand started.");

        //Get/Open the actual Database
        SQLiteDatabase db = this.getWritableDatabase();
        //db.execSQL("DROP TABLE productusage");


        // Get the design/schema as coded as opposed to what exists
        DBDatabase shopper = generateDBSchema(db);

        // Check if the schema is usable
        if(!shopper.isDBDatabaseUsable()) {
            Log.e(Constants.LOG," Unable to Expand the Tables/Columns. -" +
                    " The design(schema) has been marked as unusable." +
                    "- This is a Development Issue. Please contact the Developer.");
            return;
        }

        // Create a set of the SQL statments that would build ALL tables but due to IF NOT EXISTS
        // would only add new tables.
        // Note!!! this DOES NOT build them rather it's run to allow debugging to display the
        // SQL before  actionDBBuildSQL runs. actionDBBuildSQL is basically a wrapper that calls
        // generateDBBuildSQL and then loops through the resultant ArrayList executing the SQL
        ArrayList<String> SQLBuildStatements = new ArrayList<String>(shopper.generateDBBuildSQL(db));

        // Actually action the Build SQL (regenerates them)
        shopper.actionDBBuildSQL(db);

        // Create an array of SQL statements, if any, that would, if used, ADD colums that are coded
        // but don't actually exist. This mainly for testing purposes as next line invokes
        // the actionDBAlterSQL (this uses the generateDBALterSQL method).
        ArrayList<String> SQLALterStatements = new ArrayList<String>(shopper.generateDBAlterSQL(db));

        // Actually action the ALTER SQL statements
        shopper.actionDBAlterSQL(db);

        if(SQLALterStatements.size() > 0 | SQLBuildStatements.size() > 0 ) {
            Log.i(Constants.LOG, "Shopper Database Tables Expanded. " +
                    "Tables Added " + SQLBuildStatements.size() +
                    ". Columns Added " + SQLALterStatements.size() + ".");
        }
        Log.i(Constants.LOG," onExpand completed.");
    }

    //==============================================================================================
    public void insertShop(String name, String order, String street, String city, String state,
                           String phone, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(SHOPS_COLUMN_NAME,name);
        cv.put(SHOPS_COLUMN_ORDER,order);
        cv.put(SHOPS_COLUMN_STREET,street);
        cv.put(SHOPS_COLUMN_CITY,city);
        cv.put(SHOPS_COLUMN_STATE,state);
        cv.put(SHOPS_COLUMN_PHONE,phone);
        cv.put(SHOPS_COLUMN_NOTES,notes);
        db.insert(SHOPS_TABLE_NAME,null,cv);
        db.close();
    }

    //==============================================================================================
    public void insertAisle(String aisleName, long shopThatHasThisAisle, String aisleorder) {
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = this.getWritableDatabase();

        cv.put(AISLES_COLUMN_NAME, aisleName);
        cv.put(AISLES_COLUMN_SHOP, shopThatHasThisAisle);
        cv.put(AISLES_COLUMN_ORDER, aisleorder);
        db.insert(AISLES_TABLE_NAME, null, cv);
        db.close();
    }

    //==============================================================================================
    public void insertProduct(String productName, String productNotes) {
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = this.getWritableDatabase();

        cv.put(PRODUCTS_COLUMN_NAME, productName);
        cv.put(PRODUCTS_COLUMN_NOTES, productNotes);
        cv.put(PRODUCTS_COLUMN_AISLE, 0);
        cv.put(PRODUCTS_COLUMN_ORDER, 0);
        cv.put(PRODUCTS_COLUMN_USES, 0);
        db.insert(PRODUCTS_TABLE_NAME, null, cv);
        db.close();
    }
    //==============================================================================================
    public boolean insertRule(String rulename, int ruletype, boolean promptflag, long period,
                              long multiplier, long activeon, long productref, long aisleref,
                              long uses, float mincost, float maxcost, int quantity ) {
        ContentValues cv = new ContentValues();
        cv.put(RULES_COLUMN_NAME,rulename);
        cv.put(RULES_COLUMN_TYPE,ruletype);
        cv.put(RULES_COLUMN_PROMPTFLAG,promptflag);
        cv.put(RULES_COLUMN_PERIOD,period);
        cv.put(RULES_COLUMN_MULTIPLIER,multiplier);
        cv.put(RULES_COLUMN_ACTIVEON,activeon);
        cv.put(RULES_COLUMN_PRODUCTREF,productref);
        cv.put(RULES_COLUMN_AISLEREF,aisleref);
        cv.put(RULES_COLUMN_USES, uses);
        cv.put(RULES_COLUMN_MINCOST,mincost);
        cv.put(RULES_COLUMN_MAXCOST,maxcost);
        cv.put(RULES_COLUMN_NUMBERTOGET,quantity);
        SQLiteDatabase db = getWritableDatabase();
        db.insert(RULES_TABLE_NAME, null, cv);
        db.close();
        return true;
    }
    //==============================================================================================
    public boolean insertProductIntoAisle(long aisleid, long productid, float cost, int orderinaisle) {
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = this.getWritableDatabase();

        //Run query to see if there are any matches for the productid/aisleid combination
        // should only ever be 1 or 0.
        // 1 Indicates that we cannot add as it would be rejected as the primary key has to be unique.
        // 0 indicates that we can add product to the aisle as it doesn't exist.
        String sqlstr = "SELECT * FROM " + PRODUCTUSAGE_TABLE_NAME +
                " WHERE " + PRODUCTUSAGE_COLUMN_PRODUCTREF + " = " + productid +
                " AND " + PRODUCTUSAGE_COLUMN_AISLEREF + " = " + aisleid + " ;";
        Cursor csr = db.rawQuery(sqlstr, null);

        // Check the count from the query. If 0 insert the row and return true.
        if(csr.getCount() == 0) {
            cv.put(PRODUCTUSAGE_COLUMN_AISLEREF, aisleid);
            cv.put(PRODUCTUSAGE_COLUMN_PRODUCTREF, productid);
            cv.put(PRODUCTUSAGE_COLUMN_COST,cost);
            cv.put(PRODUCTUSAGE_COLUMN_BUYCOUNT, 0);
            cv.put(PRODUCTUSAGE_COLUMN_FIRSTBUYDATE,System.currentTimeMillis());
            cv.put(PRODUCTUSAGE_COLUMN_LATESTBUYDATE,System.currentTimeMillis());
            cv.put(PRODUCTUSAGE_COLUMN_BUYCOUNT,0);
            cv.put(PRODUCTUSAGE_COLUMN_ORDER,orderinaisle);
            db.insert(PRODUCTUSAGE_TABLE_NAME, null,cv);
            csr.close();
            //db.close();
            return true;
        }
        // if the count wasn't 0 then return false.
        csr.close();
        //db.close();
        return false;
    }
    //==============================================================================================
    public boolean insertShopListEntry(long productref,long aisleref, long numbertoget,
                                       double cost, boolean incrementqtyifduplicate) {
        boolean incremented = false;
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = this.getWritableDatabase();

        if(incrementqtyifduplicate) {
            String sqlstr = " SELECT * FROM " + SHOPLIST_TABLE_NAME +
                    " WHERE " + SHOPLIST_COLUMN_AISLEREF + " = " + aisleref +
                    " AND " + SHOPLIST_COLUMN_PRODUCTREF + " = " + productref +
                    " AND NOT " + SHOPLIST_COLUMN_DONE + " ;";
            Cursor csr = db.rawQuery(sqlstr,null);
            int test = csr.getCount();
            if(csr.getCount() > 0) {
                incremented = true;
                csr.moveToFirst();
                long qty = (csr.getLong(SHOPLIST_COLUMN_NUMBERTOGET_INDEX)) + numbertoget;
                cv.put(SHOPLIST_COLUMN_PRODUCTREF,productref);
                cv.put(SHOPLIST_COLUMN_AISLEREF, aisleref);
                cv.put(SHOPLIST_COLUMN_NUMBERTOGET, qty);
                String[] args = new String[] { Long.toString(aisleref), Long.toString(productref)};
                db.update(SHOPLIST_TABLE_NAME,cv,SHOPLIST_COLUMN_AISLEREF + "=? AND " + SHOPLIST_COLUMN_PRODUCTREF + "=? ;",args);
            }
            csr.close();
        }
        if(!incremented) {
            cv.put(SHOPLIST_COLUMN_PRODUCTREF, productref);
            cv.put(SHOPLIST_COLUMN_DATEADDED,System.currentTimeMillis());
            cv.put(SHOPLIST_COLUMN_NUMBERTOGET,numbertoget);
            cv.put(SHOPLIST_COLUMN_DONE,false);
            cv.put(SHOPLIST_COLUMN_DATEGOT,0);
            cv.put(SHOPLIST_COLUMN_COST,cost);
            cv.put(SHOPLIST_COLUMN_PRODUCTUSAGEREF,0);
            cv.put(SHOPLIST_COLUMN_AISLEREF,aisleref);
            db.insert(SHOPLIST_TABLE_NAME,null,cv);
        }
        db.close();
        return true;
    }
    //==============================================================================================
    // Ininital/Low High-level prune/reorg of the shopping list
    // i.e fully purchased (quantity/numbertoget has been reduced to 0) are set as Done and thus
    // won't appear in the shopping list.
    public void reorgShopList() {
        SQLiteDatabase db = this.getWritableDatabase();
        String sqlstr = " UPDATE " + SHOPLIST_TABLE_NAME +
                " SET " + SHOPLIST_COLUMN_DONE + " = 1 " +
                " WHERE " + SHOPLIST_COLUMN_NUMBERTOGET + " <= 0";
        db.execSQL(sqlstr);
        db.close();
    }
    //==============================================================================================
    // Note!! assumes purchase and thus updates the DATEGOT Column in addtion to updating the
    // quantity column
    public boolean changeShopListEntryQuantity(long id, long newquantity) {
        boolean retval = false;
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = this.getWritableDatabase();

        if(newquantity < 0 ) {
            db.close();
            return false;
        } else {
            String sqlstr = "SELECT * FROM " + SHOPLIST_TABLE_NAME +
                    " WHERE " + SHOPLIST_COLUMN_ID + " = " + id + "; ";
            Cursor csr = db.rawQuery(sqlstr, null);
            if(csr.getCount() > 0 ) {
                cv.put(SHOPLIST_COLUMN_NUMBERTOGET,newquantity);
                cv.put(SHOPLIST_COLUMN_DATEGOT,System.currentTimeMillis());
                db.update(SHOPLIST_TABLE_NAME,cv,SHOPLIST_COLUMN_ID + " = " + id + ";", null);
                retval = true;
            } else {
                retval = false;
            }
            csr.close();
            db.close();
        }
        return retval;
    }
    //==============================================================================================
    public void setProductUsageLatestPurchase(long aisleref, long productref, int buycount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        String sqlstr = " UPDATE " + PRODUCTUSAGE_TABLE_NAME +
                " SET " + PRODUCTUSAGE_COLUMN_BUYCOUNT + " = " +
                PRODUCTUSAGE_COLUMN_BUYCOUNT + " + " + buycount + " , " +
                PRODUCTUSAGE_COLUMN_LATESTBUYDATE + " = " + System.currentTimeMillis() +
                " WHERE " + PRODUCTUSAGE_COLUMN_AISLEREF + " = " + aisleref +
                " AND " + PRODUCTUSAGE_COLUMN_PRODUCTREF + " = " + productref + " ;";
        db.execSQL(sqlstr);
        String sqlstr2 = " UPDATE " + PRODUCTS_TABLE_NAME +
                " SET " + PRODUCTS_COLUMN_USES + " = " + PRODUCTS_COLUMN_USES + " + 1 " +
                " WHERE " + PRODUCTS_COLUMN_ID + " = " + productref + "; ";
        db.execSQL(sqlstr2);
        db.close();
    }

    //==============================================================================================
    // Basic statistics for the tables.
    public int numberOfShops() { return numberOfTableRows(SHOPS_TABLE_NAME); }
    //==============================================================================================
    public int numberOfAisles() { return numberOfTableRows(AISLES_TABLE_NAME); }
    //==============================================================================================
    public int numberOfProducts() { return numberOfTableRows(PRODUCTS_TABLE_NAME); }
    //==============================================================================================
    public int numberOfProductUsages() { return numberOfTableRows(PRODUCTUSAGE_TABLE_NAME);}
    //==============================================================================================
    public int numberOfRules() { return numberOfTableRows(RULES_TABLE_NAME); }
    //==============================================================================================
    public int numberofShoppingListEntries() { return numberOfTableRows(SHOPLIST_TABLE_NAME); }
    //==============================================================================================
    public int numberofAppValues() { return numberOfTableRows(VALUES_TABLE_NAME); }
    //==============================================================================================
    // return number of rows in a table
    private int numberOfTableRows(String table) {
        int rc;
        SQLiteDatabase db = this.getReadableDatabase();
        rc  =  (int) DatabaseUtils.queryNumEntries(db,table);
        return rc;
    }

    //==============================================================================================
    // Find latest addition for tables that have _id primary index
    public long getLastShopId() { return lastInsertId(SHOPS_TABLE_NAME); }
    public long getLastAisleId() { return lastInsertId(AISLES_TABLE_NAME); }
    public long getLastProductId() { return lastInsertId(PRODUCTS_TABLE_NAME); }
    private long getLastRuleId() { return  lastInsertId(RULES_TABLE_NAME); }
    public long getLastShopListId() { return lastInsertId(SHOPLIST_TABLE_NAME); }
    private long lastInsertId(String table) {
        long rv = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String sqlstr = " SELECT ROWID FROM " + table +
                " ORDER BY ROWID DESC LIMIT 1 ;";
        Cursor csr = db.rawQuery(sqlstr, null);
        if (csr != null && csr.moveToFirst()) {
            rv = csr.getLong(0);
        }
        assert csr != null;
        csr.close();
        return rv;
    }
    //==============================================================================================
    public int aislesPerShop(long shopid) { return rowsPerParent(AISLES_TABLE_NAME, AISLES_COLUMN_SHOP, shopid); }
    //==============================================================================================
    public int productsPerAisle(long aisleid) { return rowsPerParent(PRODUCTS_TABLE_NAME,PRODUCTS_COLUMN_AISLE,aisleid);}
    //==============================================================================================
    private int rowsPerParent(String tablename, String referencecolumn, long referencevalue) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sqlstr = "SELECT * FROM " + tablename +
                " WHERE " + referencecolumn + " = " + referencevalue + " ;";
        Cursor csr = db.rawQuery(sqlstr, null);
        int rv = csr.getCount();
        csr.close();
        return rv;
    }

    //==============================================================================================
    public Cursor getShopsAsCursor(String orderby) {
        SQLiteDatabase db = this.getReadableDatabase();
        if(orderby.length() < 1) {
            orderby = " ORDER BY " + SHOPS_COLUMN_NAME + " ASC, " + SHOPS_COLUMN_CITY + " ASC ; ";
        }
        String sqlstr = "SELECT * FROM " + SHOPS_TABLE_NAME + orderby;
        return db.rawQuery(sqlstr,null);
    }
    //=============================================================================================
    public Cursor getShopIDFromAisleID(long aisleid) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sqlstr = "SELECT * FROM " + AISLES_TABLE_NAME +
                " WHERE " + AISLES_COLUMN_ID + " = " + aisleid + " ;";
        return db.rawQuery(sqlstr,null);
    }
    //==============================================================================================
    public Cursor getShopFromShopId(Long shopid) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sqlstr = "SELECT * FROM " + SHOPS_TABLE_NAME +
                " WHERE " + SHOPS_COLUMN_ID + " = " + shopid + " ;";
        return db.rawQuery(sqlstr,null);
    }
    //==============================================================================================
    public Cursor getAislesPerShopAsCursor(long shopid, String orderby) {
        if(orderby.length() < 1) {
            orderby = Constants.AISLELISTORDER_BY_ORDER;
        }
        SQLiteDatabase db = this.getReadableDatabase();
        String sqlstr = " SELECT * FROM " + AISLES_TABLE_NAME +
                " WHERE " + AISLES_COLUMN_SHOP + " = " + shopid + orderby ;
        return db.rawQuery(sqlstr, null);
    }
    //==============================================================================================
    public Cursor getAisleFromAisleId(long aisleid) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sqlstr = " SELECT * FROM " + AISLES_TABLE_NAME +
                " WHERE " + AISLES_COLUMN_ID + " = " + aisleid + " ;";
        return db.rawQuery(sqlstr,null);
    }
    //==============================================================================================
    public Cursor getProductsAsCursor(String orderby, String selector) {
        if(orderby.length() < 1) {
            orderby = Constants.PRODUCTLISTORDER_BY_PRODUCT;
        }
        String wherestr = " WHERE " + PRODUCTS_COLUMN_NAME + " LIKE '%" + selector + "%' ";
        SQLiteDatabase db = this.getReadableDatabase();
        String sqlstr = "SELECT * FROM " + PRODUCTS_TABLE_NAME + wherestr + orderby;
        return  db.rawQuery(sqlstr, null);
    }
    public Cursor getProductFromProductId(Long productid) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sqlstr = " SELECT * FROM " + PRODUCTS_TABLE_NAME +
                " WHERE " + PRODUCTS_COLUMN_ID + " = " + productid + " ;";
        return db.rawQuery(sqlstr,null);
    }
    //==============================================================================================
    public Cursor getNoProductsAsCursor() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sqlstr = "SELECT * FROM " + PRODUCTS_TABLE_NAME +
                " WHERE " + PRODUCTS_COLUMN_ID + " = -1  " +
                " ORDER BY " + PRODUCTS_COLUMN_NAME + " ;";
        return db.rawQuery(sqlstr, null);
    }
    //==============================================================================================
    public Cursor getProductsperAisle(long aisle, String orderby) {
        if(orderby.length() < 1) {
            orderby = Constants.PRODUCTSPERAISLELISTORDER_BY_PRODUCT;
        }
        SQLiteDatabase db = this.getReadableDatabase();
        String sqlstr = " SELECT " + PRODUCTS_COLUMN_ID + ", " +
                PRODUCTS_COLUMN_NAME + ", " +
                PRODUCTS_COLUMN_ORDER + ", " +
                PRODUCTS_COLUMN_AISLE + ", " +
                PRODUCTS_COLUMN_USES + ", " +
                PRODUCTS_COLUMN_NOTES + ", " +
                PRODUCTUSAGE_COLUMN_AISLEREF + ", " +
                PRODUCTUSAGE_COLUMN_PRODUCTREF + ", " +
                PRODUCTUSAGE_COLUMN_COST + ", " +
                PRODUCTUSAGE_COLUMN_BUYCOUNT + ", " +
                PRODUCTUSAGE_COLUMN_FIRSTBUYDATE + ", " +
                PRODUCTUSAGE_COLUMN_LATESTBUYDATE + ", " +
                PRODUCTUSAGE_COLUMN_MINCOST + ", " +
                PRODUCTUSAGE_COLUMN_ORDER +
                " FROM " + PRODUCTS_TABLE_NAME + " JOIN " + PRODUCTUSAGE_TABLE_NAME +
                " ON " + PRODUCTS_TABLE_NAME + "." +
                PRODUCTS_COLUMN_ID + " = " +
                PRODUCTUSAGE_TABLE_NAME + "." +
                PRODUCTUSAGE_COLUMN_PRODUCTREF +
                " WHERE " + PRODUCTUSAGE_COLUMN_AISLEREF + " = " + aisle + "" + orderby;
        return db.rawQuery(sqlstr, null);
    }
    //==============================================================================================
    public Cursor getPurchaseableProducts(String productselect, String shopselect, String orderby) {
        if(orderby.length() < 1) {
            orderby = Constants.PURCHASEABLEPRODUCTSLISTORDER_BY_PRODUCT;
        }
        boolean whereclause_exists = false;
        SQLiteDatabase db = this.getReadableDatabase();
        String sqlstr = "SELECT " + PRODUCTUSAGE_COLUMN_AISLEREF + " AS _id, " +
            PRODUCTUSAGE_COLUMN_PRODUCTREF + ", " +
            PRODUCTUSAGE_COLUMN_COST + ", " +
            PRODUCTS_TABLE_NAME + "." + PRODUCTS_COLUMN_ID + " AS products_id, " +
            PRODUCTS_COLUMN_NAME + ", " +
            AISLES_TABLE_NAME + "." + AISLES_COLUMN_ID + " AS aisles_id, " +
            AISLES_COLUMN_NAME + ", " +
            SHOPS_COLUMN_NAME + ", " +
            SHOPS_COLUMN_CITY + ", " +
            SHOPS_COLUMN_STREET +
            " FROM " + PRODUCTUSAGE_TABLE_NAME +
            " LEFT JOIN " + PRODUCTS_TABLE_NAME +
                " ON " + PRODUCTUSAGE_COLUMN_PRODUCTREF + " = " +
                    PRODUCTS_TABLE_NAME + "."  + PRODUCTS_COLUMN_ID +
            " INNER JOIN " + AISLES_TABLE_NAME +
                " ON " + PRODUCTUSAGE_COLUMN_AISLEREF + " = " +
                    AISLES_TABLE_NAME + "." + AISLES_COLUMN_ID +
            " INNER JOIN " + SHOPS_TABLE_NAME +
                " ON " + AISLES_TABLE_NAME + "." + AISLES_COLUMN_SHOP + " = " +
                    SHOPS_TABLE_NAME + "." + SHOPS_COLUMN_ID;
        if(productselect.length() > 0)  {
            if(!whereclause_exists) {
                sqlstr = sqlstr + " WHERE ";
                whereclause_exists = true;
            }
            sqlstr = sqlstr + PRODUCTS_COLUMN_NAME + " LIKE '%" + productselect + "%' ";
        }
        if(shopselect.length() > 0) {
            if (!whereclause_exists) {
                sqlstr = sqlstr + " WHERE ";
                whereclause_exists = true;
            }
            if(productselect.length() > 0 ) {
                sqlstr = sqlstr + " AND ";
            }
            sqlstr = sqlstr + SHOPS_COLUMN_NAME + " LIKE '%" + shopselect + "%' ";
        }
        sqlstr = sqlstr + orderby;
        return db.rawQuery(sqlstr,null);
    }
    //==============================================================================================
    public Cursor getShoppingList() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sqlstr = "SELECT " + SHOPLIST_TABLE_NAME + "." + SHOPLIST_COLUMN_ID + ", " +
                SHOPLIST_TABLE_NAME + "." + SHOPLIST_COLUMN_PRODUCTREF + ", " +
                SHOPLIST_TABLE_NAME + "." + SHOPLIST_COLUMN_DATEADDED + ", " +
                SHOPLIST_TABLE_NAME + "." + SHOPLIST_COLUMN_NUMBERTOGET + ", " +
                SHOPLIST_TABLE_NAME + "." + SHOPLIST_COLUMN_DONE + ", " +
                SHOPLIST_TABLE_NAME + "." + SHOPLIST_COLUMN_DATEGOT + ", " +
                SHOPLIST_TABLE_NAME + "." + SHOPLIST_COLUMN_COST + ", " +
                SHOPLIST_TABLE_NAME + "." + SHOPLIST_COLUMN_PRODUCTUSAGEREF + ", " +
                SHOPLIST_TABLE_NAME + "." + SHOPLIST_COLUMN_AISLEREF + ", " +
                PRODUCTUSAGE_TABLE_NAME + "." + PRODUCTUSAGE_COLUMN_PRODUCTREF + " AS productusageid, " +
                PRODUCTUSAGE_TABLE_NAME + "." + PRODUCTUSAGE_COLUMN_AISLEREF + ", " +
                PRODUCTUSAGE_TABLE_NAME + "." + PRODUCTUSAGE_COLUMN_COST + ", " +
                PRODUCTUSAGE_TABLE_NAME + "." + PRODUCTUSAGE_COLUMN_BUYCOUNT + ", " +
                PRODUCTUSAGE_TABLE_NAME + "." + PRODUCTUSAGE_COLUMN_FIRSTBUYDATE + ", " +
                PRODUCTUSAGE_TABLE_NAME + "." + PRODUCTUSAGE_COLUMN_LATESTBUYDATE + ", " +
                PRODUCTUSAGE_TABLE_NAME + "." + PRODUCTUSAGE_COLUMN_MINCOST + ", " +
                PRODUCTUSAGE_TABLE_NAME + "." + PRODUCTUSAGE_COLUMN_ORDER + ", " +
                AISLES_TABLE_NAME + "." + AISLES_COLUMN_ID + " AS aisleid, " +
                AISLES_TABLE_NAME + "." + AISLES_COLUMN_NAME + ", " +
                AISLES_TABLE_NAME + "." + AISLES_COLUMN_ORDER + ", " +
                AISLES_TABLE_NAME + "." + AISLES_COLUMN_SHOP + ", " +
                SHOPS_TABLE_NAME + "." + SHOPS_COLUMN_ID + " AS shopid, " +
                SHOPS_TABLE_NAME + "." + SHOPS_COLUMN_NAME + ", " +
                SHOPS_TABLE_NAME + "." + SHOPS_COLUMN_ORDER + ", " +
                SHOPS_TABLE_NAME + "." + SHOPS_COLUMN_STREET + ", " +
                SHOPS_TABLE_NAME + "." + SHOPS_COLUMN_CITY + ", " +
                SHOPS_TABLE_NAME + "." + SHOPS_COLUMN_STATE + ", " +
                SHOPS_TABLE_NAME + "." + SHOPS_COLUMN_PHONE + ", " +
                SHOPS_TABLE_NAME + "." + SHOPS_COLUMN_NOTES + ", " +
                PRODUCTS_TABLE_NAME + "." + PRODUCTS_COLUMN_ID + " AS productid, " +
                PRODUCTS_TABLE_NAME + "." + PRODUCTS_COLUMN_NAME + ", " +
                PRODUCTS_TABLE_NAME + "." + PRODUCTS_COLUMN_ORDER + ", " +
                PRODUCTS_TABLE_NAME + "." + PRODUCTS_COLUMN_AISLE + ", " +
                PRODUCTS_TABLE_NAME + "." + PRODUCTS_COLUMN_USES + ", " +
                PRODUCTS_TABLE_NAME + "." + PRODUCTS_COLUMN_NOTES + " " +
                "FROM " + SHOPLIST_TABLE_NAME + " " +
                "LEFT JOIN " + PRODUCTUSAGE_TABLE_NAME + " ON " + SHOPLIST_TABLE_NAME + "." + SHOPLIST_COLUMN_PRODUCTREF + " = " +
                    PRODUCTUSAGE_TABLE_NAME + "." + PRODUCTUSAGE_COLUMN_PRODUCTREF + " AND " +
                    SHOPLIST_TABLE_NAME + "." + SHOPLIST_COLUMN_AISLEREF + " = " +
                    PRODUCTUSAGE_TABLE_NAME + "." + PRODUCTUSAGE_COLUMN_AISLEREF + " " +
                "JOIN " + AISLES_TABLE_NAME + " ON " + PRODUCTUSAGE_TABLE_NAME + "." + PRODUCTUSAGE_COLUMN_AISLEREF + " = " +
                    AISLES_TABLE_NAME + "." + AISLES_COLUMN_ID + " " +
                "JOIN " + PRODUCTS_TABLE_NAME + " ON " + PRODUCTUSAGE_TABLE_NAME + "." + PRODUCTUSAGE_COLUMN_PRODUCTREF + " = " +
                    PRODUCTS_TABLE_NAME + "." + PRODUCTS_COLUMN_ID + " " +
                "JOIN " + SHOPS_TABLE_NAME + " ON " + AISLES_TABLE_NAME + "." + AISLES_COLUMN_SHOP + " = " +
                    SHOPS_TABLE_NAME + "." + SHOPS_COLUMN_ID + " " +
                "WHERE NOT " + SHOPLIST_TABLE_NAME + "." + SHOPLIST_COLUMN_DONE + " " +
                "ORDER BY " + SHOPS_TABLE_NAME + "." + SHOPS_COLUMN_ORDER + ", " +
                    SHOPS_TABLE_NAME + "." + SHOPS_COLUMN_ID + ", " +
                    AISLES_TABLE_NAME + "." + AISLES_COLUMN_ORDER + ", " +
                    AISLES_TABLE_NAME + "." + AISLES_COLUMN_ID + ", " +
                    PRODUCTUSAGE_TABLE_NAME + "." + PRODUCTUSAGE_COLUMN_ORDER + ", " +
                    PRODUCTUSAGE_TABLE_NAME + "." + PRODUCTUSAGE_COLUMN_PRODUCTREF + " ;";

        return db.rawQuery(sqlstr, null);
                /**

                " shoplist._id, shoplist.productref, shoplist.dateadded, shoplist.numbertoget, shoplist.done, shoplist.dategot, shoplist.cost, shoplist. productusageref, shoplist.aisleref,\n" +
                "productusage.productproductref AS prodctusageid, productusage.productailseref, productusage.productcost, productusage.productbuycount, productusage.productfirstbuydate, productusage.productlatestbuydate,\n" +
                "aisles._id AS aisleid, aislename, aisleorder, aisleshopref,\n" +
                "products._id AS productid, productname, productorder, productaisleref, productuses, productnotes,\n" +
                "shops._id AS shopid, shopname, shoporder, shopstreet, shopcity, shopstate, shopphone, shopnotes\n" +
                "FROM shoplist \n" +
                "LEFT JOIN productusage ON shoplist.productref = productusage.productproductref AND shoplist.aisleref = productusage.productailseref\n" +
                "JOIN aisles ON productusage.productailseref = aisles._id\n" +
                "JOIN products ON productusage.productproductref = products._id\n" +
                "JOIN shops ON aisles.aisleshopref = shops._id\n" +
                "WHERE NOT done\n" +
                "ORDER BY shops.shoporder, aisles.aisleorder, products.productorder"
                 **/
    }

    //==============================================================================================
    public Cursor getRuleList(String rulenameselect, String productnameselect, Long datebefore, boolean promptonly, boolean nonpromptonly, String orderby) {
        if(orderby.length() < 1) {
            orderby = Constants.RULELISTORDER_BY_RULE;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        String sqlstr = "SELECT " + RULES_TABLE_NAME + "." + RULES_COLUMN_ID + ", " +
                RULES_TABLE_NAME + "." + RULES_COLUMN_NAME + ", " +
                RULES_TABLE_NAME + "." + RULES_COLUMN_TYPE + ", " +
                RULES_TABLE_NAME + "." + RULES_COLUMN_PROMPTFLAG + ", " +
                RULES_TABLE_NAME + "." + RULES_COLUMN_PERIOD + ", " +
                RULES_TABLE_NAME + "." + RULES_COLUMN_MULTIPLIER + ", " +
                RULES_TABLE_NAME + "." + RULES_COLUMN_ACTIVEON + ", " +
                RULES_TABLE_NAME + "." + RULES_COLUMN_PRODUCTREF + ", " +
                RULES_TABLE_NAME + "." + RULES_COLUMN_AISLEREF + ",  " +
                RULES_TABLE_NAME + "." + RULES_COLUMN_USES + ", " +
                RULES_TABLE_NAME + "." + RULES_COLUMN_NUMBERTOGET + ", " +
                RULES_TABLE_NAME + "." + RULES_COLUMN_MINCOST + ", " +
                RULES_TABLE_NAME + "." + RULES_COLUMN_MAXCOST + ", " +
                PRODUCTS_TABLE_NAME + "." + PRODUCTS_COLUMN_NAME + ", " +
                AISLES_TABLE_NAME + "." + AISLES_COLUMN_NAME + ", " +
                AISLES_TABLE_NAME + "." + AISLES_COLUMN_SHOP + ", " +
                SHOPS_TABLE_NAME + "." + SHOPS_COLUMN_NAME + ", " +
                SHOPS_TABLE_NAME + "." + SHOPS_COLUMN_CITY + ", " +
                SHOPS_TABLE_NAME + "." + SHOPS_COLUMN_STREET + ", " +
                PRODUCTUSAGE_TABLE_NAME + "." + PRODUCTUSAGE_COLUMN_COST + " " +
                "FROM " + RULES_TABLE_NAME + " " +
                "LEFT JOIN " + PRODUCTS_TABLE_NAME  + " ON " +
                    RULES_TABLE_NAME + "." + RULES_COLUMN_PRODUCTREF + " = " +
                    PRODUCTS_TABLE_NAME + "." + PRODUCTS_COLUMN_ID + " " +
                "JOIN " + AISLES_TABLE_NAME + " ON " +
                    RULES_TABLE_NAME + "." + RULES_COLUMN_AISLEREF + " = " +
                    AISLES_TABLE_NAME + "." + AISLES_COLUMN_ID + " " +
                "JOIN " + SHOPS_TABLE_NAME + " ON " +
                    AISLES_TABLE_NAME + "." + AISLES_COLUMN_SHOP + " = " +
                    SHOPS_TABLE_NAME + "." + SHOPS_COLUMN_ID + " " +
                "JOIN " + PRODUCTUSAGE_TABLE_NAME + " ON " +
                    PRODUCTS_TABLE_NAME + "." + PRODUCTS_COLUMN_ID + " = " +
                    PRODUCTUSAGE_TABLE_NAME + "." + PRODUCTUSAGE_COLUMN_PRODUCTREF + " AND " +
                    AISLES_TABLE_NAME + "." + AISLES_COLUMN_ID + " = " +
                    PRODUCTUSAGE_TABLE_NAME + "." + PRODUCTUSAGE_COLUMN_AISLEREF + " " +
                "WHERE " + RULES_TABLE_NAME + "." + RULES_COLUMN_NAME + " LIKE '%" + rulenameselect + "%' " +
                "AND " + PRODUCTS_TABLE_NAME + "." + PRODUCTS_COLUMN_NAME + " LIKE '%" + productnameselect + "%' ";
        if(datebefore > 0) {
            sqlstr = sqlstr + " AND " + RULES_TABLE_NAME + "." + RULES_COLUMN_ACTIVEON + " < " + datebefore + " ";
        }
        if(promptonly) {
            sqlstr = sqlstr + " AND " + RULES_TABLE_NAME + "." + RULES_COLUMN_PROMPTFLAG + " = 0 ";
        }
        if(nonpromptonly) {
            sqlstr = sqlstr + " AND " + RULES_TABLE_NAME + "." + RULES_COLUMN_PROMPTFLAG + " > 0 ";
        }
        sqlstr = sqlstr + orderby;
        return  db.rawQuery(sqlstr,null);
    }
    //==============================================================================================
    public Cursor getProductUsage(long aisleid, long productid) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sqlstr = " SELECT *  FROM " + PRODUCTUSAGE_TABLE_NAME +
                " WHERE " + PRODUCTUSAGE_COLUMN_AISLEREF + " = " + aisleid +
                " AND " + PRODUCTUSAGE_COLUMN_PRODUCTREF + " = " + productid + " ;";
        return db.rawQuery(sqlstr,null);
    }
    //==============================================================================================
    public void updateShop(String shopid, String shopname, String shoporder, String shopstreet,
                           String shopcity, String shopstate, String shopphone, String shopnotes) {

         SQLiteDatabase db = this.getWritableDatabase();
         ContentValues cv = new ContentValues();
         cv.put(SHOPS_COLUMN_NAME, shopname);
         cv.put(SHOPS_COLUMN_ORDER,shoporder);
         cv.put(SHOPS_COLUMN_STREET, shopstreet);
         cv.put(SHOPS_COLUMN_CITY, shopcity);
         cv.put(SHOPS_COLUMN_STATE, shopstate);
         cv.put(SHOPS_COLUMN_PHONE, shopphone);
         cv.put(SHOPS_COLUMN_NOTES, shopnotes);
         db.update(SHOPS_TABLE_NAME, cv, SHOPS_COLUMN_ID + " = " + shopid + " ;", null);
    }
    //==============================================================================================
    public void deleteShop(String shopid) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sqlstr = "SELECT " + AISLES_COLUMN_ID + " FROM " + AISLES_TABLE_NAME +
                " WHERE " + AISLES_COLUMN_SHOP + " = " + shopid + "; ";
        Cursor csr = db.rawQuery(sqlstr, null);
        while(csr.moveToNext()) {
            deleteAisle(csr.getLong(AISLES_COLUMN_ID_INDEX));
        }
        csr.close();
        db.execSQL("DELETE FROM " + SHOPS_TABLE_NAME +
                " WHERE " + SHOPS_COLUMN_ID + " = " + shopid + " ;");
        validateRules();
        validateShoplist();
    }
    //==============================================================================================
    public void updateAisle(String aisleid, String aislename, String aisleorder, long aisleshopref) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(AISLES_COLUMN_NAME, aislename);
        cv.put(AISLES_COLUMN_ORDER, aisleorder);
        cv.put(AISLES_COLUMN_SHOP, aisleshopref);
        db.update(AISLES_TABLE_NAME, cv, AISLES_COLUMN_ID + " = " + aisleid + " ;", null);
    }
    //==============================================================================================
    public void deleteAisle(long aisleid) {
        deleteProductsInAisle(aisleid);
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + AISLES_TABLE_NAME +
                " WHERE " + AISLES_COLUMN_ID + " = " + aisleid + " ;");
        validateRules();
        validateShoplist();
    }
    //==============================================================================================
    public void updateProduct(long productid, String productname, String productnotes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(PRODUCTS_COLUMN_NAME, productname);
        cv.put(PRODUCTS_COLUMN_NOTES, productnotes);
        db.update(PRODUCTS_TABLE_NAME, cv, PRODUCTS_COLUMN_ID + " = " + productid + " ;", null);
    }
    //==============================================================================================
    // Delete a product and all references made to the product
    // ie remove the product from all aisles and then delete the actual product. Hence two queries.
    public void deleteProduct(long productid) {

        SQLiteDatabase db = this.getWritableDatabase();
        String sqlstr = " DELETE FROM " + PRODUCTUSAGE_TABLE_NAME +
                " WHERE " + PRODUCTUSAGE_COLUMN_PRODUCTREF + " = " + productid + " ;";
        db.execSQL(sqlstr);
        sqlstr = " DELETE FROM " + PRODUCTS_TABLE_NAME +
                " WHERE " + PRODUCTS_COLUMN_ID + " = " + productid + " ;";
        db.execSQL(sqlstr);
        validateRules();
        validateShoplist();
    }
    //==============================================================================================
    // Delete ALL products from an Aisle
    // ie delete all link entries for an aisle. The aisle and the products are themselves
    // not deleted.
    public void deleteProductsInAisle(long aisleid) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sqlstr = " DELETE FROM " + PRODUCTUSAGE_TABLE_NAME +
                " WHERE " + PRODUCTUSAGE_COLUMN_AISLEREF + " = " + aisleid + " ;";
        db.execSQL(sqlstr);
        validateRules();
        validateShoplist();
    }
    //==============================================================================================
    // Delete a single product from an aisle's persepctive
    // ie delete the link entry, not the product nor the aisle.
    public void deleteSingleProductFromAisle(long aisleid, long productid) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sqlstr = " DELETE FROM " + PRODUCTUSAGE_TABLE_NAME +
                " WHERE " + PRODUCTUSAGE_COLUMN_AISLEREF + " = " + aisleid +
                " AND " + PRODUCTUSAGE_COLUMN_PRODUCTREF + " = " + productid + " ;";
        db.execSQL(sqlstr);
        validateRules();
        validateShoplist();
    }
    //==============================================================================================
    public void updateProductInAisle(long aisleid, long productid, float cost,  int buycount,
                                     long firstbuydate, long lastbuydate, int order, float mincost) {
        ContentValues cv = new ContentValues();
        cv.put(PRODUCTUSAGE_COLUMN_COST,cost);
        cv.put(PRODUCTUSAGE_COLUMN_BUYCOUNT,buycount);
        cv.put(PRODUCTUSAGE_COLUMN_FIRSTBUYDATE,firstbuydate);
        cv.put(PRODUCTUSAGE_COLUMN_LATESTBUYDATE,lastbuydate);
        cv.put(PRODUCTUSAGE_COLUMN_ORDER,order);
        cv.put(PRODUCTUSAGE_COLUMN_MINCOST,mincost);

        String where = PRODUCTUSAGE_COLUMN_AISLEREF + " =? AND " +
                PRODUCTUSAGE_COLUMN_PRODUCTREF + " = ? ";
        String[] whereargs = new String[] {String.valueOf(aisleid), String.valueOf(productid)};
        SQLiteDatabase db = getWritableDatabase();
        db.update(PRODUCTUSAGE_TABLE_NAME, cv, where, whereargs);
    }
    //==============================================================================================
    // Note!! rather than delete a ShopListEntry should mark it as complete so that history is
    // kept. See setShopListEntryAsComplete.
    public void deleteShopListEntry(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sqlstr = " DELETE FROM " + SHOPLIST_TABLE_NAME +
                " WHERE " + SHOPLIST_COLUMN_ID + " = " + id + " ;";
        db.execSQL(sqlstr);
        db.close();
    }
    //==============================================================================================
    // Note!! rather than delete a ShopListEntry should mark it as complete so that history is
    // kept.
    public void setShopListEntryAsComplete(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ShopperDBHelper.SHOPLIST_COLUMN_DONE,true);
        String where = ShopperDBHelper.SHOPLIST_COLUMN_ID + " = " + id + " ;";
        db.update(SHOPLIST_TABLE_NAME, cv, where, null);
        db.close();
    }
    //==============================================================================================
    // For a Shoplist entry to be valid, it must point to an existing product and an existing aisle
    // As such remove any rule that does not point to an existing product or to an existing aisle
    public void validateShoplist() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor shoplistcursor = getAllRowsFromTable(SHOPLIST_TABLE_NAME);
        Cursor productcsr;
        Cursor aislecsr;
        Cursor prdusecsr;
        while(shoplistcursor.moveToNext()) {
            productcsr = getProductFromProductId(shoplistcursor.getLong(SHOPLIST_COLUMN_PRODUCTREF_INDEX));
            aislecsr = getAisleFromAisleId(shoplistcursor.getLong(SHOPLIST_COLUMN_AISLEREF_INDEX));
            prdusecsr = getProductUsage(shoplistcursor.getLong(SHOPLIST_COLUMN_AISLEREF_INDEX),
                    shoplistcursor.getLong(SHOPLIST_COLUMN_PRODUCTREF_INDEX));
            if(productcsr.getCount() < 1 | aislecsr.getCount() < 1 | prdusecsr.getCount() < 1) {
                deleteShopListEntry(shoplistcursor.getLong(SHOPLIST_COLUMN_ID_INDEX));
            }
            if(shoplistcursor.isLast()) {
                prdusecsr.close();
                aislecsr.close();
                productcsr.close();
            }
        }
        shoplistcursor.close();
        db.close();
    }
    //==============================================================================================
    // Update Rule
    public void updateRule(long ruleid, String rulename, int ruletype, boolean rulepromtflag,
                           int ruleperiod, int rulemultiplier, long ruleactiveon,
                           long ruleproductref, long ruleaisleref, long ruleuses,
                           double rulemincost, double rulemaxcost, int rulequantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(RULES_COLUMN_NAME,rulename);
        cv.put(RULES_COLUMN_TYPE,ruletype);
        cv.put(RULES_COLUMN_PROMPTFLAG,rulepromtflag);
        cv.put(RULES_COLUMN_PERIOD,ruleperiod);
        cv.put(RULES_COLUMN_MULTIPLIER,rulemultiplier);
        cv.put(RULES_COLUMN_ACTIVEON,ruleactiveon);
        cv.put(RULES_COLUMN_PRODUCTREF,ruleproductref);
        cv.put(RULES_COLUMN_AISLEREF,ruleaisleref);
        cv.put(RULES_COLUMN_USES,ruleuses);
        cv.put(RULES_COLUMN_MINCOST,rulemincost);
        cv.put(RULES_COLUMN_MAXCOST,rulemaxcost);
        cv.put(RULES_COLUMN_NUMBERTOGET,rulequantity);
        String where = RULES_COLUMN_ID + " = ? ";
        String[] whereargs = new String[] {String.valueOf(ruleid)};
        db.update(RULES_TABLE_NAME, cv, where, whereargs);
        db.close();
    }
    //==============================================================================================
    // Update Rule after it has been added
    public void updateRuleDateAndUse(long ruleid, long newdate, int newusecount) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(RULES_COLUMN_ACTIVEON,newdate);
        cv.put(RULES_COLUMN_USES,newusecount);
        String where = RULES_COLUMN_ID + " = ? ";
        String[] whereargs = new String[] {String.valueOf(ruleid)};
        db.update(RULES_TABLE_NAME, cv, where, whereargs);
        db.close();
    }
    //==============================================================================================
    // Delete Rule
    public void deleteRule(Long ruleid) {
        SQLiteDatabase db = getWritableDatabase();
        String sqlstr = " DELETE FROM " + RULES_TABLE_NAME +
                " WHERE " + RULES_TABLE_NAME + "." + RULES_COLUMN_ID + " = " + ruleid + " ;";
        db.execSQL(sqlstr);
        db.close();
    }
    //==============================================================================================
    // For a rule to be valid, the rules must point to an existing product and an existing aisle
    // As such remove any rule that does not point to an existing product or to an existing aisle
    public void validateRules() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor rulescursor = getAllRowsFromTable(RULES_TABLE_NAME);
        while(rulescursor.moveToNext()) {
            Cursor productcsr = getProductFromProductId(rulescursor.getLong(RULES_COLUMN_PRODUCTREF_INDEX));
            Cursor aislecsr = getAisleFromAisleId(rulescursor.getLong(RULES_COLUMN_AISLEREF_INDEX));
            Cursor prdusecsr = getProductUsage(rulescursor.getLong(RULES_COLUMN_AISLEREF_INDEX),
                    rulescursor.getLong(RULES_COLUMN_PRODUCTREF_INDEX));
            if(productcsr.getCount() < 1 | aislecsr.getCount() < 1 | prdusecsr.getCount() < 1) {
                deleteRule(rulescursor.getLong(RULES_COLUMN_ID_INDEX));
            }
            if(rulescursor.isLast()) {
                prdusecsr.close();
                aislecsr.close();
                productcsr.close();

            }
        }
        rulescursor.close();
        db.close();
    }
    //==============================================================================================
    // Relatively generic get all rows from a table into a cursor
    // NOTE!!! will only return populated cursor for the defined tables as per the switch
    // undefined tables will result in empty cursor as per shops table
    // NOTE!!! Special handling is need for tables that do not have an _id (case sensitive) column
    // see second case for Productusage table where aisleref column is renamed to _id as the
    // get-around.
    //TODO allow ORDER Clause to be passed ???????????
    public Cursor getAllRowsFromTable(String tablename) {
        switch (tablename) {
            // Normal Tables (ie they have _id handled here) with generic query
            case AISLES_TABLE_NAME:
            case PRODUCTS_TABLE_NAME:
            case SHOPS_TABLE_NAME:
            case RULES_TABLE_NAME:
            case SHOPLIST_TABLE_NAME:
            case VALUES_TABLE_NAME: {
                SQLiteDatabase db = this.getReadableDatabase();
                String sqlstr = " SELECT * FROM " + tablename + " ;";
                return db.rawQuery(sqlstr,null);
            }
            // Productusage is a special case as it has no _id so swap aisleref to _id
            case PRODUCTUSAGE_TABLE_NAME: {
                SQLiteDatabase db = this.getReadableDatabase();
                String sqlstr = " SELECT " + PRODUCTUSAGE_COLUMN_AISLEREF +
                        " AS _id, " +
                        PRODUCTUSAGE_COLUMN_PRODUCTREF + ", " +
                        PRODUCTUSAGE_COLUMN_COST + ", " +
                        PRODUCTUSAGE_COLUMN_BUYCOUNT + ", " +
                        PRODUCTUSAGE_COLUMN_FIRSTBUYDATE + ", " +
                        PRODUCTUSAGE_COLUMN_LATESTBUYDATE + ", " +
                        PRODUCTUSAGE_COLUMN_MINCOST +
                        " FROM " + PRODUCTUSAGE_TABLE_NAME +
                        " ORDER BY _id;";
                return db.rawQuery(sqlstr, null);
            }
            default: {
                SQLiteDatabase db = this.getReadableDatabase();
                String sqlstr = " SELECT * FROM " + SHOPS_TABLE_NAME +
                        " WHERE " + SHOPS_COLUMN_ID + " = -1 ;";
                return db.rawQuery(sqlstr, null);
            }
        }
    }
    //==============================================================================================
    // Insert String (Type = TEXT) appvalue
    public void insertValuesEntry(String valuename, String value, boolean allowmultiple, boolean includeinsettings, String settingsinfo) {
        SQLiteDatabase db = this.getWritableDatabase();
        // If we don't allow multiple entries with the same name then check for existence
        // of that name and return if it already exists
        if(!allowmultiple) {
            String sqlstr = " SELECT * FROM " + VALUES_TABLE_NAME +
                    " WHERE " + VALUES_COLUMN_VALUENAME + " = '" + valuename + "' ;";
            Cursor csr = db.rawQuery(sqlstr,null);
            if(csr.getCount() > 0) {
                csr.close();
                return;
            }
            csr.close();
        }
        // Never allow duplicates (name and value the same)
        String sqlstr = " SELECT * FROM " + VALUES_TABLE_NAME +
                " WHERE " + VALUES_COLUMN_VALUENAME + " = '" + valuename + "' " +
                " AND " + VALUES_COLUMN_VALUETEXT + " = '" + value + "' ;";
        Cursor csr = db.rawQuery(sqlstr,null);
        if(csr.getCount() > 0 ) {
            csr.close();
            return;
        }
        csr.close();
        // OK to insert
        int settingsincl = 0;
        if(includeinsettings) {
            settingsincl = 1;
        }
        ContentValues cv = new ContentValues();
        cv.put(VALUES_COLUMN_VALUENAME,valuename);
        cv.put(VALUES_COLUMN_VALUETEXT,value);
        cv.put(VALUES_COLUMN_VALUETYPE,"TEXT");
        cv.put(VALUES_COLUMN_VALUEINCLUDEINSETTINGS,includeinsettings);
        cv.put(VALUES_COLUMN_VALUESETTINGSINFO,settingsinfo);
        db.insert(VALUES_TABLE_NAME, null, cv);
        db.close();
    }
    //==============================================================================================
    // Insert Integer (Type = INTEGER) appvalue
    public void insertValuesEntry(String valuename, long value, boolean allowmultiple, boolean includeinsettings, String settingsinfo) {
        SQLiteDatabase db = this.getWritableDatabase();
        // If we don't allow multiple entries with the same name then check for existence
        // of that name and return if it already exists
        if(!allowmultiple) {
            String sqlstr = " SELECT * FROM " + VALUES_TABLE_NAME +
                    " WHERE " + VALUES_COLUMN_VALUENAME + " = '" + valuename + "' ;";
            Cursor csr = db.rawQuery(sqlstr,null);
            if(csr.getCount() > 0) {
                csr.close();
                return;
            }
            csr.close();
        }
        // Never allow duplicates (name and value the same)
        String sqlstr = " SELECT * FROM " + VALUES_TABLE_NAME +
                " WHERE " + VALUES_COLUMN_VALUENAME + " = '" + valuename + "' " +
                " AND " + VALUES_COLUMN_VALUEINT + " = '" + value + "' ;";
        Cursor csr = db.rawQuery(sqlstr,null);
        if(csr.getCount() > 0 ) {
            csr.close();
            return;
        }
        csr.close();
        // OK to insert
        int settingsincl = 0;
        if(includeinsettings) {
            settingsincl = 1;
        }
        ContentValues cv = new ContentValues();
        cv.put(VALUES_COLUMN_VALUENAME,valuename);
        cv.put(VALUES_COLUMN_VALUEINT,value);
        cv.put(VALUES_COLUMN_VALUETYPE,"INTEGER");
        cv.put(VALUES_COLUMN_VALUEINCLUDEINSETTINGS,includeinsettings);
        cv.put(VALUES_COLUMN_VALUESETTINGSINFO,settingsinfo);
        db.insert(VALUES_TABLE_NAME,null,cv);
        db.close();
    }
    //==============================================================================================
    // Insert Double (Type = REAL) appvalue
    public void insertValuesEntry(String valuename, double value, boolean allowmultiple, boolean includeinsettings, String settingsinfo) {
        SQLiteDatabase db = this.getWritableDatabase();
        // If we don't allow multiple entries with the same name then check for existence
        // of that name and return if it already exists
        if(!allowmultiple) {
            String sqlstr = " SELECT * FROM " + VALUES_TABLE_NAME +
                    " WHERE " + VALUES_COLUMN_VALUENAME + " = '" + valuename + "' ;";
            Cursor csr = db.rawQuery(sqlstr,null);
            if(csr.getCount() > 0) {
                csr.close();
                return;
            }
            csr.close();
        }
        // Never allow duplicates (name and value the same)
        String sqlstr = " SELECT * FROM " + VALUES_TABLE_NAME +
                " WHERE " + VALUES_COLUMN_VALUENAME + " = '" + valuename + "' " +
                " AND " + VALUES_COLUMN_VALUEREAL + " = '" + value + "' ;";
        Cursor csr = db.rawQuery(sqlstr,null);
        if(csr.getCount() > 0 ) {
            csr.close();
            return;
        }
        csr.close();
        // OK to insert
        int settingsincl = 0;
        if(includeinsettings) {
            settingsincl = 1;
        }
        ContentValues cv = new ContentValues();
        cv.put(VALUES_COLUMN_VALUENAME,valuename);
        cv.put(VALUES_COLUMN_VALUEREAL,value);
        cv.put(VALUES_COLUMN_VALUETYPE,"REAL");
        cv.put(VALUES_COLUMN_VALUEINCLUDEINSETTINGS,includeinsettings);
        cv.put(VALUES_COLUMN_VALUESETTINGSINFO,settingsinfo);
        db.insert(VALUES_TABLE_NAME, null, cv);
        db.close();
    }

    //==============================================================================================
    // Get AppValue as Long according to given key. returns -1 if not found.
    public long getLongValue(String valuename) {
        long rv = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        String sqlstr = " SELECT " + VALUES_COLUMN_VALUEINT + " FROM " + VALUES_TABLE_NAME +
                " WHERE " + VALUES_COLUMN_VALUENAME + " = '" + valuename + "' " +
                " AND WHERE " + VALUES_COLUMN_VALUETYPE + " = 'INTEGER' ;";
        Cursor csr = db.rawQuery(sqlstr,null);
        if(csr.getCount() == 1) {
            csr.moveToFirst();
            rv = csr.getLong(0);
        }
        csr.close();
        db.close();
        return rv;
    }
    //==============================================================================================
    // Get AppValue as Double according to gievn key. returns -1 if not found.
    public double getDoubleValue(String valuename) {
        double rv = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        String sqlstr = " SELECT " + VALUES_COLUMN_VALUEINT + " FROM " + VALUES_TABLE_NAME +
                " WHERE " + VALUES_COLUMN_VALUENAME + " = '" + valuename + "' " +
                " AND WHERE " + VALUES_COLUMN_VALUETYPE + " = 'REAL' ;";
        Cursor csr = db.rawQuery(sqlstr,null);
        if(csr.getCount() == 1) {
            csr.moveToFirst();
            rv = csr.getDouble(0);
        }
        csr.close();
        db.close();
        return rv;
    }
    //==============================================================================================
    // Get Appvalue as String according to given key. returns "" if not found.
    public String getStringValue(String valuename){
        String rv = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String sqlstr = " SELECT " + VALUES_COLUMN_VALUEINT + " FROM " + VALUES_TABLE_NAME +
                " WHERE " + VALUES_COLUMN_VALUENAME + " = '" + valuename + "' " +
                " AND WHERE " + VALUES_COLUMN_VALUETYPE + " = 'TEXT' ;";
        Cursor csr = db.rawQuery(sqlstr,null);
        if(csr.getCount() == 1) {
            csr.moveToFirst();
            rv = csr.getString(0);
        }
        csr.close();
        db.close();
        return rv;
    }
    //==============================================================================================
    // Get List of AppValues as Long according to given key.
    // Note!! used to extract multiple same key entries
    public ArrayList<Long> getLongArrayListValue(String valuename) {
        ArrayList<Long> rv = new ArrayList<Long>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sqlstr = " SELECT " + VALUES_COLUMN_VALUEINT + " FROM " + VALUES_TABLE_NAME +
                " WHERE " + VALUES_COLUMN_VALUENAME + " = '" + valuename + "' " +
                " AND " + VALUES_COLUMN_VALUETYPE + " = 'INTEGER' ;";
        Cursor csr = db.rawQuery(sqlstr,null);
        if(csr.getCount() >  0) {
            while(csr.moveToNext()) {
                rv.add(csr.getLong(0));
            }
        }
        csr.close();
        db.close();
        return rv;
    }
    //==============================================================================================
    // Get ArrayList of AppValues as Doubles according to given key.
    // Note!! used to extract multiple same key entries
    public ArrayList<Double> getDoubleArrayListValue(String valuename) {
        ArrayList<Double> rv = new ArrayList<Double>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sqlstr = " SELECT " + VALUES_COLUMN_VALUEREAL + " FROM " + VALUES_TABLE_NAME +
                " WHERE " + VALUES_COLUMN_VALUENAME + " = '" + valuename + "' " +
                " AND " + VALUES_COLUMN_VALUETYPE + " = 'REAL' ;";
        Cursor csr = db.rawQuery(sqlstr,null);
        if(csr.getCount() >  0) {
            while(csr.moveToNext()) {
                rv.add(csr.getDouble(0));
            }
        }
        csr.close();
        db.close();
        return rv;
    }
    //==============================================================================================
    // Get ArrayList of AppValues as Strings according to given key.
    // Note!! used to extract multiple same key entries
    public ArrayList<String> getStringArrayListValue(String valuename) {
        ArrayList<String> rv = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sqlstr = " SELECT " + VALUES_COLUMN_VALUETEXT + " FROM " + VALUES_TABLE_NAME +
                " WHERE " + VALUES_COLUMN_VALUENAME + " = '" + valuename + "' " +
                " AND " + VALUES_COLUMN_VALUETYPE + " = 'TEXT' ;";
        Cursor csr = db.rawQuery(sqlstr,null);
        if(csr.getCount() >  0) {
            while(csr.moveToNext()) {
                rv.add(csr.getString(0));
            }
        }
        csr.close();
        db.close();
        return rv;
    }
    //==============================================================================================
    // GetCursorValue
    //NOTE!! returns all columns, so up to the caller to determine what is valid data
    //NOTE!! onlyifsetting will only return "user settable" values (for settings)
    public Cursor getCursorvalue(String valuename, boolean onlyifsetting) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sqlstr = " SELECT * FROM " + VALUES_TABLE_NAME +
                " WHERE " + VALUES_COLUMN_VALUENAME + " = '" + valuename + "' ";
        if(onlyifsetting) {
            sqlstr = sqlstr + " AND " + VALUES_COLUMN_VALUEINCLUDEINSETTINGS + " > 0 ";
        }
        sqlstr = sqlstr + " ;";
        return db.rawQuery(sqlstr,null);
    }
}