package com.yamblz.hardoperations.ui;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;
import com.yamblz.hardoperations.R;
import com.yamblz.hardoperations.model.Artist;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArtistsListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Artist[]>
{

    private static final int ARTISTS_LOADER_ID = 101;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.artists_recycler_view)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artists_list);

        ButterKnife.bind(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(this, R.drawable.divider));

        showProgress();
        getSupportLoaderManager().initLoader(
                ARTISTS_LOADER_ID,
                null,
                this).forceLoad();
    }

    @Override
    public Loader<Artist[]> onCreateLoader(int id, Bundle args)
    {
        return new ArtistsLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<Artist[]> loader, Artist[] data)
    {
        showContent(data);
    }

    @Override
    public void onLoaderReset(Loader<Artist[]> loader)
    {

    }

    private void showProgress()
    {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void showContent(Artist[] data)
    {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        ArtistsAdapter adapter = new ArtistsAdapter(data);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
