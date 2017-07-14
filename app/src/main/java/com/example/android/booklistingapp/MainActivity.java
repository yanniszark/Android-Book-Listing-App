package com.example.android.booklistingapp;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Book>> {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String DEFAULT_QUERY = "android";

    private String query = DEFAULT_QUERY;

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

        /* Create a new book adapter */
        mAdapter = new BookAdapter(this, new ArrayList<Book>());

        /* Bind adapter to ListView */
        bookList.setAdapter(mAdapter);

        /* Set click listener to display popup window on click */
        bookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Book book = (Book) adapterView.getItemAtPosition(i);
                 /* Inflate a new popup window */
                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View bookDetailsView = inflater.inflate(R.layout.book_details_popup, null);


                /* Create new popup window */
                final PopupWindow mPopupWindow = new PopupWindow(
                        bookDetailsView,
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        true
                );


                /* Find the relevant views to update */
                TextView titleTextView = (TextView) bookDetailsView.findViewById(R.id.book_title);
                TextView authorTextView = (TextView) bookDetailsView.findViewById(R.id.book_author);
                ImageView bookImageView = (ImageView) bookDetailsView.findViewById(R.id.book_image);
                TextView publisherTextView = (TextView) bookDetailsView.findViewById(R.id.book_publisher);
                TextView publishDateTextView = (TextView) bookDetailsView.findViewById(R.id.book_publish_date);
                TextView descriptionTextView = (TextView) bookDetailsView.findViewById(R.id.book_description);
                Button popupDismissButton = (Button) bookDetailsView.findViewById(R.id.popup_dismiss_button);

                /* Set title */
                titleTextView.setText(book.getTitle());
                /* Display book author */
                authorTextView.setText(book.getAuthorsLine());
                 /*Display book publisher */
                publisherTextView.setText(book.getPublisher());
                 /* Display book publish date */
                publishDateTextView.setText(book.getPublishDate());
                /* Display book description */
                descriptionTextView.setText(book.getDescription());
                /* Display Book Image */
                Glide
                        .with(getApplicationContext())
                        .load(book.bookImageUrl)
                        .into(bookImageView);
                /* Set dismiss button listener */
                popupDismissButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPopupWindow.dismiss();
                    }
                });

                if (Build.VERSION.SDK_INT >= 21) {
                    mPopupWindow.setElevation(6.0f);
                }

                /* Display popup */
                mPopupWindow.showAtLocation( ((RelativeLayout) findViewById(R.id.activity_main)), Gravity.CENTER, 0, 0);

            }
        });

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
            loadingIndicatorView.setVisibility(View.GONE);

            // Update empty state with no connection error message
            emptyStateTextView.setText(R.string.device_offline);
        }
    }

    public void searchForBooks(){
        mAdapter.clear();
        loadingIndicatorView.setVisibility(View.VISIBLE);
        emptyStateTextView.setVisibility(View.GONE);
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            getLoaderManager().restartLoader(BOOK_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            loadingIndicatorView.setVisibility(View.GONE);

            // Update empty state with no connection error message
            emptyStateTextView.setText(R.string.device_offline);
            emptyStateTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
        Uri baseUri = Uri.parse(GOOGLE_BOOKS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        /* API key */
        uriBuilder.appendQueryParameter("key", getResources().getString(R.string.google_books_api_key));
        /* User search term */
        uriBuilder.appendQueryParameter("q", query);
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
        mAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    /**
     * Handle new intent when user searches for something new
     */
    @Override
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            searchForBooks();
        }
    }
}
