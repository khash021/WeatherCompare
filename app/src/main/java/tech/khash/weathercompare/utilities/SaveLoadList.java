package tech.khash.weathercompare.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import tech.khash.weathercompare.model.Constant;
import tech.khash.weathercompare.model.Loc;

/**
 * Created by Khashayar "Khash" Mortazavi
 *
 * This class has a few static methods that will save ArrayList<E> to shared preferences (after
 * converting them using gson, and then again retrieve them from preferences.
 *
 * This is used for storing Loc objects mainly
 */

public class SaveLoadList {

    private static final String TAG = SaveLoadList.class.getSimpleName();

    public SaveLoadList(){}

    /**
     *      Helper private method for saving the new ArrayList<Loc> by replacing the old
     *      one (if it exists)
     * @param inputArrayList : ArrayList<Loc> to be saved
     * @param context : context (we need this since it is a static method
     */
    private static void savedLocList (Context context, ArrayList<Loc> inputArrayList) {
        //get reference to shared pref
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        //create Gson object
        Gson gson = new Gson();

        //check for null input
        if (inputArrayList == null) {
            inputArrayList = new ArrayList<>();
        }

        //convert arraylist
        String json = gson.toJson(inputArrayList);

        //get the shared preference editor
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //add the new updated list. If it already exists, it just replaces the old one so we wont need to delete first
        editor.putString(Constant.PREF_KEY_ARRAY, json);
        editor.apply();
    }//savedLocList

    //TODO: consider returning a boolean for success
    //TODO: check for duplicates

    /**
     *      This method, adds another Loc object the current list. If there is no list,
     *      it creates one and add Loc
     * @param context : context
     * @param input : Loc object to be added to the list
     */
    public static void addToLocList (Context context, Loc input) {
        //load the previous data, and add the new list to it
        ArrayList<Loc> currentList = loadLocList(context);

        //if there is nothing in there, this will be null, so we instantiate it
        if (currentList == null) {
            currentList = new ArrayList<>();
        }//if

        //add the object to list
        currentList.add(input);

        //save the updated list
        savedLocList(context, currentList);
    }//addToLocList


    /**
     *      Method for loading the ArrayList<Loc> from shared preferences
     * @param context : context needed for accessing preferences
     * @return : ArrayList<Loc>, could be null if there is no such list
     */
    public static ArrayList<Loc> loadLocList(Context context) {
        //create Gson object
        Gson gson = new Gson();
        //get reference to the shared pref
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        /*
        get the string from the preference (this will be empty string if there is no data in there
        yet). as a result the output array list will be null, so we need to check for this in the
        save array list when we pull the old data
         */
        String response = sharedPreferences.getString(Constant.PREF_KEY_ARRAY, "");
                //convert the json string back to Loc Array list and return it
        ArrayList<Loc> outputArrayList = gson.fromJson(response,
                new TypeToken<List<Loc>>() {
                }.getType());

        return outputArrayList;
    }//loadLocList

    /**
     *      Method for retrieving a single Loc object using its name
     * @param name : name of the Loc object
     * @param context : context
     * @return : Loc object corresponding to the name, or null if no such item is found
     */
    public static Loc getLocFromDb(Context context, String name) {
        //first load the current db
        ArrayList<Loc> currentList = loadLocList(context);

        //check for empty/null list
        if (currentList == null || currentList.size() < 1) {
            return null;
        }

        //search for the object
        Loc loc = null;
        for (Loc l : currentList) {
            String locName = l.getName();
            if (locName.equalsIgnoreCase(name)) {
                loc = l;
            }//if
        }//for
        return loc;
    }//getLocFromDb

    /**
     *  This method gets an updated Loc object as an input and replaces it in the main arraylist
     *  It searches the database for the Loc using its ID, and then replaces it.
     *
     *  If no such Object is found, nothing is done
     *  //TODO: This could be modified. either return a boolean, or just simply add the new one
     *
     * @param context : context
     * @param newLoc : updated Loc
     */
    public static void replaceLocInDb(Context context, Loc newLoc) {
        //find the old Loc and get its index
        int index  = getLocIndex(context, newLoc);
        if (index == -1) {
            //no such Loc exists
            return;
        }//if
        //load the database
        ArrayList<Loc> locArrayList = loadLocList(context);

        //replace the old Loc object with the new one in the ArrayList
        locArrayList.set(index, newLoc);

        //save the new list
        savedLocList(context, locArrayList);
    }//replaceLocInDb

    /**
     * This method searches the database using a given Loc (using its name), and then returns the
     * item's index
     * @param context : context
     * @param loc : Loc object to be searched for
     * @return : index of the object. returns -1 if no such Loc exists
     */
    private static int getLocIndex (Context context, Loc loc) {
        String id = loc.getName();
        ArrayList<Loc> locArrayList = loadLocList(context);

        int index = -1;
        int i = 0;
        for (Loc l : locArrayList) {
            if (l.getName().equalsIgnoreCase(id)) {
                index = i;
                return index;
            }//if
            i++;
        }//for
        return index;
    }//getLocIndex

    public static void deleteDb(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(Constant.PREF_KEY_ARRAY).apply();
    }//removeAllFences


    public static void deleteLoc (Context context, Loc loc) {
        //get the index of loc
        int index = getLocIndex(context, loc);

        //get the list
        ArrayList<Loc> locArrayList = loadLocList(context);

        //delete the specified object
        locArrayList.remove(index);

        //save the new database
        savedLocList(context, locArrayList);
    }//deleteLoc



}//SaveLoadList
