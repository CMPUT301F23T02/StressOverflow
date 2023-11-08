/**
 * Represents a single item
 * Should pictures be their own class, or is simply a link to a picture enough?
 * TODO: Throw more illegal argument exceptions or something
 * reminder: tags are not hashable, dont make tags or pictures a HashSet (which also might muck
 * around with mutex's if we require in future)
 */
package com.example.StressOverflow;

import androidx.annotation.NonNull;

import com.example.StressOverflow.Util;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

import kotlin.NotImplementedError;

public class Item {

    private String name;
    private String make;
    private String model;
    private String description;
    private GregorianCalendar date;
    private Double value;
    private String comments;
    private ArrayList<Tag> tags = new ArrayList<Tag>();
    private ArrayList<UUID> pictures = new ArrayList<UUID>();
    private Integer serial;

    public Item(
            String name,
            String make,
            String model,
            String description,
            GregorianCalendar date,
            Double value,
            String comments,
            ArrayList<Tag> tags,
            ArrayList<UUID> pictures,
            Integer serial
    ) {
        this.setName(name);
        this.setMake(make);
        this.setModel(model);
        this.setDescription(description);
        this.setDate(date);
        this.setValue(value);
        this.setComments(comments);
        this.addTags(tags);
        this.addPictures(pictures);
        this.setSerial(serial);
    }

    public void setName(String name) throws IllegalArgumentException {
        if (name.equals("")) {
            throw new IllegalArgumentException("empty name not allowed");
        } else if (name.length() > Util.MAX_ITEM_NAME_LENGTH) {
            throw new IllegalArgumentException(String.format("name exceeds maximum name length (%d)", Util.MAX_ITEM_NAME_LENGTH));
        }
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public String getMake() {
        return this.make;
    }

    public String getModel() {
        return this.model;
    }

    public String getDescription() {
        return this.description;
    }

    /**
     * Most likely to be used on the list view (truncate the description if its too long)
     * TODO: BREAKS IF IT ENCOUNTERS A WORD WITH LENGTH GREATER THAN Util.MAX_LINE_LENGTH
     * @param brief whether or not to return first 2 lines of description
     * @return
     */
    public String getDescription(boolean brief) {
        if (brief) {
            StringBuilder out = new StringBuilder();
            String[] words = this.getDescription().split(" ");
            int line_length = 0;
            boolean second_line = false;
            for (String word : words) {
                out.append(word);
                out.append(" ");
                line_length += word.length() + 1;
                if (line_length > Util.MAX_LINE_LENGTH - (second_line ? 1 : 0) * 3) {
                    if (second_line) {
                        out.append("...");
                        break;
                    }
                    line_length = 0;
                    second_line = true;
                    out.append("\n");
                }
            }
            return out.toString();
        }
        return this.getDescription();
    }

    public GregorianCalendar getDate() {
        return this.date;
    }

    public ArrayList<Tag> getTags() {
        return this.tags;
    }

    public ArrayList<UUID> getPictures() {
        return this.pictures;
    }

    public Integer getSerial() {
        return this.serial;
    }

    /**
     * TODO: This should be able to return any number of possible serial number formats.
     * Examples: this.getSerial = 12345678905, this.getSerialAsString("0-ddddd-ddddd-d")
     *           returns 0-12345-67890-5. will implement once i find a need for it
     *           (which may never come)
     *
     * @return serial number formatted as string
     */
    public String getSerialAsString(String format) {
        throw new NotImplementedError();
    }

    public Double getValue() {
        if (this.value == null) {
            return 0.0d;
        }
        return this.value;
    }

    public String getComments() {
        return comments;
    }

    /**
     * Returns "make / model", "make", "model", or an empty string depending on which
     * of the properties are not empty strings
     *
     * @return make and model together
     */
    public String getMakeModel() {
        StringBuilder out = new StringBuilder();
        if (this.getMake().equals("") && this.getModel().equals("")) {
            return "";
        } else if (this.getMake().equals("") || this.getModel().equals("")) {
            out.append(this.getMake());
            out.append(this.getModel());
        } else {
            out.append(this.getMake());
            out.append(" / ");
            out.append(this.getModel());
        }
        return out.toString();
    }

    public void setMake(String make) {
        this.make = make;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(GregorianCalendar date) {
        this.date = date;
    }

    public void setValue(Double value) throws IllegalArgumentException {
        if (value < 0.0d) {
            throw new IllegalArgumentException(String.format("negative value not allowed for item %s", this.getName()));
        }
        this.value = value;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    /**
     * Adds all tags in tags to this object's tags. If a tag is already owned by this object, then
     * don't add the tag.
     *
     * @param tags
     */
    public void addTags(@NonNull ArrayList<Tag> tags) {
        for (int i = 0; i < tags.size(); i++) {
            Tag tag = tags.get(i);
            if (!this.tags.contains(tag)) {
                this.tags.add(tag);
            }
        }
    }

    /**
     * Removes all tags which exist in both this item's tags and passed in tag list.
     *
     * @param tags The tags to remove from this item
     */
    public void removeTags(@NonNull ArrayList<Tag> tags) {
        this.tags.removeAll(tags);
    }

    /**
     * TODO: Should we allow duplicate images here??
     *
     * @param pictures
     */
    public void addPictures(@NonNull ArrayList<UUID> pictures) {
        this.pictures.addAll(pictures);
    }

    public void setSerial(int serial) {
        this.serial = serial;
    }
}