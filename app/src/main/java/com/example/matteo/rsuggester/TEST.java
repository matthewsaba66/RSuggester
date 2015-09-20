package com.example.matteo.rsuggester;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by orangepc on 17/09/2015.
 */
public class TEST {
    public static void main(String[] args) throws IOException {
        String flickrRoutesPath = "app/src/main/res/RoutesApp";
        List<FlickrRoute> flickrRoutes = createFlickrRoutes(flickrRoutesPath);
        int i = 0;
        for (FlickrRoute route : flickrRoutes) {
            System.out.println("route " + i);
            System.out.println();
            i++;
            for (float[] arr : route.getIntermediates()){
                System.out.println(arr[0] + "\t" + arr[1]);
            }
        }

    }


    private static List<FlickrRoute> createFlickrRoutes(String flickrRoutesPath) throws IOException {
        List<FlickrRoute> flickrRoutes = new ArrayList<FlickrRoute>();
        //per ogni cartella in path
        File dir = new File(flickrRoutesPath);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                //per ogni file

                File dir1 = new File(child.getPath());
                File[] directoryListing1 = dir1.listFiles();
                if (directoryListing1 != null) {
                    for (File child1 : directoryListing1) {
                        //crea unna lista di array float
                        FlickrRoute route = new FlickrRoute();
                        List<float[]> intermediates = new ArrayList<float[]>();
                        //per ogni riga
                        BufferedReader br = new BufferedReader(new FileReader(child1.getPath()));
                        String line;
                        while ((line = br.readLine()) != null) {
                            //aggiungi a rlista un array di float con le coordinate lette
                            String[] tokenizer = line.split("\t");
                            float[] coordinates = {0, 0};
                            coordinates[0] = Float.parseFloat(tokenizer[0]);
                            coordinates[1] = Float.parseFloat(tokenizer[1]);
                            intermediates.add(coordinates);
                        }
                        route.setIntermediates(intermediates);


                        flickrRoutes.add(route);
                    }
                }
            }

        }
        System.out.println("size: " + flickrRoutes.size());
        return flickrRoutes;
    }
}
