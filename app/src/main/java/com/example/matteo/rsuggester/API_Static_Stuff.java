package com.example.matteo.rsuggester;

/**
 * This class is used primarily for API keys and secrets to create a central repository.
 * This is useful for having one place to manage several API keys.
 *
 * @author ograycoding.wordpress.com
 */
public class API_Static_Stuff {

    private final String YELP_CONSUMER_KEY ="3jipH-2ZXj5hwXcED6RbbA";
    private final String YELP_CONSUMER_SECRET = "Os7OVeg26ktHZH9oVi-h4LYo6i0";
    private final String YELP_TOKEN = "PII5-eH5LvqdY155lh20tdIcDM3wW04p";
    private final String YELP_TOKEN_SECRET = "9JgST9AN_uLdg5fd22t-UtlEOu4";


    public String getYelpConsumerKey(){
        return YELP_CONSUMER_KEY;
    }

    public String getYelpConsumerSecret(){
        return YELP_CONSUMER_SECRET;
    }

    public String getYelpToken(){
        return YELP_TOKEN;
    }

    public String getYelpTokenSecret(){
        return YELP_TOKEN_SECRET;
    }

}