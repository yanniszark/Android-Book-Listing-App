package com.example.android.booklistingapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Book>> {

    private static final String TAG = MainActivity.class.getSimpleName();

    /**
     * URL for earthquake data from the USGS dataset
     */
    private static final String GOOGLE_BOOKS_REQUEST_URL =
            "https://www.googleapis.com/books/v1/volumes";

    /**
     * Constant value for the earthquake loader ID. We can choose any integer.
     */
    private static final int BOOK_LOADER_ID = 1;

    /**
     * Adapter for the list of earthquakes
     */
    private BookAdapter mAdapter;

    /* Empty screen TextView */
    @BindView(R.id.empty_view)
    TextView emptyStateTextView;

    /* Loader to display when downloading data*/
    @BindView(R.id.loading_indicator)
    View loadingIndicatorView;

    @BindView(R.id.list)
    ListView bookList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        /* Get the book data */


        /* Create a new book adapter */
        mAdapter = new BookAdapter(this, new ArrayList<Book>());
        /* Bind adapter to ListView */
        bookList.setAdapter(mAdapter);


        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(BOOK_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            emptyStateTextView.setText(R.string.device_offline);
        }
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
        Uri baseUri = Uri.parse(GOOGLE_BOOKS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        /* API key */
        uriBuilder.appendQueryParameter("key", getResources().getString(R.string.google_books_api_key));
        /* User search term */
        uriBuilder.appendQueryParameter("q", "quilting");
        /* Number of results */
        uriBuilder.appendQueryParameter("maxResults", "10");

        Log.v(TAG, "Quering URL: " + uriBuilder.toString());

        return new BookLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {
        // Hide loading indicator because the data has been loaded
        loadingIndicatorView.setVisibility(View.GONE);

        // Set empty state text to display "No books found."
        emptyStateTextView.setText(R.string.no_books_found);

        // Clear the adapter of previous book data
        mAdapter.clear();

        // If there is a valid list of {@link Book}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (books != null && !books.isEmpty()) {
            emptyStateTextView.setVisibility(View.GONE);
            mAdapter.addAll(books);
        }
        else
            emptyStateTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {

    }
}
