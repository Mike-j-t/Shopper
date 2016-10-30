package mjt.shopper;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AtomicFile;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
public class MainDataHandlingActivity extends AppCompatActivity {

    private TextView backupdir;
    private EditText datetimepart;
    private String datetimestr = "";

    private String backupfilename = "";
    private String currentdbfilename = "";
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

    private EditText sqlbasefilename;
    private EditText sqlfileext;
    private TextView sqlfullfilename;
    private Spinner sqlrestore_spinner;
    private TextView sqlbutton;
    private TextView sqlfcnt;

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
    private StoreData sdbase;

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

    private boolean confirmaction = false;
    private String exportdata = "";


    private ArrayList<String> errlist = new ArrayList<>();
    private ArrayList<String> sqlcmds = new ArrayList<>();
    private Context context;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datahandlingmain);
        context = this;

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

        sqlbasefilename = (EditText) findViewById(R.id.dh_sqlsave_basefilename);
        sqlfileext = (EditText) findViewById(R.id.dh_sqlsave_fileext);
        sqlfullfilename = (TextView) findViewById(R.id.dh_sqlsave_fullfilename);
        sqlrestore_spinner = (Spinner) findViewById(R.id.dh_sqlrestore_spinner);
        sqlbutton = (TextView) findViewById(R.id.dh_sqlsave);
        sqlfcnt = (TextView) findViewById(R.id.dh_sqlrestore_fcnt);

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
    public void setEditTextTextChangedListener(EditText et) {
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
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
                sdbase
        );
        //SQL
        populateRestoreSpinner(sqlrestore_spinner,
                sqlfcnt,
                sqlbasefilename.getText().toString(),
                sqlfileext.getText().toString(),
                sdbase);
        //DUMP
        populateRestoreSpinner(dumprestore_spinner,
                dumpfcnt,
                dumpbasefilename.getText().toString(),
                dumpfileext.getText().toString(),
                sdbase);
    }

    /**
     * method populateRestoreSpinner - Populate a restore spinner
     *      with the appropriate files for the respective restore
     * @param spn   - Spinner
     * @param basefilename  - basefilename
     * @param fileext - fileextension
     * @param sd - StoreData
     */
    private void populateRestoreSpinner(Spinner spn, TextView tv, String basefilename, String fileext, StoreData sd) {

        String spnname = "";
        boolean used = false;
        int fcount = 0;
        ArrayList<File> reverseflist = new ArrayList<>();

        //
        sd = new StoreData("ShopperBackups","xxx",true);
        sd.refreshOtherFilesInDirectory();

        // Build the File ArrayList
        ArrayList<File> flst = new ArrayList<File>(sd.getFilesInDirectory());
        if(flst.size() < 1) {
            Toast.makeText(this,"No Saved Files Found",Toast.LENGTH_SHORT).show();
        }

        // Ascertain the relevant files that are needed for the restore backup
        // file selector
        for(int i = 0; i < flst.size(); i++) {
            used = false;
            if(flst.get(i).getName().endsWith(fileext)) {
                used = true;
                fcount++;
            }
            else {
                if(flst.get(i).getName().contains(basefilename)) {
                    used = true;
                    fcount++;
                }
            }
            if(!used) {
                flst.remove(i);
                i--;
            }
        }

        // Reverse the order of the list so most recent backups appear first
        if(flst.size() > 0) {
            for (int i = (flst.size() -1); i >= 0; i--) {
                reverseflist.add(flst.get(i));
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

        currentdbfilename = this.getDatabasePath(
                ShopperDBHelper.DATABASE_NAME)
                .getPath();
        copydbfilename = currentdbfilename +
                "OLD" +
                getDateandTimeasYYMMDDhhmm();
        backupfilename = dbrestore_spinner.getSelectedItem().toString();

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
                doDBRestore();
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
