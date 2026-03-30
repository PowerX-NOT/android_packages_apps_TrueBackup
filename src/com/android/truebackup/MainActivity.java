import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ITrueBackupService;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private static final String TAG = "TrueBackup-App";
    private static final int REQUEST_CODE_PICK_DIR = 1001;
    private static final String PREF_NAME = "TrueBackupPrefs";
    private static final String KEY_BACKUP_PATH = "backup_path";

    private ITrueBackupService mService;
    private AppAdapter mAdapter;
    private TextView mPathText;
    private List<AppInfo> mApps = new ArrayList<>();
    private String mBackupPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mService = ITrueBackupService.Stub.asInterface(ServiceManager.getService("truebackup"));
        if (mService == null) {
            Toast.makeText(this, "Service not found!", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Service is null");
        }

        mPathText = findViewById(R.id.tv_backup_path);
        loadBackupPath();

        ListView listView = findViewById(R.id.app_list);
        mAdapter = new AppAdapter();
        listView.setAdapter(mAdapter);

        loadApps();

        findViewById(R.id.btn_change_path).setOnClickListener(v -> pickBackupDirectory());
        Button backupBtn = findViewById(R.id.btn_backup);
        backupBtn.setOnClickListener(v -> startBackup());
    }

    private void loadBackupPath() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        mBackupPath = prefs.getString(KEY_BACKUP_PATH, null);
        if (mBackupPath == null || mBackupPath.trim().isEmpty()) {
            mBackupPath = null;
            mPathText.setText("Backup Location: Not set");
        } else {
            mPathText.setText("Backup Location: " + mBackupPath);
        }
    }

    private void pickBackupDirectory() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, REQUEST_CODE_PICK_DIR);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_DIR && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                // For simplicity, we convert URI to a readable path if possible.
                // In system app with MANAGE_EXTERNAL_STORAGE, users usually expect real paths.
                String path = uri.getPath(); // This is a fallback
                // Real implementation should handle SAF URI or use a better conversion.
                // For this demonstration, we'll strip the tree part.
                if (path.contains(":")) {
                    path = "/sdcard/" + path.split(":")[1];
                }
                mBackupPath = path;
                
                getSharedPreferences(PREF_NAME, MODE_PRIVATE)
                    .edit().putString(KEY_BACKUP_PATH, mBackupPath).apply();
                mPathText.setText("Backup Location: " + mBackupPath);
            }
        }
    }

    private void loadApps() {
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        
        for (ApplicationInfo packageInfo : packages) {
            if ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                mApps.add(new AppInfo(
                    packageInfo.loadLabel(pm).toString(),
                    packageInfo.packageName,
                    packageInfo.loadIcon(pm)
                ));
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    private void startBackup() {
        if (mService == null) return;

        if (mBackupPath == null || mBackupPath.trim().isEmpty()) {
            Toast.makeText(this, "Please choose a backup location first", Toast.LENGTH_LONG).show();
            return;
        }
        
        for (AppInfo app : mApps) {
            if (app.selected) {
                try {
                    mService.backupPackage(app.packageName, mBackupPath);
                    Toast.makeText(this, "Backing up: " + app.name, Toast.LENGTH_SHORT).show();
                } catch (RemoteException e) {
                    Log.e(TAG, "Backup failed", e);
                }
            }
        }
    }

    private class AppInfo {
        String name;
        String packageName;
        android.graphics.drawable.Drawable icon;
        boolean selected = false;

        AppInfo(String name, String packageName, android.graphics.drawable.Drawable icon) {
            this.name = name;
            this.packageName = packageName;
            this.icon = icon;
        }
    }

    private class AppAdapter extends BaseAdapter {
        @Override
        public int getCount() { return mApps.size(); }
        @Override
        public Object getItem(int position) { return mApps.get(position); }
        @Override
        public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.app_list_item, parent, false);
            }
            AppInfo app = mApps.get(position);
            
            ImageView icon = convertView.findViewById(R.id.app_icon);
            TextView name = convertView.findViewById(R.id.app_name);
            TextView pkg = convertView.findViewById(R.id.app_package);
            CheckBox cb = convertView.findViewById(R.id.app_checkbox);

            icon.setImageDrawable(app.icon);
            name.setText(app.name);
            pkg.setText(app.packageName);
            cb.setChecked(app.selected);

            cb.setOnClickListener(v -> app.selected = cb.isChecked());

            return convertView;
        }
    }
}
