package mjt.shopper;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * MainDataHandlingActivity - Handle Main Data handling Display
 *  allowing the various options to be taken. All in regards to
 *  backup, restore and export operations.
 */
@SuppressWarnings("FieldCanBeLocal")
public class MainDataHandlingActivity extends AppCompatActivity {

    private final String THIS_ACTIVITY = "MainDataHandling";

    private TextView backupdir;
    private EditText datetimepart;
    private String datetimestr = "";

    private String backupfilename = "";
    private String currentdbfilename = "";
    private String icdbfilename = "";
    private String copydbfilename = "";
    private String logtag = "";
    private String resulttitle = "";
    private String finalmessage = "";
    private String restoresql = "";

    private File dbfile;
    private File rstsql;

    private EditText dbbasefilename;
    private EditText dbfileext;
    private TextView dbfullfilename;
    private Spinner dbrestore_spinner;
    private TextView dbbutton;
    private TextView dbfcnt;
    private TextView dbrestorebutton;

    private EditText sqlbasefilename;
    private EditText sqlfileext;
    private TextView sqlfullfilename;
    private Spinner sqlrestore_spinner;
    private TextView sqlbutton;
    private TextView sqlfcnt;
    private TextView sqlrestorebutton;

    private EditText dumpbasefilename;
    private EditText dumpfileext;
    private TextView dumpfullfilename;
    private Spinner dumprestore_spinner;
    private TextView dumpbutton;
    private TextView dumpfcnt;

    private EditText csvbasefilename;
    private EditText csvfileext;
    private TextView csvfullfilename;
    private TextView csvbutton;
    private LinearLayout datahandlinghelp;
    private StoreData sdbase;

    private SharedPreferences sp;
    private boolean strictbackupmode = true;

    private ProgressDialog busy;
    private boolean copytaken = false;
    private boolean origdeleted = false;
    private boolean restoredone = false;
    private boolean rolledback = false;
    private boolean sqlretrieved = false;
    private boolean skip_mode = false;
    private boolean concat_mode = false;
    private int copylength = 0;

    private static final int BUFFERSZ = 32768;
    private byte[] buffer = new byte[BUFFERSZ];
    private boolean dbcorrupted = false;

    private boolean confirmaction = false;
    private String exportdata = "";
    private boolean developermode;
    private boolean helpoffmode;


    private ArrayList<String> errlist = new ArrayList<>();
    private ArrayList<String> sqlcmds = new ArrayList<>();
    private Context context;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datahandlingmain);
        context = this;


        sp = PreferenceManager.getDefaultSharedPreferences(context);

        // Show/Hide Help
        helpoffmode = sp.getBoolean(
                getResources().getString(R.string.sharedpreferencekey_showhelpmode),
                false
        );
        datahandlinghelp = (LinearLayout) this.findViewById(R.id.dh_help_layout);
        if(helpoffmode) {
            datahandlinghelp.setVisibility(View.GONE);
        } else {
            datahandlinghelp.setVisibility(View.VISIBLE);
        }

        // Get developermode from shared preferences
        developermode = sp.getBoolean(
                getResources().getString(
                        R.string.sharedpreferencekey_developermode
                ),
                false
        );
        // Get strictbackupmode from shared preferences
        // if false allows files with BaseFileName or FileExtenstion
        // to be listed.
        // if true files must have BaseFileName AND FileExtension
        // to be listed.
        strictbackupmode = sp.getBoolean(
                getResources().getString(
                        R.string.sharedpreferencekey_strictbackupmode),
                        true
        );

        backupdir = (TextView) this.findViewById(R.id.dh_backupdir);
        datetimepart = (EditText) this.findViewById(R.id.dh_datetimepart);

        busy = new ProgressDialog(this);
        busy.setTitle("Work in Progress");
        busy.setCancelable(true);


        //Create a StoreData instance to get the directory to be used
        sdbase =  new StoreData("ShopperBackups","xxx",true);
        backupdir.setText(sdbase.getDirectory());
        // Get the date/time and apply it to the datetime portion
        datetimestr = getDateandTimeasYYMMDDhhmm();
        datetimepart.setText(datetimestr);

        // Prepare to use the EditText's i.e. retrieve their id's
        dbbasefilename = (EditText) findViewById(R.id.dh_dbsave_basefilename);
        dbfileext = (EditText) findViewById(R.id.dh_dbsave_fileext);
        dbfullfilename = (TextView) findViewById(R.id.dh_dbsave_fullfilename);
        dbrestore_spinner = (Spinner) findViewById(R.id.dh_dbrestore_spinner);
        dbbutton = (TextView) findViewById(R.id.dh_dbsave);
        dbfcnt = (TextView) findViewById(R.id.dh_dbrestore_fcnt);
        dbrestorebutton = (TextView) findViewById(R.id.dh_dbrestore);

        sqlbasefilename = (EditText) findViewById(R.id.dh_sqlsave_basefilename);
        sqlfileext = (EditText) findViewById(R.id.dh_sqlsave_fileext);
        sqlfullfilename = (TextView) findViewById(R.id.dh_sqlsave_fullfilename);
        sqlrestore_spinner = (Spinner) findViewById(R.id.dh_sqlrestore_spinner);
        sqlbutton = (TextView) findViewById(R.id.dh_sqlsave);
        sqlfcnt = (TextView) findViewById(R.id.dh_sqlrestore_fcnt);
        sqlrestorebutton = (TextView) findViewById(R.id.dh_sqlrestore);

        dumpbasefilename = (EditText) findViewById(R.id.dh_dumpsave_basefilename);
        dumpfileext = (EditText) findViewById(R.id.dh_dumpsave_fileext);
        dumpfullfilename = (TextView) findViewById(R.id.dh_dumpsave_fullfilename);
        dumprestore_spinner = (Spinner) findViewById(R.id.dh_dumprestore_spinner);
        dumpbutton = (TextView) findViewById(R.id.dh_dumpsave);
        dumpfcnt = (TextView) findViewById(R.id.dh_dumprestore_fcnt);

        csvbasefilename = (EditText) findViewById(R.id.dh_csvsave_basefilename);
        csvfileext = (EditText) findViewById(R.id.dh_csvsave_fileext);
        csvfullfilename = (TextView) findViewById(R.id.dh_csvsave_fullfilename);
        csvbutton = (TextView) findViewById(R.id.dh_csvsave);

        //Set all of the full filenames according to the 3 components that they
        // are comprised off.
        setFullFilenames();

        // Set handlers for all edit text's
        // Basically if any are changed then all fullfilenames are rebuilt
        setEditTextTextChangedListener(datetimepart);
        setEditTextTextChangedListener(dbbasefilename);
        setEditTextTextChangedListener(dbfileext);
        setEditTextTextChangedListener(sqlbasefilename);
        setEditTextTextChangedListener(sqlfileext);
        setEditTextTextChangedListener(dumpbasefilename);
        setEditTextTextChangedListener(dumpfileext);
        setEditTextTextChangedListener(csvbasefilename);
        setEditTextTextChangedListener(csvfileext);

        populateAllRestoreSpinners();
    }

    /**
     * Method setEdittextTextChangedListener - Generic Listener
     *      update the filenames when editable fields that affect the
     *      filename are changed.
     * @param et - Edittext to listen to
     */
    private void setEditTextTextChangedListener(EditText et) {
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setFullFilenames();
                populateAllRestoreSpinners();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // handle all button clicks (note actually TextViews that act like buttons)
    // NOTE!! still work-in-progress
    public void onButtonClick(View view) {
        boolean refreshspinners = false;
        switch (view.getId()) {
            case    R.id.dh_dbsave:
                saveDB();
                refreshspinners = true;
                break;
            case R.id.dh_dbrestore:
                restoreDB();
                break;
            case R.id.dh_sqlsave:
                saveSQL();
                refreshspinners = true;
                break;
            case R.id.dh_sqlrestore:
                restoreSQL();
                break;
            case R.id.dh_dumpsave:
                Toast.makeText(this,"Data Backup button clicked",Toast.LENGTH_SHORT).show();
                refreshspinners = true;
                break;
            case R.id.dh_dumprestore:
                Toast.makeText(this,"Data Restore button clicked",Toast.LENGTH_SHORT).show();
                break;
            case R.id.dh_csvsave:
                Toast.makeText(this,"CSV Save button clicked",Toast.LENGTH_SHORT).show();
                break;
            case R.id.dh_datetimepartreset:
                datetimestr = getDateandTimeasYYMMDDhhmm();
                datetimepart.setText(datetimestr);
                break;
            case R.id.dh_done:
                this.finish();
                break;
            default:
                break;
        }
        if(refreshspinners) {
            populateAllRestoreSpinners();
        }
    }

    /**
     * return the date and time as a string in yyyymmddhhmm format
     * @return String rv as formatted date and time
     */
    private String getDateandTimeasYYMMDDhhmm() {
        Calendar cldr = Calendar.getInstance();
        String rv = "";
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        rv = "_" + sdf.format(cldr.getTime());
        return rv;
    }

    /**
     * Method setFullFileNames - Combine the 3 parts for each of the 4
     * types of data backup and then set the respective type's textview
     * to display the text.
     *
     * The 3 parts to a full file name are:
     *      1) The base filename for that type.
     *      2) The date time portion
     *      3) The file extension for that type
     *
     */
    private void setFullFilenames() {
        String dtp = datetimepart.getText().toString();
        String dbffn = dbbasefilename.getText().toString() +
                dtp +
                dbfileext.getText().toString();
        String sqlffn = sqlbasefilename.getText().toString() +
                dtp +
                sqlfileext.getText().toString();
        String dumpffn = dumpbasefilename.getText().toString() +
                dtp +
                dumpfileext.getText().toString();
        String csvffn = csvbasefilename.getText().toString() +
                dtp +
                csvfileext.getText().toString();
        dbfullfilename.setText(dbffn);
        sqlfullfilename.setText(sqlffn);
        dumpfullfilename.setText(dumpffn);
        csvfullfilename.setText(csvffn);
    }

    /**
     * method populateAllrestoreSpinners - invoke the populateRestoreSpinner
     *      for all the respective types
     */
    private void populateAllRestoreSpinners() {
        // DB
        populateRestoreSpinner(dbrestore_spinner,
                dbfcnt,
                dbbasefilename.getText().toString(),
                dbfileext.getText().toString(),
                sdbase,
                dbrestorebutton
        );
        //SQL
        populateRestoreSpinner(sqlrestore_spinner,
                sqlfcnt,
                sqlbasefilename.getText().toString(),
                sqlfileext.getText().toString(),
                sdbase,
                sqlrestorebutton
        );
        //DUMP
        populateRestoreSpinner(dumprestore_spinner,
                dumpfcnt,
                dumpbasefilename.getText().toString(),
                dumpfileext.getText().toString(),
                sdbase,
                null
        );
    }

    /**
     * method populateRestoreSpinner - Populate a restore spinner
     *      with the appropriate files for the respective restore
     * @param spn           Spinner
     * @param basefilename  basefilename
     * @param fileext       fileextension
     * @param sd            StoreData object
     */
    @SuppressWarnings("ParameterCanBeLocal")
    private void populateRestoreSpinner(Spinner spn, TextView tv, String basefilename, String fileext, StoreData sd, TextView restorebutton) {

        String spnname = "";
        boolean used = false;
        int fcount = 0;
        ArrayList<File> reverseflist = new ArrayList<>();

        //
        sd = new StoreData("ShopperBackups","xxx",true);
        sd.refreshOtherFilesInDirectory();

        // Build the File ArrayList
        ArrayList<File> flst = new ArrayList<>(sd.getFilesInDirectory());
        if(flst.size() < 1) {
            Toast.makeText(this,"No Saved Files Found",Toast.LENGTH_SHORT).show();
        }

        // Ascertain the relevant files that are needed for the restore backup
        // file selector
        for(int i = 0; i < flst.size(); i++) {
            used = false;
            boolean endingok = flst.get(i).getName().endsWith(fileext);
            boolean containsok = flst.get(i).getName().contains(basefilename);
            if((strictbackupmode && endingok && containsok)
                || (!strictbackupmode && (endingok || containsok))) {
                used = true;
                fcount++;
            } else {
                flst.remove(i);
                i--;
            }
        }

        // Reverse the order of the list so most recent backups appear first
        // Also hide/show the Restore button and spinner according to if
        // files exist or not
        // (doing nothing in the case where the is no restore button i.e.
        //  null has been passed)
        if(flst.size() > 0) {
            for (int i = (flst.size() -1); i >= 0; i--) {
                reverseflist.add(flst.get(i));
            }
            if (restorebutton != null) {
                spn.setVisibility(View.VISIBLE);
                restorebutton.setVisibility(View.VISIBLE);
            }
        } else {
            if (restorebutton != null) {
                spn.setVisibility(View.INVISIBLE);
                restorebutton.setVisibility(View.INVISIBLE);
            }
        }

        // Set the available count for display
        String bcnt = "Available Backups=" + Integer.toString(reverseflist.size());
        tv.setText(bcnt);

        // Set the spinner adapter and dropdown layout and then set the
        // spinner's adapter
        DataHandlingFileListAdapter dhfla = new DataHandlingFileListAdapter(this,
                R.layout.datahandlingfilelistselector,
                reverseflist);
        dhfla.setDropDownViewResource(R.layout.datahandlingfilelistdropdownentry);
        spn.setAdapter(dhfla);
    }

    /**
     * method saveDB save a file copy of the Database
     */
    private void saveDB() {
        busy.show();
        errlist.clear();
        confirmaction = true;
        String dbfilename = this.getDatabasePath(ShopperDBHelper.DATABASE_NAME).getPath();
        dbfile = new File(dbfilename);
        backupfilename = backupdir.getText().toString() + "//" + dbfullfilename.getText().toString();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FileInputStream fis = new FileInputStream(dbfile);
                    OutputStream backup = new FileOutputStream(backupfilename);

                    byte[] buffer = new byte[32768];
                    int length;
                    while((length = fis.read(buffer)) > 0) {
                        backup.write(buffer, 0, length);
                    }
                    backup.flush();
                    backup.close();
                    fis.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                    errlist.add("Database backup failed with an IO Error. Error Message was " +
                            e.getMessage() +
                            "/n/tFile Name was " +
                            backupfilename);
                    confirmaction = false;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        busy.dismiss();
                        AlertDialog.Builder dbbackupresult = new AlertDialog.Builder(context);
                        dbbackupresult.setCancelable(true);
                        if(confirmaction) {
                            dbbackupresult.setTitle("DB Data Backed up OK.");
                            dbbackupresult.setMessage("DB Data successfully saved in file \n\t" +
                            backupfilename );
                        } else {
                            dbbackupresult.setTitle("DB Backup Failed.");
                            String emsg = "";
                            for(int i = 0; i < errlist.size(); i++) {
                                emsg = emsg + errlist.get(i);
                            }
                        }
                        dbbackupresult.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
                    }
                });
            }
        }).start();
    }

    /**
     * method restoreDB - Prepare for and confirm database restore
     */
    private void restoreDB() {

        //Prepare filenames
        //      currentdbfilename = curreant Shopper DataBase
        //      copydbfilename = filename used when renaming current
        //      icdbfilename = filename of an intermediate/test database used
        //                      to perform an integrity check to see if the
        //                      restore file creates a valid database.
        currentdbfilename = this.getDatabasePath(
                ShopperDBHelper.DATABASE_NAME)
                .getPath();
        copydbfilename = currentdbfilename +
                "OLD" +
                getDateandTimeasYYMMDDhhmm();
        icdbfilename = currentdbfilename.substring(0,
                currentdbfilename.lastIndexOf(ShopperDBHelper.DATABASE_NAME))
                + "IC" + ShopperDBHelper.DATABASE_NAME;

        // Get the backup file, checking that there is one (should always be one
        // as the restore button should be hidden if there are no files
        if(dbrestore_spinner.getSelectedItemPosition() == AdapterView.INVALID_POSITION) {
            AlertDialog.Builder notokdialog = new AlertDialog.Builder(this);
            notokdialog.setTitle("No DB Restore File.");
            notokdialog.setMessage("There is no file to restore from selected." +
                    "\n\nThe restore request cannot be undertaken" +
                    " and will be cancelled. "
            );
            notokdialog.setCancelable(true);
            notokdialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            }).show();
            return;
        }
        backupfilename = dbrestore_spinner.getSelectedItem().toString();

        // Confirm restore request is wanted and if so invoke it BUT only
        // if the itegrity check is passed
        // if integrity check is passed the invoke doDBRestore method which does the restore
        AlertDialog.Builder okdialog = new AlertDialog.Builder(this);
        okdialog.setTitle("Database Restore Requested");
        okdialog.setMessage(
                "A database restore has been requested." +
                "\n\nThe request will use the following file to recover from:" +
                "\n\t" + backupfilename + " ." +
                "\n\nIf the standard provided filenames are used then the database will be" +
                " recovered to the date/time as per the file." +
                "\n\nAs part of the restore process, a copy of the current database will " +
                "be created. This will be named:" +
                "\n\t" + copydbfilename +
                "\n\n Should the restore fail after the original database has been deleted, " +
                " the original database will be restored from this copy." +
                        "\n\n" +
                "Do you wish to continue with the database restore?"

        );
        okdialog.setCancelable(true);
        okdialog.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dataBaseIntegrityCheck()) {
                    doDBRestore();
                }
            }
        });
        okdialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                confirmaction = false;
            }
        });
        okdialog.show();
    }

    /**
     *
     * @return false if the backup file is invalid.
     *
     *  determine by creating a differently name database (prefixed with IC), openein it with it's
     *  own helper (does nothing) and then trying to check if there are tables in the database.
     *  No tables reflects that file is invalid type.
     *
     *  Note! if an attempt to open an invalid database file then SQLite deletes the file.
     */
    private boolean dataBaseIntegrityCheck() {

        final String THIS_METHOD = "dataBaseIntegrityCheck";
        String sqlstr_mstr = "SELECT name FROM sqlite_master WHERE type = 'table' AND name!='android_metadata' ORDER by name;";
        Cursor iccsr;
        boolean rv = true;

        //Note no use having the handler as it actualy introduces problems  as SQLite assumes that
        // the handler will restore the database.
        // No need to comment out as handler can be disabled by not not passing it as a parameter
        // of the DBHelper
        DatabaseErrorHandler myerrorhandler = new DatabaseErrorHandler() {
            @Override
            public void onCorruption(SQLiteDatabase sqLiteDatabase) {
                mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"DB onCorruption error handler invoked",THIS_ACTIVITY,THIS_METHOD,developermode);
            }
        };
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Restore Databae Integrity Check - Starting",THIS_METHOD,THIS_ACTIVITY,developermode);
        try {
            FileInputStream bkp = new FileInputStream(backupfilename);
            OutputStream ic = new FileOutputStream(icdbfilename);
            while ((copylength = bkp.read(buffer)) > 0) {
                ic.write(buffer, 0, copylength);
            }
            ic.close();
            bkp.close();

            mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"restore Database Integrity Check - IC Database created",THIS_METHOD,THIS_ACTIVITY,developermode);

            //Note SQLite will actually check for corruption and if so delete the file
            //
            IntegrityCheckDBHelper icdbh = new IntegrityCheckDBHelper(this,null,null,1,null);
            SQLiteDatabase icdb = icdbh.getReadableDatabase();

            //Check to see if there are any tables, if wrong file type shouldn't be any
            iccsr = icdb.rawQuery(sqlstr_mstr,null);
            if(iccsr.getCount() < 1) {
                errlist.add("Integrity Check extract from sqlite_master returned nothing - Propsoed file is corrupt or not a database file.");
                rv = false;
            }
            iccsr.close();
            icdb.close();

        } catch (IOException e) {
            e.printStackTrace();
            errlist.add("Integrity Check Failed Error Message was " + e.getMessage());
        }

        if(!rv) {
            AlertDialog.Builder notokdialog = new AlertDialog.Builder(this);
            notokdialog.setTitle("Invalid Restore File.");
            notokdialog.setCancelable(true);
            String msg = "File " + backupfilename + " is an invalid file." +
                    "\n\nThe Restore cannot continue and will be canclled. " +
                    "\n\nPlease Use a Valid Database Backup File!";
            notokdialog.setMessage(msg);
            notokdialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            }).show();
        }
        return rv;
    }

    /**
     * method dorestore - Restore Database in 3 stages
     *      1) make a copy of the databasefile
     *      2) delete the database
     *      3) create the database populating by copying from the designated backup
     *      If an IOexception occurs and the database has been deleted revert to the
     *      copy
     */
    private void doDBRestore() {

        confirmaction = true;
        logtag = "DB RESTORE";
        //ArrayList<String> errorlist = new ArrayList<>();
        resulttitle = "Restore Failed.";
        errlist.clear();
        dbfile = new File(currentdbfilename);

        busy.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Stage 1 Create a copy of the database
                    Log.i(logtag, "Stage 1 (make Copy of current DB)Starting");
                    FileInputStream fis = new FileInputStream(dbfile);
                    OutputStream backup = new FileOutputStream(copydbfilename);
                    while ((copylength = fis.read(buffer)) > 0) {
                        backup.write(buffer, 0, copylength);
                    }
                    backup.flush();
                    backup.close();
                    fis.close();
                    Log.i(logtag, "Stage 1 - Complete. Copy made of current DB.");
                    copytaken = true;

                    // Stage 2 - Delete the database file
                    if (dbfile.delete()) {
                        Log.i(logtag, "Stage 2 - Completed. Original DB deleted.");
                        origdeleted = true;
                    }

                    // Stage 3 copy from the backup to the deleted database file i.e. create it
                    Log.i(logtag, "Stage 3 - (Create new DB from backup) Starting.");
                    FileInputStream bkp = new FileInputStream(backupfilename);
                    OutputStream restore = new FileOutputStream(currentdbfilename);
                    copylength = 0;
                    while ((copylength = bkp.read(buffer)) > 0) {
                        restore.write(buffer, 0, copylength);
                    }
                    Log.i(logtag, "Stage 3 - Data Written");
                    restore.flush();
                    restore.close();
                    Log.i(logtag, "Stage 3 - New DB file flushed and closed");
                    restoredone = true;
                    bkp.close();
                    Log.i(logtag, "Stage 3 - Complete.");
                } catch (IOException e) {
                    e.printStackTrace();
                    if(!copytaken) {
                        errlist.add("Restore failed copying current database. Error was " + e.getMessage());
                    } else {
                        if(!origdeleted) {
                            errlist.add("Restore failed to delete current database. Error was " + e.getMessage());
                        }
                        else {
                            if(!restoredone) {
                                errlist.add("Restore failed to recreate the database from the backup. Error was "+ e.getMessage());
                                errlist.add("Restore will attempt to revert to the original database.");
                            }
                        }
                    }
                }
                // Ouch restore not done but DB deleted so recover from
                // copy by renaming copy
                if (copytaken && origdeleted && !restoredone) {

                    Log.w(logtag, "Restore failed. Recovering DB after failed restore from backup");
                    File rcvdbfile = new File(copydbfilename);
                    rcvdbfile.renameTo(dbfile);
                    Log.w(logtag, "Restore failed. DB Recovered from backup now in original state.");
                    rolledback = true;
                    errlist.add("Database reverted to original.");
                }
                if (copytaken && !origdeleted) {
                    Log.w(logtag, "Restore failed. Original DB not deleted so original" +
                            " is being used.");
                }
                if(!copytaken) {
                    Log.w(logtag,"Restore failed. Attempt to Copy original DB failed." +
                            " Original DB is being used.");
                }
                if(copytaken && origdeleted && restoredone) {
                    errlist.add("Database successfully restored.");
                    resulttitle = "Restore was successful.";

                }
                for(int i = 0; i < errlist.size(); i++){
                    if(i > 0) {
                        finalmessage = finalmessage + "\n\n";
                    }
                    finalmessage = finalmessage + errlist.get(i);
                }
                //busy.dismiss();


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        busy.dismiss();
                        AlertDialog.Builder resultdialog = new AlertDialog.Builder(context);
                        resultdialog.setTitle(resulttitle);
                        resultdialog.setMessage(finalmessage);
                        resultdialog.setCancelable(true);
                        resultdialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        resultdialog.show();
                    }
                });
            }
        }).start();
    }

    /**
     * method saveSQL - Save Database and structure as SQL
     */
    private void saveSQL() {
        busy.show();
        errlist.clear();
        confirmaction = true;
        backupfilename = backupdir.getText().toString() + "//" + sqlfullfilename.getText().toString();

        final ShopperDBHelper db = new ShopperDBHelper(this,null,null,1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                exportdata = db.getExportSQL(); //Note this is what takes most of the time
                db.close();
                File ssql = new File(backupfilename+"tmp");

                try {
                    ssql.createNewFile();
                    FileOutputStream fos = new FileOutputStream(ssql);
                    OutputStreamWriter osw = new OutputStreamWriter(fos);
                    osw.write(exportdata);
                    osw.flush();
                    osw.close();
                    fos.flush();
                    fos.close();
                    ssql.renameTo( new File(backupfilename));
                }
                catch (IOException e) {
                    e.printStackTrace();
                    errlist.add("SQL backup Failed with and IO Error. Error Message was " +
                            e.getMessage() +
                            "/n/tFile Name was " +
                            backupfilename);
                    confirmaction = false;

                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        busy.dismiss();
                        populateAllRestoreSpinners();
                        AlertDialog.Builder savesqlerr = new AlertDialog.Builder(context);
                        savesqlerr.setCancelable(true);
                        if(confirmaction) {
                            savesqlerr.setTitle("SQL Data Backed up OK.");
                            savesqlerr.setMessage("SQL Data successfully saved in file \n\t" +
                                    backupfilename);
                        } else {
                            savesqlerr.setTitle("SQL Backup Failed.");
                            String emsg = "";
                            for(int i = 0; i < errlist.size(); i++) {
                                emsg = emsg + errlist.get(i);
                            }
                            savesqlerr.setMessage(emsg);
                        }
                        savesqlerr.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
                    }
                });
            }
        }).start();
    }

    /**
     * method restoreSQL - prepare to restore database from SQL backup
     * prompting for confirmation.
     */
    private void restoreSQL() {
        currentdbfilename = this.getDatabasePath(ShopperDBHelper.DATABASE_NAME).getPath();
        copydbfilename = currentdbfilename + "OLDSQL" + getDateandTimeasYYMMDDhhmm();
        if(sqlrestore_spinner.getSelectedItemPosition() == AdapterView.INVALID_POSITION) {
            AlertDialog.Builder notokdialog = new AlertDialog.Builder(this);
            notokdialog.setTitle("No SQL Restore File.");
            notokdialog.setMessage("There is no file to restore from selected." +
                    "\n\nThe restore request cannot be undertaken," +
                    " so the request will be cancelled."
            );
            notokdialog.setCancelable(true);
            notokdialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            }).show();
            return;
        }
        backupfilename = sqlrestore_spinner.getSelectedItem().toString();

        AlertDialog.Builder okdialog = new AlertDialog.Builder(this);
        okdialog.setTitle("SQL Database Restore Requested.");
        okdialog.setMessage(
                "An SQL Datbase restore has been requested." +
                        "\n\nThe request will use the following file to restore from:" +
                        "\n\t" + backupfilename + " ." +
                        "\n\nIf the standard provided filenames are used, the the database will be " +
                        "recovered to the date/time as per the filename." +
                        "\n\nAs part of the process, a copy of the current database will " +
                        "be created. This will be named:" +
                        "\n\t" + copydbfilename +
                        "\n\nThis will be used, should the restore fail, to recover the " +
                        "orignal database." +
                        "\n\nDo you wish to continue with the restore?"
        );
        okdialog.setCancelable(true);
        okdialog.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doSQLRestore();
            }
        });
        okdialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        okdialog.show();
    }

    /**
     * Method doSQLRestore - Restore the database from an SQL backup
     * 3 stage process.
     *      Stage 1 Copy the current database and retrieve the SQL
     *      Stage 2 Delete the current database.
     *      Stage 3 Run the SQL commands
     *
     */
    private void doSQLRestore() {
        busy.show();
        logtag = "SQL Restore";
        //ArrayList<String> errorlist = new ArrayList<>();
        errlist.clear();
        resulttitle = "Restore failed.";
        restoresql = "";

        dbfile = new File(currentdbfilename);
        rstsql = new File(backupfilename);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FileInputStream fis = new FileInputStream(currentdbfilename);
                    OutputStream backup = new FileOutputStream(copydbfilename);
                    while ((copylength = fis.read(buffer)) > 0) {
                        backup.write(buffer, 0, copylength);
                    }
                    backup.flush();
                    backup.close();
                    fis.close();
                    Log.i(logtag, "Stage 1 - Complete. Copy made of the current DB.");
                    copytaken = true;

                    Log.i(logtag,"Stage 1A - Retrieving SQL initiated");
                    FileInputStream sqlfis = new FileInputStream(rstsql);
                    InputStreamReader sqlisr = new InputStreamReader(sqlfis);
                    BufferedReader sqlbr = new BufferedReader(sqlisr);
                    StringBuilder sqlsb = new StringBuilder();
                    String cline = null;
                    int linecount = 0;

                    while((cline = sqlbr.readLine()) != null) {
                        linecount++;
                        if(cline.startsWith("--CRTTB_START")) {
                            skip_mode = true;
                            continue;
                        }
                        if(cline.startsWith("--CRTTB_FINISH")) {
                            skip_mode = false;
                            continue;
                        }
                        if(cline.startsWith("--TBL_INSERTSTART")) {
                            concat_mode = true;
                            continue;
                        }
                        if(cline.startsWith("--TBL_INSERTFINISH")) {
                            concat_mode = false;
                            sqlcmds.add(sqlsb.toString());
                            sqlsb.setLength(0);
                            continue;
                        }
                        if(skip_mode) {continue;}
                        if(concat_mode) {
                            sqlsb.append(cline).append("\n");
                        }
                    }
                    restoresql = sqlsb.toString();
                    sqlfis.close();
                    sqlbr.close();
                    Log.i(logtag,"Stage 1A - Complete. SQL retrieved." + Integer.toString(linecount) + " Records read.");
                    sqlretrieved = true;

                    //Stage 2 - Delete the database file
                    if(dbfile.delete()) {
                        Log.i(logtag, "Stage 2 - Complete. Original DB deleted.");
                        origdeleted = true;
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                    if (!copytaken) {
                        errlist.add("Restore failed copying the current databse. Error was " +
                                e.getMessage());
                    }
                    else {
                        if(!sqlretrieved) {
                            errlist.add("Restore failed retrieving SQL from SQL backup file." +
                                    " Error was " + e.getMessage()
                            );
                        }
                        else {
                            if (!origdeleted) {
                                errlist.add("Restore failed to delete the current database. Error was" +
                                        e.getMessage()
                                );
                            }
                        }
                    }
                }
                if(copytaken && origdeleted) {
                    Log.i(logtag,"Stage 3 - Initiating SQL restore.");
                    ShopperDBHelper db = new ShopperDBHelper(context,null,null,1);
                    Log.i(logtag,"Stage 3 - SQLite Database created");
                    SQLiteDatabase rdb = db.getWritableDatabase();
                    Log.i(logtag,"Readying to insert " + Integer.toString(sqlcmds.size()) + " SQL commands." );
                    rdb.beginTransaction();
                    for(int i = 0; i < sqlcmds.size();i++) {
                        //Log.i(logtag,"Attempting SQL command " + Integer.toString(i + 1) + " of " + Integer.toString(sqlcmds.size()));
                        SQLiteStatement insert = rdb.compileStatement(sqlcmds.get(i));
                        try {
                            insert.execute();
                            if(i == (sqlcmds.size() -1)) {
                                restoredone = true;
                            }
                        }
                        catch (SQLiteException e) {
                            e.printStackTrace();
                            restoredone = false;
                        }
                    }
                    rdb.setTransactionSuccessful();
                    rdb.endTransaction();
                    rdb.close();
                }
                // Ouch restore not done but DB deleted so recover from
                // copy by renaming copy
                if (copytaken && origdeleted && !restoredone) {

                    Log.w(logtag, "Restore failed. Recovering DB after failed restore from backup");
                    File rcvdbfile = new File(copydbfilename);
                    rcvdbfile.renameTo(dbfile);
                    Log.w(logtag, "Restore failed. DB Recovered from backup now in original state.");
                    rolledback = true;
                    errlist.add("Database reverted to original.");
                }
                if (copytaken && !origdeleted) {
                    Log.w(logtag, "Restore failed. Original DB not deleted so original" +
                            " is being used.");
                }
                if(!copytaken) {
                    Log.w(logtag,"Restore failed. Attempt to Copy original DB failed." +
                            " Original DB is being used.");
                }
                if(copytaken && origdeleted && restoredone) {
                    errlist.add("Database successfully restored.");
                    resulttitle = "Restore was successful.";
                }
                for(int i = 0; i < errlist.size(); i++){
                    if(i > 0) {
                        finalmessage = finalmessage + "\n\n";
                    }
                    finalmessage = finalmessage + errlist.get(i);
                }

             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     busy.dismiss();
                     populateAllRestoreSpinners();
                     AlertDialog.Builder resultdialog = new AlertDialog.Builder(context);
                     resultdialog.setTitle(resulttitle);
                     resultdialog.setMessage(finalmessage);
                     resultdialog.setCancelable(true);
                     resultdialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int which) {
                         }
                     });
                     resultdialog.show();
                 }
             });
            }
        }).start();
    }
}
