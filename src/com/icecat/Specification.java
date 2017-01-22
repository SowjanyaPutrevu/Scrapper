package com.icecat;

import java.util.List;

/**
 * Created by Sowji on 21/01/2017.
 * Abstract layer for all kinds of features which can be in specs
 */
public class Specification {

    private String name;

    private List<String> attributes;

    private List<String> values;

    private String desc;

    private String type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<String> getAttribute() {
        return attributes;
    }

    public void setAttribute(List<String> attribute) {
        this.attributes = attribute;
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return "Specification{" +
                "name='" + name + '\'' +
                ", attributes=" + attributes +
                ", values=" + values +
                ", desc='" + desc + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
