package ndk.utils_android9.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.appcompat.widget.Toolbar;
import androidx.core.util.Pair;
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
}
