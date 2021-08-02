package com.codepath.tender.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.codepath.tender.LoginActivity;
import com.codepath.tender.R;
import com.codepath.tender.RestaurantViewModel;
import com.codepath.tender.UserViewModel;
import com.codepath.tender.models.Comment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static com.codepath.tender.Constants.CATEGORIES_KEY;
import static com.codepath.tender.Constants.CATEGORY_EIGHT;
import static com.codepath.tender.Constants.CATEGORY_EIGHT_NAME;
import static com.codepath.tender.Constants.CATEGORY_ELEVEN;
import static com.codepath.tender.Constants.CATEGORY_FIVE;
import static com.codepath.tender.Constants.CATEGORY_FOUR;
import static com.codepath.tender.Constants.CATEGORY_NINE;
import static com.codepath.tender.Constants.CATEGORY_NINE_NAME;
import static com.codepath.tender.Constants.CATEGORY_ONE;
import static com.codepath.tender.Constants.CATEGORY_SEVEN;
import static com.codepath.tender.Constants.CATEGORY_SIX;
import static com.codepath.tender.Constants.CATEGORY_TEN;
import static com.codepath.tender.Constants.CATEGORY_THIRTEEN;
import static com.codepath.tender.Constants.CATEGORY_THIRTEEN_NAME;
import static com.codepath.tender.Constants.CATEGORY_THREE;
import static com.codepath.tender.Constants.CATEGORY_TWELVE;
import static com.codepath.tender.Constants.CATEGORY_TWO;
import static com.codepath.tender.Constants.LATITUDE_KEY;
import static com.codepath.tender.Constants.LONGITUDE_KEY;
import static com.codepath.tender.Constants.PRICES_KEY;
import static com.codepath.tender.Constants.PROFILE_IMAGE_KEY;
import static com.codepath.tender.Constants.RADIUS_KEY;

/* user can logout of account and view profile */

public class ProfileFragment extends Fragment {

    private ImageButton ibLogout;
    private TextView tvUsername;
    private TextView tvLocation;
    private SeekBar barRadius;
    private TextView tvRadius;
    private ChipGroup priceChips;
    private ChipGroup categoryChips;
    private Switch categorySwitch;
    private ImageView profile_image;
    private ImageButton camera;
    private TextView name;

    private boolean[] prices;
    private boolean[] categories;

    private UserViewModel userViewModel;

    private File photoFile;
    public String photoFileName = "photo.jpg";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;

    //empty constructor
    public ProfileFragment() {}

    //inflate layout
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        prices = new boolean[] {false, false, false, false};
        categories = new boolean[] {false, false, false, false, false, false, false, false, false, false, false, false, false};

        ibLogout = view.findViewById(R.id.ibLogout);
        tvUsername = view.findViewById(R.id.tvProfileUsername);
        tvLocation = view.findViewById(R.id.tvLocation);
        barRadius = view.findViewById(R.id.barRadius);
        tvRadius = view.findViewById(R.id.tvRadius);
        priceChips = view.findViewById(R.id.priceChips);
        categoryChips = view.findViewById(R.id.categoryChips);
        categorySwitch = view.findViewById(R.id.categoriesSwitch);
        profile_image = view.findViewById(R.id.ivProfileImage);
        camera = view.findViewById(R.id.ibCamera);
        name = view.findViewById(R.id.tvNameProfile);


        tvUsername.setText(ParseUser.getCurrentUser().getUsername());
        tvRadius.setText(Integer.toString(ParseUser.getCurrentUser().getInt(RADIUS_KEY)) + " mi");
        barRadius.setProgress(ParseUser.getCurrentUser().getInt(RADIUS_KEY));
        tvLocation.setText(getUserAddress());
        name.setText(ParseUser.getCurrentUser().getString("name"));
        ParseFile file = ParseUser.getCurrentUser().getParseFile(PROFILE_IMAGE_KEY);
        Glide.with(this).load(file.getUrl()).circleCrop().into(profile_image);

        userViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);

        setLogout();
        setCameraBtn();

        setPriceChipsData();
        setCategoryChipsData();

        setStatusBarListener();
        setPriceChipsListener();
        setCategoryChipsListener();
        setCategorySwitchListener();
    }

    private String getUserAddress() {
        String result = "";
        double latitude = ParseUser.getCurrentUser().getDouble(LATITUDE_KEY);
        double longitude = ParseUser.getCurrentUser().getDouble(LONGITUDE_KEY);
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            result += addresses.get(0).getFeatureName() + " " + addresses.get(0).getThoroughfare();
            result += "\n";
            result += addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea() + " " + addresses.get(0).getPostalCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    //sets up the log out button to allow user to log out
    public void setLogout() {
        ibLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //log out user account
                ParseUser.logOut();

                //create intent back to login page
                Intent i = new Intent(getContext(), LoginActivity.class);
                startActivity(i);
            }
        });
    }

    //sets the price chips to the correct setting based on user preference
    public void setPriceChipsData() {
        List<String> user_prices = Arrays.asList(ParseUser.getCurrentUser().getString(PRICES_KEY).split(", "));
        //initialize price chips
        for(int i = 0; i<user_prices.size(); i++){
            int price = Integer.parseInt(user_prices.get(i));
            Chip chip = (Chip) priceChips.getChildAt(price-1);
            prices[price-1] = true;
            chip.setChecked(true);
        }
    }

    //sets the category chips to the correct setting based on user preference
    public void setCategoryChipsData() {
        //check for no categories selected
        String parse_string = ParseUser.getCurrentUser().getString(CATEGORIES_KEY);
        if(parse_string == null || parse_string.equals("")) return;

        //parse categories string and iterate
        List<String> user_categories = Arrays.asList(ParseUser.getCurrentUser().getString(CATEGORIES_KEY).split(","));
        //initialize price chips
        for(int i = 0; i<user_categories.size(); i++){
            //check the main switch since at least one category is checked
            categorySwitch.setChecked(true);
            int category = getCategoryNum(user_categories.get(i));
            Chip chip = (Chip) categoryChips.getChildAt(category);
            categories[category] = true;
            //check correct chip
            chip.setChecked(true);
        }
    }

    //sets up the status bar listener
    public void setStatusBarListener() {
        barRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvRadius.setText(progress + " mi");
                userViewModel.setRadius(progress);
                ParseUser.getCurrentUser().put(RADIUS_KEY, progress);
                ParseUser.getCurrentUser().saveInBackground();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    //sets up the chip listener
    public void setPriceChipsListener() {
        for (int i = 0; i<priceChips.getChildCount(); i++) {
            Chip chip = (Chip)priceChips.getChildAt(i);


            // Set the chip checked change listener
            chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        if(buttonView.getText().equals("$")) prices[0] = true;
                        if(buttonView.getText().equals("$$")) prices[1] = true;
                        if(buttonView.getText().equals("$$$")) prices[2] = true;
                        if(buttonView.getText().equals("$$$$")) prices[3] = true;
                    } else {
                        if(buttonView.getText().equals("$")) prices[0] = false;
                        if(buttonView.getText().equals("$$")) prices[1] = false;
                        if(buttonView.getText().equals("$$$")) prices[2] = false;
                        if(buttonView.getText().equals("$$$$")) prices[3] = false;
                    }
                    userViewModel.setPrices(getPriceString());
                    ParseUser.getCurrentUser().put(PRICES_KEY, getPriceString());
                    ParseUser.getCurrentUser().saveInBackground();
                }
            });
        }
    }

    //converts boolean array to string of prices the user has selected
    //ex: [true, false, true, true] -> "1, 3, 4"
    public String getPriceString() {
        String result = "";
        for(int i = 0; i<4; i++){
            if(prices[i] == true) {
                result = result + (i+1) + ", ";
            }
        }
        return result.substring(0, result.length()-2);
    }

    //sets up the chip listener
    public void setCategoryChipsListener() {
        for (int i = 0; i<categoryChips.getChildCount(); i++) {
            Chip chip = (Chip)categoryChips.getChildAt(i);

            // Set the chip checked change listener
            chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        categorySwitch.setChecked(true);
                        if(buttonView.getText().equals(CATEGORY_ONE)) categories[0] = true;
                        if(buttonView.getText().equals(CATEGORY_TWO)) categories[1] = true;
                        if(buttonView.getText().equals(CATEGORY_THREE)) categories[2] = true;
                        if(buttonView.getText().equals(CATEGORY_FOUR)) categories[3] = true;
                        if(buttonView.getText().equals(CATEGORY_FIVE)) categories[4] = true;
                        if(buttonView.getText().equals(CATEGORY_SIX)) categories[5] = true;
                        if(buttonView.getText().equals(CATEGORY_SEVEN)) categories[6] = true;
                        if(buttonView.getText().equals(CATEGORY_EIGHT_NAME)) categories[7] = true;
                        if(buttonView.getText().equals(CATEGORY_NINE_NAME)) categories[8] = true;
                        if(buttonView.getText().equals(CATEGORY_TEN)) categories[9] = true;
                        if(buttonView.getText().equals(CATEGORY_ELEVEN)) categories[10] = true;
                        if(buttonView.getText().equals(CATEGORY_TWELVE)) categories[11] = true;
                        if(buttonView.getText().equals(CATEGORY_THIRTEEN_NAME)) categories[12] = true;
                    } else {
                        if(buttonView.getText().equals(CATEGORY_ONE)) categories[0] = false;
                        if(buttonView.getText().equals(CATEGORY_TWO)) categories[1] = false;
                        if(buttonView.getText().equals(CATEGORY_THREE)) categories[2] = false;
                        if(buttonView.getText().equals(CATEGORY_FOUR)) categories[3] = false;
                        if(buttonView.getText().equals(CATEGORY_FIVE)) categories[4] = false;
                        if(buttonView.getText().equals(CATEGORY_SIX)) categories[5] = false;
                        if(buttonView.getText().equals(CATEGORY_SEVEN)) categories[6] = false;
                        if(buttonView.getText().equals(CATEGORY_EIGHT_NAME)) categories[7] = false;
                        if(buttonView.getText().equals(CATEGORY_NINE_NAME)) categories[8] = false;
                        if(buttonView.getText().equals(CATEGORY_TEN)) categories[9] = false;
                        if(buttonView.getText().equals(CATEGORY_ELEVEN)) categories[10] = false;
                        if(buttonView.getText().equals(CATEGORY_TWELVE)) categories[11] = false;
                        if(buttonView.getText().equals(CATEGORY_THIRTEEN_NAME)) categories[12] = false;
                    }
                    String categoryString = getCategoryString();
                    userViewModel.setCategories(categoryString);
                    ParseUser.getCurrentUser().put(CATEGORIES_KEY, categoryString);
                    ParseUser.getCurrentUser().saveInBackground();
                }
            });
        }
    }

    //converts boolean array to string of categories the user has selected
    //ex: [true, false, true, true, false] -> "pizza, chinese, burgers"
    public String getCategoryString() {
        String result = "";
        for(int i = 0; i<13; i++){
            if(categories[i] == true) {
                result = result + getCategoryName(i) + ",";
            }
        }
        if(result.equals("")) return "";
        return result.substring(0, result.length()-1);
    }

    private int getCategoryNum(String s) {
        if(s.equals(CATEGORY_ONE)) return 0;
        if(s.equals(CATEGORY_TWO)) return 1;
        if(s.equals(CATEGORY_THREE)) return 2;
        if(s.equals(CATEGORY_FOUR)) return 3;
        if(s.equals(CATEGORY_FIVE)) return 4;
        if(s.equals(CATEGORY_SIX)) return 5;
        if(s.equals(CATEGORY_SEVEN)) return 6;
        if(s.equals(CATEGORY_EIGHT)) return 7;
        if(s.equals(CATEGORY_NINE)) return 8;
        if(s.equals(CATEGORY_TEN)) return 9;
        if(s.equals(CATEGORY_ELEVEN)) return 10;
        if(s.equals(CATEGORY_TWELVE)) return 11;
        if(s.equals(CATEGORY_THIRTEEN)) return 12;
        return 0;
    }

    public String getCategoryName(int position){
        if(position == 0) return CATEGORY_ONE;
        if(position == 1) return CATEGORY_TWO;
        if(position == 2) return CATEGORY_THREE;
        if(position == 3) return CATEGORY_FOUR;
        if(position == 4) return CATEGORY_FIVE;
        if(position == 5) return CATEGORY_SIX;
        if(position == 6) return CATEGORY_SEVEN;
        if(position == 7) return CATEGORY_EIGHT;
        if(position == 8) return CATEGORY_NINE;
        if(position == 9) return CATEGORY_TEN;
        if(position == 10) return CATEGORY_ELEVEN;
        if(position == 11) return CATEGORY_TWELVE;
        if(position == 12) return CATEGORY_THIRTEEN;
        return "";
    }

    private void setCategorySwitchListener() {
        categorySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked) {
                    for (int i = 0; i<categoryChips.getChildCount(); i++) {
                        Chip chip = (Chip) categoryChips.getChildAt(i);
                        chip.setChecked(false);

                    }
                    userViewModel.setCategories("");
                    ParseUser.getCurrentUser().put(CATEGORIES_KEY, "");
                    ParseUser.getCurrentUser().saveInBackground();
                }
            }
        });
    }

    public void setCameraBtn() {
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });
    }

    //creates implicit intent to camera
    private void launchCamera() {
        //creating an implicit intent for action_image_capture
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        //defining where we want the output image to be stored (in photoFile)
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        //checking for an application that can handle the intent
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    //retrieves the image from the camera api
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //check that code is correct for camera use
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            //check that a picture was taken
            if (resultCode == RESULT_OK) {
                // set bitmap with image
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                //set image view with image from camera
                Glide.with(getContext()).load(takenImage).circleCrop().into(profile_image);
                ParseUser.getCurrentUser().put(PROFILE_IMAGE_KEY, new ParseFile(photoFile));
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //helper method to return the file based on file name
    public File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "MainActivity");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d("MainActivity", "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }
}