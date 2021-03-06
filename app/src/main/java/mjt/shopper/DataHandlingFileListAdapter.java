package mjt.shopper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
class DataHandlingFileListAdapter extends ArrayAdapter<File> {

    private Activity context;
    private List<File> flst;

    private TextView tv_filename;
    private TextView tv_filemod;
    @SuppressLint("SimpleDateFormat")private SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd yyyy 'at' HH:mm");

    DataHandlingFileListAdapter(Activity context, int layout, ArrayList<File> flst) {
        super(context, layout, flst);
        this.context = context;
        this.flst = flst;
    }

    public long getItemId(int position) {
        return position;
    }

    // This is the view used for the dropdown entries
    @Override
    public View getDropDownView(int position, final View convertview, ViewGroup parent) {
        View v = convertview;
        if (v == null) {
            v = LayoutInflater
                    .from(context)
                    .inflate(R.layout.datahandlingfilelistdropdownentry,parent,false);
        }
        File flentry = flst.get(position);
        if(position % 2 == 0) {
            v.setBackgroundColor(ContextCompat.getColor(context, R.color.colorlistviewroweven));
        } else {
            v.setBackgroundColor(ContextCompat.getColor(context, R.color.colorlistviewrowodd));
        }
        tv_filename = (TextView) v.findViewById(R.id.dhfl_filename);
        tv_filemod = (TextView) v.findViewById(R.id.dhfl_lastmodified);
        tv_filename.setText(flentry.getName());
        tv_filemod.setText(sdf.format(flentry.lastModified()));


        /*
        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(context,"Delete File Here???",Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        */
        return v;
    }


    //This is the view used for the displayed/selected entry
    @Override
    public View getView(int position, View convertview, ViewGroup parent) {
        View v = convertview;
        if(v == null) {
            v = LayoutInflater
                    .from(context)
                    .inflate(R.layout.datahandlingfilelistselector, parent, false);
        }

        File flentry = flst.get(position);

        if(flentry != null) {
            tv_filename = (TextView) v.findViewById(R.id.dhfl_filename);
            tv_filemod = (TextView) v.findViewById(R.id.dhfl_lastmodified);

            tv_filename.setText(flentry.getName());
            tv_filemod.setText(sdf.format(flentry.lastModified()));
        }
        return  v;
    }
}
