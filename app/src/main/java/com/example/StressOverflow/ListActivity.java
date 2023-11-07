package com.example.StressOverflow;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.StressOverflow.AddItemFragment;
import com.example.StressOverflow.Item;
import com.example.StressOverflow.ItemListAdapter;
import com.example.StressOverflow.Util;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.UUID;

public class ListActivity extends AppCompatActivity implements AddItemFragment.OnFragmentInteractionListener,
TagFragment.OnFragmentInteractionListener{
    ListView itemList;
    ItemListAdapter itemListAdapter;
    Button editButton;
    Button filterButton;
    FloatingActionButton addItemButton;
    FloatingActionButton deleteItemButton;
    FloatingActionButton addTagButton;
    TextView sumOfItemCosts;

    int selected = -1;
    Intent loginIntent;
    private boolean inSelectionMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        this.loginIntent = getIntent();

        this.itemList = findViewById(R.id.activity__item__list__item__list);
        this.editButton = findViewById(R.id.activity__item__list__edit__item__button);
        this.filterButton = findViewById(R.id.activity__item__list__filter__item__button);
        this.addItemButton = findViewById(R.id.activity__item__list__add__item__button);
        this.deleteItemButton = findViewById(R.id.activity__item__list__remove__item__button);
        this.addTagButton = findViewById(R.id.activity__item__list__add__tag__button);
        this.sumOfItemCosts = findViewById(R.id.activity__item__list__cost__sum__text);

        Button showTagListButton = findViewById(R.id.showTagList_button);

        showTagListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListActivity.this, TagList.class);
                startActivity(intent);
            }
        });
        this.addTagButton.setOnClickListener(openTagFragment);
        itemList.setOnItemLongClickListener(selectItems);
        //Fragment newItemFragment = new AddItemFragment();
        //newItemFragment.setArguments(new Bundle());
        //getSupportFragmentManager()
        //        .beginTransaction()
        //        .add(R.id.activity__item__list__container, newItemFragment, "test")
        //        .commit();
        /**
         this.addItemButton.setOnClickListener((v) -> {
         Fragment newItemFragment = new AddItemFragment();
         newItemFragment.setArguments(new Bundle());
         getSupportFragmentManager()
         .beginTransaction()
         .add(R.id.activity__item__list__add__fragment__layout, newItemFragment, "test")
         .commit();
         });
         */
        this.itemList.setOnItemClickListener((parent, view, position, id) -> {
            this.selected = position;
        });

        this.itemListAdapter = new ItemListAdapter(this, new ArrayList<Item>());
        this.itemList.setAdapter(this.itemListAdapter);
        ArrayList<Tag> tags = new ArrayList<Tag>();
        tags.add(new Tag("tag1"));
        tags.add(new Tag("tag2"));
        GregorianCalendar cal1 = new GregorianCalendar(2023, 11, 5);
        this.itemListAdapter.addItem(
                new Item("Test 1",
                        "Make",
                        "Model",
                        "I need to stop procrastinating... common word",
                        new GregorianCalendar(2023, 11, 5),
                        100.0d,
                        "asdf",
                        tags,
                        new ArrayList<UUID>(),
                        2000
                )
        );

        tags.add(new Tag("tag3"));

        this.itemListAdapter.addItem(
                new Item("Test 2",
                        "Make",
                        "Model",
                        "keywords are very hard to think of; common words",
                        new GregorianCalendar(2020, 1, 15),
                        100.0d,
                        "asdf",
                        tags,
                        new ArrayList<UUID>(),
                        2000
                )
        );
        this.sumOfItemCosts.setText(loginIntent.getStringExtra("login"));

        Dialog filterDialog = new Dialog(ListActivity.this);
        this.addItemButton.setOnClickListener(v -> new FilterDialog(filterDialog, this.itemListAdapter, this.itemList));

    }

    @Override
    /**
     * Receives the Item produced by the item addition dialog fragment, and adds the item
     * to the item list adapter.
     */
    public void onSubmitAdd(Item item) {
        this.itemListAdapter.add(item);
        this.setSumOfItemCosts();
    }

    /**
     * Lol
     *
     * @param position pos of item to delete
     */
    public void onSubmitDelete(int position) {
        try {
            this.itemListAdapter.deleteItem(position);
        } catch (ArrayIndexOutOfBoundsException e) {
            Util.showShortToast(this.getApplicationContext(), "Choose an item first!");
        }
        this.setSumOfItemCosts();
    }

    @SuppressLint("SetTextI18n") // ?? man
    /**
     *
     */
    public void setSumOfItemCosts() {
        this.sumOfItemCosts.setText(this.itemListAdapter.getTotalValue().toString());
    }

    private AdapterView.OnItemLongClickListener selectItems = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            addTagButton.setVisibility(View.VISIBLE);
            inSelectionMode = true;
            itemListAdapter.setSelectionMode(true);
            itemListAdapter.toggleSelection(position);
            return true;
        }

    };

    private void exitSelectionMode() {
        inSelectionMode = false;
        itemListAdapter.setSelectionMode(false);
    }

    private View.OnClickListener openTagFragment = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Switch to Tag Fragment
        }
    };


    @Override
    public void addTagPressed(ArrayList<Tag> tagsToAdd) {
        //Add all the tags to the selected items

    }
}
