package com.example.car_rental_client.adapters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.car_rental_client.ApplicationActivity;
import com.example.car_rental_client.R;
import com.example.car_rental_client.service.BookingAPI;
import com.example.car_rental_client.service.CarModel;
import com.example.car_rental_client.service.ResponseObject;
import com.example.car_rental_client.utility.NetUtilities;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.MyViewHolder>{
    private List<CarModel> carList;

    public static String sessionID;
    public static String username;
    public static int userID;

    public CarAdapter(List<CarModel> carList) {
        this.carList = carList;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public int carID;
        public TextView carMake;
        public TextView carModel;
        public TextView carPrice;
        public TextView carDeductible;
        public ImageView carThumb;
        public CardView cardView;
        public String carLocation;

        boolean performedAction;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            carMake = itemView.findViewById(R.id.car_make_text);
            carModel = itemView.findViewById(R.id.car_model_text);
            carPrice = itemView.findViewById(R.id.daily_price_text);
            carDeductible = itemView.findViewById(R.id.deductible_text);
            carThumb = itemView.findViewById(R.id.car_thumb);

            cardView = itemView.findViewById(R.id.card);

            Retrofit retrofit = NetUtilities.CreateRetrofitObject();
            BookingAPI bookingAPI = retrofit.create(BookingAPI.class);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // take them to the car with the current id as a request endpoint /cars/{id}
                    // idk create the pop up?
                    View popupView = LayoutInflater.from(v.getContext()).inflate(R.layout.popup_create_booking, null);
                    final PopupWindow popupWindow = new PopupWindow(popupView,
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            true);
                    popupWindow.setElevation(20);
                    popupWindow.setAnimationStyle(androidx.appcompat.R.style.Animation_AppCompat_Dialog);

                    // check if user actually did anything
                    performedAction = false;

                    // get xml views
                    TextView carMakePopup = popupView.findViewById(R.id.car_make_text_popup);
                    TextView carModelPopup = popupView.findViewById(R.id.car_model_text_popup);
                    TextView carDailyPricePopup = popupView.findViewById(R.id.daily_price_text_popup);
                    TextView carDeductiblePopup = popupView.findViewById(R.id.deductible_text_popup);

                    EditText startDate = popupView.findViewById(R.id.start_date_field);
                    EditText endDate = popupView.findViewById(R.id.end_date_field);

                    Button createBooking = popupView.findViewById(R.id.create_booking_button);
                    Button checkLocation = popupView.findViewById(R.id.check_location_button);

                    // populate them (from the parent, i guess? without having to resort to api)
                    carMakePopup.setText(carMake.getText());
                    carModelPopup.setText(carModel.getText());
                    carDailyPricePopup.setText(carPrice.getText());
                    carDeductiblePopup.setText(carDeductible.getText());

                    // show the pop up and listen for dismissal
                    popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
                    popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            if (!performedAction) {
                                Toast.makeText(v.getContext(), "Operation cancelled by user!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    // create booking button on click listener
                    createBooking.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // do the net stuff :) (it actually got the sessionID after some tacky maneuvering yay!)
                            // ye check the values first :p
                            if (startDate.getText().toString().isEmpty() && endDate.getText().toString().isEmpty()) {
                                Toast.makeText(v.getContext(), "No dates provided!", Toast.LENGTH_SHORT).show();
                            }
                            // format the date and create a date object from current timestamp for CHECKING BAHAHAHAHHAHAHAH
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy"); // the user inserts in this format
                            Date cDate = new Date();
                            Date convertedStartDate = new Date();
                            Date convertedEndDate = new Date();

                            // TODO: check dates :) what if the end date < start date? or do we travel backwards in time? heheh

                            // jsonify in this format
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat sqlDate = new SimpleDateFormat("yyyy-MM-dd");

                            try {
                                // T_T I'm trying to unit test but without unit testing ._. help
                                convertedStartDate = dateFormat.parse(startDate.getText().toString());
                                convertedEndDate = dateFormat.parse(endDate.getText().toString());
                            } catch (ParseException e) {
                                Toast.makeText(v.getContext(), "Invalid date format!", Toast.LENGTH_SHORT).show();
                            }

                            // if successful (idek at this point) we do the net stuff (forget it this won't work)
                            // let's check wth is a carid here (DONE)
                            performedAction = true;

                            assert convertedStartDate != null;
                            assert convertedEndDate != null;

                            // test stuff
                            Log.wtf("api vars", carID + " " + userID + " " + sqlDate.format(convertedStartDate) + " " + sqlDate.format(convertedEndDate));

                            Call<ResponseObject> call = bookingAPI.createBooking(sessionID, carID, userID, sqlDate.format(convertedStartDate), sqlDate.format(convertedEndDate)); // pass dates as strings (my head hurts oof)
                            call.enqueue(new Callback<ResponseObject>() {
                                @Override
                                public void onResponse(@NonNull Call<ResponseObject> call, @NonNull Response<ResponseObject> response) {
                                    if (response.code() == 200) {
                                        Toast.makeText(v.getContext(), response.body() != null ? response.body().getJSONMessage() : "Message body empty!", Toast.LENGTH_SHORT).show();
                                        popupWindow.dismiss();
                                    } else {
                                        Gson gson = new GsonBuilder().create();
                                        try {
                                            assert response.errorBody() != null;
                                            ResponseObject errorResponse = gson.fromJson(response.errorBody().string(), ResponseObject.class);
                                            Toast.makeText(v.getContext(), errorResponse.getJSONMessage(), Toast.LENGTH_SHORT).show();
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<ResponseObject> call, @NonNull Throwable t) {
                                    Toast.makeText(v.getContext(), "Error occurred!", Toast.LENGTH_SHORT).show();
                                    Log.w("Connection Error", t);
                                }
                            });

                        }
                    });

                    // check maps intent?
                    checkLocation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Uri gmIntentUri = Uri.parse("geo:" + carLocation.replace(" ", ""));
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            if (mapIntent.resolveActivity(popupView.getContext().getPackageManager()) != null) {
                                popupView.getContext().startActivity(mapIntent);
                            } else {
                                Toast.makeText(popupView.getContext(), "Unable to open maps!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.car_row, parent, false);
        return new MyViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CarAdapter.MyViewHolder holder, int position) {
        // get the car at position
        CarModel car = carList.get(position);

        holder.carID = car.getCarID();
        holder.carMake.setText(car.getCarMake());
        holder.carModel.setText(car.getCarModel());
        holder.carPrice.setText(Float.toString(car.getDailyPrice()));
        holder.carDeductible.setText(Float.toString(car.getDeductible()));
        holder.carLocation = car.getLocation();

        Picasso.get().load(NetUtilities.BASE_URL + car.getThumbURL()).into(holder.carThumb);
    }

    @Override
    public int getItemCount() {
        return carList.size();
    }

}
