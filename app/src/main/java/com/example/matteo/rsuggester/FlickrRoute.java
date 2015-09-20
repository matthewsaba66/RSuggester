package com.example.matteo.rsuggester;

import java.util.List;

/**
 * Created by orangepc on 17/09/2015.
 */
public class FlickrRoute {

    private List<float[]> intermediates;

    public FlickrRoute() {
    }

    public FlickrRoute(List<float[]> intermediates) {
        this.intermediates = intermediates;
    }

    public List<float[]> getIntermediates() {
        return intermediates;
    }

    public void setIntermediates(List<float[]> intermediates) {
        this.intermediates = intermediates;
    }
}
