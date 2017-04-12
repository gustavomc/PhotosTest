package com.example.gustavo.photostest.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.gustavo.photostest.R;
import com.example.gustavo.photostest.adapters.PhotoListAdapter;
import com.example.gustavo.photostest.models.ListItem;
import com.example.gustavo.photostest.utils.Constants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by gustavomedina on 11/04/17.
 */

public class ListActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    @BindView(R.id.recycler)
    public RecyclerView mRecyclerView;

    private PhotoListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos_list);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(R.string.photos_list);
            ab.setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView.LayoutManager lm = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(lm);

        showList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showList() {
        List<ListItem> lista = ListItem.getAll();

        if(lista != null && lista.size() > 0){
            mAdapter = new PhotoListAdapter(lista);

            mAdapter.setOnItemClickListener(new PhotoListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(ListItem item) {
                    showItem(item);
                }
            });

            this.mRecyclerView.setAdapter(mAdapter);
        }
    }

    private void showItem(ListItem item) {
        Intent startIntent = new Intent(getApplicationContext(), DetailActivity.class);
        startIntent.putExtra(Constants.EXTRA_BIN_ID, item.getId());
        startIntent.putExtra(Constants.EXTRA_BIN_NAME, item.getName());
        startActivity(startIntent);
    }
}
