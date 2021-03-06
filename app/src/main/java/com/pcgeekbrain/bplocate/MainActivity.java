package com.pcgeekbrain.bplocate;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.pcgeekbrain.bplocate.interfaces.AsyncResponse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AsyncResponse, View.OnClickListener {
    public final String TAG = "MainActivity";
    private static String filename = "library.cache";
    private RecyclerView locations_recycler_view;
    private LocationsAdapter locations_adapter;
    private RecyclerView.LayoutManager locations_layout_manager;
    private ArrayList<Branch> branches = new ArrayList<>();
    private ArrayList<Branch> searchResults = new ArrayList<>();
    private SearchView searchView;
    private ImageView refresh;
    private boolean update = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File file = new File(getFilesDir(), filename);
        if (file.exists()){
            Log.d(TAG, "onCreate: file exists");
            try {
                ObjectInputStream ois = new ObjectInputStream(openFileInput(filename));
                branches = (ArrayList<Branch>) ois.readObject();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "onCreate: file does not exist");
            updateData();
        }

        locations_recycler_view = (RecyclerView) findViewById(R.id.locations_list);
        locations_recycler_view.setHasFixedSize(true);  //Improves Performance
        searchView = (SearchView)findViewById(R.id.search);
        refresh = (ImageView)findViewById(R.id.refresh);
        refresh.setOnClickListener(this);

        //Layout Manager Setup
        locations_layout_manager = new LinearLayoutManager(this);
        locations_recycler_view.setLayoutManager(locations_layout_manager);

        //Add an adapter
        locations_adapter = new LocationsAdapter(branches);
        locations_recycler_view.setAdapter(locations_adapter);

        searchView.setOnQueryTextListener(searchListener);
    }

    private SearchView.OnQueryTextListener searchListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return search(newText);
        }
    };

    private boolean search(String query){
        if (query.length() > 0){
            SearchTask searchTask = new SearchTask(this, branches);
            searchTask.execute(query);
            update = true;
        } else {
            update = false;
            locations_adapter.swap(branches);
        }
        return true;
    }

    private void updateData(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null){
            UpdateData download = new UpdateData(this);
            download.execute("http://misc.brooklynpubliclibrary.org/BigApps/BPL_branch_001.xml");
        } else {
            Toast.makeText(getApplicationContext(), "Cannot Update Data\nError: 141 - No Active Network", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Processes the result from the network
     * @param output
     */
    @Override
    public void processFinish(ArrayList<Branch> output) {
        Log.d(TAG, "processFinish: output.size -> "+output.size());
        branches.clear();
        branches.addAll(output);
        locations_adapter.swap(output);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(openFileOutput(filename, Context.MODE_PRIVATE));
            oos.writeObject(branches);
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void searchFinish(ArrayList<Branch> result) {
        if (update){
            Log.d(TAG, "searchFinish: updating search");
            searchResults.clear();
            searchResults.addAll(result);
            locations_adapter.swap(searchResults);
        }
    }

    @Override
    public void onClick(View view) {
        updateData();
        locations_adapter.updateTime();
        Toast.makeText(getApplicationContext(), "Updating...", Toast.LENGTH_LONG).show();
    }
}
