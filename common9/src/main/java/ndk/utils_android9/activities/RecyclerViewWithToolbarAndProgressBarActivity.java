package ndk.utils_android9.activities;

import android.app.SearchManager;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.util.Pair;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import ndk.utils_android1.ActivityWithContexts;
import ndk.utils_android1.ExceptionUtils;
import ndk.utils_android1.SharedPreferencesUtils1;
import ndk.utils_android3.HttpApiSelectTask;
import ndk.utils_android3.HttpApiSelectTaskWrapper;
import ndk.utils_android9.R;

public abstract class RecyclerViewWithToolbarAndProgressBarActivity extends ActivityWithContexts {

    private ProgressBar progressBar;
    public RecyclerView recyclerView;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview_with_toolbar_and_progressbar);

        Toolbar toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);

        setSupportActionBar(toolbar);

        sharedPreferences = SharedPreferencesUtils1.getSharedPreferences(currentApplicationContext, configureApplicationName());

        fetchData();
    }

    public abstract String configureApplicationName();

    public void fetchData() {

        HttpApiSelectTask.AsyncResponseJSONArray httpApiGetDbSelectTaskResponseHandler = jsonArray -> {

            for (int i = 1; i < jsonArray.length(); i++) {

                try {

                    processJsonObjectInFetchedJsonArray(jsonArray.getJSONObject(i));
                    viewRecyclerView();

                } catch (JSONException exception) {

                    ExceptionUtils.handleExceptionOnGui(getApplicationContext(), configureApplicationName(), exception);
                }
            }
        };
        HttpApiSelectTaskWrapper.execute(configureFetchUrl(), this, progressBar, recyclerView, configureApplicationName(), new Pair[]{}, httpApiGetDbSelectTaskResponseHandler);
    }

    public abstract String configureFetchUrl();

    public abstract void processJsonObjectInFetchedJsonArray(JSONObject jsonObject);

    public void viewRecyclerView() {

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        prepareRecyclerView();
    }

    public abstract void prepareRecyclerView();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_search, menu);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) this.getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager != null ? searchManager.getSearchableInfo(this.getComponentName()) : null);

        EditText searchEdit = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEdit.setTextColor(Color.WHITE);
        searchEdit.setHintTextColor(Color.WHITE);
        searchEdit.setBackgroundColor(Color.TRANSPARENT);
        searchEdit.setHint("Search");

        InputFilter[] fArray = new InputFilter[2];
        fArray[0] = new InputFilter.LengthFilter(40);
        fArray[1] = (source, start, end, dest, destination_start, destination_end) -> {

            for (int i = start; i < end; i++) {

                if (!Character.isLetterOrDigit(source.charAt(i)))
                    return "";
            }

            return null;
        };

        searchEdit.setFilters(fArray);

        View v = searchView.findViewById(androidx.appcompat.R.id.search_plate);
        v.setBackgroundColor(Color.TRANSPARENT);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String searchKey) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String searchKey) {

                return configureSearchAction(searchKey);
            }
        });

        return true;
    }

    public abstract boolean configureSearchAction(String searchKey);
}
