package com.example.StressOverflow;

import static android.content.ContentValues.TAG;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static com.google.android.material.internal.ContextUtils.getActivity;
import static com.google.common.base.CharMatcher.any;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.JMock1Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.UUID;

public class TagItemTest {
    private FirebaseFirestore firestore;
    private String testTagName;
    Item item;
    Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

    @Before
    public void setUp() {
        firestore = FirebaseFirestore.getInstance();
        AppGlobals.getInstance().setOwnerName("testUser");

        ArrayList<Tag> testTags = new ArrayList<>();
        ArrayList<Image> images = new ArrayList<>();

        //Add a tag first
        testTags.add(new Tag("testTag1"));
        item = new Item("deleteItemTest","make","model","description", new GregorianCalendar(),77.00, "Comments",testTags,images,
                123456, AppGlobals.getInstance().getOwnerName());
        firestore.collection("items").document(item.getId().toString())
                .set(item.toFirebaseObject())
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error with item insertion into collection items: ", e);
                        throw new RuntimeException("Error with item insertion into collection items: ", e);
                    }
                });

    }

    @After
    public void cleanUp(){
        UUID uuid = item.getId();
        this.firestore.collection("items")
                .document(uuid.toString())
                .delete()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error with item insertion into collection items: ", e);
                        throw new RuntimeException("Error with item insertion into collection items: ", e);
                    }
                });

    }
    @Rule
    public ActivityScenarioRule<ListActivity> listActivityRule =
            new ActivityScenarioRule<>(ListActivity.class);

    @Test
    public void ListActivitytoTagList(){
        onView(withId(R.id.showTagList_button)).perform(click());
        onView(ViewMatchers.withId(R.id.addTag_button)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void AddTagToTagList(){
        int listViewId = R.id.activity__item__list__item__list;
        SystemClock.sleep(2000);

        onData(Matchers.anything())
                .inAdapterView(withId(listViewId))
                .atPosition(0)
                .onChildView(withId(R.id.listview__item__title))
                .perform(longClick());
        onView(ViewMatchers.withId(R.id.activity__item__list__add__tag__button)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        onView(ViewMatchers.withId(R.id.activity__item__list__add__tag__button)).perform(click());
        onView(ViewMatchers.withId(R.id.makeNewTag_button)).perform(click());
        onView(ViewMatchers.withId(R.id.tagListView)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void addTags(){
        int listViewId = R.id.activity__item__list__item__list;
        SystemClock.sleep(2000);

        onData(Matchers.anything())
                .inAdapterView(withId(listViewId))
                .atPosition(0)
                .onChildView(withId(R.id.listview__item__title))
                .perform(longClick());
        onView(ViewMatchers.withId(R.id.activity__item__list__add__tag__button)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        onView(ViewMatchers.withId(R.id.activity__item__list__add__tag__button)).perform(click());
        int chipGroupID = R.id.tagFragment_chipGroup;

        onView(allOf(withText("tag 1"), isDescendantOfA(withId(chipGroupID))))
                .perform(click());

        onView(withText("OK")).inRoot(isDialog()).perform(click());


        final Item[] itemInfo = new Item[1];
        this.firestore.collection("items")
                .document(item.getId().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            Map<String, Object> data = document.getData();
                            Item item = Item.fromFirebaseObject(data);
                            itemInfo[0] = item;
                        }
                    }
                });
        SystemClock.sleep(2000);
        boolean tagExists = false;
        for (Tag t: itemInfo[0].getTags()){
            if (t.getTagName().equals("tag 1")){
                tagExists = true;
                break;
            }
        }

        assertTrue(tagExists);
    }
    @Test
    public void deleteItem(){
        int listViewId = R.id.activity__item__list__item__list;
        SystemClock.sleep(2000);

        onData(Matchers.anything())
                .inAdapterView(withId(listViewId))
                .atPosition(0)
                .onChildView(withId(R.id.listview__item__title))
                .perform(longClick());

        onView(ViewMatchers.withId(R.id.activity__item__list__remove__item__button)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        onView(ViewMatchers.withId(R.id.activity__item__list__remove__item__button)).perform(click());


        final boolean[] itemBool = new boolean[1];
        this.firestore.collection("items")
                .document(item.getId().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                itemBool[0] = true;
                            } else {
                                itemBool[0] = false;
                                Log.d(TAG, "Document not found with uuid " + item.getId().toString());
                            }
                        }
                    }
                });

        assertFalse(itemBool[0]);
    }




}